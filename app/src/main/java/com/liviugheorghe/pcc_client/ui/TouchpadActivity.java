package com.liviugheorghe.pcc_client.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.pccontroller.R;


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




/**
 @Override
 public boolean onTouchEvent(MotionEvent event) {


 float X = event.getX();
 float Y = event.getY();


 int actionType = event.getActionMasked();


 Log.d(TAG, "onTouchEvent: Pointer count : "+ event.getPointerCount());
 Log.d(TAG, String.format("Pointer position : (%.2f %.2f)",X,Y));
 return true;
 }
 */
