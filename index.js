import { BOOLState, ConfigPriority, LogLevel, FCReLaunchMode, LanguageType, FinStoreConfig, Config, CapsuleConfig, NavHomeConfig, FloatWindowConfig, AuthButtonConfig, AuthViewConfig, UIConfig } from './mop.js';

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
   * @param {string} params.appletText  注入小程序统称appletText字符串，默认为“小程序”。
   * @param {number} params.language  SDK的语言类型，默认为中文，0：中文，1：英文,其他语言得使用 customLanguagePath、localeLanguage属性。具体看文档
   * @param {string} params.customLanguagePath  【iOS属性】自定义SDK的语言，优先级高于内置的 language 属性；示例：如果是放在 mainBundle 下，则设置相对路径：@"abc.lproj"；如果是放在自定于 Bundle 下，则设置相对路径：@"bundleName.bundle/abc.lproj"
   * @param {string} params.localeLanguage 【Android属性】自定义SDK的语言，优先级高于内置的 language 属性；语言列表可以参考：https://uutool.cn/info-i18n/ 或者Java类 【java.util.Locale】;示例：简体中文：zh_CN，繁体中文：zh_TW，英文：en
   * @param {Object} param.nativeEventEmitter eventEmitter 实例
   * @param {Object} param.finMopSDK  nativeModules.FINMopSDK 引用
   * @returns 
   */
  initialize(params) {
    return new Promise((resolve, reject) => {
      let { appkey, secret, apiServer, apiPrefix, userId, language, customLanguagePath, appletText, localeLanguage, nativeEventEmitter, finMopSDK } = params;
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
        appkey, secret, apiServer, apiPrefix, userId, language, customLanguagePath, appletText, localeLanguage
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
   * @description Initialize the SDK with the provided configuration
   * @param {Object} params
   * @param {Config} params.config - Main SDK configuration
   * @param {UIConfig} params.uiConfig - UI related configuration
   * @param {Object} params.finMopSDK 
   * @param {Object} params.nativeEventEmitter 
   * @returns 
   */
  initSDK(params) {
    return new Promise((resolve, reject) => {
      let { config, uiConfig, finMopSDK, nativeEventEmitter } = params;
      MopSDK._finMopSDK = finMopSDK
      // 参数校验
      if (!config || !(config instanceof Config)) {
        reject({ success: false, retMsg: 'config is required and must be class Config' });
        return;
      }
      // uiConfig 可选
      if (uiConfig && !(uiConfig instanceof UIConfig)) {
        reject({ success: false, retMsg: 'uiConfig must be class UIConfig' });
        return;
      }
      const nativeEventEmitterCheck = typeCheck(nativeEventEmitter, 'Object')
      if (!nativeEventEmitterCheck.success) {
        reject(nativeEventEmitterCheck)
        return
      }
      nativeEventEmitter.addListener('EventReminder', (event) => {
        this._extentionApiCallbacks(event)
      })
      // 调用原生模块的 initSDK 方法
      MopSDK._finMopSDK.initSDK({
        config, uiConfig
      }, (data) => {
        data = handleCallbackData(data);
        if (data.success) {
          resolve(data);
        } else {
          reject(data);
        }
      });
    });
  }


  /**
   * 
   * @param {Object} params 
   * @param {string} params.appId 小程序id，必填
   * @param {string} params.path 小程序页面路径
   * @param {string} params.query 传给页面的 query 参数
   * @param {number} params.sequence 小程序索引， 非必填
   * @param {string} params.apiServer 小程序所属服务器地址
   * @param {string} params.scene 进入小程序的场景值
   * @param {string} params.shareDepth  分享层级
   * @param {boolean} params.isSingleProcess 是否使用单进程模式,非必填，默认值为false。仅Android支持
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
      const startParams = { path: params.path, query: params.query }
      params.startParams = startParams
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

/**
 * 
 * @param {Object} params 
 * @param {string} params.appletId 小程序id，必填
 * @param {string} params.apiServer 小程序所属服务器地址，必填
 * @param {number} params.sequence 小程序索引， 非必填
 * @param {Map<string,string>} params.startParams 小程序启动参数，仅支持path 和 query
 * @param {string} params.offlineMiniprogramZipPath 离线小程序压缩包路径，可传入一个本地小程序包路径，加快首次启动速度， 非必填
 * @param {string} params.offlineFrameworkZipPath 离线基础库压缩包路径，可传入一个基础库路径，加快首次启动速度， 非必填
 * @param {boolean} params.animated 是否使用动画，非必填，默认值为true,仅iOS支持
 * @param {TranstionStyle} params.transitionStyle 打开小程序时的转场动画方式,仅iOS支持
 * @param {boolean} params.isSingleProcess 是否使用单进程模式,非必填，默认值为false。仅Android支持
 * @param {FCRelaunchMode} params.reLaunchMode 执行reLaunch的场景
 * @returns
 */
  startApplet(params) {
    return new Promise((resolve, reject) => {
      const { appletId } = params
      const appletIdCheck = typeCheck(appletId, 'String')
      if (!appletIdCheck.success) {
        reject(appletIdCheck)
        return
      }

      MopSDK._finMopSDK.startApplet(params, (params) => {
        params = handleCallbackData(params)
        if (params.success) {
          resolve(params)
        } else {
          reject(params)
        }
      })
    })
  }

  /**
   * 
   * @param {Object} params 
   * @param {String} params.userId 返回当前用户
   * @returns
   */
  changeUserId(params) {
    return new Promise((resolve, reject) => {
      const { userId } = params;
      const userIdCheck = typeCheck(userId, 'String');
      if (!userIdCheck.success) {
        reject(userIdCheck);
        return;
      }

      MopSDK._finMopSDK.changeUserId(params, (params) => {
        params = handleCallbackData(params);
        if (params.success) {
          resolve(params);
        } else {
          reject(params);
        }
      });
    });
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

  removeAllUsedApplets() {
    MopSDK._finMopSDK.removeAllUsedApplets()
  }


  /**
 * 二维码打开小程序
 * @param {string} qrcode 二维码内容，必填
 * @param {boolean} isSingleProcess 是否使用单进程模式,非必填，默认值为false。仅Android支持
 * @param {FCRelaunchMode} reLaunchMode 执行reLaunch的场景
 * @returns
 */
  qrcodeOpenApplet(qrcode, isSingleProcess = false, reLaunchMode = FCReLaunchMode.ParamsExist) {
    const qrcodeCheck = typeCheck(qrcode, 'String')
    if (!qrcodeCheck.success) {
      console.error(qrcodeCheck.retMsg)
      return
    }
    MopSDK._finMopSDK.qrcodeOpenApplet({ qrcode, isSingleProcess, reLaunchMode })
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
      return {}
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

  // 结束所有在运行的小程序
  finishAllRunningApplets() {
    MopSDK._finMopSDK.finishAllRunningApplets()
  }

  // 设置小程序切换动画 安卓
  setActivityTransitionAnim(anim) {
    MopSDK._finMopSDK.setActivityTransitionAnim({ anim })
  }

  // 获取绑定的小程序列表


  /**
   * 
   * @param {Object} params 
   * @param {string} params.apiServer 服务器地址，必填
   * @param {string} params.appClass 小程序的分类，默认为''
   * @param {number} params.appStatus 小程序状态类型，枚举值。0:所有；1：已上架的；2：未上架的；3：已下架的
   * @param {boolean} params.containForbiddenApp 查询结果是否包含被禁用的小程序，默认为false
   * @param {number} params.pageNo 分页查询的页码，默认为1
   * @param {number} params.pageSize 分页的大小，默认为20
   * @returns
   */
  getBindApplets(params) {
    return new Promise((resolve, reject) => {
      const { apiServer } = params
      const apiServerCheck = typeCheck(apiServer, 'String')
      if (!apiServerCheck.success) {
        reject(apiServerCheck)
        return
      }

      MopSDK._finMopSDK.getBindApplets(params, (result) => {
        result = handleCallbackData(result)
        if (result.success) {
          resolve(result)
        } else {
          reject(result)
        }
      })
    })
  }

}


export default new MopSDK();
export { BOOLState, ConfigPriority, LogLevel, FCReLaunchMode, LanguageType, FinStoreConfig, Config, CapsuleConfig, NavHomeConfig, FloatWindowConfig, AuthButtonConfig, AuthViewConfig, UIConfig };

