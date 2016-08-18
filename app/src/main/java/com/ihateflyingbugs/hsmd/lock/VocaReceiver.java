package com.ihateflyingbugs.hsmd.lock;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.service.DBService;

public class VocaReceiver extends BroadcastReceiver  {
	public static boolean wasScreenOn = true;
	public static AlarmManager alarm;
	StateListener phoneStateListener;
	TelephonyManager telephonyManager;
	@Override
	public void onReceive(Context context, Intent intent) {

		DBPool db = DBPool.getInstance(context);
		

		Log.v("serviceCheck", "onReceive : start");
		

//		if(!db.getLockScreenTable()){
//			Log.d("ohterapp", "VocaReceiver return");
//			return;
//		}
		
		try{
			phoneStateListener = new StateListener(context);
			telephonyManager =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			//telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
		}catch(Exception e){
			System.out.println(e);
		}

		if(telephonyManager.getCallState()==TelephonyManager.CALL_STATE_OFFHOOK
				||telephonyManager.getCallState()==TelephonyManager.CALL_STATE_RINGING){
			return;
		}
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			//Toast.makeText(context, "" + "screeen off", Toast.LENGTH_SHORT).show();
			Log.v("kjw", "ACTION_SCREEN_OFF");
			wasScreenOn = false;
			Intent intent11 = new Intent(context, LockScreenActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

			context.startActivity(intent11);


			// do whatever you need to do here
			//wasScreenOn = false;
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

			Log.v("kjw", "ACTION_SCREEN_ON");
			wasScreenOn = true;
			Intent intent11 = new Intent(context, LockScreenActivity.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

			//context.startActivity(intent11);
			//Toast.makeText(context, "" + "start activity", Toast.LENGTH_SHORT).show();
			// and do whatever you need to do here
			// wasScreenOn = true;
		}
		else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{
			/*  	KeyguardManager.KeyguardLock k1;
        	KeyguardManager km =(KeyguardManager)mContext.getSystemService(mContext.KEYGUARD_SERVICE);
            k1 = km.newKeyguardLock("IN");
            k1.disableKeyguard();
			 */
			//			Calendar cal = Calendar.getInstance();
			//			cal.add(Calendar.SECOND, 10);
			//			
			//			Intent intent12 = new Intent(mContext, DBService.class);
			//			PendingIntent pintent = PendingIntent.getService(mContext, 0, intent12, 0);
			//			AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
			//			
			//			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
			//					AlarmManager.INTERVAL_HOUR, pintent);
			//			mContext.startService(new Intent(mContext, DBService.class));

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 10);
			//			
			Log.e("alma", "ACTION_BOOT_COMPLETED");
			Intent intent12 = new Intent(context, DBService.class);
			PendingIntent pintent = PendingIntent.getService(context, 0, intent12, 0);
			AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

			if(!isMyServiceRunning(DBService.class, context)){
				Log.d("SERVISESEES", "DBService, not running");
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pintent);
			}

			Log.e("alma", "ACTION_BOOT_COMPLETED");

			
			Intent intent11 = new Intent(context, LockScreenActivity.class);

			intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent11);

			//  Intent intent = new Intent(mContext, LockPage.class);
			//  mContext.startActivity(intent);
			//  Intent serviceLauncher = new Intent(mContext, UpdateService.class);
			//  mContext.startService(serviceLauncher);
			//  Log.v("TEST", "Service loaded at start");
		}

	}

	private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.d("SERVISESEES", service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}

	class StateListener extends PhoneStateListener{
		Context context;
		public StateListener(Context context){
			this.context = context;
		}
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch(state){
			case TelephonyManager.CALL_STATE_RINGING:
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("call Activity off hook");
				Intent intent11 = new Intent(context, LockScreenActivity.class);
				intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent11);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			}
		}
	};
	
	
	
	
}
