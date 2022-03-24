#import "MopPlugin.h"
#import "MOPBaseApi.h"
#import "MOPApiRequest.h"
#import "MOPApiConverter.h"
#import "NSDictionary+MOP.h"

@implementation MopPlugin

+ (void)handleMethod:(NSString *)method arguments:(NSDictionary *)arguments mopSDK:(FINMopSDK *)mopSDK result:(MopBlock)result {
  if ([@"getPlatformVersion" isEqualToString:method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"getAppletInfo" isEqualToString:method]) {
      result([[self appInfoDictWithAppId:arguments[@"appId"]] toJSONString]);
  } else if ([@"getAbsolutePath" isEqualToString:method]) {
      NSString *path = arguments[@"path"];
      NSMutableDictionary *dict = [NSMutableDictionary dictionary];
      dict[@"path"] = [[FATClient sharedClient] fat_absolutePathWithPath:path];
      result([dict toJSONString]);
  } else {
      MOPApiRequest *request = [[MOPApiRequest alloc] init];
      request.command = method;
      request.param = (NSDictionary*)arguments;
      MOPBaseApi *api = [MOPApiConverter apiWithRequest:request];
      if (api) {
          api.mopSDK = mopSDK;
          
          [api setupApiWithSuccess:^(NSDictionary<NSString *,id> * _Nonnull data) {
              result([@{@"errMsg":@"ok",@"success":@(YES),@"data": data} toJSONString]);
          } failure:^(id _Nullable error) {
              if ([error isKindOfClass:[NSDictionary class]]) {
                  NSDictionary* dict = (NSDictionary*)error;
                  if (dict != nil) {
                      result([@{@"errMsg": dict ,@"success":@(NO)} toJSONString]);
                  } else {
                      result([@{@"errMsg": @"其它错误" ,@"success":@(NO)} toJSONString]);
                  }
              } else {
                  result([@{@"errMsg": error ,@"success":@(NO)} toJSONString]);
              }
          } cancel:^{
              
          }];
      } else {
          result([@{@"errMsg": @"api未实现" ,@"success":@(NO)} toJSONString]);
      }
  }
}

+ (NSDictionary *)appInfoDictWithAppId:(NSString *)appId {
    FATAppletInfo *info = [[FATClient sharedClient] currentApplet];
    if ([appId isEqualToString:info.appId]) {
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        dict[@"appId"] = info.appId;
        switch (info.appletVersionType) {
            case FATAppletVersionTypeRelease: {
                dict[@"appType"] = @"release";
                break;
            }
            case FATAppletVersionTypeTrial: {
                dict[@"appType"] = @"trial";
                break;
            }
            case FATAppletVersionTypeTemporary: {
                dict[@"appType"] = @"temporary";
                break;
            }
            case FATAppletVersionTypeReview: {
                dict[@"appType"] = @"review";
                break;
            }
            case FATAppletVersionTypeDevelopment: {
                dict[@"appType"] = @"development";
                break;
            }
            default:
                break;
        }
        return dict;
    }
    return nil;;
}

@end
