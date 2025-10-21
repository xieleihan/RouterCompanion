# RouterCompanion 自动构建系统

这个项目支持通过 API 接口动态修改域名并自动构建发布 APK。

## 🚀 功能特性

- 📱 动态修改 WebView 加载的域名
- 🤖 自动触发 GitHub Actions 构建
- 📦 自动发布到 GitHub Releases
- 🏷️ 智能版本号管理（自增）
- 🌐 Web 界面和命令行支持

## 🛠️ 使用方法

### 方法 1: Web 界面（推荐）

1. 打开 `build-interface.html` 文件
2. 输入目标域名和 GitHub Token
3. 点击"开始构建 APK"按钮
4. 等待构建完成并检查 Releases 页面

### 方法 2: 命令行脚本

#### Linux/macOS:

```bash
chmod +x build.sh
./build.sh "https://your-domain.com/login" "your_github_token"
```

#### Windows:

```cmd
build.bat "https://your-domain.com/login" "your_github_token"
```

## 🔧 设置说明

### 1. GitHub Token 创建

1. 访问 GitHub → Settings → Developer settings → Personal access tokens
2. 点击 "Generate new token (classic)"
3. 勾选 `repo` 权限
4. 生成并复制 Token

### 2. 修改配置

在使用前，请修改以下文件中的配置：

#### `build-interface.html`

```javascript
const GITHUB_OWNER = "xieleihan"; // 改为您的GitHub用户名
const GITHUB_REPO = "RouterCompanion"; // 改为您的仓库名
```

#### `build.sh` 和 `build.bat`

```bash
GITHUB_OWNER="xieleihan"  # 改为您的GitHub用户名
GITHUB_REPO="RouterCompanion"    # 改为您的仓库名
```

## 📋 构建流程

1. **接收请求**: Web 界面或命令行接收域名参数
2. **触发 Actions**: 通过 GitHub API 触发 `build-with-domain` 工作流
3. **修改代码**: 自动替换 MainActivity.java 中的域名
4. **版本管理**: 自动递增版本号
5. **构建 APK**: 使用 Gradle 构建 Debug APK
6. **发布 Release**: 创建包含详细信息的 GitHub Release

## 📦 Release 命名规则

发布的 APK 将按以下格式命名：

```
apk-{日期}-{域名}-{哈希}-v{版本号}
```

例如:

```
apk-20241021-example.com-a1b2c3d4-v15
```

## 🗂️ 文件说明

- `.github/workflows/build-with-domain.yml` - GitHub Actions 工作流
- `build-interface.html` - Web 构建界面
- `build.sh` - Linux/macOS 构建脚本
- `build.bat` - Windows 构建脚本
- `version.txt` - 版本号存储文件

## 🔍 故障排除

### 常见问题

1. **构建失败**

   - 检查 GitHub Token 权限
   - 确认仓库名称正确
   - 查看 Actions 页面的详细日志

2. **域名格式错误**

   - 确保包含 `http://` 或 `https://`
   - 检查 URL 格式是否正确

3. **版本号问题**
   - 版本号存储在 `version.txt` 中
   - 如需重置，手动修改该文件

### 查看日志

- 构建日志: https://github.com/{用户名}/{仓库名}/actions
- 发布页面: https://github.com/{用户名}/{仓库名}/releases

## 🔒 安全注意事项

- 不要在公开场合暴露 GitHub Token
- Token 具有仓库完整权限，请妥善保管
- 建议定期更换 Token

## 🎯 API 接口

如需集成到其他系统，可以直接调用 GitHub API：

```bash
curl -X POST \
  -H "Authorization: token YOUR_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Content-Type: application/json" \
  -d '{"event_type":"build-with-domain","client_payload":{"domain":"https://example.com"}}' \
  https://api.github.com/repos/USERNAME/REPO/dispatches
```
