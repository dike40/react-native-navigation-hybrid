//
//  AppDelegate.m
//  Navigation
//
//  Created by Listen on 2017/11/18.
//  Copyright © 2017年 Listen. All rights reserved.
//

#import "AppDelegate.h"
#import <React/RCTBundleURLProvider.h>
#import <React/RCTLinkingManager.h>
#import <React/RCTBridgeModule.h>
#import <NavigationHybrid/NavigationHybrid.h>
#import <ProgressHUD/HBDProgressHUD.h>

#import "OneNativeViewController.h"
#import "CustomContainerViewController.h"
#import "NativeModalViewController.h"

@interface AppDelegate () <HostViewProvider>

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    [HUDConfig sharedConfig].hostViewProvider = self;
    
    NSURL *jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"playground/index" fallbackResource:nil];
    [[HBDReactBridgeManager sharedInstance] installWithBundleURL:jsCodeLocation launchOptions:launchOptions];
    
    // register native modules
    [[HBDReactBridgeManager sharedInstance] registerNativeModule:@"OneNative" forController:[OneNativeViewController class]];
    [[HBDReactBridgeManager sharedInstance] registerNativeModule:@"NativeModal" forController:[NativeModalViewController class]];
    
    HBDViewController *navigation = [[HBDReactBridgeManager sharedInstance] controllerWithModuleName:@"Navigation" props:nil options:nil];
    HBDNavigationController *navigationNav = [[HBDNavigationController alloc] initWithRootViewController:navigation];
    CustomContainerViewController *rootViewController = [[CustomContainerViewController alloc] init];
    rootViewController.contentViewController = navigationNav;
    HBDViewController *vc =  [[HBDReactBridgeManager sharedInstance] controllerWithModuleName:@"Transparent" props:@{@"text": @"Hello"} options:nil];
    rootViewController.overlayViewController = vc;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [vc setAppProperties:@{ @"text" : @"World"}];
    });
   
    self.window.rootViewController = rootViewController;
    [self.window makeKeyAndVisible];
    
    return YES;
}

//// iOS 8.x or older
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    return [RCTLinkingManager application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
}
//
// iOS 9.x or newer
//- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
//    return [RCTLinkingManager application:application openURL:url options:options];
//}

//// universal links
//- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler {
//    return [RCTLinkingManager application:application continueUserActivity:userActivity restorationHandler:restorationHandler];
//}

- (UIView *)hostView {
    UIApplication *application = [[UIApplication class] performSelector:@selector(sharedApplication)];
    UIViewController *controller = application.keyWindow.rootViewController;
    return [self controller:controller].view;
}

- (UIViewController *)controller:(UIViewController *)controller {
    UIViewController *presentedController = controller.presentedViewController;
    if (presentedController && ![presentedController isBeingDismissed]) {
        return [self controller:presentedController];
    } else if ([controller isKindOfClass:[HBDDrawerController class]]) {
        HBDDrawerController *drawer = (HBDDrawerController *)controller;
        if ([drawer isMenuOpened]) {
            return drawer;
        } else {
            return [self controller:drawer.contentController];
        }
    } else if ([controller isKindOfClass:[HBDTabBarController class]]) {
        HBDTabBarController *tabs = (HBDTabBarController *)controller;
        return [self controller:tabs.selectedViewController];
    }
    return controller;
}

@end
