package com.codeard.yandexmetrica;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.support.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.lang.Exception;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.profile.UserProfile;
import com.yandex.metrica.profile.Attribute;

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

    @ReactMethod
    public void activateWithApiKey(String apiKey) {
        YandexMetricaConfig.Builder configBuilder = YandexMetricaConfig.newConfigBuilder(apiKey);
        YandexMetrica.activate(getReactApplicationContext().getApplicationContext(), configBuilder.build());
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Application application = activity.getApplication();
            YandexMetrica.enableActivityAutoTracking(application);
        }
    }

    @ReactMethod
    public void activateWithConfig(ReadableMap params) {
        YandexMetricaConfig.Builder configBuilder = YandexMetricaConfig.newConfigBuilder(params.getString("apiKey"));
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
    public void reportEvent(String message, @Nullable ReadableMap params) {
        try {
            if (params != null) {
                YandexMetrica.reportEvent(message, convertReadableMapToJson(params));
            } else {
                YandexMetrica.reportEvent(message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to report Yandex Mobile Metrica event: " + e);
        }
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
            exception = new Throwable(convertReadableMapToJson(exceptionError));
        }
        YandexMetrica.reportError(message, exception);
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
                     } else if (params.getType(key) == Array) {
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

    private String convertReadableMapToJson(final ReadableMap readableMap) {
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        JSONObject json = new JSONObject();

        try {
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();

                switch (readableMap.getType(key)) {
                    case Null:
                        json.put(key, null);
                        break;
                    case Boolean:
                        json.put(key, readableMap.getBoolean(key));
                        break;
                    case Number:
                        json.put(key, readableMap.getDouble(key));
                        break;
                    case String:
                        json.put(key, readableMap.getString(key));
                        break;
                    case Array:
                        json.put(key, readableMap.getArray(key));
                        break;
                    case Map:
                        json.put(key, convertReadableMapToJson(readableMap.getMap(key)));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, "convertReadableMapToJson fail: " + ex);
        }

        return json.toString();
    }
}
