package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.Mean;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailWord_ReportPopup extends Activity {
	String TAG = "DetailWord_ReportPopup";

	DBPool mDBPool;
	SharedPreferences settings;

	TextView mWord_TextView;
	TextView mWordClass_TextView;
	TextView mWordMean_TextView;
	EditText mComment_EditText;
	Button mClose_Button;
	Button mReport_Button;


	int mWordCode;
	Word mWord;
	Mean mMean;

	String mWord_String;

	int mMeanSize;
	String Mean_N;
	String Mean_V;
	String Mean_A;
	String Mean_AD;
	String Mean_CONJ;
	String mWordClass_String;
	String mWordMean_String;

	String mComment_String;

	Handler handler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("KARAM", TAG + "  onCreate");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.popup_detailword_report);


		mDBPool = DBPool.getInstance(this);
		//settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

		handler = new Handler();

		// Init
		mWord_TextView = (TextView)findViewById(R.id.text_detailwordpopup_report_word);
		mWordClass_TextView = (TextView)findViewById(R.id.text_detailwordpopup_report_wordclass);
		mWordMean_TextView = (TextView)findViewById(R.id.text_detailwordpopup_report_wordmean);
		mComment_EditText = (EditText)findViewById(R.id.edit_detailwordpopup_report_comment);
		mClose_Button = (Button)findViewById(R.id.btn_detailwordpopup_report_close);
		mReport_Button = (Button)findViewById(R.id.btn_detailwordpopup_report_report);


		// 
		mWordCode = getIntent().getIntExtra("wordcode", 0);

		mWord = mDBPool.getWord(mWordCode);
		mWord_String = mWord.getWord();
		mWord_TextView.setText(mWord_String);


		mMeanSize = mWord.getMeanList().size();
		Mean_N = "";
		Mean_V = "";
		Mean_A = "";
		Mean_AD = "";
		Mean_CONJ = "";
		mWordClass_String = "";
		mWordMean_String = "";

		if(mMeanSize == 0) {
			if(mWord.getMeanList().size()==0){
				mWord.setMeanList(mDBPool.getMean(mWord.get_id()));

				mMeanSize = mWord.getMeanList().size();

				if(mMeanSize != 0) {
					for (int i = 0; i < mMeanSize; i++) {
						mMean = mWord.getMean(i);
						int key = mMean.getMClass();

						switch (key) {
							case Word.Class_N: // noun : 1
								Mean_N += mMean.getMeaning() + ", ";
								break;
							case Word.Class_V: // verb : 2
								Mean_V += mMean.getMeaning() + ", ";
								break;
							case Word.Class_A: // adjective : 3
								Mean_A += mMean.getMeaning() + ", ";
								break;
							case Word.Class_Ad: // adverb : 4
								Mean_AD += mMean.getMeaning() + ", ";
								break;

							case Word.Class_Conj: // conjunction : 5
								Mean_CONJ += mMean.getMeaning() + ", ";
								break;
						}
					}

					switch (mWord.getP_class()) {
						case Word.Class_N: {
							String mClassN_String = "n.\n";
							mWordClass_String += mClassN_String;
							String mMeanN_String = Mean_N.substring(0, Mean_N.length() - 2);
							mWordMean_String += mMeanN_String + "\n";
						} break;

						case Word.Class_V: {
							String mClassV_String = "v.\n";
							mWordClass_String += mClassV_String;
							String mMeanV_String = Mean_V.substring(0, Mean_V.length() - 2);
							mWordMean_String += mMeanV_String + "\n";
						} break;

						case Word.Class_A: {
							String mClassA_String = "a.\n";
							mWordClass_String += mClassA_String;
							String mMeanA_String = Mean_A.substring(0, Mean_A.length() - 2);
							mWordMean_String += mMeanA_String + "\n";
						} break;

						case Word.Class_Ad: {
							String mClassAd_String = "ad.\n";
							mWordClass_String += mClassAd_String;
							String mMeanAd_String = Mean_AD.substring(0, Mean_AD.length() - 2);
							mWordMean_String += mMeanAd_String + "\n";
						} break;

						case Word.Class_Conj: {
							String mClassConj_String = "conj.\n";
							mWordClass_String += mClassConj_String;
							String mMeanConj_String = Mean_CONJ.substring(0, Mean_CONJ.length() - 2);
							mWordMean_String += mMeanConj_String + "\n";
						} break;
					}

					// 두번째, 세번째 단어 뜻 셋팅
					HashMap<Integer, Boolean> hm = mWord.getmClassList();
					hm.remove(mWord.getP_class());

					Iterator<Integer> iterator = hm.keySet().iterator();
					int line = 0;

					while (iterator.hasNext()) {
						int key = iterator.next();
						String multi_mean = "";
						String mclass = "";

						switch (key) {
							case Word.Class_N: {
								String mClassN_String = "n.\n";
								mWordClass_String += mClassN_String;
								String mMeanN_String = Mean_N.substring(0, Mean_N.length() - 2);
								mWordMean_String += mMeanN_String + "\n";
							} break;

							case Word.Class_V: {
								String mClassV_String = "v.\n";
								mWordClass_String += mClassV_String;
								String mMeanV_String = Mean_V.substring(0, Mean_V.length() - 2);
								mWordMean_String += mMeanV_String + "\n";
							} break;

							case Word.Class_A: {
								String mClassA_String = "a.\n";
								mWordClass_String += mClassA_String;
								String mMeanA_String = Mean_A.substring(0, Mean_A.length() - 2);
								mWordMean_String += mMeanA_String + "\n";
							} break;

							case Word.Class_Ad: {
								String mClassAd_String = "ad.\n";
								mWordClass_String += mClassAd_String;
								String mMeanAd_String = Mean_AD.substring(0, Mean_AD.length() - 2);
								mWordMean_String += mMeanAd_String + "\n";
							} break;

							case Word.Class_Conj: {
								String mClassConj_String = "conj.\n";
								mWordClass_String += mClassConj_String;
								String mMeanConj_String = Mean_CONJ.substring(0, Mean_CONJ.length() - 2);
								mWordMean_String += mMeanConj_String + "\n";
							} break;
						}
					}
				}
			}
		}
		mWordClass_TextView.setText(mWordClass_String);
		mWordMean_TextView.setText(mWordMean_String);

		mClose_Button.setOnClickListener(ClickListener);
		mReport_Button.setOnClickListener(ClickListener);
	}

	OnClickListener ClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.btn_detailwordpopup_report_close: {
					DetailWord_ReportPopup.this.finish();
				} break;

				case R.id.btn_detailwordpopup_report_report: {
					Log.e(TAG, "start");
					mComment_String = mComment_EditText.getText().toString();

					if(mComment_String.getBytes().length <= 0) {
						Toast.makeText(DetailWord_ReportPopup.this, "입력해주세요", Toast.LENGTH_SHORT).show();
					}
					else {
						// 서버에 올리기~
						Log.e(TAG, "else");
						new RetrofitService().getUseAppInfoService().retroInsertWordReport(mDBPool.getStudentId(),
																							""+mWord.get_id(),
																							mComment_String)
								.enqueue(new Callback<UseAppInfoData>() {
									@Override
									public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method stub
												Toast.makeText(DetailWord_ReportPopup.this, "소중한 의견 감사합니다", Toast.LENGTH_SHORT).show();
												DetailWord_ReportPopup.this.finish();

											}
										});
									}

									@Override
									public void onFailure(Throwable t) {
										Log.e(TAG, "onFailure : "+t.toString());
										handler.post(new Runnable() {
											public void run() {
												Toast.makeText(DetailWord_ReportPopup.this, "등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();

											}
										});
									}
								});
					}
				} break;
			}
		}
	};


	String starttime;
	String startdate;
	Date date = new com.ihateflyingbugs.hsmd.data.Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("ShowWordInfoPopup", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
	}
}
