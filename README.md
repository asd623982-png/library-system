# Library Cloud - 智慧图书馆管理系统

## 1. 项目简介
本项目是一个基于 Web 的轻量级高校图书馆管理系统，实现了图书入库、借阅、归还、查询、用户管理以及可视化的数据大屏统计功能。项目采用前后端分离架构开发，利用 AI 辅助完成了大部分基础代码与文档编写。

## 2. 技术栈与依赖

### 后端 (Backend)
* **核心框架**: Spring Boot 3.x
* **Web支持**: spring-boot-starter-web
* **ORM框架**: Spring Data JPA
* **数据库驱动**: MySQL Driver
* **工具库**: Lombok
* **开发环境**: JDK 17+, Maven 3.6+

### 前端 (Frontend)
* **核心框架**: Vue 3 (Composition API)
* **构建工具**: Vite
* **UI组件库**: Element Plus
* **HTTP请求**: Axios
* **图表库**: ECharts 5.x
* **开发环境**: Node.js 16+

## 3. 快速开始 (运行说明)

### 3.1 数据库准备
1.  确保本地安装了 MySQL 8.0。
2.  创建一个新的数据库命名为 `library_db`。
3.  运行 `sql/schema.sql` 脚本，这将创建所有表结构并注入初始管理员账号。

### 3.2 后端启动
1.  进入 `backend` 目录，使用 IntelliJ IDEA 打开项目。
2.  打开 `src/main/resources/application.properties`。
3.  修改 `spring.datasource.username` 和 `password` 为你本地的数据库账号密码。
4.  运行 `SystemApplication.java` 类。
5.  控制台看到 `Started SystemApplication` 即启动成功 (默认端口 8080)。

### 3.3 前端启动
1.  打开终端，进入 `frontend` 目录。
2.  运行 `npm install` 安装依赖（首次运行需要）。
3.  运行 `npm run dev` 启动开发服务器。
4.  终端将显示访问地址，通常为 `http://localhost:5173`。

## 4. 账号说明
* **管理员账号**: `admin`
* **默认密码**: `123456`

