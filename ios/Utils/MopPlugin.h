#import <Foundation/Foundation.h>
#import "FINMopSDK.h"

typedef void(^MopBlock)(NSString *result);

@interface MopPlugin : NSObject

+ (void)handleMethod:(NSString *)method arguments:(NSDictionary *)arguments mopSDK:(FINMopSDK *)mopSDK result:(MopBlock)result;

@end
