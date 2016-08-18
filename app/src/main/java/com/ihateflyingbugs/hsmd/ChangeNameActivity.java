package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;

import java.util.HashMap;
import java.util.Map;

public class ChangeNameActivity extends Activity {

	private Button changeNameBtn;
	private	EditText nameEdit;

	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_system_name_change);

		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		settings = getSharedPreferences(MainValue.preName,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		editor = settings.edit();

		changeNameBtn = (Button) findViewById(R.id.btn_change);
		nameEdit = (EditText) findViewById(R.id.edit_text_name);

		nameEdit.setText(settings.getString(MainValue.GpreSystemName, ""));


		changeNameBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String temp = nameEdit.getText().toString().trim();
				if (temp.length() < 2 || temp.length() > 8) {
					Toast.makeText(ChangeNameActivity.this, "시스템 이름은 2~8 글자만 가능합니다.", Toast.LENGTH_LONG).show();
				}
				else{
					FlurryAgent.logEvent("EditSysTemNameActivity:Click_Change_SystemName");
					editor.putString(MainValue.GpreSystemName, temp);
					editor.commit();
					imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
					finish();
				}
			}
		});

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				imm.showSoftInput(nameEdit, 0);
			}
		}, 500);
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
		FlurryAgent.logEvent("EditSysTemNameActivity", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("EditSysTemNameActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
