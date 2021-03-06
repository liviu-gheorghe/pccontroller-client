package com.liviugheorghe.pcc_client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.liviugheorghe.pcc_client.R;

public class LauncherActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_DURATION = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        new Handler().postDelayed(
                () -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }, SPLASH_SCREEN_DURATION
        );
    }
}
