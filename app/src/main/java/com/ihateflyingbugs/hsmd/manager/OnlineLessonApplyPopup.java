package com.ihateflyingbugs.hsmd.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.popup.ExplainBetaService;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OnlineLessonApplyPopup extends Activity {
	String TAG = "OnlineLessonApplyPopup";

	Activity mActivity;
	Context mContext;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mShared_Editor;
	DBPool mDBPool;

	//
	Button mOnlineLessonJoin_Button;
	CheckBox mDoNotShow_Button;


	//
	//boolean bDoNotShowWeek;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		Log.e(TAG, "   onCreate");
		setContentView(R.layout.popup_beta_test_apply);

		mActivity = OnlineLessonApplyPopup.this;
		mContext = getApplicationContext();
		mSharedPreferences = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		mShared_Editor = mSharedPreferences.edit();
		mDBPool = DBPool.getInstance(mContext);

		// init
		mOnlineLessonJoin_Button = (Button)findViewById(R.id.btn_onlinelesson_join);

		ImageButton bt_beta_apply_close = (ImageButton)findViewById(R.id.bt_apply_close);

		bt_beta_apply_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mOnlineLessonJoin_Button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("KARAM", "  Online Lesson Button Click");

				ApplyOnlineLesson();
			}
		});

		mDoNotShow_Button = (CheckBox)findViewById(R.id.checkbox_onlinelesson_donotshow);
		mDoNotShow_Button.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
					//	mCalendar.add(Calendar.MINUTE, 2);
					mCalendar.add(Calendar.DATE, 7);

					Calendar mm = Calendar.getInstance();
					mm.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);

					Log.e("KARAM", " -> " + mCalendar.getTimeInMillis() + "  " + mm.getTimeInMillis() + "  " + (mCalendar.getTimeInMillis() - mm.getTimeInMillis()));


					mShared_Editor.putString("onlinelesson_donot", "yes");
					mShared_Editor.putLong("onlinelesson_time", mCalendar.getTimeInMillis());

					mShared_Editor.commit();
				}
				else {
					mShared_Editor.putString("onlinelesson_donot", "no");
					mShared_Editor.commit();
				}
			}
		});
	}

	public void ApplyOnlineLesson() {
//		new Async_upload_betaservice(mDBPool.getKakaoId(), mContext, new VocaCallback() {
//
//			@Override
//			public void Resonponse(JSONObject response) {
//				runOnUiThread(new Runnable() {
//					public void run() {
//						int returncode = ManagerInfo.getInstance(mContext).getApplyBetaService();
//
//						if(returncode == 1) {
//							Log.e("KARAM", "  Online Lesson Button 11111");
//							Intent intent = new Intent(OnlineLessonApplyPopup.this, OnlineLessonApply_SuccessPopup.class);
//							mActivity.startActivityForResult(intent, 1111);
//						}
//						else if(returncode == 2) {
//							Log.e("KARAM", "  Online Lesson Button 22222");
//							Toast.makeText(mContext, "이미 신청하였습니다.", Toast.LENGTH_SHORT).show();
//
//							Intent intent = new Intent();
//							setResult(RESULT_OK, intent);
//
//							OnlineLessonApplyPopup.this.finish();
//						}
//					}
//				});
//			}
//
//			@Override
//			public void Exception() {
//				runOnUiThread(new Runnable() {
//					public void run() {
//						Toast.makeText(mContext, "연결오류. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//					}
//				});
//			}
//		}).execute();
		new RetrofitService().getUseAppInfoService().retroGetBetaServiceAddress()
				.enqueue(new Callback<UseAppInfoData>() {
					@Override
					public void onResponse(final Response<UseAppInfoData> response, Retrofit retrofit) {
						runOnUiThread(new Runnable() {
							public void run() {
								Intent i = new Intent(OnlineLessonApplyPopup.this, ExplainBetaService.class);
								try {
									i.putExtra("uri", URLDecoder.decode(response.body().getAddress(), "UTF-8"));
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									Log.e(TAG,""+ e.toString());
									Toast.makeText(OnlineLessonApplyPopup.this, "연결오류. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
								}
								startActivityForResult(i, 1111);
							}

						});
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure : "+ t.toString());
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(OnlineLessonApplyPopup.this, "연결오류. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1111)
		{

			if(resultCode == RESULT_OK) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);

				OnlineLessonApplyPopup.this.finish();
			}
			else {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(mContext, "연결오류. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
}
