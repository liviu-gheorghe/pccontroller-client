package com.liviugheorghe.pcc_client.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;

import static android.content.Intent.EXTRA_STREAM;

public class MainControlInterfaceActivity extends AppCompatActivity {

    private static final int PICK_FILE = 2;
    private Client client;
    private final String TAG = this.getClass().getSimpleName();
    private ServiceConnection serviceConnection;
    private boolean isInForeground = false;

    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY) && isInForeground) {
                Intent i = new Intent(MainControlInterfaceActivity.this, MainActivity.class);
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

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control_interface);

        setToolbar();

        registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY));
        //String hostname = getIntent().getStringExtra(App.TARGET_HOSTNAME);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_control_interface_appbar_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isInForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isInForeground = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            try {
                client.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_EXECUTE_COMMAND, "vol_up");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                client.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_EXECUTE_COMMAND, "vol_down");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            Uri uri;
            if (data != null) {
                uri = data.getData();
                if (uri == null) {
                    return;
                }
                Intent fileSharingActivityIntent = new Intent(this, FileSharingActivity.class);
                fileSharingActivityIntent.putExtra(EXTRA_STREAM, uri);
                try {
                    startActivity(fileSharingActivityIntent);
                } catch (Exception ignored) {
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


    public void clickOpenTouchPad(View v) {
        startActivity(new Intent(this, TouchpadActivity.class));
    }

    public void clickOpenFile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE);
    }

    public void clickCloseConnectionButton(MenuItem item) {
        try {
            client.closeConnection();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}