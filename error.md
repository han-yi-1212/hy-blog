# 博客系统 V8.0 升级问题记录

## 问题列表

### 1. 博客文章不显示
**错误描述**: 首页博客列表为空，显示"暂无博客文章"

**错误原因**: 
- 数据库表 `blog` 缺少 `status` 列
- 后端查询语句 `WHERE (status = ? OR status IS NULL)` 执行失败

**修复方案**:
```sql
-- 添加status字段
ALTER TABLE blog ADD COLUMN status VARCHAR(20) DEFAULT 'published';
UPDATE blog SET status = 'published' WHERE status IS NULL;
```

**修改文件**: `e:\自主博客\backend\src\main\resources\db\upgrade_v8.sql`

---

### 2. 写文章页面 - 文章内容输入框不显示
**错误描述**: 写文章页面没有显示Markdown编辑器，无法输入文章内容

**错误原因**: 
- Vditor编辑器初始化时DOM元素可能未准备好
- 缺少空值检查

**修复方案**:
```javascript
// 添加nextTick确保DOM加载完成
onMounted(() => {
  nextTick(() => {
    initEditor()
  })
})

// 添加初始化状态检查
const initEditor = () => {
  if (!editorRef.value || isInitialized) return
  // ...
}
```

**修改文件**: `e:\自主博客\frontend\src\components\MarkdownEditor.vue`

---

### 3. 写文章页面 - AI生成按钮无反应
**错误描述**: 点击"AI生成"按钮没有任何反应

**错误原因**: 
- AI服务（Python FastAPI）没有启动
- 端口8000被占用

**修复方案**:
```bash
# 终止占用端口的进程
taskkill /F /PID 15720

# 启动AI服务
cd e:\自主博客\ai-service
python app.py
```

---

### 4. 写文章页面 - 首页/取消按钮无法返回
**错误描述**: 点击"首页"链接或"取消"按钮无法返回首页

**错误原因**: 
- 使用 `$router.back()` 在无历史记录时失效
- router-link组件导航失效

**修复方案**:
```vue
<!-- 首页链接 -->
<a href="/" class="nav-link" @click.prevent="router.push('/')">首页</a>

<!-- 取消按钮 -->
<button type="button" class="btn-secondary" @click="router.push('/')">
  取消
</button>
```

**修改文件**: `e:\自主博客\frontend\src\views\Write.vue`

---

### 5. 博客详情页 - Markdown格式显示为纯文本
**错误描述**: 发布的Markdown格式文章在详情页显示为原始Markdown文本（如 `## 标题` 没有渲染为标题）

**错误原因**: 
- `formatContent` 函数只是简单的正则替换，无法正确渲染Markdown
- 缺少Markdown渲染库

**修复方案**:
```bash
# 安装marked库
npm install marked
```

```javascript
// 导入marked
import { marked } from 'marked'

// 修改formatContent函数
const formatContent = (content) => {
  if (!content) return ''
  return marked(content)
}
```

**添加Markdown样式**:
```scss
.blog-content {
  :deep(h1) { font-size: 2em; margin: 0.67em 0; font-weight: bold; }
  :deep(h2) { font-size: 1.5em; margin: 0.75em 0; font-weight: bold; }
  :deep(h3) { font-size: 1.17em; margin: 0.83em 0; font-weight: bold; }
  :deep(p) { margin: 1em 0; }
  :deep(ul), :deep(ol) { margin: 1em 0; padding-left: 2em; }
  :deep(code) { background: var(--bg-color); padding: 2px 8px; border-radius: 4px; }
  :deep(pre) { background: var(--bg-color); padding: 16px; border-radius: 8px; }
  :deep(blockquote) { border-left: 4px solid var(--primary-color); }
  // ... 更多样式
}
```

**修改文件**: 
- `e:\自主博客\frontend\src\views\BlogDetail.vue`

---

## 数据库升级脚本

**文件**: `e:\自主博客\backend\src\main\resources\db\upgrade_v8.sql`

```sql
-- V8.0 升级脚本
-- 添加博客状态字段
-- 创建标签系统

USE blue_sakura_blog;

-- 添加博客状态字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'blog' AND column_name = 'status' AND table_schema = 'blue_sakura_blog');
SET @sql := IF(@exist = 0, 'ALTER TABLE blog ADD COLUMN status VARCHAR(20) DEFAULT \'published\' COMMENT \'article status\'', 'SELECT \'status column already exists\'');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据
UPDATE blog SET status = 'published' WHERE status IS NULL;

-- 创建标签表（如果不存在）
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建博客标签关联表（如果不存在）
CREATE TABLE IF NOT EXISTS blog_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    blog_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    UNIQUE KEY uk_blog_tag (blog_id, tag_id),
    FOREIGN KEY (blog_id) REFERENCES blog(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 启动命令

### 启动后端
```bash
cd e:\自主博客\backend
D:\java21\bin\java.exe -jar target\blog-0.0.1-SNAPSHOT.jar
```

### 启动前端
```bash
cd e:\自主博客\frontend
npm run dev
```

### 启动AI服务
```bash
cd e:\自主博客\ai-service
python app.py
```

---

## 修改文件汇总

1. `e:\自主博客\backend\src\main\resources\db\upgrade_v8.sql` - 数据库升级脚本
2. `e:\自主博客\frontend\src\components\MarkdownEditor.vue` - Markdown编辑器组件
3. `e:\自主博客\frontend\src\views\Write.vue` - 写文章页面
4. `e:\自主博客\frontend\src\views\BlogDetail.vue` - 博客详情页面
