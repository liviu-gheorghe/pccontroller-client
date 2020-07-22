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

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.ui.LauncherActivity;
import com.pccontroller.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Client extends Service {
	
	private Connection connection;
	private ClientBinder clientBinder = new ClientBinder();
	
	public Connection getConnection() throws NullPointerException {
		if (connection == null) throw new NullPointerException();
		return connection;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return clientBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String targetIpAddress = intent.getStringExtra(App.EXTRA_TARGET_IP_ADDRESS);
		Intent serviceIntent = new Intent(this, Connection.class);
		serviceIntent.putExtra(App.EXTRA_TARGET_IP_ADDRESS, targetIpAddress);
		startService(serviceIntent);
		
		ServiceConnection serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Connection.ConnectionBinder binder = (Connection.ConnectionBinder) service;
				try {
					connection = binder.getConnection();
				} catch (Exception e) {
					e.printStackTrace();
					stopSelf();
				}
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
		bindService(serviceIntent, serviceConnection, 0);
		
		Notification notification = createServiceNotification("PC Controller is active", App.BACKGROUND_SERVICE_CHANNEL_ID);
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
	
	private Notification createServiceNotification(String text, String channelID) {
		Intent notificationIntent = new Intent(this, LauncherActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		return new NotificationCompat.Builder(this, channelID)
				.setContentText(text)
				.setSmallIcon(R.drawable.ic_android_black_24dp)
				.setContentIntent(pendingIntent)
				.build();
	}
	
	public class ClientBinder extends Binder {
		public Client getClient() {
			return Client.this;
		}
	}
}
