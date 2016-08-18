package com.ihateflyingbugs.hsmd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.model.BoardData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class QnAActivity extends Activity implements OnClickListener {
	String TAG = "QnAActivity";

	Activity mActivity;
	Context mContext;
	DBPool db;
	Handler handler;

	//
	EditText mPhoneNumber_EditText;
	EditText mContent_EditText;
	Button mSendQnA_Button;
	ProgressBar mProgressBar;


	String mStudentID_String;

	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_qna);

		setActionBar();


		mActivity = this;
		mContext = getApplicationContext();
		db = DBPool.getInstance(mContext);
		handler = new Handler();

		//
		mPhoneNumber_EditText = (EditText) findViewById(R.id.edit_sendqna_phone);
		mContent_EditText = (EditText) findViewById(R.id.edit_sendqna_content);
		mSendQnA_Button = (Button) findViewById(R.id.btn_sendqna_send);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_sendqna);

		mSendQnA_Button.setOnClickListener(this);


		if (db.getStudentId().length() > 0) {
			mStudentID_String = db.getStudentId();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_sendqna_send:
				mProgressBar.setVisibility(View.VISIBLE);
				String phone = mPhoneNumber_EditText.getText().toString();
				String text = mContent_EditText.getText().toString();

				try {
					if (mPhoneNumber_EditText.length() < 10) {
						Toast.makeText(mActivity, "핸드폰 번호는 10자 이상이여야 합니다.", Toast.LENGTH_SHORT).show();
						mProgressBar.setVisibility(View.GONE);
						return;
					}

					if (text.length() < 5) {
						Toast.makeText(mActivity, "내용은 5자 이상이여야 합니다.", Toast.LENGTH_SHORT).show();
						mProgressBar.setVisibility(View.GONE);
						return;
					}

					new RetrofitService().getBoardService().retroSendQnABoard(db.getStudentId(),phone,text).enqueue(new Callback<BoardData>() {
						@Override
						public void onResponse(Response<BoardData> response, Retrofit retrofit) {
							handler.post(new Runnable() {

								@Override
								public void run() {
									FlurryAgent.logEvent("QnAWriteActivity:Click_Send_Qna");
									mProgressBar.setVisibility(View.GONE);
									new AlertDialog.Builder(mActivity).setTitle("문의 사항이 접수되었습니다.")
											.setMessage("최대한 빠른시일 내에 \n답변드리도록 하겠습니다.\n감사합니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											QnAActivity.this.finish();
										}
									}).show();
								}
							});
						}

						@Override
						public void onFailure(Throwable t) {

							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Toast.makeText(QnAActivity.this, "전송이 실패 하였습니다.", Toast.LENGTH_SHORT).show();
									mProgressBar.setVisibility(View.GONE);

								}
							});
						}
					});
				} catch (NullPointerException e) {
					return;
				}

				break;
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
				FlurryAgent.logEvent("QnAWriteActivity:Click_Close_BackButton");
				QnAActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("1:1 문의하기");
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
		FlurryAgent.logEvent("QnAActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("QnAActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
