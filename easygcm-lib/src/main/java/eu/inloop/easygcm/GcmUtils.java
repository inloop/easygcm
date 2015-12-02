package eu.inloop.easygcm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

class GcmUtils {

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
            EasyGcm.Logger.d("Cannot register. Device is not online.");
            return false;
        }

        if (EasyGcm.isRegistered(context)) {
            EasyGcm.Logger.d("Registration was already done: " + EasyGcm.getRegistrationId(context));
            return false;
        }

        final int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            EasyGcm.Logger.e("Play Services are not available: " + resultCode);
            return false;
        }

        return true;
    }
}
