package com.liviugheorghe.pcc_client.util;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.liviugheorghe.pcc_client.App;


public class SystemInformation {

    private static AudioManager audioManager = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
    private static BatteryManager batteryManager = (BatteryManager) App.getAppContext().getSystemService(Context.BATTERY_SERVICE);

    public static boolean areHeadphonesConnected() {
        if (audioManager == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo audioDeviceInfo : audioDeviceInfos) {
                if (audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES)
                    return true;
                if (audioDeviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET)
                    return true;
            }
            return false;
        } else return audioManager.isWiredHeadsetOn();
    }

    public static int getMusicStreamVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isCharging() {
        return batteryManager.isCharging();
    }
}
