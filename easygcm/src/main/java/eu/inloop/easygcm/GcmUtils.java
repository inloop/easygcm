package eu.inloop.easygcm;

import android.util.Log;

class GcmUtils {

    private static final String TAG = "easygcm";

    static class Logger {
        static void d(String message) {
            Log.d(TAG, message);
        }

        static void w(String message) {
            Log.w(TAG, message);
        }
    }

}
