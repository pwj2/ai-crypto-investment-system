# AI 加密货币投资系统后端服务

这是AI加密货币投资系统的后端服务，使用Node.js和Express框架开发，提供API接口供前端调用。

## 技术栈

- Node.js
- Express
- MySQL
- CORS
- Body-parser

## 安装依赖

```bash
# 进入后端目录
cd backend

# 安装依赖
npm install
```

## 配置数据库

1. 确保您已经安装了MySQL数据库
2. 创建一个名为`ai_crypto_db`的数据库
3. 编辑`config/db.config.js`文件，配置您的数据库连接参数：

```javascript
module.exports = {
  host: 'localhost',
  user: 'root', // 您的MySQL用户名
  password: 'password', // 您的MySQL密码
  database: 'ai_crypto_db', // 数据库名称
  port: 3306
};
```

## 启动服务

### 开发环境

```bash
npm run dev
```

### 生产环境

```bash
npm start
```

服务将在`http://localhost:8081`上运行。

## API接口

### 健康检查

```
GET /api/health
```

返回服务健康状态。

### 获取当前持仓

```
GET /api/holdings/current
```

返回当前加密货币持仓信息。

### 获取持仓历史

```
GET /api/holdings/history
```

返回资产分布历史数据。

### 获取任务列表

```
GET /api/tasks
```

返回系统任务列表。

### 获取消息分析报告

```
GET /api/message-analysis-reports
```

返回消息分析报告。

## 与前端集成

前端项目已经配置了API代理，所有`/api`请求都会被代理到`http://localhost:8081`。

在`vite.config.js`中可以查看或修改代理配置：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    rewrite: (path) => path
  }
}
```

## 注意事项

1. 确保MySQL数据库已经启动
2. 确保数据库连接参数配置正确
3. 首次使用时，建议先创建必要的数据库表
4. 生产环境中，请使用环境变量存储敏感信息
