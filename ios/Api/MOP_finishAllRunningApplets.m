//
//  MOP_finishAllRunningApplets.m
//  mop
//
//  Created by 王滔 on 2021/12/21.
//

#import "MOP_finishAllRunningApplets.h"

@implementation MOP_finishAllRunningApplets

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    
    NSLog(@"MOP_finishAllRunningApplets");
    [[FATClient sharedClient] closeAllAppletsWithCompletion:^{
        [[FATClient sharedClient] clearMemoryCache];
        success(@{});
    }];
}

@end
