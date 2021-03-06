package com.eduardokraus.videoteca;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

public class Videoteca extends CordovaPlugin {

    private CallbackContext callbackContext;

    private static final String TAG = "VideotecaPlugin: ";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("fullscreenOn")) {
            return fullscreenOn();
        } else if (action.equals("fullscreenOff")) {
            return fullscreenOff();
        } else if (action.equals("appdata")) {

            try {
                Activity activity = this.cordova.getActivity();

                PackageManager packageManager = activity.getPackageManager();
                ApplicationInfo app = packageManager.getApplicationInfo(activity.getPackageName(), 0);

                String getAppName = (String) packageManager.getApplicationLabel(app);
                String getPackageName = activity.getPackageName();
                String getVersionNumber = packageManager.getPackageInfo(activity.getPackageName(), 0).versionName;

                JSONObject retorno = new JSONObject();
                retorno.put("appName", getAppName);
                retorno.put("appPackageName", getPackageName);
                retorno.put("appVersionNumber", getVersionNumber);

                String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                String osversion = android.os.Build.VERSION.RELEASE;
                String model = android.os.Build.MODEL;
                String manufacturer = android.os.Build.MANUFACTURER;
                boolean isVirtual = Build.FINGERPRINT.contains("generic") || Build.PRODUCT.contains("sdk");

                retorno.put("platformUUID", uuid);
                retorno.put("platformVersion", osversion);
                retorno.put("platformName", "Android");
                retorno.put("platformModel", model);
                retorno.put("platformManufacturer", manufacturer);
                retorno.put("platformIsVirtual", isVirtual);

                callbackContext.success(retorno);
                return true;
            } catch (Exception e) {
                callbackContext.success("N/A");
                return true;
            }

        } else {
            callbackContext.error(TAG + action + " is not a supported method.");
            return false;
        }
    }

    private boolean fullscreenOn() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cordova.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                cordova.getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });

        callbackContext.success();
        return true;
    }

    private boolean fullscreenOff() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cordova.getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        });

        callbackContext.success();
        return true;
    }
}