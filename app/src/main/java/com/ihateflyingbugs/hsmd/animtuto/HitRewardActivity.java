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

//import com.google.android.gms.internal.db;

public class HitRewardActivity extends Activity{
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

		db = DBPool.getInstance(getApplicationContext());

		intent = new Intent();
		final SharedPreferences mpreferences = getSharedPreferences(MainValue.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		TextView tv_reward;
		if ((getIntent().getBooleanExtra("start", false))) {
			if(MainValue.isLowDensity(getApplicationContext())){
				setContentView(R.layout.hit_tuto_dialog_small);
			}else{
				setContentView(R.layout.hit_tuto_dialog);
			}
			setContentView(R.layout.hit_start_dialog);
			intent.putExtra("start", true);
			tv_reward = (TextView) findViewById(R.id.tv_hit_start_reward);
			tv_reward.setText("어떤 공부든 적중률은 중요합니다\n" + mpreferences.getString(MainValue.GpreName, "이름없음") + "님의 수준에 맞는 단어장을\n선택해주세요\n[수능 93% 적중 단어장]을 드릴게요!");

			Date_Step1 = date.get_currentTime();
			Time_Step1 = String.valueOf(System.currentTimeMillis());


		} else {
			if(MainValue.isLowDensity(getApplicationContext())){
				setContentView(R.layout.hit_tuto_dialog_small);
			}else{
				setContentView(R.layout.hit_tuto_dialog);
			}
			intent.putExtra("start", false);
			tv_reward = (TextView) findViewById(R.id.tv_hit_tuto_reward);
			tv_reward.setText(mpreferences.getString(MainValue.GpreName, "이름없음") + "님이 모의고사를 볼 때 마다\n또 EBS 교재가 출시될 때마다\n실시간으로 빈출단어를 정리하여\n업데이트 해드릴게요");

			Date_Step2 = date.get_currentTime();
			Time_Step2 = String.valueOf(System.currentTimeMillis());

			db.insertTutorialHit(true);
		}

		Button bt_tuto_hit_popup_ok = (Button) findViewById(R.id.bt_tuto_hit_popup_ok);

		bt_tuto_hit_popup_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((getIntent().getBooleanExtra("start", false))) {
					FlurryAgent.logEvent("HitRewardActivity:Click_Start_Tutorial", Step1Params);
					Step1Params.put("Start", Date_Step1);
					Step1Params.put("End", date.get_currentTime());
					Step1Params.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(Time_Step1))) / 1000);

				}else{
					FlurryAgent.logEvent("HitRewardActivity:Click_Confirm_Reward", Step2Params);
					Step2Params.put("Start", Date_Step2);
					Step2Params.put("End", date.get_currentTime());
					Step2Params.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(Time_Step2))) / 1000);

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
		FlurryAgent.logEvent("HitRewardActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("HitRewardActivity");
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
