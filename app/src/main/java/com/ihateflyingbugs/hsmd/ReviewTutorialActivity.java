package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class ReviewTutorialActivity extends Activity {

	RelativeLayout firstLayout, secondLayout, backgroundLayout;
	TextView firstMsg, secondMsg;
	Button firstBtn, secondBtn;

	DBPool db;

	Boolean flag = false;

	static SharedPreferences mPreference;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_review_tutorial_small);
		}else{
			setContentView(R.layout.activity_review_tutorial);
		}


		db = DBPool.getInstance(this);
		context = getApplicationContext();
		mPreference = context.getSharedPreferences(MainValue.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		Editor editor = mPreference.edit();

		//복습 튜토리얼이 시작되면, review_flag를 false로
		editor.putBoolean(MainValue.GpreReviewFlag, false);
		editor.commit();

		firstLayout = (RelativeLayout) findViewById(R.id.first_tutorial_layout);
		secondLayout = (RelativeLayout) findViewById(R.id.second_tutorial_layout);
		backgroundLayout = (RelativeLayout) findViewById(R.id.background_layout);

		firstMsg = (TextView) findViewById(R.id.firstMsg);
		secondMsg = (TextView) findViewById(R.id.secondMsg);

		firstBtn = (Button) findViewById(R.id.firstBtn);
		secondBtn = (Button) findViewById(R.id.secondBtn);

		secondLayout.setVisibility(View.INVISIBLE);

		firstMsg.setText(mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님이 외운 단어들 중 " + Integer.toString(db.getMforget())
				+ "개가 머릿 속에서 잊혀지려 합니다.");
		secondMsg.setText("복습 관리 모드를 톨해 \n"
				+ mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님은\n잊을 수도 있었던 " + Integer.toString(db.getMforget())
				+ "개의 단어를 더 확실히 지킬 수 있습니다.");

		firstBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlurryAgent.logEvent("ReviewTutorialActivity:Click_Show_SecondPage");
				firstLayout.setVisibility(View.INVISIBLE);
				secondLayout.setVisibility(View.VISIBLE);
			}
		});

		secondBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag == false) {

					FlurryAgent.logEvent("ReviewTutorialActivity:Click_Show_ThirdPage");
					secondMsg.setText("복습 관리 모드를 거칠수록 \n"
							+ mPreference.getString(MainValue.GpreName, "이름없음")
							+ "님의\n기억 망각 주기 정보가\n만들어집니다.\n처음엔 다소 부정확하더라도 꾸준한\n학습 부탁드립니다.");
					secondBtn.setText("복습하기");
					secondBtn.setBackgroundColor(Color.parseColor("#e0ab1c"));
					secondBtn.setBackground(getResources().getDrawable(
							R.drawable.orange_btn));
					backgroundLayout.setBackground(getResources().getDrawable(
							R.drawable.info_balloon));
					backgroundLayout.setAlpha(1);
					flag = true;
				} else {
					FlurryAgent.logEvent("ReviewTutorialActivity:Click_Close_ReiviewPage");
					finish();
					overridePendingTransition(0, 0);
				}
			}
		});
	}

	public void onBackPressed() {
		// super.onBackPressed();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MainActivity.notice_layout.setVisibility(View.VISIBLE);
		MainActivity.notice_layout
				.startAnimation(new AnimationUtils().loadAnimation(
						getApplicationContext(), android.R.anim.fade_in));
		Log.d("onDestroy", "yes");
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
		FlurryAgent.logEvent("ReviewTutorialActivity", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("ReviewTutorialActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}

}
