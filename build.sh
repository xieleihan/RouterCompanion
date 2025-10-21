#!/bin/bash

# 构建脚本 - 通过命令行触发构建
# 用法: ./build.sh <domain> <github_token>

if [ $# -ne 2 ]; then
    echo "用法: $0 <域名> <GitHub_Token>"
    echo "例如: $0 'https://example.com/login' 'ghp_xxxxxxxxxxxx'"
    exit 1
fi

DOMAIN="$1"
TOKEN="$2"
GITHUB_OWNER="xieleihan"  # 替换为您的GitHub用户名
GITHUB_REPO="RouterCompanion"    # 替换为您的仓库名

echo "🚀 开始构建 RouterCompanion APK..."
echo "📍 目标域名: $DOMAIN"
echo "⏰ 构建时间: $(date)"

# 发送构建请求到GitHub Actions
response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Authorization: token $TOKEN" \
    -H "Accept: application/vnd.github.v3+json" \
    -H "Content-Type: application/json" \
    -d "{\"event_type\":\"build-with-domain\",\"client_payload\":{\"domain\":\"$DOMAIN\"}}" \
    "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/dispatches")

# 分离响应体和状态码
http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | sed '$d')

if [ "$http_code" = "204" ]; then
    echo "✅ 构建请求发送成功!"
    echo ""
    echo "🔗 相关链接:"
    echo "   📊 构建进度: https://github.com/$GITHUB_OWNER/$GITHUB_REPO/actions"
    echo "   📦 发布页面: https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases"
    echo ""
    echo "⏳ 请等待几分钟，GitHub Actions 将自动构建并发布 APK"
else
    echo "❌ 构建请求失败"
    echo "   HTTP状态码: $http_code"
    echo "   响应内容: $response_body"
    echo ""
    echo "🔍 可能的原因:"
    echo "   • GitHub Token 无效或权限不足"
    echo "   • 仓库名称错误"
    echo "   • 网络连接问题"
fi