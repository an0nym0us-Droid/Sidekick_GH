package com.example.sidekick_offline_try2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NotificationReminder extends BroadcastReceiver{

	 private NotificationManager mNotificationManager;
	 private Notification notification;   

	  @Override
	 public void onReceive(Context context, Intent intent) {
	  // TODO Auto-generated method stub  
		  Log.e("Sidekick_V3","Entering NotificaitonReminder");
	      mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	      CharSequence from = intent.getStringExtra("Name");
	      CharSequence message = intent.getStringExtra("Description");
	      PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
	      notification = new Notification(R.drawable.reminder_icon,"Show Reminder", System.currentTimeMillis());
	      notification.setLatestEventInfo(context, from, message, contentIntent);
	      mNotificationManager.notify(Integer.parseInt(intent.getExtras().get("NotifyId").toString()), notification);        
	 }

}
