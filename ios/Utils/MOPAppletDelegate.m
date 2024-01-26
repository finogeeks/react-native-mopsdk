//
//  MOPAppletDelegate.m
//  mop
//
//  Created by 康旭耀 on 2020/4/20.
//

#import "MOPAppletDelegate.h"
#import "MopCustomMenuModel.h"

@interface NSString (FATEncode)

- (NSString *)fat_encodeString;

@end

@implementation MOPAppletDelegate

+ (instancetype)instance {
    static MOPAppletDelegate *_instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        _instance = [[self alloc] init];
    });
    return _instance;
}

- (void)forwardAppletWithInfo:(NSDictionary *)contentInfo completion:(void (^)(FATExtensionCode, NSDictionary *))completion {
    NSLog(@"forwardAppletWithInfo1:%@",contentInfo);
    NSString *apiName = @"extensionApi:forwardApplet";
    NSDictionary *param = @{@"appletInfo":contentInfo};
    NSString *callbackId = [self.mopSDK callbackId];
    NSDictionary *body = @{
        @"apiName": apiName,
        @"params": param,
        @"callbackId": callbackId
    };
    [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
        // 判断回调是否为失败
        BOOL hasError = [[result allKeys] containsObject:@"errMsg"];
        if (hasError) {
            NSString *errMsg = result[@"errMsg"];
            NSString *errPrefix = [NSString stringWithFormat:@"%@:fail", apiName];
            BOOL isFail = [errMsg hasPrefix:errPrefix];
            if (isFail) {
                NSLog(@"extensionApi reslut:fail");
                completion(FATExtensionCodeFailure,nil);
                return;
            }
        }
        // 其他的按成功处理
        NSLog(@"extensionApi callback:%@",result);
        completion(FATExtensionCodeSuccess,@{@"data": result[@"data"]});
    }];
}

- (NSDictionary *)getUserInfoWithAppletInfo:(FATAppletInfo *)appletInfo {
    NSLog(@"getUserInfoWithAppletInfo");
    __block NSDictionary *userInfo;
    NSString *apiName = @"extensionApi:getUserInfo";
    NSString *callbackId = [self.mopSDK callbackId];
    NSDictionary *body = @{
        @"apiName": apiName,
        @"callbackId": callbackId
    };
    [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
        CFRunLoopStop(CFRunLoopGetMain());
        userInfo = result;
    }];
    CFRunLoopRun();
    return userInfo;
}

- (NSArray<id<FATAppletMenuProtocol>> *)customMenusInApplet:(FATAppletInfo *)appletInfo atPath:(NSString *)path {
    NSLog(@"customMenusInApplet");
    __block NSArray *list;
    NSString *apiName = @"extensionApi:getCustomMenus";
    NSDictionary *param = @{@"appId": appletInfo.appId};
    NSString *callbackId = [self.mopSDK callbackId];
    NSDictionary *body = @{
        @"apiName": apiName,
        @"params": param,
        @"callbackId": callbackId
    };
    [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
        CFRunLoopStop(CFRunLoopGetMain());
        if ([result isKindOfClass:[NSArray class]]) {
            list = result;
        }
    }];
    CFRunLoopRun();
    
    NSMutableArray *models = [NSMutableArray array];
    for (NSDictionary<NSString *, NSString *> *data in list) {
        MopCustomMenuModel *model = [[MopCustomMenuModel alloc] init];
        model.menuId = data[@"menuId"];
        model.menuTitle = data[@"title"];
        model.menuIconImage = [UIImage imageNamed:data[@"image"]];
        NSString *typeString = data[@"type"];
        if (typeString) {
            FATAppletMenuStyle style = [typeString isEqualToString:@"onMiniProgram"] ? FATAppletMenuStyleOnMiniProgram : FATAppletMenuStyleCommon;
            model.menuType = style;
        }
        [models addObject:model];
    }
    
    return models;
}

- (void)clickCustomItemMenuWithInfo:(NSDictionary *)contentInfo inApplet:(FATAppletInfo *)appletInfo completion:(void (^)(FATExtensionCode code, NSDictionary *result))completion {
    NSString *apiName = @"extensionApi:onCustomMenuClick";
    NSDictionary *arguments = @{
        @"appId": contentInfo[@"appId"],
        @"path": contentInfo[@"path"],
        @"menuId": contentInfo[@"menuId"],
        @"appInfo": appletInfo.description
    };
    NSString *callbackId = [self.mopSDK callbackId];
    NSDictionary *body = @{
        @"apiName": apiName,
        @"params": arguments,
        @"callbackId": callbackId
    };
    [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
        
    }];
    
    if ([@"Desktop" isEqualToString:contentInfo[@"menuId"]]) {
        [self addToDesktopItemClick:appletInfo path:contentInfo[@"path"]];
    }
}

- (void)appletInfo:(FATAppletInfo *)appletInfo didOpenCompletion:(NSError *)error {
    NSString *appletId = appletInfo.appId;
    if (!appletId) {
        return;
    }
    NSString *apiName = @"extensionApi:appletDidOpen";
    NSDictionary *params = @{@"appId": appletId};
    NSString *callbackId = [self.mopSDK callbackId];
    NSDictionary *body = @{
        @"apiName": apiName,
        @"params": params,
        @"callbackId": callbackId
    };
    [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
        
    }];
}

static NSString *scheme = @"fatae55433be2f62915";//App对应的scheme

- (void)addToDesktopItemClick:(FATAppletInfo *)appInfo path:(NSString *)path {
    NSMutableString *herf = [NSString stringWithFormat:@"%@://applet/appid/%@?", scheme, appInfo.appId].mutableCopy;
    NSString *query = [NSString stringWithFormat:@"apiServer=%@&path=%@",appInfo.apiServer, path];

    if ([appInfo.startParams[@"query"] length]) {
        query = [NSString stringWithFormat:@"%@&query=%@",query, appInfo.startParams[@"query"]];
    }
    [herf appendString:query.fat_encodeString];

    NSMutableString *url = [NSMutableString stringWithFormat:@"%@/mop/scattered-page/#/desktopicon", appInfo.apiServer];
    [url appendFormat:@"?iconpath=%@", appInfo.appAvatar];
    [url appendFormat:@"&apptitle=%@", appInfo.appTitle.fat_encodeString];
    [url appendFormat:@"&linkhref=%@", herf];
    
    NSLog(@"跳转到中间页面:%@", url);
    if (@available(iOS 10.0, *)) {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url] options:@{} completionHandler:^(BOOL success) {
            
        }];
    } else {
        // Fallback on earlier versions
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
    }
}

@end

@implementation NSString (FATEncode)

- (NSString *)fat_encodeString {
    return (__bridge NSString *)CFURLCreateStringByAddingPercentEscapes( NULL,  (__bridge CFStringRef)self,  NULL,  (CFStringRef)@"!*'();:@&=+$,/?%#[]", kCFStringEncodingUTF8);
}

@end
