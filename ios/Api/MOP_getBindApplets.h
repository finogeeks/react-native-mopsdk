//
//  MOP_getBindApplets.h
//  FINMopSDK
//
//  Created by fino on 2025/3/18.
//  Copyright © 2025 Facebook. All rights reserved.
//

#import "MOPBaseApi.h"

NS_ASSUME_NONNULL_BEGIN

@interface MOP_getBindApplets : MOPBaseApi

/** 服务器地址，必填，不可为空*/
@property (nonatomic, copy) NSString *apiServer;
/** 小程序的分类，默认为‘’ */
@property (nonatomic, copy) NSString *appClass;
/** 需要获取的小程序状态类型，默认为FATAppStatusListed */
@property (nonatomic, assign) int appStatus;
/** 是否获取被禁用的小程序，默认为NO*/
@property (nonatomic, assign) BOOL containForbiddenApp;
/** 分页的页码，默认为1*/
@property (nonatomic, assign) NSUInteger pageNo;
/** 分页的大小，默认为20*/
@property (nonatomic, assign) NSUInteger pageSize;

@end

NS_ASSUME_NONNULL_END
