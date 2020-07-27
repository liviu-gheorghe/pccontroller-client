package com.liviugheorghe.pcc_client.backend;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.Locale;
import java.util.UUID;

import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_URI;

public class FileConnection extends Service {
	
	
	private final String TAG = this.getClass().getSimpleName();
	private final int BUFFER_SIZE = 1024 * 40;
	MimeTypeMap mime = MimeTypeMap.getSingleton();
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
						DataInputStream dataInputStream = new DataInputStream(fileInformation.inputStream);
						int fileSize = 0;
						String fileName = fileInformation.name;
						try {
							fileSize = Integer.parseInt(fileInformation.size);
						} catch (NumberFormatException e) {
							e.printStackTrace();
							notificationBuilder.setProgress(100, 0, true);
						}
						
						if (fileName == null)
							fileName = UUID.randomUUID().toString() + "." + fileInformation.type;
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
									notificationBuilder.setProgress(100, progress, false)
											.setContentText(String.format(Locale.getDefault(), "Upload in progress , %d %% loaded", progress));
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
			Uri uri = Uri.parse(intent.getStringExtra(EXTRA_FILE_URI));
			fileInformation = getFileInformation(uri);
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
	
	private FileInformation getFileInformation(Uri uri) {
		FileInformation fileInformation = new FileInformation();
		Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				fileInformation.name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
				String size;
				if (!cursor.isNull(sizeIndex))
					size = cursor.getString(sizeIndex);
				else
					size = "Unknown";
				
				fileInformation.size = size;
				fileInformation.inputStream = getContentResolver().openInputStream(uri);
			}
			String mimeType = getContentResolver().getType(uri);
			if (mimeType != null)
				fileInformation.type = mime.getExtensionFromMimeType(mimeType);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return fileInformation;
	}
}
