#!/bin/bash

# 设置环境变量
export PATH="/Users/fino/.nvm/versions/node/v20.12.2/bin:$PATH"
export NODE_OPTIONS="--openssl-legacy-provider"

echo "1. 启动 Metro 服务器..."
npx react-native start --port 8081 &
METRO_PID=$!

echo "2. 等待 Metro 服务器启动..."
sleep 10

echo "3. 构建 iOS 应用..."
cd ios
xcodebuild -workspace example.xcworkspace -scheme example -configuration Debug -destination 'name=Lei' build

if [ $? -eq 0 ]; then
    echo "4. 安装到设备..."
    xcodebuild -workspace example.xcworkspace -scheme example -configuration Debug -destination 'name=Lei' install
    
    echo "✅ iOS 应用已成功构建并安装到设备！"
else
    echo "❌ iOS 构建失败"
fi

# 清理
kill $METRO_PID 2>/dev/null