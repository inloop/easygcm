package eu.inloop.easygcm;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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

    /**
     * Allows to specify custom {@link eu.inloop.easygcm.GcmListener} if you don't want to implement it in the {@link
     * android.app.Application} instance.
     * <p/>
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
     * {@link com.google.android.gms.common.GoogleApiAvailability#getErrorDialog(android.app.Activity, int, int)}
     *
     * @param handler your custom handler for checking GcmServices.
     */
    public static void setCheckServicesHandler(GcmServicesHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("GcmServicesHandler can't be null");
        }
        getInstance().mCheckServicesHandler = handler;
    }

    /**
     * Checks, whether a registration Id for an app exists. If not, the app needs to register
     * @param context application's context.
     * @return true if a registration Id is persisted in shared preferences, false otherwise.
     */
    public static boolean isRegistered(Context context) {
        return !TextUtils.isEmpty(getRegistrationId(context));
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private static void storeRegistrationId(Context context, String regId) {
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
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private static String getRegistrationId(Context context) {
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
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGcmPreferences(Context context) {
        // The registrationId is stored in SharedPreferences by the library.
        return context.getSharedPreferences(PREFS_EASYGCM, Context.MODE_PRIVATE);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLoggingEnabled(boolean isEnabled) {
        sLoggingEnabled = isEnabled;
    }

    /**
     * Registers the application defined by a context activity to GCM in case the registration
     * has not been done already.
     * @param context Activity belonging to the app being registered
     */
    private void onCreate(Activity context) {
        // The check method fails if: device is offline / app already registered / GooglePlayServices unavailable
        if (GcmUtils.checkCanAndShouldRegister(context)) {
            // Start a background service to register in a background thread
            context.startService(GcmRegistrationService.createGcmRegistrationIntent(context));
        }
    }

    /**
     * Get GCM sender id from available configuration.
     * <p/>
     * Returns gcm_defaultSenderId if it's provided by the google services gradle plugin via
     * google-services.json. Otherwise, returns easygcm_sender_id value for easygcm backward
     * compatibility.
     *
     * @param context Application context
     * @return GCM sender id
     */

    public static String getGcmSenderId(Context context) {
        final Context appContext = context.getApplicationContext();

        // Try to use gcm_defaultSenderId generated by google services gradle task
        String gcmSenderId = appContext.getResources().getString(R.string.gcm_defaultSenderId);
        if (!TextUtils.isEmpty(gcmSenderId)) {
            return gcmSenderId;
        }

        // Try to use easygcm_sender_id value for backward compatibility
        gcmSenderId = appContext.getResources().getString(R.string.easygcm_sender_id);

        if (!TextUtils.isEmpty(gcmSenderId)) {
            return gcmSenderId;
        }

        throw new IllegalArgumentException("You have to override the easygcm_sender_id string "
                + "resource to provide the GCM sender ID, OR provide it using google services "
                + "gradle plugin and google-services.json configuration.");
    }

    // Called from an IntentService background thread
    void onSuccessfulRegistration(Context context, String regId) {

        // You should send the registration ID to your server over HTTP, so it
        // can use GCM/HTTP or CCS to send messages to your app.
        getGcmListener(context).sendRegistrationIdToBackend(regId);

        // Persist the regID - no need to register again.
        // Also serves for detection of previous registrations
        storeRegistrationId(context, regId);
    }

    public GcmListener getGcmListener(Context context) {
        if (mGcmListener != null) {
            return mGcmListener;
        }
        if (context.getApplicationContext() instanceof GcmListener) {
            return (GcmListener) context.getApplicationContext();
        }
        throw new IllegalStateException("Please implement GcmListener in your Application or use method " +
                "setGcmListener()");
    }

}
