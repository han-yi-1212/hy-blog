from sentence_transformers import SentenceTransformer
import numpy as np
import os

_model = None

# 设置本地模型缓存目录
MODEL_DIR = os.path.join(os.path.dirname(__file__), '..', 'models')
MODEL_NAME = 'sentence-transformers/all-MiniLM-L6-v2'

def get_model():
    global _model
    if _model is None:
        # 尝试从本地加载，否则从HuggingFace下载
        local_path = os.path.join(MODEL_DIR, 'all-MiniLM-L6-v2')
        if os.path.exists(local_path):
            _model = SentenceTransformer(local_path)
        else:
            # 使用离线模式，如果本地没有就报错
            os.environ['HF_DATASETS_OFFLINE'] = '1'
            os.environ['TRANSFORMERS_OFFLINE'] = '1'
            _model = SentenceTransformer(MODEL_NAME, cache_folder=MODEL_DIR)
    return _model

def embed(text):
    if not text or not text.strip():
        return np.zeros(384)
    model = get_model()
    return model.encode(text, convert_to_numpy=True)

def embed_batch(texts):
    if not texts:
        return np.array([])
    model = get_model()
    return model.encode(texts, convert_to_numpy=True)
