// Description: mop.js
// SDK各项配置的类

import { Platform } from 'react-native';

function isAndroid() { return Platform.OS === 'android'; }

const BOOLState = {
  // 未设置
  BOOLStateUndefined: 0,
  // 所有版本强制开启vconsole，且不可调api关闭，更多面板不展示打开、关闭调试菜单
  BOOLStateTrue: 1,
  // 正式版更多面板不展示打开、关闭调试菜单；非正式版更多面板展示打开、关闭调试菜单；所有版本均可调setEnableDebug开启vconsole
  BOOLStateFalse: 2,
  // 所有版本强制关闭vconsole，且不可调api开启，多面板不展示打开、关闭调试菜单
  BOOLStateForbidden: 3
};

const FCReLaunchMode = {
  // 只要有启动参数，就会执行reLaunch
  ParamsExist: 0,
  // 有启动参数并且启动参数与上次不同时，才执行reLaunch
  OnlyParamsDiff: 1,
  // 每次热启动，都执行reLaunch
  Always: 2,
  // 每次热启动均不执行reLaunch，直接复用上次的页面栈
  Never: 3
};

const ConfigPriority = {
  // 全局配置优先
  ConfigGlobalPriority: 0,
  // 单个配置优先
  ConfigSpecifiedPriority: 1,
  // 小程序配置文件优先，小程序app.ext.json文件中配置
  ConfigAppletFilePriority: 2
};

const LogLevel = {
  // 设置为该等级，将会记录ERROR级别的日志
  LEVEL_ERROR: 0,
  // 设置为该等级，将会记录ERROR和WARNING级别的日志
  LEVEL_WARNING: 1,
  // 设置为该等级，将会记录ERROR、WARNING和INFO级别的日志
  LEVEL_INFO: 2,
  // 设置为该等级，将会记录ERROR、WARING、INFO和DEBUG级别的日志
  LEVEL_DEBUG: 3,
  // 设置为该等级，将会记录ERROR、WARING、INFO、DEBUG和VERBOSE级别的日志
  LEVEL_VERBOSE: 4,
  LEVEL_NONE: 5
}

const LanguageType = {
  // 中文
  Chinese: 0,
  // 英文    
  English: 1
}

const TranstionStyle = {
  // 页面从下往上弹出，类似present效果
  TranstionStyleUp: 0,
  // 页面从右往左弹出，类似push效果
  TranstionStylePush: 1,
};

class FinStoreConfig {
  /**
   * @param {string} sdkKey - 创建应用时生成的SDK Key
   * @param {string} sdkSecret - 创建应用时生成的SDK secret
   * @param {string} apiServer - 服务器地址，客户部署的后台地址
   * @param {Object} options - 可选参数
   * @param {string} options.apmServer - apm统计服务器的地址,如果不填，则默认与apiServer一致
   * @param {string} options.cryptType - 网络接口加密类型，默认为MD5 国密SM
   * @param {string} options.fingerprint - SDK指纹 证联环境(https://open.fdep.cn/) 时必填，其他环境的不用填
   * @param {boolean} options.encryptServerData - 是否需要接口加密验证（初始化多服务器时使用）默认为不开启，当设置为true时开启，接口返回加密数据并处理
   * @param {boolean} options.enablePreloadFramework - 是否开启预加载基础库,默认开启
   */
  constructor(sdkKey, sdkSecret, apiServer, {
    apmServer = null,
    cryptType = "MD5",
    fingerprint = null,
    encryptServerData = false,
    enablePreloadFramework = true
  } = {}) {
    this.sdkKey = sdkKey;
    this.sdkSecret = sdkSecret;
    this.apiServer = apiServer;
    this.apmServer = apmServer;
    this.cryptType = cryptType;
    this.fingerprint = fingerprint;
    this.encryptServerData = encryptServerData;
    this.enablePreloadFramework = enablePreloadFramework;
  }
}


class Config {
  /**
  * @param {Array<FinStoreConfig>} finStoreConfigs - 要初始化的服务器配置对象列表
  * @param {Object} options - 可选参数
  * @param {string} options.userId - 当前用户id，对应管理后台的用户管理->成员管理->用户id。 若体验版本配置了体验成员，则需要设置正确的userId才能具备打开小程序的权限。登录/切换用户/退出登录时，需要修改此值。小程序缓存信息会存储在以userId命名的不同目录下。  
  * @param {string} options.productIdentification - 产品的标识，非必传，默认为存储目录里的finclip，finogeeks和userAgent里的finogeeks
  * @param {boolean} options.disableRequestPermissions - 是否不让SDK申请权限;如果设置为true，则SDK内使用权限的api，不会主动申请权限
  * @param {boolean} options.appletAutoAuthorize - 小程序自动申请授权;如果设置为true，则小程序申请权限时不会弹出用户确认提示框
  * @param {boolean} options.disableGetSuperviseInfo - 是否禁用SDK的监管接口API（默认开启：false）;如果设置为true，则SDK禁用监管接口API
  * @param {boolean} options.ignoreWebviewCertAuth - 是否忽略webview的证书校验，默认为false,进行校验;如果设置为true，则忽略校验Https的证书
  * @param {number} options.appletIntervalUpdateLimit - 后台自动检查更新的小程序个数;初始化SDK成功后，如处于wifi网络下，更新最近使用的x个小程序;取值范围：0~50。0代表不检查更新；不设置默认是3。
  * @param {Map<string,string>} options.apmExtendInfo - apm 统计的扩展信息
  * @param {boolean} options.startCrashProtection - 【iOS属性】 是否开启Crash防崩溃，默认为false。;如果开启，可以防止如下类型的崩溃：UnrecognizedSelector、KVO、Notification、Timer、Container(数组越界，字典插入nil等)、String (越界、nil等);如果在开发阶段，建议关闭该属性，否则开发时不便于及时发现代码中的崩溃问题。
  * @param {boolean} options.enableApmDataCompression - 数据上报时，是否压缩请求的数据;默认为false
  * @param {boolean} options.encryptServerData - 是否需要接口加密验证（初始化单服务器时使用）;默认为不开启，当设置为true时开启，接口返回加密数据并处理
  * @param {number} options.appletDebugMode - 是否开启小程序的debug模式。默认为BOOLStateUndefined，此时为旧版通过app.json 中 debug:true 开启vconsole。当设置为BOOLStateTrue时，强制所有的小程序都会开启vconsole。当设置为BOOLStateFalse时，非正式版会在更多菜单里显示打开和关闭调试的菜单。当设置为BOOLStateForbidden时，所有版本强制关闭vconsole，且不可调api开启，多面板不展示打开、关闭调试菜单
  * @param {boolean} options.enableWatermark - 是否显示水印
  * @param {number} options.watermarkPriority - 显示水印优先级设置，默认全局配置优先
  * @param {string} options.baseLoadingViewClass - 【iOS属性】 小程序的自定义启动加载页，非必填。自定义启动加载页必须继承自FATBaseLoadingView。注意：swift中的类名带有命名空间，需要在前拼接项目文件名，如：“SwiftDemo.FCloadingView”。其中SwiftDemo是项目名，FCloadingView是类名
  * @param {string} options.baseLoadFailedViewClass -  [iOS属性] 小程序的自定义启动失败页，非必填。自定义启动失败页必须继承自FATBaseLoadFailedView。注意：swift中的类名带有命名空间，需要在前拼接项目文件名，如：“SwiftDemo.FCloadingView”。其中SwiftDemo是项目名，FCloadingView是类名
  * @param {Map<string,string>} options.header - 统一设置小程序中网络请求的header。注意，如果小程序调用api时也传递了相同的key，则会用小程序传递的参数覆盖。对ft.request、ft.downloadFile、ft.uploadFile均会生效
  * @param {number} options.headerPriority - header优先级设置，默认全局配置优先
  * @param {boolean} options.enableH5AjaxHook - 【iOS属性】 是否开启小程序中加载的H5页面hook功能，非必填。如果宿主app 拦截了http 或https，会导致H5中的request 丢失body。我们SDK为了兼容这一问题，会hook request请求;在发起请求之前，先将body中的参数，通过代理方法传递给宿主App。宿主App可自行存储每个request的body，然后在自定义的URLProtocol里发起请求之前，组装上body内容。
  * @param {string} options.h5AjaxHookRequestKey - 【iOS属性】 开启enableH5AjaxHook后，会hook request请求，会在原request 的url 后拼上一个FinClipHookBridge-RequestId=xxx的参数。
  * @param {number} options.pageCountLimit - 小程序中页面栈的最大限制。默认值为0，标识不限制。例如，设置为5，则表示页面栈中最多可有5个页面。从主页最多可再navigateTo 4 层页面
  * @param {Array<string>} options.schemes - 自定义的scheme数组
  * @param {boolean} options.debug - 设置debug模式，影响调试和日志。
  * @param {number} options.maxRunningApplet - 【Android属性】 设置最大同时运行小程序个数
  * @param {number} options.webViewMixedContentMode - 【Android属性】 WebView mixed content mode
  * @param {boolean} options.bindAppletWithMainProcess - 【Android属性】小程序与app进程绑定，App被杀死，小程序同步关闭
  * @param {string} options.killAppletProcessNotice -【Android属性】 App被杀后关闭小程序的提示文案
  * @param {number} options.minAndroidSdkVersion -【Android属性】 最低支持的Android SDK版本
  * @param {boolean} options.enableScreenShot -【Android属性】 是否允许截屏录屏，默认允许
  * @param {number} options.screenShotPriority -【Android属性】 截屏录屏配置项的优先级，默认GLOBAL
  * @param {number} options.logLevel - 日志记录等级
  * @param {number} options.logMaxAliveSec - 日志文件最长缓存时间，单位秒。最小不能小于1天，即不能小于 1 * 24 * 60 * 60 秒。
  * @param {string} options.logDir - XLog日志文件路径
  * @param {boolean} options.enablePreNewProcess - 【Android属性】是否提前创建进程
  * @param {number} options.language - SDK的语言类型，默认为中文
  * @param {string} options.customLanguagePath - 【iOS属性】自定义SDK的语言，优先级高于内置的 language 属性。示例：如果是放在 mainBundle 下，则设置相对路径：@"abc.lproj";如果是放在自定于 Bundle 下，则设置相对路径：@"bundleName.bundle/abc.lproj"
  * @param {string} options.localeLanguage - 【Android属性】自定义SDK的语言，优先级高于内置的 language 属性。语言列表可以参考：https://uutool.cn/info-i18n/ 或者Java类 【java.util.Locale】;示例：简体中文：zh_CN，繁体中文：zh_TW，英文：en
  * @param {boolean} options.useLocalTbsCore - 【Android属性】是否使用本地加载tbs内核
  * @param {string} options.tbsCoreUrl - 【Android属性】tbs内核的下载地址，不包含文件名
  * @param {boolean} options.enableJ2V8 - 【Android属性】是否开启j2v8
  * @param {number} options.backgroundFetchPeriod - 周期性更新的时间间隔(小时), 设置为0不会发起周期性更新请求，接收设置范围为3-12小时
  */
  constructor(finStoreConfigs, {
    userId = null,
    productIdentification = null,
    disableRequestPermissions = false,
    appletAutoAuthorize = false,
    disableGetSuperviseInfo = false,
    ignoreWebviewCertAuth = false,
    appletIntervalUpdateLimit = 3,
    apmExtendInfo = null,
    startCrashProtection = false,
    enableApmDataCompression = false,
    encryptServerData = false,
    appletDebugMode = BOOLState.BOOLStateUndefined,
    enableWatermark = false,
    watermarkPriority = ConfigPriority.ConfigGlobalPriority,
    baseLoadingViewClass = null,
    baseLoadFailedViewClass = null,
    header = null,
    headerPriority = ConfigPriority.ConfigGlobalPriority,
    enableH5AjaxHook = false,
    h5AjaxHookRequestKey = null,
    pageCountLimit = 0,
    schemes = null,
    debug = false,
    maxRunningApplet = null,
    webViewMixedContentMode = null,
    bindAppletWithMainProcess = false,
    killAppletProcessNotice = null,
    minAndroidSdkVersion = 21,
    enableScreenShot = true,
    screenShotPriority = ConfigPriority.ConfigGlobalPriority,
    logLevel = LogLevel.LEVEL_NONE,
    logMaxAliveSec = null,
    logDir = null,
    enablePreNewProcess = true,
    language = LanguageType.Chinese,
    customLanguagePath = null,
    localeLanguage = null,
    useLocalTbsCore = false,
    tbsCoreUrl = null,
    enableJ2V8 = false,
    backgroundFetchPeriod = 12
  } = {}) {
    this.finStoreConfigs = finStoreConfigs;
    this.userId = userId;
    this.productIdentification = productIdentification;
    this.disableRequestPermissions = disableRequestPermissions;
    this.appletAutoAuthorize = appletAutoAuthorize;
    this.disableGetSuperviseInfo = disableGetSuperviseInfo;
    this.ignoreWebviewCertAuth = ignoreWebviewCertAuth;
    this.appletIntervalUpdateLimit = appletIntervalUpdateLimit;
    this.apmExtendInfo = apmExtendInfo;
    this.startCrashProtection = startCrashProtection;
    this.enableApmDataCompression = enableApmDataCompression;
    this.encryptServerData = encryptServerData;
    this.appletDebugMode = appletDebugMode;
    this.enableWatermark = enableWatermark;
    this.watermarkPriority = watermarkPriority;
    this.baseLoadingViewClass = baseLoadingViewClass;
    this.baseLoadFailedViewClass = baseLoadFailedViewClass;
    this.header = header;
    this.headerPriority = headerPriority;
    this.enableH5AjaxHook = enableH5AjaxHook;
    this.h5AjaxHookRequestKey = h5AjaxHookRequestKey;
    this.pageCountLimit = pageCountLimit;
    this.schemes = schemes;
    this.debug = debug;
    this.maxRunningApplet = maxRunningApplet;
    this.webViewMixedContentMode = webViewMixedContentMode;
    this.bindAppletWithMainProcess = bindAppletWithMainProcess;
    this.killAppletProcessNotice = killAppletProcessNotice;
    this.minAndroidSdkVersion = minAndroidSdkVersion;
    this.enableScreenShot = enableScreenShot;
    this.screenShotPriority = screenShotPriority;
    this.logLevel = logLevel;
    this.logMaxAliveSec = logMaxAliveSec;
    this.logDir = logDir;
    this.enablePreNewProcess = enablePreNewProcess;
    this.language = language;
    this.customLanguagePath = customLanguagePath;
    this.localeLanguage = localeLanguage;
    this.useLocalTbsCore = useLocalTbsCore;
    this.tbsCoreUrl = tbsCoreUrl;
    this.enableJ2V8 = enableJ2V8;
    this.backgroundFetchPeriod = backgroundFetchPeriod;
  }
}

/**
 * 胶囊按钮配置
 */
class CapsuleConfig {
  /**
   * @param {Object} options - 可选参数
   * @param {number} options.capsuleWidth - 上角胶囊视图的宽度，默认值为88
   * @param {number} options.capsuleHeight - 上角胶囊视图的高度，默认值为32
   * @param {number} options.capsuleRightMargin - 右上角胶囊视图的右边距
   * @param {number} options.capsuleCornerRadius - 右上角胶囊视图的圆角半径，默认值为5
   * @param {number} options.capsuleBorderWidth - 右上角胶囊视图的边框宽度，默认值为1
   * @param {string} options.capsuleBgLightColor - 胶囊背景颜色浅色
   * @param {string} options.capsuleBgDarkColor - 胶囊背景颜色深色
   * @param {string} options.capsuleBorderLightColor - 右上角胶囊视图的边框浅色颜色
   * @param {string} options.capsuleBorderDarkColor - 右上角胶囊视图的边框深色颜色
   * @param {string} options.capsuleDividerLightColor - 胶囊分割线浅色颜色
   * @param {string} options.capsuleDividerDarkColor - 胶囊分割线深色颜色
   * @param {number} options.moreLightImage - 胶囊里的浅色更多按钮的图片对象，如果不传，会使用默认图标
   * @param {number} options.moreDarkImage - 胶囊里的深色更多按钮的图片对象，如果不传，会使用默认图标
   * @param {number} options.moreBtnWidth - 胶囊里的更多按钮的宽度，高度与宽度相等。android默认值为32；ios默认值为20
   * @param {number} options.moreBtnLeftMargin - 胶囊里的更多按钮的左边距。android默认值为6；ios默认值为12
   * @param {number} options.closeLightImage - 胶囊里的浅色更多按钮的图片对象，如果不传，会使用默认图标
   * @param {number} options.closeDarkImage - 胶囊里的深色更多按钮的图片对象，如果不传，会使用默认图标
   * @param {number} options.closeBtnWidth - 胶囊里的关闭按钮的宽度，高度与宽度相等。android默认值为32；ios默认值为20
   * @param {number} options.closeBtnLeftMargin - 胶囊里的关闭按钮的左边距。android默认值为6；ios默认值为12
   */
  constructor({
    capsuleWidth = 88,
    capsuleHeight = 32,
    capsuleRightMargin = 8,
    capsuleCornerRadius = 5,
    capsuleBorderWidth = 1,
    capsuleBgLightColor = isAndroid() ? "#33000000" : "#80ffffff",
    capsuleBgDarkColor = isAndroid() ? "#80ffffff" : "#33000000",
    capsuleBorderLightColor = "#80ffffff",
    capsuleBorderDarkColor = "#26000000",
    capsuleDividerLightColor = "#80ffffff",
    capsuleDividerDarkColor = "#26000000",
    moreLightImage = null,
    moreDarkImage = null,
    moreBtnWidth = isAndroid() ? 32 : 20,
    moreBtnLeftMargin = isAndroid() ? 6 : 12,
    closeLightImage = null,
    closeDarkImage = null,
    closeBtnWidth = isAndroid() ? 32 : 20,
    closeBtnLeftMargin = isAndroid() ? 6 : 12
  } = {}) {
    this.capsuleWidth = capsuleWidth;
    this.capsuleHeight = capsuleHeight;
    this.capsuleRightMargin = capsuleRightMargin;
    this.capsuleCornerRadius = capsuleCornerRadius;
    this.capsuleBorderWidth = capsuleBorderWidth;
    this.capsuleBgLightColor = capsuleBgLightColor;
    this.capsuleBgDarkColor = capsuleBgDarkColor;
    this.capsuleBorderLightColor = capsuleBorderLightColor;
    this.capsuleBorderDarkColor = capsuleBorderDarkColor;
    this.capsuleDividerLightColor = capsuleDividerLightColor;
    this.capsuleDividerDarkColor = capsuleDividerDarkColor;
    this.moreLightImage = moreLightImage;
    this.moreDarkImage = moreDarkImage;
    this.moreBtnWidth = moreBtnWidth;
    this.moreBtnLeftMargin = moreBtnLeftMargin;
    this.closeLightImage = closeLightImage;
    this.closeDarkImage = closeDarkImage;
    this.closeBtnWidth = closeBtnWidth;
    this.closeBtnLeftMargin = closeBtnLeftMargin;
  }
}


class NavHomeConfig {
  /**
   * @param {number} width - 返回首页按钮的宽度
   * @param {number} height - 返回首页按钮的高度
   * @param {Object} options - 可选参数
   * @param {number} options.leftMargin - 返回首页按钮的左边距，Android默认值为8，iOS默认值为10
   * @param {number} options.cornerRadius - 返回首页按钮的圆角半径，默认值为5
   * @param {number} options.borderWidth - 返回首页按钮的边框宽度，Android默认值为0.75，iOS默认值为0.8
   * @param {string} options.borderLightColor - 返回首页按钮的边框浅色颜色（暗黑模式）
   * @param {string} options.borderDarkColor - 返回首页按钮的边框深色颜色（明亮模式）
   * @param {string} options.bgLightColor - 返回首页按钮的背景浅色颜色（明亮模式）
   * @param {string} options.bgDarkColor - 返回首页按钮的背景深色颜色（暗黑模式）
   */
  constructor(
    width,
    height, {
      leftMargin = isAndroid() ? 8 : 10,
      cornerRadius = 5,
      borderWidth = isAndroid() ? 0.75 : 0.8,
      borderLightColor = "#80ffffff",
      borderDarkColor = "#26000000",
      bgLightColor = "#33000000",
      bgDarkColor = "#80ffffff"
    } = {}) {
    this.width = width;
    this.height = height;
    this.leftMargin = leftMargin;
    this.cornerRadius = cornerRadius;
    this.borderWidth = borderWidth;
    this.borderLightColor = borderLightColor;
    this.borderDarkColor = borderDarkColor;
    this.bgLightColor = bgLightColor;
    this.bgDarkColor = bgDarkColor;
  }
}

class FloatWindowConfig {
  /**
   * @param {Object} options - 可选参数
   * @param {boolean} options.floatMode - default value is false
   * @param {number} options.x - x coordinate
   * @param {number} options.y - y coordinate
   * @param {number} options.width - width of the window
   * @param {number} options.height - height of the window
   */
  constructor({
    floatMode = false,
    x,
    y,
    width,
    height
  } = {}) {
    this.floatMode = floatMode;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
}

class AuthButtonConfig {
  /**
   * @param {Object} options - 可选参数
   * @param {number} options.cornerRadius - 按钮的圆角半径
   * @param {string} options.normalBackgroundColor - 按钮默认背景颜色
   * @param {string} options.pressedBackgroundColor - 按钮按下背景颜色
   * @param {string} options.normalTextColor - 按钮默认文字颜色
   * @param {string} options.pressedTextColor - 按钮按下文字颜色
   * @param {string} options.normalBorderColor - 按钮默认边框颜色
   * @param {string} options.pressedBorderColor - 按钮按下边框颜色
   */
  constructor({
    cornerRadius,
    normalBackgroundColor,
    pressedBackgroundColor,
    normalTextColor,
    pressedTextColor,
    normalBorderColor,
    pressedBorderColor
  }) {
    this.cornerRadius = cornerRadius;
    this.normalBackgroundColor = normalBackgroundColor;
    this.pressedBackgroundColor = pressedBackgroundColor;
    this.normalTextColor = normalTextColor;
    this.pressedTextColor = pressedTextColor;
    this.normalBorderColor = normalBorderColor;
    this.pressedBorderColor = pressedBorderColor;
  }
}

class AuthViewConfig {
  /**
   * @param {Object} options - 可选参数
   * @param {number} options.appletNameTextSize - 小程序名称字体大小，默认字体为PingFangSC-Regular，默认大小16
   * @param {string} options.appletNameLightColor - 小程序名称的浅色颜色（明亮模式），默认#222222
   * @param {string} options.appletNameDarkColor - 小程序名称的深色颜色（暗黑模式），默认#D0D0D0
   * @param {number} options.authorizeTitleTextSize - 权限标题字体大小，默认字体为PingFangSC-Medium，默认大小17；备注：权限选项文字字体大小使用该配置项，但字体固定为PingFangSC-Regular
   * @param {string} options.authorizeTitleLightColor - 权限标题的浅色颜色（明亮模式），默认#222222 ; 备注：权限选项文字字体颜色使用该配置项
   * @param {string} options.authorizeTitleDarkColor - 权限标题的深色颜色（暗黑模式），默认#D0D0D0 ; 备注：权限选项文字字体颜色使用该配置项
   * @param {number} options.authorizeDescriptionTextSize - 权限描述字体大小，默认字体为PingFangSC-Regular，默认大小14
   * @param {string} options.authorizeDescriptionLightColor - 权限描述的浅色颜色（明亮模式），默认#666666
   * @param {string} options.authorizeDescriptionDarkColor - 权限描述的深色颜色（暗黑模式），默认#5c5c5c
   * @param {number} options.agreementTitleTextSize - 协议标题字体大小，默认字体为PingFangSC-Regular，默认大小16
   * @param {string} options.agreementTitleLightColor - 协议标题的浅色颜色（明亮模式），默认#222222
   * @param {string} options.agreementTitleDarkColor - 协议标题的深色颜色（暗黑模式），默认#D0D0D0
   * @param {number} options.agreementDescriptionTextSize - 协议描述字体大小，默认字体为PingFangSC-Regular，默认大小14
   * @param {string} options.agreementDescriptionLightColor - 协议描述的浅色颜色（明亮模式），默认#222222
   * @param {string} options.agreementDescriptionDarkColor - 协议描述的深色颜色（暗黑模式），默认#D0D0D0
   * @param {string} options.linkLightColor - 链接的浅色颜色（明亮模式），默认#4285f4
   * @param {string} options.linkDarkColor - 链接的深色颜色（暗黑模式），默认#4285f4
   * @param {AuthButtonConfig} options.allowButtonLightConfig - 同意按钮配置（明亮模式）; 默认配置-> 圆角：4; 默认背景色：#4285F4 ; 默认描边：#4285F4 ; 默认文字颜色：#FFFFFF ; 按下背景色：#3B77DB ; 按下默认描边：#3B77DB ; 按下文字颜色：#FFFFFF
   * @param {AuthButtonConfig} options.allowButtonDarkConfig - 同意按钮配置（暗黑模式） ; 默认配置-> 圆角：4; 默认背景色：#4285F4 ; 默认描边：#4285F4 ; 默认文字颜色：#FFFFFF ; 按下背景色：#5E97F5 ; 按下默认描边：#5E97F5 ; 按下文字颜色：#FFFFFF
   * @param {AuthButtonConfig} options.rejectButtonLightConfig - 拒绝按钮配置（明亮模式）; 默认配置-> 圆角：4; 默认背景色：#FFFFFF ; 默认描边：#E2E2E2 ; 默认文字颜色：#222222 ; 按下背景色：#D8D8D8 ; 按下默认描边：#D8D8D8 ; 按下文字颜色：#222222
   * @param {AuthButtonConfig} options.rejectButtonDarkConfig - 拒绝按钮配置（暗黑模式）; 默认配置-> 圆角：4; 默认背景色：#2C2C2C ; 默认描边：#2C2C2C ; 默认文字颜色：#D0D0D0 ; 按下背景色：#414141 ; 按下默认描边：#414141 ; 按下文字颜色：#D0D0D0
   */
  constructor({
    appletNameTextSize = 16,
    appletNameLightColor = "#ff222222",
    appletNameDarkColor = "#ffd0d0d0",
    authorizeTitleTextSize = 17,
    authorizeTitleLightColor = "#ff222222",
    authorizeTitleDarkColor = "#ffd0d0d0",
    authorizeDescriptionTextSize = 14,
    authorizeDescriptionLightColor = "#ff666666",
    authorizeDescriptionDarkColor = "#ff5c5c5c",
    agreementTitleTextSize = 16,
    agreementTitleLightColor = "#ff222222",
    agreementTitleDarkColor = "#ffd0d0d0",
    agreementDescriptionTextSize = 14,
    agreementDescriptionLightColor = "#ff222222",
    agreementDescriptionDarkColor = "#ffd0d0d0",
    linkLightColor = "#ff4285f4",
    linkDarkColor = "#ff4285f4",
    allowButtonLightConfig = null,
    allowButtonDarkConfig = null,
    rejectButtonLightConfig = null,
    rejectButtonDarkConfig = null
  } = {}) {
    this.appletNameTextSize = appletNameTextSize;
    this.appletNameLightColor = appletNameLightColor;
    this.appletNameDarkColor = appletNameDarkColor;
    this.authorizeTitleTextSize = authorizeTitleTextSize;
    this.authorizeTitleLightColor = authorizeTitleLightColor;
    this.authorizeTitleDarkColor = authorizeTitleDarkColor;
    this.authorizeDescriptionTextSize = authorizeDescriptionTextSize;
    this.authorizeDescriptionLightColor = authorizeDescriptionLightColor;
    this.authorizeDescriptionDarkColor = authorizeDescriptionDarkColor;
    this.agreementTitleTextSize = agreementTitleTextSize;
    this.agreementTitleLightColor = agreementTitleLightColor;
    this.agreementTitleDarkColor = agreementTitleDarkColor;
    this.agreementDescriptionTextSize = agreementDescriptionTextSize;
    this.agreementDescriptionLightColor = agreementDescriptionLightColor;
    this.agreementDescriptionDarkColor = agreementDescriptionDarkColor;
    this.linkLightColor = linkLightColor;
    this.linkDarkColor = linkDarkColor;
    this.allowButtonLightConfig = allowButtonLightConfig;
    this.allowButtonDarkConfig = allowButtonDarkConfig;
    this.rejectButtonLightConfig = rejectButtonLightConfig;
    this.rejectButtonDarkConfig = rejectButtonDarkConfig;
  }
}

class UIConfig {
  /**
   * @param {Object} options - 可选参数
   * @param {Map<string,any>} options.navigationTitleTextAttributes - 【iOS属性】 导航栏的标题样式,目前支持了font，
   * @param {number} options.navigationBarHeight - 【iOS属性】 导航栏的高度(不含状态栏高度)，默认值为44
   * @param {string} options.navigationBarTitleLightColor - 导航栏的标题颜色（深色主题），默认值为白色
   * @param {string} options.navigationBarTitleDarkColor - 导航栏的标题颜色（明亮主题），默认值为黑色
   * @param {string} options.navigationBarBackBtnLightColor - 导航栏的返回按钮颜色（深色主题），默认值为白色
   * @param {string} options.navigationBarBackBtnDarkColor - 导航栏的返回按钮颜色（明亮主题），默认值为黑色
   * @param {number} options.moreMenuStyle - 弹出的菜单视图的样式 0:默认 1:Normal
   * @param {number} options.isHideBackHomePriority - 隐藏导航栏返回首页按钮的优先级设置，默认全局配置优先
   * @param {boolean} options.isAlwaysShowBackInDefaultNavigationBar - 【Android属性】 当导航栏为默认导航栏时，是否始终显示返回按钮
   * @param {boolean} options.isClearNavigationBarNavButtonBackground -【Android属性】  是否清除导航栏导航按钮的背景
   * @param {boolean} options.isHideFeedbackAndComplaints - 隐藏...弹出菜单中的 【反馈与投诉】 菜单
   * @param {boolean} options.isHideBackHome - 隐藏...弹出菜单中的 【返回首页】 菜单
   * @param {boolean} options.isHideForwardMenu - 隐藏...弹出菜单中的 【转发】 菜单，默认为false
   * @param {boolean} options.isHideShareAppletMenu - 隐藏...弹出菜单中的 【分享】 菜单，默认为true
   * @param {boolean} options.isHideAddToDesktopMenu - 隐藏...弹出菜单中的 【添加到桌面】 菜单
   * @param {boolean} options.isHideFavoriteMenu - 隐藏...弹出菜单中的 【收藏】 菜单
   * @param {boolean} options.isHideRefreshMenu - 隐藏...弹出菜单中的 【重新进入小程序】 菜单，默认为false
   * @param {boolean} options.isHideSettingMenu - 隐藏...弹出菜单中的 【设置】 菜单，默认为false
   * @param {boolean} options.isHideClearCacheMenu - 隐藏...弹出菜单中的 【清理缓存】 菜单，默认为false
   * @param {CapsuleConfig} options.capsuleConfig - 胶囊按钮配置
   * @param {NavHomeConfig} options.navHomeConfig - 返回首页按钮的配置
   * @param {FloatWindowConfig} options.floatWindowConfig - 浮窗配置
   * @param {AuthViewConfig} options.authViewConfig - 权限弹窗UI配置
   * @param {string} options.webViewProgressBarColor - 小程序里加载H5页面时进度条的颜色 格式 #FFFFAA00
   * @param {boolean} options.hideWebViewProgressBar - 隐藏小程序里加载H5时进度条，默认为false
   * @param {boolean} options.autoAdaptDarkMode - 是否自适应暗黑模式。如果设置为true，则更多页面、关于等原生页面会随着手机切换暗黑，也自动调整为暗黑模式
   * @param {boolean} options.useNativeLiveComponent - 是否使用内置的live组件，默认为false。(目前仅iOS支持)
   * @param {string} options.appendingCustomUserAgent - 要拼接的userAgent字符串
   * @param {number} options.transtionStyle - 【iOS属性】打开小程序时的默认动画方式，默认为FATTranstionStyleUp;该属性主要针对非api方式打开小程序时的动画缺省值。主要改变如下场景的动画方式：1. scheme 打开小程序；2. universal link 打开小程序；3. navigateToMiniprogram
   * @param {boolean} options.hideTransitionCloseButton - 加载小程序过程中（小程序Service层还未加载成功，基础库还没有向SDK传递小程序配置信息），是否隐藏导航栏的关闭按钮
   * @param {boolean} options.disableSlideCloseAppletGesture - 是否禁用侧滑关闭小程序的手势。默认为false
   * @param {string} options.appletText - 注入小程序统称appletText字符串，默认为“小程序”
   * @param {string} options.loadingLayoutCls - 【Android属性】 Loading页回调Class
   */
  constructor({
    navigationTitleTextAttributes = null,
    navigationBarHeight = 44,
    navigationBarTitleLightColor = "#ffffffff",
    navigationBarTitleDarkColor = "#ff000000",
    navigationBarBackBtnLightColor = "#ffffffff",
    navigationBarBackBtnDarkColor = "#ff000000",
    moreMenuStyle = 0,
    isHideBackHomePriority = ConfigPriority.ConfigGlobalPriority,
    isAlwaysShowBackInDefaultNavigationBar = false,
    isClearNavigationBarNavButtonBackground = false,
    isHideFeedbackAndComplaints = false,
    isHideBackHome = false,
    isHideForwardMenu = false,
    isHideShareAppletMenu = true,
    isHideAddToDesktopMenu = true,
    isHideFavoriteMenu = true,
    isHideRefreshMenu = false,
    isHideSettingMenu = false,
    isHideClearCacheMenu = false,
    capsuleConfig = null,
    navHomeConfig = null,
    floatWindowConfig = null,
    authViewConfig = null,
    webViewProgressBarColor,
    hideWebViewProgressBar = false,
    autoAdaptDarkMode = false,
    useNativeLiveComponent = false,
    appendingCustomUserAgent = null,
    transtionStyle = TranstionStyle.TranstionStyleUp,
    hideTransitionCloseButton = false,
    disableSlideCloseAppletGesture = false,
    appletText = null,
    loadingLayoutCls = null
  } = {}) {
    this.navigationTitleTextAttributes = navigationTitleTextAttributes;
    this.navigationBarHeight = navigationBarHeight;
    this.navigationBarTitleLightColor = navigationBarTitleLightColor;
    this.navigationBarTitleDarkColor = navigationBarTitleDarkColor;
    this.navigationBarBackBtnLightColor = navigationBarBackBtnLightColor;
    this.navigationBarBackBtnDarkColor = navigationBarBackBtnDarkColor;
    this.moreMenuStyle = moreMenuStyle;
    this.isHideBackHomePriority = isHideBackHomePriority;
    this.isAlwaysShowBackInDefaultNavigationBar = isAlwaysShowBackInDefaultNavigationBar;
    this.isClearNavigationBarNavButtonBackground = isClearNavigationBarNavButtonBackground;
    this.isHideFeedbackAndComplaints = isHideFeedbackAndComplaints;
    this.isHideBackHome = isHideBackHome;
    this.isHideForwardMenu = isHideForwardMenu;
    this.isHideShareAppletMenu = isHideShareAppletMenu;
    this.isHideAddToDesktopMenu = isHideAddToDesktopMenu;
    this.isHideFavoriteMenu = isHideFavoriteMenu;
    this.isHideRefreshMenu = isHideRefreshMenu;
    this.isHideSettingMenu = isHideSettingMenu;
    this.isHideClearCacheMenu = isHideClearCacheMenu;
    this.capsuleConfig = capsuleConfig;
    this.navHomeConfig = navHomeConfig;
    this.floatWindowConfig = floatWindowConfig;
    this.authViewConfig = authViewConfig;
    this.webViewProgressBarColor = webViewProgressBarColor;
    this.hideWebViewProgressBar = hideWebViewProgressBar;
    this.autoAdaptDarkMode = autoAdaptDarkMode;
    this.useNativeLiveComponent = useNativeLiveComponent;
    this.appendingCustomUserAgent = appendingCustomUserAgent;
    this.transtionStyle = transtionStyle;
    this.hideTransitionCloseButton = hideTransitionCloseButton;
    this.disableSlideCloseAppletGesture = disableSlideCloseAppletGesture;
    this.appletText = appletText;
    this.loadingLayoutCls = loadingLayoutCls;
  }
}

class FATFetchBindApplet {
  /**
   * @param {Object} options - 可选参数
   * @param {string} options.apiServer - 小程序服务器地址
   * @param {string} options.miniAppId - 小程序id
   * @param {string} options.name - 小程序名称
   * @param {string} options.logo - 小程序logo
   * @param {string} options.appClass - 小程序分类
   * @param {number} options.displayStatus - 小程序状态类型，枚举值。0:所有；1：已上架的；2：未上架的；3：已下架的
   * @param {boolean} options.isForbidden - 是否被禁用
   * @param {string} options.desc - 小程序简介
   * @param {string} options.detailDesc - 小程序详细描述
   * 
   */
  constructor({
    apiServer,
    miniAppId,
    name,
    logo,
    appClass,
    displayStatus,
    isForbidden,
    desc,
    detailDesc
  }) {
    this.apiServer = apiServer;
    this.miniAppId = miniAppId;
    this.name = name;
    this.logo = logo;
    this.appClass = appClass;
    this.displayStatus = displayStatus;
    this.isForbidden = isForbidden;
    this.desc = desc;
    this.detailDesc = detailDesc;
  }
}

export { BOOLState, ConfigPriority, LogLevel, FCReLaunchMode, LanguageType, FinStoreConfig, Config, CapsuleConfig, NavHomeConfig, FloatWindowConfig, AuthButtonConfig, AuthViewConfig, UIConfig };
