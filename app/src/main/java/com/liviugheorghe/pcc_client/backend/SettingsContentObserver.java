package com.liviugheorghe.pcc_client.backend;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.liviugheorghe.pcc_client.App;

public class SettingsContentObserver extends ContentObserver {


    Context context;
    AudioManager audioManager;
    BatteryManager batteryManager;

    public SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        this.context = c;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d("STUFF", "System settings changed");
        int music_stream_volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        boolean headphones_plugged = areHeadphonesConnected();
        /**
         ActionDispatcher.dispatchAction(
         App.ACTION_SHARE_MUSIC_STREAM_LEVEL,
         String.format("%s : %d %s","Volume",music_stream_volume,((headphones_plugged==true)?"(headphones plugged in)":""))
         );
         **/
    }


    private boolean areHeadphonesConnected() {
        AudioManager audioManager = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (int i = 0; i < audioDeviceInfos.length; i++) {
                if (audioDeviceInfos[i].getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES)
                    return true;
                if (audioDeviceInfos[i].getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET)
                    return true;
            }
            return false;
        } else return audioManager.isWiredHeadsetOn();
    }
}
