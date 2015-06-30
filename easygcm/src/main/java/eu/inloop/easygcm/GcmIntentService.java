package eu.inloop.easygcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        if (GcmHelper.sLoggingEnabled) {
            GcmUtils.Logger.d("Received message from: " + from);
        }

        GcmHelper.getInstance().getGcmListener(getApplication()).onMessage(from, data);

    }
    // [END receive_message]
}
