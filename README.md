# hyのblog

<p align="center">
  <img src="frontend/public/images/logo_transparent.png" alt="hyのblog Logo" width="120">
</p>

<p align="center">
  一个基于 Spring Boot + Vue 3 的现代博客系统，支持 AI 辅助写作和内容总结功能。
</p>

<p align="center">
  <a href="#功能特性">功能特性</a> •
  <a href="#技术栈">技术栈</a> •
  <a href="#技术架构图">技术架构图</a> •
  <a href="#快速开始">快速开始</a> •
  <a href="#项目结构">项目结构</a> •
  <a href="#api文档">API文档</a> •
  <a href="#部署">部署</a> •
  <a href="#roadmap">Roadmap</a>
</p>

---

## 功能特性

### 核心功能
- ✅ **用户中心**：注册、登录、个人资料管理、头像上传
- ✅ **博客管理**：发布、编辑、删除博客，支持 Markdown 格式
- ✅ **浏览统计**：文章浏览量、字数统计、阅读时间计算
- ✅ **嵌套评论**：支持多级回复，树形结构展示
- ✅ **文章反馈**：免登录点赞/点踩功能
- ✅ **文章排行榜**：基于浏览量和点赞数的热门文章排行（V2.0.0 新增）
- ✅ **随机阅读**：随机推荐 4 篇博客文章（V2.0.0 新增）
- ✅ **管理员权限体系**：主管理员保护，禁止其他管理员删除主管理员（2.1.0 新增）

### UI/UX 特性
- ✅ **暗色模式**：一键切换明暗主题
- ✅ **毛玻璃效果**：现代感的导航栏设计
- ✅ **响应式布局**：适配桌面端和移动端
- ✅ **樱花飘落动画**：首页背景特效
- ✅ **平滑滚动**：优雅的页面滚动体验
- ✅ **代码块一键复制**：支持代码块复制功能（2.1.0 新增）

### AI 功能
- ✅ **AI 辅助写作**：输入主题自动生成博客文章
- ✅ **AI 内容总结**：一键总结文章要点
- ✅ **AI 知识问答**：基于 RAG 技术的智能问答系统，支持语义搜索（V2.0.0 新增）
- ✅ **Python + Java 混合架构**：AI 服务独立部署，支持水平扩展

### RAG 知识库功能（V2.0.0 新增）
- ✅ **语义搜索**：基于向量嵌入的相似度检索
- ✅ **智能问答**：结合知识库内容生成精准回答
- ✅ **自动同步**：博客发布自动更新知识库
- ✅ **相似度阈值过滤**：自动过滤低相关性内容

### 标签系统（V2.0.0 新增）
- ✅ **文章标签**：为博客添加多个标签分类
- ✅ **标签云**：首页展示热门标签
- ✅ **标签筛选**：按标签筛选相关文章
- ✅ **发布文章必须选择标签**：强制标签选择（2.1.0 新增）

### 编辑器增强（V2.0.0 新增）
- ✅ **Markdown 编辑器**：集成 Vditor 富文本编辑器
- ✅ **实时预览**：编写时实时预览渲染效果
- ✅ **草稿箱**：自动保存草稿，防止内容丢失

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| [![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java)](https://www.oracle.com/java/) | 21 | 编程语言 |
| [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot) | 3.2.0 | 应用框架 |
| [![Spring Security](https://img.shields.io/badge/Spring%20Security-6.1.1-6DB33F?style=flat-square&logo=spring-security)](https://spring.io/projects/spring-security) | 6.1.1 | 安全认证 |
| [![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.5-000000?style=flat-square&logo=mybatis)](https://baomidou.com/) | 3.5.5 | ORM 框架 |
| [![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/) | 8.0 | 数据库 |
| [![JWT](https://img.shields.io/badge/JWT-jsonwebtoken-000000?style=flat-square&logo=json-web-tokens)](https://jwt.io/) | 0.12.3 | Token 认证 |
| [![OkHttp](https://img.shields.io/badge/OkHttp-4.12.0-000000?style=flat-square)](https://square.github.io/okhttp/) | 4.12.0 | HTTP 客户端 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| [![Vue.js](https://img.shields.io/badge/Vue.js-3.3.4-4FC08D?style=flat-square&logo=vue.js)](https://vuejs.org/) | 3.3.4 | 前端框架 |
| [![Vite](https://img.shields.io/badge/Vite-4.4.9-646CFF?style=flat-square&logo=vite)](https://vitejs.dev/) | 4.4.9 | 构建工具 |
| [![Element Plus](https://img.shields.io/badge/Element%20Plus-2.3.12-409EFF?style=flat-square&logo=element)](https://element-plus.org/) | 2.3.12 | UI 组件库 |
| [![Pinia](https://img.shields.io/badge/Pinia-2.1.6-42B883?style=flat-square&logo=pinia)](https://pinia.vuejs.org/) | 2.1.6 | 状态管理 |
| [![Vue Router](https://img.shields.io/badge/Vue%20Router-4.2.4-4FC08D?style=flat-square&logo=vue.js)](https://router.vuejs.org/) | 4.2.4 | 路由管理 |
| [![Axios](https://img.shields.io/badge/Axios-1.6.2-5A29E4?style=flat-square)](https://axios-http.com/) | 1.6.2 | HTTP 客户端 |
| [![SCSS](https://img.shields.io/badge/SCSS-1.69.5-CC6699?style=flat-square&logo=sass)](https://sass-lang.com/) | 1.69.5 | CSS 预处理器 |

### AI 服务技术

| 技术 | 版本 | 说明 |
|------|------|------|
| [![Python](https://img.shields.io/badge/Python-3.9+-3776AB?style=flat-square&logo=python)](https://www.python.org/) | 3.9+ | 编程语言 |
| [![FastAPI](https://img.shields.io/badge/FastAPI-0.104.1-009688?style=flat-square&logo=fastapi)](https://fastapi.tiangolo.com/) | 0.104.1 | Web 框架 |
| [![DeepSeek](https://img.shields.io/badge/DeepSeek-API-000000?style=flat-square)](https://www.deepseek.com/) | - | AI 模型 API |
| [![Uvicorn](https://img.shields.io/badge/Uvicorn-0.24.0-000000?style=flat-square)](https://www.uvicorn.org/) | 0.24.0 | ASGI 服务器 |

### RAG 技术栈 (V2.0.0)

| 技术 | 版本 | 说明 |
|------|------|------|
| [![Sentence Transformers](https://img.shields.io/badge/Sentence--Transformers-2.2.2-FF6F00?style=flat-square)](https://www.sbert.net/) | 2.2.2 | 文本嵌入模型 |
| [![FAISS](https://img.shields.io/badge/FAISS-1.7.4-0081CB?style=flat-square)](https://faiss.ai/) | 1.7.4 | 向量数据库 |
| [![NumPy](https://img.shields.io/badge/NumPy-1.24.0-013243?style=flat-square&logo=numpy)](https://numpy.org/) | 1.24.0 | 数值计算 |

---

## 技术架构图

### 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              hyのblog 技术架构                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         前端层 (Frontend)                            │   │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐   │   │
│  │  │ Vue 3   │  │ Vite    │  │ Element │  │ Pinia   │  │ Axios   │   │   │
│  │  │         │  │         │  │ Plus    │  │         │  │         │   │   │
│  │  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘   │   │
│  │       └────────────┴────────────┴────────────┴────────────┘        │   │
│  │                              │                                      │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │  页面组件: Home │ BlogDetail │ Write │ UserProfile │ RagChat  │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │  功能组件: Ranking │ RandomPosts │ TagCloud │ TagSelector     │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │ HTTP/REST API                          │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        后端层 (Backend)                              │   │
│  │  ┌───────────────────────────────────────────────────────────────┐  │   │
│  │  │                    Spring Boot 3.2.0                          │  │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │  │   │
│  │  │  │ Spring      │  │ MyBatis     │  │ JWT         │           │  │   │
│  │  │  │ Security    │  │ Plus        │  │ Auth        │           │  │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘           │  │   │
│  │  └───────────────────────────────────────────────────────────────┘  │   │
│  │                              │                                       │   │
│  │  ┌───────────────────────────────────────────────────────────────┐  │   │
│  │  │  Controller: User │ Blog │ Comment │ Tag │ RAG │ AI │ Upload │  │   │
│  │  └───────────────────────────────────────────────────────────────┘  │   │
│  │                              │                                       │   │
│  │  ┌───────────────────────────────────────────────────────────────┐  │   │
│  │  │  Service: UserService │ BlogService │ RagService │ TagService │  │   │
│  │  └───────────────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│         │                        │                        │                 │
│         │ JDBC                   │ HTTP                   │ HTTP            │
│         ▼                        ▼                        ▼                 │
│  ┌─────────────┐    ┌─────────────────────────┐    ┌───────────────────┐   │
│  │   MySQL     │    │     AI 服务层 (AI)      │    │   DeepSeek API    │   │
│  │   8.0       │    │  ┌─────────────────┐    │    │                   │   │
│  │             │    │  │   FastAPI       │    │    │  ┌─────────────┐  │   │
│  │ ┌─────────┐ │    │  │                 │    │    │  │  AI 生成    │  │   │
│  │ │ User    │ │    │  │ ┌─────────────┐ │    │    │  │  AI 总结    │  │   │
│  │ │ Blog    │ │    │  │ │ RAG Pipeline│ │    │    │  │  RAG 问答   │  │   │
│  │ │ Comment │ │    │  │ │             │ │    │    │  └─────────────┘  │   │
│  │ │ Tag     │ │    │  │ │ ┌─────────┐ │ │    │    │                   │   │
│  │ │ Draft   │ │    │  │ │ │Embedder │ │ │    │    └───────────────────┘   │
│  │ └─────────┘ │    │  │ │ └─────────┘ │ │    │                            │
│  │             │    │  │ │ ┌─────────┐ │ │    │                            │
│  │             │    │  │ │ │Retriever│ │ │    │                            │
│  │             │    │  │ │ └─────────┘ │ │    │                            │
│  │             │    │  │ │ ┌─────────┐ │ │    │                            │
│  │             │    │  │ │ │Generator│ │ │    │                            │
│  │             │    │  │ │ └─────────┘ │ │    │                            │
│  │             │    │  │ └─────────────┘ │    │                            │
│  │             │    │  └─────────────────┘    │                            │
│  │             │    │           │              │                            │
│  │             │    │           ▼              │                            │
│  │             │    │  ┌─────────────────┐    │                            │
│  │             │    │  │     FAISS       │    │                            │
│  │             │    │  │  向量数据库      │    │                            │
│  │             │    │  │  (本地存储)      │    │                            │
│  │             │    │  └─────────────────┘    │                            │
│  └─────────────┘    └─────────────────────────┘                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### RAG 知识库架构 (V2.0.0)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           RAG 知识库工作流程                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐                                                           │
│  │  博客文章    │                                                           │
│  │  (MySQL)     │                                                           │
│  └──────┬───────┘                                                           │
│         │ 同步知识库                                                         │
│         ▼                                                                   │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                │
│  │  文本分块    │ ──▶ │  文本嵌入    │ ──▶ │  向量存储    │                │
│  │  (Chunking)  │     │  (Embedding) │     │  (FAISS)     │                │
│  └──────────────┘     └──────────────┘     └──────┬───────┘                │
│         │                                          │                        │
│         │                                          │                        │
│         ▼                                          ▼                        │
│  ┌──────────────┐                          ┌──────────────┐                │
│  │  用户提问    │                          │  相似度检索  │                │
│  │              │                          │  (Retrieval) │                │
│  └──────┬───────┘                          └──────┬───────┘                │
│         │                                          │                        │
│         │         ┌──────────────────────┐         │                        │
│         └────────▶│    问题嵌入          │◀────────┘                        │
│                   │    (Query Embedding) │                                  │
│                   └──────────┬───────────┘                                  │
│                              │                                              │
│                              ▼                                              │
│                   ┌──────────────────────┐                                  │
│                   │    上下文拼接        │                                  │
│                   │    (Context Build)   │                                  │
│                   └──────────┬───────────┘                                  │
│                              │                                              │
│                              ▼                                              │
│                   ┌──────────────────────┐                                  │
│                   │    DeepSeek API      │                                  │
│                   │    生成回答          │                                  │
│                   └──────────┬───────────┘                                  │
│                              │                                              │
│                              ▼                                              │
│                   ┌──────────────────────┐                                  │
│                   │    返回答案          │                                  │
│                   │    + 相关文章引用    │                                  │
│                   └──────────────────────┘                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 数据流向图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              数据流向                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  用户操作          前端处理           后端处理           数据存储            │
│  ────────          ────────           ────────           ────────            │
│                                                                             │
│  ┌───────┐        ┌───────┐        ┌───────┐        ┌───────┐              │
│  │ 浏览  │ ──────▶│ 渲染  │ ──────▶│ 查询  │ ──────▶│ MySQL │              │
│  │ 博客  │        │ 页面  │        │ 博客  │        │       │              │
│  └───────┘        └───────┘        └───────┘        └───────┘              │
│                                                                             │
│  ┌───────┐        ┌───────┐        ┌───────┐        ┌───────┐              │
│  │ 发布  │ ──────▶│ 编辑  │ ──────▶│ 保存  │ ──────▶│ MySQL │──┐           │
│  │ 博客  │        │ Markdown│       │ 博客  │        │       │  │           │
│  └───────┘        └───────┘        └───────┘        └───────┘  │           │
│                                                                  │           │
│                                                                  ▼           │
│                                                           ┌───────────┐     │
│                                                           │  AI 服务  │     │
│                                                           │  同步知识库│     │
│                                                           └─────┬─────┘     │
│                                                                 │           │
│                                                                 ▼           │
│                                                           ┌───────────┐     │
│                                                           │   FAISS   │     │
│                                                           │  向量库   │     │
│                                                           └───────────┘     │
│                                                                             │
│  ┌───────┐        ┌───────┐        ┌───────┐        ┌───────┐              │
│  │ AI    │ ──────▶│ 发送  │ ──────▶│ RAG   │ ──────▶│ FAISS │              │
│  │ 问答  │        │ 问题  │        │ 检索  │        │ 检索  │              │
│  └───────┘        └───────┘        └───────┘        └───────┘              │
│                          │              │                                   │
│                          │              ▼                                   │
│                          │        ┌───────────┐                             │
│                          └───────▶│ DeepSeek  │                             │
│                                   │ 生成答案  │                             │
│                                   └───────────┘                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 快速开始

### 环境要求

在开始之前，请确保你的系统已安装以下软件：

- **JDK 21**：后端运行环境
- **Node.js 16+**：前端构建工具
- **Python 3.9+**：AI 服务运行环境
- **MySQL 8.0+**：数据存储
- **Redis**（可选）：用于缓存点赞数据

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/hy-blog.git
cd hy-blog
```

### 2. 数据库配置

#### 2.1 创建数据库

```sql
CREATE DATABASE blue_sakura_blog 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

#### 2.2 执行初始化脚本

```powershell
# Windows
mysql -u root -p blue_sakura_blog < backend/src/main/resources/schema.sql

# Linux/Mac
mysql -u root -p blue_sakura_blog < backend/src/main/resources/schema.sql
```

#### 2.3 配置数据库连接

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blue_sakura_blog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 3. 启动服务

#### 方式一：使用命令行（开发模式）

**步骤 1：启动 AI 服务**

```powershell
cd ai-service

# 创建虚拟环境（推荐）
python -m venv venv

# 激活虚拟环境
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# 安装依赖
pip install -r requirements.txt

# 配置 DeepSeek API 密钥
# 编辑 app.py，设置你的 API_KEY

# 启动服务
python app.py
```

AI 服务将在 http://localhost:8000 启动

**步骤 2：启动后端服务**

```powershell
cd backend

# Windows（使用指定 JDK）
$env:JAVA_HOME="D:\java21"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn spring-boot:run

# Linux/Mac
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
./mvnw spring-boot:run
```

后端服务将在 http://localhost:8080 启动

**步骤 3：启动前端服务**

```powershell
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:3000 启动

#### 方式二：使用 Docker（生产模式）

```powershell
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 4. 访问应用

- **前端页面**：http://localhost:3000
- **后端 API**：http://localhost:8080/api
- **AI 服务**：http://localhost:8000
- **API 文档**：http://localhost:8080/swagger-ui.html（如果配置了 Swagger）

### 5. 初始数据

系统启动后会自动创建管理员账号：
- 用户名：admin
- 密码：admin123

**注意**：请在生产环境中修改默认密码！

---

## 项目结构

```
hy-blog/
├── 📁 ai-service/                 # AI 服务模块 (V2.0.0 增强)
│   ├── 📄 app.py                  # FastAPI 主应用
│   ├── 📄 requirements.txt        # Python 依赖
│   ├── 📄 Dockerfile              # Docker 配置
│   └── 📁 rag/                    # RAG 模块 (V2.0.0 新增)
│       ├── 📄 __init__.py
│       ├── 📄 embedder.py         # 文本嵌入
│       ├── 📄 retriever.py        # 向量检索
│       ├── 📄 generator.py        # 答案生成
│       ├── 📄 pipeline.py         # RAG 流水线
│       └── 📄 vector_store.py     # 向量存储
│
├── 📁 backend/                    # 后端服务模块
│   ├── 📁 src/main/java/com/bluesakura/blog/
│   │   ├── 📁 common/             # 通用工具类
│   │   │   └── 📄 Result.java     # 统一响应封装
│   │   ├── 📁 config/             # 配置类
│   │   │   ├── 📄 AIConfig.java
│   │   │   ├── 📄 SecurityConfig.java
│   │   │   └── 📄 WebConfig.java
│   │   ├── 📁 controller/         # API 控制器
│   │   │   ├── 📄 AIController.java
│   │   │   ├── 📄 BlogController.java
│   │   │   ├── 📄 CommentController.java
│   │   │   ├── 📄 FeedbackController.java
│   │   │   ├── 📄 LikeController.java
│   │   │   ├── 📄 RagController.java        # RAG 知识库控制器 (V2.0.0)
│   │   │   ├── 📄 TagController.java        # 标签控制器 (V2.0.0)
│   │   │   ├── 📄 UploadController.java
│   │   │   └── 📄 UserController.java
│   │   ├── 📁 dto/                # 数据传输对象
│   │   ├── 📁 entity/             # 数据实体
│   │   │   ├── 📄 Blog.java
│   │   │   ├── 📄 Comment.java
│   │   │   ├── 📄 Feedback.java
│   │   │   ├── 📄 Tag.java                  # 标签实体 (V2.0.0)
│   │   │   └── 📄 User.java
│   │   ├── 📁 exception/          # 异常处理
│   │   ├── 📁 mapper/             # MyBatis 映射
│   │   ├── 📁 security/           # 安全相关
│   │   │   ├── 📄 JwtAuthenticationFilter.java
│   │   │   └── 📄 JwtUtil.java
│   │   ├── 📁 service/            # 业务逻辑
│   │   │   ├── 📄 AIService.java
│   │   │   ├── 📄 BlogService.java
│   │   │   ├── 📄 CommentService.java
│   │   │   ├── 📄 RagService.java           # RAG 服务 (V2.0.0)
│   │   │   ├── 📄 TagService.java           # 标签服务 (V2.0.0)
│   │   │   └── 📄 UserService.java
│   │   └── 📄 BlogApplication.java
│   ├── 📁 src/main/resources/
│   │   ├── 📄 application.yml     # 应用配置
│   │   └── 📄 schema.sql          # 数据库脚本
│   └── 📄 pom.xml
│
├── 📁 frontend/                   # 前端模块
│   ├── 📁 public/                 # 静态资源
│   │   └── 📁 images/             # 图片资源
│   ├── 📁 src/
│   │   ├── 📁 api/                # API 封装
│   │   │   ├── 📄 ai.js
│   │   │   ├── 📄 blog.js
│   │   │   ├── 📄 comment.js
│   │   │   ├── 📄 rag.js                    # RAG API (V2.0.0)
│   │   │   ├── 📄 tag.js                    # 标签 API (V2.0.0)
│   │   │   └── 📄 request.js
│   │   ├── 📁 components/         # 组件
│   │   │   ├── 📄 CommentItem.vue
│   │   │   ├── 📄 CommentSection.vue
│   │   │   ├── 📄 FixedNav.vue
│   │   │   ├── 📄 HeroSection.vue
│   │   │   ├── 📄 Ranking.vue               # 文章排行榜 (V2.0.0)
│   │   │   ├── 📄 RandomPosts.vue           # 随机阅读 (V2.0.0)
│   │   │   ├── 📄 TagCloud.vue              # 标签云 (V2.0.0)
│   │   │   └── 📄 TagSelector.vue           # 标签选择器 (V2.0.0)
│   │   ├── 📁 router/             # 路由配置
│   │   ├── 📁 stores/             # Pinia 状态管理
│   │   ├── 📁 styles/             # 全局样式
│   │   ├── 📁 views/              # 页面视图
│   │   │   ├── 📄 BlogDetail.vue
│   │   │   ├── 📄 Home.vue
│   │   │   ├── 📄 Login.vue
│   │   │   ├── 📄 RagChat.vue               # RAG 问答页面 (V2.0.0)
│   │   │   ├── 📄 UserProfile.vue
│   │   │   └── 📄 Write.vue
│   │   ├── 📄 App.vue
│   │   └── 📄 main.js
│   ├── 📄 index.html
│   ├── 📄 package.json
│   └── 📄 vite.config.js
│
├── 📁 upload/                     # 上传文件存储
├── 📄 docker-compose.yml          # Docker Compose 配置
├── 📄 README.md                   # 项目说明
└── 📄 LICENSE                     # 许可证
```

### 目录说明

| 目录 | 说明 |
|------|------|
| `ai-service/` | Python AI 服务，负责调用 DeepSeek API，V2.0.0 新增 RAG 模块 |
| `backend/` | Java Spring Boot 后端服务，V2.0.0 新增 RAG 和标签相关接口 |
| `frontend/` | Vue 3 前端应用，V2.0.0 新增排行榜、标签云、RAG 问答等组件 |
| `upload/` | 用户上传的文件存储目录 |

---

## API 文档

### 用户相关

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录 | 否 |
| GET | `/api/user/profile/{id}` | 获取用户资料 | 否 |
| PUT | `/api/user/profile` | 更新用户资料 | 是 |
| POST | `/api/user/avatar` | 上传头像 | 是 |

### 博客相关

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/blog` | 获取博客列表 | 否 |
| GET | `/api/blog/{id}` | 获取博客详情 | 否 |
| POST | `/api/blog` | 创建博客 | 是 |
| PUT | `/api/blog/{id}` | 更新博客 | 是 |
| DELETE | `/api/blog/{id}` | 删除博客 | 是 |
| POST | `/api/blog/{id}/view` | 增加浏览量 | 否 |

### 评论相关

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/comment/blog/{blogId}` | 获取评论列表 | 否 |
| POST | `/api/comment` | 发表评论 | 是 |
| DELETE | `/api/comment/{id}` | 删除评论 | 是 |

### AI 相关

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/ai/generate` | AI 生成博客 | 否 |
| POST | `/api/ai/summary` | AI 总结内容 | 否 |

### RAG 知识库相关 (V2.0.0)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/rag/chat` | RAG 智能问答 | 否 |
| GET | `/api/rag/stats` | 获取知识库统计 | 是 |
| POST | `/api/rag/sync` | 同步知识库 | 是 |
| DELETE | `/api/rag/clear` | 清空知识库 | 是 |

### 标签相关 (V2.0.0)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/tag` | 获取所有标签 | 否 |
| GET | `/api/tag/{id}` | 获取标签详情 | 否 |
| GET | `/api/tag/blog/{blogId}` | 获取博客的标签 | 否 |
| POST | `/api/tag` | 创建标签 | 是 |
| POST | `/api/tag/blog/{blogId}` | 为博客添加标签 | 是 |
| DELETE | `/api/tag/blog/{blogId}/{tagId}` | 删除博客标签 | 是 |

### 反馈相关

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/feedback` | 提交反馈 | 否 |

---

## 部署

### 生产环境部署指南

#### 1. 构建前端

```powershell
cd frontend
npm install
npm run build
```

构建后的文件位于 `frontend/dist/` 目录

#### 2. 构建后端

```powershell
cd backend
mvn clean package -DskipTests
```

构建后的 JAR 文件位于 `backend/target/` 目录

#### 3. 配置生产环境

**后端配置** (`application-prod.yml`)：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-mysql-host:3306/blue_sakura_blog
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
server:
  port: 8080

# AI 服务配置
ai:
  service:
    url: http://ai-service:8000
```

**Nginx 配置**：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # 上传文件
    location /upload/ {
        alias /path/to/upload/;
    }
}
```

#### 4. Docker 部署

```powershell
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f ai-service
```

### 局域网访问配置

#### 1. 查看本机 IP

```powershell
ipconfig
# 或
ifconfig
```

#### 2. 开放防火墙端口

**Windows**：

```powershell
# 开放前端端口
netsh advfirewall firewall add rule name="Blog Frontend" dir=in action=allow protocol=tcp localport=3000

# 开放后端端口
netsh advfirewall firewall add rule name="Blog Backend" dir=in action=allow protocol=tcp localport=8080

# 开放 AI 服务端口
netsh advfirewall firewall add rule name="Blog AI Service" dir=in action=allow protocol=tcp localport=8000
```

**Linux (iptables)**：

```bash
sudo iptables -A INPUT -p tcp --dport 3000 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 8000 -j ACCEPT
```

#### 3. 修改前端配置

编辑 `frontend/vite.config.js`：

```javascript
server: {
  host: '0.0.0.0',  // 允许外部访问
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://your-server-ip:8080',
      changeOrigin: true
    }
  }
}
```

#### 4. 其他设备访问

在同一局域网内的其他设备上访问：

```
http://<服务器IP地址>:3000
```

---

## Roadmap

### 近期计划 (2.2.0)

- [ ] **AI 自动摘要**：自动为每篇文章生成摘要
- [ ] **RSS 订阅**：支持 RSS 2.0 和 Atom 格式订阅

### 中期计划 (3.0.0)

- [ ] **邮件通知**：评论回复邮件通知
- [ ] **多主题支持**：提供 3-5 种主题风格切换
- [ ] **图片上传优化**：支持拖拽上传、图片压缩、图床集成
- [ ] **访问统计**：详细的访问统计和数据分析面板

### 长期计划 (4.0.0)

- [ ] **多语言支持**：国际化（i18n），支持中英文切换
- [ ] **插件系统**：支持第三方插件扩展
- [ ] **移动端 App**：基于 Flutter 或 React Native 的移动端应用
- [ ] **PWA 支持**：渐进式 Web 应用，支持离线访问

### 2.1.0 已完成的功能

- [x] **代码块一键复制**：支持代码块复制功能
- [x] **发布文章必须选择标签**：强制标签选择
- [x] **管理员权限体系**：主管理员保护，禁止其他管理员删除主管理员

### 2.0.0 已完成的功能

- [x] **RAG 知识库系统**：向量嵌入 + 语义搜索
- [x] **AI 知识问答**：基于知识库的智能问答
- [x] **标签系统**：文章标签分类和筛选
- [x] **Markdown 编辑器增强**：Vditor 集成
- [x] **草稿箱功能**：自动保存草稿
- [x] **随机阅读**：随机推荐文章
- [x] **文章排行榜**：热度排行榜

### 已完成功能

- [x] 用户中心系统
- [x] 博客管理功能
- [x] 嵌套评论系统
- [x] 文章反馈功能
- [x] 暗色模式
- [x] 毛玻璃导航栏
- [x] ~~文章热度地图~~ → **文章排行榜**（V2.0.0 重构）
- [x] AI 辅助写作
- [x] AI 内容总结
- [x] **AI 知识问答**（V2.0.0 新增）
- [x] **RAG 知识库系统**（V2.0.0 新增）
- [x] **标签系统**（V2.0.0 新增）
- [x] **Markdown 编辑器增强**（V2.0.0 新增）
- [x] **草稿箱功能**（V2.0.0 新增）
- [x] **随机阅读**（V2.0.0 新增）
- [x] 响应式设计
- [x] 局域网访问支持

---

## 常见问题

### Q1: AI 功能无法使用，提示"生成失败"

**A**: 请检查以下几点：
1. AI 服务是否已启动（端口 8000）
2. DeepSeek API 密钥是否正确配置
3. 网络连接是否正常
4. 查看后端日志获取详细错误信息

### Q2: 如何修改网站名称和 Logo？

**A**: 
- 网站名称：修改 `frontend/src/components/FixedNav.vue` 中的标题文本
- Logo：替换 `frontend/public/images/logo_transparent.png` 文件

### Q3: 如何备份数据库？

**A**:
```powershell
# 备份
mysqldump -u root -p blue_sakura_blog > backup.sql

# 恢复
mysql -u root -p blue_sakura_blog < backup.sql
```

### Q4: 如何部署到云服务器？

**A**: 推荐使用 Docker Compose 部署，详见[部署](#部署)章节。

---

## 贡献指南

我们欢迎所有形式的贡献，包括但不限于：

- 提交 Bug 报告
- 提出新功能建议
- 改进文档
- 提交代码修复
- 分享使用经验

### 提交 PR 流程

1. **Fork 项目**：点击右上角的 Fork 按钮
2. **克隆代码**：`git clone https://github.com/yourusername/hy-blog.git`
3. **创建分支**：`git checkout -b feature/your-feature-name`
4. **提交更改**：`git commit -m "feat: add some feature"`
5. **推送分支**：`git push origin feature/your-feature-name`
6. **创建 PR**：在 GitHub 上提交 Pull Request

### 代码规范

- Java 代码遵循 Google Java Style Guide
- Vue 代码遵循 Vue Style Guide
- 提交信息遵循 Conventional Commits 规范

---

## 更新日志

### v2.0.0 (2026-03-23)

#### 新增功能
- ✨ **RAG 知识库系统**：基于向量嵌入的智能知识库，支持语义搜索
- ✨ **AI 知识问答**：结合知识库内容回答用户问题，显示相关文章引用
- ✨ **标签系统**：为文章添加标签，支持标签云展示和筛选
- ✨ **Markdown 编辑器增强**：集成 Vditor 编辑器，支持实时预览
- ✨ **草稿箱功能**：自动保存文章草稿，防止内容丢失
- ✨ **随机阅读**：首页随机推荐 4 篇博客文章
- ✨ **文章排行榜**：重构热度地图为排行榜，按热度分数排序展示

#### 优化改进
- 🎨 **个人中心改版**：评论列表改为用户动态（发布记录）
- 🎨 **昵称同步**：用户修改昵称后自动同步到所有文章
- 🎨 **相似度优化**：RAG 检索使用余弦相似度，过滤低相关性内容
- 🐛 **Bug 修复**：修复多个 V9.0 升级后出现的问题

### v1.0.0 (2026-03-21)

- ✨ 初始版本发布
- ✨ 用户中心系统
- ✨ 博客管理功能
- ✨ 嵌套评论系统
- ✨ AI 辅助写作
- ✨ AI 内容总结
- ✨ 暗色模式
- ✨ 响应式设计

---

## 许可证

本项目采用 [MIT](LICENSE) 许可证开源。

```
MIT License

Copyright (c) 2026 hyのblog

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 联系方式

- 📧 邮箱：2275789176@qq.com
- 💬 GitHub Issues：[提交问题](https://github.com/hanyi-1212/hy-blog/issues)
- 🌐 项目主页：https://github.com/hanyi-1212/hy-blog

---

<p align="center">
  如果这个项目对你有帮助，请给我们一个 ⭐️ Star！
</p>

<p align="center">
  <b>hyのblog</b> - 用心记录，分享技术，传递价值
</p>
