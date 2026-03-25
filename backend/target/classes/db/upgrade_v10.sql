-- V10.0 升级脚本
-- 为user表添加role字段

USE blue_sakura_blog;

-- 添加role字段到user表（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'user' AND column_name = 'role' AND table_schema = 'blue_sakura_blog');
SET @sql := IF(@exist = 0, 'ALTER TABLE user ADD COLUMN role VARCHAR(20) DEFAULT \'user\' COMMENT \'user role\'', 'SELECT \'role column already exists\'');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据
UPDATE user SET role = 'user' WHERE role IS NULL;

-- 将第一个用户设置为管理员（可选）
UPDATE user SET role = 'admin' WHERE id = 1;