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

-- Learning paths table (IF NOT EXISTS: 与 Hibernate ddl-auto=update 兼容；
-- 同时保证本脚本作为 docker-entrypoint-initdb.d 裸 PG 初始化脚本时表已存在)
CREATE TABLE IF NOT EXISTS learning_paths (
    id BIGSERIAL PRIMARY KEY,
    path_code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255),
    summary TEXT,
    difficulty INTEGER,
    estimated_minutes INTEGER,
    active BOOLEAN
);

-- Seed learning paths (>= 12 active, idempotent; reference: 规格书附录预置·路径目录)
INSERT INTO learning_paths (path_code, title, summary, difficulty, estimated_minutes, active)
SELECT v.path_code, v.title, v.summary, v.difficulty, v.estimated_minutes, v.active
FROM (VALUES
    ('PATH-001', '实战课单元 1',  '覆盖能力项 #1；预估 31 分钟；难度 2/5。包含概念辨析、练习与检测。', 2, 31, true),
    ('PATH-002', '实战课单元 2',  '覆盖能力项 #2；预估 32 分钟；难度 3/5。包含概念辨析、练习与检测。', 3, 32, true),
    ('PATH-003', '实战课单元 3',  '覆盖能力项 #3；预估 33 分钟；难度 4/5。包含概念辨析、练习与检测。', 4, 33, true),
    ('PATH-004', '实战课单元 4',  '覆盖能力项 #4；预估 34 分钟；难度 5/5。包含概念辨析、练习与检测。', 5, 34, true),
    ('PATH-005', '实战课单元 5',  '覆盖能力项 #5；预估 35 分钟；难度 1/5。包含概念辨析、练习与检测。', 1, 35, true),
    ('PATH-006', '实战课单元 6',  '覆盖能力项 #6；预估 36 分钟；难度 2/5。包含概念辨析、练习与检测。', 2, 36, true),
    ('PATH-007', '实战课单元 7',  '覆盖能力项 #7；预估 37 分钟；难度 3/5。包含概念辨析、练习与检测。', 3, 37, true),
    ('PATH-008', '实战课单元 8',  '覆盖能力项 #8；预估 38 分钟；难度 4/5。包含概念辨析、练习与检测。', 4, 38, true),
    ('PATH-009', '实战课单元 9',  '覆盖能力项 #9；预估 39 分钟；难度 5/5。包含概念辨析、练习与检测。', 5, 39, true),
    ('PATH-010', '实战课单元 10', '覆盖能力项 #10；预估 40 分钟；难度 1/5。包含概念辨析、练习与检测。', 1, 40, true),
    ('PATH-011', '实战课单元 11', '覆盖能力项 #11；预估 41 分钟；难度 2/5。包含概念辨析、练习与检测。', 2, 41, true),
    ('PATH-012', '实战课单元 12', '覆盖能力项 #12；预估 42 分钟；难度 3/5。包含概念辨析、练习与检测。', 3, 42, true)
) AS v(path_code, title, summary, difficulty, estimated_minutes, active)
WHERE NOT EXISTS (SELECT 1 FROM learning_paths);
