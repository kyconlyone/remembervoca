package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ihateflyingbugs.hsmd.animtuto.MainActivity;

public class LastInstallActivity extends Activity {

	Button bt_confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_last_install);

		bt_confirm = (Button)findViewById(R.id.bt_confirm);
		bt_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(LastInstallActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
