package com.codeard.yandexmetrica;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.Promise;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.lang.Exception;

import org.json.JSONObject;

public class YandexAppmetricaModule extends ReactContextBaseJavaModule {

    private boolean dryRun = false;
    private boolean initialized = false;
    private static boolean initializedStatic = false;
    public static String TAG = "YandexAppmetrica";

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
        initialized = true;
        if (dryRun) {
          Log.i(TAG, "Dry run mode, skip Yandex Mobile Metrica activation");
          return;
        }
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(apiKey).build();
        YandexMetrica.activate(reactContext, config);
      }

      @ReactMethod
      public void reportEvent(String message, @Nullable ReadableMap params) {
    	if (dryRun) {
          Log.i(TAG, "Dry run mode, skip event reporting");
          return;
        }
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

      @ReactMethod
      public void reportError(String message) {
        try {
            Integer.valueOf("00xffWr0ng");
        }
        catch (Throwable error) {
            YandexMetrica.reportError(message, error);
        }
      }

      @ReactMethod
      public void setDryRun(Boolean enabled) {
        dryRun = enabled;
      }

      @ReactMethod
      public void isInitialized(Promise promise) {
        promise.resolve(initialized || initializedStatic);
      }

      public static void activate(Context context, String apiKey) {
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(apiKey).build();
        YandexMetrica.activate(context, config);
        initializedStatic = true;
      }

      public static void enableActivityAutoTracking(final Application app) {
        YandexMetrica.enableActivityAutoTracking(app);
      }
}
