# Dify API 接口连通性测试指南

## 1. 测试目的

本指南提供了使用Postman或类似工具直接测试Dify API连通性的步骤，用于排除代码问题和验证API密钥有效性。

## 2. 测试准备

### 2.1 工具准备
- 安装[Postman](https://www.postman.com/downloads/)
- 或使用其他支持HTTP请求的工具（如curl、Insomnia等）

### 2.2 获取必要信息
- Dify API密钥：从Dify控制台获取
- API端点：`https://api.dify.ai/v1/chat-messages`（与配置中的`dify.analyze-url`一致）

## 3. Postman测试步骤

### 3.1 创建请求

1. 打开Postman，点击"New" > "Request"
2. 设置请求名称为"Dify Connectivity Test"
3. 设置请求方法为`POST`
4. 输入请求URL：`https://api.dify.ai/v1/chat-messages`

### 3.2 设置请求头

在"Headers"标签中添加以下请求头：

| Header Name | Value |
|-------------|-------|
| Authorization | `Bearer YOUR_API_KEY` (替换为实际的Dify API密钥) |
| Content-Type | `application/json` |

### 3.3 设置请求体

在"Body"标签中：
1. 选择"raw"
2. 设置格式为"JSON"
3. 输入以下JSON内容：
```json
{
  "inputs": {
    "content": "测试"
  }
}
```

### 3.4 发送请求

点击"Send"按钮发送请求。

## 4. 测试结果分析

### 4.1 成功场景

如果请求成功，您将收到一个包含状态码200和响应体的返回。这表明：
- Dify API密钥有效
- API端点可访问
- 基本请求格式正确

**结论**：如果Postman测试成功，但应用程序仍返回401错误，问题可能出在：
- 应用程序中API密钥注入失败
- 环境变量配置问题
- 配置文件中密钥读取问题

### 4.2 失败场景

#### 4.2.1 401 Unauthorized

**原因分析**：API密钥无效或格式错误

**排查步骤**：
1. 确认Authorization头的格式：`Bearer ` + 空格 + API密钥
2. 验证API密钥是否正确，可能需要从Dify控制台重新生成
3. 检查密钥是否过期或被撤销

#### 4.2.2 404 Not Found

**原因分析**：API端点不存在

**排查步骤**：
1. 确认URL是否正确：`https://api.dify.ai/v1/chat-messages`
2. 检查网络连接和代理设置

#### 4.2.3 其他错误

**常见错误**：
- 400 Bad Request：请求体格式错误
- 429 Too Many Requests：API调用频率超限
- 500 Internal Server Error：Dify服务端错误

## 5. 与应用程序集成排查

如果Postman测试成功，但应用程序仍返回401错误，请执行以下检查：

### 5.1 密钥注入检查

1. 在`AiAnalysisServiceImpl.java`中添加日志输出API密钥前几位和后几位（不要输出完整密钥）
2. 确认`@Value("${dify.api-key}")`注解正确注入了环境变量或配置文件中的值

### 5.2 环境变量检查

1. 确认`start.bat`脚本中设置了正确的`DIFY_API_KEY`环境变量
2. 使用`echo %DIFY_API_KEY%`命令验证Windows环境变量是否正确设置
3. 检查`application.properties`中的默认值是否覆盖了环境变量

### 5.3 配置文件检查

1. 确认`application.properties`中的`dify.analyze-url`配置正确
2. 检查`dify.api-key`配置格式是否正确

## 6. 参考信息

### 6.1 应用程序相关文件

- API调用实现在：`src/main/java/com/digitalcoin/holdings_service/service/impl/AiAnalysisServiceImpl.java`
- 相关方法：`testDifyConnection()`、`callDifyAdjustApi()`
- 配置文件：`src/main/resources/application.properties`

### 6.2 Dify API文档

请参考[Dify官方API文档](https://docs.dify.ai/)获取最新的API使用说明。

## 7. 测试流程总结

1. 首先使用Postman直接测试Dify API
2. 根据Postman测试结果判断问题类型
3. 如果Postman测试成功，重点排查应用程序中的密钥注入和配置问题
4. 如果Postman测试失败，重点检查API密钥有效性和网络连接问题

通过以上步骤，可以有效区分是API密钥问题还是应用程序代码中的密钥注入问题。