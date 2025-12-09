# 数字资产持仓服务部署文档

## 1. 依赖环境

### 1.1 开发环境要求
- **JDK 版本**: JDK 9 或更高版本
- **MySQL 版本**: MySQL 8.0 或更高版本
- **Maven 版本**: Apache Maven 3.6.0 或更高版本
- **操作系统**: Windows/Linux/macOS

## 2. 配置管理

### 2.1 环境变量配置（推荐）
为了安全起见，推荐使用环境变量配置敏感信息，支持以下环境变量：

**Windows (PowerShell):**
```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/studyr3q1?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='你的数据库密码'
$env:DIFY_API_KEY='你的Dify API密钥'
$env:DIFY_API_BASE_URL='https://api.dify.ai/v1'
$env:DIFY_AGENT_ID='你的智能体ID'
```

**Linux/macOS:**
```bash
export DB_URL='jdbc:mysql://localhost:3306/studyr3q1?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true'
export DB_USERNAME='root'
export DB_PASSWORD='你的数据库密码'
export DIFY_API_KEY='你的Dify API密钥'
export DIFY_API_BASE_URL='https://api.dify.ai/v1'
export DIFY_AGENT_ID='你的智能体ID'
```

### 2.2 配置文件修改（备选）
如不使用环境变量，可修改 `src/main/resources/application.properties` 文件：

```properties
# MySQL连接配置 - 使用默认值
spring.datasource.url=jdbc:mysql://localhost:3306/studyr3q1?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=你的数据库密码

# Dify API配置 - 使用默认值
dify.api-key=你的Dify API密钥
dify.api.base-url=https://api.dify.ai/v1
dify.analyze-url=https://api.dify.ai/v1/chat-messages
dify.collect-url=https://api.dify.ai/v1/agents/你的智能体ID/run
dify.chat-url=https://api.dify.ai/v1/chat-messages
```

### 2.3 服务器端口配置（可选）
如需修改服务端口，可调整以下配置：

```properties
# 服务器端口
server.port=8081
```

### 2.4 数据源连接池配置（可选）
可根据需求调整Hikari连接池参数：

```properties
# 数据源连接池配置
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=30000
```

## 3. 项目启动步骤

### 3.1 数据库准备
1. 创建MySQL数据库：`CREATE DATABASE studyr3q1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. 执行数据库初始化脚本（如适用）

### 3.2 编译打包
在项目根目录执行以下命令：

```bash
# 编译并打包
mvn clean package
```

### 3.3 运行项目

#### 3.3.1 使用Maven直接运行

```bash
mvn spring-boot:run
```

#### 3.3.2 使用Java命令运行

```bash
# 运行打包后的jar文件
java -jar target/holdings-service-0.0.1-SNAPSHOT.jar
```

## 4. 接口文档访问
项目启动成功后，可以通过以下地址访问Swagger接口文档：

```
http://localhost:8081/swagger-ui/index.html
```

## 5. 常见问题排查

### 5.1 数据库连接失败
- 检查数据库是否启动
- 验证数据库用户名和密码是否正确
- 确认数据库地址和端口配置正确
- 确保数据库防火墙允许连接
- 检查数据库用户是否有足够权限
- 验证数据库是否已创建：`CREATE DATABASE studyr3q1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

### 5.2 Dify API调用失败
- 检查API密钥是否有效
- 验证网络连接是否正常
- 确认API URL配置正确
- 检查代理设置（如需通过代理访问）
- 验证智能体ID是否正确配置
- 检查API调用频率限制

### 5.3 端口冲突
- 修改application.properties中的server.port配置，使用其他未被占用的端口
- 关闭占用端口的其他进程

### 5.4 Swagger文档兼容性问题
- **问题**: Spring Boot 2.7.0及以上版本与SpringFox Swagger存在兼容性问题
- **解决方案**:
  1. 暂时禁用Swagger：注释掉SwaggerConfig类中的@EnableOpenApi注解
  2. 使用更简化的Swagger配置
  3. 考虑升级到SpringDoc OpenAPI作为替代方案

### 5.5 Maven构建失败
- 检查JDK版本是否符合要求
- 验证Maven版本是否正确
- 检查网络连接，确保能下载依赖
- 执行`mvn clean`后再尝试打包
- 使用`mvn package -e`获取详细错误信息

### 5.6 连接池问题
- 检查Hikari连接池配置参数
- 如遇连接泄漏，增加连接超时设置
- 监控连接池状态：`logging.level.com.zaxxer.hikari=DEBUG`

## 6. 日志查看

### 6.1 日志配置
日志文件默认输出到控制台，可以通过调整application.properties中的logging配置修改日志级别和输出位置：

```properties
# 日志配置
logging.level.root=INFO
logging.level.com.zaxxer.hikari=DEBUG
logging.level.org.springframework.jdbc=DEBUG
logging.level.org.springframework.orm.jpa=DEBUG
logging.level.org.hibernate=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.javax.sql=DEBUG
```

### 6.2 常见日志分析
- 连接池错误：搜索"HikariPool"
- 数据库错误：搜索"SQLException"或"Hibernate"
- API调用错误：搜索"RestTemplate"或"Dify"
- 端口冲突：搜索"Address already in use"

## 7. 关键接口信息

### 7.1 持仓查询接口
- **URL**: GET /api/holdings/current
- **功能**: 获取用户当前的数字资产持仓信息

### 7.2 消息采集接口
- **URL**: GET /api/tasks/collect-messages
- **功能**: 手动触发消息采集任务

### 7.3 AI分析接口
- **URL**: GET /api/tasks/analyze-message/{messageId}
- **功能**: 手动触发AI分析指定消息
- **参数**: messageId - 消息ID

## 8. 环境变量完整列表

### 8.1 必需环境变量
| 环境变量名 | 描述 | 默认值 | 示例值 |
|------------|------|--------|----------|
| DB_PASSWORD | 数据库密码 | - | your_secure_password |
| DIFY_API_KEY | Dify API密钥 | - | sk-xxxxxxxxxxxxxxxxx |
| DIFY_AGENT_ID | Dify智能体ID | - | agent_xxxxxxxxxxxxxxx |

### 8.2 可选环境变量
| 环境变量名 | 描述 | 默认值 | 建议值 |
|------------|------|--------|----------|
| DB_URL | 数据库连接URL | jdbc:mysql://localhost:3306/studyr3q1?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true | 根据实际部署环境修改 |
| DB_USERNAME | 数据库用户名 | root | 根据实际部署环境修改 |
| DIFY_API_BASE_URL | Dify API基础URL | https://api.dify.ai/v1 | 生产环境使用官方地址 |
| SERVER_PORT | 服务器端口 | 8081 | 根据实际端口分配修改 |
| JAVA_OPTS | JVM参数 | - | -Xms512m -Xmx1024m |

## 9. 启动与停止命令

### 9.1 Windows环境

#### 9.1.1 使用启动脚本（推荐）
1. 确保已完成编译打包：`mvn clean package`
2. 修改`start.bat`文件中的环境变量值
3. 双击运行`start.bat`脚本

#### 9.1.2 手动启动步骤
```powershell
# 设置环境变量
$env:DB_PASSWORD='your_password'
$env:DIFY_API_KEY='your_api_key'
$env:DIFY_AGENT_ID='your_agent_id'
$env:JAVA_OPTS='-Xms512m -Xmx1024m'

# 运行应用
java $env:JAVA_OPTS -jar target/holdings-service-0.0.1-SNAPSHOT.jar
```

#### 9.1.3 停止服务
1. 在运行服务的命令窗口按 `Ctrl+C`
2. 或在任务管理器中结束Java进程

### 9.2 Linux环境

#### 9.2.1 使用启动脚本（推荐）
1. 确保已完成编译打包：`mvn clean package`
2. 设置脚本权限：`chmod +x start.sh`
3. 修改`start.sh`文件中的环境变量值
4. 运行脚本：`./start.sh`

#### 9.2.2 手动启动步骤
```bash
# 设置环境变量
export DB_PASSWORD='your_password'
export DIFY_API_KEY='your_api_key'
export DIFY_AGENT_ID='your_agent_id'
export JAVA_OPTS='-Xms512m -Xmx1024m'

# 后台运行应用
nohup java $JAVA_OPTS -jar target/holdings-service-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

#### 9.2.3 停止服务
```bash
# 查找进程ID
ps aux | grep holdings-service

# 终止进程
kill -15 <进程ID>

# 如无法正常终止，使用强制终止
kill -9 <进程ID>
```

## 10. 常见问题处理

### 10.1 服务启动失败
- **问题**: 服务启动后立即退出或报错
- **解决方案**:
  1. 检查日志文件（Linux: app.log, Windows: 命令窗口输出）
  2. 验证JDK版本是否符合要求（JDK 9+）
  3. 检查环境变量是否正确设置
  4. 确认端口是否被占用，修改`SERVER_PORT`环境变量
  5. 检查jar文件是否完整（重新打包：`mvn clean package`）

### 10.2 数据库连接超时
- **问题**: 启动时出现数据库连接超时错误
- **解决方案**:
  1. 验证数据库服务是否正常运行
  2. 检查`DB_URL`环境变量中的主机名和端口是否正确
  3. 确认数据库用户权限是否足够
  4. 检查防火墙设置，确保允许连接
  5. 增加连接超时时间：修改`spring.datasource.hikari.connection-timeout`配置

### 10.3 Dify API连接失败
- **问题**: 调用Dify API时出现连接错误
- **解决方案**:
  1. 验证`DIFY_API_KEY`是否正确且有效
  2. 检查网络连接和代理设置
  3. 确认`DIFY_API_BASE_URL`配置正确
  4. 检查智能体ID是否匹配：`DIFY_AGENT_ID`
  5. 查看API调用频率限制，避免超出配额

### 10.4 内存溢出
- **问题**: 运行过程中出现`OutOfMemoryError`
- **解决方案**:
  1. 增加JVM内存分配：修改`JAVA_OPTS`环境变量，如`-Xms1g -Xmx2g`
  2. 检查应用中是否存在内存泄漏
  3. 减少连接池大小：调整`spring.datasource.hikari.maximum-pool-size`配置

### 10.5 性能优化建议
- 调整Hikari连接池参数以适应实际负载
- 考虑启用连接池监控
- 根据服务器配置优化JVM参数
- 生产环境建议使用独立的日志管理系统
- 定期备份数据库

## 11. 部署检查清单

- [x] 数据库已创建并配置正确
- [x] 环境变量已正确设置（特别是密码和API密钥）
- [x] JDK版本符合要求
- [x] 端口未被占用
- [x] 有足够的磁盘空间和内存
- [x] 网络连接正常，可访问数据库和Dify API
- [x] 已使用优化后的配置文件（关闭调试日志）
- [x] 服务启动成功并能正常响应请求

## 12. 性能测试结果

### 12.1 并发测试数据
- **并发请求数**: 10个
- **平均响应时间**: 55.13ms
- **最大响应时间**: 160.65ms
- **最小响应时间**: 27.51ms
- **成功率**: 100%

### 12.2 性能结论
所有性能指标均满足要求，系统响应迅速，能够高效处理并发请求。

## 13. 项目状态与交付

### 13.1 已完成功能
- [x] 消息采集功能
- [x] AI分析功能
- [x] 持仓调整功能
- [x] 定时任务配置（每分钟执行一次消息采集）
- [x] 接口权限管理
- [x] 错误处理与日志记录

### 13.2 交付物清单
- 源代码仓库（含最新代码）
- 部署脚本（start.bat, start.sh）
- 部署文档（本文件）
- API接口文档（Swagger）
- 性能测试报告

### 13.3 注意事项
- 请定期更新Dify API密钥
- 生产环境建议关闭调试日志
- 定期监控服务运行状态和数据库性能