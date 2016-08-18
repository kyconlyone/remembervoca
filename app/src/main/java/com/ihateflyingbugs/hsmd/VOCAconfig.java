package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.ihateflyingbugs.hsmd.login.AlertDialogManager;
import com.ihateflyingbugs.hsmd.login.ConnectionDetector;
import com.ihateflyingbugs.hsmd.login.WakeLocker;
import com.kakao.auth.KakaoSDK;
import com.kakao.util.helper.log.Logger;

import static com.ihateflyingbugs.hsmd.CommonUtilities.DISPLAY_MESSAGE_ACTION;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;


public class VOCAconfig extends Application {

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}

	public static final String DEVELOPER_KEY = "AIzaSyCtAaw5JNZm6HxdjTQdRUL9kmvg3MJgtdg";

	// YouTube video id
	public static final String YOUTUBE_VIDEO_CODE = "_oEA18Y8gM0";



	AsyncTask<Void, Void, Void> mRegisterTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	ConnectionDetector cd;
	String TAG = "haha";

	public static Context context;

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		//ACRA.init(this);

		context = getApplicationContext();

		instance = this;
		KakaoSDK.init(new KakaoSDKAdapter());

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			//			alert.showAlertDialog(VOCAconfig.this,
			//					"Internet Connection Error",
			//					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		Log.e("gcm_check", ""+this.getPackageName());
		GCMRegistrar.checkManifest(this);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		Log.e(TAG, "A");
		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM    
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
			Log.e(TAG, "B");
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.              
				Log.e(TAG, "C");
				Log.e("gcmid", GCMRegistrar.getRegistrationId(getApplicationContext()));

			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of com.ihateflyingbugs.vocaslide.AsyncTask instead of a raw thread.
				Log.e(TAG, "D");
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						Log.e(TAG, "E");
						ServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						Log.e(TAG, "F");
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			Log.e(TAG, "G");

			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */

			// Showing received message         
			//Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};


	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}


//	private Tracker mTracker;
//
//	/**
//	 * Gets the default {@link Tracker} for this {@link Application}.
//	 * @return tracker
//	 */
//	synchronized public Tracker getDefaultTracker() {
//		if (mTracker == null) {
//			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
//			mTracker = analytics.newTracker(R.xml.global_tracker);
//		}
//		return mTracker;
//	}

//	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
//
//	private final String PROPERTY_ID = "UA-67023311-1";
//	synchronized public Tracker getTracker(TrackerName trackerId) {
//		if (!mTrackers.containsKey(trackerId)) {
//
//			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//					: analytics.newTracker(R.xml.global_tracker);
//
//			mTrackers.put(trackerId, t);
//
//		}
//		return mTrackers.get(trackerId);
//	}

	private static volatile VOCAconfig instance = null;
	private static volatile Activity currentActivity = null;

	public static Activity getCurrentActivity() {
		Logger.d("++ currentActivity : " + (currentActivity != null ? currentActivity.getClass().getSimpleName() : ""));
		return currentActivity;
	}

	public static VOCAconfig getGlobalApplicationContext() {
		if(instance == null)
			throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
		return instance;
	}

	public static void setCurrentActivity(Activity currentActivity) {
		VOCAconfig.currentActivity = currentActivity;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(base);
	}
}
