package eu.inloop.easygcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmPackageReplacedReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, final Intent intent) {
        if (intent != null && intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            if (GcmHelper.sLoggingEnabled) {
                Log.d(GcmUtils.TAG, "Received application update broadcast");
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