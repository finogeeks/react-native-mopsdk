//
//  Mop_LoadingView.m
//  FINMopSDK
//
//  Created by fino on 2023/12/19.
//  Copyright © 2023 Facebook. All rights reserved.
//

#import "Mop_LoadingView.h"


@implementation Mop_LoadingView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.bottomImageView.hidden = YES;
    }
    return self;
}

@end
