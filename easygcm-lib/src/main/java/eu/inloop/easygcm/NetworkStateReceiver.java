package eu.inloop.easygcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Tomáš Isteník on 22.9.2015.
 */
public class NetworkStateReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, final Intent intent) {
        EasyGcm.Logger.d("Connection state changed event received...");

        if (GcmUtils.checkCanAndShouldRegister(context)) {
            Intent serviceIntent = GcmRegistrationService.createGcmRegistrationIntent(context, true);
            startWakefulService(context, serviceIntent);
        }
    }
}
