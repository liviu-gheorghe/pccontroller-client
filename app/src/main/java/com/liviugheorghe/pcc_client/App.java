package com.liviugheorghe.pcc_client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class App extends Application {
    
    public static final String BACKGROUND_SERVICE_CHANNEL_ID = "backgroundServiceChannel";
    public static final String FILE_SHARING_SERVICE_CHANNEL_ID = "fileSharingServiceChannel";
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
        createNotificationChannel(BACKGROUND_SERVICE_CHANNEL_ID, "Pc Controller Background Service Notification Channel");
        createNotificationChannel(
                FILE_SHARING_SERVICE_CHANNEL_ID,
                "PC Controller File Sharing Service Notification Channel",
				NotificationManagerCompat.IMPORTANCE_MAX
        );
        App.context = getApplicationContext();
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
			NotificationManagerCompat manager = getSystemService(NotificationManagerCompat.class);
            try {
                manager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException ignored) {
            }
        }
    }
}
