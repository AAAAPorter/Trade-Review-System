# 个人交易复盘系统项目说明

这份文档用于把当前项目背景交给 ChatGPT、Gemini 或其他 AI 工具，让它们能快速理解项目现状，并基于现有代码继续给出开发建议、排查问题或生成代码。

## 1. 项目定位

项目名称：个人交易复盘系统  
英文名：Personal Trade Review System

这是一个面向个人短线交易者的交易复盘与行为纠错系统，核心目标不是行情分析、股票推荐或自动交易，而是帮助用户记录每一笔交易、归类错误、完成单笔复盘和周复盘，并沉淀下周盘中纪律。

核心闭环：

```text
交易记录 -> 错误标签 -> 单笔复盘 -> 周统计 -> 周复盘 -> 下周纪律卡片
```

## 2. 技术栈

前端：

```text
Vue 3
Vite
Element Plus
Axios
Vue Router
ECharts
```

后端：

```text
Java 17
Spring Boot 3.3.7
MyBatis Plus
Maven
Lombok
MySQL 8
```

## 3. 项目目录

```text
trade-review-system
├─ backend        后端 Spring Boot 项目
├─ frontend       前端 Vue 3 项目
├─ database       数据库初始化脚本
├─ README.md
└─ PROJECT_CONTEXT_FOR_CHATGPT.md
```

后端主要目录：

```text
backend/src/main/java/com/tom/tradereview
├─ controller
├─ service
├─ service/impl
├─ mapper
├─ entity
└─ dto
```

前端主要目录：

```text
frontend/src
├─ api
├─ components
├─ router
├─ styles
└─ views
```

## 4. 当前已实现功能

### 4.1 交易记录

已实现：

- 新增交易
- 编辑交易
- 删除交易
- 查询交易列表
- 查看交易详情
- 按日期范围筛选
- 按股票名称筛选
- 按模式内 / 模式外筛选
- 多选交易
- 导出筛选结果为 CSV
- 导出选中交易为 CSV

前端页面：

```text
/trades
/trades/create
/trades/edit/:id
/trades/:id
```

### 4.2 错误标签

已实现：

- 错误标签列表
- 新增错误标签
- 编辑错误标签
- 删除错误标签
- 删除错误标签时同步清理交易和标签的关联记录
- 新增 / 编辑交易时选择多个错误标签
- 交易列表展示错误标签

前端页面：

```text
/mistake-tags
```

### 4.3 首页仪表盘

已实现：

- 日期范围筛选
- 本周 / 指定周期盈亏统计
- 交易次数
- 胜率
- 模式内交易数量
- 错误标签排行
- 最近周复盘纪律展示

前端页面：

```text
/dashboard
```

### 4.4 单笔交易复盘

已实现基础功能：

- 给某笔交易写复盘
- 查询某笔交易复盘
- 修改复盘

前端页面：

```text
/trades/:id/review
```

复盘字段：

```text
操作经过
当时计划
实际执行
真正问题
改进规则
```

### 4.5 周复盘

已实现基础功能：

- 新增周复盘
- 查询周复盘列表
- 查询周复盘详情
- 修改周复盘
- 保存下周三条纪律
- 保存训练主题和训练方式

前端页面：

```text
/weekly-review
```

### 4.6 纪律卡片

已实现：

- 展示最近一份周复盘里的三条纪律
- 展示训练主题

前端页面：

```text
/rule-card
```

## 5. 后端接口概览

### 5.1 交易记录接口

```text
GET    /api/trades
GET    /api/trades/{id}
POST   /api/trades
PUT    /api/trades/{id}
DELETE /api/trades/{id}
```

交易列表支持参数：

```text
startDate
endDate
stockName
isPatternTrade
```

### 5.2 交易错误标签接口

```text
GET  /api/trades/{id}/mistakes
POST /api/trades/{id}/mistakes
```

`POST` 请求体：

```json
{
  "mistakeTagIds": [1, 2, 5]
}
```

### 5.3 错误标签接口

```text
GET    /api/mistake-tags
GET    /api/mistake-tags/{id}
POST   /api/mistake-tags
PUT    /api/mistake-tags/{id}
DELETE /api/mistake-tags/{id}
```

### 5.4 单笔复盘接口

```text
POST /api/trade-reviews
GET  /api/trade-reviews/{tradeId}
PUT  /api/trade-reviews/{id}
```

### 5.5 周复盘接口

```text
GET  /api/weekly-reviews
GET  /api/weekly-reviews/{id}
POST /api/weekly-reviews
PUT  /api/weekly-reviews/{id}
```

### 5.6 周统计接口

```text
GET /api/statistics/week?start=2026-04-20&end=2026-04-26
```

返回核心字段：

```text
tradeCount
winCount
lossCount
winRate
profitAmount
profitRate
patternTradeCount
nonPatternTradeCount
topMistakes
biggestWinTrade
biggestLossTrade
```

### 5.7 纪律卡片接口

```text
GET /api/rule-card
```

## 6. 数据库表

当前使用 MySQL，数据库名：

```text
trade_review_system
```

主要表：

```text
trade_record          交易记录
mistake_tag           错误标签
trade_mistake_rel     交易和错误标签关联
trade_review          单笔交易复盘
weekly_review         周复盘
```

交易记录核心字段：

```text
stock_code
stock_name
buy_time
buy_price
sell_time
sell_price
position_level
stop_loss_price
buy_reason
sell_reason
teacher_opinion
key_level
profit_amount
profit_rate
is_pattern_trade
trade_date
```

## 7. 当前前端页面

```text
/dashboard              首页仪表盘
/trades                 交易记录列表
/trades/create          新增交易
/trades/edit/:id        编辑交易
/trades/:id             交易详情
/trades/:id/review      单笔交易复盘
/mistake-tags           错误标签管理
/weekly-review          周复盘
/rule-card              纪律卡片
```

## 8. 本地启动方式

### 8.1 初始化数据库

在项目根目录执行：

```cmd
mysql --default-character-set=utf8mb4 -u root -p < database\schema.sql
```

MySQL 本地密码由开发者自己填写，不建议在公开文档或提问内容中暴露。

```text
[本地 MySQL 密码]
```

### 8.2 启动后端

进入后端目录：

```cmd
cd backend
mvn spring-boot:run
```

后端地址：

```text
http://localhost:8080
```

### 8.3 启动前端

进入前端目录：

```cmd
cd frontend
npm.cmd install
npm.cmd run dev
```

前端地址：

```text
http://localhost:5173
```

## 9. 当前代码注意事项

1. `backend/src/main/resources/application.yml` 当前为了本地开发方便，可能直接写了 MySQL 密码：

```yaml
password: [本地 MySQL 密码]
```

如果要公开仓库或部署，建议改为：

```yaml
password: ${MYSQL_ROOT_PASSWORD:}
```

2. `frontend` 使用 Element Plus，部分页面由 Gemini 优化过 UI。

3. CSV 导出逻辑在 `frontend/src/views/TradeList.vue` 中，目前是浏览器端生成 CSV 文件。

4. 周复盘页面目前还是基础表单，后续可以增强为“选择日期范围 -> 自动生成统计 -> 手动填写总结 -> 保存周复盘”的完整流程。

5. 当前系统没有登录、多用户、权限控制，也没有图片上传和券商导入。

## 10. 推荐下一步开发方向

可以继续问 ChatGPT：

```text
基于这个项目说明，请帮我设计下一阶段功能开发方案。
目标是完善周复盘页面，让它支持选择日期范围后自动拉取统计数据，
并把统计结果和用户手写总结一起保存到 weekly_review 表。
请给出后端接口设计、前端页面交互设计、需要修改的文件，以及关键代码。
```

也可以问：

```text
请基于这个项目说明，帮我检查当前系统的业务闭环还缺哪些功能，
并按优先级列出 MVP 完善路线图。
```

或者：

```text
请基于这个项目说明，帮我优化交易记录 CSV 导出的字段结构，
让它更适合上传给 ChatGPT 分析交易错误、执行纪律和下周训练重点。
```

## 11. 给 AI 的开发约束

如果让 AI 直接改代码，建议加上这些约束：

```text
请只修改必要文件，不要重构整个项目。
请保持现有路由、接口路径和数据库表名不变。
请不要删除已有功能。
请保持前端使用 Vue 3 + Element Plus。
请保持后端使用 Spring Boot + MyBatis Plus。
请输出具体文件路径和完整代码片段。
```
