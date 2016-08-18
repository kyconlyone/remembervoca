package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.Word;

import java.util.HashMap;
import java.util.Map;

public class DetailWord_MoreDetailPopup extends Activity {
	String TAG = "WordPopUp";


	DBPool mDBPool;
	SharedPreferences settings;

	TextView mReviewLevel_TextView;
	TextView mContent_TextView;
	ImageButton mReviewLock_Button;
	Button mClose_Button;


	int mWordCode;
	Word mWord;

	int mReviewLevel;

	boolean bReviewLock;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("KARAM", TAG + "  onCreate");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.popup_detailword_moredetail);


		mDBPool = DBPool.getInstance(this);
		//settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);


		// Init
		mReviewLevel_TextView = (TextView)findViewById(R.id.text_detailwordpopup_more_reviewlevel);
		mContent_TextView = (TextView)findViewById(R.id.text_detailwordpopup_more_content);
		mReviewLock_Button = (ImageButton)findViewById(R.id.btn_detailwordpopup_more_reviewlock);
		mClose_Button = (Button)findViewById(R.id.btn_detailwordpopup_more_close);

		//
		mWordCode = getIntent().getIntExtra("wordcode", 0);
		mWord = mDBPool.getWord(mWordCode);

		mReviewLevel = mWord.getState();
		mReviewLevel_TextView.setText(Integer.toString(mReviewLevel));

		//TODO 복습lock인지 아닌지 확인후~
		if((mReviewLevel%2)==0) {
			bReviewLock = true;
			mReviewLock_Button.setBackgroundResource(R.drawable.img_state_10);
		}
		else {
			bReviewLock = false;
			mReviewLock_Button.setBackgroundResource(R.drawable.img_state_10_color);
		}


		mReviewLock_Button.setOnClickListener(ClickListener);
		mClose_Button.setOnClickListener(ClickListener);
	}

	OnClickListener ClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.btn_detailwordpopup_more_reviewlock: {
					if(bReviewLock) {  // lock -> unlock
						mReviewLock_Button.setBackgroundResource(R.drawable.img_state_10_color);
						Toast.makeText(DetailWord_MoreDetailPopup.this, "다시 복습이 진행됩니다", Toast.LENGTH_SHORT).show();
						bReviewLock = false;
					}
					else {	// unlock -> lock
						mReviewLock_Button.setBackgroundResource(R.drawable.img_state_10);
						Toast.makeText(DetailWord_MoreDetailPopup.this, "해당 단어는 더이상 복습되지 않습니다", Toast.LENGTH_SHORT).show();
						bReviewLock = true;
					}
				} break;

				case R.id.btn_detailwordpopup_more_close: {
					DetailWord_MoreDetailPopup.this.finish();
				} break;
			}
		}
	};



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
