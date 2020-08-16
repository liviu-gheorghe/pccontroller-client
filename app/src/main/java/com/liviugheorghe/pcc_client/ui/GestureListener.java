package com.liviugheorghe.pcc_client.ui;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;

class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final int LEFT_CLICK = 1;
    public static final int MIDDLE_CLICK = 2;
    public static final int RIGHT_CLICK = 3;
    public static final int DOUBLE_CLICK = 4;
    private App.TouchpadParams tParams;

    private Client clientService;
    private float currentMoveX = -1;
    private float currentMoveY = -1;

    public GestureListener(Client clientService) {
        this.clientService = clientService;
        tParams = App.getTouchpadParams();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return super.onDown(e);
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if (velocityX > 10000)
            try {
                clientService.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_EXECUTE_COMMAND, "switch_tabs");
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        return false;
    }


    @Override
    public boolean onDoubleTap(MotionEvent e) {
        try {
            clientService.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_SEND_MOUSE_CLICK, String.valueOf(DOUBLE_CLICK));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        try {
            clientService.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_SEND_MOUSE_CLICK, String.valueOf(LEFT_CLICK));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        try {
            clientService.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_SEND_MOUSE_CLICK, String.valueOf(RIGHT_CLICK));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e1.getPointerCount() > 2) return false;
        DeltaInfo delta = null;
        if (currentMoveX == -1) {
            currentMoveX = e1.getX();
        }
        if (currentMoveY == -1) {
            currentMoveY = e1.getY();
        }
        float x = e2.getX();
        float y = e2.getY();
        float accumulatedDistanceToMoveX = -distanceX;
        float minimumDistanceToMoveX = 2;
        if (Math.abs(accumulatedDistanceToMoveX) > minimumDistanceToMoveX) {
            currentMoveX = x;
            delta = new DeltaInfo((int) accumulatedDistanceToMoveX, 0);
        }
        float accumulatedDistanceToMoveY = -distanceY;
        float minimumDistanceToMoveY = 2;
        if (Math.abs(accumulatedDistanceToMoveY) > minimumDistanceToMoveY) {
            currentMoveY = y;
            if (delta == null) delta = new DeltaInfo(0, (int) accumulatedDistanceToMoveY);
            else delta.y = (int) accumulatedDistanceToMoveY;
        }
        if (delta != null) {
            try {
                if (e2.getPointerCount() == 2)
                    clientService.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_SEND_MOUSE_SCROLL, String.valueOf(-delta.y / 5));
                else
                    clientService.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_SEND_MOUSE_MOVE, delta.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private class DeltaInfo {
        private int x;
        private int y;

        DeltaInfo(int x, int y) {
            this.x = x*((400-tParams.getSensitivity())/100);
            this.y = y*((400-tParams.getSensitivity())/100);
        }

        @NonNull
        public String toString() {
            return String.format("%d,%d", this.x, this.y);
        }
    }
}