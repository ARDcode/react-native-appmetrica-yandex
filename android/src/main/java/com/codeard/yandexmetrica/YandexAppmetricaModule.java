package com.codeard.yandexmetrica;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Currency;

import org.json.JSONObject;
import org.json.JSONArray;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.ecommerce.ECommerceAmount;
import com.yandex.metrica.ecommerce.ECommerceCartItem;
import com.yandex.metrica.ecommerce.ECommerceEvent;
import com.yandex.metrica.ecommerce.ECommerceOrder;
import com.yandex.metrica.ecommerce.ECommercePrice;
import com.yandex.metrica.ecommerce.ECommerceProduct;
import com.yandex.metrica.ecommerce.ECommerceReferrer;
import com.yandex.metrica.ecommerce.ECommerceScreen;
import com.yandex.metrica.profile.UserProfile;
import com.yandex.metrica.profile.Attribute;
import com.yandex.metrica.Revenue;

import java.lang.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class YandexAppmetricaModule extends ReactContextBaseJavaModule {

    private static String TAG = "YandexAppmetrica";

    private final ReactApplicationContext reactContext;

    public YandexAppmetricaModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return TAG;
    }

    public ECommerceScreen createScreen(ReadableMap params) {
        ECommerceScreen screen = new ECommerceScreen().setName(params.getString("screenName")).setSearchQuery(params.getString("searchQuery"));
        return screen;
    }

    public ECommerceProduct createProduct(ReadableMap params) {
        ECommercePrice actualPrice = new ECommercePrice(new ECommerceAmount(Integer.parseInt(params.getString("price")), params.getString("currency")));
        ECommerceProduct product = new ECommerceProduct(params.getString("sku")).setActualPrice(actualPrice).setName(params.getString("name"));
        return product;
    }

    public ECommerceCartItem createCartItem(ReadableMap params) {
        ECommerceScreen screen = this.createScreen(params);
        ECommerceProduct product = this.createProduct(params);
        ECommercePrice actualPrice = new ECommercePrice(new ECommerceAmount(Integer.parseInt(params.getString("price")), params.getString("currency")));
        ECommerceReferrer referrer = new ECommerceReferrer().setScreen(screen);
        ECommerceCartItem cartItem = new ECommerceCartItem(product, actualPrice, Integer.parseInt(params.getString("quantity"))).setReferrer(referrer);
        return cartItem;
    }

    @ReactMethod
    public void showScreen(ReadableMap params) {
        ECommerceScreen screen = this.createScreen(params);
        ECommerceEvent showScreenEvent = ECommerceEvent.showScreenEvent(screen);
        YandexMetrica.reportECommerce(showScreenEvent);
    }

    @ReactMethod
    public void showProductCard(ReadableMap params) {
        ECommerceScreen screen = this.createScreen(params);
        ECommerceProduct product = this.createProduct(params);
        ECommerceEvent showProductCardEvent = ECommerceEvent.showProductCardEvent(product, screen);
        YandexMetrica.reportECommerce(showProductCardEvent);
    }

    @ReactMethod
    public void addToCart(ReadableMap params) {
        ECommerceCartItem cartItem = this.createCartItem(params);
        ECommerceEvent addCartItemEvent = ECommerceEvent.addCartItemEvent(cartItem);
        YandexMetrica.reportECommerce(addCartItemEvent);
    }

    @ReactMethod
    public void removeFromCart(ReadableMap params) {
        ECommerceCartItem cartItem = this.createCartItem(params);
        ECommerceEvent removeCartItemEvent = ECommerceEvent.removeCartItemEvent(cartItem);
        YandexMetrica.reportECommerce(removeCartItemEvent);
    }

    @ReactMethod
    public void beginCheckout(ReadableArray products, String identifier) {
        ArrayList<ECommerceCartItem> cartItems = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            ReadableMap productData = products.getMap(i);
            cartItems.add(this.createCartItem(productData));
        }
        ECommerceOrder order = new ECommerceOrder(identifier, cartItems);
        ECommerceEvent beginCheckoutEvent = ECommerceEvent.beginCheckoutEvent(order);
        YandexMetrica.reportECommerce(beginCheckoutEvent);
    }

    @ReactMethod
    public void finishCheckout(ReadableArray products, String identifier) {
        ArrayList<ECommerceCartItem> cartItems = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            ReadableMap productData = products.getMap(i);
            cartItems.add(this.createCartItem(productData));
        }
        ECommerceOrder order = new ECommerceOrder(identifier, cartItems);
        ECommerceEvent purchaseEvent = ECommerceEvent.purchaseEvent(order);
        YandexMetrica.reportECommerce(purchaseEvent);
    }

    @ReactMethod
    public void activateWithApiKey(String apiKey) {
        YandexMetricaConfig.Builder configBuilder = YandexMetricaConfig.newConfigBuilder(apiKey).withLogs();
        YandexMetrica.activate(getReactApplicationContext().getApplicationContext(), configBuilder.build());
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Application application = activity.getApplication();
            YandexMetrica.enableActivityAutoTracking(application);
        }
    }

    @ReactMethod
    public void activateWithConfig(ReadableMap params) {
        YandexMetricaConfig.Builder configBuilder = YandexMetricaConfig.newConfigBuilder(params.getString("apiKey")).withLogs();
        if (params.hasKey("sessionTimeout")) {
            configBuilder.withSessionTimeout(params.getInt("sessionTimeout"));
        }
        if (params.hasKey("firstActivationAsUpdate")) {
            configBuilder.handleFirstActivationAsUpdate(params.getBoolean("firstActivationAsUpdate"));
        }
        YandexMetrica.activate(getReactApplicationContext().getApplicationContext(), configBuilder.build());
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Application application = activity.getApplication();
            YandexMetrica.enableActivityAutoTracking(application);
        }
    }

    @ReactMethod
    public void reportEvent(String message) {
        YandexMetrica.reportEvent(message);
    }

    @ReactMethod
    public void reportEventWithParams(String message, @Nullable ReadableMap params) {
        YandexMetrica.reportEvent(message, convertMapToJson(params).toString());
    }

    @ReactMethod
    public void reportError(@NonNull String message, @Nullable String exceptionError) {
        Throwable exception = null;
        if (exceptionError != null) {
            exception = new Throwable(exceptionError);
        }
        YandexMetrica.reportError(message, exception);
    }

    @ReactMethod
    public void reportError(@NonNull String message, @Nullable ReadableMap exceptionError) {
        Throwable exception = null;
        if (exceptionError != null) {
            exception = new Throwable(convertMapToJson(exceptionError).toString());
        }
        YandexMetrica.reportError(message, exception);
    }

    @ReactMethod
    public void reportRevenue(String productId, Double price, Integer quantity) {
        Revenue revenue = Revenue.newBuilder(price, Currency.getInstance("RUB"))
            .withProductID(productId)
            .withQuantity(quantity)
            .build();

        YandexMetrica.reportRevenue(revenue);
    }

    @ReactMethod
    public void setUserProfileID(String profileID) {
        YandexMetrica.setUserProfileID(profileID);
    }

    @ReactMethod
    public void setUserProfileAttributes(ReadableMap params) {
        UserProfile.Builder userProfileBuilder = UserProfile.newBuilder();
        ReadableMapKeySetIterator iterator = params.keySetIterator();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();

            switch (key) {
                // predefined attributes
                case "name":
                    userProfileBuilder.apply(
                    params.isNull(key)
                        ? Attribute.name().withValueReset()
                        : Attribute.name().withValue(params.getString(key))
                    );
                    break;
                case "gender":
                    // FIXME: can't access Gender
                    // userProfileBuilder.apply(
                    //   params.isNull(key)
                    //     ? Attribute.gender().withValueReset()
                    //     : Attribute.gender().withValue(
                    //         params.getString(key).equals("female")
                    //           ? GenderAttribute.Gender.FEMALE
                    //           : params.getString(key).equals("male")
                    //             ? GenderAttribute.Gender.MALE
                    //             : GenderAttribute.Gender.OTHER
                    //       )
                    // );
                    break;
                case "age":
                    userProfileBuilder.apply(
                    params.isNull(key)
                        ? Attribute.birthDate().withValueReset()
                        : Attribute.birthDate().withAge(params.getInt(key))
                    );
                    break;
                case "birthDate":
                    if (params.isNull(key)) {
                        userProfileBuilder.apply(
                        Attribute.birthDate().withValueReset()
                        );
                    } else if (params.getType(key) == ReadableType.Array) {
                        // an array of [ year[, month][, day] ]
                        ReadableArray date = params.getArray(key);
                        if (date.size() == 1) {
                            userProfileBuilder.apply(
                            Attribute.birthDate().withBirthDate(
                                date.getInt(0)
                            )
                            );
                        } else if (date.size() == 2) {
                            userProfileBuilder.apply(
                            Attribute.birthDate().withBirthDate(
                                date.getInt(0),
                                date.getInt(1)
                            )
                            );
                        } else {
                            userProfileBuilder.apply(
                            Attribute.birthDate().withBirthDate(
                                date.getInt(0),
                                date.getInt(1),
                                date.getInt(2)
                            )
                            );
                        }
                    } else {
                        // number of milliseconds since Unix epoch
                        Date date = new Date((long)params.getInt(key));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        userProfileBuilder.apply(
                        Attribute.birthDate().withBirthDate(cal)
                        );
                    }
                    break;
                case "notificationsEnabled":
                    userProfileBuilder.apply(
                    params.isNull(key)
                        ? Attribute.notificationsEnabled().withValueReset()
                        : Attribute.notificationsEnabled().withValue(params.getBoolean(key))
                    );
                    break;
                // custom attributes
                default:
                    // TODO: come up with a syntax solution to reset custom attributes. `null` will break type checking here
                    switch (params.getType(key)) {
                        case Boolean:
                            userProfileBuilder.apply(
                            Attribute.customBoolean(key).withValue(params.getBoolean(key))
                            );
                            break;
                        case Number:
                            userProfileBuilder.apply(
                            Attribute.customNumber(key).withValue(params.getDouble(key))
                            );
                            break;
                        case String:
                            String value = params.getString(key);
                            if (value.startsWith("+") || value.startsWith("-")) {
                                userProfileBuilder.apply(
                                Attribute.customCounter(key).withDelta(Double.parseDouble(value))
                                );
                            } else {
                                userProfileBuilder.apply(
                                Attribute.customString(key).withValue(value)
                                );
                            }
                            break;
                    }
            }
        }

        YandexMetrica.reportUserProfile(userProfileBuilder.build());
    }


    private JSONObject convertMapToJson(ReadableMap readableMap) {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        try {
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                switch (readableMap.getType(key)) {
                    case Null:
                        object.put(key, JSONObject.NULL);
                        break;
                    case Boolean:
                        object.put(key, readableMap.getBoolean(key));
                        break;
                    case Number:
                        object.put(key, readableMap.getDouble(key));
                        break;
                    case String:
                        object.put(key, readableMap.getString(key));
                        break;
                    case Map:
                        object.put(key, convertMapToJson(readableMap.getMap(key)));
                        break;
                    case Array:
                        object.put(key, convertArrayToJson(readableMap.getArray(key)));
                        break;
                }
            }
        }
        catch (Exception ex) {
            Log.d(TAG, "convertMapToJson fail: " + ex);
        }
        return object;
    }

    private JSONArray convertArrayToJson(ReadableArray readableArray) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < readableArray.size(); i++) {
                switch (readableArray.getType(i)) {
                    case Null:
                        break;
                    case Boolean:
                        array.put(readableArray.getBoolean(i));
                        break;
                    case Number:
                        array.put(readableArray.getDouble(i));
                        break;
                    case String:
                        array.put(readableArray.getString(i));
                        break;
                    case Map:
                        array.put(convertMapToJson(readableArray.getMap(i)));
                        break;
                    case Array:
                        array.put(convertArrayToJson(readableArray.getArray(i)));
                        break;
                }
            }
        }
        catch (Exception ex) {
            Log.d(TAG, "convertArrayToJson fail: " + ex);
        }
        return array;
    }

    @ReactMethod
    public void sendEventsBuffer() {
        YandexMetrica.sendEventsBuffer();
    }
}
