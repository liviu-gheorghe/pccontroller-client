package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.backend.Client;
import com.pccontroller.R;

import androidx.appcompat.app.AppCompatActivity;


public class WaitForPermissionActivity extends AppCompatActivity {
	
	private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent.getAction().equals("LEAVE_WAIT_FOR_PERMISSION_ACTIVITY")) {
				Intent i = new Intent(WaitForPermissionActivity.this, LauncherActivity.class);
				startActivity(i);
				finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(serviceBroadcastReceiver, new IntentFilter("LEAVE_WAIT_FOR_PERMISSION_ACTIVITY"));
		setContentView(R.layout.activity_wait_for_permission);
		Intent serviceIntent = new Intent(
				this,
				Client.class
		).putExtra(App.EXTRA_TARGET_IP_ADDRESS, getIntent().getStringExtra(App.EXTRA_TARGET_IP_ADDRESS));
		startService(serviceIntent);
	}
}
