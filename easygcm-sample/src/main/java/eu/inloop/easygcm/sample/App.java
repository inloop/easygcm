package eu.inloop.easygcm.sample;

import android.app.Application;
import android.os.Bundle;

import eu.inloop.easygcm.GcmListener;

public class App extends Application implements GcmListener {

    @Override
    public void onMessage(String from, Bundle data) {
        System.out.println("### message from: " + from);
        System.out.println("### bundle:");
        for (String key : data.keySet()) {
            System.out.println("> " + key + ": " + data.get(key));
        }
    }

    @Override
    public void sendRegistrationIdToBackend(String registrationId) {

    }
}
