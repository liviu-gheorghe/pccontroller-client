package com.liviugheorghe.pcc_client.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;
import com.liviugheorghe.pcc_client.backend.FileConnection;
import com.pccontroller.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_URI;

public class MainControlInterfaceActivity extends AppCompatActivity {

    private static final int PICK_FILE = 2;
    private Client client;
    private final String TAG = this.getClass().getSimpleName();
    private ServiceConnection serviceConnection;

    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("LEAVE_CONTROL_INTERFACE_ACTIVITY")) {
                Intent i = new Intent(MainControlInterfaceActivity.this, LauncherActivity.class);
                startActivity(i);
                finish();
            }
        }
    };


    private String getViewId(View view) {
        String rawId = getResources().getResourceName(view.getId());
        try {
            return rawId.split("/")[1];
        } catch (Error e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control_interface);
        registerReceiver(serviceBroadcastReceiver, new IntentFilter("LEAVE_CONTROL_INTERFACE_ACTIVITY"));
        TextView hostnameTextView = findViewById(R.id.hostname);
        hostnameTextView.setText(App.CONNECTED_DEVICE_HOSTNAME);
    
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Client.ClientBinder binder = (Client.ClientBinder) service;
                client = binder.getClient();
            }
        
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            
            }
        };
        Intent serviceIntent = new Intent(this, Client.class);
        bindService(serviceIntent, serviceConnection, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceBroadcastReceiver);
        unbindService(serviceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                if(uri == null) {
                    Log.d(TAG, "onActivityResult: URI IS NULL");
                    return;
                }
                Intent serviceIntent = new Intent(this, FileConnection.class);
                serviceIntent.putExtra(EXTRA_FILE_URI,uri.toString());
                try {
                    startService(serviceIntent);
                }
                catch(Exception e) {
                    Log.e(TAG, "Error starting the service");
                }
            }
        }
    }

    public void clickActionView(View v) {
        try {
            client.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_EXECUTE_COMMAND, getViewId(v));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickCloseConnectionButton(View v) {
        client.closeConnection();
    }

    public void clickOpenTouchPad(View v) {
        startActivity(new Intent(this, TouchpadActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void clickOpenFile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE);
    }
}