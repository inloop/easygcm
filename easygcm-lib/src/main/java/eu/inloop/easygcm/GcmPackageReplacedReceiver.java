package eu.inloop.easygcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmPackageReplacedReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, final Intent intent) {
        if (intent != null && intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            EasyGcm.Logger.d("Received application update broadcast");

            if (GcmUtils.checkCanAndShouldRegister(context)) {
                Intent serviceIntent = GcmRegistrationService.createGcmRegistrationIntent(context, true);
                startWakefulService(context, serviceIntent);
            }
        }
    }
}
