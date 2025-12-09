@echo off

REM 设置环境变量 - 生产环境配置
echo Setting environment variables...
set DIFY_API_KEY=your-production-api-key-here
set DB_URL=jdbc:mysql://localhost:2494/2494?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set JAVA_OPTS=-Xms512m -Xmx1024m

echo Starting holdings-service...
java %JAVA_OPTS% -jar target/holdings-service-0.0.1-SNAPSHOT.jar

pause