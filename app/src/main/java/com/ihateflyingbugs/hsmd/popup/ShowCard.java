package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.FriendCard;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.feedback.SettingActivity;
import com.ihateflyingbugs.hsmd.indicator.BaseSampleActivity;
import com.ihateflyingbugs.hsmd.indicator.FriendFragment;
import com.ihateflyingbugs.hsmd.indicator.FriendFragmentAdapter;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.data.Friend;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.Utils;
import com.ihateflyingbugs.hsmd.tutorial.InAppPurchaseActivity;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ShowCard extends BaseSampleActivity implements OnClickListener {

	FriendFragmentAdapter mAdapter;
	ViewPager mPager;

	public RelativeLayout rl_get_friendlist;
	public LinearLayout ll_purchase_bottom;

	public Button bt_payment;
	public Button bt_blog;
	public Button bt_home;
	public Button bt_tuto_withdraw;
	public Button bt_use_lockscreen;
	public Button bt_use_studyroom;

	public TextView tv_purchase_expired;
	public TextView tv_least_price;

	int use_state;

	int availability;

	int primeNum = 1;

	String DevPayload;

	List<FriendFragment> fragment_list;

	SharedPreferences mPreference;

	DBPool db;

	Handler handler;

	ProgressBar pb_get_friendlist;

	String Current_Activity = "aaaa";

	final String TAG = "FreeFinishAndPay";

	boolean isSend = false;
	long Time_send = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_payinfo);

		pb_get_friendlist = (ProgressBar) findViewById(R.id.pb_get_friendlist);
		mPager = (ViewPager) findViewById(R.id.pager_friends);
		rl_get_friendlist = (RelativeLayout) findViewById(R.id.rl_get_friendlist);
		ll_purchase_bottom = (LinearLayout) findViewById(R.id.ll_purchase_bottom);

		bt_payment = (Button) findViewById(R.id.bt_tuto_pay2_purchase);
		bt_tuto_withdraw = (Button) findViewById(R.id.bt_tuto_withdraw);
		bt_blog  = (Button) findViewById(R.id.bt_blog);
		bt_home  = (Button) findViewById(R.id.bt_home);

		bt_use_lockscreen = (Button)findViewById(R.id.bt_use_lockscreen);
		bt_use_studyroom = (Button)findViewById(R.id.bt_use_studyroom);


		tv_purchase_expired = (TextView) findViewById(R.id.tv_purchase_expired);
		tv_least_price = (TextView) findViewById(R.id.tv_least_price);

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		Intent receiveIntent = getIntent();

		bt_tuto_withdraw.setPaintFlags(bt_tuto_withdraw.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		bt_use_studyroom.setPaintFlags(bt_use_studyroom.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


		use_state = receiveIntent.getIntExtra("use_state", -1);
		availability = receiveIntent.getIntExtra("availability", 1);


		if (receiveIntent.hasExtra("prime_num")) {
			primeNum = receiveIntent.getIntExtra("prime_num", 1);
		}

		if (availability == 0 && use_state == 2) {
			tv_purchase_expired.setVisibility(View.VISIBLE);
		}else if(availability == 0 && use_state == 3){
			tv_purchase_expired.setVisibility(View.VISIBLE);
			tv_purchase_expired.setText("유료 사용 기간이 종료되었습니다.");
		}else {
			tv_purchase_expired.setVisibility(View.GONE);
		}


		db = DBPool.getInstance(getApplicationContext());

		fragment_list = new ArrayList<FriendFragment>();

		handler = new Handler();




		new RetrofitService().getAuthorizationService().retroGetNearFriendList(db.getStudentId())
				.enqueue(new Callback<AuthorizationData>() {
					@Override
					public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
						insertFriendUserInfo(response);
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure" + t.toString());
					}
				});


		new RetrofitService().getPaymentService().retroGetProductInfoList().enqueue(new Callback<PaymentData>() {
			@Override
			public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
				int count = response.body().getCount();
				Config.usable_google_product_id = new String[3][];
				Config.usable_purchase = new int[3][][];
				for (int i = 0; i < Config.usable_purchase.length; i++) {
					Config.usable_purchase[i] = new int[count / 3][];
					Config.usable_google_product_id[i] = new String[count / 3];
					for (int j = 0; j < Config.usable_purchase[i].length; j++) {
						Config.usable_purchase[i][j] = new int[8];
					}
				}

				int temp_index[] = { 0, 0, 0 };
				final List<PaymentData.Product> productlist = response.body().getUsable_product_list();
				for (int i = 0; i < productlist.size(); i++) {

                    if (primeNum == productlist.get(i).getPrice_code() && productlist.get(i).getPayment_way() == 0 && productlist.get(i).getPeriod_month() == 12) {
                        final int price =productlist.get(i).getPrice();
                        if (price < 1000) {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    tv_least_price.setText("\u20A9" + price / 12);

                                }
                            });
                        } else {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    tv_least_price.setText("\u20A9" + price / 12 / 1000 + ","
                                            + (price / 12 + "").substring((price / 12 + "").length() - 3));

                                }
                            });
                        }
                    }
                    int product_means  = productlist.get(i).getPayment_way();
                    Config.usable_purchase[product_means][temp_index[product_means]][Config.TYPE_ID] = productlist.get(i).getProduct_type_id();
					Config.usable_purchase[product_means][temp_index[product_means]][Config.NOT_DC_PRICE] = productlist.get(i).getNot_dc_price();
					Config.usable_purchase[product_means][temp_index[product_means]][Config.PRICE] = productlist.get(i).getPrice();
					Config.usable_purchase[product_means][temp_index[product_means]][Config.PRODUCT_MEANS] = productlist.get(i).getPayment_way();
					Config.usable_purchase[product_means][temp_index[product_means]][Config.PRICE_CODE] = productlist.get(i).getPrice_code();
					Config.usable_purchase[product_means][temp_index[product_means]][Config.PEROID_MONTH] = productlist.get(i).getPeriod_month();
					Config.usable_purchase[product_means][temp_index[product_means]][Config.IS_SUBSCRIBED] = productlist.get(i).getIs_subscribed();
					Config.usable_google_product_id[product_means][temp_index[product_means]++] = productlist.get(i).getGoogle_product_id();
				}
			}

			@Override
			public void onFailure(Throwable t) {
				Log.e(TAG, "onFailure" + t.toString());
			}
		});


		bt_tuto_withdraw.setOnClickListener(this);
		bt_payment.setOnClickListener(this);
		bt_blog.setOnClickListener(this);
		bt_home.setOnClickListener(this);
		bt_use_lockscreen.setOnClickListener(this);
		bt_use_studyroom.setOnClickListener(this);

		mPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MainActivity.isShowCard = false;
	}



	@Override
	public void onClick(View v) {
		int type = 0;
		switch (v.getId()) {
			case R.id.bt_tuto_pay2_kakao:
				FlurryAgent.logEvent(TAG + ":ClickRequestParents", true);
				type = 1;

				new RetrofitService().getPaymentService().retroInsertPaymentRequest(db.getStudentId(),""+type)
						.enqueue(new Callback<PaymentData>() {
							String payUrl = null;
							int serverType = 0;

							@Override
					public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								PaymentData paymentdata = response.body();
									payUrl = paymentdata.getPay_url();
									serverType = paymentdata.getPayment_type();
									Config.REQUEST_ID = paymentdata.getRequest_id();

								Handler mHandler = new Handler(Looper.getMainLooper());
								mHandler.postDelayed(new Runnable() {
									@Override
									public void run() {
										// 내용
										if (serverType == 1) {
											try {
												sendUrlLink(payUrl);
												isSend = true;
												Time_send = System.currentTimeMillis();
											} catch (NameNotFoundException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}

										} else if (serverType == 2) {
											Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
											startActivity(webIntent);

										}
									}
								}, 0);
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure" + t.toString());
					}
				});
			case R.id.bt_tuto_pay2_purchase:
				FlurryAgent.logEvent(TAG + ":ClickInAppPurchase", true);

				new RetrofitService().getPaymentService().retroInsertPaymentRequest(db.getStudentId(),"3")
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								DevPayload = response.body().getRequest_id();
								Log.e(TAG, "" + DevPayload);
								handler.post(new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent(getApplicationContext(), InAppPurchaseActivity.class);
										intent.putExtra("DevPayload", DevPayload);
										startActivityForResult(intent, 55555);
									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								Log.e(TAG, "onFailure" + t.toString());
							}
						});
				break;
			case R.id.bt_tuto_withdraw:

				FlurryAgent.logEvent(TAG + ":ClickWithdraw", true);

				Intent intent = new Intent(ShowCard.this, QuitPopup.class);
				startActivityForResult(intent, Activity.RESULT_OK);
				break;
			case R.id.bt_blog:
				FlurryAgent.logEvent(TAG + ":ClickBlog", true);
				Intent intent_blog = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/mildang_"));
				startActivity(intent_blog);
				break;
			case R.id.bt_home:
				FlurryAgent.logEvent(TAG + ":ClickHomePage", true);
				Intent intent_homepage = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.lnslab.com"));
				startActivity(intent_homepage);
				break;

			case R.id.bt_use_lockscreen:
				FlurryAgent.logEvent(TAG + ":ClickLockSetting", true);
				Intent intent_setting = new Intent(ShowCard.this, SettingActivity.class);
				startActivity(intent_setting);
				break;

			case R.id.bt_use_studyroom:
				FlurryAgent.logEvent(TAG + ":ClickStudyRoom", true);
				startActivityForResult(new Intent(ShowCard.this,StudyRoomPopup.class),22222);
				//	startActivityForResult(new Intent(ShowCard.this,StudyRoomUsePopup.class),22222);

				break;

		}

	}

	public void sendUrlLink(final String payUrl) throws NameNotFoundException {
		// Recommended: Use application mContext for parameter.

		// int checkedRadioButtonId = rg.getCheckedRadioButtonId();

		// check, intent is available.
		KakaoLink kakaoLink = null;
		KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = null;
		try {
			kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
			kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}


		Date date = new Date();

		final String mssg = mPreference.getString(MainValue.GpreName, "이름없음") + "님이 " + date.get_today() + "에 수능 영단어 마스터를 위한 목표를 세워 다짐했습니다.\n\n"
				+ mPreference.getString(MainValue.GpreName, "이름없음") + "님의 목표를 확인하시고 밀당영단어 결제를 허락해주세요.";

		final KakaoTalkLinkMessageBuilder finalKakaoTalkLinkMessageBuilder = kakaoTalkLinkMessageBuilder;
		final KakaoLink finalKakaoLink = kakaoLink;
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					finalKakaoTalkLinkMessageBuilder.addText(mssg)
							.addImage("http://lnslab.com/mildang/images/img_purchase_believe.png", Utils.dpToPixels(240, getResources()) / 2, Utils.dpToPixels(180, getResources()) / 2)
							.addWebLink(mPreference.getString(MainValue.GpreName, "이름없음") + "님의 목표 보기", payUrl).build();
					Context context = getApplicationContext();
					finalKakaoLink.sendMessage(finalKakaoTalkLinkMessageBuilder, ShowCard.this);

				} catch (KakaoParameterException e) {
					e.printStackTrace();
				}
			}
		});



		// kakaoLink
		// .openKakaoLink(
		// this,
		// "http://bit.ly/MDAppDwns",
		// mssg,
		// getPackageName(),
		// getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
		// "밀당 영단어", "UTF-8");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//android.os.Process.killProcess(android.os.Process.myPid());
		setResult(55555);
		finish();
		//super.onBackPressed();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e("splash", startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);

		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if (isSend) {
			if (Current_Activity.equals("com.kakao.talk.activity.TaskRootActivity")
					|| Current_Activity.equals("com.kakao.talk.activity.friend.picker.ConnectBroadcastFriendsPickerActivity")
					|| Current_Activity.equals("com.kakao.talk.activity.main.MainTabFragmentActivity")) {
				if (componentInfo.getClassName().equals("com.kakao.talk.activity.chat.ChatRoomActivity")) {
					new RetrofitService().getPaymentService().retroUpdateClickPayStep(Config.REQUEST_ID);
					Log.i("otherapp", "보냄");
				} else {
					Log.i("otherapp", "애매함");
				}
			}
			Current_Activity = componentInfo.getClassName().toString();
		} else {
			Current_Activity = "aaaa";
		}

		Log.e("popAct", "" + Current_Activity);
	}

	public void insertFriendUserInfo(final Response<AuthorizationData> response) {


		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<Friend> friend_list = response.body().getFriend_list();
				if(friend_list.size()!=0){
					for (int i = 0; i < friend_list.size(); i++) {
						String name = friend_list.get(i).getStudent_name();
						double use_days = friend_list.get(i).getUse_days();
						String first_goal_time = friend_list.get(i).getFirst_goal_time();
						String profile_image_url = friend_list.get(i).getProfile_image_url();

						String school;
						if (friend_list.get(i).getSchool_name().equals("null")) {
							school = "고등학교";
						} else {
							school = friend_list.get(i).getSchool_name();
						}

						String grade = "재학생";

						String birth = friend_list.get(i).getBirth();
						if (!birth.equals("null")) {

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

							if (birth.subSequence(2, 4).equals("01") || birth.subSequence(2, 4).equals("02")) {
								sum--;
							}

							int age = Integer.valueOf(TimeClass.getYear()) - sum;

							if (age == 17 || age == 14) {
								grade = "1학년";
							} else if (age == 18 || age == 15) {
								grade = "2학년";
							} else if (age == 19 || age == 16) {
								grade = "3학년";
							} else if (age > 19) {
								grade = "졸업생";
							}

						}

						FriendCard friendCard = new FriendCard(name, school, grade, use_days, first_goal_time, profile_image_url);
						fragment_list.add(new FriendFragment().newInstance(friendCard));
					}

					pb_get_friendlist.setVisibility(View.GONE);
					mAdapter = new FriendFragmentAdapter(getSupportFragmentManager(), fragment_list);
					mPager.setClipToPadding(false);
					mPager.setPadding(Utils.dpToPixels(48, getResources()), 0, Utils.dpToPixels(48, getResources()), 0);

					mPager.setAdapter(mAdapter);
					mPager.setVisibility(View.VISIBLE);

				}else{
					rl_get_friendlist.setVisibility(View.GONE);
					ll_purchase_bottom.setBackgroundResource(R.drawable.bg_tuto_end);
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if(resultCode==Activity.RESULT_OK){
			setResult(5555);
			finish();
		}else if(resultCode==22222){
			finish();
		}

	}


	String starttime;
	String startdate;
	com.ihateflyingbugs.hsmd.data.Date dates = new com.ihateflyingbugs.hsmd.data.Date();

	Map<String, String> articleParams ;
	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("TAG", articleParams);

	}



}
