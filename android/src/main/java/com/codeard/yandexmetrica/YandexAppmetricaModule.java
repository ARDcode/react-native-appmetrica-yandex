package com.codeard.yandexmetrica;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.json.JSONObject;

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
