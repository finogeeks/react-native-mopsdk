//
//  MOPAppletDelegate.h
//  mop
//
//  Created by 康旭耀 on 2020/4/20.
//

#import <Foundation/Foundation.h>
#import <FinApplet/FinApplet.h>
#import "FINMopSDK.h"

NS_ASSUME_NONNULL_BEGIN

@interface MOPAppletDelegate : NSObject<FATAppletDelegate>

/**
 FINMopSDK
 */
@property (nonatomic, weak) FINMopSDK *mopSDK;

+ (instancetype)instance;

@end

NS_ASSUME_NONNULL_END
