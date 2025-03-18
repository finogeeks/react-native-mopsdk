package com.finogeeks.mop.rnsdk;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.finogeeks.lib.applet.anim.FadeInAnim;
import com.finogeeks.lib.applet.anim.NoneAnim;
import com.finogeeks.lib.applet.anim.SlideFromBottomToTopAnim;
import com.finogeeks.lib.applet.anim.SlideFromLeftToRightAnim;
import com.finogeeks.lib.applet.anim.SlideFromRightToLeftAnim;
import com.finogeeks.lib.applet.anim.SlideFromTopToBottomAnim;
import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.client.FinAppConfigPriority;
import com.finogeeks.lib.applet.client.FinStoreConfig;
import com.finogeeks.lib.applet.db.entity.FinApplet;
import com.finogeeks.lib.applet.interfaces.FinCallback;
import com.finogeeks.lib.applet.sdk.api.request.IFinAppletRequest;
import com.finogeeks.lib.applet.page.view.moremenu.MoreMenuItem;
import com.finogeeks.lib.applet.page.view.moremenu.MoreMenuType;
import com.finogeeks.lib.applet.rest.model.GrayAppletVersionConfig;
import com.finogeeks.lib.applet.sdk.api.IAppletHandler;
import com.finogeeks.lib.applet.sdk.api.request.RemoteFinAppletRequest;
import com.finogeeks.mop.rnsdk.util.InitUtils;
import com.finogeeks.xlog.XLogLevel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;


public class FINMopSDKModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static final String TAG = FINMopSDKModule.class.getSimpleName();

    private Handler handler = new Handler(Looper.getMainLooper());

    public FINMopSDKModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "FINMopSDK";
    }

    private WritableMap success(Map data) {
        WritableMap res = Arguments.createMap();
        res.putBoolean("success", true);
        res.putString("errMsg", "ok");
        res.putMap("data", Arguments.makeNativeMap(data));
        return res;
    }

    private WritableMap fail(String msg) {
        WritableMap res = Arguments.createMap();
        res.putBoolean("success", false);
        res.putString("errMsg", "fail:" + msg);
        return res;
    }

    private final Map<String, FinCallback> callbacks = new HashMap<>();

    private void sendEvent(String apiName, Map<String, Object> params, FinCallback finCallback) {
        String callbackId = UUID.randomUUID().toString();
        WritableMap map = Arguments.createMap();
        map.putString("apiName", apiName);
        map.putMap("params", Arguments.makeNativeMap(params));
        map.putString("callbackId", callbackId);
        callbacks.put(callbackId, finCallback);
        Log.d(TAG, "sendEvent:" + apiName + "," + map);
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("EventReminder", map);
    }

    @ReactMethod
    public void eventReminderCallback(String apiName, Dynamic result, String callbackId) {
        Log.d(TAG, "eventReminderCallback," + apiName + "," + result);
        FinCallback callback = callbacks.get(callbackId);
        if (callback != null) {
            callback.onSuccess(result);
            callbacks.remove(callbackId);
        }
    }

    @ReactMethod
    public void initialize(ReadableMap params, final Callback callback) {
        Log.d(TAG, "initialize:" + params);
        Map<String, Object> param = params.toHashMap();
        if (FinAppClient.INSTANCE.isFinAppProcess(reactContext)) {
            // 小程序进程不执行任何初始化操作
            return;
        }
        String appkey = String.valueOf(param.get("appkey"));
        String secret = String.valueOf(param.get("secret"));
        String apiServer = "https://api.finclip.com";
        String apiPrefix = "/api/v1/mop/";

        if (param.get("apiServer") != null) {
            apiServer = String.valueOf(param.get("apiServer"));
        }
        if (param.get("apiPrefix") != null) {
            apiPrefix = String.valueOf(param.get("apiPrefix"));
            if (!apiPrefix.endsWith("/")) {
                apiPrefix = apiPrefix + "/";
            }
        }


        FinAppConfig.Builder builder = new FinAppConfig.Builder()
                .setSdkKey(appkey)
                .setSdkSecret(secret)
                .setApiUrl(apiServer)
                .setApiPrefix(apiPrefix);

        Object userId = param.get("userId");
        if (userId != null) {
            builder.setUserId((String) userId);
        }

        Object appletText = param.get("appletText");
        if (appletText != null) {
            builder.setAppletText((String) appletText);
        }

        Object localeLanguage = param.get("localeLanguage");
        if (localeLanguage != null) {
            String language = (String) localeLanguage;
            if (language.contains("_")) {
                String[] locales = language.split("_");
                builder.setLocale(new Locale(locales[0], locales[1]));
            } else {
                builder.setLocale(new Locale(language));
            }
        } else {
            Object languageInteger = param.get("language");
            if (languageInteger != null) {
                if (languageInteger instanceof Double && ((Double) languageInteger).intValue() == 1) {
                    builder.setLocale(Locale.ENGLISH);
                } else {
                    builder.setLocale(Locale.SIMPLIFIED_CHINESE);
                }
            }
        }

        FinAppConfig config = builder.build();

        // SDK初始化结果回调，用于接收SDK初始化状态
        FinCallback<Object> cb = new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                // SDK初始化成功
                callback.invoke(success(null));
            }

            @Override
            public void onError(int code, String error) {
                // SDK初始化失败
                callback.invoke(fail(error));
            }

            @Override
            public void onProgress(int status, String error) {

            }
        };
        this.reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FinAppClient.INSTANCE.init(reactContext.getCurrentActivity().getApplication(), config, cb);
            }
        });
    }

    @ReactMethod
    public void initSDK(ReadableMap params, final Callback callback) {
        Log.d(TAG, "initSDK:" + params);
        if (params == null) {
            callback.invoke(fail("params is null"));
            return;
        }
        if (FinAppClient.INSTANCE.isFinAppProcess(reactContext)) {
            // 小程序进程不执行任何初始化操作
            return;
        }
        // 从 param 中提取 config 和 uiConfig 字典
        // config
        ReadableMap configMap = params.getMap("config");
        if (configMap == null) {
            callback.invoke(fail("params config is null"));
            return;
        }
        // uiConfig
        ReadableMap uiConfigMap = params.getMap("uiConfig");

        FinAppConfig.Builder configBuilder = new FinAppConfig.Builder();

        ReadableArray finStoreConfigs = configMap.getArray("finStoreConfigs");
        if (finStoreConfigs != null) {
            List<FinStoreConfig> storeConfigs = new ArrayList<>();
            int size = finStoreConfigs.size();
            for (int i = 0; i < size; i++) {
                ReadableMap finStoreConfig = finStoreConfigs.getMap(i);
                String sdkKey = InitUtils.getStringVal(finStoreConfig, "sdkKey", "");
                String sdkSecret = InitUtils.getStringVal(finStoreConfig, "sdkSecret", "");
                String apiServer = InitUtils.getStringVal(finStoreConfig, "apiServer", "");
                String apmServer = InitUtils.getStringVal(finStoreConfig, "apmServer", "");
                String fingerprint = InitUtils.getStringVal(finStoreConfig, "fingerprint", "");
                String cryptType = InitUtils.getStringVal(finStoreConfig, "cryptType", "");
                boolean encryptServerData = InitUtils.getBooleanVal(finStoreConfig, "encryptServerData", false);
                boolean enablePreloadFramework = InitUtils.getBooleanVal(finStoreConfig, "enablePreloadFramework", false);
                //凡泰助手里，服务器是https://api.finclip.com，默认开启预加载基础库
                if (!TextUtils.isEmpty(apiServer) && apiServer.equals("https://api.finclip.com")) {
                    enablePreloadFramework = true;
                }
                storeConfigs.add(new FinStoreConfig(sdkKey, sdkSecret, apiServer, apmServer, "",
                        fingerprint, cryptType, encryptServerData, enablePreloadFramework));
            }
            configBuilder.setFinStoreConfigs(storeConfigs);
        }

        String userId = InitUtils.getStringVal(configMap, "userId");
        if (userId != null) {
            configBuilder.setUserId(userId);
        }
        String productIdentification = InitUtils.getStringVal(configMap, "productIdentification");
        if (productIdentification != null) {
            configBuilder.setProductIdentification(productIdentification);
        }

        configBuilder.setDisableRequestPermissions(InitUtils.getBooleanVal(configMap, "disableRequestPermissions", false));
        configBuilder.setAppletAutoAuthorize(InitUtils.getBooleanVal(configMap, "appletAutoAuthorize", false));
        configBuilder.setDisableGetSuperviseInfo(InitUtils.getBooleanVal(configMap, "disableGetSuperviseInfo", false));
        configBuilder.setIgnoreWebviewCertAuth(InitUtils.getBooleanVal(configMap, "ignoreWebviewCertAuth", false));
        configBuilder.setAppletIntervalUpdateLimit(InitUtils.getIntVal(configMap, "appletIntervalUpdateLimit", 3));
        ReadableMap apmExtendInfo = configMap.getMap("apmExtendInfo");
        if (apmExtendInfo != null) {
            configBuilder.setApmExtendInfo(apmExtendInfo.toHashMap());
        }
        configBuilder.setEnableApmDataCompression(InitUtils.getBooleanVal(configMap, "enableApmDataCompression", false));
        configBuilder.setEncryptServerData(InitUtils.getBooleanVal(configMap, "encryptServerData", false));

        int appletDebugModeIndex = InitUtils.getIntVal(configMap, "appletDebugMode", 0);
        if (appletDebugModeIndex == 0) {
            configBuilder.setAppletDebugMode(FinAppConfig.AppletDebugMode.appletDebugModeUndefined);
        } else if (appletDebugModeIndex == 1) {
            configBuilder.setAppletDebugMode(FinAppConfig.AppletDebugMode.appletDebugModeEnable);
        } else if (appletDebugModeIndex == 2) {
            configBuilder.setAppletDebugMode(FinAppConfig.AppletDebugMode.appletDebugModeDisable);
        } else if (appletDebugModeIndex == 3) {
            configBuilder.setAppletDebugMode(FinAppConfig.AppletDebugMode.appletDebugModeForbidden);
        }
        configBuilder.setEnableWatermark(InitUtils.getBooleanVal(configMap, "enableWatermark", false));
        int watermarkPriorityIndex = InitUtils.getIntVal(configMap, "watermarkPriority", 0);
        if (watermarkPriorityIndex == 0) {
            configBuilder.setWatermarkPriority(FinAppConfigPriority.GLOBAL);
        } else if (watermarkPriorityIndex == 1) {
            configBuilder.setWatermarkPriority(FinAppConfigPriority.SPECIFIED);
        } else if (watermarkPriorityIndex == 2) {
            configBuilder.setWatermarkPriority(FinAppConfigPriority.APPLET_FILE);
        }
        ReadableMap header = configMap.getMap("header");
        if (header != null) {
            HashMap<String, Object> headerMap = header.toHashMap();
            configBuilder.setHeader(InitUtils.createMapFromMap(headerMap));
        }
        int headerPriorityIndex = InitUtils.getIntVal(configMap, "headerPriority", 0);
        if (headerPriorityIndex == 0) {
            configBuilder.setHeaderPriority(FinAppConfigPriority.GLOBAL);
        } else if (headerPriorityIndex == 1) {
            configBuilder.setHeaderPriority(FinAppConfigPriority.SPECIFIED);
        } else if (headerPriorityIndex == 2) {
            configBuilder.setHeaderPriority(FinAppConfigPriority.APPLET_FILE);
        }
        configBuilder.setPageCountLimit(InitUtils.getIntVal(configMap, "pageCountLimit", 0));
        ReadableArray schemes = configMap.getArray("schemes");
        if (schemes != null) {
            configBuilder.setSchemes(InitUtils.toArray(schemes));
        }
        configBuilder.setDebugMode(InitUtils.getBooleanVal(configMap, "debug", false));
        configBuilder.setEnableLog(InitUtils.getBooleanVal(configMap, "debug", false));
        Integer maxRunningApplet = InitUtils.getIntVal(configMap, "maxRunningApplet");
        if (maxRunningApplet != null) {
            configBuilder.setMaxRunningApplet(maxRunningApplet);
        }

        Integer backgroundFetchPeriod = InitUtils.getIntVal(configMap, "backgroundFetchPeriod");
        if (backgroundFetchPeriod != null) {
            configBuilder.setBackgroundFetchPeriod(backgroundFetchPeriod);
        }

        Integer webViewMixedContentMode = InitUtils.getIntVal(configMap, "webViewMixedContentMode");
        if (webViewMixedContentMode != null) {
            configBuilder.setWebViewMixedContentMode(webViewMixedContentMode);
        }
        configBuilder.setBindAppletWithMainProcess(InitUtils.getBooleanVal(configMap, "bindAppletWithMainProcess", false));
        String killAppletProcessNotice = InitUtils.getStringVal(configMap, "killAppletProcessNotice");
        if (killAppletProcessNotice != null) {
            configBuilder.setKillAppletProcessNotice(killAppletProcessNotice);
        }
        Integer minAndroidSdkVersion = InitUtils.getIntVal(configMap, "minAndroidSdkVersion");
        if (minAndroidSdkVersion != null) {
            configBuilder.setMinAndroidSdkVersion(minAndroidSdkVersion);
        }
        Boolean enableScreenShot = InitUtils.getBooleanVal(configMap, "enableScreenShot");
        if (enableScreenShot != null) {
            configBuilder.setEnableScreenShot(enableScreenShot);
        }

        Integer screenShotPriorityIndex = InitUtils.getIntVal(configMap, "screenShotPriority", 0);
        if (screenShotPriorityIndex == 0) {
            configBuilder.setScreenShotPriority(FinAppConfigPriority.GLOBAL);
        } else if (screenShotPriorityIndex == 1) {
            configBuilder.setScreenShotPriority(FinAppConfigPriority.SPECIFIED);
        } else if (screenShotPriorityIndex == 2) {
            configBuilder.setScreenShotPriority(FinAppConfigPriority.APPLET_FILE);
        }
        Integer logLevelIndex = InitUtils.getIntVal(configMap, "logLevel");
        if (logLevelIndex == 0) {
            configBuilder.setLogLevel(XLogLevel.LEVEL_ERROR);
        } else if (logLevelIndex == 1) {
            configBuilder.setLogLevel(XLogLevel.LEVEL_WARNING);
        } else if (logLevelIndex == 2) {
            configBuilder.setLogLevel(XLogLevel.LEVEL_INFO);
        } else if (logLevelIndex == 3) {
            configBuilder.setLogLevel(XLogLevel.LEVEL_DEBUG);
        } else if (logLevelIndex == 4) {
            configBuilder.setLogLevel(XLogLevel.LEVEL_VERBOSE);
        } else if (logLevelIndex == 5) {
            configBuilder.setLogLevel(XLogLevel.LEVEL_NONE);
        }
        Integer logMaxAliveSec = InitUtils.getIntVal(configMap, "logMaxAliveSec");
        if (logMaxAliveSec != null) {
            configBuilder.setLogMaxAliveSec(logMaxAliveSec);
        }
        String logDir = InitUtils.getStringVal(configMap, "logDir");
        if (logDir != null) {
            configBuilder.setXLogDir(logDir);
        }
        configBuilder.setEnablePreNewProcess(InitUtils.getBooleanVal(configMap, "enablePreNewProcess", true));
        configBuilder.setUseLocalTbsCore(InitUtils.getBooleanVal(configMap, "useLocalTbsCore", false));
        String tbsCoreUrl = InitUtils.getStringVal(configMap, "tbsCoreUrl");
        if (tbsCoreUrl != null) {
            configBuilder.setTbsCoreUrl(tbsCoreUrl);
        }
        configBuilder.setEnableJ2V8(InitUtils.getBooleanVal(configMap, "enableJ2V8", false));

        String localeLanguage = InitUtils.getStringVal(configMap, "localeLanguage");
        if (!TextUtils.isEmpty(localeLanguage)) {
            if (localeLanguage.contains("_")) {
                String[] locales = localeLanguage.split("_");
                configBuilder.setLocale(new Locale(locales[0], locales[1]));
            } else {
                configBuilder.setLocale(new Locale(localeLanguage));
            }
        } else {
            Integer languageInteger = InitUtils.getIntVal(configMap, "language", 0);
            if (languageInteger == 1) {
                configBuilder.setLocale(Locale.ENGLISH);
            } else {
                configBuilder.setLocale(Locale.SIMPLIFIED_CHINESE);
            }
        }

        String appletText = InitUtils.getStringVal(uiConfigMap, "appletText");
        if (appletText != null) {
            configBuilder.setAppletText(appletText);
        }

        // 处理UIConfig
        if (uiConfigMap != null) {
            String appendingCustomUserAgent = InitUtils.getStringVal(uiConfigMap, "appendingCustomUserAgent");
            if (appendingCustomUserAgent != null) {
                configBuilder.setCustomWebViewUserAgent(appendingCustomUserAgent);
            }

            FinAppConfig.UIConfig uiConfig = InitUtils.createUIConfigFromMap(uiConfigMap);
            if (uiConfig != null) {
                configBuilder.setUiConfig(uiConfig);
            }

        }

        final FinAppConfig finAppConfig = configBuilder.build();

        // SDK 初始化结果回调
        final FinCallback<Object> cb = new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                // SDK 初始化成功
                callback.invoke(success(null));
            }

            @Override
            public void onError(int code, String error) {
                // SDK 初始化失败
                callback.invoke(fail(error));
            }

            @Override
            public void onProgress(int status, String error) {
                // 处理进度更新（如果有）
            }
        };

        // 运行在 UI 线程进行 SDK 初始化
        this.reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FinAppClient.INSTANCE.init(reactContext.getCurrentActivity().getApplication(), finAppConfig, cb);
            }
        });
    }


    @ReactMethod
    public void openApplet(ReadableMap params, final Callback callback) {
        Log.d(TAG, "openApplet:params:" + params);
        String appId = InitUtils.getStringVal(params, "appId");
        if (appId == null) {
            callback.invoke(fail("appId is not null"));
            return;
        }
        Integer sequence = InitUtils.getIntVal(params, "sequence");
        String apiServer = InitUtils.getStringVal(params, "apiServer");
        Boolean isSingleProcess = InitUtils.getBooleanVal(params, "isSingleProcess");
        ReadableMap startParams = params.getMap("startParams");
        RemoteFinAppletRequest request;
        if (TextUtils.isEmpty(apiServer)) {
            request = IFinAppletRequest.Companion.fromAppId(appId);
        } else {
            request = IFinAppletRequest.Companion.fromAppId(apiServer, appId);
        }
        if (sequence != null) {
            request.setSequence(sequence);
        }
        if (startParams != null) {
            request.setStartParams(InitUtils.createMapFromMap(startParams.toHashMap()));
        }
        if (Boolean.TRUE.equals(isSingleProcess)) {
            request.setProcessMode(IFinAppletRequest.ProcessMode.SINGLE);
        } else {
            request.setProcessMode(IFinAppletRequest.ProcessMode.MULTI);
        }
        FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext,
                request,
                new FinCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        callback.invoke(success(null));
                    }

                    @Override
                    public void onError(int i, String s) {
                        callback.invoke(fail(s));
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
    }

    @ReactMethod
    public void startApplet(ReadableMap params, final Callback callback) {
        Log.d(TAG, "startApplet:params:" + params);
        String appletId = InitUtils.getStringVal(params, "appletId");
        if (appletId == null) {
            callback.invoke(fail("appletId is not null"));
            return;
        }
        Integer sequence = InitUtils.getIntVal(params, "sequence");
        String apiServer = InitUtils.getStringVal(params, "apiServer");
        Boolean isSingleProcess = InitUtils.getBooleanVal(params, "isSingleProcess");
        String offlineMiniprogramZipPath = InitUtils.getStringVal(params, "offlineMiniprogramZipPath");
        String offlineFrameworkZipPath = InitUtils.getStringVal(params, "offlineFrameworkZipPath");
        ReadableMap startParams = params.getMap("startParams");
        RemoteFinAppletRequest request;
        if (TextUtils.isEmpty(apiServer)) {
            request = IFinAppletRequest.Companion.fromAppId(appletId);
        } else {
            request = IFinAppletRequest.Companion.fromAppId(apiServer, appletId);
        }
        if (sequence != null) {
            request.setSequence(sequence);
        }
        if (startParams != null) {
            request.setStartParams(InitUtils.createMapFromMap(startParams.toHashMap()));
        }
        if (Boolean.TRUE.equals(isSingleProcess)) {
            request.setProcessMode(IFinAppletRequest.ProcessMode.SINGLE);
        } else {
            request.setProcessMode(IFinAppletRequest.ProcessMode.MULTI);
        }
        
        Integer reLaunchMode = InitUtils.getIntVal(params, "reLaunchMode", 0);
        // 默认为有启动参数，就触发reLaunch
        IFinAppletRequest.ReLaunchMode mode = IFinAppletRequest.ReLaunchMode.PARAMS_EXIST;
        if (reLaunchMode == 1) {
            mode = IFinAppletRequest.ReLaunchMode.ONLY_PARAMS_DIFF;
        } else if (reLaunchMode == 2) {
            mode = IFinAppletRequest.ReLaunchMode.ALWAYS;
        } else if (reLaunchMode == 3) {
            mode = IFinAppletRequest.ReLaunchMode.NEVER;
        }  
        request.setReLaunchMode(mode);
        request.setOfflineParams(offlineFrameworkZipPath, offlineMiniprogramZipPath);
        FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext,
                request,
                new FinCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        callback.invoke(success(null));
                    }

                    @Override
                    public void onError(int i, String s) {
                        callback.invoke(fail(s));
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });


    }

    @ReactMethod
    public void changeUserId(ReadableMap params, final Callback callback) {
        Log.d(TAG, "changeUserId:params:" + params);
        String userId = InitUtils.getStringVal(params, "userId");
        FinAppConfig finAppConfig = FinAppClient.INSTANCE.getFinAppConfig();
        if (finAppConfig != null) {
            finAppConfig.setUserId(userId);
            callback.invoke(success(null));
        } else {
            callback.invoke(fail("sdk not initilized"));
        }
    }

    @ReactMethod
    public void closeApplet(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        if (param.containsKey("appletId") && param.get("appletId") instanceof String) {
            String appId = (String) param.get("appletId");
            FinAppClient.INSTANCE.getAppletApiManager().closeApplet(appId);
            Log.d(TAG, "closeApplet:" + appId);
        }
    }

    @ReactMethod
    public void closeAllApplets() {
        FinAppClient.INSTANCE.getAppletApiManager().closeApplets();
        Log.d(TAG, "closeAllApplets");
    }

    @ReactMethod
    public void removeAllUsedApplets() {
        FinAppClient.INSTANCE.getAppletApiManager().removeAllUsedApplets();
        Log.d(TAG, "closeAllApplets");
    }

    @ReactMethod
    public void finishRunningApplet(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        Log.d(TAG, "finishRunningApplet");
        if (param.containsKey("appletId") && param.get("appletId") instanceof String) {
            String appId = (String) param.get("appletId");
            FinAppClient.INSTANCE.getAppletApiManager().finishRunningApplet(appId);
        }
    }

    @ReactMethod
    public void finishAllRunningApplets() {
        Log.d(TAG, "finishAllRunningApplets");
        FinAppClient.INSTANCE.getAppletApiManager().finishAllRunningApplets();
    }

    @ReactMethod
    public void qrcodeOpenApplet(ReadableMap params) {
        Log.d(TAG, "qrcodeOpenApplet:" + params);
        String qrcode = InitUtils.getStringVal(params, "qrcode", "");
        Boolean isSingleProcess = InitUtils.getBooleanVal(params, "isSingleProcess");
        IFinAppletRequest.ProcessMode processMode;
        if (Boolean.TRUE.equals(isSingleProcess)) {
            processMode = IFinAppletRequest.ProcessMode.SINGLE;
        } else {
            processMode = IFinAppletRequest.ProcessMode.MULTI;
        }
        Integer reLaunchMode = InitUtils.getIntVal(params, "reLaunchMode", 0);
        // 默认为有启动参数，就触发reLaunch
        IFinAppletRequest.ReLaunchMode mode = IFinAppletRequest.ReLaunchMode.PARAMS_EXIST;
        if (reLaunchMode == 1) {
            mode = IFinAppletRequest.ReLaunchMode.ONLY_PARAMS_DIFF;
        } else if (reLaunchMode == 2) {
            mode = IFinAppletRequest.ReLaunchMode.ALWAYS;
        } else if (reLaunchMode == 3) {
            mode = IFinAppletRequest.ReLaunchMode.NEVER;
        }  
        FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext,
                IFinAppletRequest.Companion
                        .fromQrCode(qrcode)
                        .setProcessMode(processMode)
                        .setReLaunchMode(mode), new FinCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "qrcodeOpenApplet success");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d(TAG, "qrcodeOpenApplet:" + s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
    }

    @ReactMethod
    public void currentApplet(ReadableMap params, Callback callback) {
        Log.d(TAG, "currentApplet");
        String appId = FinAppClient.INSTANCE.getAppletApiManager().getCurrentAppletId();
        if (appId != null) {
            FinApplet applet = FinAppClient.INSTANCE.getAppletApiManager().getUsedApplet(appId);
            if (applet != null) {
                Map<String, Object> res = new HashMap<>();
                res.put("appId", applet.getId());
                res.put("name", applet.getName());
                res.put("icon", applet.getIcon());
                res.put("description", applet.getDescription());
                res.put("version", applet.getVersion());
                res.put("thumbnail", applet.getThumbnail());
                callback.invoke(success(res));
            } else {
                callback.invoke(success(null));
            }
        } else {
            callback.invoke(success(null));
        }
    }

    @ReactMethod
    public void clearApplets() {
        Log.d(TAG, "clearApplets");
        FinAppClient.INSTANCE.getAppletApiManager().clearApplets();
    }

    @ReactMethod
    public void registerAppletHandler() {
        Log.d("AppletHandlerModule", "registerAppletHandler");

        FinAppClient.INSTANCE.getAppletApiManager().setAppletHandler(new IAppletHandler() {

            @Nullable
            @Override
            public Map<String, String> getWebViewCookie(@NonNull String s) {
                return null;
            }

            @Override
            public void getJSSDKConfig(@NonNull JSONObject jsonObject, @NonNull IAppletCallback iAppletCallback) {

            }

            @Override
            public boolean launchApp(@Nullable String s) {
                return false;
            }

            @Override
            public void getPhoneNumber(@NonNull IAppletCallback iAppletCallback) {

            }

            @Override
            public boolean feedback(@NonNull Bundle bundle) {
                return false;
            }

            @Override
            public boolean contact(@NonNull JSONObject jsonObject) {
                return false;
            }

            @Override
            public void chooseAvatar(@NonNull IAppletCallback iAppletCallback) {

            }

            @Nullable
            @Override
            public List<GrayAppletVersionConfig> getGrayAppletVersionConfigs(@NotNull String s) {
                return null;
            }

            @Override
            public void shareAppMessage(@NotNull String s, @Nullable Bitmap bitmap, @NotNull IAppletCallback iAppletCallback) {
                Log.d("MopPlugin", "shareAppMessage:" + s + " bitmap:" + bitmap);
                Map<String, Object> params = new HashMap<>();
                params.put("appletInfo", new Gson().fromJson(s, new TypeToken<Map<String, Object>>() {
                }.getType()));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                params.put("bitmap", data);
                handler.post(() -> {
                    sendEvent("extensionApi:forwardApplet", params, new FinCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            iAppletCallback.onSuccess(null);
                        }

                        @Override
                        public void onError(int i, String s) {
                            iAppletCallback.onFailure();

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                });
            }

            @Nullable
            @Override
            public Map<String, String> getUserInfo() {
                Log.d("AppletHandlerModule", "getUserInfo");
                final CountDownLatch latch = new CountDownLatch(1);
                final Map<String, String>[] ret = new Map[1];
                handler.post(() -> {
                    sendEvent("extensionApi:getUserInfo", null, new FinCallback<Dynamic>() {
                        @Override
                        public void onSuccess(Dynamic o) {
                            ret[0] = new Gson().fromJson(new Gson().toJson(o.asMap().toHashMap()), new TypeToken<Map<String, String>>() {
                            }.getType());
                            latch.countDown();
                        }

                        @Override
                        public void onError(int i, String s) {
                            latch.countDown();

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ret[0].size() > 0)
                    return ret[0];
                else
                    return null;
            }

            @Nullable
            @Override
            public List<MoreMenuItem> getRegisteredMoreMenuItems(@NotNull String s) {
                CountDownLatch latch = new CountDownLatch(1);
                List<MoreMenuItem> moreMenuItems = new ArrayList<>();
                Map<String, Object> params = new HashMap<>();
                params.put("appId", s);
                handler.post(() -> {
                    sendEvent("extensionApi:getCustomMenus", params, new FinCallback<Dynamic>() {
                        @Override
                        public void onSuccess(Dynamic o) {
                            ReadableArray ret = o.asArray();
                            Log.d(TAG, "getCustomMenus success : " + ret + " size : " + ret.size());
                            if (ret != null) {
                                for (int i = 0; i < ret.size(); i++) {
                                    Map<String, Object> map = ret.getMap(i).toHashMap();
                                    String type = (String) map.get("type");
                                    MoreMenuType moreMenuType;
                                    if ("common".equals(type)) {
                                        moreMenuType = MoreMenuType.COMMON;
                                    } else {
                                        moreMenuType = MoreMenuType.ON_MINI_PROGRAM;
                                    }
                                    moreMenuItems.add(new MoreMenuItem((String) map.get("menuId"), (String) map.get("title"), moreMenuType));
                                }
                            }
                            latch.countDown();
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "getCustomMenus errorCode : " + i + " errorMessage : " + s);
                            latch.countDown();
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getRegisteredMoreMenuItems moreMenuItems : " + moreMenuItems + " size : " + moreMenuItems.size());
                return moreMenuItems;
            }

            @Override
            public void onRegisteredMoreMenuItemClicked(@NotNull String appId, @NotNull String path, @NotNull String menuItemId, @Nullable String appInfo, @Nullable Bitmap bitmap, @NotNull IAppletCallback iAppletCallback) {
                Map<String, Object> params = new HashMap<>();
                params.put("appId", appId);
                params.put("path", path);
                params.put("menuId", menuItemId);
                params.put("appInfo", appInfo);
                handler.post(() -> {
                    sendEvent("extensionApi:onCustomMenuClick", params, new FinCallback() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.d(TAG, "onCustomMenuClick success");
                            iAppletCallback.onSuccess(null);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "onCustomMenuClick errorCode : " + i + " errorMessage : " + s);
                            iAppletCallback.onFailure();
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                });
            }

            @Override
            public void onNavigationBarCloseButtonClicked(@NotNull String s) {

            }
        });
        Log.d(TAG, "registerAppletHandler");
    }

    @ReactMethod
    public void addWebExtentionApi(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        String name = (String) param.get("name");
        Log.d(TAG, "addWebExtentionApi:" + name);
        FinAppClient.INSTANCE.getExtensionWebApiManager().registerApi(new com.finogeeks.lib.applet.api.BaseApi(reactContext) {
            @Override
            public String[] apis() {
                return new String[]{name};
            }

            @Override
            public void invoke(String s, JSONObject jsonObject, com.finogeeks.lib.applet.interfaces.ICallback iCallback) {
                Log.d("MopPlugin", "invoke webExtentionApi:" + s + ",params:" + jsonObject);
                Map params = new Gson().fromJson(jsonObject.toString(), HashMap.class);
                handler.post(() -> {
                    sendEvent("webExtentionApi:" + name, params, new FinCallback<Dynamic>() {
                        @Override
                        public void onSuccess(Dynamic o) {
                            String json = null;
                            if (o != null && o.asMap() != null) {
                                json = new Gson().toJson(o.asMap().toHashMap());
                            }
                            Log.d(TAG, "channel invokeMethod:" + name
                                    + " success, result=" + o + ", json=" + json);
                            JSONObject ret = null;
                            if (json != null && !json.equals("null")) {
                                try {
                                    ret = new JSONObject(json);
                                    if (ret.has("errMsg")) {
                                        String errMsg = ret.getString("errMsg");
                                        if (errMsg.startsWith(name + ":fail")) {
                                            iCallback.onFail(ret);
                                            return;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            iCallback.onSuccess(ret);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "channel invokeMethod:" + name
                                    + " error, errorCode=" + i
                                    + ", errorMessage=" + s
                                    + ", errorDetails=");
                            iCallback.onFail();
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                });
            }
        });
    }

    @ReactMethod
    public void sendCustomEvent(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        String appId = (String) param.get("appId");
        Map eventData = (Map) param.get("eventData");
        Log.d(TAG, "sendCustomEvent:" + appId);
        if (appId != null) {
            FinAppClient.INSTANCE.getAppletApiManager().sendCustomEvent(appId, eventData == null ? "" : new Gson().toJson(eventData));
        }
    }

    @ReactMethod
    public void setActivityTransitionAnim(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        String anim = (String) param.get("anim");
        Log.d(TAG, "setActivityTransitionAnim:" + anim);
        if ("SlideFromLeftToRightAnim".equals(anim)) {
            FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromLeftToRightAnim.INSTANCE);
        } else if ("SlideFromRightToLeftAnim".equals(anim)) {
            FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromRightToLeftAnim.INSTANCE);
        } else if ("SlideFromTopToBottomAnim".equals(anim)) {
            FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromTopToBottomAnim.INSTANCE);
        } else if ("SlideFromBottomToTopAnim".equals(anim)) {
            FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromBottomToTopAnim.INSTANCE);
        } else if ("FadeInAnim".equals(anim)) {
            FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(FadeInAnim.INSTANCE);
        } else if ("NoneAnim".equals(anim)) {
            FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(NoneAnim.INSTANCE);
        }
    }

    @ReactMethod
    public void registerExtensionApi(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        String name = (String) param.get("name");
        Log.d(TAG, "registerExtensionApi:" + name);
        FinAppClient.INSTANCE.getExtensionApiManager().registerApi(new com.finogeeks.lib.applet.api.BaseApi(reactContext) {
            @Override
            public String[] apis() {
                return new String[]{name};
            }

            @Override
            public void invoke(String s, JSONObject jsonObject, com.finogeeks.lib.applet.interfaces.ICallback iCallback) {
                Log.d("MopPlugin", "invoke extensionApi:" + s + ",params:" + jsonObject);
                Map params = new Gson().fromJson(jsonObject.toString(), HashMap.class);
                handler.post(() -> {
                    sendEvent("extensionApi:" + name, params, new FinCallback<Dynamic>() {
                        @Override
                        public void onSuccess(Dynamic o) {
                            String json = null;
                            if (o != null && o.asMap() != null) {
                                json = new Gson().toJson(o.asMap().toHashMap());
                            }
                            Log.d(TAG, "channel invokeMethod:" + name
                                    + " success, result=" + o + ", json=" + json);
                            JSONObject ret = null;
                            if (json != null && !json.equals("null")) {
                                try {
                                    ret = new JSONObject(json);
                                    if (ret.has("errMsg")) {
                                        String errMsg = ret.getString("errMsg");
                                        if (errMsg.startsWith(name + ":fail")) {
                                            iCallback.onFail(ret);
                                            return;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            iCallback.onSuccess(ret);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "channel invokeMethod:" + name
                                    + " error, errorCode=" + i
                                    + ", errorMessage=" + s
                                    + ", errorDetails=");
                            iCallback.onFail();
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                });
            }
        });
    }

    @ReactMethod
    public void callJS(ReadableMap params, Callback callback) {
        Map<String, Object> param = params.toHashMap();
        String appId = (String) param.get("appId");
        String eventName = (String) param.get("eventName");
        String nativeViewId = (String) param.get("nativeViewId");
        int viewId = 0;
        if (nativeViewId != null && !nativeViewId.equals("")) {
            try {
                viewId = Integer.parseInt(nativeViewId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map eventData = (Map) param.get("eventData");
        Log.d(TAG, "callJS:" + appId);
        if (appId != null && eventName != null) {
            FinAppClient.INSTANCE.getAppletApiManager().callJS(appId, eventName, eventData == null ? "" : new Gson().toJson(eventData),
                    viewId, new FinCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Map<String, Object> res = new HashMap<>();
                            res.put("data", s);
                            callback.invoke(success(res));
                        }

                        @Override
                        public void onError(int i, String s) {
                            callback.invoke(fail(s));
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
        } else {
            callback.invoke(fail(""));
        }
    }

    @ReactMethod
    public void getBindApplets(ReadableMap params, final Callback callback) {
        String apiServer = InitUtils.getStringVal(params, "apiServer");
        String appClass = InitUtils.getStringVal(params, "appClass");
        int appStatusNumber = InitUtils.getIntVal(params, "appStatus", 0);
        boolean containForbiddenApp = InitUtils.getBooleanVal(params, "containForbiddenApp", false);
        int pageNo = InitUtils.getIntVal(params, "pageNo", 1);
        int pageSize = InitUtils.getIntVal(params, "pageSize", 20);

        AppletStatus appStatus = InitUtils.intToAppletStatus(appStatusNumber);

        FetchBindAppletRequest request = new FetchBindAppletRequest(apiServer, appClass, appStatus, containForbiddenApp, pageNo, pageSize);
        FinAppClient.INSTANCE.getAppletApiManager().getBindApplets(
                request, new FinCallback<FetchBindAppletResponse>() {
                    @Override
                    public void onSuccess(FetchBindAppletResponse fetchBindAppletResponse) {
                        int total = fetchBindAppletResponse.getTotal();
                        List<FetchBindAppletInfo> items = fetchBindAppletResponse.getItems();

                        // 将 FetchBindAppletInfo 对象转换为 Map 数组
                        List<Map<String, Object>> itemMaps = new ArrayList<>();
                        for (FetchBindAppletInfo info : items) {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("apiServer", info.getApiServer());
                            itemMap.put("miniAppId", info.getMiniAppId());
                            itemMap.put("name", info.getName());
                            itemMap.put("logo", info.getLogo());
                            itemMap.put("appClass", info.getAppClass());
                            itemMap.put("displayStatus", info.getDisplayStatus() != null ? info.getDisplayStatus().ordinal() : null);
                            itemMap.put("isForbidden", info.isForbidden());
                            itemMap.put("desc", info.getDesc());
                            itemMap.put("detailDesc", info.getDetailDesc());
                            itemMaps.add(itemMap);
                        }

                        Map<String, Object> res = new HashMap<>();
                        res.put("total", total);
                        res.put("items", itemMaps);
                        callback.invoke(success(res));
                    }

                    @Override
                    public void onError(int i, String s) {
                        callback.invoke(fail(s));
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
    }

}
