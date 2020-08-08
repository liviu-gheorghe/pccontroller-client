package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.FileConnection;
import com.liviugheorghe.pcc_client.util.FileInformation;

import java.util.UUID;

import static android.content.Intent.EXTRA_STREAM;
import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_NAME;
import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_SIZE;
import static com.liviugheorghe.pcc_client.App.EXTRA_FILE_TYPE;

public class FileSharingActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView textView;
    private FloatingActionButton button;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FileInformation fileInformation;
    private Drawable connectToADeviceDrawable;
    private Drawable sendFileDrawable;
    private FileInformationFragment fileInformationFragment;

    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY)) {
                updateConnectionInfo();
            }
        }
    };


    private String getUpdatedFileNameFromFragment() {
        try {
            return fileInformationFragment.getFilename();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadFABDrawables() {
        connectToADeviceDrawable = ContextCompat.getDrawable(this, R.drawable.ic_icon_connect_to_device);
        sendFileDrawable = ContextCompat.getDrawable(this, R.drawable.ic_icon_send);
    }

    private void handleInvalidFile() {
        Toast.makeText(this, R.string.invalid_file_toast_text, Toast.LENGTH_LONG).show();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MissingFileFragment missingFileFragment = new MissingFileFragment();
        transaction.replace(R.id.file_information_container, missingFileFragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sharing);
        setToolbar();
        loadFABDrawables();
        registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY));

        textView = findViewById(R.id.file_sharing_text);
        button = findViewById(R.id.file_sharing_button);


        if (getIntent().getExtras() == null || getIntent().getExtras().get(EXTRA_STREAM) == null) {
            handleInvalidFile();
        } else {
            if (savedInstanceState != null && savedInstanceState.getString(EXTRA_STREAM) != null)
                fileInformation = getCachedFileInformationFromBundle(savedInstanceState);
            else {
                Uri uri = (Uri) getIntent().getExtras().get(EXTRA_STREAM);
                fileInformation = getFileInformation(uri);
            }
            if (fileInformation.getName() == null)
                fileInformation.setName(UUID.randomUUID().toString() + "." + fileInformation.getType());
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fileInformationFragment = new FileInformationFragment(
                    fileInformation.getName(),
                    fileInformation.getSize(),
                    fileInformation.getType()
            );
            transaction.replace(R.id.file_information_container, fileInformationFragment);
            transaction.commit();
            button.setOnClickListener(view -> {
                if (App.CONNECTION_ALIVE) {
                    Intent serviceIntent = new Intent(this, FileConnection.class);
                    serviceIntent.putExtra(EXTRA_STREAM, fileInformation.getUri().toString());
                    String updatedFileName = getUpdatedFileNameFromFragment();
                    if (updatedFileName != null)
                        fileInformation.setName(updatedFileName);
                    serviceIntent.putExtra(EXTRA_FILE_NAME, fileInformation.getName());
                    serviceIntent.putExtra(EXTRA_FILE_SIZE, fileInformation.getSize());
                    serviceIntent.putExtra(EXTRA_FILE_TYPE, fileInformation.getType());
                    try {
                        startService(serviceIntent);
                    } catch (Exception e) {
                    }
                } else {
                    Intent intent = new Intent(FileSharingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            });
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (fileInformation == null) return;
        String updatedFileName = getUpdatedFileNameFromFragment();
        if (updatedFileName != null)
            fileInformation.setName(updatedFileName);
        outState.putString(EXTRA_STREAM, fileInformation.getUri().toString());
        outState.putString(EXTRA_FILE_NAME, fileInformation.getName());
        outState.putString(EXTRA_FILE_SIZE, fileInformation.getSize());
        outState.putString(EXTRA_FILE_TYPE, fileInformation.getType());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateConnectionInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceBroadcastReceiver);
    }


    private void updateConnectionInfo() {
        if (App.CONNECTION_ALIVE) {
            textView.setText(String.format("%s : %s", getResources().getString(R.string.connection_service_notification_text), App.CONNECTED_DEVICE_HOSTNAME));
            button.setImageDrawable(sendFileDrawable);
        } else {
            button.setImageDrawable(connectToADeviceDrawable);
            textView.setText(R.string.device_not_connected_text);
        }
    }

    private FileInformation getCachedFileInformationFromBundle(Bundle savedInstanceState) {
        return new FileInformation(
                Uri.parse(savedInstanceState.getString(EXTRA_STREAM)),
                savedInstanceState.getString(EXTRA_FILE_NAME),
                savedInstanceState.getString(EXTRA_FILE_SIZE),
                savedInstanceState.getString(EXTRA_FILE_TYPE)
        );
    }

    private FileInformation getFileInformation(Uri uri) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setUri(uri);
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                fileInformation.setName(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size;
                if (!cursor.isNull(sizeIndex))
                    size = cursor.getString(sizeIndex);
                else
                    size = "Unknown";

                fileInformation.setSize(size);
            }
            String mimeType = getContentResolver().getType(uri);
            if (mimeType != null)
                fileInformation.setType(MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInformation;
    }
}
