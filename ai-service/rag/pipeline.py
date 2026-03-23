from typing import List, Dict, Optional
from .embedding import embed, embed_batch
from .vector_store import init, add_document, add_documents_batch, search, get_document_count, get_all_documents, clear_all
from .retriever import retrieve, format_context
from .generator import generate_rag_answer

def init_rag():
    init()

def clear_knowledge_base():
    """清空知识库"""
    clear_all()

def add_blog_to_knowledge(blog_id: int, title: str, content: str):
    embedding = embed(content)
    add_document(blog_id, title, content, embedding)

def add_blogs_batch(blogs: List[Dict]):
    docs = []
    for blog in blogs:
        doc = {
            'id': blog['id'],
            'title': blog['title'],
            'content': blog['content']
        }
        docs.append(doc)
    
    contents = [doc['content'] for doc in docs]
    embeddings = embed_batch(contents)
    
    if len(embeddings) > 0:
        add_documents_batch(docs, embeddings)

def rag_query(question: str, k: int = 3) -> Dict:
    docs = retrieve(question, k)
    
    if not docs:
        return {
            'answer': '抱歉，知识库中暂时没有相关的博客内容可以回答您的问题。',
            'sources': [],
            'has_context': False
        }
    
    context = format_context(docs)
    answer = generate_rag_answer(question, context)
    
    sources = []
    for doc in docs:
        sources.append({
            'id': doc['id'],
            'title': doc['title'],
            'score': doc.get('similarity', 0)  # 使用相似度分数而不是原始距离
        })
    
    return {
        'answer': answer,
        'sources': sources,
        'has_context': True
    }

def get_knowledge_stats() -> Dict:
    return {
        'document_count': get_document_count(),
        'documents': get_all_documents()
    }

def split_text(text: str, chunk_size: int = 500) -> List[str]:
    if len(text) <= chunk_size:
        return [text]
    
    chunks = []
    for i in range(0, len(text), chunk_size):
        chunks.append(text[i:i + chunk_size])
    return chunks
