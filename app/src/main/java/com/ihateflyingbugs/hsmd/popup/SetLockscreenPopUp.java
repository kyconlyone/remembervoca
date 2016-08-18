package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.lock.LockService;

import java.util.HashMap;
import java.util.Map;

public class SetLockscreenPopUp extends Activity {


	DBPool db;
	private ToggleButton tb_popup_set_lockscreen;
	SharedPreferences mPreference;


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_setlockscreen);

		mPreference = getSharedPreferences(MainValue.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);


		mPreference.edit().putString("lockstart", ""+1).commit();

		db = DBPool.getInstance(getApplicationContext());

		db.setLockScreenTable("1");
		Intent intent = new Intent();
		intent.setPackage("com.ihateflyingbugs.hsmd.lock");
		startService(intent);
		tb_popup_set_lockscreen = (ToggleButton) findViewById(R.id.tb_popup_set_lockscreen);

		if (db.getLockScreenTable()) {
			tb_popup_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
		} else {
			tb_popup_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
		}

		tb_popup_set_lockscreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(!isMyServiceRunning(LockService.class)){
					Log.e("serviceCheck", "Mainactivity : start service");
					Intent intent1 = new Intent(LockService.LOCK_SERVICE);
					intent1.setPackage("com.ihateflyingbugs.hsmd.lock");
					startService(intent1);
				}

				if (db.getLockScreenTable()) {
					tb_popup_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					db.setLockScreenTable("0");
					FlurryAgent.logEvent("SetLockscreenPopUp:Click_Off_LockScreen");
					Toast.makeText(SetLockscreenPopUp.this, "App을 완전히 종료해 주세요.", Toast.LENGTH_SHORT).show();
					Log.e("serviceCheck", "POPUP : STOP service");
					Intent intent1 = new Intent(LockService.LOCK_SERVICE);
					intent1.setPackage("com.ihateflyingbugs.hsmd.lock");
					stopService(intent1);
				} else {
					tb_popup_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					db.setLockScreenTable("1");
					FlurryAgent.logEvent("SetLockscreenPopUp:Click_On_LockScreen");
					Log.e("serviceCheck", "POPUP : start service");
					if(!isMyServiceRunning(LockService.class)){
						Log.e("serviceCheck", "Mainactivity : start service");
						Intent intent1 = new Intent(LockService.LOCK_SERVICE);
						intent1.setPackage("com.ihateflyingbugs.hsmd.lock");
						startService(intent1);
					}
				}

			}
		});
		Button bt_close_popup = (Button)findViewById(R.id.bt_close_popup);

		bt_close_popup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});


	}
	String starttime;
	String startdate;
	Date date = new Date();

	Map<String, String> articleParams ;
	public void onStart()
	{

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SetLockscreenPopUp", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.d("SERVISESEES", service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}

}
