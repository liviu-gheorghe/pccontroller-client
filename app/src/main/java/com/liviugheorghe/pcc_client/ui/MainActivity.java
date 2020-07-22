package com.liviugheorghe.pcc_client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.util.IpAddressValidator;
import com.pccontroller.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.connect_button).setOnClickListener(view -> {

            if (App.CONNECTION_ALIVE) {
                Toast.makeText(MainActivity.this, "A connection is already established", Toast.LENGTH_SHORT).show();
                return;
            }
            TextInputEditText computerIpTextInput = findViewById(R.id.computer_ip_text_input);
            String targetIpAddress = computerIpTextInput.getText().toString();
            targetIpAddress = targetIpAddress.trim();
            if(!IpAddressValidator.isIpAddress(targetIpAddress))
                Toast.makeText(this,"Invalid Ip Address" , Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(
                        MainActivity.this,
                        WaitForPermissionActivity.class
                );
                intent.putExtra(App.EXTRA_TARGET_IP_ADDRESS, targetIpAddress);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.start_scan_button).setOnClickListener(view -> {
            if (App.CONNECTION_ALIVE) {
                Toast.makeText(MainActivity.this, "A connection is already established", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(getApplicationContext(), QrCodeScannerActivity.class));
                finish();
            }
        });
    }
}
