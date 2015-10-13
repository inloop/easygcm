package eu.inloop.easygcm;

import android.os.Bundle;

public interface GcmListener {

    /**
     * Event called when a message is received.<br>
     * <p/>
     * This method is executed asynchronously in background thread
     * (currently by {@link android.os.AsyncTask#THREAD_POOL_EXECUTOR}) and the wake lock is held
     * until the method is executed. Don't start other asynchronous tasks here unless needed -
     * the wake lock would be released and the tasks would not be guaranteed to run.
     * Put the blocking code right into this method.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     */
    void onMessage(String from, Bundle data);

    /**
     * Here you can send the registration ID to your server over HTTP.
     * <p/>
     * This method is executed asynchronously in background thread
     * (currently by {@link android.os.AsyncTask#THREAD_POOL_EXECUTOR}) and the wake lock is held
     * until the method is executed. Don't start other asynchronous tasks here unless needed -
     * the wake lock would be released and the tasks would not be guaranteed to run.
     * Put the blocking code right into this method.
     *
     * @param registrationId registration ID received from server
     */
    void sendRegistrationIdToBackend(String registrationId);
}
