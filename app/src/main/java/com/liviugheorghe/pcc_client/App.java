package com.liviugheorghe.pcc_client;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class App extends Application {


    public static final String CHANNEL_ID = "backgroundServiceChannel";
    public static final String EXTRA_TARGET_IP_ADDRESS = "TARGET_IP_ADDRESS";
    public static final String EXTRA_FILE_URI = "EXTRA_FILE_URI";
    public final static int SERVER_PORT = 8560;
    public final static int FILE_SERVER_PORT = 8561;
    public static boolean CONNECTION_ALIVE = false;
    public static String CONNECTED_DEVICE_HOSTNAME = "";
    public static String CONNECTED_DEVICE_IP_ADDRESS = null;

    private static Context context;

    public static Context getAppContext() {
        return App.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel("PC Controller Controller Notification Channel");
        App.context = getApplicationContext();
    }

    @Override
    public Object getSystemService(String name) {
        return super.getSystemService(name);
    }


    private void createNotificationChannel(String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    description,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            try {
                manager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException ignored) {
            }
        }
    }
}
