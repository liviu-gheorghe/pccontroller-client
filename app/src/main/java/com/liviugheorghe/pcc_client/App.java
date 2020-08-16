package com.liviugheorghe.pcc_client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import java.lang.ref.WeakReference;

public class App extends Application {

    public static final String SHARED_PREF_UI_MODE = "ui_mode";
    public static final String SHARED_PREF_TOUCHPAD_SENSITIVITY = "touchpad_sensitivity";
    public static final String BACKGROUND_SERVICE_CHANNEL_ID = "backgroundServiceChannel";
    public static final String FILE_SHARING_SERVICE_CHANNEL_ID = "fileSharingServiceChannel";
    public static final String EXTRA_TARGET_IP_ADDRESS = "TARGET_IP_ADDRESS";
    public static final String EXTRA_FILE_NAME = "EXTRA_FILE_NAME";
    public static final String EXTRA_FILE_SIZE = "EXTRA_FILE_SIZE";
    public static final String EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE";
    public static final String EXTRA_TARGET_HOSTNAME = "TARGET_HOSTNAME";
    public static final String BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY = "com.liviugheorghe.pcc_client.LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY";
    public static final String BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY = "com.liviugheorghe.pcc_client.LEAVE_WAIT_FOR_PERMISSION_ACTIVITY";
    public final static int SERVER_PORT = 8560;
    public final static int FILE_SERVER_PORT = 8561;
    public static boolean CONNECTION_ALIVE = false;
    public static String CONNECTED_DEVICE_HOSTNAME = "";
    public static String CONNECTED_DEVICE_OS = "";
    public static String CONNECTED_DEVICE_IP_ADDRESS = null;
    private static WeakReference<Context> context;
    private static TouchpadParams touchpadParams;

    public static Context getAppContext() {
        return context.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String mode = preferences.getString(SHARED_PREF_UI_MODE,"MODE_NIGHT_YES");
        modifyUITheme(mode);
        createNotificationChannel(BACKGROUND_SERVICE_CHANNEL_ID, "Pc Controller Background Service Notification Channel");
        createNotificationChannel(
                FILE_SHARING_SERVICE_CHANNEL_ID,
                "PC Controller File Sharing Service Notification Channel",
                NotificationManagerCompat.IMPORTANCE_HIGH
        );
        context = new WeakReference<>(this);
        int sensitivity = Integer.parseInt(preferences.getString(SHARED_PREF_TOUCHPAD_SENSITIVITY,String.valueOf(TouchpadParams.HIGH_SENSITIVITY)));
        touchpadParams = new TouchpadParams(sensitivity);
    }

    private static int getModeFromString(String s) {
        if(s.equals("MODE_NIGHT_NO")) return AppCompatDelegate.MODE_NIGHT_NO;
        if(s.equals("MODE_NIGHT_AUTO_BATTERY")) return AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
        if(s.equals("MODE_NIGHT_FOLLOW_SYSTEM")) return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        return AppCompatDelegate.MODE_NIGHT_YES;
    }
    
    @Override
    public Object getSystemService(String name) {
        return super.getSystemService(name);
    }

    public static void modifyUITheme(String s) {
        int mode = getModeFromString(s);
        AppCompatDelegate.setDefaultNightMode(mode);
    }
    
    
    private void createNotificationChannel(String channelID, String description, int... args) {
        int importance = (args.length > 0) ? args[0] : NotificationManagerCompat.IMPORTANCE_LOW;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(
                    channelID,
                    description,
                    importance
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager != null)
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public static TouchpadParams getTouchpadParams() {
        return touchpadParams;
    }

    public static final class TouchpadParams {
        private int sensitivity = MEDIUM_SENSITIVITY;
        public static int LOW_SENSITIVITY = 100;
        public static int MEDIUM_SENSITIVITY = 200;
        public static int HIGH_SENSITIVITY = 300;

        public TouchpadParams(int sensitivity) {
            this.sensitivity = sensitivity;
        }

        public TouchpadParams() {}

        public int getSensitivity() {
            return sensitivity;
        }
        public void setSensitivity(int sensitivity) {
            this.sensitivity = sensitivity;
        }
    }
}
