package com.finogeeks.mop.rnsdk.util;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.client.FinAppConfigPriority;
import com.finogeeks.lib.applet.sdk.model.AppletStatus;
import com.finogeeks.lib.applet.utils.ColorUtil;

import java.util.HashMap;
import java.util.Map;

public class InitUtils {

    public static final String COLOR_TEXT_WHITE = "#ffffffff";
    public static final String COLOR_TEXT_BLACK = "#ff000000";

    private static float covertNumToFloat(Object obj) {
        if (obj instanceof Float) {
            return ((Float) obj).floatValue();
        } else if (float.class.isInstance(obj)) {
            return (float) obj;
        } else if (obj instanceof Double) {
            return ((Double) obj).floatValue();
        } else if (double.class.isInstance(obj)) {
            return (float) obj;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).floatValue();
        } else if (int.class.isInstance(obj)) {
            return (float) obj;
        }
        throw new IllegalArgumentException("Unsupported argument type " + obj.getClass().getName());
    }

    public static FinAppConfig.UIConfig createUIConfigFromMap(ReadableMap map) {
        if (map != null) {
            FinAppConfig.UIConfig uiConfig = new FinAppConfig.UIConfig();
            Integer navigationBarTitleLightColor = getColor(map, "navigationBarTitleLightColor");
            if (navigationBarTitleLightColor != null) {
                uiConfig.setNavigationBarTitleLightColor(navigationBarTitleLightColor);
            }
            Integer navigationBarTitleDarkColor = getColor(map, "navigationBarTitleDarkColor");
            if (navigationBarTitleDarkColor != null) {
                uiConfig.setNavigationBarTitleDarkColor(navigationBarTitleDarkColor);
            }

            Integer navigationBarBackBtnLightColor = getColor(map, "navigationBarBackBtnLightColor");
            if (navigationBarBackBtnLightColor != null) {
                uiConfig.setNavigationBarBackBtnLightColor(navigationBarBackBtnLightColor);
            }
            Boolean isAlwaysShowBackInDefaultNavigationBar = getBooleanVal(map, "isAlwaysShowBackInDefaultNavigationBar", null);
            if (isAlwaysShowBackInDefaultNavigationBar != null) {
                uiConfig.setAlwaysShowBackInDefaultNavigationBar(isAlwaysShowBackInDefaultNavigationBar);
            }
            Boolean isClearNavigationBarNavButtonBackground = getBooleanVal(map, "isClearNavigationBarNavButtonBackground", null);
            if (isClearNavigationBarNavButtonBackground != null) {
                uiConfig.setClearNavigationBarNavButtonBackground(isClearNavigationBarNavButtonBackground);
            }
            Boolean isHideFeedbackAndComplaints = getBooleanVal(map, "isHideFeedbackAndComplaints");
            if (isHideFeedbackAndComplaints != null) {
                uiConfig.setHideFeedbackAndComplaints(isHideFeedbackAndComplaints);
            }
            Boolean isHideBackHome = getBooleanVal(map, "isHideBackHome");
            if (isHideBackHome != null) {
                uiConfig.setHideBackHome(isHideBackHome);
            }
            Boolean isHideForwardMenu = getBooleanVal(map, "isHideForwardMenu");
            if (isHideForwardMenu != null) {
                uiConfig.setHideForwardMenu(isHideForwardMenu);
            }

            Boolean isHideRefreshMenu = getBooleanVal(map, "isHideRefreshMenu");
            if (isHideRefreshMenu != null) {
                uiConfig.setHideRefreshMenu(isHideRefreshMenu);
            }

            Boolean isHideShareAppletMenu = getBooleanVal(map, "isHideShareAppletMenu");
            if (isHideShareAppletMenu != null) {
                uiConfig.setHideShareAppletMenu(isHideShareAppletMenu);
            }


            Boolean isHideAddToDesktopMenu = getBooleanVal(map, "isHideAddToDesktopMenu");
            if (isHideAddToDesktopMenu != null) {
                uiConfig.setHideAddToDesktopMenu(isHideAddToDesktopMenu);
            }

            Boolean isHideFavoriteMenu = getBooleanVal(map, "isHideFavoriteMenu");
            if (isHideFavoriteMenu != null) {
                uiConfig.setHideFavoriteMenu(isHideFavoriteMenu);
            }

            Boolean isHideClearCacheMenu = getBooleanVal(map, "isHideClearCacheMenu");
            if (isHideClearCacheMenu != null) {
                uiConfig.setHideClearCacheMenu(isHideClearCacheMenu);
            }
            Boolean isHideSettingMenu = getBooleanVal(map, "isHideSettingMenu");
            if (isHideSettingMenu != null) {
                uiConfig.setHideSettingMenu(isHideSettingMenu);
            }

            Boolean hideTransitionCloseButton = getBooleanVal(map, "hideTransitionCloseButton");
            if (hideTransitionCloseButton != null) {
                uiConfig.setHideTransitionCloseButton(hideTransitionCloseButton);
            }

            Boolean useNativeLiveComponent = getBooleanVal(map, "useNativeLiveComponent");
            if (useNativeLiveComponent != null) {
                uiConfig.setUseNativeLiveComponent(useNativeLiveComponent);
            }

            ReadableMap capsuleConfigMap = map.getMap("capsuleConfig");
            if (capsuleConfigMap != null) {
                FinAppConfig.UIConfig.CapsuleConfig capsuleConfig = new FinAppConfig.UIConfig.CapsuleConfig();
                Double capsuleWidth = getDoubleVal(capsuleConfigMap, "capsuleWidth");
                if (capsuleWidth != null) {
                    capsuleConfig.capsuleWidth = capsuleWidth.floatValue();
                }
                Double capsuleHeight = getDoubleVal(capsuleConfigMap, "capsuleHeight");
                if (capsuleHeight != null) {
                    capsuleConfig.capsuleHeight = capsuleHeight.floatValue();
                }
                Double capsuleRightMargin = getDoubleVal(capsuleConfigMap, "capsuleRightMargin");
                if (capsuleRightMargin != null) {
                    capsuleConfig.capsuleRightMargin = capsuleRightMargin.floatValue();
                }

                Double capsuleCornerRadius = getDoubleVal(capsuleConfigMap, "capsuleCornerRadius");
                if (capsuleCornerRadius != null) {
                    capsuleConfig.capsuleCornerRadius = capsuleCornerRadius.floatValue();
                }

                Double capsuleBorderWidth = getDoubleVal(capsuleConfigMap, "capsuleBorderWidth");
                if (capsuleBorderWidth != null) {
                    capsuleConfig.capsuleBorderWidth = capsuleBorderWidth.floatValue();
                }

                Integer capsuleBgLightColor = getColor(capsuleConfigMap, "capsuleBgLightColor");
                if (capsuleBgLightColor != null) {
                    capsuleConfig.capsuleBgLightColor = capsuleBgLightColor;
                }
                Integer capsuleBgDarkColor = getColor(capsuleConfigMap, "capsuleBgDarkColor");
                if (capsuleBgDarkColor != null) {
                    capsuleConfig.capsuleBgDarkColor = capsuleBgDarkColor;
                }
                Integer capsuleBorderLightColor = getColor(capsuleConfigMap, "capsuleBorderLightColor");
                if (capsuleBorderLightColor != null) {
                    capsuleConfig.capsuleBorderLightColor = capsuleBorderLightColor;
                }
                Integer capsuleBorderDarkColor = getColor(capsuleConfigMap, "capsuleBorderDarkColor");
                if (capsuleBorderDarkColor != null) {
                    capsuleConfig.capsuleBorderDarkColor = capsuleBorderDarkColor;
                }
                Integer capsuleDividerLightColor = getColor(capsuleConfigMap, "capsuleDividerLightColor");
                if (capsuleDividerLightColor != null) {
                    capsuleConfig.capsuleDividerLightColor = capsuleDividerLightColor;
                }

                Integer capsuleDividerDarkColor = getColor(capsuleConfigMap, "capsuleDividerDarkColor");
                if (capsuleDividerDarkColor != null) {
                    capsuleConfig.capsuleDividerDarkColor = capsuleDividerDarkColor;
                }
                Integer moreLightImage = getIntVal(capsuleConfigMap, "moreLightImage");
                if (moreLightImage != null) {
                    capsuleConfig.moreLightImage = moreLightImage;
                }
                Integer moreDarkImage = getIntVal(capsuleConfigMap, "moreDarkImage");
                if (moreDarkImage != null) {
                    capsuleConfig.moreDarkImage = moreDarkImage;
                }

                Double moreBtnWidth = getDoubleVal(capsuleConfigMap, "moreBtnWidth");
                if (moreBtnWidth != null) {
                    capsuleConfig.moreBtnWidth = moreBtnWidth.floatValue();
                }
                Double moreBtnLeftMargin = getDoubleVal(capsuleConfigMap, "moreBtnLeftMargin");
                if (moreBtnLeftMargin != null) {
                    capsuleConfig.moreBtnLeftMargin = moreBtnLeftMargin.floatValue();
                }
                Integer closeLightImage = getIntVal(capsuleConfigMap, "closeLightImage");
                if (closeLightImage != null) {
                    capsuleConfig.closeLightImage = closeLightImage;
                }

                Integer closeDarkImage = getIntVal(capsuleConfigMap, "closeDarkImage");
                if (closeDarkImage != null) {
                    capsuleConfig.closeDarkImage = closeDarkImage;
                }
                Double closeBtnWidth = getDoubleVal(capsuleConfigMap, "closeBtnWidth");
                if (closeBtnWidth != null) {
                    capsuleConfig.closeBtnWidth = closeBtnWidth.floatValue();
                }
                Double closeBtnLeftMargin = getDoubleVal(capsuleConfigMap, "closeBtnLeftMargin");
                if (closeBtnLeftMargin != null) {
                    capsuleConfig.closeBtnLeftMargin = closeBtnLeftMargin.floatValue();
                }
                uiConfig.setCapsuleConfig(capsuleConfig);
            }


            ReadableMap navHomeConfigMap = map.getMap("navHomeConfig");
            if (navHomeConfigMap != null) {
                FinAppConfig.UIConfig.NavHomeConfig navHomeConfig = new FinAppConfig.UIConfig.NavHomeConfig();
                Double width = getDoubleVal(navHomeConfigMap, "width");
                if (width != null) {
                    navHomeConfig.width = width.floatValue();
                }
                Double height = getDoubleVal(navHomeConfigMap, "height");
                if (height != null) {
                    navHomeConfig.height = height.floatValue();
                }
                Double leftMargin = getDoubleVal(navHomeConfigMap, "leftMargin");
                if (leftMargin != null) {
                    navHomeConfig.leftMargin = leftMargin.floatValue();
                }
                Double cornerRadius = getDoubleVal(navHomeConfigMap, "cornerRadius");
                if (cornerRadius != null) {
                    navHomeConfig.cornerRadius = cornerRadius.floatValue();
                }
                Double borderWidth = getDoubleVal(navHomeConfigMap, "borderWidth");
                if (borderWidth != null) {
                    navHomeConfig.borderWidth = borderWidth.floatValue();
                }

                Integer borderLightColor = getColor(navHomeConfigMap, "borderLightColor");
                if (borderLightColor != null) {
                    navHomeConfig.borderLightColor = borderLightColor;
                }
                Integer borderDarkColor = getColor(navHomeConfigMap, "borderDarkColor");
                if (borderDarkColor != null) {
                    navHomeConfig.borderDarkColor = borderDarkColor;
                }
                Integer bgLightColor = getColor(navHomeConfigMap, "bgLightColor");
                if (bgLightColor != null) {
                    navHomeConfig.bgLightColor = bgLightColor;
                }
                Integer bgDarkColor = getColor(navHomeConfigMap, "bgDarkColor");
                if (bgDarkColor != null) {
                    navHomeConfig.bgDarkColor = bgDarkColor;
                }
                uiConfig.setNavHomeConfig(navHomeConfig);
            }
            ReadableMap authViewConfigMap = map.getMap("authViewConfig");
            if (authViewConfigMap != null) {
                FinAppConfig.UIConfig.AuthViewConfig authViewConfig = new FinAppConfig.UIConfig.AuthViewConfig();
                Double appletNameTextSize = getDoubleVal(authViewConfigMap, "appletNameTextSize");
                if (appletNameTextSize != null) {
                    authViewConfig.appletNameTextSize = appletNameTextSize.floatValue();
                }
                Integer appletNameLightColor = getColor(authViewConfigMap, "appletNameLightColor");
                if (appletNameLightColor != null) {
                    authViewConfig.appletNameLightColor = appletNameLightColor;
                }

                Integer appletNameDarkColor = getColor(authViewConfigMap, "appletNameDarkColor");
                if (appletNameDarkColor != null) {
                    authViewConfig.appletNameDarkColor = appletNameDarkColor;
                }

                Double authorizeTitleTextSize = getDoubleVal(authViewConfigMap, "authorizeTitleTextSize");
                if (authorizeTitleTextSize != null) {
                    authViewConfig.authorizeTitleTextSize = authorizeTitleTextSize.floatValue();
                }
                Integer authorizeTitleLightColor = getColor(authViewConfigMap, "authorizeTitleLightColor");
                if (authorizeTitleLightColor != null) {
                    authViewConfig.authorizeTitleLightColor = authorizeTitleLightColor;
                }
                Integer authorizeTitleDarkColor = getColor(authViewConfigMap, "authorizeTitleDarkColor");
                if (authorizeTitleDarkColor != null) {
                    authViewConfig.authorizeTitleDarkColor = authorizeTitleDarkColor;
                }
                Double authorizeDescriptionTextSize = getDoubleVal(authViewConfigMap, "authorizeDescriptionTextSize");
                if (authorizeDescriptionTextSize != null) {
                    authViewConfig.authorizeDescriptionTextSize = authorizeDescriptionTextSize.floatValue();
                }
                Integer authorizeDescriptionLightColor = getColor(authViewConfigMap, "authorizeDescriptionLightColor");
                if (authorizeDescriptionLightColor != null) {
                    authViewConfig.authorizeDescriptionLightColor = authorizeDescriptionLightColor;
                }
                Integer authorizeDescriptionDarkColor = getColor(authViewConfigMap, "authorizeDescriptionDarkColor");
                if (authorizeDescriptionDarkColor != null) {
                    authViewConfig.authorizeDescriptionDarkColor = authorizeDescriptionDarkColor;
                }
                Double agreementTitleTextSize = getDoubleVal(authViewConfigMap, "agreementTitleTextSize");
                if (agreementTitleTextSize != null) {
                    authViewConfig.agreementTitleTextSize = agreementTitleTextSize.floatValue();
                }
                Integer agreementTitleLightColor = getColor(authViewConfigMap, "agreementTitleLightColor");
                if (agreementTitleLightColor != null) {
                    authViewConfig.agreementTitleLightColor = agreementTitleLightColor;
                }
                Integer agreementTitleDarkColor = getColor(authViewConfigMap, "agreementTitleDarkColor");
                if (agreementTitleDarkColor != null) {
                    authViewConfig.agreementTitleDarkColor = agreementTitleDarkColor;
                }
                Double agreementDescriptionTextSize = getDoubleVal(authViewConfigMap, "agreementDescriptionTextSize");
                if (agreementDescriptionTextSize != null) {
                    authViewConfig.agreementDescriptionTextSize = agreementDescriptionTextSize.floatValue();
                }
                Integer agreementDescriptionLightColor = getColor(authViewConfigMap, "agreementDescriptionLightColor");
                if (agreementDescriptionLightColor != null) {
                    authViewConfig.agreementDescriptionLightColor = agreementDescriptionLightColor;
                }
                Integer agreementDescriptionDarkColor = getColor(authViewConfigMap, "agreementDescriptionDarkColor");
                if (agreementDescriptionDarkColor != null) {
                    authViewConfig.agreementDescriptionDarkColor = agreementDescriptionDarkColor;
                }
                Integer linkLightColor = getColor(authViewConfigMap, "linkLightColor");
                if (linkLightColor != null) {
                    authViewConfig.linkLightColor = linkLightColor;
                }
                Integer linkDarkColor = getColor(authViewConfigMap, "linkDarkColor");
                if (linkDarkColor != null) {
                    authViewConfig.linkDarkColor = linkDarkColor;
                }

                ReadableMap allowButtonLightConfig = authViewConfigMap.getMap("allowButtonLightConfig");
                if (allowButtonLightConfig != null) {
                    authViewConfig.allowButtonLightConfig = getAuthButtonConfig(allowButtonLightConfig);
                }
                ReadableMap allowButtonDarkConfig = authViewConfigMap.getMap("allowButtonDarkConfig");
                if (allowButtonDarkConfig != null) {
                    authViewConfig.allowButtonDarkConfig = getAuthButtonConfig(allowButtonDarkConfig);
                }
                ReadableMap rejectButtonLightConfig = authViewConfigMap.getMap("rejectButtonLightConfig");
                if (rejectButtonLightConfig != null) {
                    authViewConfig.rejectButtonLightConfig = getAuthButtonConfig(rejectButtonLightConfig);
                }
                ReadableMap rejectButtonDarkConfig = authViewConfigMap.getMap("rejectButtonDarkConfig");
                if (rejectButtonDarkConfig != null) {
                    authViewConfig.rejectButtonDarkConfig = getAuthButtonConfig(rejectButtonDarkConfig);
                }
                uiConfig.setAuthViewConfig(authViewConfig);
            }
            ReadableMap floatWindowConfigMap = map.getMap("floatWindowConfig");
            if (floatWindowConfigMap != null) {
                FinAppConfig.UIConfig.FloatWindowConfig floatWindowConfig = new FinAppConfig.UIConfig.FloatWindowConfig();
                Boolean floatMode = getBooleanVal(floatWindowConfigMap, "floatMode");
                if (floatMode != null) {
                    floatWindowConfig.floatMode = floatMode;
                }
                Integer x = getIntVal(floatWindowConfigMap, "x");
                if (x != null) {
                    floatWindowConfig.x = x;
                }
                Integer y = getIntVal(floatWindowConfigMap, "y");
                if (y != null) {
                    floatWindowConfig.y = y;
                }
                Integer width = getIntVal(floatWindowConfigMap, "width");
                if (width != null) {
                    floatWindowConfig.width = width;
                }
                Integer height = getIntVal(floatWindowConfigMap, "height");
                if (height != null) {
                    floatWindowConfig.height = height;
                }
                uiConfig.setFloatWindowConfig(floatWindowConfig);
            }
            Integer webViewProgressBarColor = getColor(map, "webViewProgressBarColor");
            if (webViewProgressBarColor != null) {
                uiConfig.setWebViewProgressBarColor(webViewProgressBarColor);
            }
            Boolean hideWebViewProgressBar = getBooleanVal(map, "hideWebViewProgressBar");
            if (hideWebViewProgressBar != null) {
                uiConfig.setHideWebViewProgressBar(hideWebViewProgressBar);
            }
            Integer moreMenuStyle = getIntVal(map, "moreMenuStyle");
            if (moreMenuStyle != null) {
                uiConfig.setMoreMenuStyle(moreMenuStyle);
            }
            Integer isHideBackHomePriorityIndex = getIntVal(map, "isHideBackHomePriority");
            if (isHideBackHomePriorityIndex == 0) {
                uiConfig.setIsHideBackHomePriority(FinAppConfigPriority.GLOBAL);
            } else if (isHideBackHomePriorityIndex == 1) {
                uiConfig.setIsHideBackHomePriority(FinAppConfigPriority.SPECIFIED);
            } else if (isHideBackHomePriorityIndex == 2) {
                uiConfig.setIsHideBackHomePriority(FinAppConfigPriority.APPLET_FILE);
            }
            Boolean autoAdaptDarkMode = getBooleanVal(map, "autoAdaptDarkMode");
            if (autoAdaptDarkMode != null) {
                uiConfig.setAutoAdaptDarkMode(autoAdaptDarkMode);
            }
            Boolean disableSlideCloseAppletGesture = getBooleanVal(map, "disableSlideCloseAppletGesture");
            if (disableSlideCloseAppletGesture != null) {
                uiConfig.setDisableSlideCloseAppletGesture(disableSlideCloseAppletGesture);
            }
            String loadingLayoutCls = getStringVal(map, "loadingLayoutCls");
            if (loadingLayoutCls != null) {
                uiConfig.setLoadingLayoutCls(loadingLayoutCls);
            }
            return uiConfig;
        }
        return null;
    }

    private static FinAppConfig.UIConfig.AuthViewConfig.AuthButtonConfig getAuthButtonConfig(ReadableMap map) {
        return new FinAppConfig.UIConfig.AuthViewConfig.AuthButtonConfig(getDoubleVal(map, "cornerRadius", 0.0).floatValue(),
                getColor(map, "normalBackgroundColor", Color.TRANSPARENT),
                getColor(map, "pressedBackgroundColor", Color.TRANSPARENT),
                getColor(map, "normalBorderColor", Color.TRANSPARENT),
                getColor(map, "pressedBorderColor", Color.TRANSPARENT),
                getColor(map, "normalTextColor", Color.TRANSPARENT),
                getColor(map, "pressedTextColor", Color.TRANSPARENT));
    }

    public static Map<String, String> createMapFromMap(Map<String, Object> map) {
        Map<String, String> newMap = new HashMap<>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                newMap.put(entry.getKey(), value != null ? String.valueOf(value) : null);
            }
        }
        return newMap;
    }


    public static String[] toArray(ReadableArray array) {
        if (array == null) {
            return new String[0];
        }
        int size = array.size();
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = array.getString(i);
        }
        return result;
    }

    /**
     * 从map中获取key对应的值，如果map为空或者key不存在或者key对应的值为null，则返回defaultValue
     *
     * @param map
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */

    private static <T> T getValue(ReadableMap map, String key, Class<T> clazz, T defaultValue) {
        if (map == null || !map.hasKey(key) || map.isNull(key)) {
            return defaultValue;
        }
        if (clazz == Boolean.class) {
            return clazz.cast(map.getBoolean(key));
        } else if (clazz == Integer.class) {
            return clazz.cast(map.getInt(key));
        } else if (clazz == Double.class) {
            return clazz.cast(map.getDouble(key));
        } else if (clazz == String.class) {
            return clazz.cast(map.getString(key));
        }
        return defaultValue;
    }

    public static Boolean getBooleanVal(ReadableMap map, String key, Boolean defaultValue) {
        return getValue(map, key, Boolean.class, defaultValue);
    }

    public static Integer getIntVal(ReadableMap map, String key, Integer defaultValue) {
        return getValue(map, key, Integer.class, defaultValue);
    }

    public static Double getDoubleVal(ReadableMap map, String key, Double defaultValue) {
        return getValue(map, key, Double.class, defaultValue);
    }

    public static String getStringVal(ReadableMap map, String key, String defaultValue) {
        return getValue(map, key, String.class, defaultValue);
    }

    public static Integer getColor(ReadableMap map, String key, Integer defaultValue) {
        String color = getStringVal(map, key);
        if (color != null) {
            return ColorUtil.parseColor(color);
        }
        return defaultValue;
    }

    public static String getStringVal(ReadableMap map, String key) {
        return getValue(map, key, String.class, null);
    }

    public static Boolean getBooleanVal(ReadableMap map, String key) {
        return getValue(map, key, Boolean.class, null);
    }

    public static Double getDoubleVal(ReadableMap map, String key) {
        return getValue(map, key, Double.class, null);
    }

    public static Integer getIntVal(ReadableMap map, String key) {
        return getIntVal(map, key, null);
    }

    public static Integer getColor(ReadableMap map, String key) {
        return getColor(map, key, null);
    }

    public static <T> T getValue(ReadableArray array, int index, Class<T> clazz, T defaultValue) {
        if (array == null || index < 0 || index >= array.size() || array.isNull(index)) {
            return defaultValue;
        }
        if (clazz == Boolean.class) {
            return clazz.cast(array.getBoolean(index));
        } else if (clazz == Integer.class) {
            return clazz.cast(array.getInt(index));
        } else if (clazz == Double.class) {
            return clazz.cast(array.getDouble(index));
        } else if (clazz == String.class) {
            return clazz.cast(array.getString(index));
        }
        return defaultValue;
    }

    public static AppletStatus intToAppletStatus(int statusNumber) {
        switch (statusNumber) {
            case 1:
                return AppletStatus.LISTED;
            case 2:
                return AppletStatus.UNLISTED;
            case 3:
                return AppletStatus.DELISTED;
            default:
                return AppletStatus.ALL;
        }
    }
}
