import { NativeModules, NativeEventEmitter } from 'react-native';

 
const handleCallbackDate = (params) => {
  console.log('callbackHandler called')
  let result = {}
  if(params instanceof Array) {
    result = JSON.parse(params[0])
  } else {
    console.error('参数错误')
  }

  return result
}


const typeCheck = (data, type) => {
  const TYPE_MAP = {
    '[object String]': 'String',
    '[object Number]': 'Number',
    '[object Boolean]': 'Boolean',
    '[object Null]': 'Null',
    '[object Undefined]': 'Undefined',
    '[object Object]': 'Object'
  }
  if(!Object.values(TYPE_MAP).includes(type)) {
    console.error('error type')
    return 
  }
  const result = TYPE_MAP[Object.prototype.toString.call(data)] === type 
  if(!result) {
    console.error(`params should be ${type}`)
  }
  return result
}

const { FINMopsdk } = NativeModules;


const _handlePlatformMethodCall = async (data) =>  {
  const extensionAPIPrefix = 'extensionApi:'
  const webExtensionApiPrefix = 'webExtentionApi:'
  if(data.apiName.startsWith(extensionAPIPrefix)) {
    let apiName = data.apiName.substring(extensionAPIPrefix.length)
    const handler = MopSDK._extensionApis[apiName]
    if(handler !== null) {
      const res = await handler(data.params)
      FINMopsdk.eventReminderCallback(apiName, res, data.callbackId)
    }
  } else if(data.apiName.startsWith(webExtensionApiPrefix)) {
    let apiName = data.apiName.substring(webExtensionApiPrefix.length)
    const handler = MopSDK._webExtensionApis[apiName]
    if(handler !== null) {
      const res = await handler(data.params)
      FINMopsdk.eventReminderCallback(apiName, res, data.callbackId)
    }
  }
}

const eventEmitter = new NativeEventEmitter(FINMopsdk);
eventEmitter.addListener('EventReminder', (event) => {
  _handlePlatformMethodCall(event)
})

const MopSDK = {
  _extensionApis: {},
  _webExtensionApis: {},
  initialize(params, callback) {
    console.log(params);
    console.log(callback);
    let { appkey, secret, apiServer, apiPrefix } = params;
    if (!appkey || !secret || appkey === '' || secret === '') {
      if (typeof callback === 'function') {
        callback({ succ: false, retMsg: 'appkey、secret不能为空' });
      }
      return;
    }
    if (!apiServer) {
      apiServer = 'https://mp.finogeeks.com';
    }
    if (apiServer.endsWith('/')) {
      apiServer = apiServer.substr(0, apiServer.length() - 1);
    }
    if (!apiPrefix) {
      apiPrefix = '/api/v1/mop';
    }
    if (apiPrefix.endsWith('/')) {
      apiPrefix = apiPrefix.substr(0, apiPrefix.length() - 1);
    }
    FINMopsdk.initialize(appkey, secret, apiServer, apiPrefix, callback);
  },
  openApplet(appId, path, query, callback) {
    FINMopsdk.openApplet(appId, path, query, null, callback);
  },
  // 关闭小程序
  closeApplet(appletId, animated) {
    FINMopsdk.closeApplet({ appletId, animated})
  },
  // 关闭当前打开的所有小程序
  closeAllApplets() {
    FINMopsdk.closeAllApplets()
  },
  // 二维码打开小程序
  qrcodeOpenApplet(qrcode) {
    FINMopsdk.qrcodeOpenApplet({ qrcode })
  },
  // 获取当前正在使用的小程序信息
  currentApplet() {
    return new Promise((resolve, reject) => {
      FINMopsdk.currentApplet({},(params) => {
        params = handleCallbackData(params)
        if(params.sucuess) {
          resolve(params.data)
        } else {
          reject(params)
        }
      })
    })
  },
  // 清除缓存的小程序
  clearApplets() {
    FINMopsdk.clearApplets()
  },
  // 通过二维码打开小程序
  qrcodeOpenApplet(qrcode) {
    FINMopsdk.qrcodeOpenApplet({qrcode})
  },
  // 注册小程序事件处理
  registerAppletHandler(handler) {
    this._extensionApis['forwardApplet'] = (params) => {
      handler.forwardApplet(params)
    }

    this._extensionApis['getUserInfo'] = (params) => {
      return handler.getUserInfo()
    }

    this._extensionApis["getCustomMenus"] = async (params)  =>  {
      const res = await handler.getCustomMenus(params["appId"]);
      let list = res.map(item => {
        return { menuId, image, title, type}
      })
      return list 
    };
    this._extensionApis["onCustomMenuClick"] = (params) => {
      return handler.onCustomMenuClick(
          params["appId"], params["path"], params["menuId"], params["appInfo"]);
    };
    this._extensionApis["appletDidOpen"] = (params) =>  {
      return handler.appletDidOpen(params["appId"]);
    };
    FINMopsdk.registerAppletHandler()
  },
  // 注册webview拓展api
  addWebExtentionApi(name, handler) {
    this._webExtensionApis[name] = handler
    FINMopsdk.addWebExtentionApi({ name })
  },
  // 原生调用webview中的js方法 
  callJS(appId, eventName, nativeViewId, eventData) {
    // todo： 异步
    FINMopsdk.callJS({ appId, eventName, nativeViewId, eventData })
  },
  // 原生发送事件给小程序
  sendCustomEvent(appId, eventData) {
    // todo： 异步
    FINMopsdk.sendCustomEvent({ appId, eventData })
  },
  // 结束小程序
  finishRunningApplet(appletId, animated) {
    FINMopsdk.finishRunningApplet({ appletId, animated })
  },
  // 设置小程序切换动画 安卓
  setActivityTransitionAnim(anim) {
    FINMopsdk.setActivityTransitionAnim({ 'anim': anim.name})
  },

};
export default MopSDK;
