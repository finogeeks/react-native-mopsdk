#import "FINMopsdk.h"
#import "NSDictionary+MOP.h"
#import "MOPTools.h"

#import <FinApplet/FinApplet.h>


@implementation FINMopsdk

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(initialize:(nonnull NSString *)appkey secret:(nonnull NSString *)secret apiServer:(nonnull NSString*)apiServer apiPrefix:(nonnull NSString*)apiPrefix callback:(RCTResponseSenderBlock)callback)
{
    // TODO: Implement some actually useful functionality
    NSLog(@"%@",appkey);
    NSLog(@"%@",secret);
    NSLog(@"%@",apiServer);
    NSLog(@"%@",apiPrefix);
    FATConfig *config = [FATConfig configWithAppSecret:secret appKey:appkey];
    config.apiServer = [apiServer copy];
    config.apiPrefix = [apiPrefix copy];
    NSError* error = nil;
    [[FATClient sharedClient] initWithConfig:config error:&error];
    NSDictionary* ret;
    if (error) {
        ret = @{
            @"success": @(NO),
            @"errMsg": [NSString stringWithFormat:@"初始化小程序引擎失败%@",error.localizedDescription]
        };
        callback(@[[ret toJSONString]]);
        return;
    }
    
    ret = @{
        @"success": @(YES),
        @"errMsg": @"initialize:ok"
    };
    callback(@[[ret toJSONString]]);
}
RCT_EXPORT_METHOD(openApplet:(nonnull NSString *)appId path:(NSString *)path query:(NSString*)query sequence:(NSString*)sequence callback:(RCTResponseSenderBlock)callback)
{
    
    if (!sequence) {
        sequence = @"0";
    }
    NSDictionary* params = nil;
    if(!path) {
        params = @{
            @"path": path,
            @"query": query
        };
    }
    // 打开小程序
    dispatch_async(dispatch_get_main_queue(), ^{
        UIViewController *currentVC = [MOPTools topViewController];
        if (sequence == nil) {
            [[FATClient sharedClient] startRemoteApplet:appId startParams:params InParentViewController:currentVC completion:^(BOOL result, NSError *error) {
                NSLog(@"result:%d---error:%@", result, error);
                NSDictionary* ret;
                if (result){
                    ret = @{
                        @"success": @(YES),
                        @"errMsg": @"openApplet:ok"
                    };
                    callback(@[[ret toJSONString]]);
                }else {
                    ret = @{
                        @"success": @(NO),
                        @"errMsg": error.description
                    };
                    callback(@[[ret toJSONString]]);
                }
            }];
        }else{
            [[FATClient sharedClient] startRemoteApplet:appId sequence:@([sequence intValue]) startParams:params InParentViewController:currentVC transitionStyle:FATTranstionStylePush completion:^(BOOL result, NSError *error) {
                NSLog(@"result:%d---error:%@", result, error);
                NSDictionary* ret;
                if (result){
                    ret = @{
                        @"success": @(YES),
                        @"errMsg": @"openApplet:ok"
                    };
                    callback(@[[ret toJSONString]]);
                }else {
                    ret = @{
                        @"success": @(NO),
                        @"errMsg": error.description
                    };
                    callback(@[[ret toJSONString]]);
                }
            }];
        }
    });
    
}
@end
