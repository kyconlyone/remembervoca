package com.ihateflyingbugs.hsmd.popup;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.ihateflyingbugs.hsmd.R;

//import com.google.android.gms.internal.pb;


public class KeepCouponPopup extends Activity {

	int sort;
	Intent intent;
	
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_keep_coupon);
		
		Button bt_keep_coupon = (Button)findViewById(R.id.bt_keep_coupon);
		
		bt_keep_coupon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});

	}


}
