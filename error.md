

---

# 博客系统 V9.0 后续修复记录

## 问题列表

### 1. 同步知识库文章数量不断增加
**错误描述**: 每次点击"同步知识库"，文章数量都会增加，没有去重机制

**错误原因**: 
- `vector_store.py` 的 `add_documents_batch()` 方法没有检查文档是否已存在
- 每次同步都是直接追加，而不是更新

**修复方案**:
```python
def add_documents_batch(docs: List[Dict], embeddings: np.ndarray):
    global id_counter
    
    # 先去重，只保留每个ID的最新版本
    doc_map = {}
    for doc in docs:
        doc_map[doc['id']] = doc
    
    # 移除现有文档中将要更新的
    new_documents = []
    for doc in documents:
        if doc['id'] not in doc_map:
            new_documents.append(doc)
    
    # 添加新文档
    for doc_id, doc in doc_map.items():
        new_documents.append(doc)
    
    # 更新全局变量
    documents[:] = new_documents
    id_counter = len(documents)
    
    # 重建索引
    _rebuild_index()
    save()
```

**修改文件**: 
- `e:\自主博客\ai-service\rag\vector_store.py`

---

### 2. 已删除博客仍显示在知识库中
**错误描述**: 删除博客后同步知识库，已删除的博客仍然存在于知识库中

**错误原因**: 
- 同步逻辑只会添加新文章，不会删除知识库中已不存在的文章

**修复方案**:
1. 在AI服务中添加清空知识库接口：
```python
@app.post("/api/ai/rag/clear")
async def clear_knowledge():
    try:
        clear_knowledge_base()
        return {"success": True, "message": "知识库已清空"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"清空失败: {str(e)}")
```

2. 修改同步逻辑，先清空再添加：
```java
@Override
public void syncAllBlogs() {
    // 只同步已发布的博客，排除草稿
    QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("status", "published").or().isNull("status");
    List<Blog> blogs = blogMapper.selectList(queryWrapper);
    
    try {
        // 第一步：清空知识库
        String clearUrl = aiServiceUrl + "/api/ai/rag/clear";
        restTemplate.postForEntity(clearUrl, null, Map.class);
        
        // 第二步：如果没有博客，直接返回
        if (blogs.isEmpty()) {
            return;
        }
        
        // 第三步：批量添加博客到知识库
        // ...
    } catch (Exception e) {
        System.err.println("同步博客到知识库失败: " + e.getMessage());
    }
}
```

**修改文件**: 
- `e:\自主博客\ai-service\app.py`
- `e:\自主博客\ai-service\rag\pipeline.py`
- `e:\自主博客\ai-service\rag\__init__.py`
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\service\impl\RagServiceImpl.java`

---

### 3. 后端重启后显示"没有权限访问"
**错误描述**: 后端重启后，AI功能显示"没有权限访问"

**错误原因**: 
- 后端重启后JWT密钥可能发生变化
- 前端带着失效的token发送请求，JWT过滤器验证失败返回403错误

**修复方案**:
```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = getTokenFromRequest(request);
    
    if (StringUtils.hasText(token)) {
        try {
            if (jwtUtil.validateToken(token)) {
                // ... 设置认证信息
            }
        } catch (Exception e) {
            // Token验证失败，但不阻止请求继续（对于permitAll的路径）
            System.out.println("Token验证失败: " + e.getMessage());
        }
    }
    
    filterChain.doFilter(request, response);
}
```

**修改文件**: 
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\security\JwtAuthenticationFilter.java`

---

### 4. 无法添加文章标签
**错误描述**: 写文章时无法添加标签，点击"+ 添加标签"没有反应

**错误原因**: 
- 后端 `TagController.create()` 方法使用了 `@RequestParam` 接收参数
- 但前端发送的是 JSON 对象 `{ name }`
- 参数绑定不匹配导致请求失败

**修复方案**:
1. 创建DTO类：
```java
@Data
public class TagCreateRequest {
    private String name;
}
```

2. 修改Controller：
```java
@PostMapping
public Result create(@RequestBody TagCreateRequest request) {
    Tag tag = tagService.create(request.getName());
    return Result.success(tag);
}
```

**修改文件**: 
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\dto\TagCreateRequest.java` (新建)
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\controller\TagController.java`

---

### 5. 博客主页不显示标签
**错误描述**: 博客主页的文章卡片没有显示标签

**错误原因**: 
- 前端没有添加标签显示逻辑
- 后端返回的数据包含tags字段，但前端未使用

**修复方案**:
```vue
<div class="blog-tags" v-if="blog.tags && blog.tags.length > 0">
  <span 
    v-for="tag in blog.tags.slice(0, 3)" 
    :key="tag.id" 
    class="tag-item"
    @click.stop="goToTag(tag.name)"
  >
    {{ tag.name }}
  </span>
  <span v-if="blog.tags.length > 3" class="tag-more">+{{ blog.tags.length - 3 }}</span>
</div>
```

```javascript
const goToTag = (tagName) => {
  router.push(`/?tag=${encodeURIComponent(tagName)}`)
}
```

```scss
.blog-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
  
  .tag-item {
    display: inline-block;
    padding: 2px 10px;
    background: rgba(255, 143, 177, 0.15);
    border: 1px solid rgba(255, 143, 177, 0.3);
    border-radius: 12px;
    font-size: 12px;
    color: var(--primary-color);
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      background: rgba(255, 143, 177, 0.3);
      transform: translateY(-1px);
    }
  }
}
```

**修改文件**: 
- `e:\自主博客\frontend\src\views\Home.vue`

---

### 6. 随机阅读数量显示不正确
**错误描述**: 随机阅读显示5篇文章，用户希望改为4篇

**修复方案**:
```javascript
const fetchRandomPosts = async () => {
  loading.value = true
  try {
    const res = await blogApi.getRandom(4) // 从5改为4
    posts.value = res.data || []
  } catch (error) {
    console.error('获取随机博客失败:', error)
  } finally {
    loading.value = false
  }
}
```

**修改文件**: 
- `e:\自主博客\frontend\src\components\RandomPosts.vue`

---

### 7. 热力地图改为排行榜
**错误描述**: 用户希望将热力地图改为按点赞和浏览量排序的排行榜

**修复方案**:
- 重构 Heatmap.vue 为 Ranking.vue
- 按热度分数（浏览量 + 点赞数×2）排序
- 显示前10名，前3名显示金银铜奖牌样式

**修改文件**: 
- `e:\自主博客\frontend\src\components\Ranking.vue` (新建)
- `e:\自主博客\frontend\src\views\Home.vue` (修改引用)
- 删除 `e:\自主博客\frontend\src\components\Heatmap.vue`

---

### 8. 用户名修改后不同步到博客文章
**错误描述**: 用户修改昵称后，博客文章仍显示旧的用户名

**修复方案**:
1. 后端修改：在更新用户时同步更新博客表中的用户名
```java
@Override
public void updateUser(User user) {
    User oldUser = userMapper.selectById(user.getId());
    if (oldUser == null) {
        throw new RuntimeException("用户不存在");
    }
    
    userMapper.updateById(user);
    
    // 如果用户名发生变化，同步更新博客表中的用户名
    if (user.getUsername() != null && !user.getUsername().equals(oldUser.getUsername())) {
        LambdaUpdateWrapper<Blog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Blog::getUserId, user.getId())
                    .set(Blog::getUsername, user.getUsername());
        blogMapper.update(null, updateWrapper);
    }
}
```

2. 修改显示逻辑：博客显示昵称而不是用户名
```java
// BlogVO添加nickname字段
private String nickname;

// convertToVO中设置昵称
if (user != null) {
    vo.setUsername(user.getUsername());
    vo.setNickname(user.getNickname());
}
```

**修改文件**: 
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\service\impl\UserServiceImpl.java`
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\dto\BlogVO.java`
- `e:\自主博客\backend\src\main\java\com\bluesakura\blog\service\impl\BlogServiceImpl.java`
- `e:\自主博客\frontend\src\views\BlogDetail.vue`
- `e:\自主博客\frontend\src\views\Home.vue`

---

### 9. 个人中心评论改为动态
**错误描述**: 个人中心的评论功能无法正常使用，用户希望改为显示博客发布动态

**修复方案**:
- 将"评论"标签改为"动态"
- 动态显示用户发布的博客记录
- 按发布时间倒序排列

**修改文件**: 
- `e:\自主博客\frontend\src\views\UserProfile.vue`

---

### 10. 写文章页面显示两个"文章标签"
**错误描述**: 写文章页面出现重复的"文章标签"标题

**修复方案**:
- 删除 TagSelector.vue 组件中的 label
- 保留 Write.vue 页面中的 label

**修改文件**: 
- `e:\自主博客\frontend\src\components\TagSelector.vue`

---

### 11. RAG相似度阈值设置不当
**错误描述**: 询问"小包子是谁"时，不相关的文章也显示高相似度(1.00)

**修复方案**:
- 修改相似度计算方法，使用余弦相似度估算
- 添加阈值过滤，默认阈值 0.15
- 按相似度降序排序

```python
def retrieve(query: str, k: int = 3, threshold: float = 0.15) -> List[Dict]:
    # ...
    for doc in results:
        l2_distance = doc.get('score', float('inf'))
        # 估算余弦相似度
        cosine_sim = max(0.0, 1.0 - (l2_distance ** 2) / 2.0)
        
        if cosine_sim >= threshold:
            doc['similarity'] = round(cosine_sim, 2)
            filtered_results.append(doc)
    
    # 按相似度降序排序
    filtered_results.sort(key=lambda x: x['similarity'], reverse=True)
    return filtered_results
```

**修改文件**: 
- `e:\自主博客\ai-service\rag\retriever.py`
- `e:\自主博客\ai-service\rag\pipeline.py`

---

## 修改文件汇总 (后续修复)

1. `e:\自主博客\ai-service\rag\vector_store.py` - 向量存储去重逻辑
2. `e:\自主博客\ai-service\app.py` - 添加清空知识库接口
3. `e:\自主博客\ai-service\rag\pipeline.py` - 添加clear_knowledge_base函数和相似度计算
4. `e:\自主博客\ai-service\rag\__init__.py` - 导出clear_knowledge_base
5. `e:\自主博客\ai-service\rag\retriever.py` - 相似度阈值过滤
6. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\service\impl\RagServiceImpl.java` - 修改同步逻辑
7. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\security\JwtAuthenticationFilter.java` - 修复token验证
8. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\dto\TagCreateRequest.java` - 标签创建DTO
9. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\controller\TagController.java` - 修复标签创建接口
10. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\service\impl\UserServiceImpl.java` - 用户名同步
11. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\dto\BlogVO.java` - 添加nickname字段
12. `e:\自主博客\backend\src\main\java\com\bluesakura\blog\service\impl\BlogServiceImpl.java` - 设置nickname
13. `e:\自主博客\frontend\src\views\Home.vue` - 添加标签显示和昵称显示
14. `e:\自主博客\frontend\src\views\BlogDetail.vue` - 显示昵称
15. `e:\自主博客\frontend\src\views\UserProfile.vue` - 评论改为动态
16. `e:\自主博客\frontend\src\components\RandomPosts.vue` - 修改数量为4
17. `e:\自主博客\frontend\src\components\Ranking.vue` - 新建排行榜组件
18. `e:\自主博客\frontend\src\components\Heatmap.vue` - 删除
19. `e:\自主博客\frontend\src\components\TagSelector.vue` - 删除重复label
