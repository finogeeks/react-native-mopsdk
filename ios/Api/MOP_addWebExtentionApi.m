//
//  MOP_addWebExtentionApi.m
//  mop
//
//  Created by 王滔 on 2021/12/21.
//

#import "MOP_addWebExtentionApi.h"
#import <FinApplet/FinApplet.h>

@implementation MOP_addWebExtentionApi

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    NSLog(@"MOP_addWebExtentionApi");
    [[FATClient sharedClient] fat_registerWebApi:self.name handler:^(FATAppletInfo *appletInfo, id param, FATExtensionApiCallback callback) {
        NSLog(@"invoke webExtentionApi:");
        NSLog(@"%@",self.name);
        NSLog(@"%@",param);
        NSString *apiName = [@"webExtentionApi:" stringByAppendingString:self.name];
        NSDictionary *body = @{
            @"apiName": apiName,
            @"params": param,
            @"callbackId": [self.mopSDK callbackId]
        };
        [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
            // 判断回调是否为失败
            BOOL hasError = [[result allKeys] containsObject:@"errMsg"];
            if (hasError) {
                NSString *errMsg = result[@"errMsg"];
                NSString *errPrefix = [NSString stringWithFormat:@"%@:fail", self.name];
                BOOL isFail = [errMsg hasPrefix:errPrefix];
                if (isFail) {
                    NSLog(@"extensionApi reslut:fail");
                    callback(FATExtensionCodeFailure,nil);
                    return;
                }
            }
            // 其他的按成功处理
            NSLog(@"extensionApi callback:%@",result);
            callback(FATExtensionCodeSuccess,@{@"data": result[@"data"]});
        }];
    }];
    success(@{});
}
@end
