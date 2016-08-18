package com.ihateflyingbugs.hsmd.popup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Contact;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.FriendCard;
import com.ihateflyingbugs.hsmd.data.MainValue;
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
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RequestFreeCouponPopup extends BaseSampleActivity implements OnClickListener {


	//page1 
	public LinearLayout ll_30coupon1;

	public Button bt_30coupon1_payment;
	public Button bt_30coupon1_getcoupon;

	public ImageView iv_30coupon1_profile;

	public TextView tv_30coupon1;
	public TextView tv_30coupon1_present;


	//page2
	public LinearLayout ll_30coupon_invite;

	public ImageView iv_invite_progress;

	public Button bt_30coupon_invite;

	public TextView tv_invite_count;
	public TextView tv_invite_total;

	public ListView lv_mycontact_List;


	//page3
	public LinearLayout ll_30coupon2;
	public LinearLayout ll_free_period_start;

	public RelativeLayout rl_friend_pager;

	public Button bt_blog;
	public Button bt_home;
	public Button bt_free_period_start;

	public TextView tv_use_friend;
	public TextView tv_coupon_period;


	ProgressBar pb_request_payment;
	ProgressBar pb_get_friendlist;


	FriendFragmentAdapter mAdapter;

	ViewPager pager_friends;

	List<FriendFragment> fragment_list;




	DBPool db;
	SharedPreferences mPreference;


	final String TAG = "Request30FreeCoupon";

	int month_price;

	int free_period = 0;


	Handler handler;

	boolean isStep1 = true;
	boolean isStep2 = false;
	boolean isStep3 = false;

	String period = "30";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_request_freecoupon);


		db = DBPool.getInstance(getApplicationContext());

		handler = new Handler();

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);


		articleInviteParams = new HashMap<String, String>();




		//page1 
		ll_30coupon1= (LinearLayout) findViewById(R.id.ll_30coupon1);

		bt_30coupon1_payment= (Button) findViewById(R.id.bt_30coupon1_payment);
		bt_30coupon1_getcoupon= (Button) findViewById(R.id.bt_30coupon1_getcoupon);

		iv_30coupon1_profile= (ImageView) findViewById(R.id.iv_30coupon1_profile);

		tv_30coupon1= (TextView) findViewById(R.id.tv_30coupon1);
		tv_30coupon1_present= (TextView) findViewById(R.id.tv_30coupon1_present);


		//page2
		ll_30coupon_invite= (LinearLayout) findViewById(R.id.ll_30coupon_invite);

		iv_invite_progress= (ImageView) findViewById(R.id.iv_invite_progress);

		bt_30coupon_invite= (Button) findViewById(R.id.bt_30coupon_invite);

		tv_invite_count= (TextView) findViewById(R.id.tv_invite_count);
		tv_invite_total= (TextView) findViewById(R.id.tv_invite_total);

		lv_mycontact_List= (ListView) findViewById(R.id.lv_mycontact_List);

		//page3
		ll_30coupon2= (LinearLayout) findViewById(R.id.ll_30coupon2);
		ll_free_period_start= (LinearLayout) findViewById(R.id.ll_free_period_start);

		rl_friend_pager= (RelativeLayout) findViewById(R.id.rl_friend_pager);

		bt_blog= (Button) findViewById(R.id.bt_blog);
		bt_home= (Button) findViewById(R.id.bt_home);
		bt_free_period_start= (Button) findViewById(R.id.bt_free_period_start);

		tv_use_friend= (TextView) findViewById(R.id.tv_use_friend);
		tv_coupon_period= (TextView) findViewById(R.id.tv_coupon_period);


		pb_get_friendlist = (ProgressBar) findViewById(R.id.pb_get_friendlist);
		pb_request_payment = (ProgressBar) findViewById(R.id.pb_request_payment);
		pb_request_payment.setProgress(10);

		pb_get_friendlist.setVisibility(View.VISIBLE);
		pb_get_friendlist.setProgress(10);

		pager_friends = (ViewPager) findViewById(R.id.pager_friends);

		fragment_list = new ArrayList<FriendFragment>();


		tv_30coupon1.setText("이제 " + mPreference.getString(MainValue.GpreName, "안알랴줌") + "님의\n영단어 마스터를 위한 준비가\n끝났습니다."
				+ "\n이제 본격적으로 암기를 시작해볼까요?");

		tv_30coupon1.setVisibility(View.VISIBLE);
		pb_request_payment.setVisibility(View.GONE);

		lv_mycontact_List.setDividerHeight(0);


		bt_30coupon1_getcoupon.setOnClickListener(this);
		bt_30coupon1_payment.setOnClickListener(this);
		bt_blog.setOnClickListener(this);
		bt_home.setOnClickListener(this);
		bt_free_period_start.setOnClickListener(this);
		bt_30coupon_invite.setOnClickListener(this);

		new RetrofitService().getPaymentService().retrogetInviteFreePeriod()
				.enqueue(new Callback<PaymentData>() {
					@Override
					public void onResponse(Response<PaymentData> response, Retrofit retrofit) {

						period = ""+response.body().getFree_period();

						handler.post(new Runnable() {

							@Override
							public void run() {
								Log.e(TAG, "onResponse : "+period+"일 사용권");
								tv_coupon_period.setText(period+"일 사용권");
								bt_free_period_start.setText(period+"일 무료사용 시작");
								tv_30coupon1_present.setText("마지막 선물로 밀당영단어\n"+period+"일 무료 체험권을 드릴게요 ^^");
							}
						});
					}

					@Override
					public void onFailure(final Throwable t) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								Log.e(TAG, "onFailure : "+t.toString());
								tv_coupon_period.setText("30일 사용권");
								bt_free_period_start.setText("30일 무료사용 시작");
							}
						});

					}
				});


		getPicture();
	}

	String startinvitetime;

	HashMap<String, String> articleInviteParams;



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_30coupon1_getcoupon:


				FlurryAgent.logEvent(TAG+":Want30Coupon", true);
				startdate = dates.get_currentTime();
				starttime = String.valueOf(System.currentTimeMillis());


				ll_30coupon1.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out));
				ll_30coupon1.setVisibility(View.GONE);
				Animation Ani_bottomUp = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.slide_in_from_bottom);
				Ani_bottomUp.setAnimationListener(al);
				ll_30coupon_invite.startAnimation(Ani_bottomUp);
				ll_30coupon_invite.setVisibility(View.VISIBLE);

				isStep2 = true;
				isStep1 = false;
				isStep3 = false;
				List<Contact> friendlist = GetMyPhoneNumber();


				ContactAdapter adapter = new ContactAdapter(getApplicationContext(), R.layout.item_invitelist, friendlist);

				lv_mycontact_List.setAdapter(adapter);

				bt_30coupon_invite.setTextColor(new Color().rgb(100, 100, 100));


				startinvitetime =String.valueOf(System.currentTimeMillis());

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
										try{
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
												pager_friends.setClipToPadding(false);
												pager_friends.setPadding(Utils.dpToPixels(48, getResources()), 0, Utils.dpToPixels(48, getResources()), 0);

												pager_friends.setAdapter(mAdapter);
												pb_get_friendlist.setVisibility(View.GONE);
												pager_friends.setVisibility(View.VISIBLE);

											}else{
												rl_friend_pager.setVisibility(View.GONE);
												ll_free_period_start.setBackgroundResource(R.drawable.bg_tuto_end);
											}
										}catch (NullPointerException e){
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

				break;
			case R.id.bt_30coupon1_payment:
				FlurryAgent.logEvent(TAG+":Wantnot30CouponAnddirectlyPay", true);
//			startActivity(new Intent(RequestFreeCouponPopup.this, ShowCard.class));
				startActivityForResult(new Intent(RequestFreeCouponPopup.this, ShowCard.class), 55555);
//			finish();
				break;
			case R.id.bt_30coupon_invite:

				if(invite_count < 5){
					Toast.makeText(getApplicationContext(), "5명을 모두 선택해 주세요.", Toast.LENGTH_SHORT).show();
					return;
				}

				articleInviteParams.put("End", dates.get_currentTime());
				Log.e("splash", startdate+"        "+dates.get_currentTime());
				articleInviteParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(startinvitetime)))/1000);


				FlurryAgent.logEvent(TAG+":SelectAllInvitePerson", articleInviteParams);

				new RetrofitService().getPaymentService().retroInsertFinishDateAtInvite(db.getStudentId(), ""+100)
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										SmsManager sms = SmsManager.getDefault();
										String name = mPreference.getString(MainValue.GpreName, "이름없음");
										if(name.length()>4){
											name = name.substring(0, 4);
										}
										String context = name+"님이 수능날까지 함께 공부하고 싶은 소중한 친구로 당신을 선택했습니다."
												+"\nbit.ly/Mildang\n밀당영단어 무료증정";
										for(int i=0;i<invitelist.size();i++){
											if(invitelist.get(i).getNumber()!=null){
												sms.sendTextMessage(invitelist.get(i).getNumber(), null, context, null, null);
												Log.e("sendsms", ""+invitelist.get(i).getName() +"  : "+invitelist.get(i).getNumber());
											}
										}


										// TODO Auto-generated method stub
										ll_30coupon_invite.setVisibility(View.GONE);
										ll_30coupon2.setVisibility(View.VISIBLE);

										isStep3 = true;
										isStep2 = false;
										isStep1 = false;
									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
									}
								});
							}
						});

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

				finish();


				//
				//			new Async_make_willcoupon(db.getKakaoId(), "2", new VocaCallback() {
				//
				//				@Override
				//				public void Resonponse(JSONObject response) {
				//					// TODO Auto-generated method stub
				//
				//				}
				//				@Override
				//				public void Exception() {
				//					// TODO Auto-generated method stub
				//
				//					new Async_make_willcoupon(db.getKakaoId(), "2", new VocaCallback() {
				//
				//						@Override
				//						public void Resonponse(JSONObject response) {
				//							// TODO Auto-generated method stub
				//						}
				//
				//						@Override
				//						public void Exception() {
				//							// TODO Auto-generated method stub
				//						}
				//					}).execute();
				//
				//				}
				//			}).execute();
				break;
		}

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MainActivity.isShowCard = false;
		super.onDestroy();
	}


	AnimationListener al = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(RequestFreeCouponPopup.this, FriendRecommend_SelectDialogActivity.class);
			intent.putExtra("period", period);
			startActivity(intent);
		}
	};


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		// android.os.Process.killProcess(android.os.Process.myPid());
		if(isStep2){
			isStep3 =false;
			isStep2 = false;
			isStep1 =true;
			ll_30coupon1.setVisibility(View.VISIBLE);
			ll_30coupon_invite.setVisibility(View.GONE);
			ll_30coupon2.setVisibility(View.GONE);
		}else if(isStep3){
			finish();
		}else{
			setResult(55555);
			finish();
		}
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


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if((requestCode==55555)&&(resultCode==55555)){
			setResult(55555);
			finish();
		}

	}


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
								iv_30coupon1_profile.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_30coupon1_profile.setImageResource(R.drawable.profile);
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


	private List<Contact> GetMyPhoneNumber() {
		Cursor cursor = null;
		List<Contact> contactss = new ArrayList<Contact>();
		contactss.clear();
		try {
			cursor = getApplicationContext().getContentResolver().query(Phone.CONTENT_URI, null, null, null, Phone.DISPLAY_NAME + " ASC");
			int contactIdIdx = cursor.getColumnIndex(Phone._ID);
			int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
			int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
			int i =0;
			int count = 0;
			count = cursor.getCount();

			cursor.moveToFirst();
			do {


				String name = cursor.getString(nameIdx);
				String phoneNumber = cursor.getString(phoneNumberIdx);

				if(phoneNumber !=null){
					Log.v("contactcheck", "null");
					contactss.add(new Contact(name, phoneNumber));
				}

				i++;

			} while (cursor.moveToNext());

			for (int k = 0; k < contactss.size(); k++) {
				for (int j = k+1; j < contactss.size(); j++) {
					if(contactss.get(k).getNumber().equals(contactss.get(j).getNumber())){
						contactss.remove(j);
						Log.d("contactcheck", "equle");
						j--;
					}
				}
			}


		} catch (Exception e) {
			Log.e("contactss", ""+e.toString());
			contactss.add(new Contact("없음", "01000000000"));
		} finally {

			cursor.close();
		}

		return contactss;
	}

	int invite_count = 0;

	List<Contact> invitelist;

	public class ContactAdapter extends ArrayAdapter<Contact>{


		private LayoutInflater mInflater;

		public ContactAdapter(Context context, int resource,
							  List<Contact> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub

			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			invitelist = new ArrayList<Contact>();
			invitelist.clear();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_invitelist, null);
				viewHolder = new ViewHolder();
				viewHolder.name = (TextView)convertView.findViewById(R.id.tv_invite_name);
				viewHolder.ll_invite = (LinearLayout)convertView.findViewById(R.id.ll_invite_item);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder)convertView.getTag();
			}

			final Contact contact = getItem(position);

			if(!contact.isSelect()){
				viewHolder.ll_invite.setBackgroundResource(R.drawable.ll_contact_friend_selector);
				viewHolder.name.setTextColor(Color.BLACK);
			}else {
				viewHolder.ll_invite.setBackgroundResource(R.drawable.invite_list_selected);
				viewHolder.name.setTextColor(Color.WHITE);
			}

			viewHolder.ll_invite.setSelected(true);

			viewHolder.name.setText(contact.getName());

			viewHolder.ll_invite.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if(!getItem(position).isSelect()){
						FlurryAgent.logEvent("press_invitelist_for_invite", true);
						invite_count++;
						contact.setSelect(true);
						set_progress(true);
						tv_invite_count.setText(String.valueOf(invite_count));

						viewHolder.name.setTextColor(Color.WHITE);
						invitelist.add(contact);
						notifyDataSetChanged();
					}else{
						invite_count--;
						set_progress(false);
						FlurryAgent.logEvent("press_invitelist_for_cancel_invite", true);
						tv_invite_count.setText(String.valueOf(invite_count));
						contact.setSelect(false);
						viewHolder.name.setTextColor(Color.BLACK);
						invitelist.remove(contact);
						notifyDataSetChanged();
					}
				}
			});
			return convertView;
		}

	}

	public class ViewHolder
	{
		public LinearLayout ll_invite;
		public TextView name;
	}

	boolean isConfirm = false;
	int img_id = R.drawable.invite_0;
	public void set_progress(boolean set_calcur){
		int src = img_id;
		if(invite_count<6&&invite_count>=0){
			src = src+invite_count;
			iv_invite_progress.setImageResource(src);
		}


		if(invite_count >=5){
			bt_30coupon_invite.setTextColor(new Color().rgb(255, 255, 255));
		}else{
			bt_30coupon_invite.setTextColor(new Color().rgb(100, 100, 100));
		}
	}
	String starttime;
	String startdate;
	Date dates = new Date();

	Map<String, String> articleParams ;
	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent(TAG, articleParams);

	}

	@Override
	protected void onStop() {

		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e("splash", startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);

	}



}
