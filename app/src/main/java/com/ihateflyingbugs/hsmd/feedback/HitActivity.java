package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.WordListFragment;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.popup.GradeSelectPopUp;

import java.util.HashMap;
import java.util.Map;

public class HitActivity extends FragmentActivity {

	Intent intent;


	private Intent deliveredIntent = null;

	private Handler handler;

	private DBPool db;
	private Integer level_counting[][] = new Integer[6][2];

	SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	public static SharedPreferences timerPreference;

	String notice_title, notice_body;

	public static LayoutParams progress_gauge_param_1, progress_gauge_param_2,
			progress_gauge_param_3, progress_gauge_param_4,
			progress_gauge_param_5;

	public static LayoutParams progress_background_param_1,
			progress_background_param_2, progress_background_param_3,
			progress_background_param_4, progress_background_param_5;

	LinearLayout grade_one_ll, grade_two_ll, grade_three_ll, grade_four_ll,
			grade_five_ll, workBook_ListLayout;

	static public LinearLayout grade_one_touch_ll, grade_two_touch_ll,
			grade_three_touch_ll, grade_four_touch_ll, grade_five_touch_ll;

	TextView grade_one_word, grade_two_word, grade_three_word, grade_four_word,
			grade_five_word;

	TextView before_hit_percentage_tv;


	TextView progress_gauge_1, progress_gauge_2, progress_gauge_3,
			progress_gauge_4, progress_gauge_5, progress_background_1,
			progress_background_2, progress_background_3,
			progress_background_4, progress_background_5;

	TextView select_progress_gauge_1, select_progress_gauge_2,
			select_progress_gauge_3, select_progress_gauge_4,
			select_progress_gauge_5, select_progress_background_1,
			select_progress_background_2, select_progress_background_3,
			select_progress_background_4, select_progress_background_5;

	static public TextView my_progress_gauge, my_progress_background;

	static public TextView tv_current_word_grade, tv_word_grade,
			tv_word_percentage, tv_word_difficuty, tv_word_count;

	static public TextView tv_select_word_count, tv_grade_one_word_count,
			tv_grade_two_word_count, tv_grade_three_word_count,
			tv_grade_four_word_count, tv_grade_five_word_count,
			tv_select_one_word_count, tv_select_two_word_count,
			tv_select_three_word_count, tv_select_four_word_count,
			tv_select_five_word_count;

	static public ImageView using_grade_img;

	ToggleButton hit_push;
	TextView hit_pushTv;
	TextView workBook_ListTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_hit_small);
		}else{
			setContentView(R.layout.activity_hit);
		}


		setActionBar();

		intent = new Intent();

		tv_select_word_count = (TextView) findViewById(R.id.select_word_count_tv);

		tv_grade_one_word_count = (TextView) findViewById(R.id.grade_one_word_count_tv);
		tv_grade_two_word_count = (TextView) findViewById(R.id.grade_two_word_count_tv);
		tv_grade_three_word_count = (TextView) findViewById(R.id.grade_three_word_count_tv);
		tv_grade_four_word_count = (TextView) findViewById(R.id.grade_four_word_count_tv);
		tv_grade_five_word_count = (TextView) findViewById(R.id.grade_five_word_count_tv);

		tv_select_one_word_count = (TextView) findViewById(R.id.select_one_word_count_tv);
		tv_select_two_word_count = (TextView) findViewById(R.id.select_two_word_count_tv);
		tv_select_three_word_count = (TextView) findViewById(R.id.select_three_word_count_tv);
		tv_select_four_word_count = (TextView) findViewById(R.id.select_four_word_count_tv);
		tv_select_five_word_count = (TextView) findViewById(R.id.select_five_word_count_tv);

		tv_current_word_grade = (TextView) findViewById(R.id.tv_current_word_grade);
		tv_word_grade = (TextView) findViewById(R.id.tv_word_grade);
		tv_word_percentage = (TextView) findViewById(R.id.tv_word_percentage);
		tv_word_difficuty = (TextView) findViewById(R.id.tv_word_difficuty);
		tv_word_count = (TextView) findViewById(R.id.tv_word_count);

		grade_one_ll = (LinearLayout) findViewById(R.id.grade_one_layout);
		grade_two_ll = (LinearLayout) findViewById(R.id.grade_two_layout);
		grade_three_ll = (LinearLayout) findViewById(R.id.grade_three_layout);
		grade_four_ll = (LinearLayout) findViewById(R.id.grade_four_layout);
		grade_five_ll = (LinearLayout) findViewById(R.id.grade_five_layout);

		grade_one_touch_ll = (LinearLayout) findViewById(R.id.grade_one_touch_layout);
		grade_two_touch_ll = (LinearLayout) findViewById(R.id.grade_two_touch_layout);
		grade_three_touch_ll = (LinearLayout) findViewById(R.id.grade_three_touch_layout);
		grade_four_touch_ll = (LinearLayout) findViewById(R.id.grade_four_touch_layout);
		grade_five_touch_ll = (LinearLayout) findViewById(R.id.grade_five_touch_layout);

		progress_gauge_1 = (TextView) findViewById(R.id.progress_gauge_1);
		progress_gauge_2 = (TextView) findViewById(R.id.progress_gauge_2);
		progress_gauge_3 = (TextView) findViewById(R.id.progress_gauge_3);
		progress_gauge_4 = (TextView) findViewById(R.id.progress_gauge_4);
		progress_gauge_5 = (TextView) findViewById(R.id.progress_gauge_5);

		progress_background_1 = (TextView) findViewById(R.id.progress_background_1);
		progress_background_2 = (TextView) findViewById(R.id.progress_background_2);
		progress_background_3 = (TextView) findViewById(R.id.progress_background_3);
		progress_background_4 = (TextView) findViewById(R.id.progress_background_4);
		progress_background_5 = (TextView) findViewById(R.id.progress_background_5);

		select_progress_gauge_1 = (TextView) findViewById(R.id.select_progress_gauge_1);
		select_progress_gauge_2 = (TextView) findViewById(R.id.select_progress_gauge_2);
		select_progress_gauge_3 = (TextView) findViewById(R.id.select_progress_gauge_3);
		select_progress_gauge_4 = (TextView) findViewById(R.id.select_progress_gauge_4);
		select_progress_gauge_5 = (TextView) findViewById(R.id.select_progress_gauge_5);

		select_progress_background_1 = (TextView) findViewById(R.id.select_progress_background_1);
		select_progress_background_2 = (TextView) findViewById(R.id.select_progress_background_2);
		select_progress_background_3 = (TextView) findViewById(R.id.select_progress_background_3);
		select_progress_background_4 = (TextView) findViewById(R.id.select_progress_background_4);
		select_progress_background_5 = (TextView) findViewById(R.id.select_progress_background_5);


		my_progress_gauge = (TextView) findViewById(R.id.my_progress_gauge);
		my_progress_background = (TextView) findViewById(R.id.my_progress_background);

		mPreference = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		editor = mPreference.edit();

		db = DBPool.getInstance(getApplicationContext());
		level_counting = db.getLevelCounting();


		using_grade_img = (ImageView) findViewById(R.id.using_grade_img);

		grade_one_word = (TextView) findViewById(R.id.grade_one_word_count_tv);
		grade_two_word = (TextView) findViewById(R.id.grade_two_word_count_tv);
		grade_three_word = (TextView) findViewById(R.id.grade_three_word_count_tv);
		grade_four_word = (TextView) findViewById(R.id.grade_four_word_count_tv);
		grade_five_word = (TextView) findViewById(R.id.grade_five_word_count_tv);

		before_hit_percentage_tv = (TextView) findViewById(R.id.before_book_hit_percentageTv);
		before_hit_percentage_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlurryAgent.logEvent("HitActivity:Click_Show_BeforePercent");
				Intent intent = new Intent(HitActivity.this,
						BeforeHitActivity.class);
				startActivity(intent);
			}
		});

		workBook_ListLayout = (LinearLayout) findViewById(R.id.workBook_ListLayout);
		workBook_ListLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("HitActivity:Click_Set_BookList");
				Intent intent = new Intent(HitActivity.this,
						BookListActivity.class);
				startActivity(intent);
			}
		});

		grade_one_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("HitActivity:Click_Show_Grade1");
				Intent intent = new Intent(HitActivity.this, GradeSelectPopUp.class);
				intent.putExtra("grade", 1);
				startActivityForResult(intent, 1);

			}
		});
		grade_two_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlurryAgent.logEvent("HitActivity:Click_Show_Grade2");
				Intent intent = new Intent(HitActivity.this, GradeSelectPopUp.class);
				intent.putExtra("grade", 2);
				startActivityForResult(intent, 22);
			}
		});
		grade_three_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("HitActivity:Click_Show_Grade3");
				Intent intent = new Intent(HitActivity.this, GradeSelectPopUp.class);
				intent.putExtra("grade", 3);
				startActivityForResult(intent, 333);
			}
		});
		grade_four_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlurryAgent.logEvent("HitActivity:Click_Show_Grade4");
				Intent intent = new Intent(HitActivity.this, GradeSelectPopUp.class);
				intent.putExtra("grade", 4);
				startActivityForResult(intent, 4444);
			}
		});
		grade_five_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FlurryAgent.logEvent("HitActivity:Click_Show_Grade5");
				Intent intent = new Intent(HitActivity.this, GradeSelectPopUp.class);
				intent.putExtra("grade", 5);
				startActivityForResult(intent, 55555);
			}
		});

		hit_push = (ToggleButton) findViewById(R.id.hit_pushBtn);

		if (mPreference.getBoolean(MainValue.GpreMemoryPush, true)) {
			hit_push.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
			hit_push.setChecked(true);
		} else {
			hit_push.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
			hit_push.setChecked(false);
		}

		hit_push.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPreference.getBoolean(MainValue.GpreHitPush, false)) {
					hit_push.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					editor.putBoolean(MainValue.GpreHitPush, false);
					editor.commit();
					FlurryAgent.logEvent("HitActivity:Click_Off_HitPush");
				} else {
					hit_push.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					editor.putBoolean(MainValue.GpreHitPush, true);
					editor.commit();

					FlurryAgent.logEvent("HitActivity:Click_On_HitPush");
				}

			}
		});

		int current_grade = db.getWordLevel();

		int[][] all_grade_count = db.getCountOfWordsAsGrade();
		int[][] my_grade_count = db.getCountOfUserStudiedWords();

		tv_select_word_count.setText("0/"
				+ all_grade_count[current_grade - 1][1]);

		tv_grade_one_word_count.setText("0/"
				+ Integer.toString(all_grade_count[0][1]));
		tv_grade_two_word_count.setText("0/"
				+ Integer.toString(all_grade_count[1][1]));
		tv_grade_three_word_count.setText("0/"
				+ Integer.toString(all_grade_count[2][1]));
		tv_grade_four_word_count.setText("0/"
				+ Integer.toString(all_grade_count[3][1]));
		tv_grade_five_word_count.setText("0/"
				+ Integer.toString(all_grade_count[4][1]));
		tv_select_one_word_count.setText("0/"
				+ Integer.toString(all_grade_count[0][1]));
		tv_select_two_word_count.setText("0/"
				+ Integer.toString(all_grade_count[1][1]));
		tv_select_three_word_count.setText("0/"
				+ Integer.toString(all_grade_count[2][1]));
		tv_select_four_word_count.setText("0/"
				+ Integer.toString(all_grade_count[3][1]));
		tv_select_five_word_count.setText("0/"
				+ Integer.toString(all_grade_count[4][1]));

		progress_gauge_param_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_gauge_param_1.weight = 0;
		progress_background_param_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_background_param_1.weight = all_grade_count[0][1];

		progress_gauge_param_2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_gauge_param_2.weight = 0;
		progress_background_param_2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_background_param_2.weight = all_grade_count[1][1];

		progress_gauge_param_3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_gauge_param_3.weight = 0;
		progress_background_param_3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_background_param_3.weight = all_grade_count[2][1];

		progress_gauge_param_4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_gauge_param_4.weight = 0;
		progress_background_param_4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_background_param_4.weight = all_grade_count[3][1];

		progress_gauge_param_5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_gauge_param_5.weight = 0;
		progress_background_param_5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progress_background_param_5.weight = all_grade_count[4][1];

		for (int i = 0; i < my_grade_count.length; i++) {

			if (current_grade == my_grade_count[i][0]) {
				tv_select_word_count.setText(Integer
						.toString(my_grade_count[i][1])
						+ "/"
						+ all_grade_count[current_grade - 1][1]);
			}

			switch (my_grade_count[i][0]) {

				case 1:
					tv_grade_one_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[0][1]));
					tv_select_one_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[0][1]));

					progress_gauge_param_1.weight = my_grade_count[i][1];
					progress_background_param_1.weight = all_grade_count[0][1]
							- my_grade_count[i][1];

					break;
				case 2:
					tv_grade_two_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[1][1]));
					tv_select_two_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[1][1]));

					progress_gauge_param_2.weight = my_grade_count[i][1];
					progress_background_param_2.weight = all_grade_count[1][1]
							- my_grade_count[i][1];
					break;
				case 3:
					tv_grade_three_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[2][1]));
					tv_select_three_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[2][1]));

					progress_gauge_param_3.weight = my_grade_count[i][1];
					progress_background_param_3.weight = all_grade_count[2][1]
							- my_grade_count[i][1];
					break;
				case 4:
					tv_grade_four_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[3][1]));
					tv_select_four_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[3][1]));

					progress_gauge_param_4.weight = my_grade_count[i][1];
					progress_background_param_4.weight = all_grade_count[3][1]
							- my_grade_count[i][1];
					break;
				case 5:
					tv_grade_five_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[4][1]));
					tv_select_five_word_count.setText(Integer
							.toString(my_grade_count[i][1])
							+ "/"
							+ Integer.toString(all_grade_count[4][1]));

					progress_gauge_param_5.weight = my_grade_count[i][1];
					progress_background_param_5.weight = all_grade_count[4][1]
							- my_grade_count[i][1];
					break;
				default:
					break;
			}
		}

		progress_gauge_1.setLayoutParams(progress_gauge_param_1);
		progress_gauge_2.setLayoutParams(progress_gauge_param_2);
		progress_gauge_3.setLayoutParams(progress_gauge_param_3);
		progress_gauge_4.setLayoutParams(progress_gauge_param_4);
		progress_gauge_5.setLayoutParams(progress_gauge_param_5);

		progress_background_1.setLayoutParams(progress_background_param_1);
		progress_background_2.setLayoutParams(progress_background_param_2);
		progress_background_3.setLayoutParams(progress_background_param_3);
		progress_background_4.setLayoutParams(progress_background_param_4);
		progress_background_5.setLayoutParams(progress_background_param_5);

		select_progress_gauge_1.setLayoutParams(progress_gauge_param_1);
		select_progress_gauge_2.setLayoutParams(progress_gauge_param_2);
		select_progress_gauge_3.setLayoutParams(progress_gauge_param_3);
		select_progress_gauge_4.setLayoutParams(progress_gauge_param_4);
		select_progress_gauge_5.setLayoutParams(progress_gauge_param_5);

		select_progress_background_1
				.setLayoutParams(progress_background_param_1);
		select_progress_background_2
				.setLayoutParams(progress_background_param_2);
		select_progress_background_3
				.setLayoutParams(progress_background_param_3);
		select_progress_background_4
				.setLayoutParams(progress_background_param_4);
		select_progress_background_5
				.setLayoutParams(progress_background_param_5);

		switch (current_grade) {
			case 1:
				using_grade_img
						.setImageResource(R.drawable.icon_targrtpage_wordset1);
				grade_one_touch_ll.setVisibility(View.VISIBLE);
				grade_two_touch_ll.setVisibility(View.GONE);
				grade_three_touch_ll.setVisibility(View.GONE);
				grade_four_touch_ll.setVisibility(View.GONE);
				grade_five_touch_ll.setVisibility(View.GONE);

				tv_current_word_grade.setText("적중 기초 단어장");
				tv_word_difficuty.setText("매우 쉬움");
				tv_word_grade.setText("8~9등급");
				tv_word_percentage.setText("50~100%");
				tv_word_count.setText(Integer.toString(all_grade_count[0][1]));

				my_progress_gauge.setLayoutParams(progress_gauge_param_1);
				my_progress_background.setLayoutParams(progress_background_param_1);

				break;
			case 2:
				using_grade_img
						.setImageResource(R.drawable.icon_targrtpage_wordset2);
				grade_one_touch_ll.setVisibility(View.GONE);
				grade_two_touch_ll.setVisibility(View.VISIBLE);
				grade_three_touch_ll.setVisibility(View.GONE);
				grade_four_touch_ll.setVisibility(View.GONE);
				grade_five_touch_ll.setVisibility(View.GONE);

				tv_current_word_grade.setText("적중 필수1 단어장");
				tv_word_difficuty.setText("쉬움");
				tv_word_grade.setText("5~7등급");
				tv_word_percentage.setText("20~50%");
				tv_word_count.setText(Integer.toString(all_grade_count[1][1]));

				my_progress_gauge.setLayoutParams(progress_gauge_param_2);
				my_progress_background.setLayoutParams(progress_background_param_2);

				break;
			case 3:
				using_grade_img
						.setImageResource(R.drawable.icon_targrtpage_wordset3);
				grade_one_touch_ll.setVisibility(View.GONE);
				grade_two_touch_ll.setVisibility(View.GONE);
				grade_three_touch_ll.setVisibility(View.VISIBLE);
				grade_four_touch_ll.setVisibility(View.GONE);
				grade_five_touch_ll.setVisibility(View.GONE);

				tv_current_word_grade.setText("적중 필수2 단어장");
				tv_word_difficuty.setText("중간");
				tv_word_grade.setText("3~4등급");
				tv_word_percentage.setText("10~20%");
				tv_word_count.setText(Integer.toString(all_grade_count[2][1]));

				my_progress_gauge.setLayoutParams(progress_gauge_param_3);
				my_progress_background.setLayoutParams(progress_background_param_3);

				break;
			case 4:
				using_grade_img
						.setImageResource(R.drawable.icon_targrtpage_wordset4);
				grade_one_touch_ll.setVisibility(View.GONE);
				grade_two_touch_ll.setVisibility(View.GONE);
				grade_three_touch_ll.setVisibility(View.GONE);
				grade_four_touch_ll.setVisibility(View.VISIBLE);
				grade_five_touch_ll.setVisibility(View.GONE);

				tv_current_word_grade.setText("적중 마스터 단어장");
				tv_word_difficuty.setText("어려움");
				tv_word_grade.setText("1~2등급");
				tv_word_percentage.setText("5~10%");
				tv_word_count.setText(Integer.toString(all_grade_count[3][1]));

				my_progress_gauge.setLayoutParams(progress_gauge_param_4);
				my_progress_background.setLayoutParams(progress_background_param_4);

				break;
			case 5:
				using_grade_img
						.setImageResource(R.drawable.icon_targrtpage_wordset5);
				grade_one_touch_ll.setVisibility(View.GONE);
				grade_two_touch_ll.setVisibility(View.GONE);
				grade_three_touch_ll.setVisibility(View.GONE);
				grade_four_touch_ll.setVisibility(View.GONE);
				grade_five_touch_ll.setVisibility(View.VISIBLE);

				tv_current_word_grade.setText("적중 심화 단어장");
				tv_word_difficuty.setText("매우 어려움");
				tv_word_grade.setText("1등급");
				tv_word_percentage.setText("0.1~5%");
				tv_word_count.setText(Integer.toString(all_grade_count[4][1]));

				my_progress_gauge.setLayoutParams(progress_gauge_param_5);
				my_progress_background.setLayoutParams(progress_background_param_5);

				break;

			default:
				break;
		}

		timerPreference = getSharedPreferences(MainActivitys.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);



		handler = new Handler();


	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 1592){
			deliveredIntent = data;
		}else if(resultCode == Activity.RESULT_OK){

			WordListFragment.clearWordList();

			switch (data.getExtras().getInt("grade")) {
				case 1:
					intent.putExtra("selectgrade", true);
					intent.putExtra("grade", 1);
					break;
				case 2:
					intent.putExtra("selectgrade", true);
					intent.putExtra("grade", 1);
					break;
				case 3:
					intent.putExtra("selectgrade", true);
					intent.putExtra("grade", 1);
					break;
				case 4:
					intent.putExtra("selectgrade", true);
					intent.putExtra("grade", 1);
					break;
				case 5:
					intent.putExtra("selectgrade", true);
					intent.putExtra("grade", 1);
					break;

			}
		}
	}




	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mActionBar_Layout;
	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mActionBar_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar);
		mActionBar_Layout.setBackgroundColor(Color.parseColor("#2f6e9b"));

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(deliveredIntent == null){
					deliveredIntent = new Intent();
				}

				FlurryAgent.logEvent("HitActivity:Click_Close_BackButton");
				setResult(1592, deliveredIntent);
				HitActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("적중단어");
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
		FlurryAgent.logEvent("HitActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("HitActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
