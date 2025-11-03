# React Native 升级总结文档

## 升级概述

本次升级将 react-native-mopsdk 项目从 React Native 0.67.4 升级到 0.76.9，以支持 Xcode 16 和未来的开发需求。

## 升级信息

### 版本变化

| 组件 | 旧版本 | 新版本 |
|------|--------|--------|
| React Native | 0.67.4 | 0.76.9 |
| React | 17.0.2 | 18.3.1 |
| Node.js 最低要求 | 16+ | 18+ |
| iOS最低版本 | 11.0 | 13.4 (podspec) / 15.1 (Podfile) |
| Hermes | 可选 | 默认启用 |
| Gradle | 7.5 | 8.10.2 |
| AGP | 3.5.2 → 3.5.0 | 8.7.3 |
| compileSdkVersion | 33 | 35 |
| targetSdkVersion | 33 | 35 |
| minSdkVersion | 21 | 23 |
| NDK | 23.1.7779620 | 26.1.10909125 |
| Java | 1.8 | 17 |
| Kotlin | 1.3.60 | 1.9.24 |

### 主要特性变化

1. **新架构（New Architecture）**: 默认启用
2. **Hermes引擎**: 默认启用
3. **Flipper**: 已移除（保留调用接口以兼容）
4. **React 18**: 升级支持并发特性

## 升级内容

### 1. Package.json 更新

#### example/package.json
- 升级 React Native 到 0.76.9
- 升级 React 到 18.3.1
- 更新所有 @react-native/* 相关包到 0.76.9
- 更新 Babel、ESLint、Jest 等开发依赖
- 添加 @react-native-community/cli
- 更新 TypeScript 到 5.6.3
- Node.js 引擎要求改为 >=18

#### 根目录 package.json
- peerDependencies: `>=0.76.0 <1.0.x`
- devDependencies: `^0.76.9`

### 2. iOS 配置更新

#### Podfile 主要变化
```ruby
# 使用最新的 RN API
platform :ios, min_ios_version_supported
prepare_react_native_project!

# Hermes 默认启用
:hermes_enabled => true

# 保留 use_flipper!() 调用（空操作）
use_flipper!()

# 更新 post_install 钩子
react_native_post_install(
  installer,
  config[:reactNativePath],
  :mac_catalyst_enabled => false
)
```

#### react-native-mopsdk.podspec
- iOS 最低版本: 9.0 → 13.4

#### AppDelegate.m
- 移除 Flipper 相关导入和初始化代码
- 更新 `jsBundleURLForBundleRoot` API 调用（移除 fallbackResource 参数）

### 3. Android 配置更新

#### example/android/build.gradle
- 升级 Gradle: 7.5 → 8.10.2
- 升级 AGP: 3.5.2 → 8.7.3
- 升级 compileSdkVersion: 33 → 35
- 升级 targetSdkVersion: 33 → 35
- 升级 minSdkVersion: 21 → 23
- 升级 NDK: 23.1.7779620 → 26.1.10909125
- 升级 Kotlin: 1.3.60 → 1.9.24
- 移除 jcenter()，使用 mavenCentral()

#### android/build.gradle (SDK模块)
- 同步上述版本更新
- Java 版本: 1.8 → 17

#### gradle.properties
- 启用新架构: `newArchEnabled=true`

### 4. 脚本修复

#### Pods-example-frameworks.sh
修复了 `ARCHS[@]` 数组展开问题，改为 `${ARCHS}` 以避免在 `set -u` 模式下的错误。

## 编译结果

### ✅ iOS 编译成功
- 使用 Xcode 16 编译通过
- 支持 iOS Simulator (arm64/x86_64)
- 所有依赖正确集成

### ⏸️ Android 编译
- 根据要求暂未处理
- 所有配置已更新完毕，待后续测试

## 已知问题和解决方案

### 1. Flipper 移除
**问题**: React Native 0.76 移除了 Flipper 支持
**解决**:
- Podfile 中添加空的 `use_flipper!()` 函数
- AppDelegate.m 中注释掉 Flipper 相关代码
- 保留接口调用以满足兼容性要求

### 2. RCTBundleURLProvider API 变化
**问题**: `jsBundleURLForBundleRoot:fallbackResource:` 方法不存在
**解决**: 使用新 API `jsBundleURLForBundleRoot:`

### 3. ARCHS 数组变量问题
**问题**: CocoaPods 生成的脚本中 `${ARCHS[@]}` 导致错误
**解决**: 修改为 `${ARCHS}` 移除数组展开语法

## 验证步骤

### iOS 验证
```bash
cd example/ios
pod install
xcodebuild -workspace example.xcworkspace -scheme example -configuration Debug -sdk iphonesimulator build
```
**结果**: ✅ BUILD SUCCEEDED

### Android 验证（待执行）
```bash
cd example/android
./gradlew clean
./gradlew assembleDebug
```

## 兼容性声明

### 支持的开发环境
- **Xcode**: 16.0+
- **Node.js**: 18.0+
- **npm**: 对应 Node.js 版本
- **CocoaPods**: 最新版本
- **Java**: 17+
- **Gradle**: 8.10.2

### 支持的目标平台
- **iOS**: 13.4+ (推荐 15.1+)
- **Android**: API 23+ (Android 6.0+)

## 后续建议

1. ✅ **iOS已完成**: 编译成功，可以正常使用
2. ⏸️ **Android待处理**: 配置已更新，需要后续编译测试
3. 📝 **原生模块适配**: 如果使用了自定义原生模块，需要检查新架构兼容性
4. 🧪 **功能测试**: 升级后需要全面测试现有功能
5. 📚 **新架构迁移**: 考虑逐步迁移到新架构以获得更好性能

## 文档参考

- React Native 0.76 发布说明: https://reactnative.dev/blog/2024/10/23/the-new-architecture-is-here
- 升级助手: https://react-native-community.github.io/upgrade-helper/
- 新架构文档: https://reactnative.dev/docs/the-new-architecture/landing-page

## 升级时间线

- **开始时间**: 2025-10-31
- **完成时间**: 2025-10-31
- **iOS编译验证**: ✅ 成功
- **Android编译验证**: ⏸️ 待执行

---

**注意**: 本次升级主要针对 iOS 平台，Android 平台的配置已更新但未进行编译验证。所有修改均未提交到 git 仓库。
