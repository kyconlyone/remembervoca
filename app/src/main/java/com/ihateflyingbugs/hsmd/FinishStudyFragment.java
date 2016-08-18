package com.ihateflyingbugs.hsmd;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;

import java.util.HashMap;
import java.util.Map;

public class FinishStudyFragment extends Fragment implements OnClickListener{
	private EditText et_qna_email, et_qna_phone, et_qna_text;
	private Spinner sp_qna_title;
	private Button bt_qna_submit;
	DBPool db;

	private static Activity thisActivity;

	private static Handler handler;

	static SharedPreferences mPreference;

	TextView tv_finish_study_cong;
	TextView tv_finish_study_time;
	Context mContext;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		thisActivity = getActivity();

		mContext = getActivity().getApplicationContext();
		mPreference =  mContext.getSharedPreferences(MainValue.preName, mContext.MODE_WORLD_READABLE|mContext.MODE_WORLD_WRITEABLE);
		View view = inflater.inflate(R.layout.fragment_finish_study, container, false);

		db = DBPool.getInstance(getActivity());

		tv_finish_study_cong= (TextView)view.findViewById(R.id.tv_finish_study_cong);
		tv_finish_study_time= (TextView)view.findViewById(R.id.tv_finish_study_time);


		Button bt_finish_study_next = (Button)view.findViewById(R.id.bt_finish_study_next);
		Button bt_finish_study_destory = (Button)view.findViewById(R.id.bt_finish_study_destory);

		bt_finish_study_destory.setOnClickListener(this);
		bt_finish_study_next.setOnClickListener(this);


		if(db.getWordLevel()==5){
			bt_finish_study_next.setVisibility(View.INVISIBLE);
		}

		String topic = null;

//		if(mPreference.getString(MainActivitys.GpreTopic,"5").equals("1")){
//
//			topic = "수능";
//		}else if(mPreference.getString(MainActivitys.GpreTopic,"5").equals("2")){
//
//			topic = "토익";
//
//		}else if(mPreference.getString(MainActivitys.GpreTopic,"5").equals("3")){
//			topic = "토플";
//		}else{
//			topic = "해당";
//		}
		tv_finish_study_cong.setText("축하합니다\n 해당 단어장을\n 마스터 하셨습니다.");

		handler= new Handler();

		return view;
	}





	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_finish_study_next:
				FlurryAgent.logEvent("SideActivity_CompleteCurrentTopic:ClickNextTopic");
				Config.Difficulty= Config.Difficulty+1;
				db.LevelUp_Word();
				//Config.MIN_DIFFICULTY = Config.MIN_DIFFICULTY+2;
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.linearFragment, new WordListFragment()).addToBackStack(null).commit();

				break;

			case R.id.bt_finish_study_destory:
				FlurryAgent.logEvent("SideActivity_CompleteCurrentTopic:ClickAppFinish");
				getActivity().finish();
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
		FlurryAgent.logEvent("SideActivity_SideActivity_CompleteCurrentTopic", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
//		FlurryAgent.endTimedEvent("CompleteCurrentTopic:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}

}
