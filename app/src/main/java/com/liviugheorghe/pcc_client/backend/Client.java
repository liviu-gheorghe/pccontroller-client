package com.liviugheorghe.pcc_client.backend;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.ui.LauncherActivity;
import com.pccontroller.R;

public class Client extends Service {

    private Connection connection;
    private String targetIpAddress;
    private ClientBinder clientBinder = new ClientBinder();

    public Connection getConnection() throws NullPointerException{
        if(connection == null) throw new NullPointerException();
        return connection;
    }


    public class ClientBinder extends Binder {
        public Client getClient() {
            return Client.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("STUFF", "Service client has been created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("STUFF", "Service client has been destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("STUFF", "Client has been bound again");
        return clientBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        targetIpAddress = intent.getStringExtra(App.EXTRA_TARGET_IP_ADDRESS);
                    Intent serviceIntent = new Intent(this,Connection.class);
                    serviceIntent.putExtra(App.EXTRA_TARGET_IP_ADDRESS,targetIpAddress);
                    startService(serviceIntent);

                    ServiceConnection serviceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            Connection.ConnectionBinder binder = (Connection.ConnectionBinder) service;
                            try {
                                connection = binder.getConnection();
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                                stopSelf();
                            }
                        }
                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                        }
                    };
                    bindService(serviceIntent,serviceConnection,0);

                        Notification notification = createServiceNotification("PC Controller is active");
                        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    public void closeConnection() {
        try {
            connection.stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Notification createServiceNotification(String text) {
        Intent notificationIntent = new Intent(this, LauncherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
    }
}
