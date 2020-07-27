package com.liviugheorghe.pcc_client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;

public class WaitForPermissionActivity extends AppCompatActivity {
	
	private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == null) return;
			if (intent.getAction().equals(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY)) {
				Intent i = new Intent(WaitForPermissionActivity.this, LauncherActivity.class);
				startActivity(i);
				finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_WAIT_FOR_PERMISSION_ACTIVITY));
		setContentView(R.layout.activity_wait_for_permission);
	}
      @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceBroadcastReceiver);
    }
}
