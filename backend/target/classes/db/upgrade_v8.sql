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
