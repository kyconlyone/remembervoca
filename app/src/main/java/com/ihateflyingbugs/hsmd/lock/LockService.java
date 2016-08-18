package com.ihateflyingbugs.hsmd.lock;


import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.ihateflyingbugs.hsmd.data.DBPool;

public class LockService extends Service{

	public static final String LOCK_SERVICE = "com.ihateflyingbugs.hsmd.lock.LockService";

	BroadcastReceiver mReceiver;
	DBPool db;
	// Intent myIntent;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();


	}


	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		db = DBPool.getInstance(getApplicationContext());

		if(!db.getLockScreenTable()){
			Log.e("serviceCheck", "Lockservice : cancel");
			onDestroy();
			return;
		}



		Log.v("serviceCheck", "Lockservice : start");
		KeyguardManager.KeyguardLock k1;
		Log.d("ohterapp", "LockService");

		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		k1= km.newKeyguardLock("IN");
		k1.disableKeyguard();




		/*try{
     StateListener phoneStateListener = new StateListener();
     TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
     telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
     }catch(Exception e){
    	 System.out.println(e);
     }*/

		/* myIntent = new Intent(LockService.this,LockScreenAppActivity.class);
     myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     Bundle myKillerBundle = new Bundle();
     myKillerBundle.putInt("kill",1);
     myIntent.putExtras(myKillerBundle);*/

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		mReceiver = new VocaReceiver();
		registerReceiver(mReceiver, filter);

		super.onStart(intent, startId);

	}

	/*class StateListener extends PhoneStateListener{
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        super.onCallStateChanged(state, incomingNumber);
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                System.out.println("call Activity off hook");
            	getApplication().startActivity(myIntent);



                break;
            case TelephonyManager.CALL_STATE_IDLE:
                break;
        }
    }
};*/


	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(mReceiver);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}
}
