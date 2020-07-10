package io.kavenegar.android.sample.standalone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;


public class SettingsService {

    public static void authenticate(String accessToken, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AccessToken", accessToken);
        editor.apply();
    }

    public static boolean isAuthenticated(Context context) {
        return getAccessToken(context) != null;
    }

    public static String getAccessToken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("AccessToken", null);
    }

    public static void setSelectedEndpoint(Context context, Integer endpointId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("SelectedEndpoint", endpointId);
        editor.apply();
    }

    public static Integer getSelectedEndpoint(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("SelectedEndpoint", 0);
    }

    public static void signOut(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("AccessToken");
        editor.apply();
        MyApplication.setUser(null);
    }

    public static void setNotificationToken(Context context, String notificationToken) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("NotificationToken", notificationToken);
        editor.apply();
    }

    public static String getNotificationToken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("NotificationToken", null);
    }

    public static LocalDateTime getLastCallsSyncedAt(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String syncedAt = preferences.getString("CallsLastSyncedAt", null);
        if (syncedAt == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(syncedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static void saveLastCallsSyncedAt(Context context, LocalDateTime syncedAt) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CallsLastSyncedAt", syncedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        editor.apply();
    }

    public static void setLogsNeedToSync(Context context, Boolean isActive) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("LogsNeedToSync", isActive);
        editor.apply();
    }

    public static boolean getLogsNeedToSync(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean callIsActive = preferences.getBoolean("LogsNeedToSync", false);
        return callIsActive;
    }

}
