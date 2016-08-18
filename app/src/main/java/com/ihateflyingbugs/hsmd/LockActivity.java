package com.ihateflyingbugs.hsmd;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.lock.LockService;


public class LockActivity extends Activity implements ViewFactory{

	private final int DISAPPEAR_TIME = 1000;

	KeyguardManager.KeyguardLock k1;

	Handler serviceHandler;
	//	Task myTask = new Task();
	BroadcastReceiver _broadcastReceiver;

//	public boolean starting = true;
//	public boolean finishing = false;
//
//	public boolean paused = false;
//
//	public boolean idle = false;
//
//	public boolean dormant = false;
//
//	public boolean pendingExit = false;
//
//	public boolean slideWakeup = false;
//
//	public boolean pendingDismiss = false;
//
//	public boolean resurrected = false;

	private LinearLayout linearTrue, linearFalse;
	private FrameLayout frame;
	private PlayAreaView playAreaView;

	private TextView tvWordTrue, tvMeaningTrue, tvWordFalse, tvMeaningFalse;

	private String time;
	private String day;
	private String stringDayOfWeek[] = { "", "일", "월", "화", "수", "목", "금", "토" }; // 일요일이 1이고

	private final int GESTURE_Y = 400;

	private int windowWidth, windowHeight;
	private int index = 0;
	private final int interval = 40;
	private boolean isRunning = true;

	private float startY = 0;
	private float rawY = 0;

	private float mLastFocusY;

	private float scrollY;

	private static ArrayList<Word> words;
	private DBPool db;
	private Word word;

	private boolean isFinish = false;

//	@Override
//	public void onAttachedToWindow() {
//		// TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD|WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		super.onAttachedToWindow();
//	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		//| WindowManager.LayoutParams.FLAG_FULLSCREEN);
		updateLayout();

		if(getIntent()!=null&&getIntent().hasExtra("kill")&&getIntent().getExtras().getInt("kill")==1){
			// Toast.makeText(this, "" + "kill activityy", Toast.LENGTH_SHORT).show();
			finish();
		}

		startService(new Intent(this, LockService.class));

		StateListener phoneStateListener = new StateListener();
		TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

		linearTrue = (LinearLayout) findViewById(R.id.linearTrue);
		linearFalse = (LinearLayout) findViewById(R.id.linearFalse);

		tvWordTrue = (TextView) findViewById(R.id.tvWordTrue);
		tvMeaningTrue = (TextView) findViewById(R.id.tvMeaningTrue);
		tvWordFalse = (TextView) findViewById(R.id.tvWordFalse);
		tvMeaningFalse = (TextView) findViewById(R.id.tvMeaningFalse);

		db = DBPool.getInstance(this);
//		word = db.wordWithScore();


		words = db.wordsWithScore();
		words.addAll(db.wordsWithUnknown());

		int level = Config.Difficulty;
		int wordsCount = words.size();
		int restCount = Config.ONCE_WORD_COUNT - wordsCount;

		if(restCount > 0)
		{
			int p80Count = restCount * 4 / 5;

			ArrayList<Word> temp = db.wordsWithLevel(level, p80Count);
			int tempCount = temp.size();

			if(tempCount >= p80Count)
			{
				words.addAll(temp);

				wordsCount = words.size();
				restCount = Config.ONCE_WORD_COUNT - wordsCount;


				if(level - 1 == 0)
					words.addAll(db.wordsWithLevel(level, restCount));
				else
					words.addAll(db.wordsWithLevel(level - 1, restCount));
			}
			else
			{
				words.addAll(temp);

				wordsCount = words.size();
				restCount = Config.ONCE_WORD_COUNT - wordsCount;

				words.addAll(db.wordsWithLevel(level + 1, restCount));
			}
		}

		word = words.get(0);

		tvWordTrue.setText(word.getWord());
		tvWordFalse.setText(word.getWord());
		//tvMeaningTrue.setText(word.getMeaning());
		//tvMeaningFalse.setText(word.getMeaning());

		updateClock();

//		IntentFilter callbegin = new IntentFilter ("com.jinu.voca.lifecycle.CALL_START");
//		registerReceiver(callStarted, callbegin);
//
//		IntentFilter callpend = new IntentFilter ("com.jinu.voca.lifecycle.CALL_PENDING");
//		registerReceiver(callPending, callpend);
//
//		IntentFilter idleFinish = new IntentFilter ("com.jinu.voca.lifecycle.IDLE_TIMEOUT");
//		registerReceiver(idleExit, idleFinish);

		serviceHandler = new Handler();
		windowWidth = getWindowManager().getDefaultDisplay().getWidth();
		windowHeight = getWindowManager().getDefaultDisplay().getHeight();



		frame = (FrameLayout) findViewById(R.id.graphics_holder);
		playAreaView = new PlayAreaView(this);
		frame.addView(playAreaView);
	}

	public void updateClock() {
		GregorianCalendar calendar = new GregorianCalendar();

		int mHour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
		int mMin = calendar.get(GregorianCalendar.MINUTE);

//		String hour = new String("");
//		String min = new String("");

		String hour = "";
		String minute = "";

		if (mHour < 10) hour = hour + "0";
		hour = hour + mHour;

		if (mMin < 10) minute = minute + "0";
		minute = minute + mMin;

		time = hour + ":" + minute;

//		int y = calendar.get(GregorianCalendar.YEAR);
		int m = calendar.get(GregorianCalendar.MONTH)+1; // 1월(0), 2월(1), ..., 12월(11)
		int d = calendar.get(GregorianCalendar.DAY_OF_MONTH);
		int dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK); // 일요일(1), 월요일(2), ..., 토요일(7)

//		day = String.format("%4d년 %d월 %d일 %s요일", y, m, d, stringDayOfWeek[dayOfWeek]);
		day = String.format("%d월 %d일 %s요일", m, d, stringDayOfWeek[dayOfWeek]);

//		tvHour.setText(hour);
//		tvMinute.setText(min);

		if(playAreaView != null)
			playAreaView.refresh();
	}

	protected View inflateView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.activity_lock, null);
	}

	private void updateLayout() {
		LayoutInflater inflater = LayoutInflater.from(this);

		setContentView(inflateView(inflater));
	}

	@Override
	public void onBackPressed() {
		//Back will cause unlock

//		StartDismiss(getApplicationContext());
//		finishing=true;
	}

//	BroadcastReceiver callStarted = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context mContext, Intent intent) {
//			if (!intent.getAction().equals("com.jinu.voca.lifecycle.CALL_START")) return;
//
//			//we are going to be dormant while this happens, therefore we need to force finish
//			Log.v("guard received broadcast","completing callback and finish");
//
//			StopCallback();
//			finish();
//
//			return;
//		}
//	};
//
//	BroadcastReceiver callPending = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context mContext, Intent intent) {
//			if (!intent.getAction().equals("com.jinu.voca.lifecycle.CALL_PENDING")) return;
//			//incoming call does not steal focus till user grabs a tab
//			//lifecycle treats this like a home key exit
//			//forcing dormant state here will allow us to only exit if call is answered
//			dormant = true;
//			return;
//		}
//	};
//
//
//	BroadcastReceiver idleExit = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context mContext, Intent intent) {
//			if (!intent.getAction().equals("com.jinu.voca.lifecycle.IDLE_TIMEOUT")) return;
//
//			finishing = true;
//			idle = true;
//
//			Log.v("exit intent received","calling finish");
//			finish();//we will still have focus because this comes from the mediator as a wake event
//			return;
//		}
//	};

//	class Task implements Runnable {
//		public void run() {
//
//			ManageKeyguard.exitKeyguardSecurely(new LaunchOnKeyguardExit() {
//				public void LaunchOnKeyguardExitSuccess() {
//					Log.v("doExit", "This is the exit callback");
//					StopCallback();
//					finish();
//				}});
//		}}

	@Override
	protected void onStop() {
		super.onStop();
		if (_broadcastReceiver != null)
			unregisterReceiver(_broadcastReceiver);

		FlurryAgent.onEndSession(this);

//		if (pendingDismiss) return;
//
//		if (finishing) {
//			Log.v("lock stop","we have been unlocked by a user exit request");
//		}
//		else if (paused) {
//			if (hasWindowFocus()) {
//
//				//stop is called, we were already paused, and still have focus
//				//this means something is about to take focus, we should go dormant
//				dormant = true;
//				Log.v("lock stop","detected external event about to take focus, setting dormant");
//			}
//			else if (!hasWindowFocus()) {
//				//we got paused, lost focus, then finally stopped
//				//this only happens if user is navigating out via notif, popup, or home key shortcuts
//				Log.v("lock stop","onStop is telling mediator we have been unlocked by user navigation");
//
//				if (dormant) finishing = true;//dialog popup other than notif panel allowed a nav exit
//			}
//		}
//		else Log.v("unexpected onStop","lockscreen was stopped for unknown reason");
//
//		if (finishing) {
//			//most finish commands will already call these tw
//			//user exit unlock only calls finish and has set this finishing flag
//			//FIXME finishing and pendingDismiss might be redundant
//			//since pendingdismiss was added for instant unlock mode
//			StopCallback();
//			finish();
//		}

	}

	@Override
	protected void onPause() {
		super.onPause();

//		paused = true;
//
//		if (!starting && !hasWindowFocus() && !pendingDismiss) {
//			Log.v("navigation exit","got paused without focus, starting dismiss sequence");
//
//			//anytime we lose focus before pause, we are calling disable
//			//this will exit properly as we navigate out
//			ManageKeyguard.exitKeyguardSecurely(new LaunchOnKeyguardExit() {
//				public void LaunchOnKeyguardExitSuccess() {
//					Log.v("doExit", "This is the exit callback");
//					StopCallback();
//					finish();
//				}});
//
//		}
//		else {
//			if (hasWindowFocus()) Log.v("lock paused","normal pause - we still have focus");
//			else Log.v("lock paused","exit pause - don't have focus");
//			if (slideWakeup) {
//				Log.v("returning to sleep","toggling slide wakeup false");
//				slideWakeup = false;
//			}
//			if (resurrected) {
//				Log.v("returning to sleep","toggling resurrected false");
//				resurrected = false;
//				//sometimes the invalid screen on doesn't happen
//				//in that case we just turn off the flag at next pause
//			}
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("kjw","resuming, focus is " + hasWindowFocus());

//		paused = false;

		updateClock();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		System.exit(0);
//		serviceHandler.removeCallbacks(myTask);
//
//		serviceHandler = null;

//		unregisterReceiver(callStarted);
//		unregisterReceiver(callPending);
//		unregisterReceiver(idleExit);

		//StopCallback();
		//doesnt always get sent by the time screen off is going to happen again.

		Log.v("kjw","Destroying");
	}

//	@Override
//	public void onWindowFocusChanged (boolean hasFocus) {
//		if (hasFocus) {
//			Log.v("focus change","we have gained focus");
//			//Catch first focus gain after onStart here.
//			//this allows us to know if we actually got as far as having focus (expected but bug sometimes prevents
//			if (starting) {
//				starting = false;
//				//set our own lifecycle reference now that we know we started and got focus properly
//
//				//tell mediator it is no longer waiting for us to start up
//				StartCallback();
//			}
//			else if (dormant) {
//				Log.v("regained","we are no longer dormant");
//				dormant = false;
//				resurrected = true;
//			}
//			else if (pendingExit) {
//				Log.v("regained","we are no longer pending nav exit");
//				pendingExit = false;
//				ManageKeyguard.reenableKeyguard();
//			}
//		}
//		else if (!pendingDismiss) {
//			if (!finishing && paused) {
//				//Handcent popup issue-- we haven't gotten resume & screen on yet
//				//Handcent is taking focus first thing
//				//So it is now behaving like an open of notif panel where we aren't stopped and aren't even getting paused
//
//				//we really need to know we were just resumed and had screen come on to do this exit
//				//Need to implement the method check tool so we can rely on mediator bind in pre 2.1
//
//				if (dormant) Log.v("dormant handoff complete","the external event now has focus");
//				else {
//					if (isScreenOn()) {
//						Log.v("home key exit","launching full secure exit");
//
//						ManageKeyguard.disableKeyguard(getApplicationContext());
//						serviceHandler.postDelayed(myTask, 50);
//					}
//					else {
//						//Here's the handcent case
//						//if you then exit via a link on pop,
//						//we do get the user nav handling on onStop
//						Log.v("popup event","focus handoff before screen on, nav exit possible");
//						dormant = true;
//						//we need to be dormant so we realize once the popup goes away
//					}
//				}
//			}
//			else if (!paused) {
//				//not paused, losing focus, we are going to manually disable KG
//				Log.v("focus yielded while active","about to exit through notif nav");
//				pendingExit = true;
//				ManageKeyguard.disableKeyguard(getApplicationContext());
//			}
//		}
//
//	}




	protected void onStart() {
		super.onStart();
		Log.v("lockscreen start success","setting flags");
		_broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent)
			{
				if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
					updateClock();
				}
			}
		};

		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

//		if (finishing) {
//			finishing = false;
//			Log.v("re-start","we got restarted while in Finishing phase, wtf");
//			//since we are sometimes being brought back, safe to ensure flags are like at creation
//		}
	}

//	public void StartCallback() {
//		Intent i = new Intent("com.jinu.voca.lifecycle.LOCKSCREEN_PRIMED");
//		getApplicationContext().sendBroadcast(i);
//	}
//
//	public void StopCallback() {
//		Intent i = new Intent("com.jinu.voca.lifecycle.LOCKSCREEN_EXITED");
//		getApplicationContext().sendBroadcast(i);
//	}

//	public void StartDismiss(Context mContext) {
//
//		PowerManager myPM = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//		myPM.userActivity(SystemClock.uptimeMillis(), false);
//		//the KeyguardViewMediator poke doesn't have enough time to register before our handoff sometimes (rare)
//		//this might impact the nexus more than droid. need to test further
//		//result is the screen is off (as the code is successful)
//		//but no keyguard, have to hit any key to wake it back up
//
//		//we solved this by using a wake lock, but only applicable in instant unlock circumstance
//
//		Class w = DismissActivity.class;
//
//		Intent dismiss = new Intent(mContext, w);
//		dismiss.setFlags(//Intent.FLAG_ACTIVITY_NEW_TASK//For some reason it requires this even though we're already an activity
//				Intent.FLAG_ACTIVITY_NO_USER_ACTION//Just helps avoid conflicting with other important notifications
//				| Intent.FLAG_ACTIVITY_NO_HISTORY//Ensures the activity WILL be finished after the one time use
//				| Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//		pendingDismiss = true;
//		startActivity(dismiss);
//	}

//	public boolean isScreenOn() {
//		//Allows us to tap into the 2.1 screen check if available
//
//		boolean on = false;
//
//		if(Integer.parseInt(Build.VERSION.SDK) < 7) {
//			//we will bind to mediator and ask for the isAwake, if on pre 2.1
//			//for now we will just use a pref since we only need it during life cycle
//			//so we don't have to also get a possibly unreliable screen on broadcast within activity
//			Log.v("pre 2.1 screen check","grabbing screen state from prefs");
//			SharedPreferences settings = getSharedPreferences("myLock", 0);
//			on = settings.getBoolean("screen", false);
//
//		}
//		else {
//			PowerManager myPM = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//			on = myPM.isScreenOn();
//		}
//
//		return on;
//	}


	private class PlayAreaView extends View {

		private final String GUIDE_TEXT = "∧\n\n알면 위로\n모르면 아래로\n\n∨";

		private Matrix translate;
		private Bitmap droid;
//		private Bitmap droid, up, down;

		private Matrix animateStart;
		private Interpolator animateInterpolator;
		private Interpolator animateInterpolator_b;
		private long startTime;
		private long endTime;
		private float totalAnimDx;
		private float totalAnimDy;

		private Paint timePaint = new Paint();
		private Paint dayPaint = new Paint();
		private Paint wordPaint = new Paint();
		private Paint guidePaint = new Paint();
		ViewConfiguration vc = ViewConfiguration.get(this.getContext());
		private int mSlop;

		public void onAnimateMove(float dx, float dy, long duration) {
			animateStart = new Matrix(translate);
			animateInterpolator = new OvershootInterpolator();
			animateInterpolator_b = new BounceInterpolator();
			startTime = System.currentTimeMillis();
			endTime = startTime + duration+300;
			totalAnimDx = dx;
			totalAnimDy = dy;
			post(new Runnable() {
				@Override
				public void run() {

					onAnimateStep();

				}
			});
		}

		private void onAnimateStep() {
			long curTime = System.currentTimeMillis();
			float percentTime = (float) (curTime - startTime)
					/ (float) (endTime - startTime);
			float percentDistance = animateInterpolator_b
					.getInterpolation(percentTime);
			float curDx = percentDistance * totalAnimDx;
			float curDy = percentDistance * totalAnimDy;
			translate.set(animateStart);
			onMove(curDx, curDy);

			if (percentTime < 1.0f) {
				post(new Runnable() {
					@Override
					public void run() {
						onAnimateStep();
					}
				});
			}else{
				onResetLocation();
				isFinish = false;
				this.setClickable(true);
			}
		}

		public void onMove(float dx, float dy) {
			translate.postTranslate(dx, dy);
			invalidate();
		}

		public void onResetLocation() {
			translate.reset();
			invalidate();
		}

		public void onSetLocation(float dx, float dy) {
			translate.postTranslate(dx, dy);
		}

		public void refresh()
		{
			invalidate();
		}

		public PlayAreaView(Context context) {
			super(context);

			mSlop = vc.getScaledTouchSlop();

			translate = new Matrix();




//			up = BitmapFactory.decodeResource(getResources(), R.drawable.lock_up_icon);
//			down = BitmapFactory.decodeResource(getResources(), R.drawable.lock_down_icon);

			timePaint.setColor(Color.BLACK);

			timePaint.setTextAlign(Align.LEFT);


			dayPaint.setColor(Color.BLACK);

			dayPaint.setTextAlign(Align.LEFT);

			wordPaint.setColor(Color.WHITE);

			wordPaint.setTextAlign(Align.CENTER);

			guidePaint.setColor(Color.WHITE);

			guidePaint.setTextAlign(Align.CENTER);
			if(windowHeight>1280){
				timePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_time_font_large_size));
				dayPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_day_font_large_size));
				wordPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_word_font_large_size));
				guidePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_guide_font_large_size));
			}else{
				timePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_time_font_size));
				dayPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_day_font_size));
				wordPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_word_font_size));
				guidePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lock_guide_font_size));
			}
		}


		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(droid, translate, null);
			canvas.concat(translate);

			int xPos = (int) (canvas.getWidth() / 20);
			int xCenterPos = canvas.getWidth() / 2;
			int timeYPos = (int) ((canvas.getHeight() / 15) - ((timePaint.descent() + timePaint.ascent()) / 2));
//			int timeYPos = 10;

			Rect timeBounds = new Rect();
			timePaint.getTextBounds(time, 0, time.length(), timeBounds);

			canvas.drawText(time, xPos, timeYPos, timePaint);
			canvas.drawText(day, xPos, timeYPos + timeBounds.bottom + 50, dayPaint);

			String w = word.getWord();
			wordPaint.getTextBounds(w, 0, w.length(), timeBounds);

			int wordY = (int) ((canvas.getHeight() / 3.3) - ((wordPaint.descent() + wordPaint.ascent()) / 2));
			canvas.drawText(w, xCenterPos, wordY, wordPaint);
//			canvas.drawBitmap(up, xCenterPos, wordY + timeBounds.top - 80 , null);
//			canvas.drawBitmap(down, xCenterPos, wordY + timeBounds.bottom + 30, null);

			int guideY = (int) ((canvas.getHeight() * 2 / 3) - ((wordPaint.descent() + wordPaint.ascent()) / 2));
			for (String line: GUIDE_TEXT.split("\n"))
			{
				canvas.drawText(line, xCenterPos, guideY, guidePaint);
				guideY += -guidePaint.ascent() + guidePaint.descent();
			}

//			canvas.drawBitmap(bitmap, matrix, paint)

//			canvas.drawBi
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {

			if(!isFinish)
			{


				final int action = ev.getAction();

				final boolean pointerUp =
						(action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP;
				final int skipIndex = pointerUp ? ev.getActionIndex() : -1;

				float sumY = 0;
				final int count = ev.getPointerCount();
				for (int i = 0; i < count; i++) {
					if (skipIndex == i) continue;
					sumY += ev.getY(i);
				}
				final int div = pointerUp ? count - 1 : count;
				final float focusY = sumY / div;

				switch(ev.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						onResetLocation();
						startY = ev.getY();
						mLastFocusY = focusY;

						break;

					case MotionEvent.ACTION_MOVE:

						rawY = ev.getRawY();
						scrollY = mLastFocusY - focusY;

						if(ev.getY() - startY > 0)
						{
							linearFalse.setVisibility(View.VISIBLE);
							linearTrue.setVisibility(View.INVISIBLE);
						}
						else if(startY - ev.getY() > 0)
						{
							linearFalse.setVisibility(View.INVISIBLE);
							linearTrue.setVisibility(View.VISIBLE);
						}else if(Math.abs(startY - ev.getY()) > mSlop){
							isFinish = true;
							this.getParent().requestDisallowInterceptTouchEvent(true);
							MotionEvent cancelEvent = MotionEvent.obtain(ev);
							cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
									(ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
							this.onTouchEvent(cancelEvent);
							cancelEvent.recycle();
						}

						this.onMove(0, -scrollY);
						mLastFocusY = focusY;

						break;

					case MotionEvent.ACTION_UP:

						final float distanceTimeFactor = 0.9f;
						long delay = (long)(1000 * distanceTimeFactor);
						if(ev.getY() - startY > GESTURE_Y)
						{

							Log.d("kjw", "down gesture " );
							onAnimateMove(0, windowHeight,  delay);

//						db.updateWordFrequency(word.get_id(), word.getFrequency(), false, word.getState());
//						db.updateProbabilityCount(word.getTime(), word.getState());
							db.updateWordInfo(word, false);
							db.insertLevel(word, false);
							//Config.Difficulty = db.calcLevel(10, LockActivity.this);

							new Handler().postDelayed(new Runnable(){
								@Override
								public void run() {
									finish();
								}
							}, DISAPPEAR_TIME);

//						goNextTutorial(delay);
						}
						else if(startY - ev.getY() > GESTURE_Y)
						{

							Log.d("kjw", "up gesture " );
							onAnimateMove(0, -windowHeight, delay);

//						db.updateWordFrequency(word.get_id(), word.getFrequency(), true, word.getState());
//						db.updateProbabilityCount(word.getTime(), word.getState());
							db.updateWordInfo(word, true);
							db.insertLevel(word, true);
							//Config.Difficulty = db.calcLevel(10, LockActivity.this);

							new Handler().postDelayed(new Runnable(){
								@Override
								public void run() {
									finish();
								}
							}, DISAPPEAR_TIME);

//						goNextTutorial(delay);
						}
						else
						{
							if(ev.getY() - startY < 0){
								isFinish = true;
								onAnimateMove(0, -(ev.getY() - startY),  delay);
							}else if(startY - ev.getY() < 0){
								isFinish = true;
								onAnimateMove(0, startY - ev.getY(), delay);
							}
						}

						break;
				}
			}

			return true;
		}

	}

	@Override
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return imageView;
	}

	@Override
	public void finish() {
		isRunning = false;
		super.finish();
	}

	class StateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch(state){
				case TelephonyManager.CALL_STATE_RINGING:
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					System.out.println("call Activity off hook");
					finish();



					break;
				case TelephonyManager.CALL_STATE_IDLE:
					break;
			}
		}
	};


	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode == KeyEvent.KEYCODE_POWER)||(keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)) {
			//this is where I can do my stuff
			return true; //because I handled the event
		}
		if((keyCode == KeyEvent.KEYCODE_HOME)){

			Log.d("kjw", "ket down home !!!");
			return true;
		}

		return false;

	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_POWER ||(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)||(event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
			//Intent i = new Intent(this, NewActivity.class);
			//startActivity(i);
			return false;
		}
		if((event.getKeyCode() == KeyEvent.KEYCODE_HOME)){

			Log.d("kjw", "dispatchKey home !!!");
			return true;
		}
		return false;
	}


}
