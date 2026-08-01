-- Database initialization script for PostgreSQL

-- Drop table if exists
DROP TABLE IF EXISTS "learning_behaviors" CASCADE;
DROP TABLE IF EXISTS "user_path_progress" CASCADE;
DROP TABLE IF EXISTS "learning_paths" CASCADE;
DROP TABLE IF EXISTS "users" CASCADE;

-- Create users table
CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "username" VARCHAR(50) NOT NULL UNIQUE,
    "password" VARCHAR(100) NOT NULL,
    "email" VARCHAR(100),
    "role" VARCHAR(20) NOT NULL,
    "avatar" VARCHAR(255),
    "nickname" VARCHAR(50),
    "bio" TEXT,
    "learning_goals" TEXT,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create learning_paths table
CREATE TABLE "learning_paths" (
    "id" SERIAL PRIMARY KEY,
    "path_code" VARCHAR(50) NOT NULL UNIQUE,
    "title" VARCHAR(200) NOT NULL,
    "summary" VARCHAR(500),
    "difficulty" INTEGER NOT NULL,
    "estimated_minutes" INTEGER NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create learning_behaviors table
CREATE TABLE "learning_behaviors" (
    "id" SERIAL PRIMARY KEY,
    "user_id" BIGINT NOT NULL,
    "path_id" BIGINT NOT NULL,
    "study_duration" INTEGER NOT NULL,
    "mastery_score" INTEGER NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_path_progress table
CREATE TABLE "user_path_progress" (
    "id" SERIAL PRIMARY KEY,
    "user_id" BIGINT NOT NULL,
    "path_id" BIGINT NOT NULL,
    "mastery_score" INTEGER NOT NULL,
    "progress" INTEGER NOT NULL,
    "total_study_minutes" INTEGER NOT NULL,
    "updated_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "uq_user_path" UNIQUE ("user_id", "path_id")
);

-- Insert initial users
INSERT INTO "users" ("username", "password", "email", "role", "avatar", "nickname", "bio", "learning_goals")
VALUES
('admin', '123456', 'admin@example.com', 'ADMIN', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', '管理员', '系统管理员', '管理整个系统'),
('user', '123456', 'user@example.com', 'USER', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user', '普通用户', '编程爱好者', '学习Java和Vue');

-- Insert seed learning paths (>= 12 active paths)
INSERT INTO "learning_paths" ("path_code", "title", "summary", "difficulty", "estimated_minutes", "active")
VALUES
('PATH-001', '实战课单元 1', '覆盖能力项 #1；预估 31 分钟；难度 2/5。包含概念辨析、练习与检测。', 2, 31, TRUE),
('PATH-002', '实战课单元 2', '覆盖能力项 #2；预估 32 分钟；难度 3/5。包含概念辨析、练习与检测。', 3, 32, TRUE),
('PATH-003', '实战课单元 3', '覆盖能力项 #3；预估 33 分钟；难度 4/5。包含概念辨析、练习与检测。', 4, 33, TRUE),
('PATH-004', '实战课单元 4', '覆盖能力项 #4；预估 34 分钟；难度 5/5。包含概念辨析、练习与检测。', 5, 34, TRUE),
('PATH-005', '实战课单元 5', '覆盖能力项 #5；预估 35 分钟；难度 1/5。包含概念辨析、练习与检测。', 1, 35, TRUE),
('PATH-006', '实战课单元 6', '覆盖能力项 #6；预估 36 分钟；难度 2/5。包含概念辨析、练习与检测。', 2, 36, TRUE),
('PATH-007', '实战课单元 7', '覆盖能力项 #7；预估 37 分钟；难度 3/5。包含概念辨析、练习与检测。', 3, 37, TRUE),
('PATH-008', '实战课单元 8', '覆盖能力项 #8；预估 38 分钟；难度 4/5。包含概念辨析、练习与检测。', 4, 38, TRUE),
('PATH-009', '实战课单元 9', '覆盖能力项 #9；预估 39 分钟；难度 5/5。包含概念辨析、练习与检测。', 5, 39, TRUE),
('PATH-010', '实战课单元 10', '覆盖能力项 #10；预估 40 分钟；难度 1/5。包含概念辨析、练习与检测。', 1, 40, TRUE),
('PATH-011', '实战课单元 11', '覆盖能力项 #11；预估 41 分钟；难度 2/5。包含概念辨析、练习与检测。', 2, 41, TRUE),
('PATH-012', '实战课单元 12', '覆盖能力项 #12；预估 42 分钟；难度 3/5。包含概念辨析、练习与检测。', 3, 42, TRUE),
('PATH-013', '实战课单元 13', '覆盖能力项 #13；预估 43 分钟；难度 4/5。包含概念辨析、练习与检测。', 4, 43, TRUE),
('PATH-014', '实战课单元 14', '覆盖能力项 #14；预估 44 分钟；难度 5/5。包含概念辨析、练习与检测。', 5, 44, TRUE),
('PATH-015', '实战课单元 15', '覆盖能力项 #15；预估 45 分钟；难度 1/5。包含概念辨析、练习与检测。', 1, 45, TRUE);
