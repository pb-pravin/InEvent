//
//  UIView+Triangle.m
//  InEvent
//
//  Created by Pedro Góes on 16/08/13.
//  Copyright (c) 2013 Pedro Góes. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "NSObject+Triangle.h"
#import "HumanToken.h"
#import "UtilitiesController.h"

@implementation NSObject (Triangle)

- (void)createUpperTriangleAtView:(UIView *)view withState:(ScheduleState)state {
    
    UIColor *color;
    
    if (state == ScheduleStateUnknown) {
        color = [UIColor grayColor];
    } else if (state == ScheduleStateApproved) {
        color = [UtilitiesController colorFromHexString:@"#278D27"];
    } else if (state == ScheduleStateDenied) {
        color = [UtilitiesController colorFromHexString:@"#C51F1F"];
    }
    
    CGFloat width = view.frame.size.width;
    
    CGMutablePathRef path = CGPathCreateMutable();
    CGPathMoveToPoint(path, NULL, width * 0.95, 0.0f);
    CGPathAddLineToPoint(path, NULL, width, width * 0.05);
    CGPathAddLineToPoint(path, NULL, width, 0.0f);
    CGPathAddLineToPoint(path, NULL, width * 0.95, 0.0f);
    
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    [shapeLayer setPath:path];
    [shapeLayer setFillColor:[color CGColor]];
    [view.layer addSublayer:shapeLayer];
    
    CGPathRelease(path);
}

- (void)defineStateForApproved:(NSInteger)approved withView:(UIView *)view {
    
    // Remove all alpha and border views
    if (view.layer != nil) {
        for (CALayer *layer in view.layer.sublayers) {
            if ([layer isKindOfClass:[CAShapeLayer class]]) {
                [layer removeFromSuperlayer];
            }
        }
    }
    
    if ([[HumanToken sharedInstance] isMemberAuthenticated]) {
        if (approved == 1) {
            [self createUpperTriangleAtView:view withState:ScheduleStateApproved];
        } else {
            [self createUpperTriangleAtView:view withState:ScheduleStateDenied];
        }
    } else {
        [self createUpperTriangleAtView:view withState:ScheduleStateUnknown];
    }
}

@end
