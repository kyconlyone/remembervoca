package com.ihateflyingbugs.hsmd;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UseActivity extends Activity {
	String TAG = "UseActivity";


	Context mContext;
	SharedPreferences mPreference;

	ProgressBar pb_use_law;
	ProgressBar pb_userinfo_law ;
	TextView tv_use_law;
	TextView tv_userinfo_law ;

	Handler handler;



	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_use);

		setActionBar();


		mPreference =  getSharedPreferences(MainValue.preName, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);


		handler = new Handler();

		pb_use_law = (ProgressBar) findViewById(R.id.pb_use_law);
		pb_userinfo_law = (ProgressBar) findViewById(R.id.pb_userinfo_law) ;

		tv_use_law = (TextView) findViewById(R.id.tv_use_law);
		tv_userinfo_law = (TextView) findViewById(R.id.tv_userinfo_law);

		pb_use_law.setVisibility(View.VISIBLE);
		pb_userinfo_law.setVisibility(View.VISIBLE);

		mContext = getApplicationContext();

		new RetrofitService().getUseAppInfoService().retroGetPolicy()
				.enqueue(new Callback<UseAppInfoData>() {
					@Override
					public void onResponse(final Response<UseAppInfoData> response, Retrofit retrofit) {
						handler.post(new Runnable() {
							public void run() {
								pb_use_law.setVisibility(View.GONE);
								pb_userinfo_law.setVisibility(View.GONE);

								tv_use_law.setText(response.body().getType1());
								tv_userinfo_law.setText(response.body().getType2());
							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure : "+ t.toString());
					}
				});
	}


	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("UseActivity:Click_Close_BackButton");
				UseActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("이용약관");
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
		FlurryAgent.logEvent("BeforeHitActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("BeforeHitActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
