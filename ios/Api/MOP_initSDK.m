//
//  MOP_initSDK.m
//  react-native-mopsdk
//
//  Created by 王兆耀 on 2024/1/9.
//

#import "Mop_initSDK.h"
#import "MOPTools.h"

@implementation MOP_initSDK

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel
{
    if (!self.config) {
        failure(@"config不能为空");
        return;
    }
    FATConfig *config;
    NSArray *storeConfigList = self.config[@"finStoreConfigs"];
    if (storeConfigList && storeConfigList.count > 0) {
        NSMutableArray *storeArrayM = [NSMutableArray array];
        for (NSDictionary *dict in storeConfigList) {
            FATStoreConfig *storeConfig = [[FATStoreConfig alloc] init];
            storeConfig.sdkKey = dict[@"sdkKey"];
            storeConfig.sdkSecret = dict[@"sdkSecret"];
            storeConfig.apiServer = dict[@"apiServer"];
            storeConfig.apmServer = dict[@"apmServer"];
            storeConfig.fingerprint = dict[@"fingerprint"];
            if ([@"SM" isEqualToString:dict[@"cryptType"]]) {
                storeConfig.cryptType = FATApiCryptTypeSM;
            } else {
                storeConfig.cryptType = FATApiCryptTypeMD5;
            }
            storeConfig.encryptServerData = [dict[@"encryptServerData"] boolValue];
            storeConfig.enablePreloadFramework = [storeConfig.apiServer isEqualToString:@"https://api.finclip.com"];
            [storeArrayM addObject:storeConfig];
        }
        config = [FATConfig configWithStoreConfigs:storeArrayM];
    } else {
        failure(@"storeConfigs不能为空");
        return;
    }
    
    config.currentUserId = self.config[@"userId"];
    config.productIdentification = self.config[@"productIdentification"];
    config.disableAuthorize = [self.config[@"disableRequestPermissions"] boolValue];
    config.appletAutoAuthorize = [self.config[@"appletAutoAuthorize"] boolValue];
    config.disableGetSuperviseInfo = [self.config[@"disableGetSuperviseInfo"] boolValue];
    config.ignoreWebviewCertAuth = [self.config[@"ignoreWebviewCertAuth"] boolValue];
    config.appletIntervalUpdateLimit = [self.config[@"appletIntervalUpdateLimit"] integerValue];
    config.startCrashProtection = [self.config[@"startCrashProtection"] boolValue];
    config.enableApmDataCompression = [self.config[@"enableApmDataCompression"] boolValue];
    config.encryptServerData = [self.config[@"encryptServerData"] boolValue];
    config.enableAppletDebug = [self.config[@"appletDebugMode"] integerValue];
    config.enableWatermark = [self.config[@"enableWatermark"] boolValue];
    config.watermarkPriority = [self.config[@"watermarkPriority"] integerValue];
    config.baseLoadingViewClass = self.config[@"baseLoadingViewClass"];
    config.baseLoadFailedViewClass = self.config[@"baseLoadFailedViewClass"];
    config.header = self.config[@"header"];
    config.headerPriority = [self.config[@"headerPriority"] integerValue];
    config.enableH5AjaxHook = [self.config[@"enableH5AjaxHook"] boolValue];
    config.h5AjaxHookRequestKey = self.config[@"h5AjaxHookRequestKey"];
    config.pageCountLimit = [self.config[@"pageCountLimit"] integerValue];
    config.schemes = self.config[@"schemes"];
   config.webViewInspectable = [self.config[@"debug"] boolValue];
    NSInteger languageInteger = [self.config[@"language"] integerValue];
   if (self.config[@"backgroundFetchPeriod"]) {
       config.backgroundFetchPeriod = [self.config[@"backgroundFetchPeriod"] integerValue];
   }
    if (languageInteger == 1) {
        config.language = FATPreferredLanguageEnglish;
    } else {
        config.language = FATPreferredLanguageSimplifiedChinese;
    }
   config.customLanguagePath = self.config[@"customLanguagePath"];
        
    NSError* error = nil;
    FATUIConfig *uiconfig = [[FATUIConfig alloc]init];
    if (_uiConfig) {
        // 导航栏配置
        if (_uiConfig[@"navigationTitleTextAttributes"]) {
            uiconfig.navigationTitleTextAttributes = _uiConfig[@"navigationTitleTextAttributes"];
        }
        
        uiconfig.navigationBarHeight = [_uiConfig[@"navigationBarHeight"] floatValue];
        if (_uiConfig[@"navigationBarTitleLightColor"]) {
            uiconfig.navigationBarTitleLightColor = [UIColor fat_colorWithARGBHexString:_uiConfig[@"navigationBarTitleLightColor"]];
        }
        if (_uiConfig[@"navigationBarTitleDarkColor"]) {
            uiconfig.navigationBarTitleDarkColor = [UIColor fat_colorWithARGBHexString:_uiConfig[@"navigationBarTitleDarkColor"]];
        }
        if (_uiConfig[@"navigationBarBackBtnLightColor"]) {
            uiconfig.navigationBarBackBtnLightColor = [UIColor fat_colorWithARGBHexString:_uiConfig[@"navigationBarBackBtnLightColor"]];
        }
        if (_uiConfig[@"navigationBarBackBtnDarkColor"]) {
            uiconfig.navigationBarBackBtnDarkColor = [UIColor fat_colorWithARGBHexString:_uiConfig[@"navigationBarBackBtnDarkColor"]];
        }

        // 更多视图配置
        uiconfig.moreMenuStyle = [_uiConfig[@"moreMenuStyle"] integerValue];
        uiconfig.hideBackToHomePriority = [_uiConfig[@"isHideBackHomePriority"] integerValue];
        uiconfig.hideBackToHome = [_uiConfig[@"isHideBackHome"] boolValue];
        uiconfig.hideShareAppletMenu = [_uiConfig[@"isHideShareAppletMenu"] boolValue];
        uiconfig.hideForwardMenu = [_uiConfig[@"isHideForwardMenu"] boolValue];
        uiconfig.hideSettingMenu = [_uiConfig[@"isHideSettingMenu"] boolValue];
        uiconfig.hideFeedbackMenu = [_uiConfig[@"isHideFeedbackAndComplaints"] boolValue];
        uiconfig.hideRefreshMenu = [_uiConfig[@"isHideRefreshMenu"] boolValue];
        uiconfig.hideFavoriteMenu = [_uiConfig[@"isHideFavoriteMenu"] boolValue];
        uiconfig.hideAddToDesktopMenu = [_uiConfig[@"isHideAddToDesktopMenu"] boolValue];
       uiconfig.hideClearCacheMenu = [_uiConfig[@"isHideClearCacheMenu"] boolValue];

        // 胶囊配置
        if (_uiConfig[@"capsuleConfig"]) {
            NSDictionary *capsuleConfigDic = _uiConfig[@"capsuleConfig"];
            FATCapsuleConfig *capsuleConfig = [[FATCapsuleConfig alloc]init];
            capsuleConfig.capsuleWidth = [capsuleConfigDic[@"capsuleWidth"] floatValue];
            capsuleConfig.capsuleHeight = [capsuleConfigDic[@"capsuleHeight"] floatValue];
            capsuleConfig.capsuleRightMargin = [capsuleConfigDic[@"capsuleRightMargin"] floatValue];
            capsuleConfig.capsuleCornerRadius = [capsuleConfigDic[@"capsuleCornerRadius"] floatValue];
            capsuleConfig.capsuleBorderWidth = [capsuleConfigDic[@"capsuleBorderWidth"] floatValue];
            capsuleConfig.moreBtnWidth = [capsuleConfigDic[@"moreBtnWidth"] floatValue];
            capsuleConfig.moreBtnLeftMargin = [capsuleConfigDic[@"moreBtnLeftMargin"] floatValue];
            capsuleConfig.closeBtnWidth = [capsuleConfigDic[@"closeBtnWidth"] floatValue];
            capsuleConfig.closeBtnLeftMargin = [capsuleConfigDic[@"closeBtnLeftMargin"] floatValue];
            
            
            capsuleConfig.capsuleBorderLightColor = [UIColor fat_colorWithARGBHexString:capsuleConfigDic[@"capsuleBorderLightColor"]];
            capsuleConfig.capsuleBorderDarkColor = [UIColor fat_colorWithARGBHexString:capsuleConfigDic[@"capsuleBorderDarkColor"]];
            capsuleConfig.capsuleBgLightColor = [UIColor fat_colorWithARGBHexString:capsuleConfigDic[@"capsuleBgLightColor"]];
            capsuleConfig.capsuleBgDarkColor = [UIColor fat_colorWithARGBHexString:capsuleConfigDic[@"capsuleBgDarkColor"]];
            capsuleConfig.capsuleDividerLightColor = [UIColor fat_colorWithARGBHexString:capsuleConfigDic[@"capsuleDividerLightColor"]];
            capsuleConfig.capsuleDividerDarkColor = [UIColor fat_colorWithARGBHexString:capsuleConfigDic[@"capsuleDividerDarkColor"]];
            uiconfig.capsuleConfig = capsuleConfig;
        }
        uiconfig.hideTransitionCloseButton = [_uiConfig[@"hideTransitionCloseButton"] boolValue];
        
        if (_uiConfig[@"navHomeConfig"]) {
            NSDictionary *navHomeConfigDic = _uiConfig[@"navHomeConfig"];
            FATNavHomeConfig *navHomeConfig = [[FATNavHomeConfig alloc]init];
            navHomeConfig.width = [navHomeConfigDic[@"width"] floatValue];
            navHomeConfig.height = [navHomeConfigDic[@"height"] floatValue];
            navHomeConfig.leftMargin = [navHomeConfigDic[@"leftMargin"] floatValue];
            navHomeConfig.cornerRadius = [navHomeConfigDic[@"cornerRadius"] floatValue];
            navHomeConfig.borderWidth = [navHomeConfigDic[@"width"] floatValue];
            if (navHomeConfigDic[@"borderLightColor"]) {
                navHomeConfig.borderLightColor = [UIColor fat_colorWithARGBHexString:navHomeConfigDic[@"borderLightColor"]];
            }
            if (navHomeConfigDic[@"borderDarkColor"]) {
                navHomeConfig.borderDarkColor = [UIColor fat_colorWithARGBHexString:navHomeConfigDic[@"borderDarkColor"]];
            }
            if (navHomeConfigDic[@"bgLightColor"]) {
                navHomeConfig.bgLightColor = [UIColor fat_colorWithARGBHexString:navHomeConfigDic[@"bgLightColor"]];
            }
            if (navHomeConfigDic[@"bgDarkColor"]) {
                navHomeConfig.bgDarkColor = [UIColor fat_colorWithARGBHexString:navHomeConfigDic[@"bgDarkColor"]];
            }
            uiconfig.navHomeConfig = navHomeConfig;
        }
        
        if (_uiConfig[@"authViewConfig"]) {
            NSDictionary *authViewConfigDic = _uiConfig[@"authViewConfig"];
            FATAuthViewConfig *authViewConfig = [[FATAuthViewConfig alloc]init];
            authViewConfig.appletNameFont = [UIFont systemFontOfSize:[authViewConfigDic[@"appletNameTextSize"] floatValue]];
            if (authViewConfigDic[@"appletNameLightColor"]) {
                authViewConfig.appletNameLightColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"appletNameLightColor"]];
            }
            
            if (authViewConfigDic[@"appletNameDarkColor"]) {
                authViewConfig.appletNameDarkColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"appletNameDarkColor"]];
            }
            
            authViewConfig.authorizeTitleFont = [UIFont systemFontOfSize:[authViewConfigDic[@"authorizeTitleTextSize"] floatValue] weight:UIFontWeightMedium];
            
            if (authViewConfigDic[@"authorizeTitleLightColor"]) {
                authViewConfig.authorizeTitleLightColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"authorizeTitleLightColor"]];
            }
            
            if (authViewConfigDic[@"authorizeTitleDarkColor"]) {
                authViewConfig.authorizeTitleDarkColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"authorizeTitleDarkColor"]];
            }
            
            authViewConfig.authorizeDescriptionFont = [UIFont systemFontOfSize:[authViewConfigDic[@"authorizeDescriptionTextSize"] floatValue]];

            
            if (authViewConfigDic[@"authorizeDescriptionLightColor"]) {
                authViewConfig.authorizeDescriptionLightColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"authorizeDescriptionLightColor"]];
            }
            
            if (authViewConfigDic[@"authorizeDescriptionDarkColor"]) {
                authViewConfig.authorizeDescriptionDarkColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"authorizeDescriptionDarkColor"]];
            }
            
            authViewConfig.agreementTitleFont = [UIFont systemFontOfSize:[authViewConfigDic[@"agreementTitleTextSize"] floatValue]];
            
            if (authViewConfigDic[@"agreementTitleLightColor"]) {
//                authViewConfig.agreementTitleLightColor = [MOPTools colorWithRGBHex:[authViewConfigDic[@"agreementTitleLightColor"] intValue]];
                authViewConfig.agreementTitleLightColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"agreementTitleLightColor"]];
            }
            
            if (authViewConfigDic[@"agreementTitleDarkColor"]) {
                authViewConfig.agreementTitleDarkColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"agreementTitleDarkColor"]];
            }
            
            authViewConfig.agreementDescriptionFont = [UIFont systemFontOfSize:[authViewConfigDic[@"agreementDescriptionTextSize"] floatValue]];
            
            if (authViewConfigDic[@"agreementDescriptionLightColor"]) {
                authViewConfig.agreementDescriptionLightColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"agreementDescriptionLightColor"]];
            }
            
            if (authViewConfigDic[@"agreementDescriptionDarkColor"]) {
                authViewConfig.agreementDescriptionDarkColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"agreementDescriptionDarkColor"]];
            }
            
            if (authViewConfigDic[@"linkLightColor"]) {
                authViewConfig.linkLightColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"linkLightColor"]];
            }
            
            if (authViewConfigDic[@"linkDarkColor"]) {
                authViewConfig.linkDarkColor = [UIColor fat_colorWithARGBHexString:authViewConfigDic[@"linkDarkColor"]];
            }
            
            if (authViewConfigDic[@"allowButtonLightConfig"]) {
                FATAuthButtonConfig *allowButtonLightConfig = [self createAuthButtonConfigWithDic:authViewConfigDic[@"allowButtonLightConfig"]];
                authViewConfig.allowButtonLightConfig = allowButtonLightConfig;
                
            }
            
            if (authViewConfigDic[@"allowButtonDarkConfig"]) {
                FATAuthButtonConfig *allowButtonDarkConfig = [self createAuthButtonConfigWithDic:authViewConfigDic[@"allowButtonDarkConfig"]];
                authViewConfig.allowButtonDarkConfig = allowButtonDarkConfig;
                
            }
            if (authViewConfigDic[@"rejectButtonLightConfig"]) {
                FATAuthButtonConfig *rejectButtonLightConfig = [self createAuthButtonConfigWithDic:authViewConfigDic[@"rejectButtonLightConfig"]];
                authViewConfig.rejectButtonLightConfig = rejectButtonLightConfig;
                
            }
            if (authViewConfigDic[@"rejectButtonDarkConfig"]) {
                FATAuthButtonConfig *rejectButtonDarkConfig = [self createAuthButtonConfigWithDic:authViewConfigDic[@"rejectButtonDarkConfig"]];
                authViewConfig.rejectButtonDarkConfig = rejectButtonDarkConfig;
                
            }
            
            uiconfig.authViewConfig = authViewConfig;
        }
        uiconfig.transtionStyle = [_uiConfig[@"transtionStyle"] integerValue];
        uiconfig.disableSlideCloseAppletGesture = [_uiConfig[@"disableSlideCloseAppletGesture"] boolValue];
        if (_uiConfig[@"webViewProgressBarColor"]) {
            uiconfig.progressBarColor = [UIColor fat_colorWithARGBHexString:_uiConfig[@"webViewProgressBarColor"]];
        }
        uiconfig.hideWebViewProgressBar = [_uiConfig[@"hideWebViewProgressBar"] boolValue];
        
        uiconfig.appletText = _uiConfig[@"appletText"];
        uiconfig.appendingCustomUserAgent = _uiConfig[@"customWebViewUserAgent"];
        uiconfig.autoAdaptDarkMode = [_uiConfig[@"autoAdaptDarkMode"] boolValue];
        uiconfig.useNativeLiveComponent = [_uiConfig[@"useNativeLiveComponent"] boolValue];
    }
    
    [[FATClient sharedClient] initWithConfig:config uiConfig:uiconfig error:&error];
    if (error) {
        failure(@"初始化失败");
        return;
    }
    
    int logMaxAliveSec = [self.config[@"logMaxAliveSec"] intValue];
    if (logMaxAliveSec) {
        [[FATClient sharedClient].logManager setLogFileAliveDuration:logMaxAliveSec];
    }

    BOOL debug = [self.config[@"debug"] boolValue];
    NSInteger logLevelIntValue = [self.config[@"logLevel"] integerValue];
    if (debug && logLevelIntValue < 5) {
        FATLogLevel logLevel = logLevelIntValue;
        NSString *logDir = self.config[@"logDir"];
        [[FATClient sharedClient].logManager initLogWithLogDir:logDir logLevel:logLevel consoleLog:YES];
    } else {
        [FATClient sharedClient].enableLog = NO;
    }
    
    success(@{});
    
}

- (FATAuthButtonConfig *)createAuthButtonConfigWithDic:(NSDictionary *)dic {
    if (!dic) {
        return nil;
    }
    FATAuthButtonConfig * authButtonConfig = [[FATAuthButtonConfig alloc]init];
    if (dic[@"cornerRadius"]) {
        authButtonConfig.cornerRadius = [dic[@"cornerRadius"] floatValue];
    }
    if (dic[@"normalBackgroundColor"]) {
        authButtonConfig.normalBackgroundColor = [UIColor fat_colorWithARGBHexString:dic[@"normalBackgroundColor"]];
    }
    if (dic[@"pressedBackgroundColor"]) {
        authButtonConfig.pressedBackgroundColor = [UIColor fat_colorWithARGBHexString:dic[@"pressedBackgroundColor"]];
    }
    if (dic[@"normalTextColor"]) {
        authButtonConfig.normalTextColor = [UIColor fat_colorWithARGBHexString:dic[@"normalTextColor"]];
    }
    if (dic[@"pressedTextColor"]) {
        authButtonConfig.pressedTextColor = [UIColor fat_colorWithARGBHexString:dic[@"pressedTextColor"]];
    }
    if (dic[@"normalBorderColor"]) {
        authButtonConfig.normalBorderColor = [UIColor fat_colorWithARGBHexString:dic[@"normalBorderColor"]];
    }
    if (dic[@"pressedBorderColor"]) {
        authButtonConfig.pressedBorderColor = [UIColor fat_colorWithARGBHexString:dic[@"pressedBorderColor"]];
    }
    
    return authButtonConfig;
}
@end
