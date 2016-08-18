
package com.ihateflyingbugs.hsmd.manager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.ihateflyingbugs.hsmd.data.AsyncResultType;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Manager_ExamResultActivity extends Activity {
	String TAG = "ExamResultActivity";

	Context mContext;

	ImageView mExamType_ImageView;
	TextView mExamTypeContent_TextView;
	TextView mDetailContent_TextView;

	ListView mQuestions_ListView;


	//
	String mExamDate_String;

	int mExamCode_id;
	int mExamType;
	String mExamTypeContent_String;
	int mCount_Question;
	int mCount_Correct;
	int mCount_Incorrect;
	double mCorrectRate;
	String mComplete_DetailContent_String;

	DBPool mDBpool;

	//
	ExamQuestion_Result_Adapter ExamQuestion_Result_Adapter;
	ArrayList<ExamResult_Questions> mExamQuestion_ArrayList;



	//
	ActionBar mActionBar;
	View mCustomActionBarView;
	RelativeLayout rl_activity_resultexam_progress;
	CircularProgressView resultexam_progress_view;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_examresult);

		setActionBar();


		Log.e(TAG, "   onCreate");
		mContext = getApplicationContext();

		mDBpool = DBPool.getInstance(getApplicationContext());


		// init
		mQuestions_ListView = (ListView)findViewById(R.id.list_examresult_questions);

		View header = getLayoutInflater().inflate(R.layout.listheader_examresult_sendsms, null);
		mQuestions_ListView.addHeaderView(header);

		mExamType_ImageView = (ImageView)header.findViewById(R.id.img_examresult_examtype);
		mExamTypeContent_TextView = (TextView)header.findViewById(R.id.text_examresult_examtypecontent);
		mDetailContent_TextView = (TextView)header.findViewById(R.id.text_examresult_content);

		rl_activity_resultexam_progress = (RelativeLayout)findViewById(R.id.rl_activity_resultexam_progress);
		resultexam_progress_view = (CircularProgressView) findViewById(R.id.resultexam_progress_view);
		rl_activity_resultexam_progress.setVisibility(View.VISIBLE);
		mQuestions_ListView.setVisibility(View.GONE);
		resultexam_progress_view.setColor(Color.parseColor("#00b569"));
		resultexam_progress_view.startAnimation();




		//
		long now = System.currentTimeMillis();
		Date mDate = new Date(now);
		SimpleDateFormat format_date_to_string = new SimpleDateFormat("yyyy-MM-dd");
		mExamDate_String = format_date_to_string.format(mDate);
		mTitle_TextView.setText(mExamDate_String);


		mExamCode_id = ManagerInfo.getInstance(mContext).getExamID();
		mExamType = ManagerInfo.getInstance(mContext).getExamType();
		mExamTypeContent_String = URLDecoder.decode(ManagerInfo.getInstance(mContext).getExamTypeContent_String());
		mCount_Question = ManagerInfo.getInstance(mContext).getExamQuestionCount();


		// ExamType 
		if(mExamType == 1)
			mExamType_ImageView.setBackgroundResource(R.drawable.icon_exam_type_a);
		else if(mExamType == 2)
			mExamType_ImageView.setBackgroundResource(R.drawable.icon_exam_type_b);
		else if(mExamType == 3)
			mExamType_ImageView.setBackgroundResource(R.drawable.icon_exam_type_c);
		else if(mExamType == 10)
			mExamType_ImageView.setBackgroundResource(R.drawable.icon_exam_type_d);

		mExamTypeContent_TextView.setText(mExamTypeContent_String);

		//
		mExamQuestion_ArrayList = new ArrayList<ExamResult_Questions>();
		getExamResult_Questions();


	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
	}
	void getExamResult_Questions() {
		new RetrofitService().getManagerService().retroGetWordTestResult(""+mExamCode_id)
				.enqueue(new Callback<ManagerData>() {
					@Override
					public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
						try {
							int mReturnCode = response.body().getResult();

							if(mReturnCode == AsyncResultType.RESULT_SUCCESS) {
								List<ManagerData.TestExam> testExamList = response.body().getTest_paper();
								mCount_Question = testExamList.size();

								mCount_Correct = 0;
								for(int i=0; i<mCount_Question; i++) {
									int num = (i+1);
									int rowid = testExamList.get(i).getWord_test_paper_id();
									int wordcode = testExamList.get(i).getWord_id();
									String word = testExamList.get(i).getWord();
									int correctnum = testExamList.get(i).getCorrect_num();
									int EBS = testExamList.get(i).getCount_ebs();
									int SN = testExamList.get(i).getCount_sat();
									int PGW = testExamList.get(i).getCount_sat2();
									double forgetrate = testExamList.get(i).getForgetting_percent();
									String example1 = URLDecoder.decode(testExamList.get(i).getExam1());
									String example2 = URLDecoder.decode(testExamList.get(i).getExam2());
									String example3 = URLDecoder.decode(testExamList.get(i).getExam3());
									String example4 = URLDecoder.decode(testExamList.get(i).getExam4());
									int studentchoice = testExamList.get(i).getChoice_num();
									if(studentchoice==correctnum){

										Word word_info = mDBpool.getWord(wordcode);


										mDBpool.isMaxState(word_info.getState());
										mDBpool.updateForgettingCurvesByNewInputs(word_info, Config.EXAMTEST,  true);
										mDBpool.insertReviewTimeToGetMemoryCoachMent(word_info);
										mDBpool.insertLevel(word_info, false);
									}else{
										Word word_info = mDBpool.getWord(wordcode);

										mDBpool.updateForgettingCurvesByNewInputs(word_info,  Config.EXAMTEST, false);
										mDBpool.insertReviewTimeToGetMemoryCoachMent(word_info);
										mDBpool.insertLevel(word_info, false);
									}

									ExamResult_Questions mExamQuestion = new ExamResult_Questions(num, rowid, wordcode, word, correctnum, example1, example2, example3, example4,EBS, SN, PGW,forgetrate, studentchoice);
									if(mExamType!=10){
										try {
											String sentence = testExamList.get(i).getSentence();
											String sentence_mean = URLDecoder.decode(testExamList.get(i).getSentence_translation());
											String sentence_info = URLDecoder.decode(testExamList.get(i).getSentence_info());

											mExamQuestion.setSentence(sentence);
											mExamQuestion.setSentence_Mean(sentence_mean);
											mExamQuestion.setSentence_Info(sentence_info);
										} catch(Exception e) {
											Log.e(TAG, "Sentence Null " + e.toString());
										}

									}else{
										mExamQuestion.setSentence(null);
										mExamQuestion.setSentence_Mean(null);
										mExamQuestion.setSentence_Info(null);
									}

									mExamQuestion_ArrayList.add(mExamQuestion);


									if(correctnum == studentchoice)
										mCount_Correct += 1;
									else
										mCount_Incorrect += 1;
								}

								ArrayList<DBPool.word_log> list =mDBpool.get_word_log();
								Gson gson = new GsonBuilder().create();
								JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();
								String word_log = myCustomArray.toString();
								new RetrofitService().getStudyInfoService().retroInsertWordLog(mDBpool.getStudentId(),
																								word_log
										).enqueue(new Callback<StudyInfoData>() {
									@Override
									public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

									}

									@Override
									public void onFailure(Throwable t) {
										Log.e(TAG,  "   Exception retroInsertWordLog : " + t.toString());
									}
								});

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										int size = mExamQuestion_ArrayList.size();
										ExamQuestion_Result_Adapter = new ExamQuestion_Result_Adapter(Manager_ExamResultActivity.this);

										for(int i =0; i<size; i++) {
											ExamQuestion_Result_Adapter.addListItem(mExamQuestion_ArrayList.get(i));
										}
										mQuestions_ListView.setAdapter(ExamQuestion_Result_Adapter);

										rl_activity_resultexam_progress.setVisibility(View.GONE);
										mQuestions_ListView.setVisibility(View.VISIBLE);
										resultexam_progress_view.stopAnimation();

										mCorrectRate = ((float)mCount_Correct / (float)mCount_Question) * 100.0;

										// ExamResult Content
										mComplete_DetailContent_String = getString(R.string.examresult_content);
										mComplete_DetailContent_String = mComplete_DetailContent_String.replace("questioncount", Integer.toString(mCount_Question));
										mComplete_DetailContent_String = mComplete_DetailContent_String.replace("incorrectcount", Integer.toString(mCount_Incorrect));
										mComplete_DetailContent_String = mComplete_DetailContent_String.replace("correctcount", Integer.toString(mCount_Correct));
										mComplete_DetailContent_String = mComplete_DetailContent_String.replace("correctrate", String.format("%.1f", mCorrectRate));
										mDetailContent_TextView.setText(mComplete_DetailContent_String);


										MainActivity.sendWorkEachCount(mDBpool);

									}
								});
							}
						} catch(Exception e) {
							Log.e(TAG, "   Async Response Error : " + e.toString());
						}
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG,  "   Exception retroGetWordTestResult : " + t.toString());
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
//								int mReturnCode = ManagerInfo.getInstance(mContext).getAsyncReturnCode();
//								String mReturnMessage_String = ManagerInfo.getInstance(mContext).getAsyncReturnMessage();

								String mErrorMeesage_String = "Error Message : 잠시후 다시 시도하세요";
//								mErrorMeesage_String = mErrorMeesage_String.replace("msg", mReturnMessage_String);
								rl_activity_resultexam_progress.setVisibility(View.GONE);
								resultexam_progress_view.stopAnimation();

								Toast.makeText(Manager_ExamResultActivity.this, mErrorMeesage_String, Toast.LENGTH_SHORT).show();
							}
						});
					}
				});

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
				Manager_ExamResultActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
	}
}
