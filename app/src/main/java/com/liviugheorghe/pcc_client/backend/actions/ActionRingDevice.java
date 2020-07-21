package com.liviugheorghe.pcc_client.backend.actions;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.Action;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes;
import com.liviugheorghe.pcc_client.ui.LauncherActivity;
import com.pccontroller.R;

public class ActionRingDevice implements Action {

    private String content;

    public ActionRingDevice(String content) {
        this.content = content;
    }

    @Override
    public void execute() {
        new Thread(
                () -> {
                    if (content.contains("v")) {
                        vibrateDevice(500);
                    }
                    if (content.contains("b")) {
                        ringDevice();
                    }
                }
        ).start();
    }


    private void ringDevice() {
        MediaPlayer mp = MediaPlayer.create(App.getAppContext(), R.raw.beep_beep);
        mp.setVolume(1, 1);
        mp.start();
    }

    private void vibrateDevice(int milliseconds) {
        Vibrator v = (Vibrator) App.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(milliseconds);
        }
    }


    @Override
    public int getActionType() {
        return ReceivedActionsCodes.RING_DEVICE;
    }
}
