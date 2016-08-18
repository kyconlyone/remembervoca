package com.ihateflyingbugs.hsmd.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;

import java.util.ArrayList;
import java.util.List;

public class CheckStudyRoomService extends Service {

	Date date;
	DBPool db;
	public static SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		db = DBPool.getInstance(getApplicationContext());
	}

	WifiManager wifi;
	int size = 0;
	List<ScanResult> results;
	ArrayList<String> arraylist = new ArrayList<String>();
	String ITEM_KEY = "key";

	JsonArray myCustomArray;

	final int MAX_COUNT=3;
	int count = 0;
	BroadcastReceiver br;
	Handler handler;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mPreference= getApplicationContext().getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		Log.e("servicewifi", "start");
		handler = new Handler();
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (wifi.isWifiEnabled() == false)
		{
			stopService();
			onDestroy();
			mPreference.edit().putBoolean("studyroom", false).commit();
		}


		br = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context c, Intent intent)
			{
				try {
					results = wifi.getScanResults();
					size = results.size();
				} catch (RuntimeException e) {
					// TODO: handle exception
					this.clearAbortBroadcast();
				}

			}
		};

		registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		count = 0;
		Log.e("servicewifi", "else");

		wifi.startScan();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					unregisterReceiver(br);
				} catch (IllegalArgumentException e) {
					// TODO: handle exception

				}
				if(getlist().size()!=0){
					if(myCustomArray.size()!=0){

						Log.e("servicewifi", "success : "+myCustomArray.toString());

//						new Async_check_studyroom(db.getStudentId(), myCustomArray.toString(), new VocaCallback() {
//
//							@Override
//							public void Resonponse(JSONObject response) {
//								// TODO Auto-generated method stub
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//
//										mPreference.edit().putBoolean("studyroom", true).commit();
//									}
//								});
//
//							}
//
//							@Override
//							public void Exception() {
//								// TODO Auto-generated method stub
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//										mPreference.edit().putBoolean("studyroom", false).commit();
//										stopService();
//										onDestroy();
//									}
//								});
//							}
//						}).execute();
					}else{
						Log.e("servicewifi", "faile ");
						mPreference.edit().putBoolean("studyroom", false).commit();
						stopService();
						onDestroy();
					}
				}
			}
		}, 5000);

		return startId;
	}

	public void stopService(){
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent intent2 = new Intent(this, CheckStudyRoomService.class);
		PendingIntent pintent2 = PendingIntent.getService(this, 0, intent2, 0);
		alarmManager.cancel(pintent2);

	}

	public JsonArray getlist(){
		arraylist.clear();
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
		return myCustomArray;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			unregisterReceiver(br);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}

	//	@Override
	//	public void onDestroy() {
	//		// TODO Auto-generated method stub
	//		super.onDestroy();
	//	}



}
