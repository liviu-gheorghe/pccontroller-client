package com.liviugheorghe.pcc_client.backend.actions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.Action;
import com.liviugheorghe.pcc_client.backend.ReceivedActionsCodes;
import com.liviugheorghe.pcc_client.ui.LauncherActivity;

public class ActionReceivePing implements Action {
	
	private String content;
	
	public ActionReceivePing(String content) {
		this.content = content;
	}
	
	@Override
    public void execute() {
        new Thread(this::createPingNotification).start();
    }

    private void createPingNotification() {
        Intent notificationIntent = new Intent(
                App.getAppContext(), LauncherActivity.class
        );
		PendingIntent pendingIntent = PendingIntent.getActivity(
				App.getAppContext(), 0, notificationIntent, 0
		);
		Notification notification = new NotificationCompat.Builder(App.getAppContext(), App.BACKGROUND_SERVICE_CHANNEL_ID)
				.setContentTitle("")
				.setContentText("Ping!")
				.setSmallIcon(R.drawable.ic_android_black_24dp)
				.setContentIntent(pendingIntent)
				.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;


        NotificationManager nm = (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(3, notification);
    }

    @Override
    public int getActionType() {
        return ReceivedActionsCodes.RECEIVE_PING;
    }
}
