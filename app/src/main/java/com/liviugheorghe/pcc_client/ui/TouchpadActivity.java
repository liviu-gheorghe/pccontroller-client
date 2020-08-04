package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;


public class TouchpadActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private GestureDetectorCompat gestureDetector;
    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY)) {
                Intent i = new Intent(TouchpadActivity.this, LauncherActivity.class);
                startActivity(i);
                finish();
            }
        }
    };
    StringBuffer stringBuffer = new StringBuffer();
    private Client client;
    private ServiceConnection serviceConnection;
    private EditText textBox;
    private InputMethodManager imm;

    private void addTextBoxInputListener() {
        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int diff = s.length() - stringBuffer.length();
                if (diff < 0) {
                    try {
                        stringBuffer.delete(stringBuffer.length() + diff, stringBuffer.length());
                        sendChars("a");
                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else if (diff > 0) {
                    for (int i = s.length() - diff; i < s.length(); i++) {
                        stringBuffer.append(s.charAt(i));
                    }
                    String str = "c" + stringBuffer.substring(s.length() - diff);
                    sendChars(str);
                }
            }
        });
    }

    private void sendChars(String chars) {
        try {
            client.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_SEND_KEYBOARD_INPUT, chars);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchpad);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textBox = findViewById(R.id.text_box);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        addTextBoxInputListener();
        registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY));
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Client.ClientBinder binder = (Client.ClientBinder) service;
                client = binder.getClient();
                gestureDetector = new GestureDetectorCompat(TouchpadActivity.this, new GestureListener(client));
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
        getMenuInflater().inflate(R.menu.activity_touchpad_appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(serviceBroadcastReceiver);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void keyboardActionClick(MenuItem item) {
        textBox.requestFocus();
        imm.showSoftInput(textBox, InputMethodManager.SHOW_IMPLICIT);
    }
}