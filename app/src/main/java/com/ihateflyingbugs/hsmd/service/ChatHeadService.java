package com.ihateflyingbugs.hsmd.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.lock.WebViewActivity;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.popup.MemoryPushDialogActivity;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.SplashActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;

import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ChatHeadService extends Service {
	String TAG = "ChatHeadService";

	private final IBinder mBinder = new LocalBinder();

	// 컴포넌트에 반환해줄 IBinder를 위한 클래스//
	public class LocalBinder extends Binder {
		public ChatHeadService getService() {
			return ChatHeadService.this;
		}
	}

	private static final int TRAY_MOVEMENT_REGION_FRACTION = 7; // Controls fraction of y-axis on screen within  which the tray stays.
	private static final int ANIMATION_FRAME_RATE = 20; // Animation frame rate per second.
	private static final int TRAY_DIM_X_DP = 48; // Width of the tray in dps
	private static final int TRAY_DIM_Y_DP = 48; // Height of the tray in dps
	private static final int MARGIN_BOTTOM_DP = 25; // BOTTOM MARGIN of the tray in dps
	private static final int TRASH_AREA_DP = 98; // TRASH of AREA in dps
	private static final int IN_AREA_DP = 60; // TRASH of AREA in dps

	private final int RIGHT = 11, LEFT = 22;


	Handler handler;
	Service service;
	DBPool db;
	private static SharedPreferences mPreference;

	int Display_Width;
	int Display_Height;


	// App or Blog
	int mAppBlogFlag;
	String mPushMessageString;
	long idx;
	String mProfile_URL;
	String mBlog_URL;




	private LinearLayout chatHeadLayout;
	RelativeLayout mChatHeadIconLayout;
	TextView mMemoryChatHead_CountTextView;
	private TextView chatHeadTextView;
	private Bitmap mProfileBitmap;

	private LinearLayout blogHeadLayout;
	RelativeLayout mBlogHeadIconLayout;
	private TextView blogHeadTextView;

	ImageView gradationImageView;
	ImageView DeleteImageView;

	int App_chathead_direction = 0;
	int Blog_chathead_direction = 0;


	private WindowManager mWindowManager; // Reference to the window
	private WindowManager.LayoutParams mChatHeadLayoutParams; // Parameters of the root layout
	private WindowManager.LayoutParams mBlogHeadLayoutParams;
	private WindowManager.LayoutParams mGradationImageViewParams;
	private WindowManager.LayoutParams mDeleteImageViewParams;

	LinearLayout.LayoutParams mChatHeadIconLayoutParams;
	RelativeLayout.LayoutParams mCountTextViewParams;
	LinearLayout.LayoutParams chatHeadTextViewParams;
	LinearLayout.LayoutParams mBlogHeadIconLayoutParams;
	LinearLayout.LayoutParams blogHeadTextViewParams;


	// Controls for animations
	private Timer mTrayAnimationTimer;
	private TrayAnimationTimerTask mTrayTimerTask;
	private Handler mAnimationHandler = new Handler();


	private boolean mIsTrayOpen = true;


	// Variables that control drag
	private int mStartDragX;
	private int mStartDragY; // Not Use
	private int mPrevDragX;
	private int mPrevDragY;

	int Trash_startX;
	int Trash_endX;
	int Trash_height;


	// Memory Push
	String mSystemNameString;
	int mGorgotNumberInt;
	String mMemoryPushCommentString;

	Intent intents;


	@Override
	public void onCreate() {
		super.onCreate();

		mPreference =  getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		db = DBPool.getInstance(getApplicationContext());
		handler = new Handler();
		service = this;

		mSystemNameString = mPreference.getString(MainValue.GpreSystemName, "Riche");
		mGorgotNumberInt = db.getMforget();



		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = mWindowManager.getDefaultDisplay();
		Display_Width = display.getWidth();
		Display_Height = display.getHeight();

		chatHeadLayout = new LinearLayout(this);
		chatHeadLayout.setOrientation(LinearLayout.HORIZONTAL);
		chatHeadLayout.setVisibility(View.INVISIBLE);

		mChatHeadIconLayout = new RelativeLayout(this);

		mMemoryChatHead_CountTextView = new TextView(this);
		mMemoryChatHead_CountTextView.setGravity(Gravity.CENTER);
		mMemoryChatHead_CountTextView.setBackgroundResource(R.drawable.icon_chathead_memory_small);
		Log.e("KARAM", TAG + " Forget Count = "  + db.getMforget());
		mMemoryChatHead_CountTextView.setText(Integer.toString(db.getMforget()));

		mMemoryChatHead_CountTextView.setTextColor(Color.parseColor("#E94F37"));
		mMemoryChatHead_CountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		mMemoryChatHead_CountTextView.setTypeface(null, Typeface.BOLD);

		chatHeadTextView = new TextView(this);
		chatHeadTextView.setText("");
		chatHeadTextView.setBackgroundResource(R.drawable.chat_notice_basic_right);
		chatHeadTextView.setTextColor(Color.BLACK);
		chatHeadTextView.setMaxWidth(Display_Width/2);
		chatHeadTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

		blogHeadLayout = new LinearLayout(this);
		blogHeadLayout.setOrientation(LinearLayout.HORIZONTAL);
		blogHeadLayout.setVisibility(View.INVISIBLE);

		mBlogHeadIconLayout = new RelativeLayout(this);

		blogHeadTextView = new TextView(this);
		blogHeadTextView.setText("");
		blogHeadTextView.setBackgroundResource(R.drawable.chat_notice_will_right);
		blogHeadTextView.setTextColor(Color.parseColor("#ffffff"));
		blogHeadTextView.setMaxWidth(Display_Width/2);
		blogHeadTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

		gradationImageView = new ImageView(this);
		gradationImageView.setImageResource(R.drawable.bottom_gradient);
		gradationImageView.setVisibility(View.INVISIBLE);

		DeleteImageView = new ImageView(this);
		DeleteImageView.setImageResource(R.drawable.deletebutton);
		DeleteImageView.setVisibility(View.INVISIBLE);



		mChatHeadLayoutParams = new WindowManager.LayoutParams(Utils.dpToPixels(TRAY_DIM_X_DP, getResources()), Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
		mChatHeadLayoutParams.width = LayoutParams.WRAP_CONTENT;
		mChatHeadLayoutParams.height = LayoutParams.WRAP_CONTENT;
		mChatHeadLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

		mBlogHeadLayoutParams = new WindowManager.LayoutParams(Utils.dpToPixels(TRAY_DIM_X_DP, getResources()), Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
		mBlogHeadLayoutParams.width = LayoutParams.WRAP_CONTENT;
		mBlogHeadLayoutParams.height = LayoutParams.WRAP_CONTENT;
		mBlogHeadLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

		mGradationImageViewParams = new WindowManager.LayoutParams(Display_Height, Utils.dpToPixels(120, getResources()), 0, Display_Height - 120, PixelFormat.TRANSLUCENT);
		mGradationImageViewParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		mGradationImageViewParams.alpha = 128;
		mGradationImageViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mGradationImageViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		mDeleteImageViewParams = new WindowManager.LayoutParams(Utils.dpToPixels(TRAY_DIM_X_DP, getResources()), Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		mDeleteImageViewParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		mDeleteImageViewParams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());
		mDeleteImageViewParams.alpha = 128;
		mDeleteImageViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mDeleteImageViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		mChatHeadIconLayoutParams = new LinearLayout.LayoutParams(Utils.dpToPixels(70, getResources()), Utils.dpToPixels(70, getResources()));
		mChatHeadIconLayoutParams.gravity = Gravity.CENTER;

		mCountTextViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mCountTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		chatHeadTextViewParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		chatHeadTextViewParams.gravity = Gravity.CENTER_VERTICAL;

		mBlogHeadIconLayoutParams = new LinearLayout.LayoutParams(Utils.dpToPixels(70, getResources()), Utils.dpToPixels(70, getResources()));
		mBlogHeadIconLayoutParams.gravity = Gravity.CENTER_VERTICAL;

		blogHeadTextViewParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		blogHeadTextViewParams.gravity = Gravity.CENTER_VERTICAL;


		mChatHeadIconLayout.setLayoutParams(mChatHeadIconLayoutParams);
		mMemoryChatHead_CountTextView.setLayoutParams(mCountTextViewParams);

		chatHeadTextView.setLayoutParams(chatHeadTextViewParams);
		mBlogHeadIconLayout.setLayoutParams(mBlogHeadIconLayoutParams);
		blogHeadTextView.setLayoutParams(blogHeadTextViewParams);

		calcArea();


		chatHeadLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
				new RetrofitService().getUseAppInfoService().retroUpdateUserPushLog(idx+"", "3421",  pm.isScreenOn()+"")
						.enqueue(new Callback<UseAppInfoData>() {
							@Override
							public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {

							}

							@Override
							public void onFailure(Throwable t) {
								Log.e(TAG, "onFailure : "+t.toString() );
							}
						});

				// 3
				if(mAppBlogFlag == 3) {
					NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
					nm.cancel(mAppBlogFlag);

					if(db.getMforget() == 0){
						Intent intent = new Intent(getBaseContext(), SplashActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("pushresponse", true);
						startActivity(intent);
					}
					else {
						Intent intent = new Intent(getBaseContext(), MemoryPushDialogActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("memorypush_comment", mMemoryPushCommentString);
						startActivity(intent);
					}
				}
				else {
					intents = new Intent(getBaseContext(), SplashActivity.class);
					intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
					nm.cancel(mAppBlogFlag);

					Log.e("KARAM", TAG + "  mAppBlogFlag  : " + mAppBlogFlag);

					// 2
					if(mAppBlogFlag == 2){
						intents.putExtra("pushresponse", true);
					}
					else if(mAppBlogFlag == 11){
						intents.putExtra("friend", true);
						db.setPushTable(1, 1);
					}
					else if(mAppBlogFlag == 12){
						intents.putExtra("kakaostory", true);
						db.setPushTable(2, 1);
					}
					else if(mAppBlogFlag == 13){
						Session.getCurrentSession().addCallback(new ISessionCallback() {
							@Override
							public void onSessionOpened() {
								String url = "http://lnslab.com/sat/loading.php";
								try {
									if(Session.getCurrentSession().isOpened() && !db.getStudentId().equals("")){
										url +="?sat_user_id="+db.getStudentId()+"&"+"access_token="+Session.getCurrentSession().getAccessToken();
									}
								} catch (IllegalStateException e) {
									Log.e(TAG, "mAppBlogFlag == 13 Error : " + e.toString());
								}

								Uri uri = Uri.parse(url);

								intents = new Intent(Intent.ACTION_VIEW, uri);
								intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
								db.setPushTable(3, 1);
							}

							@Override
							public void onSessionOpenFailed(KakaoException exception) {

							}
						});

					}
					else if(mAppBlogFlag == 14){
						String url = "http://lnslab.com/sat/sat_result.php?sat_user_id="+db.getStudentId();
						Uri uri = Uri.parse(url);

						intents = new Intent(Intent.ACTION_VIEW, uri);
						intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					else if(mAppBlogFlag==15){
						intents.putExtra("1freeday", true);
						intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					}

					startActivity(intents);
				}
				service.stopSelf();
			}
		});

		chatHeadLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getActionMasked();

				switch (action) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_CANCEL:
						DeleteImageView.setVisibility(View.VISIBLE);
						gradationImageView.setVisibility(View.VISIBLE);

						dragTray(action, (int) event.getRawX(), (int) event.getRawY(), chatHeadLayout, chatHeadTextView, mChatHeadIconLayout, mChatHeadLayoutParams);
						return false;
				}
				return false;
			}
		});


		blogHeadLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancel(mAppBlogFlag);

				Uri uri = Uri.parse(mBlog_URL);

				Intent intent = new Intent(ChatHeadService.this, WebViewActivity.class);
				intent.putExtra("uri", mBlog_URL);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


				startActivity(intent);
				service.stopSelf();
			}
		});

		blogHeadLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getActionMasked();

				switch (action) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_CANCEL:
						DeleteImageView.setVisibility(View.VISIBLE);
						gradationImageView.setVisibility(View.VISIBLE);

						dragTray(action, (int) event.getRawX(), (int) event.getRawY(), blogHeadLayout, blogHeadTextView, mBlogHeadIconLayout, mBlogHeadLayoutParams);
						return false;
				}
				return false;
			}
		});
	}


	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		if (intent != null) {
			FlurryAgent.logEvent("ChatHead:Called_ChatHead");

			// flag로  blog 푸시인지 혹은 app 푸시인지 결정
			mAppBlogFlag = intent.getExtras().getInt("flag");
			mPushMessageString = intent.getExtras().getString("msg");
			idx = intent.getExtras().getLong("idx");
			mProfile_URL = intent.getExtras().getString("picture");
			mBlog_URL = intent.getExtras().getString("url");

			mMemoryPushCommentString = intent.getExtras().getString("memorypush");

			if (Display_Width / 2 > mChatHeadLayoutParams.x + Utils.dpToPixels(35, getResources()) && mChatHeadLayoutParams.x != 0) {
				// app 챗헤드가 왼쪽에 있는 경우
				App_chathead_direction = LEFT;

				chatHeadLayout.removeAllViews();
				mChatHeadIconLayout.removeAllViews();

				Set_AppChatHead();
			}
			else if (Display_Width / 2 < mChatHeadLayoutParams.x || mChatHeadLayoutParams.x == 0) {
				// app 챗헤드가 오른쪽에 있거나 없는 경우
				App_chathead_direction = RIGHT;
				chatHeadLayout.removeAllViews();
				mChatHeadIconLayout.removeAllViews();

				Set_AppChatHead();
			}

			if (Display_Width / 2 > mBlogHeadLayoutParams.x + Utils.dpToPixels(35, getResources()) && mBlogHeadLayoutParams.x != 0) {
				// blog 챗헤드가 왼쪽에 있는 경우
				Blog_chathead_direction = LEFT;
				blogHeadLayout.removeAllViews();

				Set_BlogChatHead();
			}
			else if (Display_Width / 2 < mBlogHeadLayoutParams.x || mBlogHeadLayoutParams.x == 0) {
				// blog 챗헤드가 오른쪽에 있는 경우
				Blog_chathead_direction = RIGHT;
				blogHeadLayout.removeAllViews();

				Set_BlogChatHead();
			}


			if (chatHeadLayout.getVisibility() != View.VISIBLE && blogHeadLayout.getVisibility() != View.VISIBLE) {
				// 챗헤드가 없는 경우
				if (mAppBlogFlag < 4 || mAppBlogFlag == 11 || mAppBlogFlag == 12 || mAppBlogFlag == 13 || mAppBlogFlag == 14 || mAppBlogFlag == 15) {
					// app chathead 추가
					chatHeadTextView.measure(0, 0);

					mChatHeadLayoutParams.x = Display_Width - chatHeadTextView.getMeasuredWidth() - Utils.dpToPixels(70, getResources());
					mChatHeadLayoutParams.y = Display_Height / 2 - chatHeadLayout.getHeight();

					chatHeadTextView.setVisibility(View.VISIBLE);
					chatHeadLayout.setVisibility(View.VISIBLE);
					mWindowManager.addView(chatHeadLayout, mChatHeadLayoutParams);
				}
				else {
					// blog chathead 추가
					blogHeadTextView.measure(0, 0);

					mBlogHeadLayoutParams.x = Display_Width - blogHeadTextView.getMeasuredWidth() - Utils.dpToPixels(70, getResources());
					mBlogHeadLayoutParams.y = Display_Height / 2 - blogHeadLayout.getHeight();

					blogHeadTextView.setVisibility(View.VISIBLE);
					blogHeadLayout.setVisibility(View.VISIBLE);
					mWindowManager.addView(blogHeadLayout, mBlogHeadLayoutParams);
				}

				DeleteImageView.setVisibility(View.GONE);
				gradationImageView.setVisibility(View.GONE);

				try {

					mWindowManager.addView(gradationImageView, mGradationImageViewParams);
					mWindowManager.addView(DeleteImageView, mDeleteImageViewParams);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			else if (chatHeadLayout.getVisibility() != View.VISIBLE && blogHeadLayout.getVisibility() == View.VISIBLE) {
				// blog 챗헤드만 있는 경유
				if (mAppBlogFlag < 4 || mAppBlogFlag == 11 || mAppBlogFlag == 12 || mAppBlogFlag == 13 || mAppBlogFlag == 14 || mAppBlogFlag == 15) {
					// app 챗헤드가 추가될 때
					chatHeadTextView.measure(0, 0);

					mChatHeadLayoutParams.x = Display_Width - chatHeadTextView.getMeasuredWidth() - Utils.dpToPixels(70, getResources());
					mChatHeadLayoutParams.y = Display_Height / 2 - chatHeadLayout.getHeight();

					chatHeadTextView.setVisibility(View.VISIBLE);
					chatHeadLayout.setVisibility(View.VISIBLE);
					mWindowManager.addView(chatHeadLayout, mChatHeadLayoutParams);
				}
				else {
					// blog 챗헤드가 추가될 때
					blogHeadTextView.measure(0, 0);

					if (blogHeadTextView.getVisibility() != View.VISIBLE) {
						if (Blog_chathead_direction == RIGHT) {
							mBlogHeadLayoutParams.x = Display_Width - blogHeadTextView.getMeasuredWidth() - Utils.dpToPixels(70, getResources());
						}
					}

					blogHeadTextView.setVisibility(View.VISIBLE);
					mWindowManager.updateViewLayout(blogHeadLayout, mBlogHeadLayoutParams);
				}
			}
			else if (chatHeadLayout.getVisibility() == View.VISIBLE && blogHeadLayout.getVisibility() != View.VISIBLE) {
				// app 팻헤드만 있는 경우
				if (mAppBlogFlag < 4 || mAppBlogFlag == 11 || mAppBlogFlag == 12 || mAppBlogFlag == 13 || mAppBlogFlag == 14 || mAppBlogFlag == 15) {
					// app 챗헤드가 추가될 때
					chatHeadTextView.measure(0, 0);

					if (chatHeadTextView.getVisibility() != View.VISIBLE) {
						if (App_chathead_direction == RIGHT) {
							mChatHeadLayoutParams.x = Display_Width - chatHeadTextView.getMeasuredWidth() - Utils.dpToPixels(70, getResources());
						}
					}

					chatHeadTextView.setVisibility(View.VISIBLE);
					mWindowManager.updateViewLayout(chatHeadLayout, mChatHeadLayoutParams);
				}
				else {
					// blog 챗헤드가 추가될 때
					blogHeadTextView.measure(0, 0);

					mBlogHeadLayoutParams.x = Display_Width- blogHeadTextView.getMeasuredWidth() -Utils.dpToPixels(70, getResources());
					mBlogHeadLayoutParams.y = Display_Height / 2 - blogHeadLayout.getHeight();

					blogHeadTextView.setVisibility(View.VISIBLE);
					blogHeadLayout.setVisibility(View.VISIBLE);
					mWindowManager.addView(blogHeadLayout, mBlogHeadLayoutParams);
				}
			}
			else {
				if (mAppBlogFlag < 4 || mAppBlogFlag == 11 || mAppBlogFlag == 12 || mAppBlogFlag == 13 || mAppBlogFlag == 14 || mAppBlogFlag == 15) { // app 챗헤드
					chatHeadTextView.measure(0, 0);

					if (chatHeadTextView.getVisibility() != View.VISIBLE) {
						if (App_chathead_direction == RIGHT) {
							mChatHeadLayoutParams.x = Display_Width- chatHeadTextView.getMeasuredWidth() -Utils.dpToPixels(70, getResources());
						}
					}

					chatHeadTextView.setVisibility(View.VISIBLE);
					chatHeadLayout.setVisibility(View.VISIBLE);
					mWindowManager.updateViewLayout(chatHeadLayout, mChatHeadLayoutParams);
				}
				else { 	// blog 챗헤드    flag > 4
					blogHeadTextView.measure(0, 0);

					if (blogHeadTextView.getVisibility() != View.VISIBLE) {
						if (Blog_chathead_direction == RIGHT) {
							mBlogHeadLayoutParams.x = Display_Width- blogHeadTextView.getMeasuredWidth() -Utils.dpToPixels(70, getResources());
						}
					}

					blogHeadTextView.setVisibility(View.VISIBLE);
					blogHeadLayout.setVisibility(View.VISIBLE);
					mWindowManager.updateViewLayout(blogHeadLayout, mBlogHeadLayoutParams);
				}
			}

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						if (mAppBlogFlag < 4 || mAppBlogFlag == 11 || mAppBlogFlag == 12 || mAppBlogFlag == 13 || mAppBlogFlag == 14 || mAppBlogFlag == 15) {
							if (chatHeadLayout != null && chatHeadLayout.getVisibility() != View.GONE) {	// app chathead
								chatHeadTextView.measure(0, 0);
								chatHeadLayout.measure(0, 0);

								chatHeadTextView.setVisibility(View.GONE);
								chatHeadLayout.removeView(chatHeadTextView);

								if (App_chathead_direction == RIGHT) {
									mChatHeadLayoutParams.x = Display_Width - Utils.dpToPixels(70, getResources());
								}

								mWindowManager.updateViewLayout(chatHeadLayout, mChatHeadLayoutParams);
							}
						}
						else {
							if (blogHeadLayout != null && blogHeadLayout.getVisibility() != View.GONE) {	// blog chathead
								blogHeadTextView.measure(0, 0);
								blogHeadLayout.measure(0, 0);

								blogHeadTextView.setVisibility(View.GONE);
								blogHeadLayout.removeView(chatHeadTextView);

								if (App_chathead_direction == RIGHT) {
									mBlogHeadLayoutParams.x = Display_Width - Utils.dpToPixels(70, getResources());
								}

								mWindowManager.updateViewLayout(blogHeadLayout, mBlogHeadLayoutParams);
							}
						}

					} catch (final Exception e) {
						Log.e(TAG, "onStart  Exception " + e.toString());
					}
				}
			}, 10000);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	void Set_AppChatHead() {
		chatHeadTextView.setTextColor(Color.BLACK);
		chatHeadTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

		if (mAppBlogFlag == 2) {		// app 의지
			mChatHeadIconLayout.setBackgroundResource(R.drawable.will_app_chathead);
		}
		else if (mAppBlogFlag == 3) {	// app 기억
			mChatHeadIconLayout.setBackgroundResource(R.drawable.memory_app_chathead);

			if(db.getMforget() != 0)
				mChatHeadIconLayout.addView(mMemoryChatHead_CountTextView);

			chatHeadTextView.setTextColor(Color.parseColor("#A75926"));
			chatHeadTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		}
		else if (mAppBlogFlag == 1) {	// app 적중
			mChatHeadIconLayout.setBackgroundResource(R.drawable.target_app_chathead);
		}
		else if (mAppBlogFlag == 11) {	// 새로운 친구가입
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						InputStream in = new URL(mProfile_URL).openStream();
						mProfileBitmap = BitmapFactory.decodeStream(in);
					} catch (Exception e) {
						Log.e(TAG, "mAppBlogFlag == 11  왼쪽  Error : " + e.toString());
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					if (mProfileBitmap != null) {
						Drawable d = new BitmapDrawable(round(mProfileBitmap));
						mChatHeadIconLayout.setBackgroundDrawable(d);
					}
					//chatHeadImageView.setImageBitmap(round(mProfileBitmap));
				}

			}.execute();
		}
		else if(mAppBlogFlag == 12){	// 카카오스토리 공유
			mChatHeadIconLayout.setBackgroundResource(R.drawable.target_app_chathead);
		}
		else if(mAppBlogFlag == 13){	// 카톡 단어시험
			mChatHeadIconLayout.setBackgroundResource(R.drawable.target_app_chathead);
		}
		else if(mAppBlogFlag == 14){	// 카톡 단어시험 결과 확인
			mChatHeadIconLayout.setBackgroundResource(R.drawable.target_app_chathead);
		}
		else if(mAppBlogFlag == 15){	// 1일 무료기간 증정 푸시
			mChatHeadIconLayout.setBackgroundResource(R.drawable.img_chathead_24h);
		}

		chatHeadTextView.setBackgroundResource(R.drawable.chat_notice_basic_left);
		chatHeadTextView.setText(mPushMessageString);

		chatHeadLayout.addView(mChatHeadIconLayout);
		chatHeadLayout.addView(chatHeadTextView);
	}

	void Set_BlogChatHead() {
		if (mAppBlogFlag == 4) {	// blog 의지
			mBlogHeadIconLayout.setBackgroundResource(R.drawable.will_blog_chathead);
			blogHeadTextView.setBackgroundResource(R.drawable.chat_notice_will_left);
		}
		else if (mAppBlogFlag == 5) {	// blog 기억
			mBlogHeadIconLayout.setBackgroundResource(R.drawable.memory_blog_chathead);
			blogHeadTextView.setBackgroundResource(R.drawable.chat_notice_memory_left);
		}
		else if (mAppBlogFlag == 6) {	// blog 적중
			mBlogHeadIconLayout.setBackgroundResource(R.drawable.target_blog_chathead);
			blogHeadTextView.setBackgroundResource(R.drawable.chat_notice_target_left);
		}
		blogHeadTextView.setText(mPushMessageString);

		blogHeadLayout.addView(mBlogHeadIconLayout);
		blogHeadLayout.addView(blogHeadTextView);
	}


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void calcArea() {
		Trash_startX = Display_Width / 2 - Utils.dpToPixels(TRAY_DIM_X_DP, getResources());
		Trash_endX = Display_Width / 2 + Utils.dpToPixels(TRAY_DIM_X_DP, getResources());
		Trash_height = Display_Height - Utils.dpToPixels(TRASH_AREA_DP, getResources());
	}

	private void dragTray(int action, int x, int y, LinearLayout layout, TextView tv, RelativeLayout icon, LayoutParams param) {
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				// Cancel any currently running animations/automatic tray movements.
				if (mTrayTimerTask != null) {
					mTrayTimerTask.cancel();
					mTrayAnimationTimer.cancel();
				}

				// Store the start points
				mStartDragX = x;
				mPrevDragX = x;
				mPrevDragY = y;

				break;

			case MotionEvent.ACTION_MOVE:

				// Calculate position of the whole tray according to the drag, and update layout.
				float deltaX = x - mPrevDragX;
				float deltaY = y - mPrevDragY;
				param.x += deltaX;
				param.y += deltaY;
				mPrevDragX = x;
				mPrevDragY = y;
				mWindowManager.updateViewLayout(layout, param);

				if ((x > Trash_startX && x < Trash_endX) && (y > Trash_height)) {
					WindowManager.LayoutParams Inparams = new WindowManager.LayoutParams(
							Utils.dpToPixels(IN_AREA_DP, getResources()),
							Utils.dpToPixels(IN_AREA_DP, getResources()),
							WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
							PixelFormat.TRANSLUCENT);

					Inparams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
					Inparams.alpha = 128;
					Inparams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());

					Inparams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
					Inparams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

					Inparams.alpha = 128;

					mWindowManager.updateViewLayout(DeleteImageView, Inparams);
					mWindowManager.updateViewLayout(gradationImageView, mGradationImageViewParams);
				}
				else {
					WindowManager.LayoutParams Outparams = new WindowManager.LayoutParams(
							Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
							Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
							WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
							PixelFormat.TRANSLUCENT);

					Outparams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
					Outparams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

					Outparams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
					Outparams.y = Utils
							.dpToPixels(MARGIN_BOTTOM_DP, getResources());
					Outparams.alpha = 128;
					mWindowManager.updateViewLayout(DeleteImageView, Outparams);
					mWindowManager.updateViewLayout(gradationImageView, mGradationImageViewParams);
				}
				break;

			case MotionEvent.ACTION_UP:

			case MotionEvent.ACTION_CANCEL:
				if ((x > Trash_startX && x < Trash_endX) && (y > Trash_height)) {
					layout.setVisibility(View.GONE);
					mWindowManager.removeView(layout);
					DeleteImageView.setVisibility(View.GONE);
					gradationImageView.setVisibility(View.GONE);
					param.x = 0;
				}
				else {
					if ((mIsTrayOpen && (x - mStartDragX) <= 0) || (!mIsTrayOpen && (x - mStartDragX) >= 0))
						mIsTrayOpen = !mIsTrayOpen;
					mTrayTimerTask = new TrayAnimationTimerTask(x, layout, tv, icon, param);
					mTrayAnimationTimer = new Timer();
					mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);
					DeleteImageView.setVisibility(View.GONE);
					gradationImageView.setVisibility(View.GONE);

					WindowManager.LayoutParams Outparams = new WindowManager.LayoutParams(Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
							Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()), WindowManager.LayoutParams.TYPE_PHONE,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

					Outparams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
					Outparams.y = Utils.dpToPixels(MARGIN_BOTTOM_DP, getResources());

					Outparams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
					Outparams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

					Outparams.alpha = 128;
					mWindowManager.updateViewLayout(DeleteImageView, Outparams);
					mWindowManager.updateViewLayout(gradationImageView, mGradationImageViewParams);
				}
				break;
		}
	}

	@Override
	public void onDestroy() {

		if (mAppBlogFlag < 4 || mAppBlogFlag == 11 || mAppBlogFlag == 12|| mAppBlogFlag == 13 || mAppBlogFlag == 14 || mAppBlogFlag == 15) {
			if (chatHeadLayout != null && chatHeadLayout.getVisibility() != View.GONE) {
				mWindowManager.removeView(chatHeadLayout);
				mWindowManager.removeView(DeleteImageView);
				mWindowManager.removeView(gradationImageView);
				chatHeadLayout = null;
			}
		}
		else {
			if (blogHeadLayout != null && blogHeadLayout.getVisibility() != View.GONE) {
				mWindowManager.removeView(blogHeadLayout);
				mWindowManager.removeView(DeleteImageView);
				mWindowManager.removeView(gradationImageView);
				blogHeadLayout = null;
			}
		}

		super.onDestroy();
	}


	// Timer for animation/automatic movement of the tray.
	public class TrayAnimationTimerTask extends TimerTask {

		// Ultimate destination coordinates toward which the tray will move
		int mDestX;
		int mDestY;
		LinearLayout layout;
		private WindowManager.LayoutParams tempLayoutParams;


		public TrayAnimationTimerTask(int x, LinearLayout layout, TextView tv, RelativeLayout icon, LayoutParams param) {

			// Setup destination coordinates based on the tray state.
			super();
			this.tempLayoutParams = param;
			this.layout = layout;
			if (Display_Width / 2 > x) {
				mDestX = 0;	// - (int) (17 * density);
				layout.removeView(tv);
				if(mAppBlogFlag == 4) {
					tv.setBackgroundResource(R.drawable.chat_notice_will_left);
				}
				else if(mAppBlogFlag == 5) {
					tv.setBackgroundResource(R.drawable.chat_notice_memory_left);
				}
				else if(mAppBlogFlag == 6) {
					tv.setBackgroundResource(R.drawable.chat_notice_target_left);
				}
				else {
					tv.setBackgroundResource(R.drawable.chat_notice_basic_left);
				}

				layout.addView(tv);
			}
			else {
				mDestX = Display_Width - layout.getWidth();	// + (int) (17 * density);
				layout.removeView(icon);
				if(mAppBlogFlag == 4) {
					tv.setBackgroundResource(R.drawable.chat_notice_will_right);
				}
				else if(mAppBlogFlag == 5) {
					tv.setBackgroundResource(R.drawable.chat_notice_memory_right);
				}
				else if(mAppBlogFlag == 6) {
					tv.setBackgroundResource(R.drawable.chat_notice_target_right);
				}
				else {
					tv.setBackgroundResource(R.drawable.chat_notice_basic_right);
				}

				layout.addView(icon);
			}

			// Keep upper edge of the widget within the upper limit of screen
			int screenHeight = getResources().getDisplayMetrics().heightPixels;
			mDestY = Math.max(screenHeight / TRAY_MOVEMENT_REGION_FRACTION, tempLayoutParams.y);
			// Keep lower edge of the widget within the lower limit of screen
			mDestY = Math.min(((TRAY_MOVEMENT_REGION_FRACTION - 1) * screenHeight) / TRAY_MOVEMENT_REGION_FRACTION - icon.getWidth(), mDestY);
		}

		// This function is called after every frame.
		@Override
		public void run() {
			// handler is used to run the function on main UI thread in order to
			// access the layouts and UI elements.   
			mAnimationHandler.post(new Runnable() {
				@Override
				public void run() {
					if (Display_Width / 2 > 1) {
						tempLayoutParams.x = (2 * (tempLayoutParams.x - mDestX)) / 3 + mDestX;
						tempLayoutParams.y = (2 * (tempLayoutParams.y - mDestY)) / 3 + mDestY;
						if(layout.isShown())
							mWindowManager.updateViewLayout(layout, tempLayoutParams);

						if (Math.abs(tempLayoutParams.x - mDestX) < 2 && Math.abs(tempLayoutParams.y - mDestY) < 2) {
							TrayAnimationTimerTask.this.cancel();
							mTrayAnimationTimer.cancel();
						}
					}
					// Update coordinates of the tray
				}
			});
		}
	}

	// Bitmap Image Round
	private Bitmap round(Bitmap source) {
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
}