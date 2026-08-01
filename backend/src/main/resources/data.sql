-- Database initialization script for PostgreSQL

-- Drop table if exists
DROP TABLE IF EXISTS "users";

-- Create users table
CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "username" VARCHAR(50) NOT NULL UNIQUE,
    "password" VARCHAR(100) NOT NULL, -- Plaintext as requested
    "email" VARCHAR(100),
    "role" VARCHAR(20) NOT NULL, -- USER or ADMIN
    "avatar" VARCHAR(255),
    "nickname" VARCHAR(50),
    "bio" TEXT,
    "learning_goals" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial data
INSERT INTO "users" ("username", "password", "email", "role", "avatar", "nickname", "bio", "learning_goals")
VALUES 
('admin', '123456', 'admin@example.com', 'ADMIN', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', '管理员', '系统管理员', '管理整个系统'),
('user', '123456', 'user@example.com', 'USER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user', '普通用户', '编程爱好者', '学习Java和Vue');

-- ========================================================================
-- 学习域数据模型（规格书第五章）：learning_paths / learning_behaviors / user_path_progress
-- ========================================================================

DROP TABLE IF EXISTS "learning_behaviors";
DROP TABLE IF EXISTS "user_path_progress";
DROP TABLE IF EXISTS "learning_paths";

-- 学习路径主数据
CREATE TABLE "learning_paths" (
    "id" SERIAL PRIMARY KEY,
    "path_code" VARCHAR(50) NOT NULL UNIQUE,
    "title" VARCHAR(100) NOT NULL,
    "summary" TEXT,
    "difficulty" INTEGER,           -- 1-5
    "estimated_minutes" INTEGER,
    "active" BOOLEAN NOT NULL DEFAULT TRUE
);

-- 学习行为流水（不可变）
CREATE TABLE "learning_behaviors" (
    "id" SERIAL PRIMARY KEY,
    "user_id" BIGINT NOT NULL,
    "path_id" BIGINT NOT NULL,
    "study_duration" INTEGER NOT NULL,   -- 分钟，正整数
    "mastery_score" INTEGER NOT NULL,    -- 0-100
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户-路径聚合进度
CREATE TABLE "user_path_progress" (
    "id" SERIAL PRIMARY KEY,
    "user_id" BIGINT NOT NULL,
    "path_id" BIGINT NOT NULL,
    "mastery_score" INTEGER NOT NULL,        -- 0-100
    "progress" INTEGER NOT NULL,             -- 0-100，>=100 完成
    "total_study_minutes" INTEGER NOT NULL,
    "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "uk_user_path" UNIQUE ("user_id", "path_id")
);

-- 种子：12 条活跃学习路径（规格书附录预置 · 路径目录 / 第五章「≥12 条活跃」）
INSERT INTO "learning_paths" ("path_code", "title", "summary", "difficulty", "estimated_minutes", "active")
VALUES
('PATH-001', '实战课单元 1',  '覆盖能力项 #1；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  2, 31, TRUE),
('PATH-002', '实战课单元 2',  '覆盖能力项 #2；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  3, 32, TRUE),
('PATH-003', '实战课单元 3',  '覆盖能力项 #3；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  4, 33, TRUE),
('PATH-004', '实战课单元 4',  '覆盖能力项 #4；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  5, 34, TRUE),
('PATH-005', '实战课单元 5',  '覆盖能力项 #5；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  1, 35, TRUE),
('PATH-006', '实战课单元 6',  '覆盖能力项 #6；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  2, 36, TRUE),
('PATH-007', '实战课单元 7',  '覆盖能力项 #7；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  3, 37, TRUE),
('PATH-008', '实战课单元 8',  '覆盖能力项 #8；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  4, 38, TRUE),
('PATH-009', '实战课单元 9',  '覆盖能力项 #9；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。',  5, 39, TRUE),
('PATH-010', '实战课单元 10', '覆盖能力项 #10；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。', 1, 40, TRUE),
('PATH-011', '实战课单元 11', '覆盖能力项 #11；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。', 2, 41, TRUE),
('PATH-012', '实战课单元 12', '覆盖能力项 #12；概念辨析、练习与检测。自适应学习、行为分析、分钟制、薄弱优先、统一包络。', 3, 42, TRUE);
