# 📱 KidStudyLauncher - 儿童AI学习桌面

> 一款专为儿童设计的 Android 学习启动器，提供受控、安全的学习环境

![Android](https://img.shields.io/badge/Android-8.0%2B-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.8-blue)
![API](https://img.shields.io/badge/API-26%2B-important)

---

## 📖 项目简介

KidStudyLauncher 是一款儿童学习启动器应用，旨在为孩子提供一个安全、专注的学习环境。家长可以控制孩子能够访问的应用，并集成豆包 AI 提供智能学习辅导功能。

### 🎯 核心功能

| 功能 | 描述 |
|------|------|
| **儿童桌面** | 替代系统默认启动器，仅显示家长允许的应用 |
| **应用白名单** | 家长可自由勾选允许儿童使用的应用 |
| **AI 智能辅导** | 集成豆包 AI，提供儿童友好的学习问答 |
| **家长密码保护** | 通过密码验证进入家长控制模式 |
| **沉浸式全屏** | 隐藏状态栏和导航栏，防止误操作 |
| **音量键唤醒** | 连续按5次音量键可唤醒密码验证 |
| **TTS 语音朗读** | AI 回答支持语音播报 |

---

## 🏗️ 项目结构

```
app/src/main/java/com/kidstudy/launcher/
├── ui/
│   ├── KidLauncherActivity.kt      # 儿童桌面主界面
│   ├── ParentControlActivity.kt     # 家长控制/白名单设置
│   ├── PasswordVerifyActivity.kt    # 密码验证
│   ├── SetPasswordActivity.kt       # 设置密码
│   └── AITutorActivity.kt           # AI辅导界面
├── service/
│   ├── KioskAccessibilityService.kt # 无障碍服务(拦截按键)
│   └── AppMonitorService.kt         # 应用监控服务
└── utils/
    ├── Constants.kt                 # 常量定义
    └── ServiceUtils.kt              # 服务工具类
```

---

## 🚀 快速开始

### 环境要求

- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **Kotlin**: 1.8+
- **Gradle**: 8.0+
- **最低 SDK**: API 26 (Android 8.0)
- **目标 SDK**: API 33 (Android 13)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-username/KidStudyLauncher.git
   cd KidStudyLauncher
   ```

2. **配置豆包 AI API Key**

   在 `AITutorActivity.kt` 中替换 API Key：
   ```kotlin
   // TODO: 替换为你的豆包API Key
   private val API_KEY = "你的豆包API_Key"
   ```

   获取 API Key：
   - 访问 [火山引擎控制台](https://console.volcengine.com/)
   - 开通豆包大模型服务
   - 获取 API Key

3. **构建项目**
   ```bash
   ./gradlew build
   ```

4. **安装到设备**
   ```bash
   ./gradlew installDebug
   ```

---

## ⚙️ 配置说明

### 权限说明

应用需要以下权限才能正常运行：

| 权限 | 用途 |
|------|------|
| `BIND_ACCESSIBILITY_SERVICE` | 无障碍服务，拦截系统按键 |
| `PACKAGE_USAGE_STATS` | 应用使用情况统计 |
| `SYSTEM_ALERT_WINDOW` | 悬浮窗权限 |
| `WRITE_SETTINGS` | 系统设置权限 |
| `FOREGROUND_SERVICE` | 前台服务 |
| `INTERNET` | 网络访问（AI API） |

### 首次使用配置

1. **设置家长密码**
   - 进入应用后，点击"设置密码"
   - 设置4-6位数字密码

2. **配置应用白名单**
   - 点击"保存白名单"
   - 勾选允许儿童使用的应用

3. **开启无障碍服务**
   - 系统会自动跳转到无障碍设置页面
   - 开启"KidStudyLauncher"无障碍服务

4. **设置默认启动器**
   - 按Home键时选择"KidStudyLauncher"
   - 勾选"始终使用"

---

## 🎨 自定义

### 修改主题色

编辑 `app/src/main/res/values/colors.xml`:

```xml
<color name="purple_500">#FF6200EE</color>  <!-- 主色调 -->
<color name="kid_bg">#FFF5F8FF</color>      <!-- 桌面背景 -->
```

### 修改 AI 系统提示词

编辑 `AITutorActivity.kt` 中的 `buildRequestBody()` 方法:

```kotlin
put("system_message", "你是一个儿童学习辅导老师...")
```

---

## 📦 依赖库

| 库 | 版本 | 用途 |
|----|------|------|
| AndroidX Core KTX | 1.12.0 | Android 核心库 |
| Material Design | 1.11.0 | UI 组件 |
| PermissionX | 1.7.1 | 权限请求 |
| OkHttp | 4.12.0 | 网络请求 |
| Gson | 2.10.1 | JSON 解析 |
| Kotlin Coroutines | 1.7.3 | 协程支持 |

---

## 🔒 安全机制

| 机制 | 说明 |
|------|------|
| 无障碍服务拦截 | 拦截返回键、Home键等系统操作 |
| 应用白名单过滤 | 只允许运行家长指定的应用 |
| 沉浸式全屏模式 | 隐藏状态栏和导航栏 |
| 音量键唤醒 | 连续按5次音量键唤醒家长验证 |
| 密码保护 | 所有家长操作需要密码验证 |

---

## 🐛 已知问题

- [ ] 豆包 AI 需要网络连接才能使用
- [ ] 首次安装需要手动授予多个权限
- [ ] 某些系统应用可能无法被拦截

---

## 🛣️ 路线图

- [ ] 支持学习时长统计
- [ ] 添加更多 AI 互动功能（语音输入、图片识别）
- [ ] 支持家长远程控制
- [ ] 学习报告生成
- [ ] 多孩子配置支持

---

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📞 联系方式

- **作者**: Qiupengchao
- **邮箱**: your-email@example.com
- **项目链接**: https://github.com/your-username/KidStudyLauncher

---

## 🙏 致谢

- [豆包AI](https://www.doubao.com/) - 提供智能对话能力
- [PermissionX](https://github.com/guolindev/PermissionX) - 简化权限请求
- [Material Design](https://material.io/) - 设计规范

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star！**

</div>
