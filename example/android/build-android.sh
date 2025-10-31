#!/bin/bash

# 设置 Node.js 路径
export PATH="/Users/fino/.nvm/versions/node/v20.12.2/bin:$PATH"
export NODE_OPTIONS="--openssl-legacy-provider"

# 构建 Android APK
cd "$(dirname "$0")"
./gradlew assembleDebug