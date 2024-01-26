//
//  MOP_initialize.m
//  mop
//
//  Created by 杨涛 on 2020/2/27.
//

#import "MOP_initialize.h"
#import <FinApplet/FinApplet.h>
//#import <FinAppletExt/FinAppletExt.h>
#import "MOPTools.h"

@implementation MOP_initialize

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    if (!self.appkey || !self.secret) {
        failure(@"sdkkey 或 secret不能为空");
        return;
    }
    if (!self.apiServer || [self.apiServer isEqualToString:@""]) {
        self.apiServer = @"https://api.finclip.com";
    }
    if (!self.apiPrefix|| [self.apiPrefix isEqualToString:@""]) {
        self.apiPrefix = @"/api/v1/mop";
    }
    FATConfig *config;
    if (_finStoreConfigs && _finStoreConfigs.count > 0) {
        NSMutableArray *storeArrayM = [NSMutableArray array];
        for (NSDictionary *dict in _finStoreConfigs) {
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
            [storeArrayM addObject:storeConfig];
        }
        config = [FATConfig configWithStoreConfigs:storeArrayM];
    } else {
        FATStoreConfig *storeConfig = [[FATStoreConfig alloc] init];
        storeConfig.sdkKey = [self.appkey copy];
        storeConfig.sdkSecret = [self.secret copy];
        storeConfig.apiServer = [self.apiServer copy];
        if([self.cryptType isEqualToString: @"SM"])
        {
            storeConfig.cryptType = FATApiCryptTypeSM;
        }
        else
        {
            storeConfig.cryptType = FATApiCryptTypeMD5;
        }
        
        // encryptServerData
        NSLog(@"encryptServerData:%d",self.encryptServerData);
        storeConfig.encryptServerData = self.encryptServerData;
        
        config = [FATConfig configWithStoreConfigs:@[storeConfig]];
    }
    
    NSLog(@"disablePermission:%d",self.disablePermission);
    config.disableAuthorize = self.disablePermission;
    config.currentUserId = [self.userId copy];
    config.appletIntervalUpdateLimit = self.appletIntervalUpdateLimit;
    config.baseLoadingViewClass = @"Mop_LoadingView";
    
    if (self.language == 1) {
        config.language = FATPreferredLanguageEnglish;
    } else {
        config.language = FATPreferredLanguageSimplifiedChinese;
    }
    
    
    config.customLanguagePath = self.customLanguagePath;


    NSError* error = nil;
    FATUIConfig *uiconfig = [[FATUIConfig alloc]init];
    uiconfig.autoAdaptDarkMode = YES;
    if (_uiConfig) {
        if (_uiConfig[@"navigationTitleTextAttributes"]) {
            uiconfig.navigationTitleTextAttributes = _uiConfig[@"navigationTitleTextAttributes"];
        }
        if (_uiConfig[@"progressBarColor"]) {
            uiconfig.progressBarColor = [UIColor fat_colorWithARGBHexString:_uiConfig[@"progressBarColor"]];
        }
        uiconfig.hideBackToHome = [_uiConfig[@"isHideBackHome"] boolValue];
        uiconfig.hideFeedbackMenu = [_uiConfig[@"isHideFeedbackAndComplaints"] boolValue];
        uiconfig.hideForwardMenu = [_uiConfig[@"isHideForwardMenu"] boolValue];
        uiconfig.autoAdaptDarkMode = [_uiConfig[@"autoAdaptDarkMode"] boolValue];
        
        uiconfig.appletText = _uiConfig[@"appletText"];
        uiconfig.hideTransitionCloseButton = [_uiConfig[@"hideTransitionCloseButton"] boolValue];
        uiconfig.disableSlideCloseAppletGesture = _uiConfig[@"disableSlideCloseAppletGesture"];
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
    }
    uiconfig.appendingCustomUserAgent = self.customWebViewUserAgent;
    uiconfig.appletText = self.appletText;

    [[FATClient sharedClient] initWithConfig:config uiConfig:uiconfig error:&error];
    if (error) {
        failure(@"初始化失败");
        return;
    }
//    [[FATExtClient sharedClient] fat_prepareExtensionApis];
    
    [[FATClient sharedClient] setEnableLog:YES];
    
    success(@{});
}

@end
