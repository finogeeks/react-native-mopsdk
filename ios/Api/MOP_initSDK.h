//
//  MOP_initSDK.h
//  react-native-mopsdk
//
//  Created by 王兆耀 on 2024/1/9.
//

#import <UIKit/UIKit.h>
#import "MOPBaseApi.h"

NS_ASSUME_NONNULL_BEGIN

@interface MOP_initSDK : MOPBaseApi
@property (nonatomic, strong) NSDictionary *config;
@property (nonatomic, strong) NSDictionary *uiConfig;
@end

NS_ASSUME_NONNULL_END
