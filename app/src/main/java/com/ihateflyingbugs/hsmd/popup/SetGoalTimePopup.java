package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;

import java.util.HashMap;
import java.util.Map;

public class SetGoalTimePopup extends Activity {

	InputMethodManager imm;

	RadioGroup radioGroup;
	private static SharedPreferences mPreference;

	RelativeLayout rl_setgoal_layout;
	RadioButton radio_10minute, radio_15minute, radio_30minute,
			radio_minute_etc;
	EditText et_minute_etc;
	TextView tv_10minute, tv_15minute, tv_30minute;

	DBPool db;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_goaltime);
		db = DBPool.getInstance(getApplicationContext());

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		mPreference = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		radioGroup = (RadioGroup) findViewById(R.id.rg_settime);

		switch (db.getGoalTime()) {
			case 10:
				radioGroup.check(R.id.radio_10minute);
				break;
			case 15:
				radioGroup.check(R.id.radio_15minute);
				break;
			case 30:
				radioGroup.check(R.id.radio_30minute);
				break;
			default:
				radioGroup.check(R.id.radio_minute_etc);
				break;
		}

		rl_setgoal_layout = (RelativeLayout) findViewById(R.id.rl_setgoal_layout);
		rl_setgoal_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.hideSoftInputFromWindow(et_minute_etc.getWindowToken(), 0);

				FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_1");
			}
		});

		et_minute_etc = (EditText) findViewById(R.id.et_minute_etc);
		et_minute_etc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_etc");
				radioGroup.check(R.id.radio_minute_etc);
				imm.showSoftInput(et_minute_etc, 0);
			}
		});
		radio_minute_etc = (RadioButton) findViewById(R.id.radio_minute_etc);
		radio_minute_etc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_etc");
				radioGroup.check(R.id.radio_minute_etc);
				imm.showSoftInput(et_minute_etc, 0);
			}
		});

		tv_10minute = (TextView) findViewById(R.id.tv_10minute);
		tv_10minute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_10m");
				radioGroup.check(R.id.radio_10minute);
			}
		});

		tv_15minute = (TextView) findViewById(R.id.tv_15minute);
		tv_15minute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_15m");
				radioGroup.check(R.id.radio_15minute);
			}
		});

		tv_30minute = (TextView) findViewById(R.id.tv_30minute);
		tv_30minute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_30m");
				radioGroup.check(R.id.radio_30minute);
			}
		});

		et_minute_etc.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch (actionId) {
					case EditorInfo.IME_ACTION_DONE:
						imm.hideSoftInputFromWindow(et_minute_etc.getWindowToken(), 0);
						FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_30m");
						break;
					default:
						return false;
				}
				return true;
			}
		});

		Button bt_close_popup = (Button) findViewById(R.id.bt_close_popup);

		bt_close_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (radioGroup.getCheckedRadioButtonId()) {
					case R.id.radio_10minute:
						//mPreference.edit().putInt(MainValue.GpreGoalTime, 10).commit();
						db.insertGoalTime(10);
						break;
					case R.id.radio_15minute:
						//mPreference.edit().putInt(MainValue.GpreGoalTime, 15).commit();
						db.insertGoalTime(15);
						break;
					case R.id.radio_30minute:
						//mPreference.edit().putInt(MainValue.GpreGoalTime, 30).commit();
						db.insertGoalTime(30);
						break;
					case R.id.radio_minute_etc:
						if(et_minute_etc.length() != 0){
							int goalTime = Integer.parseInt(et_minute_etc.getText().toString());
							FlurryAgent.logEvent("SetGoalTimePopUp:SelectGoalTime_"+et_minute_etc.getText().toString()+"m");
							db.insertGoalTime(goalTime);
						}
						break;
					default:
						//mPreference.edit().putInt(MainValue.GpreGoalTime, 15).commit();
						db.insertGoalTime(15);
						break;
				}


				FlurryAgent.logEvent("SetGoalTimePopUp:Click_Close_Popup");
				setResult(1155, new Intent());
				finish();

			}
		});

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
		FlurryAgent.logEvent("SetGoalTimePopUp", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
