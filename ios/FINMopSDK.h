#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

FOUNDATION_EXPORT NSString * const kMopEventReminder;

typedef void(^MopSDKChannelBlock)(id _Nullable result);

@interface FINMopSDK : RCTEventEmitter <RCTBridgeModule>

- (NSString *)callbackId;
- (void)sendEventWithName:(NSString *)name body:(id)body callback:(MopSDKChannelBlock)callback;

@end

NS_ASSUME_NONNULL_END
