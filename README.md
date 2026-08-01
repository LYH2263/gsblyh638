# 自适应编程学习路径推荐系统

## 🛠 技术栈
- **Frontend**: Vue 3 + Vite + WindiCSS
- **Backend**: Java 17 + Spring Boot 3 + Spring Data JPA
- **Database**: PostgreSQL 15

## 📖 项目说明
本项目名为“基于用户行为分析的自适应编程学习路径推荐系统”。
**注意**：本项目并不强制依赖 Docker 进行部署。为了方便验收人员快速搭建测试环境，我们提供了 Docker Compose 配置脚本。在实际生产或本地开发环境中，推荐使用标准的本地部署方式。

---

## 💻 本地开发指南 (Local Development)

如果您希望在本地进行开发调试，请按照以下步骤操作：

### 1. 环境准备
确保您的电脑已安装：
- **Java JDK 17**
- **Node.js (v18+)**
- **PostgreSQL Database**

### 2. 数据库配置
1. 本地启动 PostgreSQL 服务。
2. 创建数据库 `adaptive_learning`。
3. 执行 `backend/src/main/resources/init.sql` 中的 SQL 脚本初始化表结构和数据。
4. 修改 `backend/src/main/resources/application.properties`，将数据库连接信息改为您本地的配置：
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/adaptive_learning
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```

### 3. 后端启动 (Backend)
```bash
cd backend
# 使用 Maven Wrapper 启动
./mvnw spring-boot:run
```
服务默认运行在 `http://localhost:8080`。

### 4. 前端启动 (Frontend)
```bash
cd frontend
# 安装依赖
npm install
# 启动开发服务器
npm run dev
```
访问 `http://localhost:5173`。
> **注意**: 在本地开发模式下，请确保 `vite.config.js` 中的代理配置指向了本地后端端口 (8080)。

---

## 🐳 快速测试指南 (Docker Testing)

为了方便快速预览和验收，本项目提供了 Docker 一键启动方案。**此方式仅建议用于快速演示和测试。**

### 启动服务
在项目根目录下运行：
```bash
docker compose up --build -d
```
等待容器启动完成后，访问 `http://localhost:5173`。

### 服务信息
- **前端页面**: http://localhost:5173
- **后端接口**: http://localhost:8080
- **数据库**: 端口 5432, 用户 `postgres`, 密码 `123456`

---

## 🧪 测试账号
系统初始化时会自动创建以下账号：

| 角色 | 用户名 | 密码 | 权限 |
| :--- | :--- | :--- | :--- |
| **管理员** | `admin` | `123456` | ADMIN |
| **普通用户** | `user` | `123456` | USER |

## 📂 项目结构
- `/backend`: Spring Boot 后端工程
- `/frontend`: Vue 3 前端工程
- `docker-compose.yml`: 快速测试用的容器编排文件
- `SELF_TEST.md`: 自测说明文档
