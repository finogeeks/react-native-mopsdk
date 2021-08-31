package com.finogeeks.mop.rnsdk;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;


import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.interfaces.FinCallback;

import java.util.HashMap;
import java.util.Map;


public class FINMopsdkModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public FINMopsdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "FINMopsdk";
    }

    @ReactMethod
    public void initialize(String appkey, String secret,String apiServer,String apiPrefix, Callback callback) {
        // TODO: Implement some actually useful functionality
        // callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);

        if (FinAppClient.INSTANCE.isFinAppProcess(reactContext)) {
            // 小程序进程不执行任何初始化操作
            return;
        }
        if (apiServer == null) {
          apiServer = "https://mp.finogeeks.com";
        }
        if (!apiPrefix.endsWith("/")) {
            apiPrefix = apiPrefix + "/";
        }
        FinAppConfig config = new FinAppConfig.Builder()
                .setAppKey(appkey)
                .setAppSecret(secret)
                .setApiUrl(apiServer)
                .setApiPrefix(apiPrefix)
                .setGlideWithJWT(false)
                .build();
        // SDK初始化结果回调，用于接收SDK初始化状态
        FinCallback<Object> cb = new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                // SDK初始化成功
                callback.invoke("{success: true,errMsg: \"initialize:ok\"}");
            }

            @Override
            public void onError(int code, String error) {
                // SDK初始化失败
                callback.invoke("{success: false,errMsg: \""+error+"\"}");
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
    public void openApplet(String appId, String path,String query,String sequence, Callback callback) {
        // TODO: Implement some actually useful functionality
        // callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
        Map<String, String> params = null;
        if (path != null) {
            params = new HashMap<>();
            params.put("path", path);
            params.put("query", query);
        }
        if (params == null) {
            if (sequence == null) {
                FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext, appId);
            } else {
                FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext, appId, Integer.parseInt(sequence), null);
            }
        } else {
            FinAppClient.INSTANCE.getAppletApiManager().startApplet(reactContext, appId, params);
        }
        callback.invoke("{success: true, errMsg: \"openApplet:ok\"}");
    }
}
