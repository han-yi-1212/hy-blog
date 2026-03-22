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
        response = requests.post(API_URL, headers=headers, json=data, timeout=60)
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
    
    prompt = f"""角色：你是一位专业、客观、精准的文本总结专家，具备极强的信息提炼和逻辑梳理能力，能无偏差还原作者的核心意图。
任务：请对我提供的文章进行全面且精炼的总结，严格遵守以下要求：
1.  客观中立：完全基于原文内容总结，不添加任何个人观点、主观解读、额外延伸和无关内容，不篡改原文的核心观点与事实信息。
2.  核心完整：必须完整保留原文的核心主题、关键论点、重要数据、核心事件/结论、关键逻辑脉络，不遗漏影响原文理解的核心信息。
3.  精炼通顺：语言简洁凝练，语句通顺流畅，去除原文中无关的举例、修饰、铺垫性内容，避免冗余重复。
4.  输出格式：
    - 先输出【文章核心主题】：用1句话清晰概括全文的核心主题。
    - 再输出【全文总结】：短文控制在200字以内，中长文控制在300-500字，超长文可分点输出，最多不超过5点。
5.  特殊要求：如果原文包含明确的时间、人物、关键数据、重要结论，必须在总结中精准体现。

以下是需要总结的文章：
{content}"""
    return {"content": call_deepseek_api(prompt)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
