package com.ihateflyingbugs.hsmd.manager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Manager_TakeExamActivity extends Activity {
	String TAG = "Private_TakeExamActivity";

	Activity mActivity;
	Context mContext;


	ListView mQuestions_ListView;
	Button mExamFinish_Button;

	//
	DBPool mDBPool;

	String mStudentID_String;
	String mExamDate_String;
	String mLastExamDate_String;
	int mExamQuestionCount;

	int mQuestionNum;
	int mWordCode;
	String mWord_String;

	String mExample1_String;
	String mExample2_String;
	String mExample3_String;
	String mExample4_String;

	ArrayList<ExamQuestion> mQuestion_ArrayList = new ArrayList<ExamQuestion>();
	ExamQuestionAdapter mExamQuestion_Adapter;


	ArrayList<ExamResult> mResult_ArrayList = new ArrayList<ExamResult>();
	Gson mGson;
	JsonArray mJsonArray;
	String mJsonArray_String;

	int mExamID;
	int mExamType;


	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	RelativeLayout rl_activity_takeexam_progress;
	CircularProgressView takeexam_progress_view;
	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_takeexam);

		setActionBar();


		try {
			mExamID = getIntent().getExtras().getInt("test_id");
		} catch (Exception e) {
			// TODO: handle exception
		}

		mActivity = Manager_TakeExamActivity.this;
		mContext = getApplicationContext();
		mDBPool = DBPool.getInstance(mContext);


		mQuestions_ListView = (ListView)findViewById(R.id.list_takeexam_questions);

		View footer = getLayoutInflater().inflate(R.layout.listfooter_examresult_finish, null);
		mQuestions_ListView.addFooterView(footer);

		mExamFinish_Button = (Button)footer.findViewById(R.id.btn_examresultfooter_finish);

		rl_activity_takeexam_progress = (RelativeLayout)findViewById(R.id.rl_activity_takeexam_progress);
		takeexam_progress_view = (CircularProgressView) findViewById(R.id.takeexam_progress_view);

		rl_activity_takeexam_progress.setVisibility(View.VISIBLE);
		mQuestions_ListView.setVisibility(View.GONE);
		takeexam_progress_view.setColor(Color.parseColor("#00b569"));
		takeexam_progress_view.startAnimation();

		//
		mStudentID_String = ManagerInfo.getInstance(mContext).getStudentID_String();
		mLastExamDate_String = ManagerInfo.getInstance(mContext).getLastExamDate_String();
		mExamQuestionCount = ManagerInfo.getInstance(mContext).getExamQuestionCount();
		mExamType = ManagerInfo.getInstance(mContext).getExamType();

		long now = System.currentTimeMillis();
		Date mDate = new Date(now);
		SimpleDateFormat format_date_to_string = new SimpleDateFormat("yyyy-MM-dd");
		mExamDate_String = format_date_to_string.format(mDate);
		mTitle_TextView.setText(mExamDate_String);

		setListView();

		mQuestions_ListView.setSelector(android.R.color.transparent);
		mExamFinish_Button.setOnClickListener(clickListener);
		mBack_Layout.setOnClickListener(clickListener);

	}


	void setListView() {
		ArrayList<DBPool.WordLog_for_exam> list = null;

		if(mExamType == 1) {  // A
			list = mDBPool.getExamWord(mLastExamDate_String, mExamQuestionCount);
		}
		else if(mExamType == 2) {	// B
			list = mDBPool.getExamWord_difficulty(mExamQuestionCount);
		}else if(mExamType == 10){
			list = new ArrayList<DBPool.WordLog_for_exam>();
		}
		else {	// C
			list = mDBPool.getExamWord_All(mExamQuestionCount);
		}

		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();

		String exam_list = myCustomArray.toString();

		Log.e("string_word", ""+exam_list);

		new RetrofitService().getManagerService().retroInsertTestExam(mDBPool.getStudentId(),
																		exam_list)
				.enqueue(new Callback<ManagerData>() {
					@Override
					public void onResponse(Response<ManagerData> response, Retrofit retrofit) {

						ManagerInfo.getInstance(mContext).setAsyncReturnCode(response.body().getResult());
						ManagerInfo.getInstance(mContext).setAsyncReturnMessage("Network Error");


						List<ManagerData.TestExam> testExamList = response.body().getTest_paper();

						int length = testExamList.size();
						if (length != 0) {
							for (int i = 0; i < length; i++) {
								int rowid = testExamList.get(i).getWord_test_paper_id();
								int num = i + 1;
								int wordcode = testExamList.get(i).getWord_id();
								String word = testExamList.get(i).getWord();
								String example1 = testExamList.get(i).getExam1();
								String example2 = testExamList.get(i).getExam2();
								String example3 = testExamList.get(i).getExam3();
								String example4 = testExamList.get(i).getExam4();

								ExamQuestion mExamQuestion = new ExamQuestion(rowid, num, wordcode, word, example1, example2, example3, example4);

								if(mExamType!=10){
									try {
										String sentence = testExamList.get(i).getSentence();

										mExamQuestion.setSentence(sentence);
									} catch (Exception e) {
										e.toString();
									}
								}else{
									mExamQuestion.setSentence(null);
								}


								mQuestion_ArrayList.add(mExamQuestion);
							}

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									int size = mQuestion_ArrayList.size();
									ManagerInfo.getInstance(mContext).setExamQuestionCount(size);
									mExamQuestion_Adapter = new ExamQuestionAdapter(Manager_TakeExamActivity.this);

									for (int i = 0; i < size; i++) {
										mExamQuestion_Adapter.addListItem(mQuestion_ArrayList.get(i));
									}
									rl_activity_takeexam_progress.setVisibility(View.GONE);
									mQuestions_ListView.setVisibility(View.VISIBLE);
									takeexam_progress_view.stopAnimation();
									mQuestions_ListView.setAdapter(mExamQuestion_Adapter);
								}
							});

						} else {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									takeexam_progress_view.stopAnimation();
									Toast.makeText(getApplicationContext(), "시험을 생성하지 못하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
									finish();
								}
							});
						}
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG,  "onFailure : " + t.toString());
						new RetrofitService().getManagerService().retroUpdateFailMakeExam(mDBPool.getStudentId())
								.enqueue(new Callback<ManagerData>() {
									@Override
									public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {

												takeexam_progress_view.stopAnimation();
												int mReturnCode = ManagerInfo.getInstance(mContext).getAsyncReturnCode();
												String mReturnMessage_String = ManagerInfo.getInstance(mContext).getAsyncReturnMessage();

												String mErrorMeesage_String = "Error Message : 잠시후 다시 시도하세요";

												Log.e(TAG,  "   Exception ReturnCode : " + mReturnCode);
												Toast.makeText(Manager_TakeExamActivity.this, mErrorMeesage_String, Toast.LENGTH_SHORT).show();
												finish();
											}
										});
									}

									@Override
									public void onFailure(Throwable t) {
										Log.e(TAG,  "onFailure : " + t.toString());
									}
								});
					}
				});

	}

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.layout_actionbar_back: {
					Intent intent = new Intent(Manager_TakeExamActivity.this, Manager_StopExamPopup.class);
					intent.putExtra("test_id", mExamID);
					startActivity(intent);
				} break;
				case R.id.btn_examresultfooter_finish: {
					boolean bAllChecked = true;

					for(int i=0; i<mQuestion_ArrayList.size(); i++) {
						int rowid = mQuestion_ArrayList.get(i).getRowID();
						int selectednum = mQuestion_ArrayList.get(i).getSelectedNum();

						if(selectednum == 0)
							bAllChecked = false;

						ExamResult mExamWord = new ExamResult(rowid, selectednum);
						mResult_ArrayList.add(mExamWord);
					}

					if(bAllChecked) {
						mGson = new GsonBuilder().create();
						mJsonArray = mGson.toJsonTree(mResult_ArrayList).getAsJsonArray();
						mJsonArray_String = mJsonArray.toString();

						Log.e("KARAM", "Result " + mJsonArray_String);

						new RetrofitService().getManagerService().retroUpdateFinishTest(mJsonArray_String,
								                                                        ""+mDBPool.get_new_word_count())
								.enqueue(new Callback<ManagerData>() {
									@Override
									public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
										mExamID = response.body().getTest_id();
										Log.e("KARAM", "   ExamID : " + mExamID);

										ManagerInfo.getInstance(mContext).setExamID(mExamID);

										runOnUiThread(new Runnable() {

											@Override
											public void run() {

												Intent intent = new Intent(Manager_TakeExamActivity.this, Manager_ExamResultActivity.class);
												startActivity(intent);
												Manager_TakeExamActivity.this.finish();
											}
										});
									}

									@Override
									public void onFailure(final Throwable t) {
										runOnUiThread(new Runnable() {

											@Override
											public void run() {

												Log.e(TAG,  "onFailure : " + t.toString());
												int mReturnCode = ManagerInfo.getInstance(mContext).getAsyncReturnCode();
												//String mReturnMessage_String = ManagerInfo.getInstance(mContext).getAsyncReturnMessage();

												String mErrorMeesage_String = "Error Message : 잠시후 다시 시도하세요";
//												mErrorMeesage_String = mErrorMeesage_String.replace("msg", mReturnMessage_String);

												Log.e(TAG,  "   Exception ReturnCode : " + mReturnCode);
												Toast.makeText(Manager_TakeExamActivity.this, mErrorMeesage_String, Toast.LENGTH_SHORT).show();
											}
										});
									}
								});
					}
					else {
						Toast.makeText(Manager_TakeExamActivity.this, "시험문제를 모두 풀어주세요", Toast.LENGTH_SHORT).show();
					}
				} break;
			}
		}
	};

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();

		Intent intent = new Intent(Manager_TakeExamActivity.this, Manager_StopExamPopup.class);
		intent.putExtra("test_id", mExamID);
		startActivityForResult(intent, 111);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 111){
			finish();
		}
	}


	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
	}



}
