//
//  MOP_registerExtensionApi.m
//  mop
//
//  Created by 康旭耀 on 2020/4/20.
//

#import "MOP_registerExtensionApi.h"
#import <FinApplet/FinApplet.h>

@implementation MOP_registerExtensionApi

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    NSLog(@"MOP_registerExtensionApi");
    [[FATClient sharedClient] registerExtensionApi:self.name handler:^(FATAppletInfo *appletInfo, id param, FATExtensionApiCallback callback) {
        NSLog(@"invoke ExtensionApi:");
        NSLog(@"%@",self.name);
        NSLog(@"%@",param);
        NSString *apiName = [@"extensionApi:" stringByAppendingString:self.name];
        NSDictionary *body = @{
            @"apiName": apiName,
            @"params": param,
            @"callbackId": [self.mopSDK callbackId]
        };
        [self.mopSDK sendEventWithName:kMopEventReminder body:body callback:^(id  _Nullable result) { 
            // 如果不是字典类型，按照默认按照成功处理
            if (![result isKindOfClass:[NSDictionary class]]) {
                callback(FATExtensionCodeSuccess, @{});
                return;
            }
            
            NSString *errMsg = result[@"errMsg"];
            // errMsg 不是字符串类型，也默认按照成功处理
            if (![errMsg isKindOfClass:[NSString class]]) {
                errMsg = [NSString stringWithFormat:@"%@:ok", apiName];
            }
            
            NSString *errPrefix = [NSString stringWithFormat:@"%@:fail", apiName];
            BOOL isFail = [errMsg hasPrefix:errPrefix];
            if (isFail) {
                NSLog(@"extensionApi reslut:fail");
                callback(FATExtensionCodeFailure,@{@"errMsg": errMsg});
                return;
            }
            
            // 剩下的就是成功的情况
            NSLog(@"extensionApi callback:%@",result);
            id data = result[@"data"];
            if (!data) {
                callback(FATExtensionCodeSuccess, @{});
                return;
            }
            if ([NSJSONSerialization isValidJSONObject:data]) {
                callback(FATExtensionCodeSuccess,@{@"data": data});
                return;
            }
           
            // data 不能序列化的情况
            callback(FATExtensionCodeFailure,@{@"errMsg": [NSString stringWithFormat:@"%@:fail data can't serialize"]});
        }];
    }];
    success(@{});
}

@end
