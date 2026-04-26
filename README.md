# 个人交易复盘系统

个人短线交易复盘与行为纠错系统，覆盖交易记录、错误归因、单笔复盘、周统计、周复盘和纪律卡片。

核心闭环：

```text
交易记录 -> 错误标签 -> 单笔复盘 -> 周统计 -> 周复盘 -> 下周纪律卡片
```

## 技术栈

- 前端：Vue 3、Vite、Element Plus、Axios、Vue Router、ECharts
- 后端：Java 17、Spring Boot、MyBatis Plus、Maven、Lombok
- 数据库：MySQL 8

## 目录结构

```text
trade-review-system
├─ backend
├─ frontend
└─ database
```

## 初始化数据库

先确认 MySQL 服务已经启动，然后执行：

```powershell
mysql -u root -p < database\schema.sql
```

如果是已初始化过的数据库，阶段一新增的周复盘统计快照字段可执行：

```powershell
mysql -u root -p < database\phase1_weekly_review_snapshot.sql
```

## 后端启动

启动前先设置 MySQL 密码环境变量：

```powershell
$env:MYSQL_ROOT_PASSWORD="你的MySQL密码"
cd backend
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

## 前端启动

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

前端默认地址：

```text
http://localhost:5173
```
