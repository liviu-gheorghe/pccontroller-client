package com.liviugheorghe.pcc_client.ui;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private String DEBUG_TAG = this.getClass().getSimpleName();


    private float prevX;
    private float prevY;
    private float currentX;
    private float currentY;
    private float accumulatedDistanceToMoveX;
    private float accumulatedDistanceToMoveY;
    private float minimumDistanceToMoveX;
    private float minimumDistanceToMoveY;


    @Override
    public boolean onDown(MotionEvent e) {
        return super.onDown(e);
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        return super.onFling(event1,event2,velocityX,velocityY);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG, String.format("Double tap with %d fingers !!!",e.getPointerCount()));
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(DEBUG_TAG, String.format("Single tap with %d fingers !!!",e.getPointerCount()));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(DEBUG_TAG, String.format("Long press with with %d fingers !!!",e.getPointerCount()));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Log.d(DEBUG_TAG, String.format("Scroll , distX = %f , distY = %f",distanceX,distanceY));
        //if(e2.getPointerCount() >= 2) return false;

        return false;

    }
}