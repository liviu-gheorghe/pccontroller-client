package com.liviugheorghe.pcc_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.util.IpAddressValidator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    @Override
    public void handleResult(Result result) {

        vibrateDevice(250);
        String scanResult = result.getText();
        if (!isValidQrCode(scanResult)) {
            displayDialogBox(
                    "Invalid QR code",
                    "Scan again",
                    "Exit",
                    (dialog, which) -> {
                        onResume();
                    },
                    (dialog, which) -> {
                        finish();
                    }
            );
            return;
        }

        String targetIpAddress = scanResult.split(",")[0];
        String targetHostName = scanResult.split(",")[1];

        if (App.CONNECTION_ALIVE) {
            return;
        }

        Intent waitForPermissionActivityIntent = new Intent(this, WaitForPermissionActivity.class);
        waitForPermissionActivityIntent.putExtra(App.EXTRA_TARGET_IP_ADDRESS, targetIpAddress);
        startActivity(waitForPermissionActivityIntent);
        finish();
    }

    private boolean isValidQrCode(String text) {
        if (text == null) return false;
        String[] params = text.split(",");
        if (params.length != 2) return false;
        if (!IpAddressValidator.isLocalIpAddress(params[0])) return false;
        if (params[1].length() == 0) return false;
        return true;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        displayAlertMessage("You need to allow permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{CAMERA},
                                            REQUEST_CAMERA);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else requestPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    private void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void displayDialogBox(String message,String option1,String option2,DialogInterface.OnClickListener listener1,DialogInterface.OnClickListener listener2) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(option1, listener1)
                .setNegativeButton(option2, listener2)
                .create()
                .show();
    }


    private void vibrateDevice(int milliseconds) {
        Vibrator v = (Vibrator) App.getAppContext().getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(milliseconds);
        }
    }

}
