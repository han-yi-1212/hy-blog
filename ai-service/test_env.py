import os
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

# 测试API_KEY是否正确读取
api_key = os.getenv("API_KEY")
print(f"API_KEY: {api_key}")
print(f"API_KEY length: {len(api_key) if api_key else 0}")

if api_key:
    print("环境变量配置成功！")
else:
    print("环境变量配置失败，API_KEY为空！")
