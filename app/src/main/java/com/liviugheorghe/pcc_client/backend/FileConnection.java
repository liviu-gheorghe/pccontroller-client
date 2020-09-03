package com.liviugheorghe.pcc_client.backend;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.util.FileInformation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Locale;
import java.util.UUID;

import static android.content.Intent.EXTRA_STREAM;

public class FileConnection extends Service {
	
	
	private final String TAG = this.getClass().getSimpleName();
	private final int BUFFER_SIZE = 1024 * 40;
	private Socket fileSocket;
	private DataOutputStream fileSocketOutputStream;
	private FileInformation fileInformation;
	private NotificationManager notificationManager;
	private NotificationCompat.Builder notificationBuilder;
	private static final int NOTIFICATION_ID = 4;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	private void createSocket(String host, int port) throws Exception {
		try {
			fileSocket = new Socket(host, port);
			fileSocketOutputStream = getSocketOutputStream(fileSocket);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private DataOutputStream getSocketOutputStream(Socket socket) throws Exception {
		return new DataOutputStream(socket.getOutputStream());
	}
	
	private void onConnectionEstablished() {
		
		new Thread(
				() -> {
					try {
						byte[] buffer = new byte[BUFFER_SIZE];

						notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						notificationBuilder = new NotificationCompat.Builder(FileConnection.this, App.FILE_SHARING_SERVICE_CHANNEL_ID);
						notificationBuilder.setContentTitle("Uploading file")
								.setContentText("Upload in progress")
								.setSmallIcon(R.drawable.ic_file_upload_black_24dp)
								.setProgress(100, 0, false);

						startForeground(NOTIFICATION_ID, notificationBuilder.build());
						DataInputStream dataInputStream = new DataInputStream(getContentResolver().openInputStream(fileInformation.getUri()));
						int fileSize = 0;
						String fileName = fileInformation.getName();
						try {
							fileSize = Integer.parseInt(fileInformation.getSize());
						} catch (NumberFormatException e) {
							e.printStackTrace();
							notificationBuilder.setProgress(100, 0, true);
						}

						if (fileName == null)
							fileName = UUID.randomUUID().toString() + "." + fileInformation.getType();
						int filenameSize = fileName.length();
						fileSocketOutputStream.writeInt(filenameSize);
						fileSocketOutputStream.writeBytes(fileName);
						fileSocketOutputStream.flush();
						int numberOfBytes;
						int totalNumberOfBytes = 0;
						while ((numberOfBytes = dataInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
							if (numberOfBytes > 0) {
								totalNumberOfBytes += numberOfBytes;
								if (fileSize != 0) {
									int progress = (int) (((float) totalNumberOfBytes / fileSize) * 100);
									Log.d(TAG, String.format("Progress is %d",progress));
									notificationBuilder.setProgress(100, progress, false)
											.setContentText(String.format(Locale.getDefault(), "Upload in progress , %d%% loaded", progress));
									notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
								}
								fileSocketOutputStream.write(buffer, 0, numberOfBytes);
							}
						}
						dataInputStream.close();
						stopSelf();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		).start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (App.CONNECTED_DEVICE_IP_ADDRESS == null) stopSelf();
		try {
			createSocket(App.CONNECTED_DEVICE_IP_ADDRESS, App.FILE_SERVER_PORT);
		} catch (Exception e) {
			e.printStackTrace();
			stopSelf();
		}
		
		try {
			fileInformation = new FileInformation(
					Uri.parse(intent.getStringExtra(EXTRA_STREAM)),
					intent.getStringExtra(App.EXTRA_FILE_NAME),
					intent.getStringExtra(App.EXTRA_FILE_SIZE),
					intent.getStringExtra(App.EXTRA_FILE_TYPE)
			);
			onConnectionEstablished();
		} catch (Exception e) {
			e.printStackTrace();
			stopSelf();
		}
		return START_NOT_STICKY;
	}
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			fileSocketOutputStream.close();
			fileSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
