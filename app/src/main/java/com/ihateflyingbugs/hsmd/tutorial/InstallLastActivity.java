package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.animtuto.MainActivity;
import com.ihateflyingbugs.hsmd.animtuto.WillTutoActivity;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;

import org.andlib.ui.CircleAnimationView;

import java.util.HashMap;
import java.util.Map;

public class InstallLastActivity extends Activity {
	CircleAnimationView bt_confirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_last_install_small);
		}else{
			setContentView(R.layout.activity_last_install);
		}

		bt_confirm = (CircleAnimationView) findViewById(R.id.bt_confirm);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTextSize(WillTutoActivity.spTopx(getApplicationContext(), 12));
		paint.setAntiAlias(true);
		paint.setAlpha(222);
		paint.setTextAlign(Paint.Align.CENTER);

		bt_confirm.setText("완료");

		bt_confirm.setAnimButton(paint, R.drawable.redbutton, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(InstallLastActivity.this, MainActivity.class);
//						overridePendingTransition(R.anim.tutorial_activity_in, R.anim.tutorial_activity_in);
						startActivity(intent);
						finish();
					}
				});
			}
		});
	}

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart() {

		super.onStart();

		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();

		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("InstallLastActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			// Do whatever
			articleParams.put("WIFI", "On");
		} else {
			articleParams.put("WIFI", "Off");
		}

		FlurryAgent.endTimedEvent("InstallLastActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put(
				"Duration",
				""
						+ ((Long.valueOf(System.currentTimeMillis()) - Long
						.valueOf(starttime))) / 1000);


		FlurryAgent.onEndSession(this);

		// your code

	}
}