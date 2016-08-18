package com.ihateflyingbugs.hsmd.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.ihateflyingbugs.hsmd.GetExpectRateInReviewWords;
import com.ihateflyingbugs.hsmd.ServiceLogDataFile;
import com.ihateflyingbugs.hsmd.WordListFragment;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.DBState;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.ZeroStateWord;
import com.ihateflyingbugs.hsmd.lock.LockService;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.NotificationData;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.roomorama.caldroid.CalendarHelper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DBService extends Service {
	final String TAG= "CHECK_TIME";
	public SharedPreferences mPreference;
	private int count = 0;
	private final int FINISH_UPDATE = 1000;
	private int dayPerTime = 24;
	private int weekPerDay = 7;
	ServiceLogDataFile service_log_file;
	static int loopCounter;

	Handler handler;

	Map<String, String> EndInfoParams = new HashMap<String, String>();

	DBPool db;

	boolean isRunning = false;




	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.

		throw new UnsupportedOperationException("Not yet implemented");

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d("alma", "onCreate start");
		service_log_file = new ServiceLogDataFile(getApplicationContext());
		service_log_file.input_LogData_in_file(WordListFragment.get_date() + "_Service Created : start\r\n");
		super.onCreate();
		//	android.os.Debug.waitForDebugger();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Service Destroy", Toast.LENGTH_LONG).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if(isRunning){
			Log.d("dsthread", "thread isRunning");
			return START_REDELIVER_INTENT;
		}else{
			Thread.interrupted();
		}

		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==FINISH_UPDATE){
					isRunning = false;
				}
			}

		};

		//		if(thr_DBupdate != null && thr_DBupdate.isAlive()){
		//			thr_DBupdate.interrupt();
		//			isRunning = false;
		//		}

		// TODO Auto-generated method stub
		long start_time = System.currentTimeMillis();

		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//		Toast.makeText(getApplicationContext(), "service start", Toast.LENGTH_SHORT).show();

			}
		});


		isRunning = true;

		Log.d("dsthread", "onStartCommand start");

		db = DBPool.getInstance(getApplicationContext());
		Log.d("alma", "onStartCommand start");
		Long time;

		MildangDate date = new MildangDate();

		try {
			time = Long.valueOf(getMySharedPreferences(MainValue.GpreTime));
		} catch (NumberFormatException e) {
			// TODO: handle exception
			time = System.currentTimeMillis() - (AlarmManager.INTERVAL_HOUR);
			setMySharedPreferences(MainValue.GpreTime, ""+time);
		}

		if(!isMyServiceRunning(LockService.class)){
			Log.e("serviceCheck", "DBSERVICE : start service");
			Intent Screenintent = new Intent(DBService.this, LockService.class);
			Screenintent.setPackage("com.ihateflyingbugs.hsmd.lock");
			startService(Screenintent);
		}


		Float fl = (float) ((System.currentTimeMillis() - time) / 3600000.0);
		fl = Float.valueOf(String.format("%.4f", fl));
		service_log_file.input_LogData_in_file(WordListFragment.get_date() + "_Service Running : time is " + Math.round(fl) + "hour after \r\n");
		Log.e("almas", "calc score    " + String.valueOf(fl));

		if (Math.floor(fl) >= 1) {
			loopCounter = 0;

			db.calcMaxCnt();
			Log.e("dbservice", "1");
			// upload state0 flag table

			Word[] word = db.getState0FlagTable();				//get state0 flag table on local DB

			ArrayList<ZeroStateWord> zero_list = new ArrayList<ZeroStateWord>();
			for(int i =0; i<word.length; i++){
				zero_list.add(new ZeroStateWord(word[i].get_id(), word[i].getKnownFlag()));
			}
			Gson gson = new Gson();
			String json_word_list = gson.toJson(zero_list);
			if(!zero_list.isEmpty()){
				new RetrofitService().getStudyInfoService().retroInsertStateZeroWord(db.getStudentId(),
																					json_word_list)
						.enqueue(new Callback<StudyInfoData>() {
							@Override
							public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {
								db.deleteState0FlagTable();
							}

							@Override
							public void onFailure(Throwable t) {

							}
						});
			}

			Log.e("dbservice", "2");
			// fit forgetting curves

			Log.d("dsthread", "running");

			File src = new File(Config.DB_FILE_DIR + Config.DB_NAME);
			File dst = new File(Config.DB_FILE_DIR + "backup_remember.sqlite");

			try {
				db.backupDB(src, dst);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.d("dsthread", "running");
			}

			DBState[] st = db.getForgettingCurvesAsDBState(); // declare DBState

			Log.e("dbservice", "3");
			db.fitForgettingCurves(st); // fitting forgetting curves
			Log.e("dbservice", "4");
			// calculate and reassign score of words
			double Time_to_hour = fl; // calculate elapsed time from last using time
			db.calculateAndReassignScoreOfWords(st, Time_to_hour); // calculate and reassign score of words
			Log.e("dbservice", "5");
			Log.e("almas", "Time to hour : " + String.valueOf(Math.round(fl)));
			service_log_file.input_LogData_in_file(WordListFragment.get_date() + "_Service Running : Finish ForgettingCurve\r\n");


			service_log_file.input_LogData_in_file(WordListFragment.get_date() + "_Service Running : Finish\r\n");
			setMySharedPreferences(MainValue.GpreTime,
					String.valueOf(System.currentTimeMillis()));
			Log.e("dbservice", "check : "+getMySharedPreferences(MainValue.GpreTime));
			MainValue.GpreAccessDuration++;
		}


		if (Config.isNetworkAvailable(getApplicationContext())){

			// 매일 업로드 하도록 하는 코드
			DateTime dateTime = CalendarHelper.convertDateToDateTime(new java.util.Date());
			int tempInteger = 1;
			String tempMonth, tempDay;
			String saved_date = db.getDate();
			if (!saved_date.equals(date.get_today())) {
				// 에러 발생시 하루에 한번 csv파일을 업로드 하도록하는 플래그 초기화
				mPreference.edit().putBoolean(MainValue.GpreErrorSQLSend, true).commit();
				// csv파일을 업로드한다.
				new RetrofitService().getStudyInfoService().retroInsertStudentForgettingCurves(db.getStudentId(),
																								db.exportForgettingCurvesTableToJson(),
																								"0")
						.enqueue(new Callback<StudyInfoData>() {
							@Override
							public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

							}

							@Override
							public void onFailure(Throwable t) {

							}
						});
				// 저장된 날짜와 현재 날짜가 다른 경우
				// 학습시간을 업로드한다
				int useTime[] = db.getTime();

				new RetrofitService().getStudyInfoService().retroInsertStudentCalendarData(db.getStudentId(),
																							db.exportCalendarDataTableToJson()[0])
						.enqueue(new Callback<StudyInfoData>() {
							@Override
							public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

								Log.e("server_test", "success");
							}

							@Override
							public void onFailure(Throwable t) {

								Log.e("server_test", db.exportCalendarDataTableToJson()[0]);
							}
						});

				// 학습량을 feeds 테이블에 저장한다.
				db.putDay_Of_Study();
				while (true) {

					// 하루 전의 월은 구한다.
					if (dateTime.minusDays(tempInteger).getMonth() > 9) {
						tempMonth = dateTime.minusDays(tempInteger).getMonth().toString();
					} else {
						tempMonth = "0" + dateTime.minusDays(tempInteger).getMonth().toString();
					}

					// 하루 전의 일을 구한다.
					if (dateTime.minusDays(tempInteger).getDay() > 9) {
						tempDay = dateTime.minusDays(tempInteger).getDay().toString();
					} else {
						tempDay = "0" + dateTime.minusDays(tempInteger).getDay().toString();
					}

					// 하루 전의 연을 구하고 이전에 구한 월, 일과 합친다.
					String tempDate = dateTime.minusDays(tempInteger++).getYear().toString() + tempMonth + tempDay;

					Log.d("c_Date", saved_date + " : " + tempDate);

					// 저장된 날짜와 같으면 저장된 날짜에 덮어쓰기 전 break
					if (tempDate.equals(saved_date)) {
						break;
					}

					// 저장된 날짜와 현재로부터 x 이전 날짜가 다를 경우 db에 0분 저장
					db.putCalendarData_customTime(tempDate, 0, 0, 0);

				}

				// 저장된 날짜에  시간 저장
				db.putCalendarData(saved_date);

				// 현재 날짜 저장
				db.insertDate(date.get_today());

				// 학습시간 초기화
				db.removeStudyTime();

			}


			Log.d("ReviewPushLog", "mForgetCount : " + db.getMforget() + ", mPre : " + mPreference.getString(MainValue.GpreReviewPeriod, "") + ", yesterday : " + date.get_yesterday() + ", hour : " + date.get_hour()+date.get_minute());
			Log.d("ReviewPushLog", (db.getMforget() > 0) + ", " + ((mPreference.getString(MainValue.GpreReviewPeriod, "")).equals("")));
			Log.d("ReviewPushLog", (db.getMforget() > 0) + ", " + ((mPreference.getString(MainValue.GpreReviewPeriod, "")).equals(date.get_yesterday())) + ", " +
					(Integer.parseInt(date.get_hour()+date.get_minute()) >= 1930) + ", " + (Integer.parseInt(date.get_hour()+date.get_minute()) <= 2030));


			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////
			// 2015.02.09 복습단어 푸시 추가 - 양대현(최초작성)
			//
			// 복습단어가 있고, GpreReviewPeriod에 저장된 날짜가 없으면 최초의 복습단어 푸시를 보낸다.
			// 푸시를 전송하고 GpreReviewPeriod에 현재 날짜를 저장한다.

			if(db.getMforget() > 0 && (mPreference.getString(MainValue.GpreReviewPeriod, "")).equals("")){
				new GetExpectRateInReviewWords(db.getForgetWords(), db.getForgetWordCountByGrade(), 31);
				mPreference.edit().putString(MainValue.GpreReviewPeriod, date.get_today()).commit();
			}
			// 복습단어가 있고 GpreReviewPeriod에 저장된 날짜가 어제날짜이면서 현재 시간이 22시 이후이면 두번째 복습단어 푸시를 보낸다.
			// 푸시를 전송하고 GpreReviewPeriod에 현재 날짜를 저장한다.
			else if (db.getMforget() > 0 && ((mPreference.getString(MainValue.GpreReviewPeriod, "")).equals(date.get_yesterday())) &&
					Integer.parseInt(date.get_hour()+date.get_minute()) >= 1930 && Integer.parseInt(date.get_hour()+date.get_minute()) <= 2030 ){
				/*
				 * 2015-11-20 자체적으로 푸시를 보내도록 수정함. - 영철
				 *
				 *
						new Async_send_push_message(db.getKakaoId(), "", 32+"", VOCAconfig.context, new VocaCallback() {
							@Override
							public void Resonponse(JSONObject response) {
								Log.d("ReviewPushLog", response.toString());

							}
							@Override
							public void Exception() {
								Log.d("ReviewPushLog", "exception");
							}
						}).execute(db.getKakaoId(), 32 + "", db.getMforget() + "");

				 */
				notify_push(32, "망각");

				mPreference.edit().putString(MainValue.GpreReviewPeriod, date.get_today()).commit();
			}


			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////

			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////
			// 2015.02.09 불꽃의지 푸시 추가 - 양대현(최초작성)
			//
			//
			//
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			java.util.Date joinDate = null;
			java.util.Date lastDate = null;
			try {
				joinDate = sdf.parse(db.getJoinDate());
				lastDate = sdf.parse(mPreference.getString(MainValue.GpreLastLoginDate, date.get_today()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
			}
			java.util.Date today = new java.util.Date();

			Log.d("DBSERVICE PUSH", "1: " + db.getCalendarDataCount() + ", "
					+ (today.getTime() - joinDate.getTime())/(1000*60*60*24)
					+ ", " + date.get_hour().equals(mPreference.getString(MainValue.GpreContactLog, "22")));	// 현재시간 몇시인가

			Log.d("DBSERVICE PUSH", "2: " + db.getCalendarDataCount() + ", "
					+ (today.getTime() - lastDate.getTime())/(1000*60*60*24)
					+ ", " + date.get_hour().equals(mPreference.getString(MainValue.GpreContactLog, "22")));	// 현재시간 몇시인가


			final String[] pushdata = db.getFreeTable();


			String[] freepushdate = pushdata[0].split("/");
			final MildangDate freeDate = new MildangDate();

			Log.v("isGotFreeDate", ""+pushdata[1].toString());


			if(Boolean.valueOf(pushdata[1])){
				//의지 푸시로 접속후 1루가 지났을때
				Log.e("isGotFreeDate", ""+dateTime.minusDays(1).getDay()
						+"   "+Integer.valueOf(freepushdate[1]));
				if(dateTime.minusDays(1).getDay()==Integer.valueOf(freepushdate[1])){
					//서버에 푸시 전송 관련 요청 보내고 응답이 오면 false처리, 날짜 오늘날짜로 갱신.
					//usestate가 2인 애들만 처리 하자

					new RetrofitService().getAuthorizationService().retroCheckavailability(db.getStudentId(), "14").enqueue(new Callback<AuthorizationData>() {
						@Override
						public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {

							int availability = -2;
							int student_use_state = 0;

							availability = response.body().getAvailability();
							student_use_state = response.body().getStudent_use_state();

							if(availability==0){
								Log.e("isGotFreeDate", "send free push");
								db.setFreeTable("false", pushdata[0]);

								/*
								 * 2015-11-20 자체적으로 푸시를 보내도록 수정함. - 영철
								 * new Async_send_push_message(db.getKakaoId(), null, "70", VOCAconfig.context, new VocaCallback() {
											@Override
											public void Resonponse(JSONObject response) {}

											@Override
											public void Exception() {}
										}).execute(db.getKakaoId(), "70");

								 */
								notify_push(70, "1일 무료 사용 팩이 도착하였습니다. 앱에 접속하시면 사용하실 수 있습니다.");

							}
						}

						@Override
						public void onFailure(Throwable t) {
							Log.e("availability", "Exception");
						}
					});
				}
			}else if(dateTime.minusDays(28).getDay()==Integer.valueOf(freepushdate[1])
					&&!freepushdate[1].equals(32)){
				Log.e("isGotFreeDate", "add 1 month ");
				db.setFreeTable("false", "00/00/00");
			}


			//		if(db.getCalendarDataCount() == 1){
			int joinValue = (int) ((today.getTime() - joinDate.getTime())/(1000*60*60*24));
			Log.d("DBSERVICE PUSH", joinValue + ", " + db.getCalendarDataCount());
			if (2 <= joinValue && joinValue < 3) {
				if (date.get_hour().equals(mPreference.getString(MainValue.GpreContactLog, "22"))) {
					if (mPreference.getBoolean(MainValue.GpreJoinPushFlag, true)) {
						new RetrofitService().getNotificationService().retroNotAccessFirstTime(db.getStudentId(),
								""+NotificationData.PUSH_WILL_NOT_ACCESS_FIRSTTIME)
								.enqueue(new Callback<NotificationData>() {
									@Override
									public void onResponse(Response<NotificationData> response, Retrofit retrofit) {
										Log.d("DBSERVICE PUSH 21", "JOIN 2" + response.toString());
									}

									@Override
									public void onFailure(Throwable t) {
										Log.d("DBSERVICE PUSH 21", "onFailure : " + t.toString());
									}
								});

						mPreference.edit().putBoolean(MainValue.GpreJoinPushFlag, false).commit();
					}
				}
			} else if(5 <= joinValue && joinValue < 6){
				if(date.get_hour().equals(mPreference.getString(MainValue.GpreContactLog, "22"))){
					if (mPreference.getBoolean(MainValue.GpreJoinPushFlag, true)) {
						new RetrofitService().getNotificationService().retroNotAccessFirstTime(db.getStudentId(),
								""+NotificationData.PUSH_WILL_NOT_ACCESS_FIRSTTIME)
								.enqueue(new Callback<NotificationData>() {
									@Override
									public void onResponse(Response<NotificationData> response, Retrofit retrofit) {
										Log.d("DBSERVICE PUSH 21", "JOIN 2" + response.toString());
									}

									@Override
									public void onFailure(Throwable t) {
										Log.d("DBSERVICE PUSH 21", "onFailure : " + t.toString());
									}
								});

						mPreference.edit().putBoolean(MainValue.GpreJoinPushFlag, false).commit();
					}
				}
			}

			//		} else if(db.getCalendarDataCount() > 1){
			int lastGap = (int) ((today.getTime() - lastDate.getTime())/(1000*60*60*24));
			Log.d("DBSERVICE PUSH", lastGap + ", " + db.getCalendarDataCount());
			if (3 <= lastGap && lastGap < 4) {
				if(date.get_hour().equals(mPreference.getString(MainValue.GpreContactLog, "22"))){
					if (mPreference.getBoolean(MainValue.GpreLast1PushFlag, true)) {
						new RetrofitService().getNotificationService().retroNotAccessFirstTime(db.getStudentId(),
								""+NotificationData.PUSH_WILL_NOT_ACCESS)
								.enqueue(new Callback<NotificationData>() {
									@Override
									public void onResponse(Response<NotificationData> response, Retrofit retrofit) {
										Log.d("DBSERVICE PUSH 22", "JOIN 2" + response.toString());
									}

									@Override
									public void onFailure(Throwable t) {
										Log.d("DBSERVICE PUSH 22", "onFailure : " + t.toString());
									}
								});
						mPreference.edit().putBoolean(MainValue.GpreLast1PushFlag, false).commit();
					}
				}
			} else if(5 <= lastGap && lastGap < 6){
				if(date.get_hour().equals(mPreference.getString(MainValue.GpreContactLog, "22"))){
					if (mPreference.getBoolean(MainValue.GpreLast2PushFlag, true)) {
						new RetrofitService().getNotificationService().retroNotAccessFirstTime(db.getStudentId(),
								""+NotificationData.PUSH_WILL_NOT_ACCESS)
								.enqueue(new Callback<NotificationData>() {
									@Override
									public void onResponse(Response<NotificationData> response, Retrofit retrofit) {
										Log.d("DBSERVICE PUSH 22", "JOIN 2" + response.toString());
									}

									@Override
									public void onFailure(Throwable t) {
										Log.d("DBSERVICE PUSH 22", "onFailure : " + t.toString());
									}
								});
						mPreference.edit().putBoolean(MainValue.GpreLast2PushFlag, false).commit();
					}
				}
			}
			//		}
			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////


		}





		if (MainValue.GpreAccessDuration >= dayPerTime * weekPerDay && MainValue.GpreFlag == true) { // user doesn't access application more than 7 days

			Log.e("not accessed long time", "not accessed long time");
			EndInfoParams.put("EndTime", getMySharedPreferences(MainValue.GpreEndTime));
			EndInfoParams.put("TotalReviewCnt", getMySharedPreferences(MainValue.GpreTotalReviewCnt));
			EndInfoParams.put("TodayReviewCnt", getMySharedPreferences(MainValue.GpreTodayReviewCnt));
			EndInfoParams.put("TodayLearnCnt", getMySharedPreferences(MainValue.GpreTodayLearnCnt));
			FlurryAgent.logEvent("" + weekPerDay + "동안 접속하지 않음,DBService,1", EndInfoParams, true);
			MainValue.GpreFlag = false;
			// setMySharedPreferences(MainActivitys.GpreFlag, "false");
		}

		// Calendar mCalendar = Calendar.getInstance();
		// mCalendar.setTimeInMillis(System.currentTimeMillis());
		//
		// int hour = mCalendar.getTime().getHours();
		// int minute = mCalendar.getTime().getMinutes();
		// int second = mCalendar.getTime().getSeconds();

		// writeFile(count + "   " + hour + ":" + minute + ":" + second + "\n");
		Log.e("almas", "calc end    " + String.valueOf(System.currentTimeMillis()) + "    " + String.valueOf(time) + "    "+ String.valueOf(AlarmManager.INTERVAL_HOUR));
		//return super.onStartCommand(intent, flags, startId);


		Log.d("dsthread", "end");


		isRunning= false;
		Message message = new Message().obtain();
		message.what = FINISH_UPDATE;
		handler.sendMessage(message);


		long end_time = (System.currentTimeMillis()- start_time)/1000;
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//	Toast.makeText(getApplicationContext(), "service end : "+ end_time, Toast.LENGTH_SHORT).show();

			}
		});

		return START_REDELIVER_INTENT;
	}

	private String getMySharedPreferences(String _key) {
		if (mPreference == null) {
			mPreference = getSharedPreferences(MainValue.preName,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		}
		return mPreference.getString(_key, "");
	}

	private void setMySharedPreferences(String _key, String _value) {
		if (mPreference == null) {
			mPreference = getSharedPreferences(MainValue.preName,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		}
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.d("SERVISESEES", service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}

	public void notify_push(int sort, String message){
		//		// https://play.google.com/store/apps/details?id=com.ihateflyingbugs.hsmd
		//		mPreference = getSharedPreferences(Config.PREFS_NAME,
		//				Context.MODE_PRIVATE);
		//
		//		Intent serviceIntent = new Intent(this, ChatHeadService.class);
		////		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
		//		int notiId = 0;
		//		int icon = R.drawable.icon48;
		//		long when = System.currentTimeMillis();
		//		String msg = "안녕하세요 밀당영단어입니다.";
		//		String summary = "밀당 영단어";
		//		int largeIconRes = R.drawable.launcher_xxhdpi;
		//
		//		final DBPool db;
		//		db = DBPool.getInstance(getApplicationContext());
		//
		//		Boolean receivePushFlag = true;
		//
		//
		//		String birth = mPreference.getString(MainValue.GpreBirth, "900000");
		//
		//		int sum = 0;
		//
		//		if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
		//			sum = 1900;
		//			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
		//			sum += years;
		//		} else {
		//			sum = 2000;
		//			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
		//			sum += years;
		//		}
		//		int age = Integer.valueOf(TimeClass.getYear()) - sum;
		//
		//		String students_year;
		//
		//		if (age <= 16) {
		//			students_year = "중학생";
		//		} else if (age == 17) {
		//			students_year = "고1학생";
		//		} else if (age == 18) {
		//			students_year = "고2학생";
		//		} else if (age == 19) {
		//			students_year = "고3학생";
		//		} else if (age > 19) {
		//			students_year = "재수생";
		//		} else{
		//			students_year = "안얄랴줌";
		//		}
		//
		//		int ledColor = 0xFF000000;
		//
		//		Log.d("GCMReceive", message + " " + sort);
		//
		//		// sort = 0;
		//
		//		// 7  : 개인에게 보내는 공지사항 푸시
		//		// 8  : 공지사항
		//		// 9  : 업데이트
		//		// 1X : 적중
		//		// 2X : 의지
		//		// 3X : 기억
		//		if (sort == 32) { // 기억관련 푸시
		//			ledColor = 0xFFFFFF00;
		//			int word_count = Integer.parseInt(pushIntent.getStringExtra("word_count"));
		//
		//			/*msg = "지금 까먹는 단어 하나가 등급을 깎을 수도 있습니다. 복습이 필요한 단어 "
		//					+ db.getMforget() + "개 - 5분이면 복습할 수 있습니다.";*/
		//
		//			msg = "당신의 암기시스템 '" + mPreference.getString(MainValue.GpreSystemName, "Riche") + "'가 알려드립니다.\n망각 위험에 있는 단어 "+ db.getMforget() + "개를 찾았어요!\n지금 바로 복습하면 까먹지 않아요.";
		//			summary = "망각 곡선";
		//			largeIconRes = R.drawable.push_icon_forget;
		//
		//			notiId = 3;
		//			if(!mPreference.getBoolean(MainValue.GpreMemoryPush, true)){
		//				receivePushFlag = false;
		//			}
		//			notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
		//			notificationIntent.putExtra("pushresponse", true);
		//		} else if (sort == 51) { // 카카오스토리 공유
		//			ledColor = 0xFFFFFFFF;
		//			msg = pushIntent.getStringExtra("contents");
		//			summary = "";
		//			largeIconRes = R.drawable.push_icon_word;
		//			notiId = 12;
		//			if(!mPreference.getBoolean(MainValue.GpreHitPush, true)){
		//				receivePushFlag = false;
		//			}
		//			notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
		//			notificationIntent.putExtra("kakaostory", true);
		//			db.setPushTable(2, 1);
		//			//
		//		} else if (sort == 70) { // 1일 무료기간 증정 푸시
		//			ledColor = 0xFFFFFFFF;
		//			msg = message;
		//			summary = "";
		//			largeIconRes = R.drawable.img_chathead_24h;
		//			notiId = 15;
		//			if(!mPreference.getBoolean(MainValue.GpreWillPush, true)){
		//				receivePushFlag = false;
		//			}
		//			notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
		//			notificationIntent.putExtra("1freeday", true);
		//		}
		//
		//		// https://play.google.com/store/apps/details?id=com.ihateflyingbugs.hsmd
		//		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//
		//
		//		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		//		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		//		builder.setContentTitle("밀당영단어")
		//			.setTicker(msg)
		//			.setContentText(msg)
		//			.setSmallIcon(R.drawable.ic_push_notification)
		//			.setLargeIcon(BitmapFactory.decodeResource(getResources(), largeIconRes))
		//			.setPriority(Notification.PRIORITY_MAX)
		//			.setAutoCancel(true)
		//			.setLights(ledColor, 1000, 500)
		//			.setSound(alarmSound)
		//			.setDefaults(Notification.DEFAULT_VIBRATE)
		//			.setStyle(new NotificationCompat.BigTextStyle()
		//				.bigText(msg)
		//				.setBigContentTitle("밀당영단어")
		//				.setSummaryText(summary));
		//
		//		if(notificationIntent != null){
		//			//String title = context.getString(R.string.app_name);
		//
		//			// notificationIntent = new Intent(context, SplashActivity.class);
		//			// set intent so it does not start a new activity
		//			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//	//
		//	//		notification.contentView = new RemoteViews(context.getPackageName(),
		//	//				R.layout.notification);
		//	//		notification.contentView.setTextViewText(R.id.tv_noti_title, "밀당 영단어");
		//	//		notification.contentView.setTextViewText(R.id.tv_noti_message, msg);
		//
		//			notificationIntent.putExtra("idx", idx);
		//	//
		//			PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		//			builder.setContentIntent(intent);
		//		}
		////
		////		notification.setLatestEventInfo(context, title, msg, intent);
		//		//builder.flags |= Notification.FLAG_AUTO_CANCEL;
		//		MildangDate date = new MildangDate();
		//		Log.d("date", date.get_hour()+date.get_minute());
		//		if((Integer.parseInt(date.get_hour() + date.get_minute()) <  0500) || !receivePushFlag){ // 한국시간 0730 이하일때 차단
		////		if((Integer.parseInt(date.get_hour() + date.get_minute()) >  0730) || blockPush){ // 0730 초과일때 차단
		//			builder.setSound(null);
		//			builder.setVibrate(null);
		//			builder.setLights(0, 0, 0);
		//			notificationManager.notify(notiId, builder.build());
		//		} else{
		//			// Play default notification sound
		//			// Vibrate if vibrate is enabled
		//			ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		//			List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		//			ComponentName componentInfo = taskInfo.get(0).topActivity;
		//			if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd")) {
		//				if(notiId != 0){
		//					serviceIntent.putExtra("flag", notiId);
		//					serviceIntent.putExtra("msg", msg);
		//					serviceIntent.putExtra("idx", idx);
		//					startService(serviceIntent);
		//				}
		//				notificationManager.notify(notiId, builder.build());
		//			}
		//		}
	}



}
