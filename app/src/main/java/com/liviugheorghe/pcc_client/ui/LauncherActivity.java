package com.liviugheorghe.pcc_client.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.liviugheorghe.pcc_client.App;
import com.pccontroller.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Intent intent = new Intent(this, App.CONNECTION_ALIVE ? MainControlInterfaceActivity.class : MainActivity.class);
        startActivity(intent);
        finish();
    }
}
