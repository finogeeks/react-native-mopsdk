#import "NSDictionary+MOP.h"

@implementation NSDictionary (MOP)

- (NSString *)toJSONString {
  NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self options:0 error:0];
  NSString *retStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
  return [retStr copy];
}

@end