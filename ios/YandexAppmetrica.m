#import "YandexAppmetrica.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import <YandexMobileMetrica/YandexMobileMetrica.h>

@implementation YandexAppmetrica {

}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(activateWithApiKey:(NSString *)apiKey)
{
    YMMYandexMetricaConfiguration *configuration = [[YMMYandexMetricaConfiguration alloc] initWithApiKey:apiKey];
    [YMMYandexMetrica activateWithConfiguration:configuration];
}

RCT_EXPORT_METHOD(activateWithConfig:(NSDictionary *)config) {
    YMMYandexMetricaConfiguration *configuration = [[YMMYandexMetricaConfiguration alloc] initWithApiKey:config[@"apiKey"]];
    if (config[@"sessionTimeout"] != (id)[NSNull null]) {
        [configuration setSessionTimeout:[config[@"sessionTimeout"] intValue]];
    }
    if (config[@"firstActivationAsUpdate"] != (id)[NSNull null]) {
        [configuration setHandleFirstActivationAsUpdate:[config[@"firstActivationAsUpdate"] boolValue]];
    }
    [YMMYandexMetrica activateWithConfiguration:configuration];
}

RCT_EXPORT_METHOD(reportEvent:(NSString *)message)
{
    [YMMYandexMetrica reportEvent:message onFailure:NULL];
}

RCT_EXPORT_METHOD(reportEvent:(NSString *)message params:(nullable NSDictionary *) params)
{
    [YMMYandexMetrica reportEvent:message parameters:params onFailure:NULL];
}

RCT_EXPORT_METHOD(reportError:(NSString *)message exception:(nullable NSString *) exceptionText) {
    NSException *exception = [[NSException alloc] initWithName:exceptionText reason:nil userInfo:nil];
    [YMMYandexMetrica reportError:message exception:exception onFailure:NULL];
}

RCT_EXPORT_METHOD(setUserProfileAttributes:(NSDictionary *)userConfig) {
    YMMMutableUserProfile *profile = [[YMMMutableUserProfile alloc] init];

    if (userConfig[@"name"] != (id)[NSNull null]) {
        [profile apply:[YMMProfileAttribute name] withValue:userConfig[@"name"]];
    }

    if (userConfig[@"age"] != (id)[NSNull null]) {
        [profile apply:[YMMProfileAttribute birthDate] withAge:userConfig[@"age"]];
    }

    [YMMYandexMetrica setUserProfileID:userConfig[@"userProfileId"]];

    // Sending profile attributes.
    [YMMYandexMetrica reportUserProfile:[profile copy] onFailure:^(NSError *error) {
        NSLog(@"Error: %@", error);
    }];
}

@end
