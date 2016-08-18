package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.hsmd.data.AsyncResultType;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.InstallActivity;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class KakaoLoginActivity extends Activity {
	String TAG = "KakaoLoginActivity";

	SessionCallback callback;

	ProgressBar pb_kakaologin_login;
	SharedPreferences mPreference;
	Map<String, String> articleParams = new HashMap<String, String>();
	protected boolean isStart = false;
	protected boolean isActivity= false;

	private Handler handler;
	LoginButton bt_kakaologin_login;
	DBPool db;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_kakao_login_small);
		}else{
			setContentView(R.layout.activity_kakao_login);
		}


		Log.e(TAG, "kakaologinactivity  "+isActivity);

		if(mPreference == null){
			mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		}
		handler = new Handler();

		bt_kakaologin_login = (LoginButton)findViewById(R.id.bt_kakaologin_login);

		pb_kakaologin_login = (ProgressBar)findViewById(R.id.pb_kakaologin_login);


//		UserManagement.requestLogout(new LogoutResponseCallback() {
//			@Override
//			public void onCompleteLogout() {
//				//로그아웃 성공 후 하고싶은 내용 코딩 ~
//				Log.e("UserProfile", "onCompleteLogout");
//			}
//		});

		callback = new SessionCallback();
		Session.getCurrentSession().addCallback(callback);


	}


	private class SessionCallback implements ISessionCallback {


		@Override
		public void onSessionOpened() {

			UserManagement.requestMe(new MeResponseCallback() {

				@Override
				public void onFailure(ErrorResult errorResult) {
					String message = "failed to get user info. msg=" + errorResult;
					Logger.d(message);
					Log.e("UserProfile", "onFailure");

					ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
					if (result == ErrorCode.CLIENT_ERROR_CODE) {
						finish();
					} else {
						//redirectMainActivity();
					}
				}

				@Override
				public void onSessionClosed(ErrorResult errorResult) {
					pb_kakaologin_login.setVisibility(View.VISIBLE);
					Log.e(TAG, "onSessionClosed");
				}

				@Override
				public void onNotSignedUp() {
					Log.e("UserProfile", "onNotSignedUp");
				}

				@Override
				public void onSuccess(UserProfile userProfile) {

					Log.e(TAG, "onSessionOpened_IN:onSuccess");
					Log.e(TAG, "onSuccess end");
					if (userProfile != null)
						userProfile.saveUserToCache();
					PackageManager packageManager = getApplicationContext().getPackageManager();
					PackageInfo infor = null;
					try {
						infor = packageManager.getPackageInfo(getApplicationContext().getPackageName(),      PackageManager.GET_META_DATA);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String cur_version = infor.versionName;
					int code = infor.versionCode;
					Config.VERSION= cur_version;
					Config.USER_ID  =String.valueOf(userProfile.getId());
					db = DBPool.getInstance(getApplicationContext());
					db.insertLoginId(Config.USER_ID);
					FlurryAgent.setUserId(Config.USER_ID);

					if(mPreference.getString(MainActivitys.GpreName, "0").equals("0")){
						Log.e(TAG, mPreference.getString(MainActivitys.GpreName, "0"));
						mPreference.edit().putString(MainActivitys.GpreName, userProfile.getNickname()).commit();
						Log.e(TAG,  ""+userProfile.getNickname());
						Config.NAME =  userProfile.getNickname();
					}else{
						Log.e(TAG, mPreference.getString(MainActivitys.GpreName, "0"));
						mPreference.edit().putString(MainActivitys.GpreName, userProfile.getNickname()).commit();
						Log.e(TAG,  ""+userProfile.getNickname());
					}

					if(!isStart){
						Date dated = new Date();
						check_id(db.getLoginId(), cur_version, getApplicationContext());
					}else{
						isStart =true;
					}

				}
			});

		}

		@Override
		public void onSessionOpenFailed(KakaoException exception) {
			// 세션 연결이 실패했을때
			// 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ
			Log.e("UserProfile", "onSessionOpenFailed"+ exception);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/* (non-Javadoc)
         * @see android.app.Activity#onResume()
         */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, "kakaologinactivity  "+isActivity);

	}


	@Override
	public boolean isFinishing() {
		// TODO Auto-generated method stub
		return super.isFinishing();
	}


	public void check_id(String login_id, String version ,Context context){
		Log.e("UserProfile", "DATA : "+ db.getLoginId()+ "  "+ version+"  "+ GCMRegistrar.getRegistrationId(getApplicationContext()));
		String push_id = "NULL";
		if(!GCMRegistrar.getRegistrationId(getApplicationContext()).equals("")){
			push_id =GCMRegistrar.getRegistrationId(getApplicationContext());
		}
		new RetrofitService().getAuthorizationService().retroIsExistStudentID(db.getLoginId(),
				"0",
				version,
		 		""+GCMRegistrar.getRegistrationId(getApplicationContext())).enqueue(new Callback<AuthorizationData>() {
			@Override
			public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
				int result = response.body().getResult();
				if (result == AsyncResultType.RESULT_NOT_EXIST) {

					final Intent intent = new Intent(KakaoLoginActivity.this, InstallActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(intent);

				} else if (result == AsyncResultType.RESULT_AREALDY_EXIST) {

					String student_id = response.body().getStudent_id();
					db.insertStudentID(student_id);
					if (mPreference.getString(MainActivitys.GpreTutorial, "0").equals("1")) {
						final Intent intent = new Intent(KakaoLoginActivity.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						finish();
						startActivity(intent);
					} else {
						final Intent intent = new Intent(KakaoLoginActivity.this, com.ihateflyingbugs.hsmd.animtuto.MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Log.e("getRegistrationId", "startactivity");
						finish();
						startActivity(intent);
					}

				} else if (result == AsyncResultType.RESULT_IS_LEFT_STUDENT) {

					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AlertDialog.Builder dialog = new AlertDialog.Builder(KakaoLoginActivity.this);
							dialog.setTitle("밀당 영단어");
							dialog.setMessage("이미 탈퇴한 사용자입니다. 다시 사용을 원하는 경우 cs@lnslab.com으로 연락바랍니다.");
							dialog.setNeutralButton("확인", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							});
							dialog.create().show();
						}
					});

				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AlertDialog.Builder dialog = new AlertDialog.Builder(KakaoLoginActivity.this);
							dialog.setTitle("밀당 영단어");
							dialog.setMessage("잘못된 접근입니다. 앱을 완전히 종료한 후에 다시 시도해주세요.");
							dialog.setNeutralButton("확인", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							});
							dialog.create().show();
						}
					});
				}

			}

			@Override
			public void onFailure(Throwable t) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						AlertDialog.Builder dialog = new AlertDialog.Builder(KakaoLoginActivity.this);
						dialog.setTitle("밀당 영단어");
						dialog.setMessage("잘못된 접근입니다. 앱을 완전히 종료한 후에 다시 시도해주세요.");
						dialog.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
						dialog.create().show();
					}
				});
			}
		});


//		new Async_check_id(Config.USER_ID, Config.VERSION, getApplicationContext(),new VocaCallback() {
//
//			@Override
//			public void Resonponse(JSONObject response) {
//				// TODO Auto-generated method stub
//
//				int success = 0;
//				try {
//					success = response.getInt(Config.TAG_SUCCESS);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if (!isActivity) {
//					if(success == Async_check_id.ID_DUPLICATION){
//						//메인
//						if(mPreference.getString(MainActivitys.GpreTutorial,"0").equals("1")){
//							final Intent intent = new Intent(KakaoLoginActivity.this, MainActivity.class);
//							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//							finish();
//							startActivity(intent);
//						}else{
//							final Intent intent = new Intent(KakaoLoginActivity.this, com.ihateflyingbugs.hsmd.animtuto.MainActivity.class);
//							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//							finish();
//							startActivity(intent);
//						}
//
//						isActivity = true;
//					}else if(success == Async_check_id.ID_SUCCESS){
//						//성공
//						final Intent intent = new Intent(KakaoLoginActivity.this, InstallActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						finish();
//						startActivity(intent);
//						isActivity = true;
//					}else{
//						final Intent intent = new Intent(KakaoLoginActivity.this, InstallActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						finish();
//						startActivity(intent);
//						isActivity = true;
//					}
//				}
//			}
//
//			@Override
//			public void Exception() {
//				// TODO Auto-generated method stub
//
//			}
//		}).execute();
	}


	String starttime;
	String startdate;
	Date dates = new Date();

	public void onStart()
	{

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("KakaoLoginActivity", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e(TAG, startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.endTimedEvent("KakaoLoginActivity");
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}



}
