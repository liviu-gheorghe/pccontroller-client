package com.liviugheorghe.pcc_client.backend;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.ui.LauncherActivity;
import com.pccontroller.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class Connection extends Service {

    private String targetIpAddress;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Connection.ActionHandler actionHandler;
    private Connection.ActionDispatcher actionDispatcher;

    public class ConnectionBinder extends Binder {
        public Connection getConnection() {
            return Connection.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ConnectionBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        targetIpAddress = intent.getStringExtra(App.EXTRA_TARGET_IP_ADDRESS);
        if(targetIpAddress == null) stopSelf();
        try {
            createSocket(targetIpAddress,App.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"The device is not reachable",Toast.LENGTH_LONG).show();
            stopSelf();
        }
        actionHandler = new ActionHandler(inputStream);
        actionDispatcher = new ActionDispatcher(outputStream);
        sendConnectionRequest();
        return START_NOT_STICKY;
    }

    private void sendConnectionRequest() {
        actionDispatcher.dispatchAction(DispatchedActionsCodes.ACTION_SEND_CONNECTION_REQUEST,getDeviceFullName());
    }

    private String getDeviceFullName() {
        return Build.BRAND + " " + Build.MODEL;
    }

    private void onConnectionAccepted() {
        Notification notification = createServiceNotification("Connected to " + targetIpAddress, App.BACKGROUND_SERVICE_CHANNEL_ID);
        startForeground(2,notification);
        App.CONNECTION_ALIVE = true;
        App.CONNECTED_DEVICE_IP_ADDRESS = targetIpAddress;
        sendBroadcast(new Intent("LEAVE_WAIT_FOR_PERMISSION_ACTIVITY"));
    }

    private void createSocket(String host,int port) throws IOException {

            try {
                socket = new Socket(host, port);
                inputStream = getSocketInputStream(socket);
                outputStream = getSocketOutputStream(socket);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
    }

    private DataInputStream getSocketInputStream(Socket socket) throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    private DataOutputStream getSocketOutputStream(Socket socket) throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("LEAVE_CONTROL_INTERFACE_ACTIVITY"));
        sendBroadcast(new Intent("LEAVE_WAIT_FOR_PERMISSION_ACTIVITY"));
        App.CONNECTION_ALIVE = false;
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void dispatchAction(int type, String content) {
        actionDispatcher.dispatchAction(type, content);
    }

    private class ActionHandler {

        ActionHandler(DataInputStream inputStream) {
            new Thread(() -> listenForInput(inputStream)).start();
        }

        private void listenForInput(DataInputStream dIn) {
            try {
                while(true) {
                    byte type = dIn.readByte();
                    String content = dIn.readUTF();
                    actionHandler.handleAction(type, content);
                }
            } catch (Exception e) {
                e.printStackTrace();
                stopSelf();
            }
        }

        private void handleAction(int type, String content) {
            if(type == ReceivedActionsCodes.RECEIVE_CONNECTION_PERMISSION) {
                onConnectionAccepted();
            }
            else if(type == ReceivedActionsCodes.RECEIVE_CONNECTION_REJECTION)
            {
                sendBroadcast(new Intent("LEAVE_WAIT_FOR_PERMISSION_ACTIVITY"));
            }
            else {
                Action action = ActionFactory.createAction(type, content);
                if (action == null) return;
                action.execute();
            }
        }
    }

    private class ActionDispatcher {

        private DataOutputStream outputStream;

        private ActionDispatcher(DataOutputStream outputStream) {
            this.outputStream = outputStream;
        }
        private void dispatchAction(int type, String content) {
            try {
                outputStream.writeByte(type);
                outputStream.writeUTF(content);
                outputStream.flush();
            } catch (Exception e) {
                stopSelf();
            }
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
    
    public static class DeviceNotReachableException extends Exception {
        
        DeviceNotReachableException(String message) {
            super(message);
        }
        
        DeviceNotReachableException() {
            super("The device is not reachable");
        }
    }
}
