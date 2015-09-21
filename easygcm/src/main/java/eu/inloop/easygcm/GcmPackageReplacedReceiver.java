package eu.inloop.easygcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GcmPackageReplacedReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, final Intent intent) {
        if (intent != null && intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            if (GcmHelper.sLoggingEnabled) {
                GcmUtils.Logger.d("Received application update broadcast");
            }

            // Don't register again on same version code (might happen during development)
            if (!TextUtils.isEmpty(GcmHelper.getRegistrationId(context))) {
                if (GcmHelper.sLoggingEnabled) {
                    GcmUtils.Logger.d("Package replaced but registration already done, skipping");
                }
                return;
            }

            final int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            if (resultCode != ConnectionResult.SUCCESS) {
                GcmUtils.Logger.e("Package replaced but Play Services are not available, skipping. " + resultCode);
                return;
            }

            GcmHelper.getInstance().registerInBackground(context, new GcmHelper.RegistrationListener() {
                @Override
                public void onFinish() {
                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                }
            });
        }
    }
}
