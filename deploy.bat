@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

echo 🚀 RouterCompanion 自动构建系统部署脚本
echo ========================================

:check_requirements
echo 📋 检查系统要求...
where git >nul 2>nul
if errorlevel 1 (
    echo ❌ Git 未安装，请先安装 Git
    pause
    exit /b 1
)

where curl >nul 2>nul
if errorlevel 1 (
    echo ❌ curl 未安装，请先安装 curl
    pause
    exit /b 1
)

echo ✅ 系统要求检查通过

:setup_github_config
echo.
echo 🔧 配置 GitHub 信息...

set /p GITHUB_USER="请输入您的 GitHub 用户名: "
set /p GITHUB_REPO="请输入仓库名 (默认: RouterCompanion): "
if "!GITHUB_REPO!"=="" set GITHUB_REPO=RouterCompanion

echo.
echo 📝 更新配置文件...

REM 更新 build-interface.html
powershell -Command "(Get-Content 'build-interface.html') -replace 'const GITHUB_OWNER = ''xieleihan''', 'const GITHUB_OWNER = ''!GITHUB_USER!''' | Set-Content 'build-interface.html'"
powershell -Command "(Get-Content 'build-interface.html') -replace 'const GITHUB_REPO = ''RouterCompanion''', 'const GITHUB_REPO = ''!GITHUB_REPO!''' | Set-Content 'build-interface.html'"

REM 更新 build.sh
powershell -Command "(Get-Content 'build.sh') -replace 'GITHUB_OWNER=\"xieleihan\"', 'GITHUB_OWNER=\"!GITHUB_USER!\"' | Set-Content 'build.sh'"
powershell -Command "(Get-Content 'build.sh') -replace 'GITHUB_REPO=\"RouterCompanion\"', 'GITHUB_REPO=\"!GITHUB_REPO!\"' | Set-Content 'build.sh'"

REM 更新 build.bat
powershell -Command "(Get-Content 'build.bat') -replace 'set \"GITHUB_OWNER=xieleihan\"', 'set \"GITHUB_OWNER=!GITHUB_USER!\"' | Set-Content 'build.bat'"
powershell -Command "(Get-Content 'build.bat') -replace 'set \"GITHUB_REPO=RouterCompanion\"', 'set \"GITHUB_REPO=!GITHUB_REPO!\"' | Set-Content 'build.bat'"

REM 更新 api-docs.html
powershell -Command "(Get-Content 'api-docs.html') -replace 'xieleihan', '!GITHUB_USER!' | Set-Content 'api-docs.html'"
powershell -Command "(Get-Content 'api-docs.html') -replace 'RouterCompanion', '!GITHUB_REPO!' | Set-Content 'api-docs.html'"

echo ✅ GitHub 配置更新完成

:test_github_actions
echo.
set /p test_actions="🧪 是否要测试 GitHub Actions？(y/n): "

if /i "!test_actions!"=="y" (
    set /p GITHUB_TOKEN="请输入您的 GitHub Personal Access Token: "
    set /p TEST_DOMAIN="请输入测试域名 (默认: http://example.com/test): "
    if "!TEST_DOMAIN!"=="" set TEST_DOMAIN=http://example.com/test
    
    echo 🚀 发送测试构建请求...
    
    echo {"event_type":"build-with-domain","client_payload":{"domain":"!TEST_DOMAIN!"}} > temp_test.json
    
    curl -s -w "%%{http_code}" -X POST ^
        -H "Authorization: token !GITHUB_TOKEN!" ^
        -H "Accept: application/vnd.github.v3+json" ^
        -H "Content-Type: application/json" ^
        -d @temp_test.json ^
        "https://api.github.com/repos/!GITHUB_USER!/!GITHUB_REPO!/dispatches" > test_response.tmp
    
    for /f %%i in (test_response.tmp) do set "http_code=%%i"
    
    del temp_test.json test_response.tmp
    
    if "!http_code!"=="204" (
        echo ✅ 测试成功！构建已触发
        echo 🔗 查看构建状态: https://github.com/!GITHUB_USER!/!GITHUB_REPO!/actions
    ) else (
        echo ❌ 测试失败，HTTP 状态码: !http_code!
        echo 请检查 GitHub Token 权限和仓库配置
    )
)

:generate_usage_guide
echo.
echo 📖 生成使用说明...

(
echo # 🚀 RouterCompanion 快速开始指南
echo.
echo ## 已完成的配置
echo.
echo - ✅ GitHub Actions 工作流已配置
echo - ✅ Web 构建界面已就绪
echo - ✅ 命令行脚本已配置
echo - ✅ API 文档已生成
echo.
echo ## 使用方法
echo.
echo ### 1. 创建 GitHub Token
echo.
echo 1. 访问 https://github.com/settings/tokens
echo 2. 点击 "Generate new token (classic)"
echo 3. 勾选 `repo` 权限
echo 4. 生成并复制 Token
echo.
echo ### 2. 开始构建
echo.
echo #### 方法1: Web界面
echo 打开 `build-interface.html` 文件，输入域名和 Token
echo.
echo #### 方法2: Windows命令行
echo ```cmd
echo build.bat "https://your-domain.com" "your_github_token"
echo ```
echo.
echo ## 相关链接
echo.
echo - 🔄 构建状态: https://github.com/!GITHUB_USER!/!GITHUB_REPO!/actions
echo - 📦 APK下载: https://github.com/!GITHUB_USER!/!GITHUB_REPO!/releases
echo - 📖 API文档: 打开 api-docs.html
echo.
echo ## 故障排除
echo.
echo 如果遇到问题，请检查：
echo 1. GitHub Token 权限是否正确
echo 2. 仓库名称是否匹配
echo 3. 域名格式是否正确（需包含 http:// 或 https://）
) > QUICK_START.md

echo ✅ 使用说明已生成: QUICK_START.md

:finish
echo.
echo 🎉 部署完成！
echo.
echo 📋 下一步：
echo 1. 提交代码到 GitHub
echo 2. 确保 GitHub Actions 已启用
echo 3. 打开 build-interface.html 开始使用
echo.
echo 🔗 快速链接：
echo    Web界面: build-interface.html
echo    API文档: api-docs.html
echo    使用指南: QUICK_START.md
echo.

pause