package com.liviugheorghe.pcc_client.backend;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.liviugheorghe.pcc_client.App;
import com.pccontroller.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.net.Socket;

import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_URI;

public class FileConnection extends Service {


    private Socket fileSocket;
    private DataOutputStream fileSocketOutputStream;
    private final String TAG = this.getClass().getSimpleName();
    private FileInformation fileInformation;
    private final int BUFFER_SIZE = 1024*40;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void createSocket(String host,int port) throws Exception {
        try {
            Log.d(TAG, String.format("Creating socket, host = %s , port = %s",App.CONNECTED_DEVICE_HOSTNAME,App.FILE_SERVER_PORT));
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
                        notificationBuilder = new NotificationCompat.Builder(FileConnection.this,App.CHANNEL_ID);
                        notificationBuilder.setContentTitle("Uploading file")
                                .setContentText("Upload in progress")
                                .setSmallIcon(R.drawable.ic_file_upload_black_24dp)
                                .setProgress(100,0,false);



                        //Notification notification = createServiceNotification(String.format("Sending file %s to %s",fileInformation.name,App.CONNECTED_DEVICE_HOSTNAME));
                        startForeground(3,notificationBuilder.build());
                        DataInputStream dataInputStream = new DataInputStream(fileInformation.inputStream);
                        int b;
                        int fileSize = 0;
                        try {
                            fileSize = Integer.parseInt(fileInformation.size);
                            Log.d(TAG, "File size is : "+ fileSize);
                        }
                        catch(NumberFormatException e) {
                            e.printStackTrace();
                            notificationBuilder.setProgress(100,0,true);
                        }
                        int numberOfBytes;
                        int totalNumberOfBytes = 0;
                        while((numberOfBytes = dataInputStream.read(buffer,0,BUFFER_SIZE)) != -1) {
                            if(numberOfBytes > 0) {
                                totalNumberOfBytes += numberOfBytes;
                                if(fileSize != 0) {


                                    Log.d(TAG, "Bytes read so far : "+ totalNumberOfBytes);
                                    int progress = (int) (((float)totalNumberOfBytes/fileSize)*100);
                                    Log.d(TAG, "Progress is : " + progress);
                                    notificationBuilder.setProgress(100,progress,false);
                                    notificationManager.notify(3,notificationBuilder.build());
                                }
                                fileSocketOutputStream.write(buffer,0,numberOfBytes);
                            }
                        }
                        dataInputStream.close();
                        Log.d(TAG, "File transfer successful , bytes written : " + totalNumberOfBytes);
                        stopSelf();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(App.CONNECTED_DEVICE_IP_ADDRESS == null) stopSelf();
        try {
            createSocket(App.CONNECTED_DEVICE_IP_ADDRESS,App.FILE_SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }

        Log.d("STUFF","Received uri is "+ intent.getStringExtra(EXTRA_FILE_URI));
        try {
            Uri uri = Uri.parse(intent.getStringExtra(EXTRA_FILE_URI));
            fileInformation = getFileInformation(uri);
            Log.d("STUFF", "Is input stream null ? "+ (fileInformation.inputStream == null));
            onConnectionEstablished();
        }
        catch(Exception e) {
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

        Log.d(TAG, "Service Destroyed");
    }

    private FileInformation getFileInformation(Uri uri) {
        FileInformation fileInformation = new FileInformation();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {
                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileInformation.name = displayName;
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                fileInformation.size = size;
                fileInformation.inputStream = getContentResolver().openInputStream(uri);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return fileInformation;
    }

    private Notification createServiceNotification(String text) {
        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_file_upload_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
    }
}
