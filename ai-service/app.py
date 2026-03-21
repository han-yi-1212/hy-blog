from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
import json

app = FastAPI()

# DeepSeek API配置
API_KEY = "sk-010f097d937349daa2a1858e78b846a2"
API_URL = "https://api.deepseek.com/v1/chat/completions"
MODEL = "deepseek-chat"

class GenerateRequest(BaseModel):
    topic: str

class SummaryRequest(BaseModel):
    content: str

def call_deepseek_api(prompt):
    """调用DeepSeek API"""
    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }
    
    data = {
        "model": MODEL,
        "messages": [
            {"role": "user", "content": prompt}
        ]
    }
    
    try:
        response = requests.post(API_URL, headers=headers, json=data, timeout=30)
        response.raise_for_status()
        result = response.json()
        
        # 提取content
        if "choices" in result and len(result["choices"]) > 0:
            return result["choices"][0]["message"]["content"]
        else:
            raise Exception("API响应格式错误")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"API调用失败: {str(e)}")

@app.post("/api/ai/generate")
async def generate_blog(request: GenerateRequest):
    """生成博客文章"""
    prompt = f"请写一篇博客文章：\n主题：{request.topic}\n要求：\n1. 结构清晰（引言 + 正文 + 总结）\n2. 适合初学者\n3. 1000字左右\n4. 使用通俗语言"
    return {"content": call_deepseek_api(prompt)}

@app.post("/api/ai/summary")
async def summarize_content(request: SummaryRequest):
    """总结文章内容"""
    # 控制token消耗
    content = request.content
    if len(content) > 2000:
        content = content[:2000]
    
    prompt = f"请用3句话总结以下文章内容，简洁清晰：\n{content}"
    return {"content": call_deepseek_api(prompt)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
