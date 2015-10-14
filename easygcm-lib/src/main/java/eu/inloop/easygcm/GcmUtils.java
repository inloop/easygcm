package eu.inloop.easygcm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

class GcmUtils {

    static final String TAG = "easygcm";

    static class Logger {
        static void d(String message) {
            Log.d(TAG, message);
        }

        static void w(String message) {
            Log.w(TAG, message);
        }

        static void e(String message) {
            Log.e(TAG, message);
        }
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    static int getAppVersion(Context context) {
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    static boolean checkCanAndShouldRegister(Context context) {
        if (!ConnectionUtils.isOnline(context)) {
            if (EasyGcm.sLoggingEnabled) {
                GcmUtils.Logger.d("Cannot register. Device is not online.");
            }
            return false;
        }

        if (EasyGcm.isRegistered(context)) {
            if (EasyGcm.sLoggingEnabled) {
                GcmUtils.Logger.d("Registration was already done: " + EasyGcm.getRegistrationId(context));
            }
            return false;
        }

        final int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            GcmUtils.Logger.e("Play Services are not available: " + resultCode);
            return false;
        }

        return true;
    }
}
