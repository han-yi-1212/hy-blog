import requests
import os

DEEPSEEK_API_KEY = os.getenv('DEEPSEEK_API_KEY', 'sk-010f097d937349daa2a1858e78b846a2')
DEEPSEEK_API_URL = 'https://api.deepseek.com/v1/chat/completions'

def generate(prompt: str, max_tokens: int = 2000) -> str:
    try:
        response = requests.post(
            DEEPSEEK_API_URL,
            headers={
                'Authorization': f'Bearer {DEEPSEEK_API_KEY}',
                'Content-Type': 'application/json'
            },
            json={
                'model': 'deepseek-chat',
                'messages': [
                    {'role': 'user', 'content': prompt}
                ],
                'max_tokens': max_tokens,
                'temperature': 0.7
            },
            timeout=60
        )
        
        if response.status_code == 200:
            data = response.json()
            return data['choices'][0]['message']['content']
        else:
            return f"AI调用失败: {response.status_code}"
            
    except Exception as e:
        return f"AI调用异常: {str(e)}"

def generate_rag_answer(question: str, context: str) -> str:
    prompt = f"""请基于以下知识库内容回答问题。如果知识库中没有相关信息，请诚实告知，不要编造答案。

【知识库内容】
{context}

【问题】
{question}

【回答要求】
1. 只基于提供的知识库内容回答
2. 如果知识库中没有相关信息，请明确说明
3. 回答要逻辑清晰、条理分明
4. 可以适当引用知识库中的原文

请回答："""
    
    return generate(prompt)
