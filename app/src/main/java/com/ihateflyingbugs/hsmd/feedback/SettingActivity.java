package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.ChangeNameActivity;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.UseActivity;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.lock.LockService;
import com.ihateflyingbugs.hsmd.manager.Manager_CreateCodePopup;
import com.ihateflyingbugs.hsmd.manager.Manager_ReadyExamPopup;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.popup.QuitPopup;
import com.ihateflyingbugs.hsmd.popup.ShowCard;
import com.ihateflyingbugs.hsmd.popup.getDBtable;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.Feed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SettingActivity extends FragmentActivity implements OnClickListener {
	private LinearLayout noticeLayout;
	private LinearLayout provisionLayout;
	private LinearLayout updateLayout;
	private LinearLayout changeNameLayout;
	private LinearLayout ll_set_blogpush;
	private LinearLayout ll_setting_withdraw;
	private LinearLayout ll_setting_pay;
	private LinearLayout ll_setting_lockscreen;
	private LinearLayout ll_setting_recovery;
	private LinearLayout ll_setting_restore;

	private ToggleButton set_blogpush;
	private ToggleButton tb_set_lockscreen;
	private SharedPreferences pushPreference;
	private TextView tv_setting_update;

	Handler handler;

	TextView tv_notice_date;
	TextView tv_setting_period;



	int primeNum = 1;

	DBPool db;
	String finish_date;


	// Manager
	LinearLayout mCreateCode_Layout;
	LinearLayout mTakeExam_Layout;

	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		setActionBar();

		final Date date = new Date();

		handler = new Handler();

		Intent i = getIntent();

		primeNum = i.getIntExtra("prime_num", 1);

		db = DBPool.getInstance(getApplicationContext());

		tv_setting_period= (TextView)findViewById(R.id.tv_setting_period);

		new RetrofitService().getAuthorizationService().retroGetFinishDate(db.getStudentId())
				.enqueue(new Callback<AuthorizationData>() {
					@Override
					public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
						try {
							finish_date = response.body().getFinish_date();
						} catch (Exception e) {
							// TODO: handle exception
						}
						handler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (finish_date != null) {
									tv_setting_period.setText("" + finish_date + "까지");
								} else {
									tv_setting_period.setText(date.get_today_bar() + "까지");
								}

							}

						});
					}

					@Override
					public void onFailure(Throwable t) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								tv_setting_period.setText(date.get_today_bar() + "까지");
							}
						});
					}
				});


		noticeLayout = (LinearLayout) findViewById(R.id.notice_layout);
		provisionLayout = (LinearLayout) findViewById(R.id.provision_layout);
		updateLayout = (LinearLayout) findViewById(R.id.update_layout);
		changeNameLayout = (LinearLayout) findViewById(R.id.change_name_layout);
		ll_set_blogpush = (LinearLayout) findViewById(R.id.ll_set_blogpush);
		ll_setting_withdraw = (LinearLayout) findViewById(R.id.ll_setting_withdraw);
		ll_setting_pay = (LinearLayout) findViewById(R.id.ll_setting_pay);
		ll_setting_lockscreen = (LinearLayout) findViewById(R.id.ll_setting_lockscreen);
		ll_setting_restore = (LinearLayout) findViewById(R.id.ll_setting_restore);

		set_blogpush = (ToggleButton) findViewById(R.id.set_blogpush);
		tb_set_lockscreen = (ToggleButton)findViewById(R.id.tb_set_lockscreen);

		tv_setting_update = (TextView) findViewById(R.id.tv_setting_update);

		try{
			if (Config.SERVER_VERSION.equals(Config.VERSION)) {
			} else {
				tv_setting_update.setText("업데이트가 필요합니다.");
				updateLayout.setOnClickListener(this);
			}
		}catch(NullPointerException e){
			tv_setting_update.setText("현재 최신버전입니다.");
			updateLayout.setClickable(false);
		}


		tv_notice_date = (TextView) findViewById(R.id.tv_notice_date);
		tv_notice_date.setText("");

		new RetrofitService().getBoardService().retroGetNotice().enqueue(new Callback<List<Feed>>() {
			@Override
			public void onResponse(Response<List<Feed>> response, Retrofit retrofit) {
				final List<Feed> notice = response.body();
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						int recent = 0;
						Log.d("seokangseok", "start");
						for (int i = 0; i < notice.size(); i++) {
							int temp = Integer.parseInt(notice.get(i).getDate().replace("-", "").substring(0, 8));
							Log.d("seokangseok", Integer.toString(temp));
							if (temp > recent) {
								recent = temp;
							}
						}
						String notice_date = Integer.toString(recent);
						tv_notice_date.setText(notice_date.substring(0, 4) + "/" + notice_date.substring(4, 6) + "/"
								+ notice_date.substring(6, 8));
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {

				Log.e("notice_log", "notice fail" + t.toString());
			}
		});

		noticeLayout.setOnClickListener(this);
		provisionLayout.setOnClickListener(this);
		changeNameLayout.setOnClickListener(this);
		ll_set_blogpush.setOnClickListener(this);
		ll_setting_withdraw.setOnClickListener(this);
		ll_setting_pay.setOnClickListener(this);
		ll_setting_restore.setOnClickListener(this);

		pushPreference = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		final Editor pushEditor = pushPreference.edit();

		if (pushPreference.getBoolean(MainValue.GpreBlogPush, true)) {
			set_blogpush.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
		} else {
			set_blogpush.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
		}

		set_blogpush.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pushPreference.getBoolean(MainValue.GpreBlogPush, false)) {
					set_blogpush.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					pushEditor.putBoolean(MainValue.GpreBlogPush, false);
					pushEditor.commit();
					FlurryAgent.logEvent("SettingActivity:Click_Off_BlogPush");
				} else {
					set_blogpush.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					pushEditor.putBoolean(MainValue.GpreBlogPush, true);
					pushEditor.commit();
					FlurryAgent.logEvent("SettingActivity:Click_On_BlogPush");
				}

			}
		});


		if (db.getLockScreenTable()) {
			tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
		} else {
			tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
		}

		tb_set_lockscreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (db.getLockScreenTable()) {
					tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					db.setLockScreenTable("0");
					FlurryAgent.logEvent("LockScreenSettingActivity:Click_Off_LockScreen");
					Toast.makeText(SettingActivity.this, "App을 완전히 종료해 주세요.", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LockService.LOCK_SERVICE);
					intent.setPackage("com.ihateflyingbugs.hsmd.lock");
					stopService(intent);
					Log.e("serviceCheck", "Setting : stop service");
				}
				else {
					tb_set_lockscreen.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					db.setLockScreenTable("1");
					FlurryAgent.logEvent("LockScreenSettingActivity:Click_On_LockScreen");
					if(!isMyServiceRunning(LockService.class)){
						Log.e("serviceCheck", "Mainactivity : start service");
						Intent intent = new Intent(LockService.LOCK_SERVICE);
						intent.setPackage("com.ihateflyingbugs.hsmd.lock");
						startService(intent);
					}
					Log.e("serviceCheck", "Setting : start service");
				}
			}
		});




		// Manager
		mCreateCode_Layout = (LinearLayout)findViewById(R.id.layout_setting_studentcode);
		mTakeExam_Layout = (LinearLayout)findViewById(R.id.layout_setting_exam);

		mCreateCode_Layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, Manager_CreateCodePopup.class);
				startActivity(intent);
			}
		});

		mTakeExam_Layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, Manager_ReadyExamPopup.class);
				startActivity(intent);
			}
		});
	}


	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.e("KARAM", " Service   " + service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		Fragment newFragment = null;
		switch (v.getId()) {
			case R.id.notice_layout:
				Log.d("NoticeClick", "공지사항");
				intent = new Intent(SettingActivity.this, NoticeActivity.class);
				FlurryAgent.logEvent("SettingActivity:Click_Notice");
				break;
			case R.id.provision_layout:
				Log.d("NoticeClick", "약관");
				intent = new Intent(SettingActivity.this, UseActivity.class);
				FlurryAgent.logEvent("SettingActivity:Click_UseDoc");
				break;
			case R.id.update_layout:
				Uri uri = Uri.parse("market://details?id=com.ihateflyingbugs.hsmd");
				intent = new Intent(Intent.ACTION_VIEW, uri);
				FlurryAgent.logEvent("SettingActivity:Click_Update");
				break;
			case R.id.change_name_layout:
				intent = new Intent(SettingActivity.this, ChangeNameActivity.class);
				FlurryAgent.logEvent("SettingActivity:Click_Change_SystemName");
				break;
			case R.id.ll_setting_withdraw:
				// AlertDialog.Builder dialog = new
				// AlertDialog.Builder(SettingActivity.this);
				// dialog.setMessage("밀당영단어 서비스 탈퇴를 하시겠습니까?");
				// dialog.setPositiveButton("탈퇴완료", new
				// DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// new Async_upload_is_withdrawn(new VocaCallback() {
				//
				// @Override
				// public void Resonponse(JSONObject response) {
				// handler.post(new Runnable() {
				// @Override
				// public void run() {
				// Toast.makeText(getApplicationContext(), "탈퇴가 완료되었습니다.",
				// Toast.LENGTH_LONG).show();
				// }
				// });
				// setResult(55555);
				// finish();
				// }
				// @Override
				// public void Exception() {
				// handler.post(new Runnable() {
				// @Override
				// public void run() {
				// Toast.makeText(getApplicationContext(), "잠시후에 다시 시도해 주세요",
				// Toast.LENGTH_LONG).show();
				// }
				// });
				// }
				// }).execute();
				// }
				// });
				// dialog.setNegativeButton("다음에", new
				// DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {}
				// });
				// dialog.create().show();

				intent = new Intent(SettingActivity.this, QuitPopup.class);

				break;
			case R.id.ll_setting_pay:
				intent = new Intent(SettingActivity.this, ShowCard.class);
				intent.putExtra("prime_num", primeNum);
				break;
			case R.id.ll_setting_restore:
				intent = new Intent(SettingActivity.this, getDBtable.class);
				break;

			/*case R.id.ll_setting_lockscreen:
			intent = new Intent(SettingActivity.this, LockScreenSettingActivity.class);
			break;
			 */
			default:
				break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("SettingActivity:Click_Close_BackButton");
				SettingActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("설정");
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
		FlurryAgent.logEvent("SettingActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("SettingActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
