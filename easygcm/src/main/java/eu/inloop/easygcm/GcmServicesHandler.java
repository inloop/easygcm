package eu.inloop.easygcm;

import android.app.Activity;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GcmServicesHandler {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    protected void onPlayServicesUnavailable(Activity context, int errorCode, boolean recoverable) {
        GooglePlayServicesUtil.getErrorDialog(errorCode, context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
        if (GcmHelper.sLoggingEnabled) {
            GcmUtils.Logger.d("This device is not supported. Error code " + errorCode + " Recoverable - " + recoverable);
        }
    }
}
