from typing import List, Dict
from .embedding import embed
from .vector_store import search
import numpy as np

def cosine_similarity(vec1, vec2):
    """计算余弦相似度"""
    dot_product = np.dot(vec1, vec2)
    norm1 = np.linalg.norm(vec1)
    norm2 = np.linalg.norm(vec2)
    if norm1 == 0 or norm2 == 0:
        return 0.0
    return dot_product / (norm1 * norm2)

def retrieve(query: str, k: int = 3, threshold: float = 0.15) -> List[Dict]:
    """
    检索相关文档
    :param query: 查询问题
    :param k: 返回文档数量
    :param threshold: 相似度阈值(0-1)，低于此值的文档会被过滤掉
    """
    if not query or not query.strip():
        return []
    
    query_embedding = embed(query)
    results = search(query_embedding, k)
    
    # 使用余弦相似度重新计算相关性
    filtered_results = []
    for doc in results:
        # 从vector_store获取原始文档的embedding重新计算
        # 这里我们使用L2距离来估算余弦相似度
        # 对于单位向量，cosine_similarity = 1 - L2_distance^2 / 2
        l2_distance = doc.get('score', float('inf'))
        # 估算余弦相似度 (假设向量已归一化)
        cosine_sim = max(0.0, 1.0 - (l2_distance ** 2) / 2.0)
        
        if cosine_sim >= threshold:
            doc['similarity'] = round(cosine_sim, 2)
            filtered_results.append(doc)
    
    # 按相似度降序排序
    filtered_results.sort(key=lambda x: x['similarity'], reverse=True)
    
    return filtered_results

def retrieve_with_scores(query: str, k: int = 3) -> List[Dict]:
    results = retrieve(query, k)
    return results

def format_context(docs: List[Dict], max_length: int = 3000) -> str:
    if not docs:
        return ""
    
    context_parts = []
    current_length = 0
    
    for i, doc in enumerate(docs):
        title = doc.get('title', '无标题')
        content = doc.get('content', '')
        score = doc.get('score', 0)
        
        part = f"【文章{i+1}】{title}\n{content}\n"
        
        if current_length + len(part) > max_length:
            break
        
        context_parts.append(part)
        current_length += len(part)
    
    return "\n".join(context_parts)
