#import "YandexAppmetrica.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import <YandexMobileMetrica/YandexMobileMetrica.h>

@implementation YandexAppmetrica {

}

BOOL dryRun = false;
BOOL initialized = false;

RCT_EXPORT_MODULE();
RCT_EXPORT_METHOD(activateWithApiKey:(NSString *)apiKey)
{
  initialized = true;
  if (dryRun) {
    NSLog(@"Dry run mode, skip Yandex Mobile Metrica activation");
    return;
  }

YMMYandexMetricaConfiguration *configuration = [[YMMYandexMetricaConfiguration alloc] initWithApiKey:apiKey];
[YMMYandexMetrica activateWithConfiguration:configuration];
}

RCT_EXPORT_METHOD(reportEvent:(NSString *)event)
{
  if (dryRun) {
    NSLog(@"Dry run mode, skip event reporting");
    return;
  }
  [YMMYandexMetrica reportEvent:event
                      onFailure:^(NSError *error) {
  NSLog(@"DID FAIL REPORT EVENT: %@", event);
  NSLog(@"REPORT ERROR: %@", [error localizedDescription]);
                      }];
}

RCT_EXPORT_METHOD(setDryRun:(BOOL *)enabled)
{
  dryRun = enabled;
}

RCT_EXPORT_METHOD(isInitialized:(RCTPromiseResolveBlock)resolve
                  reject:(__unused RCTPromiseRejectBlock)reject)
{
    NSNumber *ret = [NSNumber numberWithBool:initialized];
    resolve(ret);
}

@end
