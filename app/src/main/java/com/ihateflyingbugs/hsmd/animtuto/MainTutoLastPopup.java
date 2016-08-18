package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainTutoLastPopup extends Activity {

	String TAG = "MainTutoLastPopup";
	SharedPreferences mPreferences;
	DBPool db;

	ProgressBar pb_splash_tuto;

	Handler handler;

	boolean isFinish= false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("ActiviityTest", "MainTutoLastPopup Create");

		handler = new Handler();
		db = DBPool.getInstance(MainTutoLastPopup.this);
		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_tuto_last_popup_small);
		}else{
			setContentView(R.layout.activity_tuto_last_popup);
		}

		pb_splash_tuto = (ProgressBar)findViewById(R.id.pb_splash_tuto);
		db = DBPool.getInstance(getApplicationContext());

		mPreferences =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		Button bt_last_tuto_popup_ok = (Button) findViewById(R.id.bt_last_tuto_popup_ok);

		bt_last_tuto_popup_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pb_splash_tuto.setVisibility(View.VISIBLE);
				Log.d("firstStart",  "LastPopup " + mPreferences.getString("firststart", "0"));
				new RetrofitService().getUseAppInfoService().retroUpdateTutorialFinished(db.getStudentId())
						.enqueue(new Callback<UseAppInfoData>() {
							@Override
							public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
								Log.d("ActiviityTest", "MainTutoLastPopup Response");
								// TODO Auto-generated method stub
								db.insertTutorial(true);
//						overridePendingTransition(R.anim.tutorial_activity_in, R.anim.tutorial_activity_in);
								Intent intent = new Intent(MainTutoLastPopup.this, MainActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										pb_splash_tuto.setVisibility(View.GONE);
										finish();
									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								Log.e(TAG, "onFailure : "+t.toString());
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										pb_splash_tuto.setVisibility(View.GONE);
									}
								});
								if(isFinish){
									Toast.makeText(getApplicationContext(), "다시 한번 확인버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
									isFinish = true;
								}else{
									Log.d("ActiviityTest", "MainTutoLastPopup Exception Else");
									db.insertTutorial(true);
//							overridePendingTransition(R.anim.tutorial_activity_in, R.anim.tutorial_activity_in);
									Intent intent = new Intent(MainTutoLastPopup.this, MainActivity.class);
//							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									isFinish = false;

									finish();
								}
							}
						});


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
		FlurryAgent.logEvent("MainTutoLastPopup", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("MainTutoLastPopup");
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
