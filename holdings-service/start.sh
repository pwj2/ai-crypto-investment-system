#!/bin/bash

# 设置环境变量 - 生产环境配置
echo "设置环境变量..."
export DIFY_API_KEY=your-production-api-key-here
export DB_URL=jdbc:mysql://localhost:3306/studyr3q1?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
export JAVA_OPTS="-Xms512m -Xmx1024m"

echo "启动holdings-service服务..."
nohup java $JAVA_OPTS -jar target/holdings-service-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "服务已启动，日志输出到app.log文件"
ps aux | grep holdings-service