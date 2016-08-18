package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;

import java.util.HashMap;
import java.util.Map;

public class MemoryRewardActivity extends Activity {
	Intent intent;

	Map<String, String> Step1Params= new HashMap<String, String>();
	Map<String, String> Step2Params= new HashMap<String, String>();

	String Date_Step1;
	String Time_Step1;
	String Date_Step2;
	String Time_Step2;


	DBPool db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		db = DBPool.getInstance(MemoryRewardActivity.this);

		final SharedPreferences mpreferences = getSharedPreferences(MainValue.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		intent = new Intent();
		if ((getIntent().getBooleanExtra("start", false))) {
			if(MainValue.isLowDensity(getApplicationContext())){
				setContentView(R.layout.memory_start_dialog_small);
			}else{
				setContentView(R.layout.memory_start_dialog);
			}
			intent.putExtra("start", true);

			Date_Step1 = date.get_currentTime();
			Time_Step1 = String.valueOf(System.currentTimeMillis());

		} else {
			if(MainValue.isLowDensity(getApplicationContext())){
				setContentView(R.layout.memory_tuto_dialog_small);
			}else{
				setContentView(R.layout.memory_tuto_dialog);
			}
			TextView tv_reward = (TextView) findViewById(R.id.tv_reward);
			tv_reward.setText(mpreferences.getString(MainValue.GpreName, "이름없음") + "님의 기억력을 측정하고\n까먹을 것 같은 단어를 푸쉬로 보내주는\n[망각곡선 자동 복습기]를 드릴게요");
			intent.putExtra("start", false);

			Date_Step2 = date.get_currentTime();
			Time_Step2 = String.valueOf(System.currentTimeMillis());

		}

		Button bt_tuto_memory_popup_ok = (Button) findViewById(R.id.bt_tuto_memory_popup_ok);

		db = DBPool.getInstance(getApplicationContext());

		bt_tuto_memory_popup_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if ((getIntent().getBooleanExtra("start", false))) {
					FlurryAgent.logEvent("MemoryRewardActivity:Click_Start_Tutorial", Step1Params);
					Step1Params.put("Start", Date_Step1);
					Step1Params.put("End", date.get_currentTime());
					Step1Params.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(Time_Step1))) / 1000);

				}else{
					FlurryAgent.logEvent("MemoryRewardActivity:Click_Confirm_Reward", Step2Params);
					Step2Params.put("Start", Date_Step2);
					Step2Params.put("End", date.get_currentTime());
					Step2Params.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(Time_Step2))) / 1000);

					db.insertTutorialMemory(true);
				}
				setResult(97531, intent);
				finish();
			}
		});
	}

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart() {

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("MemoryRewardActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("MemoryRewardActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put(
				"Duration",
				""
						+ ((Long.valueOf(System.currentTimeMillis()) - Long
						.valueOf(starttime))) / 1000);
		FlurryAgent.onEndSession(this);
		// your code

	}
}