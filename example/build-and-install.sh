#!/bin/bash

# 设置环境变量
export PATH="/Users/fino/.nvm/versions/node/v20.12.2/bin:$PATH"
export NODE_OPTIONS="--openssl-legacy-provider"

echo "1. 生成 Android bundle..."
npx react-native bundle --platform android --dev false --entry-file index.js --bundle-output android/app/src/main/assets/index.android.bundle --assets-dest android/app/src/main/res

echo "2. 构建 APK..."
cd android
./gradlew assembleDebug

echo "3. 安装到设备..."
adb install app/build/outputs/apk/debug/app-debug.apk

echo "4. 启动应用..."
adb shell am start -n org.reactjs.native.example.reactNativeMopSDKDemo/com.rnsdkdemo.MainActivity

echo "✅ 应用已成功安装并启动！"