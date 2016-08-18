package com.ihateflyingbugs.hsmd.lock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.LockInfo;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.WordListArraySet;
import com.ihateflyingbugs.hsmd.data.WordListCallback;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.analytics.ecommerce.Product;
//import com.google.android.gms.analytics.ecommerce.ProductAction;

public class LockScreenActivity extends Activity{
	final static String TAG = "LockScreenActivity";

	// Word DB
	DBPool mDBPool;
	Handler handler;
	SharedPreferences settings;
	Context mContext;
	DisplayMetrics metrics;

	LinearLayout mLockScreenLayout;

	View mTopView;
	RelativeLayout mTopLayout;
	TextView mTodayTimeTextView;
	TextView mTodayDateTextView;
	TextView mTodayDayTextView;
	ImageView mRechargeBatteryImageView;
	TextView mBatteryTextView;

	AutoResizeTextView mCommentAutoResizeTextView;
	Button mMoreButton;

	LinearLayout mWordLayout;

	RelativeLayout mBottomLayout;
	ImageView mLogoImageView;


	String mTodayTime;
	String mTodayDate;
	String mTodayDay;

	static final int ANIMATION_DURATION = 400;

	int TouchDownX;
	int TouchDownY;
	int TouchUpX;
	int TouchUpY;

	// Word List
	ArrayList<Word> words;

	// Comment
	int mCommentTypeInt = -1;			// 0 : normal, 1 : will, 2 : hit, 3 : memory
	String mBlogUrlString;
	String mCommentString;

	// Timer
	LockTimerTask mTimerTask = new LockTimerTask();
	Timer mTimer = new Timer();

	boolean mReceiverRegister = false;

	//Lock
	KeyguardManager km = null;
	KeyguardManager.KeyguardLock keyLock = null;


	/*
	 *  type_finish
	 *  
	 *  0 : 알수없음
	 *  1 : 더 알아보기
	 *  2 : 더 공부하기
	 *  3 : 드래그
	 *  4 : backpress
	 *
	 */


	int type_finish = 0;
	int during = 0;
	int word_count = 0;
	int type_info = -1;

	String startlockact = "0";

	boolean isButtonPre = false;

//	Tracker mTracker;

	SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VOCAconfig application = (VOCAconfig) getApplication();
//		mTracker = application.getTracker(TrackerName.APP_TRACKER);

		Log.v("checklock", "onCreate");
		type_info = -1;

		mPreferences = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
						| MODE_MULTI_PROCESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		if (km == null)
			km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		if (keyLock == null)
			keyLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);


		setContentView(R.layout.lockscreen);


		startlockact = ""+System.currentTimeMillis();


		mContext = getApplicationContext();
		handler = new Handler();
		mDBPool = DBPool.getInstance(LockScreenActivity.this);
		settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		words = new ArrayList<Word>();
		metrics = new DisplayMetrics();

		if(!mDBPool.isExistStudentId()){
			finish();
			return;
		}



		mLockScreenLayout = (LinearLayout)findViewById(R.id.layout_lockscreen);

		mTopView = (View)findViewById(R.id.view_lockscreen_top);

		mTopLayout = (RelativeLayout)findViewById(R.id.layout_lockscreen_top);
		mTodayTimeTextView = (TextView)findViewById(R.id.text_lockscreen_time);
		mTodayDateTextView = (TextView)findViewById(R.id.text_lockscreen_date);
		mTodayDayTextView = (TextView)findViewById(R.id.text_lockscreen_day);
		mRechargeBatteryImageView = (ImageView)findViewById(R.id.img_lockscreen_recharge);
		mBatteryTextView = (TextView)findViewById(R.id.text_lockscreen_battery);

		mCommentAutoResizeTextView = (AutoResizeTextView)findViewById(R.id.text_lockscreen_comment);
		mCommentAutoResizeTextView.setMinTextSize(10);
		mMoreButton = (Button)findViewById(R.id.btn_lockscreen_more);

		mWordLayout = (LinearLayout)findViewById(R.id.layout_lockscreen_word);
		mBottomLayout = (RelativeLayout)findViewById(R.id.layout_lockscreen_bottom);

		mTopView.setOnTouchListener(touch_listener);
		mTopLayout.setOnTouchListener(touch_listener);
		mBottomLayout.setOnTouchListener(touch_listener);
		mMoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mBlogUrlString.equals("null")) {

					// Build and send an Event.
//					mTracker.send(new HitBuilders.EventBuilder()
//						    .setCategory("Button")
//						    .setAction("click")
//						    .setLabel("more study")
//						    .build());

					type_finish = 2;
					Intent intent = new Intent(getBaseContext(), SplashActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					FlurryAgent.logEvent(TAG + " : OpenApp", true);
					startActivity(intent);
				}
				else {
					// Build and send an Event.
					try {
//						Product relatedProduct =  new Product()
//						.setId(""+type_info+100)
//						.setName("LockScreenInfo")
//						.setCategory("Infomation_Blog")
//						.setBrand(""+mCommentString.substring(8))
//						.setVariant(""+mCommentTypeInt)
//						.setPosition(1);
//
//						// 보고있는 제품
//						Product viewedProduct =  new Product()
//						.setId(""+type_info)
//						.setName("LockScreenInfo")
//						.setCategory("type_info")
//						.setBrand(""+mCommentString.substring(8))
//						.setVariant(""+mCommentTypeInt)
//						.setPosition(1);
//
//						ProductAction productAction = new ProductAction(ProductAction.ACTION_DETAIL);
//						HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
//						.addImpression(relatedProduct, "Related Products")
//						.addProduct(viewedProduct)
//						.setProductAction(productAction);
//
//						Tracker t = ((VOCAconfig)getApplication()).getTracker(
//								TrackerName.APP_TRACKER);
//						t.setScreenName("product");
//						t.send(builder.build());

					} catch (Exception e) {
						// TODO: handle exception
					}

					Uri uri = Uri.parse(mBlogUrlString);

					type_finish = 1;
					Intent intent = new Intent(LockScreenActivity.this, WebViewActivity.class);
					intent.putExtra("uri", mBlogUrlString);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_HISTORY);
					FlurryAgent.logEvent(TAG + " : OpenBlogPage", true);
					startActivity(intent);
				}
				isButtonPre = true;
				finish();
			}
		});


		mCommentString = "";
		mBlogUrlString = "null";
		mCommentTypeInt = 0;

		mMoreButton.setBackgroundResource(R.drawable.lock_mentforapp);
		mMoreButton.setVisibility(View.VISIBLE);

		refresh();

		// Time
		mTimer.schedule(mTimerTask, 0, 1000);
	}

	long start_tracker;

	@Override
	protected void onResume() {
		super.onResume();

//		mTracker.setScreenName("MainWordActivity");
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		start_tracker  = System.currentTimeMillis();


		isButtonPre = false;

		word_count  = mDBPool.getStudyWord();

		Log.v("checklock", "onResume");

		startlockact = ""+System.currentTimeMillis();

		Log.e("KARAM", TAG + "  onResume    ");

		if(!Config.isNetworkAvailable(VOCAconfig.context)){
			mCommentString = "인터넷을 연결할 수 없습니다. \n연결 상태를 확인한 후 다시 시도해 주세요.";
			setComment(mCommentString, mCommentAutoResizeTextView);

			Intent intent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			int BatteryInt = getBatteryPercentage(this, intent);
			mBatteryTextView.setText(BatteryInt+"%");
			mMoreButton.setVisibility(View.GONE);

			return;
		}
		else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					Log.e("type_info", "settingcomment : " + mCommentAutoResizeTextView.getText().length());
					if(mCommentAutoResizeTextView.getText().length() == 0){
						SettingComment();
					}
				}
			}, 1000);
		}

		// Battery
		if(!mReceiverRegister) {
			IntentFilter mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
			registerReceiver(mBatteryReceiver, mIntentFilter);
			mReceiverRegister = true;
		}
	}

	BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int BatteryInt = getBatteryPercentage(mContext, intent);
				int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);

				mBatteryTextView.setText(BatteryInt + "%");

				if(status == BatteryManager.BATTERY_STATUS_CHARGING) {
					mRechargeBatteryImageView.setVisibility(View.VISIBLE);
				}
				else {
					mRechargeBatteryImageView.setVisibility(View.INVISIBLE);
				}
			}
		}
	};

	int getBatteryPercentage(Context context, Intent intent) {
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);

		float batteryPct = level / (float)scale;
		return (int)(batteryPct * 100);
	}


	class LockTimerTask extends TimerTask {
		public void run() {
			handler.post(mUpdateTimeTask);
		}
	}

	private Runnable mUpdateTimeTask = new Runnable() {

		@Override
		public void run() {
			Date kor_date = new Date();

			SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm");
			SimpleDateFormat DateFormat = new SimpleDateFormat("M. d", Locale.KOREA);
			SimpleDateFormat DayFormat = new SimpleDateFormat("EEE.", Locale.US);

			mTodayTime = TimeFormat.format(kor_date);
			mTodayDate = DateFormat.format(kor_date);
			mTodayDay = DayFormat.format(kor_date);

			mTodayTimeTextView.setText(mTodayTime);
			mTodayDateTextView.setText(mTodayDate);
			mTodayDayTextView.setText(mTodayDay);
		}
	};



	OnTouchListener touch_listener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					TouchDownX = (int)event.getX();
					TouchDownY = (int)event.getY();

				} break;

				case MotionEvent.ACTION_UP: {
					TouchUpX = (int)event.getX();
					TouchUpY = (int)event.getY();

					float x = TouchDownX - TouchUpX;
					float y = TouchDownY - TouchUpY;

					double mSpacing =  Math.sqrt(x * x + y * y);

					if(70 < mSpacing) {
						FlurryAgent.logEvent(TAG + " : Unlock LockScreen", true);
						type_finish = 3;
						finish();
					}

				} break;
			}
			return true;
		}
	};





	void SettingComment() {
		try {

			new RetrofitService().getUseAppInfoService().retroGetLockScreenComment()
					.enqueue(new Callback<UseAppInfoData>() {
						@Override
						public void onResponse(final Response<UseAppInfoData> response, Retrofit retrofit) {
							handler.post(new Runnable() {

								@Override
								public void run() {
									type_info = response.body().getInfo_type();
									mBlogUrlString = response.body().getBlog_url();
									mCommentString = response.body().getLock_screen_comment();
									mCommentTypeInt = response.body().getComment_type();	//KARAM 8.6.		0 : normal, 1 : will, 2 : hit, 3 : memory

									Log.e("GetLockScreenComment", "Lock Screen : " + type_info + "  " + mBlogUrlString + "  " + mCommentString + "  " + mCommentTypeInt);
									//mCommentString = "밀당영단어는 개개인의 기억망각주기를 측정하여 까먹기 전에 푸쉬와 잠금화면을 통해 보여드립니다. 기억력이 모두 다르듯이 복습해야 할 시간도 달라져야 합니다.";
									setComment(mCommentString, mCommentAutoResizeTextView);

									if(mBlogUrlString.equals("null")){
										mMoreButton.setBackgroundResource(R.drawable.lock_mentforapp);
									}else{
										mMoreButton.setBackgroundResource(R.drawable.lock_mentforblog);
									}

									switch(mCommentTypeInt) {
										case 0:
											mLockScreenLayout.setBackgroundResource(R.drawable.bg_lockscreen04); break;
										case 1:
											mLockScreenLayout.setBackgroundResource(R.drawable.bg_lockscreen01); break;
										case 2:
											mLockScreenLayout.setBackgroundResource(R.drawable.bg_lockscreen02); break;
										case 3:
											mLockScreenLayout.setBackgroundResource(R.drawable.bg_lockscreen03); break;
									}

								}
							});
						}

						@Override
						public void onFailure(Throwable t) {
							mBlogUrlString = "null";
							Log.e("GetLockScreenComment", "onFailure : "+t.toString());
						}
					});

		} catch(NullPointerException e) {
			mBlogUrlString = "null";
		}
	}

	public void setComment(String comment, TextView tv){
		String replace_string = comment.replaceAll("xxx", settings.getString(MainValue.GpreName, "이름없음"));
		tv.setText(replace_string);
	}



	private void refresh() {
		// Network Connect Error
		if(!Config.isNetworkAvailable(VOCAconfig.context)){
			mCommentString = "인터넷을 연결할 수 없습니다. \n연결 상태를 확인한 후 다시 시도해 주세요.";
			setComment(mCommentString, mCommentAutoResizeTextView);

			mMoreButton.setVisibility(View.GONE);
		}
		words = mDBPool.WordArrayOneSet_LockScreen(this);

		if(words.size() == 0) {
			// 단어를 모두 외움!!
		}
		else
			printList();
	}

	WordListArraySet mWordListAdapter;

	private void printList() {
		for(int i=0; i<words.size(); i++) {
			String mSpecialRate = words.get(i).getmSpecialRate();

			if(mSpecialRate == null) {
				words.get(i).setmSpecialRate(mDBPool.getSpecialRate(words.get(i).get_id()));
			}
		}

		mWordListAdapter = new WordListArraySet(this, mContext, words, metrics, mWordLayout, new WordListCallback() {

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
	com.ihateflyingbugs.hsmd.data.Date dates = new com.ihateflyingbugs.hsmd.data.Date();

	Map<String, String> articleParams ;

	public void onStart()
	{
		super.onStart();

		Log.v("checklock", "onStart");

		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("LockScreenActivity", articleParams);

	}

	@Override
	protected void onStop() {

		super.onStop();


		Log.v("checklock", "onStop");
		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e("splash", startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");

		/*
		 * com.android.systemui//홈버튼 길게 com.buzzpia.aqua.launcher
		 * com.pantech.launcher2.Launcher
		 * com.kakao.talk.activity.chat.ChatRoomActivity 초대시에는
		 * com.kakao.talk.activity.TaskRootActivity
		 */

		//When press homebutton, start lockactivity
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if(!isButtonPre){
			if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd")) {
				for (int i = 0; i < taskInfo.size(); i++) {
					finish();
				}
			}
		}else{

		}

	}




	@Override
	public void onBackPressed() {
		super.onBackPressed();
		type_finish = 4;
		finish();
	}


	int tag = 0;

	@Override
	public void finish() {

		if(!isButtonPre){
			moveTaskToBack(true);
		}
		super.finish();
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("lockinfo", "onDestroy");

//		mTracker.send(new HitBuilders.TimingBuilder()
//	    .setCategory("UseTime")
//	    .setValue(System.currentTimeMillis()- start_tracker)
//	    .setVariable("LockScreen")
//	    .setLabel("AllTime")
//	    .build());



		during = (int) (((Long.valueOf(System.currentTimeMillis())-Long.valueOf(startlockact)))/1000);
		if(mDBPool.getStudyWord() - word_count>0){
			word_count = mDBPool.getStudyWord() - word_count;
		}else{
			word_count=0;
		}

		if(during>=1){
			mDBPool.makeLockinfoTable(during, word_count, type_info, type_finish);
			Log.e("lockinfo", "INPUT : " + during+ "  "+ word_count + "  "+ type_info + "  "+ type_finish);

		}else{
			Log.e("lockinfo", "else");
		}

		if(mDBPool.getLockInfo().size()>0){

			JsonArray myCustomArray = null;

			ArrayList<LockInfo> list = mDBPool.getLockInfo();
			Gson gson = new GsonBuilder().create();
			myCustomArray = gson.toJsonTree(list).getAsJsonArray();

			String lockinfo = myCustomArray.toString();
			Log.e("lockinfo", lockinfo);

			new RetrofitService().getUseAppInfoService().retroInsertUseLockScreenInfo(mDBPool.getStudentId(),
																						lockinfo)
					.enqueue(new Callback<UseAppInfoData>() {
						@Override
						public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
							Log.e("lockinfo", "Resonponse");
							mDBPool.deleteLockInfo();
						}

						@Override
						public void onFailure(Throwable t) {
							Log.e("lockinfo", "onFailure : "+ t.toString());
						}
					});
		}else{

			Log.e("lockinfo", "data is not exist");
		}


		tag++;
		Log.e("finish_check", ""+tag);
		Log.v("finish_check", ""+startlockact+ "  "+ during);


		word_count = 0 ;
		during = 0;
		type_info = -1;
		type_finish = 0;
		startlockact = "0";


		Log.v("checklock", "onDestroy");
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.v("checklock", "onPause");


		km.exitKeyguardSecurely(new OnKeyguardExitResult() {

			@Override
			public void onKeyguardExitResult(boolean success) {
				keyLock.reenableKeyguard();
			}
		});

		if(mReceiverRegister){
			unregisterReceiver(mBatteryReceiver);
			mReceiverRegister = false;
		}
	}



}




