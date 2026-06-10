# 云博物馆 (Cloud Museum)

华为杯人工智能比赛 - 研究生组项目

## 项目简介

云博物馆是一个基于 Web 的博物馆数字化平台，包含用户端和管理端。用户可以浏览藏品、在线预约参观；管理员可以管理藏品信息、审核预约。

## 技术栈

### 后端
- Spring Boot 3.2.5
- Spring Data JPA
- MySQL 8.0
- Redis (缓存 + 验证码存储)
- JWT 认证
- Maven

### 管理端 (Admin Web)
- Vue 3 (Composition API)
- Element Plus
- Vite

### 用户端 (User H5)
- Vue 3
- Vant 4 (移动端组件库)
- Vite

## 项目结构

```
cloud-museum/
├── server/                 # Spring Boot 后端
│   ├── src/main/java/com/cloudmuseum/
│   │   ├── config/         # 配置类 (Redis, WebMvc)
│   │   ├── controller/     # 控制器
│   │   ├── dto/            # 数据传输对象
│   │   ├── entity/         # 实体类
│   │   ├── interceptor/    # 拦截器 (用户/管理员认证)
│   │   ├── repository/     # 数据访问层
│   │   ├── service/        # 业务逻辑层
│   │   └── util/           # 工具类 (JWT)
│   └── src/main/resources/application.yml
├── admin-web/              # 管理端前端
│   └── src/
│       ├── api/            # API 接口封装
│       ├── router/         # 路由配置
│       ├── layouts/        # 布局组件
│       └── views/          # 页面组件
├── user-h5/                # 用户端 H5
│   └── src/
│       ├── api/            # API 接口封装
│       ├── router/         # 路由配置
│       ├── components/     # 公共组件
│       └── views/          # 页面组件
└── sql/
    └── init.sql            # 数据库初始化脚本
```

## 快速开始

### 1. 环境准备

- JDK 17+
- Node.js 18+
- MySQL 8.0
- Redis
- Maven

### 2. 数据库初始化

```sql
-- 执行初始化脚本
source sql/init.sql;
```

### 3. 后端启动

```bash
cd server
# 确保 MySQL 和 Redis 已启动

# 修改 application.yml 中的数据库连接和 Redis 配置
# spring.datasource.username/password

# 启动
./mvnw spring-boot:run
# 或
mvn spring-boot:run
```

后端启动后访问: http://localhost:8080/api

### 4. 管理端启动

```bash
cd admin-web
npm install
npm run dev
```

管理端访问: http://localhost:3000
默认账号: admin / admin123

### 5. 用户端启动

```bash
cd user-h5
npm install
npm run dev
```

用户端访问: http://localhost:3001 (手机浏览器或开发者工具移动端模式)

## API 接口

### 用户端接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/send-code | 发送验证码 |
| POST | /api/user/login | 手机号登录 |
| GET  | /api/user/info | 获取用户信息 |
| GET  | /api/collection/list | 藏品列表(分页) |
| GET  | /api/collection/detail/{id} | 藏品详情 |
| GET  | /api/collection/featured | 推荐藏品 |
| GET  | /api/category/list | 分类列表 |
| POST | /api/reservation/create | 创建预约 |
| GET  | /api/reservation/my | 我的预约 |
| POST | /api/reservation/cancel/{id} | 取消预约 |

### 管理端接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/admin/login | 管理员登录 |
| GET  | /api/collection/admin/page | 藏品分页(管理) |
| POST | /api/collection/admin | 新增藏品 |
| PUT  | /api/collection/admin | 修改藏品 |
| DELETE | /api/collection/admin/{id} | 删除藏品 |
| POST | /api/category/admin | 新增分类 |
| PUT  | /api/category/admin | 修改分类 |
| DELETE | /api/category/admin/{id} | 删除分类 |
| GET  | /api/reservation/admin/list | 预约列表 |
| POST | /api/reservation/admin/approve/{id} | 通过预约 |
| POST | /api/reservation/admin/reject/{id} | 拒绝预约 |
| GET  | /api/admin/dashboard/stats | 控制台统计 |

## 功能说明

### 用户端 H5
- 首页: banner、分类导航、推荐藏品、预约入口
- 藏品浏览: 分类筛选、关键词搜索、下拉加载更多
- 藏品详情: 图片、基本信息、描述、文化意义
- 预约参观: 选择日期时段、填写信息、提交预约
- 我的: 个人中心、预约记录查看与管理

### 管理端 Web
- 控制台: 数据统计概览
- 藏品管理: 列表查询、新增、编辑、删除
- 分类管理: 列表展示、新增、编辑、删除
- 预约管理: 状态筛选、审核通过/拒绝、标记完成

## 后续规划

- [ ] 大模型藏品讲解功能 (接入 AI)
- [ ] 藏品收藏功能
- [ ] 在线客服
- [ ] 博物馆商品展示
- [ ] 用户评论互动
- [ ] 数据可视化统计
