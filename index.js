const handleCallbackData = (params) => {
  let result = {}
  if (params instanceof Array) {
    result = params[0]
    if (typeof params[0] === 'string') {
      result = JSON.parse(params[0])
    }
  } else if (params instanceof Object) {
    result = params
  } else if (typeof params === 'string') {
    result = JSON.parse(params)
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
  if (!Object.values(TYPE_MAP).includes(type)) {
    return { success: false, retMsg: 'error type' }
  }
  const result = TYPE_MAP[Object.prototype.toString.call(data)] === type
  if (!result) {
    return { success: false, retMsg: `params should be ${type}` }
  }
  return { success: true, retMsg: '' }
}

class MopSDK {
  constructor() {
    console.log('REACT NATIVE SDK')
  }
  static _extensionApis = {}
  static _webExtensionApis = {}
  static _finMopSDK = {}

  async _extentionApiCallbacks(event) {
    const extensionAPIPrefix = 'extensionApi:'
    const webExtensionApiPrefix = 'webExtentionApi:'

    if (event.apiName.startsWith(extensionAPIPrefix)) {
      let apiName = event.apiName.substring(extensionAPIPrefix.length)
      const handler = MopSDK._extensionApis[apiName]
      if (handler !== null) {
        const res = await handler(event.params)
        console.warn("CUSTOM API 调用的结果", res)
        MopSDK._finMopSDK.eventReminderCallback(apiName, res, event.callbackId)
      }
    } else if (event.apiName.startsWith(webExtensionApiPrefix)) {
      let apiName = event.apiName.substring(webExtensionApiPrefix.length)
      const handler = MopSDK._webExtensionApis[apiName]
      if (handler !== null) {
        const res = await handler(event.params)
        console.warn("WEB CUSTOM API 调用的结果", res)
        MopSDK._finMopSDK.eventReminderCallback(apiName, res, event.callbackId)
      }
    }
  }

  /**
   * @description 
   * @param {Object} params 
   * @param {string} params.appkey 
   * @param {string} params.secret 
   * @param {string} params.apiServer 
   * @param {string} params.apiPrefix 
   * @param {string} params.userId 
   * @param {number} params.language  SDK的语言类型，默认为中文，0：中文，1：英文
   * @param {string} params.customLanguagePath  【iOS属性】自定义SDK的语言，优先级高于内置的 language 属性；示例：如果是放在 mainBundle 下，则设置相对路径：@"abc.lproj"；如果是放在自定于 Bundle 下，则设置相对路径：@"bundleName.bundle/abc.lproj"
   * @param {string} params.localeLanguage 【Android属性】自定义SDK的语言，优先级高于内置的 language 属性；语言列表可以参考：https://uutool.cn/info-i18n/ 或者Java类 【java.util.Locale】;示例：简体中文：zh_CN，繁体中文：zh_TW，英文：en
   * @param {Object} param.nativeEventEmitter eventEmitter 实例
   * @param {Object} param.finMopSDK  nativeModules.FINMopSDK 引用
   * @returns 
   */
  initialize(params) {
    return new Promise((resolve, reject) => {
      let { appkey, secret, apiServer, apiPrefix, userId, language, customLanguagePath, localeLanguage, nativeEventEmitter, finMopSDK } = params;
      MopSDK._finMopSDK = finMopSDK
      const appKeyCheck = typeCheck(appkey, 'String')
      const secretCheck = typeCheck(secret, 'String')
      const nativeEventEmitterCheck = typeCheck(nativeEventEmitter, 'Object')
      if (!appKeyCheck.success) {
        reject(appKeyCheck)
        return
      }
      if (!secretCheck.success) {
        reject(secretCheck)
        return
      }

      if (!nativeEventEmitterCheck.success) {
        reject(nativeEventEmitterCheck)
        return
      }

      if (appkey === '' || secret === '') {
        reject({ success: false, retMsg: 'appkey, secret 不能为空' })
      }

      if (!apiServer) {
        apiServer = 'https://mp.finogeeks.com';
      }
      if (apiServer && apiServer.endsWith('/')) {
        apiServer = apiServer.substr(0, apiServer.length - 1);
      }
      if (!apiPrefix) {
        apiPrefix = '/api/v1/mop';
      }
      if (apiPrefix && apiPrefix.endsWith('/')) {
        apiPrefix = apiPrefix.substr(0, apiPrefix.length - 1);
      }
      params.apiPrefix = apiPrefix
      params.apiServer = apiServer
      params.userId = userId
      nativeEventEmitter.addListener('EventReminder', (event) => {
        this._extentionApiCallbacks(event)
      })
      MopSDK._finMopSDK.initialize({
        appkey, secret, apiServer, apiPrefix, userId, language, customLanguagePath, localeLanguage
      }, (data) => {
        data = handleCallbackData(data)
        if (data.success) {
          resolve(data)
        } else {
          reject(data)
        }
      });
    })
  }

  /**
   * 
   * @param {Object} params 
   * @param {String} params.appId
   * @param {String} params.path
   * @param {String} params.query
   * @param {Number} params.sequence
   * @param {String} params.apiServer
   * @param {String} params.scene
   * @returns
   */
  openApplet(params) {
    return new Promise((resolve, reject) => {
      const { appId } = params
      const appIdCheck = typeCheck(appId, 'String')
      if (!appIdCheck.success) {
        reject(appIdCheck)
        return
      }

      MopSDK._finMopSDK.openApplet(params, (params) => {
        params = handleCallbackData(params)
        if (params.success) {
          resolve(params)
        } else {
          reject(params)
        }
      })
    })
  }
  // 关闭小程序
  closeApplet(appletId, animated) {
    const appletIdCheck = typeCheck(appletId, 'String')
    const animatedCheck = typeCheck(animated, 'Boolean')
    if (!appletIdCheck.success) {
      console.error(appletIdCheck.retMsg)
      return
    }

    if (!animatedCheck) {
      console.error(appletIdCheck.retMsg)
      return
    }
    MopSDK._finMopSDK.closeApplet({ appletId, animated })
  }
  // 关闭当前打开的所有小程序
  closeAllApplets() {
    MopSDK._finMopSDK.closeAllApplets()
  }
  // 二维码打开小程序
  qrcodeOpenApplet(qrcode) {
    const qrcodeCheck = typeCheck(qrcode, 'String')
    if (!qrcodeCheck.success) {
      console.error(qrcodeCheck.retMsg)
      return
    }
    MopSDK._finMopSDK.qrcodeOpenApplet({ qrcode })
  }
  // 获取当前正在使用的小程序信息
  currentApplet() {
    return new Promise((resolve, reject) => {
      MopSDK._finMopSDK.currentApplet({}, (params) => {
        params = handleCallbackData(params)
        if (params.success) {
          resolve(params)
        } else {
          reject(params)
        }
      })
    })
  }
  // 清除缓存的小程序
  clearApplets() {
    MopSDK._finMopSDK.clearApplets()
  }
  // 注册小程序事件处理
  registerAppletHandler(handler) {
    MopSDK._extensionApis['forwardApplet'] = (params) => {
      handler.forwardApplet(params)
    }

    MopSDK._extensionApis['getUserInfo'] = (params) => {
      return handler.getUserInfo(params)
    }

    MopSDK._extensionApis["getCustomMenus"] = async (params) => {
      const res = await handler.getCustomMenus(params["appId"]);
      let list = res.map(item => {
        return {
          menuId: item.menuId,
          image: item.image,
          title: item.title,
          type: item.type
        }
      })
      return list
    };
    MopSDK._extensionApis["onCustomMenuClick"] = (params) => {
      return handler.onCustomMenuClick(
        params["appId"], params["path"], params["menuId"], params["appInfo"]);
    };
    MopSDK._extensionApis["appletDidOpen"] = (params) => {
      return handler.appletDidOpen(params["appId"]);
    };
    MopSDK._finMopSDK.registerAppletHandler()

  }
  // 注册webview拓展api
  addWebExtentionApi(name, handler) {
    MopSDK._webExtensionApis[name] = handler
    MopSDK._finMopSDK.addWebExtentionApi({ name })
  }

  registerExtensionApi(name, handler) {
    MopSDK._extensionApis[name] = handler;
    MopSDK._finMopSDK.registerExtensionApi({ name })
  }

  // 原生调用webview中的js方法 
  callJS(appId, eventName, eventData, nativeViewId) {
    const appIdCheck = typeCheck(appId, 'String')
    const eventNameCheck = typeCheck(eventName, 'String')

    if (!appIdCheck.success) {
      console.error(appIdCheck.retMsg)
      return Promise.reject()
    }

    if (!eventNameCheck.success) {
      console.error(eventNameCheck.retMsg)
      return Promise.reject()
    }
    return new Promise((resolve, reject) => {
      MopSDK._finMopSDK.callJS({ appId, eventName, nativeViewId, eventData }, (params) => {
        params = handleCallbackData(params)
        if (params.success) {
          resolve(params)
        } else {
          reject(params)
        }
      })
    })
  }
  // 原生发送事件给小程序
  sendCustomEvent(appId, eventData) {
    const appIdCheck = typeCheck(appId, 'String')
    if (!appIdCheck.success) {
      console.error(appIdCheck.retMsg)
      return
    }
    MopSDK._finMopSDK.sendCustomEvent({ appId, eventData })
  }
  // 结束小程序
  finishRunningApplet(appletId, animated) {
    const appletIdCheck = typeCheck(appletId, 'String')
    if (!appletIdCheck.success) {
      console.error(appletIdCheck.retMsg)
      return
    }
    MopSDK._finMopSDK.finishRunningApplet({ appletId, animated })
  }
  // 设置小程序切换动画 安卓
  setActivityTransitionAnim(anim) {
    MopSDK._finMopSDK.setActivityTransitionAnim({ anim })
  }
}


export default new MopSDK();
