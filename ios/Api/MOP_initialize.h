//
//  MOP_initialize.h
//  mop
//
//  Created by 杨涛 on 2020/2/27.
//

#import <UIKit/UIKit.h>
#import "MOPBaseApi.h"

NS_ASSUME_NONNULL_BEGIN

@interface MOP_initialize : MOPBaseApi

@property (nonatomic, copy) NSString *appkey;
@property (nonatomic, copy) NSString *secret;
@property (nonatomic, copy) NSString *apiServer;
@property (nonatomic, copy) NSString *apiPrefix;
@property (nonatomic, copy) NSString *cryptType;
@property (nonatomic, assign) BOOL encryptServerData;
@property (nonatomic, copy) NSString *userId;
@property (nonatomic, strong) NSArray *finStoreConfigs;
@property (nonatomic, strong) NSDictionary *uiConfig;
@property (nonatomic, copy) NSString *customWebViewUserAgent;
@property (nonatomic, assign) BOOL disablePermission;
@property (nonatomic, assign) NSInteger appletIntervalUpdateLimit;
@property (nonatomic, assign) NSInteger maxRunningApplet;

/**
 设置SDK的语言，应用公共UI（比如关于、设置、更多面板等）上展示的文字。默认为中文,当值为1的时候，为英文
 设置不支持的语言时，显示默认值
 */
@property (nonatomic, assign) NSInteger language;

/**
 自定义SDK的语言，优先级高于内置的 language 属性。
 示例：
 如果是放在 mainBundle 下，则设置相对路径：@"abc.lproj"
 如果是放在自定于 Bundle 下，则设置相对路径：@"bundleName.bundle/abc.lproj"
 */
@property (nonatomic, copy, nullable) NSString *customLanguagePath;

/**
 注入小程序统称appletText字符串，默认为“小程序”。
*/
@property (nonatomic, copy, nullable) NSString *appletText;


@end

NS_ASSUME_NONNULL_END
