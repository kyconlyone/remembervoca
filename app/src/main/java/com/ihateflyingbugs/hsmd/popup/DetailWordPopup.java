package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.WordPopupAdapter;
import com.ihateflyingbugs.hsmd.WordPopupBody;
import com.ihateflyingbugs.hsmd.WordPopupHeader;
import com.ihateflyingbugs.hsmd.WordPopupItem;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailWordPopup extends Activity {
	String TAG = "WordPopUp";


	DBPool mDBPool;
	SharedPreferences settings;

	RelativeLayout mOutsideLayout;

	ImageButton mWordReport_ImageButton;

	TextView mWordTitleTextView;
	ImageView mWordImageView;

	TextView mReviewWordTextView;

	RelativeLayout mSpecialLayout;
	TextView mSpecialRateTextView;
	RelativeLayout mMemoryLayout;
	TextView mMemoryRateTextView;
	RelativeLayout mTargetLayout;
	TextView mTargetRateTextView;
	TextView mHitCountTextView;

	LinearLayout mHitInfoLayout;
	ToggleButton mHitInfoToggleButton;
	ListView mHitInfoListView;


	int mWordCode;
	Word mWord;

	String mTitleWordString;
	boolean bSpecialWord;
	int mWordStateInt;
	boolean bFirstWord;

	int mSpecialRateInt;
	int mMemoryRateInt;
	int mTargetRateInt;

	int ebsCount = 0; // EBS 출제횟수
	int kiceCount = 0; // 모의고사 출제 횟수
	int sooneungCount = 0; // 수능 출제횟수
	int mHitcountInt;

	String mHitInfoStringArray[][];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("KARAM", TAG + "  onCreate");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.activity_word_popup);



		mDBPool = DBPool.getInstance(this);
		//settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);


		// Init
		mOutsideLayout = (RelativeLayout)findViewById(R.id.background);

		mWordReport_ImageButton = (ImageButton)findViewById(R.id.btn_detailwordpopup_report);

		mWordTitleTextView = (TextView)findViewById(R.id.text_detailwordpopup_word);
		mWordImageView = (ImageView)findViewById(R.id.img_detailwordpopup_word);

		mReviewWordTextView = (TextView)findViewById(R.id.text_detailwordpopup_firstmemory);

		mSpecialLayout = (RelativeLayout)findViewById(R.id.layout_detailwordpopup_special);
		mSpecialRateTextView = (TextView)findViewById(R.id.text_detailwordpopup_specialrate);
		mMemoryLayout = (RelativeLayout)findViewById(R.id.layout_detailwordpopup_memory);
		mMemoryRateTextView = (TextView)findViewById(R.id.text_detailwordpopup_memoryrate);
		mTargetLayout = (RelativeLayout)findViewById(R.id.layout_detailwordpopup_target);
		mTargetRateTextView = (TextView)findViewById(R.id.text_detailwordpopup_targetrate);
		mHitCountTextView = (TextView)findViewById(R.id.text_detailwordpopup_hitcount);

		mHitInfoLayout = (LinearLayout)findViewById(R.id.layout_detailwordpopup_hitinfo);
		mHitInfoToggleButton = (ToggleButton)findViewById(R.id.toggle_detailwordpopup_hitinfo);
		mHitInfoListView = (ListView) findViewById(R.id.list_detailwordpopup_hitinfo);


		// TODO
		mWordCode = getIntent().getIntExtra("wordcode", 0);
		mWord = mDBPool.getWord(mWordCode);


		mWordStateInt = mWord.getState();
		String mSpecialRateString = mWord.getmSpecialRate();
		if(mSpecialRateString != null)
			bSpecialWord = true;
		else
			bSpecialWord = false;

		mTitleWordString = mWord.getWord();
		if(bSpecialWord) {	// 스페셜 단어
			mWordTitleTextView.setBackgroundResource(R.drawable.bg_wordpopup_flag);
			mWordTitleTextView.setTextColor(Color.parseColor("#FFFFFF"));
			mSpecialLayout.setVisibility(View.VISIBLE);
		}
		else {
			mWordTitleTextView.setBackground(null);
			mWordTitleTextView.setTextColor(Color.parseColor("#2f6e9b"));
			mSpecialLayout.setVisibility(View.GONE);
		}
		mWordTitleTextView.setText(mTitleWordString);


		if(mWordStateInt == 0) {
			mWordImageView.setVisibility(View.GONE);
			mReviewWordTextView.setText("처음 등장한 단어");
			mMemoryLayout.setVisibility(View.GONE);
		}
		else {
			if(mWordStateInt == -1) {
				mWordImageView.setBackgroundResource(R.drawable.img_wordpopup_forget);
				mReviewWordTextView.setText("암기가 필요한 단어");
				mMemoryLayout.setVisibility(View.GONE);
			}
			else {
				String mWordImageString = "img_wordpopup_" + mWordStateInt;
				int mWordImageResourceIdInt = getResources().getIdentifier(mWordImageString, "drawable", getPackageName());
				mWordImageView.setBackgroundResource(mWordImageResourceIdInt);
				mReviewWordTextView.setText("복습 레벨 " + mWordStateInt + "단계");
				mMemoryLayout.setVisibility(View.VISIBLE);
			}
		}


		//TODO
		if(mSpecialRateString != null) {
			mSpecialRateString = mSpecialRateString.replace("%", "");
			mSpecialRateTextView.setText(mSpecialRateString);
		}


		double mMemoryRateDouble = mWord.getScore();
		Log.e("KARAM", "Score : " + mMemoryRateDouble);
		if(0 < mWordStateInt) {
			mMemoryRateDouble = 1 - mDBPool.getScoreCriterion() + mMemoryRateDouble;
			mMemoryRateDouble = mMemoryRateDouble * 100;

			String mMemoryRateString = String.format("%.1f", mMemoryRateDouble);

			if(mMemoryRateString.equals("100.0"))
				mMemoryRateString = String.format("%.0f", mMemoryRateDouble);

			mMemoryRateTextView.setText(mMemoryRateString);
		}

		double mTargetRateDouble = Config.setting_rate;
		String mTargetRateString = String.format("%.1f", mTargetRateDouble);

		if(mTargetRateString.equals("100.0"))
			mTargetRateString = String.format("%.0f", mTargetRateDouble);

		mTargetRateTextView.setText(mTargetRateString);



		for (int i = 0; i < Config.category.length; i++) {
			if (Config.cat2[i].equals("EBS")) {
				ebsCount += Config.sum[i];

			} else if (Config.cat2[i].equals("수능")) {
				sooneungCount += Config.sum[i];

			} else if (Config.cat2[i].equals("평가원")) {
				kiceCount += Config.sum[i];

			}
		}
		mHitcountInt = ebsCount + sooneungCount + kiceCount;
		mHitCountTextView.setText(mHitcountInt + "회 출제");


		mHitInfoToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mHitInfoListView.setVisibility(View.VISIBLE);
					mHitInfoLayout.setBackground(getResources().getDrawable(R.drawable.bg_wordpopup_bottom_opened));
					mHitInfoToggleButton.setBackground(getResources().getDrawable(R.drawable.onbutton));

					FlurryAgent.logEvent("ShowWordInfoPopup:Click_Show_Detail");
				} else {
					mHitInfoListView.setVisibility(View.GONE);
					mHitInfoLayout.setBackground(getResources().getDrawable(R.drawable.bg_wordpopup_bottom));
					mHitInfoToggleButton.setBackground(getResources().getDrawable(R.drawable.offbutton));

					FlurryAgent.logEvent("ShowWordInfoPopup:Click_Close_Detail");
				}
			}
		});


		HitInfoListViewSetting();


		mWordReport_ImageButton.setOnClickListener(ClickListener);
		mWordImageView.setOnClickListener(ClickListener);
	}

	OnClickListener ClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.btn_detailwordpopup_report: {
					Intent intent = new Intent(DetailWordPopup.this, DetailWord_ReportPopup.class);
					intent.putExtra("wordcode", mWordCode);
					startActivity(intent);

					DetailWordPopup.this.finish();
				} break;

				case R.id.img_detailwordpopup_word: {/*
				if(1 <= mWordStateInt) {
//					Intent intent = new Intent(DetailWordPopup.this, DetailWord_MoreDetailPopup.class);
//					intent.putExtra("wordcode", mWordCode);
//					startActivity(intent);
//
//					DetailWordPopup.this.finish();
				}*/
				} break;
			}
		}
	};





	void HitInfoListViewSetting() {
		List<WordPopupItem> items = new ArrayList<WordPopupItem>();

		mHitInfoStringArray = new String[Config.category.length][4];

		//	for(int i=Config.category.length-1; 0<=i; i--) {
		for (int i = 0; i < Config.category.length; i++) {
			mHitInfoStringArray[i][0] = Config.cat1[i];
			mHitInfoStringArray[i][1] = Config.cat2[i] + " " + Config.cat2_sub[i];
			mHitInfoStringArray[i][2] = Integer.toString(Config.sum[i]);
			mHitInfoStringArray[i][3] = "no";
		}


		for(int i=mHitInfoStringArray.length-1; 0<=i; i--) {
			//for (int i = 0; i < mHitInfoStringArray.length; i++) {
			if (mHitInfoStringArray[i][3].equals("no")) {
				items.add(new WordPopupHeader(mHitInfoStringArray[i][0].split("\\(")[0]+"학년도", mHitInfoStringArray[i][1], mHitInfoStringArray[i][2]));
				mHitInfoStringArray[i][3] = "check";

				for (int j = 0; j < mHitInfoStringArray.length; j++) {
					if (mHitInfoStringArray[i][0].equals(mHitInfoStringArray[j][0]) && mHitInfoStringArray[j][3].equals("no")) {

						mHitInfoStringArray[j][0] = null;
						mHitInfoStringArray[j][3] = "check";
						items.add(new WordPopupBody(mHitInfoStringArray[j][1], mHitInfoStringArray[j][2]));
					}
				}
			}
		}

		WordPopupAdapter wAdapter = new WordPopupAdapter(getApplicationContext(), items);
		mHitInfoListView.setAdapter(wAdapter);
	}


	String starttime;
	String startdate;
	Date date = new com.ihateflyingbugs.hsmd.data.Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("ShowWordInfoPopup", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
	}
}
