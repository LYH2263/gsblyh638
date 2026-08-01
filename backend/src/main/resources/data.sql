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
