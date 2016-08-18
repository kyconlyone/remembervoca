package com.ihateflyingbugs.hsmd.indicator;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.KakaoLoginActivity;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

public class SampleCirclesSnap extends BaseSampleActivity {

	String TAG = "SampleCirclesSnap";
	private static SharedPreferences mPreference;
	
	static long curPageTime=-1;
	static long prePageTime=-1;
	int preArg0=-1;
	static Map<String, String> pageParams ;
	 
	static String token;

	ProgressDialog mProgress;
	boolean loadingFinished = true;
	AlertDialog.Builder alert;
	static Context mContext;
	static Activity activity;
	
	 Button bt_ok;
	 
	 
	 Date date = new Date();
	 ActivityManager am;

		private boolean isFinish;
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isFinish = false;
		
		if(!Config.isNetworkAvailable(getApplicationContext())){
			FlurryAgent.logEvent("SplashActivity:NotAccessInternet");
			isFinish = true;
			AlertDialog alertDialog = new AlertDialog.Builder(SampleCirclesSnap.this).create();
	        // Setting Dialog Title
	        alertDialog.setTitle("인터넷을 연결할 수 없습니다.");
	 
	        // Setting Dialog Message
	        alertDialog.setMessage("연결 상태를 확인한 후 다시 시도해 주세요.");
	 
	        
	          alertDialog.setIcon(R.drawable.icon36);
	 
	        // Setting OK Button
	        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	finish();
	            }
	        });
	 
	        // Showing Alert Message
	        alertDialog.show();
	        return;
		}
		
		mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
				curPageTime=System.currentTimeMillis();
				try {
					switch(preArg0){
					case -1:
						pageParams.put("FirstPage","0");
						pageParams.put("SecondPage","0");
						pageParams.put("ThirdPage","0");
						pageParams.put("FourthPage","0");
						pageParams.put("FirstPage", ""+ ((Long.valueOf(pageParams.get("FirstPage")) + curPageTime - prePageTime))/1000);
						break;
					case 0:
						pageParams.put("FirstPage", ""+ ((Long.valueOf(pageParams.get("FirstPage")) + curPageTime - prePageTime))/1000);
						break;
					case 1:
						pageParams.put("SecondPage", ""+ ((Long.valueOf(pageParams.get("SecondPage")) + curPageTime - prePageTime))/1000);
						break;
					case 2:
						pageParams.put("ThirdPage", ""+ ((Long.valueOf(pageParams.get("ThirdPage")) + curPageTime - prePageTime))/1000);
						break;
					case 3:
						pageParams.put("FourthPage", ""+ ((Long.valueOf(pageParams.get("FourthPage")) + curPageTime - prePageTime))/1000);
						break;
					default:
						break;
					}
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
				
				//pageParams.put("FirstPage", String.valueOf(System.currentTimeMillis()))
				if(arg0<3){
					bt_ok.setVisibility(View.INVISIBLE);
				}else{
					if(!anim){
						bt_ok.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in));
					anim = true;
					}
					bt_ok.setVisibility(View.VISIBLE);
					bt_next.setText("단어장 선택하기");
				}
				preArg0=arg0;
				prePageTime=curPageTime;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		
	        
	}

	boolean anim = false;
	Button bt_next;
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			if(getIntent().getExtras().getBoolean("EXIT")){
				finish();
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	
		setContentView(R.layout.simple_circles);
		
		
		prePageTime=System.currentTimeMillis();
		
		//MainActivity.writeLog("머신러닝 소개 시작,SampleCirclesSnap,1]\r\n");
		
		Log.e("activitygg", "Sample onCreate");
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		activity = this;
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mContext = getApplicationContext();
		mPager.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		handler = new Handler();
		mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		
		
		
		mPreference.edit().putString(MainActivitys.GpreFirtst, "1").commit();
		
		final CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);
		indicator.setSnap(true);
		
		
		bt_next = (Button)findViewById(R.id.bt_tutorial_login);
		
		bt_ok = (Button)findViewById(R.id.button);
		bt_ok.setVisibility(View.INVISIBLE);
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e(TAG, "bt_ok.setonclick");
				Intent intent = new Intent(SampleCirclesSnap.this, KakaoLoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
		});
		
//		bt_login.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.e("onClickLogin!","onClickLogin!");
//				// TODO Auto-generated method stub
//				if(mPreference.getString(MainActivitys.GpreTutorial,"0").equals("1")){
//		            final Intent intent = new Intent(SampleCirclesSnap.this, MainActivity.class);
//		            startActivity(intent);
//		            finish();
//		    	}else{
//		            final Intent intent = new Intent(SampleCirclesSnap.this, ChoiceTypeActivity.class);
//		            startActivity(intent);
//		            finish();
//		    	}
//			}
//		});
		
		//ImageButton ib_choice = (ImageButton)findViewById(R.id.ib_choice);
		bt_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mPager.getCurrentItem()< 2){
				mPager.setCurrentItem(mPager.getCurrentItem()+1,true);
				bt_ok.setVisibility(View.VISIBLE);

				}else{
					
					bt_ok.setVisibility(View.INVISIBLE);

				}
			}
		});
		
		
	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultcode, Intent intent) {
		// TODO Auto-generated method stub
		if(resultcode!=RESULT_CANCELED){
			setResult(RESULT_OK);
			finish();
		}else if(resultcode==RESULT_CANCELED){
//			switch(requestCode){
//			case SideActivity.Sign_requestCode:
//				finish();
//			}
		}

	}

	/**
	 * ?ㅽ��?��� ?���?��� 寃�怨� 珥�湲�?��? ?���??吏���?��ㅺ�??���???곕��??泥�由�
	 *
	 */
	private void initialize()
	{
		InitializationRunnable init = new InitializationRunnable();
		new Thread(init).start();
	}


	/**
	 * 珥�湲�???��� 泥�由�
	 *
	 */

	class InitializationRunnable implements Runnable
	{
		public void run()
		{
			// ?ш린?��???珥�湲�???��� 泥�由�
			// do_something


			//踰��� ?�蹂대�??���?��� 怨?




		}

	}
	
	
	
	
	
	Map<String, String> articleParams = new HashMap<String, String>();

	
   
    
    
    private void setMySharedPreferences(String _key, String _value) {
		if(mPreference == null){
			mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}  
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mPager.getCurrentItem()> 0){
			mPager.setCurrentItem(mPager.getCurrentItem()-1,true);
		}else{
//			moveTaskToBack(true);
			isFinish = true;
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if(isFinish){
			ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);    

			Intent startMain = new Intent(Intent.ACTION_MAIN); 
			startMain.addCategory(Intent.CATEGORY_HOME); 
			startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(startMain);
			finish();
//						Log.e("activitygg", "first end");
//					}
//				}
//			},15000);
//			finish();
//			Log.e("activitygg", "second end");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		Log.e("activitygg", "=====================================");
		Log.e("alarm", "app is destroy");
	}
	
	public void onPageFinished() {
		
		if(loadingFinished){
			if(null !=mProgress) {
				if(mProgress.isShowing()) {
					mProgress.dismiss();
				}
			}

		}
	}
	
	String startdate;
	String starttime;
	
	static boolean isStopCheck =true;
	
	
	public void onStart()
	{

		super.onStart();

		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		Log.e("activitygg", "Sample onStart");
		pageParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		if(isStopCheck){
			FlurryAgent.logEvent("IntroMachineLearning", pageParams);
		}
		
		// your code
	}
	
	
	public void onStop()
	{
		
		super.onStop();
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    // Do whatever
			pageParams.put("WIFI", "On");
		}else{
			pageParams.put("WIFI", "Off");
		}
		
		Log.e("activitygg", "Sample onStop");
		FlurryAgent.endTimedEvent("IntroMachineLearning");
		pageParams.put("Start", startdate);
		curPageTime=System.currentTimeMillis();
		
		if(isStopCheck){
			try {
				pageParams.put("FourthPage", ""+ ((Long.valueOf(pageParams.get("FourthPage")) + curPageTime - prePageTime))/1000);
				pageParams.put("End", date.get_currentTime());
				pageParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
				//pageParams.clear();
			} catch (NumberFormatException e) {
				// TODO: handle exception
				FlurryAgent.logEvent("IntroMachineLearning:NumberFormatException");
			//	MainActivity.writeLog("[머신러닝 소개 끝,SampleCirclesSnap,,{Start:"+pageParams.get("Start")+",End:"+pageParams.get("End")+"}]\r\n");
			}
		}
		
		ActivityManager am = (ActivityManager) this .getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if(componentInfo.getClassName().equals("com.kakao.sdk.CapriLoggedInActivity")){
			isStopCheck = false;
		}else{
			isStopCheck = true;
		}
		FlurryAgent.onEndSession(this);
		
		// your code
	}

}