package eu.inloop.easygcm.sample;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import eu.inloop.easygcm.GcmListener;

public class App extends Application implements GcmListener {

    private static final String TAG = "App";

    @Override
    public void onMessage(String from, Bundle data) {
        Log.v(TAG, "### message from: " + from);
        Log.v(TAG, "### data: " + from);
        for (String key : data.keySet()) {
            Log.v(TAG, "### > " + key + ": " + data.get(key));
        }
    }

    @Override
    public void sendRegistrationIdToBackend(String registrationId) {
        Log.v(TAG, "### registration id: " + registrationId);
    }
}
