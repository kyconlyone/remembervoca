package com.ihateflyingbugs.hsmd;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class ChoiceCateFragment extends Fragment implements OnClickListener,OnCheckedChangeListener{


	private Activity thisActivity;

	private Handler handler;

	SharedPreferences mPreference;
	SharedPreferences.Editor editor;
	Context mContext;
	DBPool db;
	int topic;
	RadioGroup rg ;
	RadioButton rb_sat, rb_toeic, rb_toefl;
	Button bt_choice_cate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		thisActivity = getActivity();
		mContext = getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_choicecate, container, false);

		db = DBPool.getInstance(getActivity().getApplicationContext());

		bt_choice_cate=(Button)view.findViewById(R.id.bt_choice_cate);



		rg = (RadioGroup)view.findViewById(R.id.rg_choice_cate);
		rb_sat = (RadioButton)view.findViewById(R.id.rb_choice_sat);
		rb_toeic = (RadioButton)view.findViewById(R.id.rb_choice_toeic);
		rb_toefl = (RadioButton)view.findViewById(R.id.rb_choice_toefl);



		mPreference = mContext.getSharedPreferences(MainActivitys.preName, mContext.MODE_WORLD_READABLE|mContext.MODE_WORLD_WRITEABLE);
		editor = mPreference.edit();

		handler= new Handler();

		topic = Integer.parseInt(mPreference.getString(MainActivitys.GpreTopic, "1"));


		return view;
	}




	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bt_choice_cate.setOnClickListener(this);
		rb_sat.setOnCheckedChangeListener(this);
		rb_toeic.setOnCheckedChangeListener(this);
		rb_toefl.setOnCheckedChangeListener(this);
		switch (topic) {
			case 1:
				FlurryAgent.logEvent("SideActivity_ReselectTopicFragment:CurrentSAT", true);
				rb_sat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				rb_toeic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
				rb_toefl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
				rb_sat.setTextColor(Color.argb(0xff, 255, 255, 255));
				rb_toeic.setTextColor(Color.argb(0x99, 255, 255, 255));
				rb_toefl.setTextColor(Color.argb(0x99, 255, 255, 255));
				Config.MIN_DIFFICULTY = 1;
				Config.MAX_DIFFICULTY = 2;
				db.calcLevel(Config.CHANGE_LEVEL_COUNT);
				rb_sat.setChecked(true);
				rb_toeic.setChecked(false);
				rb_toefl.setChecked(false);
				break;
			case 2:
				FlurryAgent.logEvent("SideActivity_ReselectTopicFragment:CurrentTOEIC", true);
				rb_sat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
				rb_toeic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				rb_toefl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
				rb_sat.setTextColor(Color.argb(0x99, 255, 255, 255));
				rb_toeic.setTextColor(Color.argb(0xff, 255, 255, 255));
				rb_toefl.setTextColor(Color.argb(0x99, 255, 255, 255));
				db.calcLevel(Config.CHANGE_LEVEL_COUNT);
				Config.MIN_DIFFICULTY = 1;
				Config.MAX_DIFFICULTY = 4;
				rb_sat.setChecked(false);
				rb_toeic.setChecked(true);
				rb_toefl.setChecked(false);
				break;
			case 3:
				FlurryAgent.logEvent("SideActivity_ReselectTopicFragment:CurrentTOEFL", true);
				rb_sat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
				rb_toeic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
				rb_toefl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				rb_sat.setTextColor(Color.argb(0x99, 255, 255, 255));
				rb_toeic.setTextColor(Color.argb(0x99, 255, 255, 255));
				rb_toefl.setTextColor(Color.argb(0xff, 255, 255, 255));
				db.calcLevel(Config.CHANGE_LEVEL_COUNT);
				rb_sat.setChecked(false);
				rb_toeic.setChecked(false);
				rb_toefl.setChecked(true);
				break;
			default:
				break;
		}
	}




	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_choice_cate:
				editor.putString(MainActivitys.GpreTopic, ""+topic).commit();
				thisActivity.finish();
				thisActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
				MainActivity.anim_ListView();
				break;
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
			case R.id.rb_choice_sat:
				if(isChecked){
					FlurryAgent.logEvent("SideActivity_ReselectTopicFragment:SelectSAT", true);
					rb_sat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
					rb_toefl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
					rb_toeic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
					rb_sat.setTextColor(Color.argb(0xff, 255, 255, 255));
					rb_toefl.setTextColor(Color.argb(0x99, 255, 255, 255));
					rb_toeic.setTextColor(Color.argb(0x99, 255, 255, 255));
					Config.MIN_DIFFICULTY = 1;
					Config.MAX_DIFFICULTY = 2;
					topic = 1;
				}
				break;
			case R.id.rb_choice_toeic:
				if(isChecked){
					FlurryAgent.logEvent("SideActivity_ReselectTopicFragment:SelectTOEIC", true);
					rb_sat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
					rb_toefl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
					rb_toeic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
					rb_sat.setTextColor(Color.argb(0x99, 255, 255, 255));
					rb_toefl.setTextColor(Color.argb(0x99, 255, 255, 255));
					rb_toeic.setTextColor(Color.argb(0xff, 255, 255, 255));
					Config.MIN_DIFFICULTY = 1;
					Config.MAX_DIFFICULTY = 4;
					topic = 2;
				}
				break;
			case R.id.rb_choice_toefl:
				if(isChecked){
					FlurryAgent.logEvent("SideActivity_ReselectTopicFragment:SelectTOEFL", true);
					rb_sat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
					rb_toefl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
					rb_toeic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
					rb_sat.setTextColor(Color.argb(0x99, 255, 255, 255));
					rb_toefl.setTextColor(Color.argb(0xff, 255, 255, 255));
					rb_toeic.setTextColor(Color.argb(0x99, 255, 255, 255));
					Config.MIN_DIFFICULTY = 1;
					Config.MAX_DIFFICULTY = 6;
					topic = 3;
				}
				break;
			default:
				break;
		}
	}


	String starttime;
	String startdate;
	Date date = new Date();

	Map<String, String> articleParams;
	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SideActivity_ReselectTopicFragment", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("SideActivity_ReselectTopicFragment");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}

}

