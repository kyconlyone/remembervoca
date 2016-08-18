package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class ScreenPopupActivity extends Activity {

	Button yes_Btn, no_Btn;

	private Vibrator mVibrator;
	private AudioManager mAudioManager;
	Uri notification;
	Ringtone mRingtone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_popup);

		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mRingtone = RingtoneManager.getRingtone(getApplicationContext(),
				notification);

		if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
			mVibrator.vibrate(1000);
		} else if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			mRingtone.play();
		}

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		yes_Btn = (Button) findViewById(R.id.yes_btn);
		yes_Btn.setOnClickListener(new OnClickListener() {

			@Override
			//
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		no_Btn = (Button) findViewById(R.id.no_btn);
		no_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
