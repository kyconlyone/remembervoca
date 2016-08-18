package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.feedback.HitActivity;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class GradeSelectPopUp extends Activity {

	Button select_btn, close_btn;
	TextView grade_name_tv, grade_word_tv, grade_explain_tv;
	ImageView grade_img;
	DBPool db;
	TextView progress_gauge, background_gauge;

	public static SharedPreferences mPreferences;

	int[][] all_grade_count;
	int[][] my_grade_count;

	LayoutParams progress_param, background_param;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_selectgrade);

		db = DBPool.getInstance(getApplicationContext());
		final int grade = getIntent().getIntExtra("grade", 1);

		progress_param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		background_param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		mPreferences = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		select_btn = (Button) findViewById(R.id.select_btn);
		close_btn = (Button) findViewById(R.id.close_btn);

		all_grade_count = db.getCountOfWordsAsGrade();
		my_grade_count = db.getCountOfUserStudiedWords();

		select_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(db.getWordLevel() == grade){
					Toast.makeText(getApplicationContext(), "현재 공부중인 단어장입니다.", Toast.LENGTH_SHORT).show();
				} else if (grade == 1) {
					MainActivity.tvCurrentLevel.setText("적중 기초 단어장");

					HitActivity.using_grade_img
							.setImageResource(R.drawable.icon_targrtpage_wordset1);
					HitActivity.grade_one_touch_ll.setVisibility(View.VISIBLE);
					HitActivity.grade_two_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_three_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_four_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_five_touch_ll.setVisibility(View.GONE);

					HitActivity.tv_current_word_grade.setText("적중 기초 단어장");
					HitActivity.tv_word_difficuty.setText("매우 쉬움");
					HitActivity.tv_word_grade.setText("8~9등급");
					HitActivity.tv_word_percentage.setText("50~100%");
					HitActivity.tv_word_count.setText(Integer
							.toString(all_grade_count[0][1]));
					Config.Difficulty = 1;
					db.insertWordLevel(1);

					HitActivity.tv_select_word_count.setText("0/"
							+ all_grade_count[0][1]);
					for (int i = 0; i < my_grade_count.length; i++) {
						if (my_grade_count[i][0] == 1)
							HitActivity.tv_select_word_count.setText(Integer
									.toString(my_grade_count[i][1])
									+ "/"
									+ all_grade_count[0][1]);
					}

					HitActivity.my_progress_gauge
							.setLayoutParams(HitActivity.progress_gauge_param_1);
					HitActivity.my_progress_background
							.setLayoutParams(HitActivity.progress_background_param_1);

					FlurryAgent.logEvent("GradeSelectPopUp:SelectGrade_1");

				} else if (grade == 2) {
					MainActivity.tvCurrentLevel.setText("적중 필수1 단어장");
					HitActivity.using_grade_img
							.setImageResource(R.drawable.icon_targrtpage_wordset2);
					HitActivity.grade_one_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_two_touch_ll.setVisibility(View.VISIBLE);
					HitActivity.grade_three_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_four_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_five_touch_ll.setVisibility(View.GONE);

					HitActivity.tv_current_word_grade.setText("적중 필수1 단어장");
					HitActivity.tv_word_difficuty.setText("쉬움");
					HitActivity.tv_word_grade.setText("5~7등급");
					HitActivity.tv_word_percentage.setText("20~50%");
					HitActivity.tv_word_count.setText(Integer
							.toString(all_grade_count[1][1]));
					Config.Difficulty = 2;
					db.insertWordLevel(2);
					HitActivity.tv_select_word_count.setText("0/"
							+ all_grade_count[1][1]);
					for (int i = 0; i < my_grade_count.length; i++) {
						if (my_grade_count[i][0] == 2)
							HitActivity.tv_select_word_count.setText(Integer
									.toString(my_grade_count[i][1])
									+ "/"
									+ all_grade_count[1][1]);
					}
					HitActivity.my_progress_gauge
							.setLayoutParams(HitActivity.progress_gauge_param_2);
					HitActivity.my_progress_background
							.setLayoutParams(HitActivity.progress_background_param_2);
					FlurryAgent.logEvent("GradeSelectPopUp:SelectGrade_2");
				} else if (grade == 3) {
					MainActivity.tvCurrentLevel.setText("적중 필수2 단어장");
					HitActivity.using_grade_img
							.setImageResource(R.drawable.icon_targrtpage_wordset3);
					HitActivity.grade_one_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_two_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_three_touch_ll
							.setVisibility(View.VISIBLE);
					HitActivity.grade_four_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_five_touch_ll.setVisibility(View.GONE);

					HitActivity.tv_current_word_grade.setText("적중 필수2 단어장");
					HitActivity.tv_word_difficuty.setText("중간");
					HitActivity.tv_word_grade.setText("3~4등급");
					HitActivity.tv_word_percentage.setText("10~20%");
					HitActivity.tv_word_count.setText(Integer
							.toString(all_grade_count[2][1]));
					Config.Difficulty = 3;
					db.insertWordLevel(3);
					HitActivity.tv_select_word_count.setText("0/"
							+ all_grade_count[2][1]);
					for (int i = 0; i < my_grade_count.length; i++) {
						if (my_grade_count[i][0] == 3)
							HitActivity.tv_select_word_count.setText(Integer
									.toString(my_grade_count[i][1])
									+ "/"
									+ all_grade_count[2][1]);
					}
					HitActivity.my_progress_gauge
							.setLayoutParams(HitActivity.progress_gauge_param_3);
					HitActivity.my_progress_background
							.setLayoutParams(HitActivity.progress_background_param_3);
					FlurryAgent.logEvent("GradeSelectPopUp:SelectGrade_3");
				} else if (grade == 4) {
					MainActivity.tvCurrentLevel.setText("적중 마스터 단어장");
					HitActivity.using_grade_img
							.setImageResource(R.drawable.icon_targrtpage_wordset4);
					HitActivity.grade_one_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_two_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_three_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_four_touch_ll.setVisibility(View.VISIBLE);
					HitActivity.grade_five_touch_ll.setVisibility(View.GONE);

					HitActivity.tv_current_word_grade.setText("적중 마스터 단어장");
					HitActivity.tv_word_difficuty.setText("어려움");
					HitActivity.tv_word_grade.setText("1~2등급");
					HitActivity.tv_word_percentage.setText("5~10%");
					HitActivity.tv_word_count.setText(Integer
							.toString(all_grade_count[3][1]));
					Config.Difficulty = 4;
					db.insertWordLevel(4);
					HitActivity.tv_select_word_count.setText("0/"
							+ all_grade_count[3][1]);
					for (int i = 0; i < my_grade_count.length; i++) {
						if (my_grade_count[i][0] == 4)
							HitActivity.tv_select_word_count.setText(Integer
									.toString(my_grade_count[i][1])
									+ "/"
									+ all_grade_count[3][1]);
					}
					HitActivity.my_progress_gauge
							.setLayoutParams(HitActivity.progress_gauge_param_4);
					HitActivity.my_progress_background
							.setLayoutParams(HitActivity.progress_background_param_4);
					FlurryAgent.logEvent("GradeSelectPopUp:SelectGrade_4");
				} else if (grade == 5) {
					MainActivity.tvCurrentLevel.setText("적중 심화 단어장");
					HitActivity.using_grade_img
							.setImageResource(R.drawable.icon_targrtpage_wordset5);
					HitActivity.grade_one_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_two_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_three_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_four_touch_ll.setVisibility(View.GONE);
					HitActivity.grade_five_touch_ll.setVisibility(View.VISIBLE);

					HitActivity.tv_current_word_grade.setText("적중 심화 단어장");
					HitActivity.tv_word_difficuty.setText("매우 어려움");
					HitActivity.tv_word_grade.setText("1등급");
					HitActivity.tv_word_percentage.setText("0.1~5%");
					HitActivity.tv_word_count.setText(Integer
							.toString(all_grade_count[4][1]));
					Config.Difficulty = 5;
					db.insertWordLevel(5);
					HitActivity.tv_select_word_count.setText("0/"
							+ all_grade_count[4][1]);
					for (int i = 0; i < my_grade_count.length; i++) {
						if (my_grade_count[i][0] == 5)
							HitActivity.tv_select_word_count.setText(Integer
									.toString(my_grade_count[i][1])
									+ "/"
									+ all_grade_count[4][1]);
					}
					HitActivity.my_progress_gauge
							.setLayoutParams(HitActivity.progress_gauge_param_5);
					HitActivity.my_progress_background
							.setLayoutParams(HitActivity.progress_background_param_5);
					FlurryAgent.logEvent("GradeSelectPopUp:SelectGrade_5");
				}
				setResult(Activity.RESULT_OK, new Intent().putExtra("grade", grade));

				finish();
			}
		});

		close_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlurryAgent.logEvent("GradeSelectPopUp:ClickCancel");
				finish();
			}
		});

		progress_gauge = (TextView) findViewById(R.id.progress_gauge);
		background_gauge = (TextView) findViewById(R.id.background_gauge);
		grade_name_tv = (TextView) findViewById(R.id.grade_name_tv);
		grade_word_tv = (TextView) findViewById(R.id.grade_word_tv);
		grade_explain_tv = (TextView) findViewById(R.id.grade_explain_tv);

		grade_img = (ImageView) findViewById(R.id.grade_img);

		/*
		 * caller 0 ~ : 10 ~ :의지관련 푸시 20 ~ :기억관련 푸시 30 ~ :적중관련 푸시 40 ~ :
		 */

		grade_word_tv.setText("0/"
				+ Integer.toString(all_grade_count[grade - 1][1]));
		progress_param.weight = 0;
		background_param.weight = all_grade_count[grade - 1][1];

		for (int i = 0; i < my_grade_count.length; i++) {
			if (grade == my_grade_count[i][0]) {
				grade_word_tv
						.setText(Integer.toString(my_grade_count[i][1])
								+ "/"
								+ Integer
								.toString(all_grade_count[grade - 1][1]));

				progress_param.weight = my_grade_count[i][1];
				background_param.weight = all_grade_count[grade - 1][1]
						- my_grade_count[i][1];

			}
		}

		progress_gauge.setLayoutParams(progress_param);
		background_gauge.setLayoutParams(background_param);

		switch (grade) {
			case 1:
				grade_name_tv.setText("적중 기초 단어장");
				grade_img.setImageResource(R.drawable.icon_targrtpage_wordset1);
				grade_explain_tv
						.setText("적중 기초 단어장은 수능에 출제될 확률이 50~100% 이상의 고교 필수 단어들입니다.\n등장할 확률이 높은 만큼 익숙한 단어들일 확률이 높습니다.");
				break;
			case 2:
				grade_name_tv.setText("적중 필수 1 단어장");
				grade_img.setImageResource(R.drawable.icon_targrtpage_wordset2);
				grade_explain_tv
						.setText("적중 필수1 단어장은 수능에 출제될 확률이 20~50% 미만의 수능 필수 단어들입니다.\n적중 기초보다는 어렵지만 일반적으로 쉬운 단어들로 구성되어 있습니다.");
				break;
			case 3:
				grade_name_tv.setText("적중 필수 2 단어장");
				grade_img.setImageResource(R.drawable.icon_targrtpage_wordset3);
				grade_explain_tv
						.setText("적중 필수2 단어장은 수능에 출제될 확률이 10~20% 미만의단어들입니다.\n수험생이라면 꼭 알고 있어야 할 평균 수준의 단어로 구성되어져 있습니다.");
				break;
			case 4:
				grade_name_tv.setText("적중 마스터 단어장");
				grade_img.setImageResource(R.drawable.icon_targrtpage_wordset4);
				grade_explain_tv
						.setText("적중 마스터 단어장은 수능에 출제될 확률이 5~10% 미만인 단어들의 집합입니다.\n나올 확률이 높진 않지만 1등급의 당락을 가를 수 있는 어려운 단어들입니다.");
				break;
			case 5:
				grade_name_tv.setText("적중 심화 단어장");
				grade_img.setImageResource(R.drawable.icon_targrtpage_wordset5);
				grade_explain_tv
						.setText("적중 심화 단어장은수능에 나올 확률이 높진 않습니다.\n하지만 EBS 문제집, 평가원, 수능 시험에 1번 이상 출제된 단어들입니다.\n이전 단계 단어를 완벽히 마스터한 분들은 만약을 위해 외워보시길 권합니다.");
				break;

			default:
				break;
		}
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
		FlurryAgent.logEvent("GradeSelectPopUp", articleParams);
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
