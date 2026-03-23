import faiss
import numpy as np
import json
import os
import tempfile
from typing import List, Dict, Optional

# 使用临时目录避免中文路径问题
DATA_DIR = os.path.join(tempfile.gettempdir(), 'blog_rag_data')
INDEX_FILE = os.path.join(DATA_DIR, 'faiss_index.bin')
DOCS_FILE = os.path.join(DATA_DIR, 'documents.json')

dimension = 384
index = None
documents: List[Dict] = []
id_counter = 0

def _ensure_data_dir():
    if not os.path.exists(DATA_DIR):
        os.makedirs(DATA_DIR)

def init():
    global index, documents, id_counter
    _ensure_data_dir()
    
    if os.path.exists(INDEX_FILE) and os.path.exists(DOCS_FILE):
        index = faiss.read_index(INDEX_FILE)
        with open(DOCS_FILE, 'r', encoding='utf-8') as f:
            data = json.load(f)
            documents = data.get('documents', [])
            id_counter = data.get('id_counter', 0)
    else:
        index = faiss.IndexFlatL2(dimension)
        documents = []
        id_counter = 0

def save():
    _ensure_data_dir()
    print(f"Saving index to: {INDEX_FILE}")
    print(f"DATA_DIR exists: {os.path.exists(DATA_DIR)}")
    faiss.write_index(index, INDEX_FILE)
    with open(DOCS_FILE, 'w', encoding='utf-8') as f:
        json.dump({
            'documents': documents,
            'id_counter': id_counter
        }, f, ensure_ascii=False, indent=2)

def _rebuild_index():
    """重建FAISS索引"""
    global index
    from .embedding import embed_batch
    index = faiss.IndexFlatL2(dimension)
    if documents:
        contents = [doc['content'] for doc in documents]
        embeddings = embed_batch(contents)
        if len(embeddings) > 0:
            index.add(embeddings.astype(np.float32))

def add_document(doc_id: int, title: str, content: str, embedding: np.ndarray):
    global id_counter
    
    # 检查是否已存在相同ID的文档
    existing_idx = None
    for i, doc in enumerate(documents):
        if doc['id'] == doc_id:
            existing_idx = i
            break
    
    doc = {
        'id': doc_id,
        'title': title,
        'content': content
    }
    
    if existing_idx is not None:
        # 更新现有文档
        documents[existing_idx] = doc
        # 重建索引（FAISS不支持直接更新）
        _rebuild_index()
    else:
        # 添加新文档
        documents.append(doc)
        index.add(np.array([embedding], dtype=np.float32))
        id_counter += 1
    
    save()

def add_documents_batch(docs: List[Dict], embeddings: np.ndarray):
    global id_counter
    
    # 先去重，只保留每个ID的最新版本
    doc_map = {}
    for doc in docs:
        doc_map[doc['id']] = doc
    
    # 移除现有文档中将要更新的
    new_documents = []
    for doc in documents:
        if doc['id'] not in doc_map:
            new_documents.append(doc)
    
    # 添加新文档
    for doc_id, doc in doc_map.items():
        new_documents.append(doc)
    
    # 更新全局变量
    documents[:] = new_documents
    id_counter = len(documents)
    
    # 重建索引
    _rebuild_index()
    save()

def search(query_embedding: np.ndarray, k: int = 3) -> List[Dict]:
    if len(documents) == 0:
        return []
    
    k = min(k, len(documents))
    if k == 0:
        return []
    
    query_vec = np.array([query_embedding], dtype=np.float32)
    distances, indices = index.search(query_vec, k)
    
    results = []
    for i, idx in enumerate(indices[0]):
        if idx < len(documents):
            doc = documents[idx].copy()
            doc['score'] = float(distances[0][i])
            results.append(doc)
    
    return results

def get_document_count() -> int:
    return len(documents)

def clear_all():
    global index, documents, id_counter
    index = faiss.IndexFlatL2(dimension)
    documents = []
    id_counter = 0
    save()

def get_all_documents() -> List[Dict]:
    return documents.copy()
