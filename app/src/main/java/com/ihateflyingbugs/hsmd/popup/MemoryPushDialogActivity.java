package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.WordListArraySet;
import com.ihateflyingbugs.hsmd.data.WordListCallback;
import com.ihateflyingbugs.hsmd.tutorial.SplashActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryPushDialogActivity extends Activity {
	final String TAG = "MemoryPushDialogActivity";

	DBPool db;
	Handler handler;
	SharedPreferences settings;

	RelativeLayout mPushIconLayout;
	TextView mWordCountTextView;
	LinearLayout mCommentLayout;
	TextView mCommentTextView;

	LinearLayout mWordLayout;
	PullToRefreshListView mPullToRefreshListView;
	LinearLayout mFinishLayout;

	Button mGoAppButton;




	static final int ANIMATION_DURATION = 400;

	int TouchDownX;
	int TouchDownY;
	int TouchUpX;
	int TouchUpY;


	// Comment
	Intent intent;
	String mCommentString;
	ArrayList<String> mCommentsArray;
	String mSystemNameString;

	// Word List
	ArrayList<Word> words;
	int mWordCountInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.activity_memorypush_dialog);


		handler = new Handler();
		db = DBPool.getInstance(MemoryPushDialogActivity.this);
		settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

		mPushIconLayout = (RelativeLayout)findViewById(R.id.layout_memorypush_dialog_icon);
		mWordCountTextView = (TextView)findViewById(R.id.text_memorypush_dialog_wordcount);
		mCommentLayout = (LinearLayout)findViewById(R.id.layout_memorypush_dialog_comment);
		mCommentTextView = (TextView)findViewById(R.id.text_memorypush_dialog_comment);
		mWordLayout = (LinearLayout)findViewById(R.id.layout_memorypush_dialog_word);
		mFinishLayout = (LinearLayout)findViewById(R.id.layout_memorypush_dialog_finish);
		mGoAppButton = (Button)findViewById(R.id.btn_memorypush_dialog_goapp);


		mWordCountInt = db.getMforget();
		mWordCountTextView.setText(Integer.toString(mWordCountInt));

		intent = getIntent();
		mCommentString = intent.getExtras().getString("memorypush_comment");

		if(mCommentString != null)
			mCommentTextView.setText(mCommentString);
		else {
			mSystemNameString = settings.getString(MainValue.GpreSystemName, "Riche");
			mCommentsArray = new ArrayList<String>();
			mCommentsArray.add("'" + mSystemNameString + "' 입니다.\n지금 복습해야 할 단어를 알려드릴께요~");
			mCommentsArray.add("지금 외우면 까먹을수가 없어요");
			mCommentsArray.add("지금이 복습에 최적의 시간이에요");
			mCommentsArray.add("제가 도망가는 단어들을 잡아왔어요");

			Collections.shuffle(mCommentsArray);
			mCommentString = mCommentsArray.get(0);
			mCommentTextView.setText(mCommentString);

			Log.e("KARAM", "Comment : " + mCommentsArray.get(0));
			Log.e("KARAM", "Comment : " + mCommentsArray.get(1));
			Log.e("KARAM", "Comment : " + mCommentsArray.get(2));
			Log.e("KARAM", "Comment : " + mCommentsArray.get(3));
		}


		mCommentLayout.setVisibility(View.VISIBLE);
		mWordLayout.setVisibility(View.VISIBLE);
		mFinishLayout.setVisibility(View.GONE);


		mPushIconLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("KARAM", TAG + " Icon Click !!");

				Intent service_intent = new Intent("com.ihateflyingbugs.hsmd.service.ChatHeadService");
				intent.setPackage("com.ihateflyingbugs.hsmd.service");
				startService(service_intent);

				finish();
			}
		});


		//mFinishLayout.setOnTouchListener(touch_listener);
		/*
		mCommentTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mWordCountTextView.setTextColor(Color.GREEN);
				mWordLayout.setVisibility(View.GONE);
				mFinishLayout.setVisibility(View.VISIBLE);
				mGoAppButton.setTextColor(Color.YELLOW);
			}
		});
		 */

		mGoAppButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("KARAM", TAG + "  GoApp Button");
				Intent intent = new Intent(getBaseContext(), SplashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("pushresponse", true);
				startActivity(intent);
			}
		});


		words = new ArrayList<Word>();
		words.clear();
	}







	@Override
	protected void onResume() {
		super.onResume();
		if(words.size()==0){
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					refresh();
				}
			}, 200);
		}
	}


	OnTouchListener touch_listener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					TouchDownX = (int)event.getX();
					TouchDownY = (int)event.getY();

				} break;

				case MotionEvent.ACTION_UP: {
					Log.e("KARAM", TAG + " Touch");
					TouchUpX = (int)event.getX();
					TouchUpY = (int)event.getY();

					float x = TouchDownX - TouchUpX;
					float y = TouchDownY - TouchUpY;

					double mSpacing =  Math.sqrt(x * x + y * y);

					if(70 < mSpacing)
						finish();

				} break;
			}
			return true;
		}
	};


	void refresh() {
		// Network Connect Error
		if(!Config.isNetworkAvailable(VOCAconfig.context)){
			AlertDialog alertDialog = new AlertDialog.Builder(MemoryPushDialogActivity.this).create();
			alertDialog.setTitle("인터넷을 연결할 수 없습니다.");
			alertDialog.setMessage("연결 상태를 확인한 후 다시 시도해 주세요.");
			alertDialog.setIcon(R.drawable.icon36);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
			return;
		}
		else {	// Network Connect
			words.clear();

			words = db.wordsWithScoreInMPopup();	//복습 단어 불러오기

			int wordsCount = words.size();
			int restCount = Config.ONCE_WORD_COUNT - wordsCount;


			while (words.size() >  Config.ONCE_WORD_COUNT) {
				words.remove(words.size() - 1);
			}

			int i = 1;
			for (Word word : words) {
				words.get(i - 1).setExState(words.get(i - 1).getState());
				i++;
			}

			printList();
		}
	}


	DisplayMetrics metrics;
	WordListArraySet mWordListAdapter;

	private void printList() {
		for(int i=0; i<words.size(); i++) {
			String mSpecialRate = words.get(i).getmSpecialRate();

			if(mSpecialRate == null) {
				words.get(i).setmSpecialRate(db.getSpecialRate(words.get(i).get_id()));
			}
		}

		mWordListAdapter = new WordListArraySet(this, this.getApplicationContext(), words, metrics, mWordLayout, new WordListCallback() {

			@Override
			public void refreshWordList() {
				Log.e("KARAM", TAG + "  Callback Refresh()");
				refresh();
			}
		});
		mWordListAdapter.setInit();
	}


	String starttime;
	String startdate;
	Date dates = new com.ihateflyingbugs.hsmd.data.Date();
	Map<String, String> articleParams ;
	public void onStart()
	{

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("LockScreenActivity", articleParams);

	}

	@Override
	protected void onStop() {
		super.onStop();

		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e("splash", startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);

		/*
		 * com.android.systemui//홈버튼 길게 com.buzzpia.aqua.launcher
		 * com.pantech.launcher2.Launcher
		 * com.kakao.talk.activity.chat.ChatRoomActivity 초대시에는
		 * com.kakao.talk.activity.TaskRootActivity
		 */

		if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd")) {
			for (int i = 0; i < taskInfo.size(); i++) {
				finish();
			}
		}
	}



	@Override
	protected void onPause() {
		super.onPause();

		KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock keyLock = km.newKeyguardLock(KEYGUARD_SERVICE);
		keyLock.disableKeyguard();
	}

}
