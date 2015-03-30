package eu.inloop.easygcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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

    private static GcmHelper sInstance;
    private GcmListener mGcmListener;
    private GcmServicesHandler mCheckServicesHandler;
    static volatile boolean sLoggingEnabled = true;
    private final AtomicBoolean mRegistrationRunning = new AtomicBoolean(false);
    private static final int DEFAULT_BACKOFF_MS = 2000;
    private static final int MAX_RETRIES = 5;

    @SuppressWarnings("UnusedDeclaration")
    public static void init(Activity activity) {
        getInstance().onCreate(activity);
    }

    synchronized static GcmHelper getInstance() {
        if (sInstance == null) {
            sInstance = new GcmHelper();
        }
        return sInstance;
    }

    private GcmHelper() {
        mCheckServicesHandler = new GcmServicesHandler();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLoggingEnabled(boolean isEnabled) {
        sLoggingEnabled = isEnabled;
    }

    /**
     * Allows to specify custom {@link eu.inloop.easygcm.GcmListener} if you don't want to implement it in the {@link
     * android.app.Application} instance.
     *
     * This method should be called in {@link android.app.Application#onCreate()}.
     *
     * @param gcmListener custom GCM listener
     */
    public static void setGcmListener(GcmListener gcmListener) {
        getInstance().mGcmListener = gcmListener;
    }

    /**
     * Allows to specify a custom {@link eu.inloop.easygcm.GcmServicesHandler} which handles a situation
     * when Google Play services are not available. Typically this should display a warning dialog.
     * The default handler shows
     * {@link com.google.android.gms.common.GooglePlayServicesUtil#getErrorDialog(int, android.app.Activity, int)}
     *
     * @param handler your custom handler for checking GcmServices.
     */
    public static void setCheckServicesHandler(GcmServicesHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("GcmServicesHandler can't be null");
        }
        getInstance().mCheckServicesHandler = handler;
    }

    private void onCreate(Activity context) {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices(context)) {
            final String currentRegId = getRegistrationId(context);

            if (currentRegId.isEmpty()) {
                registerInBackground(context, null);
            } else {
                if (sLoggingEnabled) {
                    Logger.d("Checking existing registration ID=[" + currentRegId + "]");
                }
            }
        } else {
            if (sLoggingEnabled) {
                Logger.d("No valid Google Play Services found.");
            }
        }
    }

    private boolean checkPlayServices(Activity activity) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            mCheckServicesHandler.onPlayServicesUnavailable(activity, resultCode,
                    GooglePlayServicesUtil.isUserRecoverableError(resultCode));
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

        final AsyncTask<Void, Void, Void> registrationTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(appContext);
                String regId = null;
                long currentBackoff = DEFAULT_BACKOFF_MS;

                for (int i = 0; i < MAX_RETRIES; i++) {
                    try {
                        regId = gcm.register(gcmSenderId);
                        break;
                    }  catch (IOException ex) {
                        if (sLoggingEnabled) {
                            Logger.w("Failed to register. Error :" + ex.getMessage());
                        }
                        // If there is an error, don't just keep trying to register.
                        // Require the user to click a button again, or perform
                        // exponential back-off.

                        if (i < MAX_RETRIES - 1) {
                            try {
                                Thread.sleep(currentBackoff);
                            } catch (InterruptedException e) {
                                //
                            }
                            currentBackoff *= 2;
                        }
                    }
                }

                if (regId != null) {
                    if (sLoggingEnabled) {
                        Logger.d("New registration ID=[" + regId + "]");
                    }

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    getGcmListener(appContext).sendRegistrationIdToBackend(regId);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(appContext, regId);

                } else {
                    if (sLoggingEnabled) {
                        Logger.w("Definitely failed to register after " + MAX_RETRIES + " retries");
                    }
                }

                mRegistrationRunning.set(false);
                if (registrationListener != null) {
                    registrationListener.onFinish();
                }

                return null;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            registrationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
        } else {
            registrationTask.execute(null, null, null);
        }

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

    GcmListener getGcmListener(Context context) {
        if (mGcmListener != null) {
            return mGcmListener;
        }
        if (context.getApplicationContext() instanceof GcmListener) {
            return (GcmListener)context.getApplicationContext();
        }
        throw new IllegalStateException("Please implement GcmListener in your Application or use method " +
            "setGcmListener()");
    }

}
