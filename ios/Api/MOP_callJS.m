//
//  MOP_callJS.m
//  mop
//
//  Created by 王滔 on 2021/12/21.
//

#import "MOP_callJS.h"
#import "NSDictionary+MOP.h"

@implementation MOP_callJS

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    if (!self.eventData) {
        failure(@{@"errMsg": @"callJS:fail eventData is not null"});
        return;
    }
    if (!self.eventName) {
        failure(@{@"errMsg": @"callJS:fail eventName is not null"});
        return;
    }
    if (!self.appId) {
        failure(@{@"errMsg": @"callJS:fail appId is not null"});
        return;
    }
    
    [[FATClient sharedClient] fat_callWebApi:self.eventName paramString:[self.eventData toJSONString] pageId:nil handler:^(id result, NSError *error) {
        if (error) {
            failure(@{@"errMsg": @"callJS:fail"});
        } else {
            result ? success(result) : success(@{@"errMsg": @"callJS:ok"});
        }
    }];
}

@end
