package com.liviugheorghe.pcc_client.ui;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.liviugheorghe.pcc_client.R;


public class TouchpadActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchpad);
        gestureDetector = new GestureDetectorCompat(this,new GestureListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}