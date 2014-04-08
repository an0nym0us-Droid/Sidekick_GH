package com.example.sidekick_offline_try2;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * This is our service client, it is the 'middle-man' between the
 * service and any activity that wants to connect to the service
 * 
 * @author paul.blundell
 */
public class ScheduleClient {

	// The hook into our service
	private ScheduleService mBoundService;
	// The context to start the service in
	private Context mContext;
	// A flag if we are connected to the service or not
	private boolean mIsBound;

	public ScheduleClient(Context context) {
		mContext = context;
	}
	
	/**
	 * Call this to connect your activity to your service
	 */
	public void doBindService() {
		// Establish a connection with our service
		Log.i("REMINDER","doBindService Called");

		if(mContext.bindService(new Intent(mContext, ScheduleService.class), mConnection, Context.BIND_AUTO_CREATE))
			Log.i("REMINDER","success bind");
		else
			Log.i("REMINDER","UNNsuccess bind");

		mIsBound = true;
	}
	
	/**
	 * When you attempt to connect to the service, this connection will be called with the result.
	 * If we have successfully connected we instantiate our service object so that we can call methods on it.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with our service has been established, 
			// giving us the service object we can use to interact with our service.
			Log.i("REMINDER","INITIALIZING mBoundService");
			mBoundService = ((ScheduleService.ServiceBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.i("REMINDER","SERVICE DISCONNECTED");
			mBoundService = null;
		}
	};

	/**
	 * Tell our service to set an alarm for the given date
	 * @param c a date to set the notification for
	 */
	public void setAlarmForNotification(Calendar c,ShowsList show){
		ShowsList showItem = new ShowsList(show);
		if(c==null)
			Log.e("REMINDER","NULLLLLLL");
		if(mBoundService==null)
			Log.e("REMINDER","NULLLLLLL");
		mBoundService.setAlarm(c,showItem);
	}
	public void resetAlarmForNotification(Calendar c,ShowsList show) {
		mBoundService.removeAlarm(c, show);
//		((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(mContext, (int) uniqueId, new Intent(mContext, NotifyService.class), 0));
	}
	/**
	 * When you have finished with the service call this method to stop it 
	 * releasing your connection and resources
	 */
	public void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			mContext.unbindService(mConnection);
			mIsBound = false;
		}
	}
}