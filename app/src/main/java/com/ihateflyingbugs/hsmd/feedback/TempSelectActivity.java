package com.ihateflyingbugs.hsmd.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.tutorial.InstallActivity;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;
import com.ihateflyingbugs.hsmd.tutorial.SplashActivity;

public class TempSelectActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.temp_select_activity);
		Button install = (Button) findViewById(R.id.install);
		Button main = (Button) findViewById(R.id.main);
		Button withoutLogin = (Button) findViewById(R.id.install_without_login);

		install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
				startActivity(intent);
			}
		});
		main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
			}
		});

		withoutLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), InstallActivity.class);
				startActivity(intent);
			}
		});

	}
}
