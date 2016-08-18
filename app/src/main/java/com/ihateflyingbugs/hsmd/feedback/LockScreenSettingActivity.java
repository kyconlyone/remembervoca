package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.lock.LockService;

import java.util.HashMap;
import java.util.Map;

public class LockScreenSettingActivity extends FragmentActivity implements OnClickListener {
	private LinearLayout ll_set_lockscreen;
	private LinearLayout ll_setting_lock_contents;

	private ToggleButton tb_set_lockscreen;

	Handler handler;

	DBPool db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_lockscreen);

		setActionBar();


		handler = new Handler();
		db = DBPool.getInstance(getApplicationContext());


		ll_set_lockscreen = (LinearLayout) findViewById(R.id.ll_set_lockscreen);
		tb_set_lockscreen = (ToggleButton) findViewById(R.id.tb_set_lockscreen);

		ll_set_lockscreen.setOnClickListener(this);
		tb_set_lockscreen.setOnClickListener(this);


		if (db.getLockScreenTable()) {
			tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
		} else {
			tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_set_lockscreen: case R.id.tb_set_lockscreen: {
				if (db.getLockScreenTable()) {
					tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					db.setLockScreenTable("0");
					FlurryAgent.logEvent("LockScreenSettingActivity:Click_Off_LockScreen");
					Toast.makeText(LockScreenSettingActivity.this, "App을 완전히 종료해 주세요.", Toast.LENGTH_SHORT).show();
					stopService(new Intent(LockService.LOCK_SERVICE));
					Log.e("serviceCheck", "Setting : stop service");
				}
				else {
					tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					db.setLockScreenTable("1");
					FlurryAgent.logEvent("LockScreenSettingActivity:Click_On_LockScreen");
					if(!isMyServiceRunning(LockService.class)){
						Log.e("serviceCheck", "Mainactivity : start service");
						Intent intent1 = new Intent(LockService.LOCK_SERVICE);
						intent1.setPackage("com.ihateflyingbugs.hsmd.lock");
						startService(intent1);
					}
					Log.e("serviceCheck", "Setting : start service");
				}
			}
			break;
		}

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


	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("LockScreenSettingActivity:Click_Close_BackButton");
				LockScreenSettingActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("잠금화면 설정하기");
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("LockScreenSettingActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("LockScreenSettingActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
