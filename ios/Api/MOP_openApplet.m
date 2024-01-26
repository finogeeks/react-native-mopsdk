//
//  MOP_openApplet.m
//  mop
//
//  Created by 杨涛 on 2020/2/27.
//

#import "MOP_openApplet.h"
#import "MOPTools.h"
#import <FinApplet/FinApplet.h>

@implementation MOP_openApplet

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    UIViewController *currentVC = [MOPTools topViewController];
    // 打开小程序
     if (self.appId == NULL) {
        failure(@"appId不能为空");
        return;
    }
    FATAppletRequest *request = [[FATAppletRequest alloc] init];
    request.appletId = self.appId;
    if (self.apiServer) {
        request.apiServer = self.apiServer;
    }
    if (self.sequence) {
        request.sequence = self.sequence;
    }
    if (self.params) {
        request.startParams = self.params;
    }
    [[FATClient sharedClient] startAppletWithRequest:request InParentViewController:currentVC completion:^(BOOL result, FATError *error) {
        NSLog(@"result:%d---error:%@", result, error);
        
        if (result){
            success(@{});
        } else {
            failure(error.description);
        }
    } closeCompletion:^{
            
    }];
}
@end
