//
//  MOP_getBindApplets.m
//  FINMopSDK
//
//  Created by fino on 2025/3/18.
//  Copyright © 2025 Facebook. All rights reserved.
//

#import "MOP_getBindApplets.h"

@implementation MOP_getBindApplets

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel {
    FATFetchBindAppletRequest *request = [[FATFetchBindAppletRequest alloc] init];
    request.apiServer = self.apiServer;
    request.appClass = self.appClass;
    request.appStatus = self.appStatus;
    request.containForbiddenApp = self.containForbiddenApp;
    request.pageNo = self.pageNo;
    request.pageSize = self.pageSize;

    [[FATClient sharedClient] getBindAppletsWithRequest:request completion:^(FATFetchBindAppletResponse *response, FATError *aError) {
        NSLog(@"获取app关联小程序列表：%@--error:%@", response, aError);
        if (aError) {
            failure(aError.localizedDescription);
            return;
        }

        NSMutableArray *items = [NSMutableArray array];
        for (FATFetchBindApplet *fetchBindApplet in response.items) {
            NSDictionary *dict = [self dictFromBindApplet:fetchBindApplet];
            [items addObject:dict];
        }
        
        success(@{@"total": @(response.total), @"items": items});
    }];
    
}

- (NSDictionary *)dictFromBindApplet:(FATFetchBindApplet *)fetchBindApplet
{
    NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
    [dictM setValue:fetchBindApplet.apiServer forKey:@"apiServer"];
    [dictM setValue:fetchBindApplet.miniAppId forKey:@"miniAppId"];
    [dictM setValue:fetchBindApplet.name forKey:@"name"];
    [dictM setValue:fetchBindApplet.logo forKey:@"logo"];
    [dictM setValue:@(fetchBindApplet.displayStatus) forKey:@"displayStatus"];
    [dictM setValue:fetchBindApplet.desc forKey:@"desc"];
    [dictM setValue:fetchBindApplet.detailDesc forKey:@"detailDesc"];
    [dictM setValue:@(fetchBindApplet.isForbidden) forKey:@"isForbidden"];

    return dictM;
}

@end
