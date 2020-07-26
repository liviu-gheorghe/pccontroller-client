package com.liviugheorghe.pcc_client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.util.IpAddressValidator;
import com.pccontroller.R;

public class MainActivity extends AppCompatActivity {


    private final int GET_QR_CODE_INFORMATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_QR_CODE_INFORMATION) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(
                        MainActivity.this,
                        WaitForPermissionActivity.class
                );
                intent.putExtra(App.EXTRA_TARGET_IP_ADDRESS, data.getStringExtra(App.EXTRA_TARGET_IP_ADDRESS));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, R.string.invalid_ip_address_toast_text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void connectButtonClick(View v) {
        if (App.CONNECTION_ALIVE) {
            Toast.makeText(MainActivity.this, R.string.connection_already_established_toast_text, Toast.LENGTH_SHORT).show();
            return;
        }
        TextInputEditText computerIpTextInput = findViewById(R.id.computer_ip_text_input);
        String targetIpAddress = "";
        Editable editable = computerIpTextInput.getText();
        if (editable != null) {
            targetIpAddress = editable.toString();
        }
        targetIpAddress = targetIpAddress.trim();
        if (!IpAddressValidator.isLocalIpAddress(targetIpAddress))
            Toast.makeText(this, R.string.invalid_ip_address_toast_text, Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(
                    MainActivity.this,
                    WaitForPermissionActivity.class
            );
            intent.putExtra(App.EXTRA_TARGET_IP_ADDRESS, targetIpAddress);
            startActivity(intent);
            finish();
        }
    }

    public void startScanButtonClick(View v) {
        if (App.CONNECTION_ALIVE) {
            Toast.makeText(MainActivity.this, R.string.connection_already_established_toast_text, Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(new Intent(getApplicationContext(), QrCodeScannerActivity.class), GET_QR_CODE_INFORMATION);
            finish();
        }
    }
}
