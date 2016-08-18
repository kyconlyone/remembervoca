package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;

public class SuccessCouponPopUp extends Activity {



	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_successcoupon);

		Button bt_setgoal_popup = (Button)findViewById(R.id.bt_setgoal_popup);

		bt_setgoal_popup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(55555);
				finish();
			}
		});




	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Toast.makeText(SuccessCouponPopUp.this, "확인버튼을 눌러주세요!!", Toast.LENGTH_SHORT).show();
	}
}
