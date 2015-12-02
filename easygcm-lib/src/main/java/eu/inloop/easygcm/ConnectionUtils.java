package eu.inloop.easygcm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Tomáš Isteník on 22.9.2015.
 */
public class ConnectionUtils {

    public static boolean isOnline(Context context) {
        if (hasAccessNetworkStatePermission(context)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }
        // Hope for the best if no permission was given to check network status
        return true;
    }

    public static boolean hasAccessNetworkStatePermission(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return (packageManager.checkPermission(
                Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED);
    }
}
