//
//  APIController.m
//  PedidoNaRede
//
//  Created by Pedro Góes on 14/10/12.
//  Copyright (c) 2012 Pedro Góes. All rights reserved.
//

#import "APIController.h"
#import "NSString+URLEncoding.h"

#define kGlobalQueue dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)

@interface APIController ()

@property (nonatomic, strong) NSMutableData *JSONData;
@property (nonatomic, strong) NSDictionary *attributes;

@end

@implementation APIController

#pragma mark - Initializers

- (id)initWithDelegate:(id<APIControllerDelegate>)aDelegate {
    return [self initWithDelegate:aDelegate forcing:NO withMaxAge:3600.0 withUserInfo:nil];
}

- (id)initWithDelegate:(id<APIControllerDelegate>)aDelegate forcing:(BOOL)aForce {
    return [self initWithDelegate:aDelegate forcing:aForce withMaxAge:3600.0 withUserInfo:nil];
}

- (id)initWithDelegate:(id<APIControllerDelegate>)aDelegate forcing:(BOOL)aForce withUserInfo:(NSDictionary *)aUserInfo {
    return [self initWithDelegate:aDelegate forcing:aForce withMaxAge:3600.0 withUserInfo:aUserInfo];
}

- (id)initWithDelegate:(id<APIControllerDelegate>)aDelegate forcing:(BOOL)aForce withMaxAge:(NSTimeInterval)aMaxAge withUserInfo:(NSDictionary *)aUserInfo {
    
    self = [super init];
    if (self) {
        // Set our properties
        self.delegate = aDelegate;
        self.force = aForce;
        self.maxAge = aMaxAge;
        self.userInfo = aUserInfo;
    }
    return self;
}

#pragma mark - Activity

- (void)activityRequestEnrollmentAtActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID]}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"requestEnrollment" attributes:attributes];
    }
}

- (void)activityRequestEnrollmentForPerson:(NSInteger)personID atActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID], @"personID" : [NSString stringWithFormat:@"%d", personID]}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"requestEnrollment" attributes:attributes];
    }
}

- (void)activityDismissEnrollmentAtActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID]}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"dismissEnrollment" attributes:attributes];
    }
}

- (void)activityDismissEnrollmentForPerson:(NSInteger)personID atActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID], @"personID" : [NSString stringWithFormat:@"%d", personID]}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"dismissEnrollment" attributes:attributes];
    }
}

- (void)activityConfirmEntranceForPerson:(NSInteger)personID atActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {
   
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID], @"personID" : [NSString stringWithFormat:@"%d", personID]}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"confirmEntrance" attributes:attributes];
    }
}

- (void)activityGetPeopleAtActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID], @"selection" : @"all"}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"getPeople" attributes:attributes];
    }
}

- (void)activityGetQuestionsAtActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID], @"selection" : @"all"}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"getQuestions" attributes:attributes];
    }
}

- (void)activitySendQuestion:(NSString *)question toActivity:(NSInteger)activityID withTokenID:(NSString *)tokenID {

    if (tokenID != nil && question != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"activityID" : [NSString stringWithFormat:@"%d", activityID]}, @"POST" : @{@"question" : question}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"sendQuestion" attributes:attributes];
    }
}

- (void)activityUpvoteQuestion:(NSInteger)questionID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"questionID" : [NSString stringWithFormat:@"%d", questionID]}};
        
        [self JSONObjectWithNamespace:@"activity" method:@"upvoteQuestion" attributes:attributes];
    }
}

#pragma mark - Event

- (void)eventGetPeopleAtEvent:(NSInteger)eventID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"eventID" : [NSString stringWithFormat:@"%d", eventID], @"selection" : @"all"}};
        
        [self JSONObjectWithNamespace:@"event" method:@"getPeople" attributes:attributes];
    }   
}

- (void)eventGetActivitiesAtEvent:(NSInteger)eventID {

    NSDictionary *attributes = @{@"GET" : @{@"eventID" : [NSString stringWithFormat:@"%d", eventID]}};
    
    [self JSONObjectWithNamespace:@"event" method:@"getActivities" attributes:attributes];
}

- (void)eventGetScheduleAtEvent:(NSInteger)eventID withTokenID:(NSString *)tokenID {

    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"eventID" : [NSString stringWithFormat:@"%d", eventID]}};
        
        [self JSONObjectWithNamespace:@"event" method:@"getSchedule" attributes:attributes];
    }
}


#pragma mark - Notifications

- (void)notificationGetNumberOfNotificationsWithTokenID:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID}};
    
        [self JSONObjectWithNamespace:@"notification" method:@"getNumberOfNotifications" attributes:attributes];
    }
}

- (void)notificationGetNotificationsWithTokenID:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID}};
    
        [self JSONObjectWithNamespace:@"notification" method:@"getNotifications" attributes:attributes];
    }
}

- (void)notificationGetNotificationsSinceNotification:(NSInteger)lastNotificationID withTokenID:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"lastNotificationID" : [NSString stringWithFormat:@"%d", lastNotificationID]}};
    
        [self JSONObjectWithNamespace:@"notification" method:@"getNotificationsSinceNotification" attributes:attributes];
    }
}

- (void)notificationGetLastNotificationIDWithTokenID:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID}};
        
        [self JSONObjectWithNamespace:@"notification" method:@"getLastNotificationID" attributes:attributes];
    }
}

- (void)notificationGetNotificationsWithinTime:(NSInteger)seconds withTokenID:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"seconds" : [NSString stringWithFormat:@"%d", seconds]}};
    
        [self JSONObjectWithNamespace:@"notification" method:@"getLastNotificationID" attributes:attributes];
    }
}

- (void)notificationGetSingleNotification:(NSInteger)notificationID withTokenID:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID, @"notificationID" : [NSString stringWithFormat:@"%d", notificationID]}};
    
        [self JSONObjectWithNamespace:@"notification" method:@"getSingleNotification" attributes:attributes];
    }
}

#pragma mark - Opinion
- (void)opinionSendOpinionWithRating:(NSInteger)rating withMessage:(NSString *)message withToken:(NSString *)tokenID {
    
    if (message != nil && tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID}, @"POST" : @{@"message" : message, @"rating" : [NSString stringWithFormat:@"%d", rating]}};
        
        [self JSONObjectWithNamespace:@"opinion" method:@"sendOpinion" attributes:attributes];
    }
}

#pragma mark - Person
- (void)personSignIn:(NSString *)name withPassword:(NSString *)password {
    
    if (name != nil && password != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"name" : name, @"password" : password}};
        
        [self JSONObjectWithNamespace:@"person" method:@"signIn" attributes:attributes];
    }
}

- (void)personSignInWithFacebookToken:(NSString *)facebookToken {
    
    if (facebookToken != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"facebookToken" : facebookToken}};
        
        [self JSONObjectWithNamespace:@"person" method:@"signInWithFacebook" attributes:attributes];
    }
}

- (void)personRegister:(NSString *)name withPassword:(NSString *)password withEmail:(NSString *)email {

    if (name != nil && password != nil && email != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"name" : name, @"password" : password, @"email" : email}};
        
        [self JSONObjectWithNamespace:@"person" method:@"register" attributes:attributes];
    }
}

- (void)personGetEventsWithToken:(NSString *)tokenID {
    
    if (tokenID != nil) {
        NSDictionary *attributes = @{@"GET" : @{@"tokenID" : tokenID}};
        
        [self JSONObjectWithNamespace:@"person" method:@"getEvents" attributes:attributes];
    }
}

#pragma mark - Setup Methods

- (void)JSONObjectWithNamespace:(NSString *)namespace method:(NSString *)method attributes:(NSDictionary *)attributes {
    
    // Set our properties
    self.namespace = namespace;
    self.method = method;
    self.attributes = attributes;

    [self start];
}

#pragma mark - Connection Support

- (void)start {
    
    NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent:  @"Documents"] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@.json", _namespace, _method]];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    // Check if the data file is available inside the filesystem
    BOOL existance = [fileManager fileExistsAtPath:path];

    // Check if the maximum cache age has been surpassed
    NSError *attributesRetrievalError = nil;
    NSTimeInterval fileDate = [[[fileManager attributesOfItemAtPath:path error:&attributesRetrievalError] fileModificationDate] timeIntervalSince1970];
    NSTimeInterval currentDate = [[NSDate date] timeIntervalSince1970];
    
    // In case it has, we set the controller to the forced state
    if (currentDate - fileDate > self.maxAge) {
        self.force = YES;
    }
    
    // If the data shouldn't be download again (a.k.a. forced), we just retrieve it from the filesystem
    if (existance && !_force) {
        // Load it from the filesystem
        if ([self.delegate respondsToSelector:@selector(apiController:didLoadDictionaryFromServer:)]) {
            [self.delegate apiController:self didLoadDictionaryFromServer: [NSDictionary dictionaryWithContentsOfFile:path]];
        }
    } else {
        // Define our API url
        NSMutableString *url = [NSMutableString stringWithFormat:@"%@developer/api/?method=%@.%@", URL, _namespace, _method];
        
        // Concatenate all the GET attributes inside the URL
        for (NSString *param in [[_attributes objectForKey:@"GET"] allKeys]) {
            [url appendFormat:@"&%@=%@", param, [[[_attributes objectForKey:@"GET"] objectForKey:param] urlEncodeUsingEncoding:NSUTF8StringEncoding]];
        }
        
#ifdef DEBUG
        NSLog(@"%@", url);
#endif
    
        // Concatenate all the POST attributes inside the URL
        NSMutableString *postAttributes = [NSMutableString string];
        
        for (NSString *param in [[_attributes objectForKey:@"POST"] allKeys]) {
            [postAttributes appendFormat:@"&%@=%@", param, [[[_attributes objectForKey:@"POST"] objectForKey:param] urlEncodeUsingEncoding:NSUTF8StringEncoding]];
        }
        
        // Create a requisition
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0];
        
        if ([_attributes objectForKey:@"POST"] != nil) {
            [request setHTTPBody:[[postAttributes substringFromIndex:1] dataUsingEncoding:NSUTF8StringEncoding]];
            [request setHTTPMethod:@"POST"];
        }
        
        // Create a connection
        NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
        
        // Alloc object if true
        if (connection) {
            self.JSONData = [NSMutableData data];
        }
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    
    if ([((NSHTTPURLResponse *)response) statusCode] == 200) {
        [self.JSONData setLength:0];
        [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    } else {
        // Kill the connection
        [connection cancel];
        
        // Notify the delegate about the error
        if ([self.delegate respondsToSelector:@selector(apiController:didFailWithError:)]) {
            NSError *error = [NSError errorWithDomain:@"HTTP Code Error" code:[((NSHTTPURLResponse *)response) statusCode] userInfo:nil];
            [self.delegate apiController:self didFailWithError:error];
        }
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    // Append data
    [self.JSONData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    // Notify the delegate about the error
    if ([self.delegate respondsToSelector:@selector(apiController:didFailWithError:)]) {
        [self.delegate apiController:self didFailWithError:error];
    }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
 
#ifdef DEBUG
    NSLog(@"%@", [[NSString alloc] initWithData:self.JSONData encoding:NSUTF8StringEncoding]);
#endif
    
    // Check for integrity
    if (self.JSONData) {
        dispatch_async(kGlobalQueue, ^{
            NSError *error = nil;
            NSDictionary *JSON = [NSJSONSerialization JSONObjectWithData:self.JSONData options:0 error:&error];
            [self performSelectorOnMainThread:@selector(end:) withObject:JSON waitUntilDone:YES];
        });
    }
}

- (void)end:(NSDictionary *)JSON {

    // Some typo checking
    if (!JSON || !([JSON isKindOfClass:[NSDictionary class]])) {
        // Notify the delegate about the error
        if ([self.delegate respondsToSelector:@selector(apiController:didFailWithError:)]) {
            [self.delegate apiController:self didFailWithError:[NSError errorWithDomain:@"self" code:3840 userInfo:nil]];
        }
    } else {
        
        // Let's also save our JSON object inside a file
        NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent: @"Documents"]
                          stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@.json", _namespace, _method]];
        [JSON writeToFile:path atomically:YES];
        
        // Return our parsed object
        if ([self.delegate respondsToSelector:@selector(apiController:didLoadDictionaryFromServer:)]) {
            [self.delegate apiController:self didLoadDictionaryFromServer:JSON];
        }
    }
}


@end
