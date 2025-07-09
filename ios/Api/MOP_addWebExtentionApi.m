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
    NSString *name = self.name;
    NSLog(@"MOP_addWebExtentionApi:%@", name);
    [[FATClient sharedClient] fat_registerWebApi:self.name handler:^(FATAppletInfo *appletInfo, id param, FATExtensionApiCallback callback) {
        NSLog(@"invoke webExtentionApi:%@, param:%@", name, param);
        NSString *apiName = [@"webExtentionApi:" stringByAppendingString:name];
        NSDictionary *body = @{
            @"apiName": apiName,
            @"params": param,
            @"callbackId": [self.mopSDK callbackId]
        };
        [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) {
             NSLog(@"extensionApi callback:%@",result);
            // 如果不是字典类型，默认按照成功处理
            if (![result isKindOfClass:[NSDictionary class]]) {
                callback(FATExtensionCodeSuccess, @{});
                return;
            }
            
            NSString *errMsg = result[@"errMsg"];
            // errMsg 不是字符串类型，也默认按照成功处理
            if (![errMsg isKindOfClass:[NSString class]]) {
                errMsg = [NSString stringWithFormat:@"%@:ok", name];
                return;
            }

            // 不能序列化的情况
            if (![NSJSONSerialization isValidJSONObject:result]) {
                callback(FATExtensionCodeFailure,@{@"errMsg": [NSString stringWithFormat:@"%@:fail data can't serialize", name]});
                return;
            }
            
            NSString *errPrefix = [NSString stringWithFormat:@"%@:fail", name];
            BOOL isFail = [errMsg hasPrefix:errPrefix];
            if (isFail) {
                NSLog(@"extensionApi reslut:fail");
                callback(FATExtensionCodeFailure, result);
                return;
            }
            
            // 剩下的就是成功的情况
            callback(FATExtensionCodeSuccess, result);
        }];
    }];
    success(@{});
}
@end
