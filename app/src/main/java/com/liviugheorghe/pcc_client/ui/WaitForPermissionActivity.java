package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;

public class WaitForPermissionActivity extends AppCompatActivity {

	private boolean isInForeground = false;
	private String TAG = getClass().getSimpleName();
	
	private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Received local broadcast");
			if (intent.getAction() == null || !isInForeground) return;
			if (intent.getAction().equals(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY)) {
				Intent i;
				String where = intent.getStringExtra("WHERE");
				Log.d("Wait", "Where is : "+ where);
				if(where != null && where.equals("MAIN_ACTIVITY"))
					i = new Intent(WaitForPermissionActivity.this, MainActivity.class);
				else
					i = new Intent(WaitForPermissionActivity.this, MainControlInterfaceActivity.class);
				startActivity(i);
				finish();
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		isInForeground = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isInForeground = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(this).registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY));
		setContentView(R.layout.activity_wait_for_permission);
	}
      @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceBroadcastReceiver);
    }
}
