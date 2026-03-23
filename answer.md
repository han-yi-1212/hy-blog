# Blue Sakura Blog 常见问题解答

## 一、如何启动前后端进程

### 方式一：使用命令行（推荐开发时使用）

#### 启动后端
```powershell
# 打开 PowerShell，进入项目目录
cd e:\自主博客

# 设置 JDK 21 环境并启动后端
$env:JAVA_HOME="D:\java21"; $env:PATH="$env:JAVA_HOME\bin;$env:PATH"; cd backend; mvn spring-boot:run
```

#### 启动前端
```powershell
# 打开新的 PowerShell 窗口
cd e:\自主博客\frontend

# 启动前端开发服务器
npm run dev
```

#### 启动AI服务
```powershell
# 打开新的 PowerShell 窗口
cd e:\自主博客\ai-service

# 首次运行需要先安装依赖
pip install -r requirements.txt

# 启动AI服务
python app.py
```

### 方式二：使用 Docker（推荐部署时使用）

```powershell
cd e:\自主博客

# 一键启动所有服务（MySQL + Redis + 后端 + 前端）
docker-compose up -d

# 查看运行状态
docker-compose ps

# 停止所有服务
docker-compose down
```

---

## 二、如何停止前后端进程

### 方式一：在终端中按快捷键

在运行进程的终端窗口中：
- 按 `Ctrl + C` 停止进程

### 方式二：使用命令查找并终止进程

#### 停止后端（端口 8080）
```powershell
# 查找占用 8080 端口的进程
netstat -ano | findstr :8080

# 终止进程（将 PID 替换为上一步查到的进程ID）
taskkill /PID <PID> /F

# 示例：如果查到 PID 是 12345
taskkill /PID 12345 /F
```

#### 停止前端（端口 3000/3001/3002）
```powershell
# 查找占用端口的进程
netstat -ano | findstr :3000
netstat -ano | findstr :3001
netstat -ano | findstr :3002

# 终止进程
taskkill /PID <PID> /F
```

#### 停止AI服务（端口 8000）
```powershell
# 查找占用 8000 端口的进程
netstat -ano | findstr :8000

# 终止进程
taskkill /PID <PID> /F
```

#### 一键停止所有 Node.js 进程（慎用）
```powershell
taskkill /IM node.exe /F
```

#### 一键停止所有 Java 进程（慎用）
```powershell
taskkill /IM java.exe /F
```

#### 一键停止所有 Python 进程（慎用）
```powershell
taskkill /IM python.exe /F
```

### 方式三：Docker 方式

```powershell
# 停止所有服务
docker-compose down

# 停止并删除数据卷（会清空数据库数据）
docker-compose down -v
```

---

## 三、数据存储位置

### 1. MySQL 数据库（主要数据存储）

| 数据类型 | 存储位置 |
|---------|---------|
| 用户信息 | `blue_sakura_blog.user` 表 |
| 博客文章 | `blue_sakura_blog.blog` 表 |
| 点赞记录 | `blue_sakura_blog.blog_like` 表 |

**本地开发时**：数据存储在你本地的 MySQL 服务中

**Docker 部署时**：数据存储在 Docker 卷 `mysql_data` 中

### 2. Redis 缓存（点赞数据缓存）

| 数据类型 | Redis Key 格式 |
|---------|---------------|
| 博客点赞用户集合 | `blog:like:{blogId}` |

**本地开发时**：需要本地安装并启动 Redis 服务

**Docker 部署时**：使用 Docker 容器中的 Redis

### 3. 前端静态文件

- 开发时：`e:\自主博客\frontend\src\` 目录
- 构建后：`e:\自主博客\frontend\dist\` 目录

### 4. 用户上传的图片

目前博客封面图片使用 URL 方式，需要用户输入图片链接。如需支持本地上传，后续可集成 OSS 或本地文件存储。

---

## 四、快速启动脚本

你可以创建以下脚本方便启动：

### start-backend.ps1（启动后端）
```powershell
$env:JAVA_HOME="D:\java21"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd e:\自主博客\backend
mvn spring-boot:run
```

### start-frontend.ps1（启动前端）
```powershell
cd e:\自主博客\frontend
npm run dev
```

### start-ai-service.ps1（启动AI服务）
```powershell
cd e:\自主博客\ai-service
python app.py
```

---

## 五、服务端口说明

| 服务 | 端口 | 访问地址 |
|------|------|---------|
| 前端 | 3000 | http://localhost:3000 |
| 后端 API | 8080 | http://localhost:8080/api |
| AI 服务 | 8000 | http://localhost:8000 |
| MySQL | 3306 | localhost:3306 |
| Redis | 6379 | localhost:6379 |

## 六、已完成功能

- [x] 用户中心系统
- [x] 博客管理功能
- [x] 嵌套评论系统
- [x] 文章反馈功能
- [x] 暗色模式
- [x] 毛玻璃导航栏
- [x] 文章热度地图
- [x] AI 辅助写作
- [x] AI 内容总结
- [x] RAG 知识库系统
- [x] 标签系统
- [x] Markdown 编辑器增强
- [x] 草稿箱功能
- [x] 响应式设计
- [x] 局域网访问支持

---

## 七、局域网访问配置

### 1. 前端配置（已完成）

`vite.config.js` 已配置 `host: '0.0.0.0'`，允许局域网访问。

### 2. 查看本机 IP 地址

```powershell
ipconfig
```

找到 `IPv4 地址`，通常是 `192.168.x.x` 格式。

### 3. 配置 Windows 防火墙

需要开放 3000（前端）、8080（后端）和 8000（AI服务）端口：

#### 方式一：通过 PowerShell（管理员权限）
```powershell
# 开放前端端口 3000
netsh advfirewall firewall add rule name="Blog Frontend" dir=in action=allow protocol=tcp localport=3000

# 开放后端端口 8080
netsh advfirewall firewall add rule name="Blog Backend" dir=in action=allow protocol=tcp localport=8080

# 开放AI服务端口 8000
netsh advfirewall firewall add rule name="Blog AI Service" dir=in action=allow protocol=tcp localport=8000
```

#### 方式二：通过 Windows 设置
1. 打开 `控制面板` → `Windows Defender 防火墙` → `高级设置`
2. 点击 `入站规则` → `新建规则`
3. 选择 `端口` → `TCP` → 输入 `3000, 8080, 8000`
4. 选择 `允许连接`
5. 应用到所有配置文件，命名为 `Blog Ports`

### 4. 启动服务

```powershell
# 启动后端
$env:JAVA_HOME="D:\java21"; cd e:\自主博客\backend; mvn spring-boot:run

# 启动前端（新终端）
cd e:\自主博客\frontend; npm run dev

# 启动AI服务（新终端）
cd e:\自主博客\ai-service; python app.py
```

### 5. 局域网访问

其他设备在浏览器中访问：
```
http://<你的IP地址>:3000

例如：http://192.168.1.100:3000
```

### 6. 注意事项

- 确保所有设备连接的是**同一个 WiFi/路由器**
- 如果还是无法访问，尝试暂时关闭防火墙测试
- 公司网络可能有额外限制，需要联系网络管理员
