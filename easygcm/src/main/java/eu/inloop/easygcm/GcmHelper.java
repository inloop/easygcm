package eu.inloop.easygcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import easygcm.R;

import static eu.inloop.easygcm.GcmUtils.Logger;

public final class GcmHelper {

    private static final String PREFS_EASYGCM = "easygcm";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static GcmHelper sInstance;
    static volatile boolean sLoggingEnabled = true;
    private final AtomicBoolean mRegistrationRunning = new AtomicBoolean(false);

    @SuppressWarnings("UnusedDeclaration")
    public static void init(Activity activity) {
        if (!(activity.getApplicationContext() instanceof GcmListener)) {
            throw new IllegalStateException("Application must implement GcmListener interface!");
        }
        getInstance().onCreate(activity);
    }

    synchronized static GcmHelper getInstance() {
        if (sInstance == null) {
            sInstance = new GcmHelper();
        }

        return sInstance;
    }

    private GcmHelper() {

    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLoggingEnabled(boolean isEnabled) {
        sLoggingEnabled = isEnabled;
    }

    private void onCreate(Activity activity) {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices(activity)) {
            final String currentRegId = getRegistrationId(activity);

            if (currentRegId.isEmpty()) {
                registerInBackground(activity, null);
            } else {
                if (sLoggingEnabled) {
                    Logger.d("Checking existing registration ID=[" + currentRegId + "]");
                }
            }
        } else {
            Logger.d("No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(Activity activity) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                if (sLoggingEnabled) {
                    Logger.d("This device is not supported.");
                }
                activity.finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final int appVersion = GcmUtils.getAppVersion(context);
        if (sLoggingEnabled) {
            Logger.d("Saving regId on app version " + appVersion);
        }
        final SharedPreferences.Editor editor = getGcmPreferences(context).edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        final String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            if (sLoggingEnabled) {
                Logger.d("Registration not found.");
            }
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        final int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        final int currentVersion = GcmUtils.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            if (sLoggingEnabled) {
                Logger.d("App version changed.");
            }
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    void registerInBackground(final Context context, final RegistrationListener registrationListener) {
        final Context appContext = context.getApplicationContext();
        if (mRegistrationRunning.getAndSet(true)) {
            if (sLoggingEnabled) {
                Logger.d("Registration already running. Skipping");
            }
            //call to release wakelock
            if (registrationListener != null) {
                registrationListener.onFinish();
            }
            return;
        }

        final String gcmSenderId = appContext.getResources().getString(R.string.easygcm_sender_id);
        if (TextUtils.isEmpty(gcmSenderId)) {
            throw new IllegalArgumentException("You have to override the easygcm_sender_id string resource to provide the GCM sender ID");
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(appContext);
                    final String regId = gcm.register(gcmSenderId);
                    msg = "Device registered, registration ID=" + regId;
                    if (sLoggingEnabled) {
                        Logger.d("New registration ID=[" + regId + "]");
                    }

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    if (appContext instanceof GcmListener) {
                        ((GcmListener) appContext).sendRegistrationIdToBackend(regId);
                    } else {
                        Logger.w("Application should implement GcmHelper interface!");
                    }

                    // Persist the regID - no need to register again.
                    storeRegistrationId(appContext, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                } finally {
                    mRegistrationRunning.set(false);
                    if (registrationListener != null) {
                        registrationListener.onFinish();
                    }
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (sLoggingEnabled) {
                    Logger.d("Post-registration message: " + msg);
                }
            }
        }.execute(null, null, null);
    }

    interface RegistrationListener {
        void onFinish();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(PREFS_EASYGCM, Context.MODE_PRIVATE);
    }

}
