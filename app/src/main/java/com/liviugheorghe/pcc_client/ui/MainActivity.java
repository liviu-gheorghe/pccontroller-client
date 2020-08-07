package com.liviugheorghe.pcc_client.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.util.IpAddressValidator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int GET_QR_CODE_INFORMATION = 3;
    DrawerLayout drawer;
    private Client client;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer_content_description, R.string.close_drawer_content_description);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_appbar_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            bindClientService();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_QR_CODE_INFORMATION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Intent serviceIntent = new Intent(
                        this,
                        Client.class
                ).putExtra(App.EXTRA_TARGET_IP_ADDRESS, data.getStringExtra(App.EXTRA_TARGET_IP_ADDRESS));
                startService(serviceIntent);
                bindClientService();
            } else {
                Toast.makeText(this, R.string.invalid_ip_address_toast_text, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(serviceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void connectButtonClick(View v) {
        if (App.CONNECTION_ALIVE) {
            Toast.makeText(MainActivity.this, R.string.connection_already_established_toast_text, Toast.LENGTH_SHORT).show();
            return;
        }
        EditText computerIpTextInput = findViewById(R.id.computer_ip_text_input);
        String targetIpAddress = "";
        Editable editable = computerIpTextInput.getText();
        if (editable != null) {
            targetIpAddress = editable.toString();
        }
        targetIpAddress = targetIpAddress.trim();
        if (!IpAddressValidator.isLocalIpAddress(targetIpAddress))
            Toast.makeText(this, R.string.invalid_ip_address_toast_text, Toast.LENGTH_SHORT).show();
        else {
            Intent serviceIntent = new Intent(
                    this,
                    Client.class
            ).putExtra(App.EXTRA_TARGET_IP_ADDRESS, targetIpAddress);
            startService(serviceIntent);
            bindClientService();
        }
    }


    private void bindClientService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Client.ClientBinder clientBinder = (Client.ClientBinder) service;
                client = clientBinder.getClient();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        bindService(new Intent(this, Client.class), serviceConnection, 0);
    }

    public void closeAppMenuItemClick(MenuItem item) {
        if (client != null) client.stopClient();
        finish();
    }

    public void startScanButtonClick(View v) {
        if (App.CONNECTION_ALIVE) {
            Toast.makeText(MainActivity.this, R.string.connection_already_established_toast_text, Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(new Intent(getApplicationContext(), QrCodeScannerActivity.class), GET_QR_CODE_INFORMATION);
        }
    }

    private boolean checkIfDeviceIsConnectedBeforeOpeningActivity() {
        if (!App.CONNECTION_ALIVE) {
            drawer.closeDrawer(GravityCompat.START);
            Toast.makeText(this, R.string.device_not_connected_text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_action_remote_control:
                if (checkIfDeviceIsConnectedBeforeOpeningActivity())
                    startActivity(new Intent(this, MainControlInterfaceActivity.class));
                break;
            case R.id.drawer_action_file_sharing:
                if (checkIfDeviceIsConnectedBeforeOpeningActivity())
                    startActivity(new Intent(this, FileSharingActivity.class));
                break;
            case R.id.drawer_action_link_sharing:
                if (checkIfDeviceIsConnectedBeforeOpeningActivity())
                    startActivity(new Intent(this, LinkSharingActivity.class));
                break;
            case R.id.drawer_action_touchpad:
                if (checkIfDeviceIsConnectedBeforeOpeningActivity())
                    startActivity(new Intent(this, TouchpadActivity.class));
            default:
                return false;
        }
        return true;
    }
}
