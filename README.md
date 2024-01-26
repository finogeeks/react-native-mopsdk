<p align="center">
    <a href="https://www.finclip.com?from=github">
    <img width="auto" src="https://www.finclip.com/mop/document/images/logo.png">
    </a>
</p>

<p align="center"> 
    <strong>FinClip React Native SDK</strong></br>
<p>
<p align="center"> 
        本项目提供在 React Native 环境中运行小程序的能力
<p>

<p align="center"> 
	👉 <a href="https://www.finclip.com?from=github">https://www.finclip.com/</a> 👈
</p>

<div align="center">

<a href="#"><img src="https://img.shields.io/badge/%E4%B8%93%E5%B1%9E%E5%BC%80%E5%8F%91%E8%80%85-20000%2B-brightgreen"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%B7%B2%E4%B8%8A%E6%9E%B6%E5%B0%8F%E7%A8%8B%E5%BA%8F-6000%2B-blue"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%B7%B2%E9%9B%86%E6%88%90%E5%B0%8F%E7%A8%8B%E5%BA%8F%E5%BA%94%E7%94%A8-75%2B-yellow"></a>
<a href="#"><img src="https://img.shields.io/badge/%E5%AE%9E%E9%99%85%E8%A6%86%E7%9B%96%E7%94%A8%E6%88%B7-2500%20%E4%B8%87%2B-orange"></a>

<a href="https://www.zhihu.com/org/finchat"><img src="https://img.shields.io/badge/FinClip--lightgrey?logo=zhihu&style=social"></a>
<a href="https://www.finclip.com/blog/"><img src="https://img.shields.io/badge/FinClip%20Blog--lightgrey?logo=ghost&style=social"></a>



</div>

<p align="center">

<div align="center">

[官方网站](https://www.finclip.com/) | [示例小程序](https://www.finclip.com/#/market) | [开发文档](https://www.finclip.com/mop/document/) | [部署指南](https://www.finclip.com/mop/document/introduce/quickStart/cloud-server-deployment-guide.html) | [SDK 集成指南](https://www.finclip.com/mop/document/introduce/quickStart/intergration-guide.html) | [API 列表](https://www.finclip.com/mop/document/develop/api/overview.html) | [组件列表](https://www.finclip.com/mop/document/develop/component/overview.html) | [隐私承诺](https://www.finclip.com/mop/document/operate/safety.html)

</div>

-----
## 🤔 FinClip 是什么?

有没有**想过**，开发好的微信小程序能放在自己的 APP 里直接运行，只需要开发一次小程序，就能在不同的应用中打开它，是不是很不可思议？

有没有**试过**，在自己的 APP 中引入一个 SDK ，应用中不仅可以打开小程序，还能自定义小程序接口，修改小程序样式，是不是觉得更不可思议？

这就是 FinClip ，就是有这么多不可思议！

## ⚙️ 配置环境

`$ npm install react-native-mopsdk --save`

## 🖥 自动安装

`$ react-native link react-native-mopsdk`

## 🔨 使用方法
```javascript
import MopSDK from 'react-native-mopsdk';
import { NativeModules, NativeEventEmitter } from 'react-native';

// 初始化
const eventEmitter = new NativeEventEmitter(NativeModules.FINMopSDK);
  MopSDK.initialize({
    appkey:
      'Ev7QHvml1UcW98Y1GaLfRz34ReffbDESaTXbCoFyKhEm0a3gam0elOOOdZ6Twpa3HkBzlvOwJ2cyhOrMVWuuGw==',
    secret: '16f2d2700453ae51',
    apiServer: 'https://api.finclip.com',
    apiPrefix: '/api/v1/mop/',
    nativeEventEmitter: eventEmitter,
    finMopSDK: NativeModules.FINMopSDK,
  }).then(res => {
    console.log('初始化成功')
  }).catch(err => {
    console.log('初始化失败')
  })

```
## 🔨 使用方法
```javascript
import MopSDK from 'react-native-mopsdk';
import { NativeModules, NativeEventEmitter } from 'react-native';

/**
 * @description Initialize the SDK with specific configurations
 * @param {Object} params - Configuration parameters
 */
MopSDK.initSDK = function(params) {
  return new Promise((resolve, reject) => {
    // 这里假设 params 中已经包含了 config 和 uiConfig
    let { config, uiConfig } = params;

    // 确保 config 和 uiConfig 是有效的对象
    if (!config || typeof config !== 'object' || !uiConfig || typeof uiConfig !== 'object') {
      reject(new Error('Invalid config or uiConfig'));
      return;
    }

    // 调用原生模块的 initSDK 方法
    NativeModules.FINMopSDK.initSDK({ config, uiConfig }).then(res => {
      console.log('SDK 初始化成功');
      resolve(res);
    }).catch(err => {
      console.error('SDK 初始化失败', err);
      reject(err);
    });
  });
};

// 使用示例
const eventEmitter = new NativeEventEmitter(NativeModules.FINMopSDK);
MopSDK.initSDK({
  config: {
    appkey: '您的appkey',
    secret: '您的secret',
    apiServer: 'https://api.finclip.com',
    apiPrefix: '/api/v1/mop/',
    // 其他相关的配置项
  },
  uiConfig: {
    // UI 相关配置
  }
}).then(res => {
  console.log('初始化成功');
}).catch(err => {
  console.log('初始化失败', err);
});

```

## 📱 DEMO
[点击这里](https://github.com/finogeeks/finclip-react-native-demo) 查看 React Native Demo

## 📋 接口文档
[点击这里](https://www.finclip.com/mop/document/runtime-sdk/reactNative/rn-integrate.html) 查看 React Native 快速集成文档

## 🔗 常用链接
以下内容是您在 FinClip 进行开发与体验时，常见的问题与指引信息

- [FinClip 官网](https://www.finclip.com/#/home)
- [示例小程序](https://www.finclip.com/#/market)
- [文档中心](https://www.finclip.com/mop/document/)
- [SDK 部署指南](https://www.finclip.com/mop/document/introduce/quickStart/intergration-guide.html)
- [小程序代码结构](https://www.finclip.com/mop/document/develop/guide/structure.html)
- [iOS 集成指引](https://www.finclip.com/mop/document/runtime-sdk/ios/ios-integrate.html)
- [Android 集成指引](https://www.finclip.com/mop/document/runtime-sdk/android/android-integrate.html)
- [Flutter 集成指引](https://www.finclip.com/mop/document/runtime-sdk/flutter/flutter-integrate.html)

## ☎️ 联系我们
微信扫描下面二维码，关注官方公众号 **「凡泰极客」**，获取更多精彩内容。<br>
<img width="150px" src="https://www.finclip.com/mop/document/images/ic_qr.svg">

微信扫描下面二维码，加入官方微信交流群，获取更多精彩内容。<br>
<img width="150px" src="https://www-cdn.finclip.com/images/qrcode/qrcode_shequn_text.png">
