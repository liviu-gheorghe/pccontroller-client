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

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;
import com.pccontroller.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class LinkSharingActivity extends AppCompatActivity {


    private Button button;
    private TextView textView;
    private Client client;
    private String link="";
    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            
            if (intent.getAction().equals("LEAVE_CONTROL_INTERFACE_ACTIVITY")) {
                onResume();
            }
        }
    };
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sharing);
        registerReceiver(serviceBroadcastReceiver,new IntentFilter("LEAVE_CONTROL_INTERFACE_ACTIVITY"));
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
                Intent intent = new Intent(LinkSharingActivity.this, LauncherActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (App.CONNECTION_ALIVE) {
            textView.setText("Connected device : " + App.CONNECTED_DEVICE_HOSTNAME);
            button.setText("Send");
        } else {
            textView.setText("No device connected");
            button.setText("Connect to a device");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceBroadcastReceiver);
        unbindService(serviceConnection);
    }
}
