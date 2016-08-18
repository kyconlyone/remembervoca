package com.ihateflyingbugs.hsmd.feedback;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.ihateflyingbugs.hsmd.R;

public class CustomDialog extends Dialog {


	public CustomDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_custom);
	}

}
