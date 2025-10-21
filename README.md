# RouterCompanion 🚀

一个支持动态域名配置和自动构建的 Android WebView 应用。

## ✨ 主要特性

- 📱 **WebView 应用**: 基于 WebView 的 Android 应用
- 🌐 **动态域名**: 支持通过 API 动态修改访问域名
- 🤖 **自动构建**: GitHub Actions 自动构建和发布 APK
- 📦 **版本管理**: 智能版本号管理（自增）
- 💾 **状态保存**: 自动保存浏览状态和 Cookie
- 🔄 **离线缓存**: 支持离线访问

## 🚀 快速开始

### 方法 1: Web 界面 (推荐)

1. 打开项目中的 `build-interface.html` 文件
2. 输入目标域名和 GitHub Personal Access Token
3. 点击"开始构建 APK"按钮
4. 等待构建完成，在 [Releases](https://github.com/xieleihan/RouterCompanion/releases) 页面下载 APK

### 方法 2: 命令行

```bash
# Linux/macOS
chmod +x build.sh
./build.sh "https://your-domain.com/login" "your_github_token"

# Windows
build.bat "https://your-domain.com/login" "your_github_token"
```

### 方法 3: API 调用

```bash
curl -X POST \
  -H "Authorization: token YOUR_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Content-Type: application/json" \
  -d '{"event_type":"build-with-domain","client_payload":{"domain":"https://example.com"}}' \
  https://api.github.com/repos/xieleihan/RouterCompanion/dispatches
```

## 📋 使用说明

### 1. 获取 GitHub Token

1. 访问 [GitHub Settings → Personal Access Tokens](https://github.com/settings/tokens)
2. 点击 "Generate new token (classic)"
3. 勾选 `repo` 权限
4. 生成并复制 Token

### 2. 构建流程

1. **发送请求**: 通过 Web 界面、命令行或 API 发送构建请求
2. **触发构建**: GitHub Actions 自动开始构建流程
3. **修改代码**: 自动替换 MainActivity.java 中的域名
4. **构建 APK**: 编译生成 Debug APK
5. **发布 Release**: 自动创建 GitHub Release 并上传 APK

### 3. APK 命名规则

```
apk-{日期}-{域名标识}-{哈希}-v{版本号}
```

例如: `apk-20241021-example.com-a1b2c3d4-v15.apk`

## 🛠️ 项目结构

```
RouterCompanion/
├── app/                          # Android 应用源码
│   └── src/main/java/           # Java 源码
├── .github/workflows/           # GitHub Actions 工作流
│   ├── buildAndroidApk.yml     # 原始构建流程 (Legacy)
│   └── build-with-domain.yml   # 动态域名构建流程
├── build-interface.html         # Web 构建界面
├── api-docs.html               # API 文档和测试工具
├── build.sh                    # Linux/macOS 构建脚本
├── build.bat                   # Windows 构建脚本
├── deploy.sh                   # Linux/macOS 部署脚本
├── deploy.bat                  # Windows 部署脚本
├── server.js                   # Node.js API 服务器 (可选)
├── version.txt                 # 版本号存储
└── BUILD_README.md            # 详细构建说明
```

## 🔧 配置说明

如果您 fork 了这个项目，需要修改以下配置：

1. **更新 GitHub 信息**:

   - 运行 `deploy.sh` (Linux/macOS) 或 `deploy.bat` (Windows)
   - 或手动修改各文件中的 `xieleihan` 和 `RouterCompanion`

2. **启用 GitHub Actions**:
   - 确保仓库中的 Actions 功能已启用
   - 检查工作流文件权限

## 📊 监控和日志

- 🔄 **构建状态**: [GitHub Actions](https://github.com/xieleihan/RouterCompanion/actions)
- 📦 **APK 下载**: [GitHub Releases](https://github.com/xieleihan/RouterCompanion/releases)
- 📖 **API 文档**: 打开项目中的 `api-docs.html`

## 🔍 故障排除

### 常见问题

1. **构建失败**

   - 检查 GitHub Token 是否有 `repo` 权限
   - 确认仓库名称配置正确
   - 查看 Actions 页面的详细错误日志

2. **域名格式错误**

   - 确保域名包含 `http://` 或 `https://`
   - 检查 URL 格式是否正确

3. **版本号问题**
   - 版本号存储在 `version.txt` 文件中
   - 如需重置，手动编辑该文件

## 📱 应用功能

- ✅ **WebView 浏览**: 支持现代 Web 技术
- ✅ **Cookie 持久化**: 自动保存登录状态
- ✅ **状态恢复**: 应用重启后恢复上次页面
- ✅ **返回键处理**: 智能处理返回键逻辑
- ✅ **缓存管理**: 支持离线访问
- ✅ **全屏适配**: Edge-to-edge 显示

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

---

<div align="center">
  
**[🌟 Star this repo](https://github.com/xieleihan/RouterCompanion) | [🐛 Report Bug](https://github.com/xieleihan/RouterCompanion/issues) | [✨ Request Feature](https://github.com/xieleihan/RouterCompanion/issues)**

</div>
