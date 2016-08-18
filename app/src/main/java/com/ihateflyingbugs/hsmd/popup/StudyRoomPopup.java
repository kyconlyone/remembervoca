package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;

import java.util.ArrayList;
import java.util.List;

public class StudyRoomPopup extends Activity implements OnClickListener {


	DBPool db;

	Handler handler;

	boolean isFreeState = false;
	boolean isAfter= false;

	LinearLayout ll_searchsr_1;
	LinearLayout ll_searchsr_2;

	JsonArray myCustomArray;

	WifiManager wifi;
	int size = 0;
	List<ScanResult> results;
	ArrayList<String> arraylist = new ArrayList<String>();
	String ITEM_KEY = "key";

	BroadcastReceiver br;

	ProgressBar mProgressDialog;

	SharedPreferences mPreference;

	ImageView iv_searchsr;

	Button bt_searchsr_turnon;

	TextView tv_searchsr_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_studyroom);

		mPreference  = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		handler = new Handler();

		db= DBPool.getInstance(getApplicationContext());


		ll_searchsr_1= (LinearLayout)findViewById(R.id.ll_searchsr_1);
		ll_searchsr_2= (LinearLayout)findViewById(R.id.ll_searchsr_2);
		iv_searchsr = (ImageView)findViewById(R.id.iv_searchsr);

		tv_searchsr_title = (TextView)findViewById(R.id.tv_searchsr_title);
		bt_searchsr_turnon = (Button)findViewById(R.id.bt_searchsr_turnon);

		mProgressDialog = (ProgressBar)findViewById(R.id.progress_studyroom_search);

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (wifi.isWifiEnabled() == false)
		{
			bt_searchsr_turnon.setBackgroundResource(R.drawable.willqwest_confirm_btn);
			iv_searchsr.setImageResource(R.drawable.img_studyroom_wifi);
			mProgressDialog.setVisibility(View.GONE);
			bt_searchsr_turnon.setClickable(true);
			tv_searchsr_title.setText("가맹 독서실 WIFI 접속시\n밀당영단어를 무료로 이용하실 수 있습니다.");
		}else{
			bt_searchsr_turnon.setClickable(false);
			bt_searchsr_turnon.setBackgroundResource(R.drawable.willqwest_confirm_btn_disable);
			iv_searchsr.setImageResource(R.drawable.img_studyroom_progressbar);
			mProgressDialog.setVisibility(View.VISIBLE);
			tv_searchsr_title.setText("밀당영단어 가맹점 여부를 확인중입니다.");
			bt_searchsr_turnon.setText("다시 시도");

		}

		br = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context c, Intent intent)
			{
				try {
					unregisterReceiver(br);
					results = wifi.getScanResults();
					size = results.size();
				} catch (RuntimeException e) {
					// TODO: handle exception
					this.clearAbortBroadcast();
				}

			}
		};

		registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		if (wifi.isWifiEnabled() != false)
		{
			wifi.startScan();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					check_studyroom();
				}
			}, 5000);
		}



		Button bt_searchsr_redo = (Button)findViewById(R.id.bt_searchsr_redo);
		Button bt_searchsr_undo = (Button)findViewById(R.id.bt_searchsr_undo);
		Button bt_searchsr_cancel = (Button)findViewById(R.id.bt_searchsr_cancel);

		bt_searchsr_redo.setOnClickListener(this);
		bt_searchsr_undo.setOnClickListener(this);
		bt_searchsr_cancel.setOnClickListener(this);
		bt_searchsr_turnon.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

			case R.id.bt_searchsr_redo:
				ll_searchsr_1.setVisibility(View.VISIBLE);
				ll_searchsr_2.setVisibility(View.GONE);
				registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				wifi.startScan();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						check_studyroom();
					}
				}, 5000);
				break;
			case R.id.bt_searchsr_undo:
				finish();
				break;
			case R.id.bt_searchsr_cancel:
				finish();
				break;

			case R.id.bt_searchsr_turnon:
				bt_searchsr_turnon.setBackgroundResource(R.drawable.willqwest_confirm_btn_disable);
				iv_searchsr.setImageResource(R.drawable.img_studyroom_progressbar);
				mProgressDialog.setVisibility(View.VISIBLE);
				wifi.setWifiEnabled(true);
				bt_searchsr_turnon.setClickable(false);
				tv_searchsr_title.setText("밀당영단어 가맹점 여부를 확인중입니다.");

				wifi.startScan();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						check_studyroom();
					}
				}, 5000);
				break;

			default:

				break;
		}

	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.d("SERVISESEES", service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}

	public boolean isStudyroom(){
		myCustomArray = new JsonArray();
		try
		{
			size = size - 1;
			while (size >= 0)
			{
				int i = results.get(size).level;
				arraylist.add(""+results.get(size).BSSID);
				size--;
			}
			Gson gson = new GsonBuilder().create();
			myCustomArray = gson.toJsonTree(arraylist).getAsJsonArray();
		}
		catch (Exception e)
		{
			Log.e("getwifi", "error : "+e.toString());
		}

		Log.e("getwifi", "wifijson : "+myCustomArray.toString());
		if(myCustomArray.size()!=0){
			arraylist.clear();
			return true;
		}else{
			return false;
		}
	}

	public void check_studyroom(){
		if(isStudyroom()){
//			new Async_check_studyroom(db.getStudentId(), myCustomArray.toString(), new VocaCallback() {
//
//				@Override
//				public void Resonponse(JSONObject response) {
//					// TODO Auto-generated method stub
//					mPreference.edit().putBoolean("studyroom", true).commit();
//					Calendar cal = Calendar.getInstance();
//					Intent intent2 = new Intent(StudyRoomPopup.this, CheckStudyRoomService.class);
//					intent2.setFlags(intent2.FLAG_ACTIVITY_NEW_TASK|intent2.FLAG_ACTIVITY_CLEAR_TOP);
//					PendingIntent pintent2 = PendingIntent.getService(StudyRoomPopup.this, 0, intent2, 0);
//					AlarmManager alarm2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//					alarm2.cancel(pintent2);
//					stopService(intent2);
//					alarm2.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), (AlarmManager.INTERVAL_HOUR)/2, pintent2);
//
//					setResult(22222);
//					finish();
//				}
//
//				@Override
//				public void Exception() {
//					// TODO Auto-generated method stub
//					handler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							mPreference.edit().putBoolean("studyroom", false).commit();
//							ll_searchsr_1.setVisibility(View.GONE);
//							ll_searchsr_2.setVisibility(View.VISIBLE);
//						}
//					});
//				}
//			}).execute();
		}else{
			ll_searchsr_1.setVisibility(View.GONE);
			ll_searchsr_2.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}


}
