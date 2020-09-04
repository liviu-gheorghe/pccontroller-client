package com.liviugheorghe.pcc_client.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liviugheorghe.pcc_client.R;

public class MainActivityConnectedFragment extends Fragment {


    private Drawable linuxDrawable;
    private Drawable windowsDrawable;
    private Drawable macDrawable;
    private Drawable genericDrawable;
    private ImageView osImageView;
    private TextView targetHostnameTextView;
    private String hostname;
    private String osType;

    public MainActivityConnectedFragment() {
    }

    private void loadDrawables() {
        windowsDrawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_windows);
        macDrawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_mac);
        linuxDrawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_linux);
        genericDrawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_laptop_100dp);
    }

    private void setRequiredDrawable() {
        if(osType == null) {
            osImageView.setImageDrawable(genericDrawable);
            return;
        }
        Log.d("MainActivity", "osType is "+osType);
        switch (osType) {
            case "linux":
                osImageView.setImageDrawable(linuxDrawable);
                break;
            case "windows":
                osImageView.setImageDrawable(windowsDrawable);
                break;
            case "mac":
                osImageView.setImageDrawable(macDrawable);
                break;
            default:
                osImageView.setImageDrawable(genericDrawable);
        }
    }

    public MainActivityConnectedFragment(String hostname,String osType) {
        this.hostname = hostname;
        this.osType = osType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDrawables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_activity_connected, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        osImageView = view.findViewById(R.id.connected_device_os_logo);
        targetHostnameTextView = view.findViewById(R.id.connected_device_hostname);
        setRequiredDrawable();
        targetHostnameTextView.setText(hostname);
    }
}