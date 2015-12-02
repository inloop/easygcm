package eu.inloop.easygcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * This {@code GcmListenerService} does the actual handling of the GCM message.
 */
public class EasyGcmListenerService extends GcmListenerService {

    private static final String TAG = "EasyGcmListenerService";

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
        EasyGcm.Logger.d("Received message from: " + from);

        EasyGcm.getInstance().getGcmListener(getApplication()).onMessage(from, data);

    }
    // [END receive_message]
}
