package com.ihateflyingbugs.hsmd.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Manager_ReadyExamPopup extends Activity {
	String TAG = "Manager_ExamPopup";

	Context mContext;

	TextView mContent_TextView;
	ProgressBar mSearch_ProgressBar;
	Button mExam_Button;
	Button mClose_Button;

	//
	DBPool mDBPool;
	String mStudentID_String;
	String mStudentCode_String;

	int test_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_manager_readyexam);

		mContext = getApplicationContext();
		mDBPool = DBPool.getInstance(mContext);


		mContent_TextView = (TextView)findViewById(R.id.text_managerexam_content);
		mSearch_ProgressBar = (ProgressBar)findViewById(R.id.progress_managerexam_search);
		mExam_Button = (Button)findViewById(R.id.btn_managerexam_exam);
		mClose_Button = (Button)findViewById(R.id.btn_managerexam_close);

		//
		mContent_TextView.setText("등록된 시험을 찾고 있습니다");
		mClose_Button.setText("닫기");
		mSearch_ProgressBar.setVisibility(View.VISIBLE);
		mExam_Button.setVisibility(View.GONE);
		mClose_Button.setVisibility(View.GONE);


		mStudentID_String = mDBPool.getStudentId();
		ManagerInfo.getInstance(mContext).setStudentID_String(mStudentID_String);
		mStudentCode_String = ManagerInfo.getInstance(mContext).getStudentCode_String();


		new RetrofitService().getManagerService().retroIsExistTest(mDBPool.getStudentId())
				.enqueue(new Callback<ManagerData>() {
					@Override
					public void onResponse(final Response<ManagerData> response, Retrofit retrofit) {
						try {
							int mReturn_SuccessCode = response.body().getResult();
							ManagerInfo.getInstance(mContext).setAsyncReturnCode(mReturn_SuccessCode);

							Log.e("Manager_ReadyExamPopup", "   ReturnCode : " + mReturn_SuccessCode);

							if(mReturn_SuccessCode == 1 || mReturn_SuccessCode == 2) {

								test_id = response.body().getTest_id();
								int count = response.body().getCount();
								int type = response.body().getType();
								String type_content = response.body().getType_content();
								String lastexam = response.body().getLast_time();

								ManagerInfo.getInstance(mContext).setExamQuestionCount(count);
								ManagerInfo.getInstance(mContext).setExamType(type);
								ManagerInfo.getInstance(mContext).setExamTypeContent_String(type_content);
								ManagerInfo.getInstance(mContext).setLastExamDate_String(lastexam);

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										mContent_TextView.setText("등록된 시험이 있습니다\n시험을 보시겠습니까?");

										mSearch_ProgressBar.setVisibility(View.GONE);
										mExam_Button.setVisibility(View.VISIBLE);
										mClose_Button.setVisibility(View.VISIBLE);
									}
								});
							}
							else {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										mContent_TextView.setText("등록된 시험이 없습니다");
										mClose_Button.setText("확인");

										mSearch_ProgressBar.setVisibility(View.GONE);
										mExam_Button.setVisibility(View.GONE);
										mClose_Button.setVisibility(View.VISIBLE);
									}
								});
							}
						}
						catch(Exception e) {
							Log.e("Manager_ReadyExamPopup", "   Response Error : " + e.toString());

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(Manager_ReadyExamPopup.this, "연결오류\n잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
									Manager_ReadyExamPopup.this.finish();
								}
							});
						}
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e("Manager_ReadyExamPopup", "retroIsExistTest onFailure : " + t.toString());
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mContent_TextView.setText("등록된 시험이 없습니다");
								mClose_Button.setText("확인");

								mSearch_ProgressBar.setVisibility(View.GONE);
								mExam_Button.setVisibility(View.GONE);
								mClose_Button.setVisibility(View.VISIBLE);
							}
						});
					}
				});
		//
		mExam_Button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//		Log.e("KARAM", " type  : " + ManagerInfo.getInstance(mContext).getExamType());
				//		Log.e("KARAM", " typecontent  : " + ManagerInfo.getInstance(mContext).getExamTypeContent_String());
				//		Log.e("KARAM", " count  : " + ManagerInfo.getInstance(mContext).getExamQuestionCount());

				Intent intent = new Intent(Manager_ReadyExamPopup.this, Manager_TakeExamActivity.class);
				intent.putExtra("test_id", test_id);
				finish();
				startActivity(intent);
			}
		});


		mClose_Button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
		FlurryAgent.logEvent("CreateCodePopup", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("CreateCodePopup");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
