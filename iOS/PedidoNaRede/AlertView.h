//
//  AlertView.h
//  PedidoNaRede
//
//  Created by Pedro Góes on 14/10/12.
//  Copyright (c) 2012 Pedro Góes. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "APIController.h"

@class AlertView;

@protocol AlertViewDelegate <NSObject>
@optional

- (void)alertView:(AlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;

@end

@interface AlertView : UIView

@property (strong, nonatomic) IBOutlet UIView *masterView;
@property (strong, nonatomic) IBOutlet UIView *box;
@property (strong, nonatomic) IBOutlet UILabel *title;
@property (strong, nonatomic) IBOutlet UIView *messageBox;
@property (strong, nonatomic) IBOutlet UIButton *message;
@property (strong, nonatomic) IBOutlet UIButton *yesButton;
@property (strong, nonatomic) IBOutlet UIButton *noButton;
@property (assign, nonatomic) NSInteger errorCode;

@property (strong, nonatomic) id<AlertViewDelegate> delegate;

- (id)initWithTitle:(NSString *)title message:(NSString *)message delegate:(id<AlertViewDelegate>)delegate cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitle:(NSString *)otherButtonTitle;

- (void)configureView;
- (void)show;

@end