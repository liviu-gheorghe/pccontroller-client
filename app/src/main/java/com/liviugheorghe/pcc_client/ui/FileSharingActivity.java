package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.FileConnection;
import com.pccontroller.R;

import androidx.appcompat.app.AppCompatActivity;

import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_URI;

public class FileSharingActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView textView;
    private Button button;
    private Uri uri;

    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("LEAVE_CONTROL_INTERFACE_ACTIVITY")) {
                onResume();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sharing);
        registerReceiver(serviceBroadcastReceiver,new IntentFilter("LEAVE_CONTROL_INTERFACE_ACTIVITY"));

        textView = findViewById(R.id.file_sharing_text);
        button  = findViewById(R.id.file_sharing_button);

        Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        if(uri == null) {
            Toast.makeText(this,"Invalid file",Toast.LENGTH_SHORT).show();
            button.setOnClickListener((view)->{});
        }
        else
        button.setOnClickListener(view -> {
            if(App.CONNECTION_ALIVE) {
                Intent serviceIntent = new Intent(this, FileConnection.class);
                serviceIntent.putExtra(EXTRA_FILE_URI,uri.toString());
                try {
                    startService(serviceIntent);
                }
                catch(Exception e) {
                }
            }
            else {
                Intent intent = new Intent(FileSharingActivity.this, LauncherActivity.class);
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
    }
}
