package com.ihateflyingbugs.hsmd.tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.FriendCard;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.indicator.BaseSampleActivity;
import com.ihateflyingbugs.hsmd.indicator.FriendFragment;
import com.ihateflyingbugs.hsmd.indicator.FriendFragmentAdapter;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.CouponData;
import com.ihateflyingbugs.hsmd.data.Friend;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RequestPaymentActivity extends BaseSampleActivity implements OnClickListener {

	public LinearLayout ll_tuto_pay1;
	public LinearLayout ll_tuto_pay2;
	public RelativeLayout rl_friend_pager;
	public LinearLayout ll_free_period_start;


	public Button bt_tuto_pay1;
	public Button bt_free_period_start;

	public Button bt_blog;
	public Button bt_home;

	public ImageView iv_pay_top;

	public TextView tv_tuto_pay1;
	public TextView tv_tuto_pay1_present;
	public TextView tv_coupon_period;

	public ImageView iv_tuto_pay1_profile;

	DBPool db;
	SharedPreferences mPreference;

	ProgressBar pb_request_payment;
	ProgressBar pb_get_friendlist;

	final String TAG = "RequestPaymentActivity";

	int month_price;
	int free_period = 0;

	FriendFragmentAdapter mAdapter;
	ViewPager mPager;

	List<FriendFragment> fragment_list;

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_request_payment_2);

		Log.d("ActiviityTest", "RequestPaymentActivity Create");

		db = DBPool.getInstance(getApplicationContext());

		handler = new Handler();

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		ll_tuto_pay1 = (LinearLayout) findViewById(R.id.ll_tuto_pay1);
		ll_tuto_pay2 = (LinearLayout) findViewById(R.id.ll_tuto_pay2);
		ll_free_period_start = (LinearLayout) findViewById(R.id.ll_free_period_start);

		rl_friend_pager = (RelativeLayout) findViewById(R.id.rl_friend_pager);

		bt_tuto_pay1 = (Button) findViewById(R.id.bt_tuto_pay1);
		bt_free_period_start = (Button) findViewById(R.id.bt_free_period_start);

		bt_blog  = (Button) findViewById(R.id.bt_blog);
		bt_home  = (Button) findViewById(R.id.bt_home);

		tv_tuto_pay1 = (TextView) findViewById(R.id.tv_tuto_pay1);
		tv_tuto_pay1_present = (TextView) findViewById(R.id.tv_tuto_pay1_present);
		tv_coupon_period = (TextView) findViewById(R.id.tv_coupon_period);

		iv_pay_top = (ImageView) findViewById(R.id.iv_pay2_top);
		iv_tuto_pay1_profile = (ImageView) findViewById(R.id.iv_tuto_pay1_profile);

		pb_get_friendlist = (ProgressBar) findViewById(R.id.pb_get_friendlist);
		pb_request_payment = (ProgressBar) findViewById(R.id.pb_request_payment);
		pb_request_payment.setProgress(10);

		pb_get_friendlist.setVisibility(View.VISIBLE);
		pb_get_friendlist.setProgress(10);

		mPreference = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE | MODE_MULTI_PROCESS);

		mPager = (ViewPager) findViewById(R.id.pager_friends);

		fragment_list = new ArrayList<FriendFragment>();

		tv_tuto_pay1.setVisibility(View.VISIBLE);
		pb_request_payment.setVisibility(View.GONE);

//		new Async_get_freeperiod(new VocaCallback() {
//
//			@Override
//			public void Resonponse(final JSONObject response) {
//				// TODO Auto-generated method stub
//				final SpannableStringBuilder sb1 = new SpannableStringBuilder();
//				String pay_1_text = "마지막 선물로 밀당영단어\n1일 무료 체험권을 드릴게요 ^^";
//				try {
//					pay_1_text = "마지막 선물로 밀당영단어\n"+response.getString("period")+"일 무료 체험권을 드릴게요 ^^";
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				sb1.append(pay_1_text);
//				sb1.setSpan(new ForegroundColorSpan(Color.rgb(0xe9, 0x4f, 0x37)), 8, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//				sb1.setSpan(new StyleSpan(Typeface.BOLD), 13, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						tv_tuto_pay1_present.setText(sb1);
//						try {
//							tv_coupon_period.setText(response.getString("period")+"일 사용권");
//							bt_free_period_start.setText(response.getString("period")+"일 무료사용 시작");
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				});
//
//			}
//
//			@Override
//			public void Exception() {
//				// TODO Auto-generated method stub
//				final SpannableStringBuilder sb1 = new SpannableStringBuilder();
//				String pay_1_text = "마지막 선물로 밀당영단어\n1일 무료 체험권을 드릴게요 ^^";
//				sb1.append(pay_1_text);
//				sb1.setSpan(new ForegroundColorSpan(Color.rgb(0xe9, 0x4f, 0x37)), 8, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//				sb1.setSpan(new StyleSpan(Typeface.BOLD), 13, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						tv_tuto_pay1_present.setText(sb1);
//					}
//				});
//			}
//		}).execute();

		new RetrofitService().getAuthorizationService().retroGetFriendList(db.getStudentId())
				.enqueue(new Callback<AuthorizationData>() {
					@Override
					public void onResponse(final Response<AuthorizationData> response, Retrofit retrofit) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								pb_get_friendlist.setVisibility(View.GONE);
								rl_friend_pager.setVisibility(View.VISIBLE);
								ll_free_period_start.setBackgroundResource(R.drawable.bg_purchase_bottom);

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

									mAdapter = new FriendFragmentAdapter(getSupportFragmentManager(), fragment_list);
									mPager.setClipToPadding(false);
									mPager.setPadding(Utils.dpToPixels(48, getResources()), 0, Utils.dpToPixels(48, getResources()), 0);

									mPager.setAdapter(mAdapter);
									pb_get_friendlist.setVisibility(View.GONE);
									mPager.setVisibility(View.VISIBLE);

								}else{
									rl_friend_pager.setVisibility(View.GONE);
									ll_free_period_start.setBackgroundResource(R.drawable.bg_tuto_end);
								}
							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						rl_friend_pager.setVisibility(View.GONE);
						ll_free_period_start.setBackgroundResource(R.drawable.bg_tuto_end);
					}
				});


		tv_tuto_pay1.setText("이제 " + mPreference.getString(MainValue.GpreName, "안알랴줌") + "님의\n영단어 마스터를 위한 준비가\n끝났습니다.");

		bt_tuto_pay1.setOnClickListener(this);
		bt_blog.setOnClickListener(this);
		bt_home.setOnClickListener(this);
		bt_free_period_start.setOnClickListener(this);

		getPicture();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_tuto_pay1:
				ll_tuto_pay1.setVisibility(View.GONE);
				ll_tuto_pay2.setVisibility(View.VISIBLE);
				break;
			case R.id.bt_blog:
				Intent intent_blog = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/mildang_"));
				startActivity(intent_blog);
				break;
			case R.id.bt_home:
				Intent intent_homepage = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.lnslab.com"));
				startActivity(intent_homepage);
				break;
			case R.id.bt_free_period_start:

				new RetrofitService().getPaymentService().retroInsertPaymentRequest(db.getStudentId(), "0")
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								finish();
							}

							@Override
							public void onFailure(final 	Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Log.e(TAG, "onFailure : " + t.toString());
										Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
									}
								});
							}
						});

				new RetrofitService().getPaymentService().retroInsertCoupon(db.getStudentId(), "2")
						.enqueue(new Callback<CouponData>() {
							@Override
							public void onResponse(Response<CouponData> response, Retrofit retrofit) {

							}

							@Override
							public void onFailure(Throwable t) {

							}
						});

				break;
		}

	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		// android.os.Process.killProcess(android.os.Process.myPid());
		setResult(55555);
		finish();
		return;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	String Current_Activity = "aaaa";

	Bitmap bitmap;
	static String User_id = null;

	private String thumbnailURL;

	String profileUrl = null;

	public void getPicture() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String path = Config.DB_FILE_DIR;

				File directory = new File(path);

				File file = new File(directory, Config.PROFILE_NAME); // or any
				// other
				// format
				// supported

				FileInputStream streamIn;

				try {
					streamIn = new FileInputStream(file);
					bitmap = BitmapFactory.decodeStream(streamIn); // This gets
					// the image
					streamIn.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (bitmap != null) {
					bitmap = round(bitmap);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (!bitmap.equals(null)) {
								iv_tuto_pay1_profile.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_tuto_pay1_profile.setImageResource(R.drawable.profile);
						}
					}
				});
			}
		});
		thread.start();
	}

	private Bitmap round(Bitmap source) {
		// TODO Auto-generated method stub
		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		return output;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		System.out.println("requestCode : " + requestCode);

		System.out.println("resultCode : " + resultCode);

		if (resultCode == 55555) {
			setResult(55555);
			finish();
		}

	}
}
