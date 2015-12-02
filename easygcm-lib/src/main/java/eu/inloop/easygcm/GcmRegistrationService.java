package eu.inloop.easygcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Tomáš Isteník on 22.9.2015.
 */
public class GcmRegistrationService extends IntentService {

    public static final String EXTRA_ACTION_CODE = "actionCode";
    public static final String EXTRA_HAS_WAKELOCK = "hasWakeLock";

    public static final int ACTION_REGISTER_GCM = 1;

    public static final int MAX_RETRIES = 5;
    public static final int DEFAULT_BACKOFF_MS = 2000;

    private Intent mIntent;

    public GcmRegistrationService() {
        super(GcmRegistrationService.class.getName());
    }

    public static Intent createGcmRegistrationIntent(Context context) {
        Intent intent = new Intent(context, GcmRegistrationService.class);
        intent.putExtra(EXTRA_ACTION_CODE, ACTION_REGISTER_GCM);
        return intent;
    }

    public static Intent createGcmRegistrationIntent(Context context, boolean hasWakeLock) {
        Intent intent = createGcmRegistrationIntent(context);
        intent.putExtra(EXTRA_HAS_WAKELOCK, hasWakeLock);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mIntent = intent;
        int actionCode = intent.getIntExtra(EXTRA_ACTION_CODE, 0);
        switch (actionCode){
            // Not really needed, only handles one type of intent
            case ACTION_REGISTER_GCM:
                if (isAlreadyRegistered(getApplicationContext())) {
                    EasyGcm.Logger.w("The application was registered already before this registration could start.");
                    releaseWakeLock();
                    return;
                }
                registerGcm();
                releaseWakeLock();
                break;
        }
    }

    private void registerGcm() {
        String regId = null;
        String gcmHelperId = EasyGcm.getGcmSenderId(getApplicationContext());

        long currentBackoff = DEFAULT_BACKOFF_MS;

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                regId = instanceID.getToken(gcmHelperId,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                break;
            } catch (IOException ex) {
                EasyGcm.Logger.w("Failed to register. Error :" + ex.getMessage());

                if (i < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(currentBackoff);
                    } catch (InterruptedException e) {
                        //
                    }
                    currentBackoff *= 2;
                }
            } catch (SecurityException ex) {

                EasyGcm.Logger.w("Failed to register. Error :" + ex.getMessage());
                // On some devices like (GT-P5210, NokiaX2DS , GT-I9082L, W100, ILIUM
                // S220) and/with custom ROM's library crashes with following error:
                // java.lang.SecurityException: Not allowed to start service Intent
                // { act=com.google.android.c2dm.intent.REGISTER pkg=com.google.android.gms (has extras) }
                // without permission com.google.android.c2dm.permission.RECEIVE ...
                // We think, it is probably due to missing Google Play Services, but we do not have
                // proper feedback on this.
                // Since there is no known solution at the moment for this, we will just catch it.
            }
        }

        if (regId != null) {
            EasyGcm.Logger.d("New registration ID=[" + regId + "]");
            EasyGcm.getInstance().onSuccessfulRegistration(getApplicationContext(), regId);

        } else {
            EasyGcm.Logger.w("Definitely failed to register after " + MAX_RETRIES + " retries");
        }
    }

    private boolean isAlreadyRegistered(Context context) {
        return EasyGcm.isRegistered(context);
    }

    private void releaseWakeLock() {
        if (mIntent.getBooleanExtra(EXTRA_HAS_WAKELOCK, false)) {
            Intent intent = mIntent;
            mIntent = null;
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        } else {
            mIntent = null;
        }
    }
}
