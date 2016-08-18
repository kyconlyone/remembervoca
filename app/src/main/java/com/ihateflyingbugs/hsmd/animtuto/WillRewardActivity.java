package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.ihateflyingbugs.hsmd.model.NotificationData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WillRewardActivity extends Activity{
	String extra;

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


		db = DBPool.getInstance(WillRewardActivity.this);
		final SharedPreferences mpreferences = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		if ((getIntent().getBooleanExtra("start", false))) {
			if(MainValue.isLowDensity(getApplicationContext())){
				setContentView(R.layout.will_start_dialog_small);
			}else{
				setContentView(R.layout.will_start_dialog);
			}
			Log.d("ACTIVITYRESULT", "start");
			extra = "Start";


			Date_Step1 = date.get_currentTime();
			Time_Step1 = String.valueOf(System.currentTimeMillis());

		} else {
			if(MainValue.isLowDensity(getApplicationContext())){
				setContentView(R.layout.will_tuto_dialog_small);
			}else{
				setContentView(R.layout.will_tuto_dialog);
			}
			Log.d("ACTIVITYRESULT", "end");
			TextView tv_willtutodialog_contents = (TextView)findViewById(R.id.tv_willtutodialog_contents);
			tv_willtutodialog_contents.setText("인기 많은 "+mpreferences.getString(MainValue.GpreName, "이름없음")
					+"님의\n의지를 돕기 위해\n카카오톡 차단 기능을 드릴께요.^^");
			extra = "End";

			Date_Step2 = date.get_currentTime();
			Time_Step2 = String.valueOf(System.currentTimeMillis());

		}

		Button bt_tuto_will_popup_ok = (Button) findViewById(R.id.bt_tuto_will_popup_ok);

		db = DBPool.getInstance(getApplicationContext());

		bt_tuto_will_popup_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if ((getIntent().getBooleanExtra("start", false))) {
					FlurryAgent.logEvent("WillRewardActivity:Click_Start_Tutorial", Step1Params);
					Step1Params.put("Start", Date_Step1);
					Step1Params.put("End", date.get_currentTime());
					Step1Params.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(Time_Step1))) / 1000);

				}else{
					FlurryAgent.logEvent("WillRewardActivity:Click_Confirm_Reward", Step2Params);
					Step2Params.put("Start", Date_Step2);
					Step2Params.put("End", date.get_currentTime());
					Step2Params.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(Time_Step2))) / 1000);
					db.insertTutorialWill(true);

					new RetrofitService().getNotificationService()
							.retroMayKnowFriendPush(db.getStudentId(),
													mpreferences.getString(MainValue.GpreSchoolName,"고등학교"),
													mpreferences.getString(MainValue.GpreName, "친구"),
													""+NotificationData.PUSH_NEW_MEMBER)
							.enqueue(new Callback<NotificationData>() {
								@Override
								public void onResponse(Response<NotificationData> response, Retrofit retrofit) {

								}

								@Override
								public void onFailure(Throwable t) {
									Log.d("WillRewardActivity", "onFailure : "+ t.toString());
								}
							});

				}

				Intent intent = new Intent();
				intent.putExtra("start", extra);
				Log.d("ACTIVITYRESULT", intent.getStringExtra("start") + " 111");
				setResult(97531, intent);
				finish();
				Log.d("ACTIVITYRESULT", intent.getStringExtra("start") + " 222");
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
		FlurryAgent.logEvent("WillRewardActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("WillRewardActivity");
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
