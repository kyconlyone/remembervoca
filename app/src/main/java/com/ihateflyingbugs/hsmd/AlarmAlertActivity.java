package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.alarm.Alarm;
import com.ihateflyingbugs.hsmd.alarm.StaticWakeLock;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Word;

import java.util.ArrayList;

public class AlarmAlertActivity extends Activity implements OnClickListener {

	private Alarm alarm;
	private MediaPlayer mediaPlayer;

	private Vibrator vibrator;

	private boolean alarmActive;

	private TextView tvMeaning, tvWord;
	private Button btnKnown, btnUnknown;

	private static ArrayList<Word> words;
	private DBPool db;
	private Word word;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.dialog_alarm);

		db = DBPool.getInstance(this);

		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");

		this.setTitle(alarm.getAlarmName());

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

			Log.d("kjw", "80Count = " + p80Count + "  after db 80 = " + tempCount);

			if(tempCount >= p80Count)
			{
				words.addAll(temp);

				wordsCount = words.size();
				restCount = Config.ONCE_WORD_COUNT - wordsCount;

				Log.d("kjw", "final rest" + restCount + "  wordsCOunt = " + wordsCount);

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

		tvMeaning = (TextView) findViewById(R.id.tvMeaning);
		tvWord = (TextView) findViewById(R.id.tvWord);
		//tvMeaning.setText(word.getMeaning());
		tvWord.setText(word.getWord());

		btnKnown = (Button) findViewById(R.id.btnKnown);
		btnUnknown = (Button) findViewById(R.id.btnUnknown);
		btnKnown.setOnClickListener(this);
		btnUnknown.setOnClickListener(this);

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
					case TelephonyManager.CALL_STATE_RINGING:
						Log.d(getClass().getSimpleName(), "Incoming call: "
								+ incomingNumber);
						try {
							mediaPlayer.pause();
						} catch (IllegalStateException e) {

						}
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						Log.d(getClass().getSimpleName(), "Call State Idle");
						try {
							mediaPlayer.start();
						} catch (IllegalStateException e) {

						}
						break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();

		startAlarm();

	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}

	private void startAlarm() {

		if (alarm.getSound()) {
			mediaPlayer = new MediaPlayer();
			if (alarm.getVibrate()) {
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = { 1000, 200, 200, 200 };
				vibrator.vibrate(pattern, 0);
			}
			try {
				mediaPlayer.setVolume(1.0f, 1.0f);

				AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.intro);
				mediaPlayer.setDataSource(afd.getFileDescriptor());
				afd.close();

				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				mediaPlayer.release();
				alarmActive = false;
			}
		}


		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				finish();
			}
		};
		handler.sendEmptyMessageDelayed(0, 10000);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {

		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onPause();
		StaticWakeLock.lockOff(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!alarmActive)
			return;

		boolean isKnown = true;
		switch(v.getId()){

			case R.id.btnKnown:
				isKnown = true;
				break;
			case R.id.btnUnknown:
				isKnown = false;
				break;
		}

//		db.updateWordFrequency(word.get_id(), word.getFrequency(), isKnown, word.getState());
//		db.updateProbabilityCount(word.getTime(), word.getState());

		db.updateWordInfo(word, isKnown);
		db.insertLevel(word, isKnown);

		//Config.Difficulty = db.calcLevel(10, AlarmAlertActivity.this);

		alarmActive = false;
		finish();

	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(this);
	}


}
