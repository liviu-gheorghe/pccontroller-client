package com.liviugheorghe.pcc_client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;

import java.lang.ref.WeakReference;

public class App extends Application {


    public static final String BACKGROUND_SERVICE_CHANNEL_ID = "backgroundServiceChannel";
    public static final String FILE_SHARING_SERVICE_CHANNEL_ID = "fileSharingServiceChannel";
    public static final String EXTRA_TARGET_IP_ADDRESS = "TARGET_IP_ADDRESS";
    public static final String EXTRA_FILE_NAME = "EXTRA_FILE_NAME";
    public static final String EXTRA_FILE_SIZE = "EXTRA_FILE_SIZE";
    public static final String EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE";
    public static final String EXTRA_TARGET_HOSTNAME = "TARGET_HOSTNAME";
    public static final String BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY = "LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY";
    public static final String BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY = "LEAVE_WAIT_FOR_PERMISSION_ACTIVITY";
    public final static int SERVER_PORT = 8560;
    public final static int FILE_SERVER_PORT = 8561;
    public static boolean CONNECTION_ALIVE = false;
    public static String CONNECTED_DEVICE_HOSTNAME = "";
    public static String CONNECTED_DEVICE_IP_ADDRESS = null;
    private static WeakReference<Context> context;

    public static Context getAppContext() {
        return context.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        createNotificationChannel(BACKGROUND_SERVICE_CHANNEL_ID, "Pc Controller Background Service Notification Channel");
        createNotificationChannel(
                FILE_SHARING_SERVICE_CHANNEL_ID,
                "PC Controller File Sharing Service Notification Channel",
                NotificationManagerCompat.IMPORTANCE_HIGH
        );
        context = new WeakReference<>(this);
    }
    
    @Override
    public Object getSystemService(String name) {
        return super.getSystemService(name);
    }
    
    
    private void createNotificationChannel(String channelID, String description, int... args) {
        int importance = (args.length > 0) ? args[0] : NotificationManagerCompat.IMPORTANCE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(
                    channelID,
                    description,
                    importance
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            try {
                manager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
