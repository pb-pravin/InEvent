//
//  CompanyToken.h
//  Garça
//
//  Created by Pedro Góes on 06/04/13.
//  Copyright (c) 2013 Pedro Góes. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "APIController.h"

@interface EventToken : NSObject <APIControllerDelegate>

@property (assign, nonatomic) NSInteger eventID;
@property (strong, nonatomic) NSString *name;

+ (EventToken *)sharedInstance;

- (BOOL)isEventSelected;
- (void)removeEvent;

@end
