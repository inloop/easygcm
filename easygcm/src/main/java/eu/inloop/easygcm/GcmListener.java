package eu.inloop.easygcm;

import android.os.Bundle;

public interface GcmListener {

    /**
     * Event called when message the is received.<br>
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     */
    void onMessage(String from, Bundle data);

    /**
     * Sends the registration ID to your server over HTTP.
     * @param registrationId registration ID received from server
     */
    void sendRegistrationIdToBackend(String registrationId);

}
