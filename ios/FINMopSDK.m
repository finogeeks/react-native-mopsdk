#import "FINMopSDK.h"
#import "MopPlugin.h"
#import "MOPTools.h"

NSString * const kMopEventReminder = @"EventReminder";

@interface FINMopSDK ()

@property (nonatomic, strong) NSMutableDictionary *eventDict;

@end

@implementation FINMopSDK

RCT_EXPORT_MODULE()

#pragma mark - override methods
- (NSArray<NSString *> *)supportedEvents {
    return @[kMopEventReminder];
}

#pragma mark - pushlish methods
- (NSString *)callbackId {
    double currentTime = [[NSDate date] timeIntervalSince1970] * 1000;
    NSString *strTime = [NSString stringWithFormat:@"%.0f", currentTime];
    return [MOPTools md5:strTime];
}

- (void)sendEventWithName:(NSString *)name body:(id)body callback:(MopSDKChannelBlock)callback {
    [self sendEventWithName:name body:body];
    // 存储callback，当eventReminderCallback触发时，取出并执行对应的callback
    NSString *callbackId = body[@"callbackId"];
    self.eventDict[callbackId] = callback;
}

#pragma mark lazy load
- (NSMutableDictionary *)eventDict {
    if (!_eventDict) {
        _eventDict = [NSMutableDictionary dictionary];
    }
    return _eventDict;
}


#pragma mark - RCT export methods
// command模式设计，只需要对外暴露这个方法，让外部调用就行（讨论后没通过这个方案，先预留）
RCT_EXPORT_METHOD(command:(NSString *)command param:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"command: %@, param: %@", command, param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"command" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(initialize:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"initialize param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"initialize" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(initSDK:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"initSDK param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"initSDK" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(openApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"openApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"openApplet" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(scanOpenApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"scanOpenApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"scanOpenApplet" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(qrcodeOpenApplet:(NSDictionary *)param) {
    NSLog(@"qrcodeOpenApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"qrcodeOpenApplet" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(closeApplet:(NSDictionary *)param) {
    NSLog(@"closeApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"closeApplet" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(closeAllApplets) {
    NSDictionary *param = @{};
    NSLog(@"closeAllApplets param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"closeAllApplets" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(removeApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"removeApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"removeApplet" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(removeUsedApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"removeUsedApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"removeUsedApplet" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(finishRunningApplet:(NSDictionary *)param) {
    NSLog(@"finishRunningApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"finishRunningApplet" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(clearApplets) {
    NSDictionary *param = @{};
    NSLog(@"clearApplets param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"clearApplets" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(currentApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"currentApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"currentApplet" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(sdkVersion:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"sdkVersion param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"sdkVersion" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(sendCustomEvent:(NSDictionary *)param) {
    NSLog(@"sendCustomEvent param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"sendCustomEvent" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(parseAppletInfoFromWXQrCode:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"parseAppletInfoFromWXQrCode param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"parseAppletInfoFromWXQrCode" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(showBotomSheetModel:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"showBotomSheetModel param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"showBotomSheetModel" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(webViewBounces:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"webViewBounces param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"webViewBounces" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

// 实现是空的？？？
RCT_EXPORT_METHOD(setFinStoreConfigs:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"setFinStoreConfigs param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"setFinStoreConfigs" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(callJS:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"callJS param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"callJS" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(addWebExtentionApi:(NSDictionary *)param) {
    NSLog(@"addWebExtentionApi param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"addWebExtentionApi" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(registerExtensionApi:(NSDictionary *)param) {
    NSLog(@"registerExtensionApi param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"registerExtensionApi" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(registerAppletHandler) {
    NSDictionary *param = @{};
    NSLog(@"registerAppletHandler param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"registerAppletHandler" arguments:param mopSDK:self result:^(NSString *result) {
//            callback(@[result]);
        }];
    });
}

// eventReminderCallback
RCT_EXPORT_METHOD(eventReminderCallback:(NSString *)apiName param:(id)param callbackId:(NSString *)callbackId) {
    NSLog(@"eventReminderCallback apiName: %@, param: %@", apiName, param);
    MopSDKChannelBlock block = self.eventDict[callbackId];
    if (block) {
        block(param);
        self.eventDict[callbackId] = nil;
    }
}

RCT_EXPORT_METHOD(smsign:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"smsign param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"smsign" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(openApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"startApplet param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"startApplet" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(openApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"removeAllUsedApplets param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"removeAllUsedApplets" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(openApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"changeUserId param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"changeUserId" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(openApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"AppletDelegate param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"AppletDelegate" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

RCT_EXPORT_METHOD(openApplet:(NSDictionary *)param callback:(RCTResponseSenderBlock)callback) {
    NSLog(@"ButtonOpenTypeDelegate param: %@", param);
    dispatch_async(dispatch_get_main_queue(), ^{
        [MopPlugin handleMethod:@"ButtonOpenTypeDelegate" arguments:param mopSDK:self result:^(NSString *result) {
            callback(@[result]);
        }];
    });
}

@end
