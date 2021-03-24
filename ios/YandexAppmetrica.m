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
    configuration.logs = YES;
    [YMMYandexMetrica activateWithConfiguration:configuration];
    RCTLogInfo(@"activateWithApiKey - %@", apiKey);
}

RCT_EXPORT_METHOD(activateWithConfig:(NSDictionary *)config) {
    YMMYandexMetricaConfiguration *configuration = [[YMMYandexMetricaConfiguration alloc] initWithApiKey:config[@"apiKey"]];
    configuration.logs = YES;
    if (config[@"sessionTimeout"] != (id)nil) {
        [configuration setSessionTimeout:[config[@"sessionTimeout"] intValue]];
    }
    if (config[@"firstActivationAsUpdate"] != (id)nil) {
        [configuration setHandleFirstActivationAsUpdate:[config[@"firstActivationAsUpdate"] boolValue]];
    }
    [YMMYandexMetrica activateWithConfiguration:configuration];
}

RCT_EXPORT_METHOD(reportEventWithParams:(NSString *)message params:(nullable NSDictionary *) params)
{
    RCTLogInfo(@"reportEvent message - %@", message);
    [YMMYandexMetrica reportEvent:message parameters:params onFailure:NULL];
}

RCT_EXPORT_METHOD(reportEvent:(NSString *)message)
{
    [YMMYandexMetrica reportEvent:message onFailure:NULL];
}

RCT_EXPORT_METHOD(reportError:(NSString *)message exception:(nullable NSString *) exceptionText) {
    NSException *exception = [[NSException alloc] initWithName:exceptionText reason:nil userInfo:nil];
    [YMMYandexMetrica reportError:message exception:exception onFailure:NULL];
}

RCT_EXPORT_METHOD(reportRevenue:(NSString *)productId price:(nonnull NSNumber *)price quantity:(NSUInteger)quantity)
{
    NSDecimalNumber *decimalPrice = [NSDecimalNumber decimalNumberWithDecimal:[price decimalValue]];
    YMMMutableRevenueInfo *revenueInfo = [[YMMMutableRevenueInfo alloc] initWithPriceDecimal:decimalPrice currency:@"RUB" quantity:quantity productID:productId  transactionID:NULL receiptData:NULL payload:NULL];

    [YMMYandexMetrica reportRevenue:[revenueInfo copy] onFailure:^(NSError *error) {
        NSLog(@"Revenue error: %@", error);
    }];
}

- (YMMECommerceScreen *)createScreen:(NSDictionary *)screen {
    YMMECommerceScreen *screenObj = [[YMMECommerceScreen alloc] initWithName:screen[@"screenName"] categoryComponents:@[] searchQuery:screen[@"searchQuery"] payload:@{}];
    return screenObj;
}

- (YMMECommerceProduct *)createProduct:(NSDictionary *)product {
    YMMECommerceAmount *actualFiat = [[YMMECommerceAmount alloc] initWithUnit:product[@"currency"] value:[NSDecimalNumber decimalNumberWithString:product[@"price"]]];
   YMMECommercePrice *actualPrice = [[YMMECommercePrice alloc] initWithFiat:actualFiat internalComponents:@[]];
    YMMECommerceProduct *productObj = [[YMMECommerceProduct alloc] initWithSKU:product[@"sku"] name:product[@"name"] categoryComponents:@[] payload:@{} actualPrice:actualPrice originalPrice:actualPrice promoCodes:@[]];
    
    return productObj;
}

- (YMMECommercePrice *)createPrice:(NSDictionary *)product {
    YMMECommerceAmount *priceObj = [[YMMECommerceAmount alloc] initWithUnit:product[@"currency"] value:[NSDecimalNumber decimalNumberWithString:product[@"price"]]];
    YMMECommercePrice *actualPrice = [[YMMECommercePrice alloc] initWithFiat:priceObj internalComponents:@[]];
    
    return actualPrice;
}

- (YMMECommerceCartItem *)createCartItem:(NSDictionary *)product {
    YMMECommerceScreen *screen = [self createScreen:@{}];
    
    YMMECommerceProduct *productObj = [self createProduct:product];
    
     YMMECommerceReferrer *referrer = [[YMMECommerceReferrer alloc] initWithType:@"" identifier:@"" screen:screen];
    
    NSDecimalNumber *quantity = [NSDecimalNumber decimalNumberWithString:product[@"quantity"]];
    
    YMMECommercePrice *actualPrice = [self createPrice:product];
    
    YMMECommerceCartItem *cartItem = [[YMMECommerceCartItem alloc]  initWithProduct:productObj quantity:quantity revenue:actualPrice referrer:referrer];
    
    return cartItem;
}

// Используйте его, чтобы сообщить об открытии какой-либо страницы, например: списка товаров, поиска, главной страницы.
RCT_EXPORT_METHOD(showScreen:(NSDictionary *)screen) {
    YMMECommerceScreen *screenObj = [self createScreen:screen];
    
    [YMMYandexMetrica reportECommerce:[YMMECommerce showScreenEventWithScreen:screenObj] onFailure:nil];
}

// Используйте его, чтобы сообщить о просмотре карточки товара среди других в списке.
RCT_EXPORT_METHOD(showProductCard:(NSDictionary *)product ) {
    YMMECommerceScreen *screen = [self createScreen:@{}];
    YMMECommerceProduct *productObj = [self createProduct:product];
    
    [YMMYandexMetrica reportECommerce:[YMMECommerce showProductCardEventWithProduct:productObj screen:screen] onFailure:nil];
}

RCT_EXPORT_METHOD(addToCart:(NSDictionary *)product) {
    YMMECommerceCartItem *cartItem = [self createCartItem:product];
    
    [YMMYandexMetrica reportECommerce:[YMMECommerce addCartItemEventWithItem:cartItem] onFailure:nil];
}

RCT_EXPORT_METHOD(removeFromCart:(NSDictionary *)product) {
    YMMECommerceCartItem *cartItem = [self createCartItem:product];

    [YMMYandexMetrica reportECommerce:[YMMECommerce removeCartItemEventWithItem:cartItem] onFailure:nil];
}

RCT_EXPORT_METHOD(beginCheckout:(NSArray<NSDictionary *> *)products identifier:(NSString *)identifier) {
    NSMutableArray *cartItems = [[NSMutableArray alloc] init];
    for(int i=0; i< products.count; i++){
       [cartItems addObject:[self createCartItem:products[i]]];
    }
    
    YMMECommerceOrder *order = [[YMMECommerceOrder alloc] initWithIdentifier:identifier
                                                                   cartItems:cartItems
                                                                     payload:@{}];
    
    [YMMYandexMetrica reportECommerce:[YMMECommerce beginCheckoutEventWithOrder:order] onFailure:nil];
}


RCT_EXPORT_METHOD(finishCheckout:(NSArray<NSDictionary *> *)products identifier:(NSString *)identifier) {
    NSMutableArray *cartItems = [[NSMutableArray alloc] init];
    for(int i=0; i< products.count; i++){
       [cartItems addObject:[self createCartItem:products[i]]];
    }
    YMMECommerceOrder *order = [[YMMECommerceOrder alloc] initWithIdentifier:identifier
                                                                   cartItems:cartItems
                                                                     payload:@{}];

    [YMMYandexMetrica reportECommerce:[YMMECommerce purchaseEventWithOrder:order] onFailure:nil];
}

RCT_EXPORT_METHOD(setUserProfileID:(NSString *)userProfileID) {
    [YMMYandexMetrica setUserProfileID:userProfileID];
}

RCT_EXPORT_METHOD(setUserProfileAttributes:(NSDictionary *)attributes) {
    YMMMutableUserProfile *profile = [[YMMMutableUserProfile alloc] init];
    NSMutableArray *attrsArray = [NSMutableArray array];

    for (NSString* key in attributes) {
        // predefined attributes
        if ([key isEqual: @"name"]) {
            if (attributes[key] == nil) {
                [attrsArray addObject:[[YMMProfileAttribute name] withValueReset]];
            } else {
                [attrsArray addObject:[[YMMProfileAttribute name] withValue: attributes[key]]];
            }
        } else if ([key isEqual: @"gender"]) {
            if (attributes[key] == nil) {
                [attrsArray addObject:[[YMMProfileAttribute gender] withValueReset]];
            } else {
                [attrsArray addObject:[[YMMProfileAttribute gender] withValue:[[attributes[key] stringValue] isEqual: @"female"] ? YMMGenderTypeFemale : [[attributes[key] stringValue] isEqual: @"male"] ? YMMGenderTypeMale : YMMGenderTypeOther]];
            }
        } else if ([key isEqual: @"age"]) {
            if (attributes[key] == nil) {
                [attrsArray addObject:[[YMMProfileAttribute birthDate] withValueReset]];
            } else {
                [attrsArray addObject:[[YMMProfileAttribute birthDate] withAge:[attributes[key] intValue]]];
            }
        } else if ([key isEqual: @"birthDate"]) {
            if (attributes[key] == nil) {
                [attrsArray addObject:[[YMMProfileAttribute birthDate] withValueReset]];
            } else if ([attributes[key] isKindOfClass:[NSArray class]]) {
                NSArray *date = [attributes[key] array];
                if ([date count] == 1) {
                    [attrsArray addObject:[[YMMProfileAttribute birthDate] withYear:[[date objectAtIndex:0] intValue]]];
                } else if ([[attributes[key] array] count] == 2) {
                    [attrsArray addObject:[[YMMProfileAttribute birthDate] withYear:[[date objectAtIndex:0] intValue] month:[[date objectAtIndex:1] intValue]]];
                } else if ([[attributes[key] array] count] == 3) {
                    [attrsArray addObject:[[YMMProfileAttribute birthDate] withYear:[[date objectAtIndex:0] intValue] month:[[date objectAtIndex:1] intValue] day:[[date objectAtIndex:2] intValue]]];
                }
            } else {
                // number of milliseconds since Unix epoch
                NSDate *date = [attributes[key] date];
                NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
                NSDateComponents *dateComponents =
                    [gregorian components:(NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay) fromDate:date];
                [attrsArray addObject:[[YMMProfileAttribute birthDate] withDateComponents:dateComponents]];
            }
        } else if ([key isEqual: @"notificationsEnabled"]) {
            if (attributes[key] == nil) {
                [attrsArray addObject:[[YMMProfileAttribute notificationsEnabled] withValueReset]];
            } else {
                [attrsArray addObject:[[YMMProfileAttribute notificationsEnabled] withValue:[attributes[key] boolValue]]];
            }
        // custom attributes
        }
        else if ([key isEqual: @"id"]) {
            [YMMYandexMetrica setUserProfileID:attributes[key]];
        } else {
            // TODO: come up with a syntax solution to reset custom attributes. `null` will break type checking here
            if ([attributes[key] isEqual: @YES] || [attributes[key] isEqual: @NO]) {
                [attrsArray addObject:[[YMMProfileAttribute customBool:key] withValue:[attributes[key] boolValue]]];
            } else if ([attributes[key] isKindOfClass:[NSNumber class]]) {
                [attrsArray addObject:[[YMMProfileAttribute customNumber:key] withValue:[attributes[key] doubleValue]]];
                // [NSNumber numberWithInt:[attributes[key] intValue]]
            } else if ([attributes[key] isKindOfClass:[NSString class]]) {
                if ([attributes[key] hasPrefix:@"+"] || [attributes[key] hasPrefix:@"-"]) {
                    [attrsArray addObject:[[YMMProfileAttribute customCounter:key] withDelta:[attributes[key] doubleValue]]];
                } else {
                    [attrsArray addObject:[[YMMProfileAttribute customString:key] withValue:attributes[key]]];
                }
            }
        }
    }

    [profile applyFromArray: attrsArray];
    [YMMYandexMetrica reportUserProfile:[profile copy] onFailure:^(NSError *error) {
        NSLog(@"Error: %@", error);
    }];
}

RCT_EXPORT_METHOD(sendEventsBuffer) {
    [YMMYandexMetrica sendEventsBuffer];
}

@end
