package com.ihateflyingbugs.hsmd;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.model.WordUpdateData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.ChatHeadService;
import com.ihateflyingbugs.hsmd.service.ChatHeadService.LocalBinder;
import com.ihateflyingbugs.hsmd.tutorial.SplashActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.ihateflyingbugs.hsmd.CommonUtilities.SENDER_ID;
import static com.ihateflyingbugs.hsmd.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

	final static int LOG_OUT = 0;

	private static final String TAG = "GCMIntentService";

	Long idx;

	private ChatHeadService mService;    // 연결 타입 서비스
	private boolean mBound = false;    // 서비스 연결 여부

	public GCMIntentService() {
		super(SENDER_ID);
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		android.os.Debug.waitForDebugger();
	}
	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		displayMessage(context, "Your device registred with GCM");

		// Log.d("NAME", VOCAconfig.name);
		ServerUtilities.register(context, registrationId);

	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		String message = intent.getExtras().getString("message");
		// int sort = intent.getExtras().getInt("sort");
		Log.v("pushmessage", ""+message);
		int sort;
		try {
			sort = Integer.parseInt((intent.getExtras().getString("sort")));
		} catch (NumberFormatException e) {
			// TODO: handle exception
			return;
		}

		idx = Long.parseLong(intent.getStringExtra("idx"));


		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		new RetrofitService().getUseAppInfoService().retroUpdateUserPushLog(idx+"", "1592",  pm.isScreenOn()+"")
				.enqueue(new Callback<UseAppInfoData>() {
					@Override
					public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {

					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure : "+t.toString() );
					}
				});
		displayMessage(context, message);
		// notifies user
		generateNotification(context, intent, sort, message);
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		int sort = 1;
		// notifies user
		generateNotification(context, null, sort, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	static SharedPreferences settings;
	Intent notificationIntent;

	DBPool db;
	String url = "http://lnslab.com/sat/loading.php";
	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private void generateNotification(final Context context, Intent pushIntent, int sort, String message) {

		settings = context.getSharedPreferences(MainValue.preName,
				Context.MODE_PRIVATE);

		Intent serviceIntent = new Intent(this, ChatHeadService.class);
		serviceIntent.setPackage("com.ihateflyingbugs.hsmd.service");
//		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
		int notiId = 0;
		int icon = R.drawable.icon48;
		long when = System.currentTimeMillis();
		String msg = "안녕하세요 밀당영단어입니다.";
		String summary = "밀당 영단어";
		int largeIconRes = R.drawable.launcher_xxhdpi;

		db = DBPool.getInstance(getApplicationContext());

		Boolean receivePushFlag = true;


		String birth = settings.getString(MainValue.GpreBirth, "900000");

		int sum = 0;

		if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
			sum = 1900;
			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
			sum += years;
		} else {
			sum = 2000;
			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
			sum += years;
		}
		int age = Integer.valueOf(TimeClass.getYear()) - sum;

		String students_year;

		if (age <= 16) {
			students_year = "중학생";
		} else if (age == 17) {
			students_year = "고1학생";
		} else if (age == 18) {
			students_year = "고2학생";
		} else if (age == 19) {
			students_year = "고3학생";
		} else if (age > 19) {
			students_year = "재수생";
		} else{
			students_year = "안얄랴줌";
		}

		int ledColor = 0xFF000000;

		Log.d("GCMReceive", message + " " + sort);

		// sort = 0;

		// 7  : 개인에게 보내는 공지사항 푸시
		// 8  : 공지사항
		// 9  : 업데이트
		// 1X : 적중
		// 2X : 의지
		// 3X : 기억
		if (sort == LOG_OUT) {
			msg = "다른 기기에서 로그인 되었습니다.";
		} else if (sort == 7 || sort == 8) { // 일반, 공지사항 푸시
			msg = pushIntent.getStringExtra("contents");
			notiId = 0;
			notificationIntent = new Intent(context, SplashActivity.class);
		} else if (sort == 9) { // 업데이트 푸시
			msg = "밀당영단어의 업데이트가 있습니다.";
			notiId = 0;
			Uri uri = Uri.parse("market://details?id=com.ihateflyingbugs.hsmd");
			notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
		} else if (sort == 21) { // 의지관련 푸시
			notiId = 2;
			ledColor = 0xFFFF0000;
			int same_age_count=100;
			int today_login_count=100;
			try {
				same_age_count = Integer.parseInt(pushIntent.getStringExtra("same_age_count"));
				today_login_count = Integer.parseInt(pushIntent.getStringExtra("today_login_count"));
			} catch (Exception e) {
				// TODO: handle exception
			}


			String percent = String.format("%.1f", (Double.isNaN(today_login_count*100.0/same_age_count)  ? 0.0 : (today_login_count*100.0/same_age_count)));

			msg = "오늘은 " + students_year + " " + same_age_count + "명 중에 " + today_login_count
					+ "(" + percent + "%)명이 단어를 암기했습니다. 접속하셔서 적중률 93%의 단어를 만나보세요!";
			summary = "불꽃 의지";
			largeIconRes = R.drawable.push_icon_will;

			if(!settings.getBoolean(MainValue.GpreWillPush, true)){
				receivePushFlag = false;
			}
			Log.d("Notification", settings.getBoolean(MainValue.GpreWillPush, true) + " " + receivePushFlag);
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("pushresponse", true);
		} else if (sort == 22){ // 의지관련 푸시
			notiId = 2;
			ledColor = 0xFFFF0000;
			int same_age_count = Integer.parseInt(pushIntent.getStringExtra("same_age_count"));
			int today_login_count = Integer.parseInt(pushIntent.getStringExtra("today_login_count"));

			String percent = String.format("%.1f", (Double.isNaN(today_login_count*100.0/same_age_count)  ? 0.0 : (today_login_count*100.0/same_age_count)));

			msg = "오늘은 " + students_year + " " + same_age_count + "명 중에 " + today_login_count + "("
					+ percent
					+ "%)명이 단어를 암기했습니다. 오늘은 다시 "+ percent +"%의 열공하는 " + students_year + "학습자로 돌아와 주실 거라고 믿어요!";
			summary = "불꽃 의지";
			largeIconRes = R.drawable.push_icon_will;

			if(!settings.getBoolean(MainValue.GpreWillPush, true)){
				receivePushFlag = false;
			}
			Log.d("Notification", settings.getBoolean(MainValue.GpreWillPush, true) + " " + receivePushFlag);
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("pushresponse", true);



		} else if (sort == 31) { // 기억관련 푸시
			ledColor = 0xFFFFFF00;
			//int word_count = Integer.parseInt(pushIntent.getStringExtra("word_count"));
			//double rate = Double.parseDouble(pushIntent.getStringExtra("rate"));

			/*msg = settings.getString(MainValue.GpreName, "이름없음") + "님의 망각 곡선 분석 결과 망각할 위험이 있는 단어 "
					+ word_count + "개가 추출되었습니다. 이 단어들이 적어도 " + (int)Math.ceil(word_count/10.0)
					+ "개 이상 수능에 나올 확률은 "+ String.format("%.2f", rate) + "%입니다.";*/
			msg = "당신의 암기시스템 '" + settings.getString(MainValue.GpreSystemName, "Riche") + "'가 알려드립니다.\n망각 위험에 있는 단어 "+ db.getMforget() + "개를 찾았어요!\n지금 바로 복습하면 까먹지 않아요.";
			summary = "망각 곡선";
			largeIconRes = R.drawable.push_icon_forget;

			notiId = 3;
			if(!settings.getBoolean(MainValue.GpreMemoryPush, true)){
				receivePushFlag = false;
			}
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("pushresponse", true);
		} else if (sort == 32) { // 기억관련 푸시
			ledColor = 0xFFFFFF00;
			int word_count = Integer.parseInt(pushIntent.getStringExtra("word_count"));

			/*msg = "지금 까먹는 단어 하나가 등급을 깎을 수도 있습니다. 복습이 필요한 단어 "
					+ word_count + "개 - 5분이면 복습할 수 있습니다.";*/

			msg = "당신의 암기시스템 '" + settings.getString(MainValue.GpreSystemName, "Riche") + "'가 알려드립니다.\n망각 위험에 있는 단어 "+ db.getMforget() + "개를 찾았어요!\n지금 바로 복습하면 까먹지 않아요.";
			summary = "망각 곡선";
			largeIconRes = R.drawable.push_icon_forget;

			notiId = 3;
			if(!settings.getBoolean(MainValue.GpreMemoryPush, true)){
				receivePushFlag = false;
			}
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("pushresponse", true);
		} else if (sort == 33) { // 기억관련 푸시
			ledColor = 0xFFFFFF00;
			int word_count = Integer.parseInt(pushIntent.getStringExtra("word_count"));
			double rate = Double.parseDouble(pushIntent.getStringExtra("rate"));

			/*msg = "지금 까먹어 가는 " + word_count + "개 단어 중 적어도 " + (int)Math.ceil(word_count/10.0)
					+ "개 이상 수능에 나올 확률은 "+ String.format("%.2f", rate) + "%입니다.";*/

			msg = "당신의 암기시스템 '" + settings.getString(MainValue.GpreSystemName, "Riche") + "'가 알려드립니다.\n망각 위험에 있는 단어 "+ db.getMforget() + "개를 찾았어요!\n지금 바로 복습하면 까먹지 않아요.";
			summary = "망각 곡선";
			largeIconRes = R.drawable.push_icon_forget;

			notiId = 3;
			if(!settings.getBoolean(MainValue.GpreMemoryPush, true)){
				receivePushFlag = false;
			}
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("pushresponse", true);
		} else if (sort == 19) { // 적중 블로그
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "적중 단어";
			largeIconRes = R.drawable.push_icon_word_blog;
			notiId = 6;
			if(!settings.getBoolean(MainValue.GpreBlogPush, true)){
				receivePushFlag = false;
			}
			Uri uri = Uri.parse(pushIntent.getStringExtra("url"));
			notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
		} else if (sort == 29) { // 의지 블로그
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "불꽃 의지";
			largeIconRes = R.drawable.push_icon_will_blog;
			notiId = 4;
			if(!settings.getBoolean(MainValue.GpreBlogPush, true)){
				receivePushFlag = false;
			}
			Uri uri = Uri.parse(pushIntent.getStringExtra("url"));
			notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
		} else if (sort == 39) { // 기억 블로그
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "망각 곡선";
			largeIconRes = R.drawable.push_icon_forget_blog;
			notiId = 5;
			if(!settings.getBoolean(MainValue.GpreBlogPush, true)){
				receivePushFlag = false;
			}
			Uri uri = Uri.parse(pushIntent.getStringExtra("url"));
			notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
			//
		} else if (sort == 50) { // 새로운 친구가입
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "새로운 친구";
			largeIconRes = R.drawable.push_icon_will;
			notiId = 11;
			if(!settings.getBoolean(MainValue.GpreWillPush, true)){
				receivePushFlag = false;
			}
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("friend", true);
			db.setPushTable(1, 1);
			//
		} else if (sort == 51) { // 카카오스토리 공유
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "";
			largeIconRes = R.drawable.push_icon_word;
			notiId = 12;
			if(!settings.getBoolean(MainValue.GpreHitPush, true)){
				receivePushFlag = false;
			}
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("kakaostory", true);
			db.setPushTable(2, 1);
			//
		} else if (sort == 60) { // 카톡 단어시험
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "";
			largeIconRes = R.drawable.push_icon_word;
			notiId = 13;
			if(!settings.getBoolean(MainValue.GpreMemoryPush, true)){
				receivePushFlag = false;
			}
			Uri uri = Uri.parse(url);

			SessionCallback callback = new SessionCallback();
			Session.getCurrentSession().addCallback(callback);

		}else if (sort == 61) { // 카톡 단어시험
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "";
			largeIconRes = R.drawable.push_icon_word;
			notiId = 14;
			if(!settings.getBoolean(MainValue.GpreMemoryPush, true)){
				receivePushFlag = false;
			}
			String url = "http://lnslab.com/sat/sat_result.php?sat_user_id="+db.getStudentId();
			Uri sat_uri = Uri.parse(url);
			notificationIntent = new Intent(Intent.ACTION_VIEW, sat_uri);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		}else if (sort == 70) { // 1일 무료기간 증정 푸시
			ledColor = 0xFFFFFFFF;
			msg = pushIntent.getStringExtra("contents");
			summary = "";
			largeIconRes = R.drawable.img_chathead_24h;
			notiId = 15;
			if(!settings.getBoolean(MainValue.GpreWillPush, true)){
				receivePushFlag = false;
			}
			notificationIntent = new Intent(context, SplashActivity.class);
			notificationIntent.putExtra("1freeday", true);
		}else if(sort == 100){

			notiId = 100;

			new RetrofitService().getStudyInfoService().retroUpdateWordState(""+db.getStudentId())
					.enqueue(new Callback<WordUpdateData>() {
						@Override
						public void onResponse(Response<WordUpdateData> response, Retrofit retrofit) {
							if(response.isSuccess()){
								for(int i =0; i<response.body().getWord_list().size();i++){
									Word word = db.getWord(response.body().getWord_list().get(i).getWord_id());
									Config.unknow_count++;

									int ex_state = word.getState();
									word.increaseWrongCount();

									if (!word.isWrong()) {
										word.setWrong(true);
										word.setRight(false);
										db.updateRightWrong(false, word.get_id());
									}

									db.insertState0FlagTableElement(word, false);
									db.updateForgettingCurvesByNewInputs(word, Config.MAINWORDBOOK,  false);
									db.insertReviewTimeToGetMemoryCoachMent(word);
									db.insertLevel(word, false);


									Word word_for_write = db.getWord(word.get_id());
									word.setState(word_for_write.getState());


									if (ex_state > 0) {
										settings.edit().putInt(MainValue.GpreCheckCurve, settings.getInt(MainValue.GpreCheckCurve, 0) + 1).commit();
									}
								}

								ArrayList<DBPool.word_log> list =db.get_word_log();
								Log.e("KARAM", "sort=100");
								try {
									if(list.size()>0){
										Gson gson = new GsonBuilder().create();
										JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();
										String word_log = myCustomArray.toString();
										Log.e("Async_upload_word_log", ""+word_log);
										//140013340   db.getKakaoId()
										new RetrofitService().getStudyInfoService().retroInsertWordLog(db.getStudentId(),
												word_log)
												.enqueue(new Callback<StudyInfoData>() {
													@Override
													public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {
														db.update_word_log();
														Log.e("KARAM", "Resonponse");
													}

													@Override
													public void onFailure(Throwable t) {
														Log.e("KARAM", "Exception");
													}
												});

									}
								}catch (NullPointerException e){

								}


								notificationIntent = new Intent(context, SplashActivity.class);
								notificationIntent.putExtra("pushresponse", true);
							}else{

								Log.e("KARAM", "retroUpdateWordState else");
							}


						}

						@Override
						public void onFailure(Throwable t) {
							Log.e(TAG,"retroUpdateWordState onFailure : "+t.toString());
						}
					});
			return;

		}

		// https://play.google.com/store/apps/details?id=com.ihateflyingbugs.hsmd
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		builder.setContentTitle("밀당영단어")
				.setTicker(msg)
				.setContentText(msg)
				.setSmallIcon(R.drawable.ic_push_notification)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), largeIconRes))
				.setPriority(Notification.PRIORITY_MAX)
				.setAutoCancel(true)
				.setLights(ledColor, 1000, 500)
				.setSound(alarmSound)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(msg)
						.setBigContentTitle("밀당영단어")
						.setSummaryText(summary));

		if(notificationIntent != null||notiId!=100){
			//String title = context.getString(R.string.app_name);

			// notificationIntent = new Intent(context, SplashActivity.class);
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//
			//		notification.contentView = new RemoteViews(context.getPackageName(),
			//				R.layout.notification);
			//		notification.contentView.setTextViewText(R.id.tv_noti_title, "밀당 영단어");
			//		notification.contentView.setTextViewText(R.id.tv_noti_message, msg);

			notificationIntent.putExtra("idx", idx);
			//
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(intent);
		}
//
//		notification.setLatestEventInfo(context, title, msg, intent);
		//builder.flags |= Notification.FLAG_AUTO_CANCEL;
		MildangDate date = new MildangDate();
		Log.d("date", date.get_hour()+date.get_minute());
		if((Integer.parseInt(date.get_hour() + date.get_minute()) <  0500) || !receivePushFlag){ // 한국시간 0730 이하일때 차단
//		if((Integer.parseInt(date.get_hour() + date.get_minute()) >  0730) || blockPush){ // 0730 초과일때 차단
			builder.setSound(null);
			builder.setVibrate(null);
			builder.setLights(0, 0, 0);
			notificationManager.notify(notiId, builder.build());
		} else{
			// Play default notification sound
			// Vibrate if vibrate is enabled
			ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
			List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd")) {
				if(notiId != 0){
					serviceIntent.putExtra("flag", notiId);
					serviceIntent.putExtra("msg", msg);
					serviceIntent.putExtra("idx", idx);
					if(sort % 10 == 9){
						serviceIntent.putExtra("url", pushIntent.getStringExtra("url"));
					}else if(sort ==50){
						Log.e("thumbnailURL", "asdf    "+pushIntent.getStringExtra("profile"));
						serviceIntent.putExtra("picture", pushIntent.getStringExtra("profile"));

					}
					startService(serviceIntent);
				}

				notificationManager.notify(notiId, builder.build());
			}
		}

		if (sort == LOG_OUT) {
			deleteMySharedPreferences(MainActivitys.GpreEmail, context);
			deleteMySharedPreferences(MainActivitys.GprePass, context);

			SharedPreferences.Editor editor = settings.edit();
			editor.remove(MainActivitys.GpreEmail);
			editor.remove(MainActivitys.GprePass);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	private static void deleteMySharedPreferences(String _key, Context context) {
		if (MainActivitys.mPreference == null) {
			MainActivitys.mPreference = context.getSharedPreferences(
					MainActivitys.preName, MODE_WORLD_READABLE
							| MODE_WORLD_WRITEABLE);
		}
		SharedPreferences.Editor editor = MainActivitys.mPreference.edit();
		editor.remove(_key);
		editor.commit();
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			GCMIntentService.this.mService = binder.getService();
			mBound = true;
		}
	};

	private class SessionCallback implements ISessionCallback {

		@Override
		public void onSessionOpened() {
			if(Session.getCurrentSession().isOpened()&&!db.getStudentId().equals("")){
				url +="?sat_user_id="+db.getStudentId()+"&"+"access_token="+Session.getCurrentSession().getAccessToken();
				Uri sat_uri = Uri.parse(url);
				notificationIntent = new Intent(Intent.ACTION_VIEW, sat_uri);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		}

		@Override
		public void onSessionOpenFailed(KakaoException exception) {
			if(exception != null) {
				Logger.e(exception);
			}
		}
	}


}