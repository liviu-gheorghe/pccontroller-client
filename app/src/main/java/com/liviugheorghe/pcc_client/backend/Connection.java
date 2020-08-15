package com.liviugheorghe.pcc_client.backend;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.ui.MainControlInterfaceActivity;
import com.liviugheorghe.pcc_client.ui.WaitForPermissionActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Connection extends Service {

    private String targetIpAddress;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Connection.ActionHandler actionHandler;
    private Connection.ActionDispatcher actionDispatcher;
    private final int SOCKET_TIMEOUT = 1000;

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
        if (targetIpAddress == null) stopSelf();
        boolean isDeviceReachable = true;
        try {
            createSocket(targetIpAddress, App.SERVER_PORT);
        } catch (IOException e) {
            isDeviceReachable = false;
            e.printStackTrace();
            Toast.makeText(this, R.string.device_not_reachable_toast_text, Toast.LENGTH_LONG).show();
            stopSelf();
        }

        if (isDeviceReachable) {
            Intent i = new Intent(this, WaitForPermissionActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            actionHandler = new ActionHandler(inputStream);
            actionDispatcher = new ActionDispatcher(outputStream);
            sendConnectionRequest();
        }
        return START_NOT_STICKY;
    }

    private void sendConnectionRequest() {
        actionDispatcher.dispatchAction(DispatchedActionsCodes.ACTION_SEND_CONNECTION_REQUEST,getDeviceFullName());
    }

    private String getDeviceFullName() {
        return Build.BRAND + " " + Build.MODEL;
    }

    private void onConnectionAccepted() {
        Notification notification = createServiceNotification(
                String.format("%s %s", getResources().getString(R.string.connection_service_notification_text), App.CONNECTED_DEVICE_HOSTNAME),
                App.BACKGROUND_SERVICE_CHANNEL_ID
        );
        startForeground(2, notification);
        App.CONNECTION_ALIVE = true;
        App.CONNECTED_DEVICE_IP_ADDRESS = targetIpAddress;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY));
    }

    private void createSocket(String host,int port) throws IOException {

            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT);
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
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY));
        Intent i = new Intent(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY);
        i.putExtra("WHERE","MAIN_ACTIVITY");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
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
                Intent intent = new Intent(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY);
                intent.putExtra("WHERE","MAIN_ACTIVITY");
                LocalBroadcastManager.getInstance(Connection.this).sendBroadcast(intent);
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
        Intent notificationIntent = new Intent(this, MainControlInterfaceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new NotificationCompat.Builder(this, channelID)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification)
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
