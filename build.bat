@echo off
setlocal enabledelayedexpansion

REM 构建脚本 - Windows版本
REM 用法: build.bat <domain> <github_token>

if "%~2"=="" (
    echo 用法: %0 ^<域名^> ^<GitHub_Token^>
    echo 例如: %0 "https://example.com/login" "ghp_xxxxxxxxxxxx"
    exit /b 1
)

set "DOMAIN=%~1"
set "TOKEN=%~2"
set "GITHUB_OWNER=xieleihan"
set "GITHUB_REPO=RouterCompanion"

echo 🚀 开始构建 RouterCompanion APK...
echo 📍 目标域名: !DOMAIN!
echo ⏰ 构建时间: %DATE% %TIME%

REM 创建临时JSON文件
echo {"event_type":"build-with-domain","client_payload":{"domain":"!DOMAIN!"}} > temp_payload.json

REM 发送构建请求到GitHub Actions
curl -s -w "%%{http_code}" -X POST ^
    -H "Authorization: token !TOKEN!" ^
    -H "Accept: application/vnd.github.v3+json" ^
    -H "Content-Type: application/json" ^
    -d @temp_payload.json ^
    "https://api.github.com/repos/!GITHUB_OWNER!/!GITHUB_REPO!/dispatches" > response.tmp

REM 读取HTTP状态码
for /f %%i in (response.tmp) do set "http_code=%%i"

REM 清理临时文件
del temp_payload.json response.tmp

if "!http_code!"=="204" (
    echo ✅ 构建请求发送成功!
    echo.
    echo 🔗 相关链接:
    echo    📊 构建进度: https://github.com/!GITHUB_OWNER!/!GITHUB_REPO!/actions
    echo    📦 发布页面: https://github.com/!GITHUB_OWNER!/!GITHUB_REPO!/releases
    echo.
    echo ⏳ 请等待几分钟，GitHub Actions 将自动构建并发布 APK
) else (
    echo ❌ 构建请求失败
    echo    HTTP状态码: !http_code!
    echo.
    echo 🔍 可能的原因:
    echo    • GitHub Token 无效或权限不足
    echo    • 仓库名称错误  
    echo    • 网络连接问题
)

pause