package eu.inloop.easygcm;

import android.os.Bundle;

public interface GcmListener {

    /**
     * Event called when message the is received.<br>
     * It is <b>important to call</b> {@link WakeLockRelease#release()} when you are done handling the event
     * to release the wake lock!
     *
     * @param messageType Message type as received from {@link com.google.android.gms.gcm.GoogleCloudMessaging#getMessageType(android.content.Intent)}
     * @param extras content of the received intent
     * @param wakeLockRelease helper for holding and releasing the wake lock. Always call {@link WakeLockRelease#release()} when you are done.
     */
    void onMessage(String messageType, Bundle extras, WakeLockRelease wakeLockRelease);

    /**
     * Sends the registration ID to your server over HTTP.
     * @param registrationId registration ID received from server
     */
    void sendRegistrationIdToBackend(String registrationId);

}
