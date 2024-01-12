package com.finogeeks.mop.rnsdk;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.finogeeks.lib.applet.anim.FadeInAnim;
import com.finogeeks.lib.applet.anim.NoneAnim;
import com.finogeeks.lib.applet.anim.SlideFromBottomToTopAnim;
import com.finogeeks.lib.applet.anim.SlideFromLeftToRightAnim;
import com.finogeeks.lib.applet.anim.SlideFromRightToLeftAnim;
import com.finogeeks.lib.applet.anim.SlideFromTopToBottomAnim;
import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.client.FinAppInfo;
import com.finogeeks.lib.applet.client.FinStoreConfig;
import com.finogeeks.lib.applet.db.entity.FinApplet;
import com.finogeeks.lib.applet.interfaces.FinCallback;
import com.finogeeks.lib.applet.sdk.api.request.IFinAppletRequest;
import com.finogeeks.lib.applet.page.view.moremenu.MoreMenuItem;
import com.finogeeks.lib.applet.page.view.moremenu.MoreMenuType;
import com.finogeeks.lib.applet.rest.model.GrayAppletVersionConfig;
import com.finogeeks.lib.applet.sdk.api.IAppletHandler;
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

    private Map<String, FinCallback> callbacks = new HashMap<>();

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
    public void initialize(ReadableMap params, Callback callback) {
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
        String cryptType = (String) param.get("cryptType");
        if (cryptType == null || cryptType.isEmpty()) {
            cryptType = "MD5";
        }
        if (param.get("apiServer") != null) {
            apiServer = String.valueOf(param.get("apiServer"));
        }
        if (param.get("apiPrefix") != null) {
            apiPrefix = String.valueOf(param.get("apiPrefix"));
            if (!apiPrefix.endsWith("/")) {
                apiPrefix = apiPrefix + "/";
            }
        }
        Boolean disablePermission = (Boolean) param.get("disablePermission");
        if (disablePermission == null) {
            disablePermission = false;
        }
        String userId = "";
        if (param.get("userId") != null) {
            userId = (String) param.get("userId");
        }

        Boolean encryptServerData = (Boolean) param.get("encryptServerData");
        if (encryptServerData == null) encryptServerData = false;
        Boolean debug = (Boolean) param.get("debug");
        if (debug == null) debug = false;
        Boolean bindAppletWithMainProcess = (Boolean) param.get("bindAppletWithMainProcess");
        if (bindAppletWithMainProcess == null) bindAppletWithMainProcess = false;

        String customWebViewUserAgent = (String) param.get("customWebViewUserAgent");
        Integer appletIntervalUpdateLimit = (Integer) param.get("appletIntervalUpdateLimit");
        Integer maxRunningApplet = (Integer) param.get("maxRunningApplet");
        Gson gson = new Gson();
        List<FinStoreConfig> finStoreConfigs = null;
        if (param.get("finStoreConfigs") != null) {
            finStoreConfigs = new ArrayList<>();
            List<Map<String, Object>> configs = (List<Map<String, Object>>) param.get("finStoreConfigs");
            for (Map<String, Object> config : configs) {
                for (String key : config.keySet()) {
                    String sdkKey = (String) config.get("sdkKey");
                    String sdkSecret = (String) config.get("sdkSecret");
                    String apiUrl = (String) config.get("apiServer");
                    String apmUrl = (String) config.get("apmServer");
                    if (apmUrl == null) apmUrl = "";
                    String fingerprint = (String) config.get("fingerprint");
                    if (fingerprint == null) fingerprint = "";
                    String encryptType = (String) config.get("cryptType");
                    Boolean encryptServerData1 = (Boolean) config.get("encryptServerData");
                    if (encryptServerData1 == null) encryptServerData1 = false;
                    finStoreConfigs.add(new FinStoreConfig(sdkKey, sdkSecret, apiUrl, apmUrl, "", fingerprint, encryptType, encryptServerData1));
                }
            }
        }
        FinAppConfig.UIConfig uiConfig = new FinAppConfig.UIConfig();
        if (param.get("uiConfig") != null) {
            uiConfig = gson.fromJson(gson.toJson(param.get("uiConfig")), FinAppConfig.UIConfig.class);
        }
        uiConfig.setLoadingLayoutCls(FINMopCustomLoadingPage.class);


        FinAppConfig.Builder builder = new FinAppConfig.Builder()
                .setSdkKey(appkey)
                .setSdkSecret(secret)
                .setApiUrl(apiServer)
                .setApiPrefix(apiPrefix)
                .setEncryptionType(cryptType)
                .setEncryptServerData(encryptServerData)
                .setUserId(userId)
                .setDebugMode(debug)
                .setDisableRequestPermissions(disablePermission)
                .setBindAppletWithMainProcess(bindAppletWithMainProcess);

        if (customWebViewUserAgent != null)
            builder.setCustomWebViewUserAgent(customWebViewUserAgent);
        if (appletIntervalUpdateLimit != null)
            builder.setAppletIntervalUpdateLimit(appletIntervalUpdateLimit);
        if (maxRunningApplet != null) builder.setMaxRunningApplet(maxRunningApplet);
        if (finStoreConfigs != null) builder.setFinStoreConfigs(finStoreConfigs);
        if (uiConfig != null) builder.setUiConfig(uiConfig);

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
        Log.d(TAG, "config:" + gson.toJson(config));

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
    public void initSDK(ReadableMap params, Callback callback) {
        Map<String, Object> param = params.toHashMap();
        if (FinAppClient.INSTANCE.isFinAppProcess(reactContext)) {
            // 小程序进程不执行任何初始化操作
            return;
        }

        // 从 param 中提取 config 和 uiConfig 字典
        ReadableMap configMap = params.getMap("config");
        ReadableMap uiConfigMap = params.getMap("uiConfig");

        // 对 configMap 和 uiConfigMap 进行必要的验证
        // ...

        Gson gson = new Gson();
        FinAppConfig config = null;
        FinAppConfig.UIConfig uiConfig = null;

        if (configMap != null) {
            config = gson.fromJson(gson.toJson(configMap.toHashMap()), FinAppConfig.class);
        }
        if (uiConfigMap != null) {
            uiConfig = gson.fromJson(gson.toJson(uiConfigMap.toHashMap()), FinAppConfig.UIConfig.class);
        }

        // 构建 FinAppConfig 对象
        FinAppConfig.Builder builder = new FinAppConfig.Builder();
        if (config != null) {
            // 设置 config 相关的属性
            // ...
        }
        if (uiConfig != null) {
            builder.setUiConfig(uiConfig);
        }

        FinAppConfig finalConfig = builder.build();
        Log.d(TAG, "config:" + gson.toJson(finalConfig));

        // SDK 初始化结果回调
        FinCallback<Object> cb = new FinCallback<Object>() {
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
                FinAppClient.INSTANCE.init(reactContext.getCurrentActivity().getApplication(), finalConfig, cb);
            }
        });
    }


    @ReactMethod
    public void openApplet(ReadableMap map, Callback callback) {
        Map<String, Object> param = map.toHashMap();
        if (param.get("appId") == null) {
            callback.invoke(fail("appId不能为空"));
            return;
        }
        Log.d(TAG, "openApplet:params:" + param);
        String appId = String.valueOf(param.get("appId"));
        Integer sequence = (Integer) param.get("sequence");
        String apiServer = (String) param.get("apiServer");

        if (apiServer == null) {
            apiServer = "";
        }
        Map<String, String> startParams = new HashMap<>();
        if (param.get("params") != null) {
            startParams = (Map<String, String>) param.get("params");
        }

        FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext,
                IFinAppletRequest.Companion.fromAppId(apiServer, appId)
                        .setSequence(sequence)
                        .setStartParams(startParams),
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
    public void qrcodeOpenApplet(ReadableMap params) {
        Log.d(TAG, "qrcodeOpenApplet");
        Map<String, Object> param = params.toHashMap();
        String qrcode = String.valueOf(param.get("qrcode"));
        FinAppClient.INSTANCE.getAppletApiManager().startAppletByQrcode(reactContext, qrcode, new FinCallback<String>() {
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
                CountDownLatch latch = new CountDownLatch(1);
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
    public void finishRunningApplet(ReadableMap params) {
        Map<String, Object> param = params.toHashMap();
        Log.d(TAG, "finishRunningApplet");
        if (param.containsKey("appletId") && param.get("appletId") instanceof String) {
            String appId = (String) param.get("appletId");
            FinAppClient.INSTANCE.getAppletApiManager().finishRunningApplet(appId);
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

}
