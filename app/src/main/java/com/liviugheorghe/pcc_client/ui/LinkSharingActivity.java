package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkSharingActivity extends AppCompatActivity {


    private Button button;
    private TextView textView;
    private Client client;
    private String link="";
    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY)) {
                onResume();
            }
        }
    };
    private ServiceConnection serviceConnection;


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sharing);

        setToolbar();

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY));
        button = findViewById(R.id.link_sharing_button);
        textView = findViewById(R.id.link_sharing_text);


        String extraString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (extraString == null) return;
        Pattern pattern = Pattern.compile("http.+");
        Matcher matcher = pattern.matcher(extraString);
            if (matcher.find()) {
                link = matcher.group(0);
            }
    
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
            Intent serviceIntent = new Intent(LinkSharingActivity.this, Client.class);
            bindService(serviceIntent, serviceConnection, 0);


        button.setOnClickListener(view -> {
            if(App.CONNECTION_ALIVE) {
              try {
                client.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_OPEN_LINK_IN_BROWSER,link);
              } catch(NullPointerException e) {
                  e.printStackTrace();
              }
            }
            else {
                Intent intent = new Intent(LinkSharingActivity.this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (App.CONNECTION_ALIVE) {
            textView.setText(String.format("%s : %s", getResources().getString(R.string.connection_service_notification_text), App.CONNECTED_DEVICE_HOSTNAME));
            button.setText(R.string.send_link_button_text);
        } else {
            textView.setText(R.string.device_not_connected_text);
            button.setText(R.string.connect_to_a_device_button_text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceBroadcastReceiver);
        if (serviceConnection != null)
            unbindService(serviceConnection);
    }
}
