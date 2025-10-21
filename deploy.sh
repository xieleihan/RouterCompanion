#!/bin/bash

echo "🚀 RouterCompanion 自动构建系统部署脚本"
echo "========================================"

# 检查必要的工具
check_requirements() {
    echo "📋 检查系统要求..."
    
    if ! command -v git &> /dev/null; then
        echo "❌ Git 未安装，请先安装 Git"
        exit 1
    fi
    
    if ! command -v curl &> /dev/null; then
        echo "❌ curl 未安装，请先安装 curl"
        exit 1
    fi
    
    echo "✅ 系统要求检查通过"
}

# 设置 GitHub 信息
setup_github_config() {
    echo ""
    echo "🔧 配置 GitHub 信息..."
    
    read -p "请输入您的 GitHub 用户名: " GITHUB_USER
    read -p "请输入仓库名 (默认: RouterCompanion): " GITHUB_REPO
    GITHUB_REPO=${GITHUB_REPO:-RouterCompanion}
    
    echo ""
    echo "📝 更新配置文件..."
    
    # 更新 build-interface.html
    sed -i "s/const GITHUB_OWNER = 'xieleihan'/const GITHUB_OWNER = '$GITHUB_USER'/g" build-interface.html
    sed -i "s/const GITHUB_REPO = 'RouterCompanion'/const GITHUB_REPO = '$GITHUB_REPO'/g" build-interface.html
    
    # 更新 build.sh
    sed -i "s/GITHUB_OWNER=\"xieleihan\"/GITHUB_OWNER=\"$GITHUB_USER\"/g" build.sh
    sed -i "s/GITHUB_REPO=\"RouterCompanion\"/GITHUB_REPO=\"$GITHUB_REPO\"/g" build.sh
    
    # 更新 build.bat
    sed -i "s/set \"GITHUB_OWNER=xieleihan\"/set \"GITHUB_OWNER=$GITHUB_USER\"/g" build.bat
    sed -i "s/set \"GITHUB_REPO=RouterCompanion\"/set \"GITHUB_REPO=$GITHUB_REPO\"/g" build.bat
    
    # 更新 api-docs.html
    sed -i "s/xieleihan/$GITHUB_USER/g" api-docs.html
    sed -i "s/RouterCompanion/$GITHUB_REPO/g" api-docs.html
    
    echo "✅ GitHub 配置更新完成"
}

# 测试 GitHub Actions
test_github_actions() {
    echo ""
    echo "🧪 测试 GitHub Actions 配置..."
    
    read -p "是否要测试 GitHub Actions？(y/n): " test_actions
    
    if [[ $test_actions =~ ^[Yy]$ ]]; then
        read -p "请输入您的 GitHub Personal Access Token: " -s GITHUB_TOKEN
        echo ""
        read -p "请输入测试域名 (默认: http://example.com/test): " TEST_DOMAIN
        TEST_DOMAIN=${TEST_DOMAIN:-http://example.com/test}
        
        echo "🚀 发送测试构建请求..."
        
        response=$(curl -s -w "\n%{http_code}" -X POST \
            -H "Authorization: token $GITHUB_TOKEN" \
            -H "Accept: application/vnd.github.v3+json" \
            -H "Content-Type: application/json" \
            -d "{\"event_type\":\"build-with-domain\",\"client_payload\":{\"domain\":\"$TEST_DOMAIN\"}}" \
            "https://api.github.com/repos/$GITHUB_USER/$GITHUB_REPO/dispatches")
        
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" = "204" ]; then
            echo "✅ 测试成功！构建已触发"
            echo "🔗 查看构建状态: https://github.com/$GITHUB_USER/$GITHUB_REPO/actions"
        else
            echo "❌ 测试失败，HTTP 状态码: $http_code"
            echo "请检查 GitHub Token 权限和仓库配置"
        fi
    fi
}

# 生成使用说明
generate_usage_guide() {
    echo ""
    echo "📖 生成使用说明..."
    
    cat > QUICK_START.md << EOF
# 🚀 RouterCompanion 快速开始指南

## 已完成的配置

- ✅ GitHub Actions 工作流已配置
- ✅ Web 构建界面已就绪 
- ✅ 命令行脚本已配置
- ✅ API 文档已生成

## 使用方法

### 1. 创建 GitHub Token

1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 勾选 \`repo\` 权限
4. 生成并复制 Token

### 2. 开始构建

#### 方法1: Web界面
打开 \`build-interface.html\` 文件，输入域名和 Token

#### 方法2: 命令行
\`\`\`bash
chmod +x build.sh
./build.sh "https://your-domain.com" "your_github_token"
\`\`\`

#### 方法3: Windows
\`\`\`cmd
build.bat "https://your-domain.com" "your_github_token"
\`\`\`

## 相关链接

- 🔄 构建状态: https://github.com/$GITHUB_USER/$GITHUB_REPO/actions
- 📦 APK下载: https://github.com/$GITHUB_USER/$GITHUB_REPO/releases
- 📖 API文档: 打开 api-docs.html

## 故障排除

如果遇到问题，请检查：
1. GitHub Token 权限是否正确
2. 仓库名称是否匹配
3. 域名格式是否正确（需包含 http:// 或 https://）

EOF

    echo "✅ 使用说明已生成: QUICK_START.md"
}

# 主流程
main() {
    check_requirements
    setup_github_config
    test_github_actions
    generate_usage_guide
    
    echo ""
    echo "🎉 部署完成！"
    echo ""
    echo "📋 下一步："
    echo "1. 提交代码到 GitHub"
    echo "2. 确保 GitHub Actions 已启用"
    echo "3. 打开 build-interface.html 开始使用"
    echo ""
    echo "🔗 快速链接："
    echo "   Web界面: ./build-interface.html"
    echo "   API文档: ./api-docs.html" 
    echo "   使用指南: ./QUICK_START.md"
}

# 运行主流程
main