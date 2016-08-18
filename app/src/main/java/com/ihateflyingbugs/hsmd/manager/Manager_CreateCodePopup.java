package com.ihateflyingbugs.hsmd.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.gsm.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Manager_CreateCodePopup extends Activity {
	String TAG = "CreateCodePopup";

	SharedPreferences mPreference;
	Context mContext;

	RelativeLayout mCreateCode_Layout;
	Button mNewCode_Button;
	ProgressBar mProgressBar;

	RelativeLayout mNewCode_Layout;
	TextView mCode_TextView;
	Button mSendSMS_Button;
	Button mSendKAKAO_Button;
	Button mNewCodeClose_Button;

	RelativeLayout mSendSMS_Layout;
	EditText mPhoneNumber_EditText;
	Button mSend_Button;
	Button mSendSMSClose_Button;


	//
	String mStudentName_String;
	String mStudentCode_String;

	String mPhoneNumber_String;

	InputMethodManager imm;
	DBPool mDBPool;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_manager_create_code);

		mPreference = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		mContext = getApplicationContext();
		mDBPool = DBPool.getInstance(mContext);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		mCreateCode_Layout = (RelativeLayout)findViewById(R.id.layout_createcode_code);
		mNewCode_Button = (Button)findViewById(R.id.btn_createcode_newcode);
		mProgressBar = (ProgressBar)findViewById(R.id.progress_manager_createcode);

		mNewCode_Layout = (RelativeLayout)findViewById(R.id.layout_createcode_newcode);
		mCode_TextView = (TextView)findViewById(R.id.text_createcode_newcode_code);
		mSendSMS_Button = (Button)findViewById(R.id.btn_createcode_newcode_sendsms);
		mSendKAKAO_Button = (Button)findViewById(R.id.btn_createcode_newcode_sendkakao);
		mNewCodeClose_Button = (Button)findViewById(R.id.btn_createcode_newcode_close);

		mSendSMS_Layout = (RelativeLayout)findViewById(R.id.layout_createcode_sendsms);
		mPhoneNumber_EditText = (EditText)findViewById(R.id.edit_createcode_sendsms_phonenumber);
		mSend_Button = (Button)findViewById(R.id.btn_createcode_sendsms_send);
		mSendSMSClose_Button = (Button)findViewById(R.id.btn_createcode_sendsms_close);



		//
		mStudentName_String = mPreference.getString(MainValue.GpreName, "");
		mStudentCode_String = randomCode();
		mCode_TextView.setText(mStudentCode_String);

		//
		mPhoneNumber_EditText.setInputType(InputType.TYPE_CLASS_PHONE);
		mPhoneNumber_EditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


		//
		mCreateCode_Layout.setVisibility(View.VISIBLE);
		mNewCode_Layout.setVisibility(View.GONE);
		mSendSMS_Layout.setVisibility(View.GONE);

		mNewCode_Button.setOnClickListener(ClickListener);
		mSendSMS_Button.setOnClickListener(ClickListener);
		mSendKAKAO_Button.setOnClickListener(ClickListener);
		mNewCodeClose_Button.setOnClickListener(ClickListener);
		mSend_Button.setOnClickListener(ClickListener);
		mSendSMSClose_Button.setOnClickListener(ClickListener);
	}


	OnClickListener ClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.btn_createcode_newcode: {
					mProgressBar.setVisibility(View.VISIBLE);
					uploadCode();
				} break;

				case R.id.btn_createcode_newcode_sendsms: {
					mNewCode_Layout.setVisibility(View.GONE);
					mSendSMS_Layout.setVisibility(View.VISIBLE);
				} break;

				case R.id.btn_createcode_newcode_sendkakao: {
					//TODO
					try {
						sendUrlLink(v, mStudentCode_String);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						Toast.makeText(Manager_CreateCodePopup.this, "코드를 전송할 수 없습니다. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

					}
				} break;

				case R.id.btn_createcode_sendsms_send: {
					mPhoneNumber_String = mPhoneNumber_EditText.getText().toString();

					if(mPhoneNumber_String.getBytes().length <= 0) {	// 전화번호 입력이 없을때
						FlurryAgent.logEvent(TAG + " -> SendSMS Click : NoInput Error");
						Toast.makeText(Manager_CreateCodePopup.this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
					}
					else if(mPhoneNumber_String.getBytes().length > 13) {	// 전화번호 길이가 맞지 않을때
						FlurryAgent.logEvent(TAG + " -> SendSMS Click : Length Error");
						Toast.makeText(Manager_CreateCodePopup.this, "형식이 올바르지 않습니다\n다시 입력하세요", Toast.LENGTH_SHORT).show();
					}
					else {
						FlurryAgent.logEvent(TAG + " -> SendSMS Click");

						imm.hideSoftInputFromWindow(mPhoneNumber_EditText.getWindowToken(), 0);

						mPhoneNumber_String = mPhoneNumber_String.replace("-", "");

						String mSMSMessage = mStudentName_String + "회원님의 등록번호는 [" + mStudentCode_String + "]입니다. 밀당매니저내 '학생등록하기'버튼 터치 후 등록번호를 입력하세요";
						Log.e(TAG, "  SMS  : " + mPhoneNumber_String + "  " + mSMSMessage);

						// send sms
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(mPhoneNumber_String, null, mSMSMessage, null, null);

						mNewCode_Layout.setVisibility(View.VISIBLE);
						mSendSMS_Layout.setVisibility(View.GONE);
					}
				} break;


				case R.id.btn_createcode_newcode_close: {
					Manager_CreateCodePopup.this.finish();
				} break;

				case R.id.btn_createcode_sendsms_close: {
					mNewCode_Layout.setVisibility(View.VISIBLE);
					mSendSMS_Layout.setVisibility(View.GONE);
				} break;

			}
		}
	};

	public void uploadCode() {
		new RetrofitService().getManagerService().retroInsertRelationCode(mDBPool.getStudentId(),
																		mStudentCode_String)
				.enqueue(new Callback<ManagerData>() {
					@Override
					public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
						ManagerInfo.getInstance(mContext).setStudentCode_String(mStudentCode_String);

						runOnUiThread(new Runnable() {
							public void run() {
								mProgressBar.setVisibility(View.GONE);
								mCreateCode_Layout.setVisibility(View.GONE);
								mNewCode_Layout.setVisibility(View.VISIBLE);
								Toast.makeText(mContext, "서버에 등록하였습니다.", Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(mContext, "연결오류. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}

	public void sendUrlLink(View v, String code) throws NameNotFoundException {
		// Recommended: Use application mContext for parameter.

		// int checkedRadioButtonId = rg.getCheckedRadioButtonId();

		// check, intent is available.

		KakaoLink kakaoLink = null;
		try {
			kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
		} catch (KakaoParameterException e1) {
			// TODO Auto-generated catch block
			Log.e("rrrr", "" + e1);
		}

		final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
				.createKakaoTalkLinkMessageBuilder();


		String mssg = mPreference.getString(MainValue.GpreName, "이름없음")+"회원님의 등록번호는\n[ "+ code+" ] 입니다. 밀당매니저내 ‘학생등록하기’ 버튼 터치 후 등록번호를 입력하세요.";
		String linkContents = null;
		try {
			kakaoTalkLinkMessageBuilder.addText(mssg).build();

		} catch (KakaoParameterException e) {
			// TODO Auto-generated catch block
			Log.e("rrrr", "" + e);
			e.printStackTrace();
		}

		try {
			kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
		} catch (KakaoParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FlurryAgent.logEvent("BaseActivity:MakeManagerCode");

		// kakaoLink
		// .openKakaoLink(
		// this,
		// "http://bit.ly/MDAppDwns",
		// mssg,
		// getPackageName(),
		// getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
		// "밀당 영단어", "UTF-8");
	}


	public String randomCode() {
		Random mRandom = new Random();
		StringBuffer buf = new StringBuffer();

		for(int i=0; i<8; i++) {
			/*	if(mRandom.nextBoolean()) {
				buf.append((char)((int)(mRandom.nextInt(26)) + 65));
			}
			else {*/
			buf.append(mRandom.nextInt(10));
			//	}
		}

		return buf.toString();
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
