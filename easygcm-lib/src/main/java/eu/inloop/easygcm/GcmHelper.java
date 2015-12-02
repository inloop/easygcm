package eu.inloop.easygcm;

import android.content.Context;

@Deprecated
public final class GcmHelper {

    private static GcmHelper sInstance;

    @SuppressWarnings("UnusedDeclaration")
    public static void init(Context context) {
        EasyGcm.init(context);
    }

    synchronized static GcmHelper getInstance() {
        if (sInstance == null) {
            sInstance = new GcmHelper();
        }
        return sInstance;
    }

    private GcmHelper() {
    }

    public static void setGcmListener(GcmListener gcmListener) {
        EasyGcm.setGcmListener(gcmListener);
    }

    public static void setCheckServicesHandler(GcmServicesHandler handler) {
        EasyGcm.setCheckServicesHandler(handler);
    }

    public static boolean isRegistered(Context context) {
        return EasyGcm.isRegistered(context);
    }

    public static String getRegistrationId(Context context) {
        return EasyGcm.getRegistrationId(context);
    }

    public static void removeRegistrationId(Context context) {
        EasyGcm.removeRegistrationId(context);
    }

    public static String getGcmSenderId(Context context) {
        return EasyGcm.getGcmSenderId(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLoggingEnabled(int logLevel) {
        EasyGcm.setLoggingLevel(logLevel);
    }

    public GcmListener getGcmListener(Context context) {
        return EasyGcm.getInstance().getGcmListener(context);
    }

}
