package eu.inloop.easygcm;

import android.content.Intent;

public class WakeLockRelease {

    private final Intent mWakefulIntent;

    WakeLockRelease(Intent wakefulIntent) {
        this.mWakefulIntent = wakefulIntent;
    }

    /**
     * Call this after you are done with processing of the broadcast.
     */
    public void release() {
        GcmBroadcastReceiver.completeWakefulIntent(mWakefulIntent);
    }
}
