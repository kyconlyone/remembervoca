package com.ihateflyingbugs.hsmd.data;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

public class CheckBoxCoupon implements Item{


	String student_id;
	int student_coupon_id;
	boolean is_used;
	String reg_time;
	int coupon_type_id;
	String coupon_type_name;
	String coupon_type_info;
	boolean is_duplicated;
	String student_coupon_valid_date;

	public LinearLayout ll_item_coupon;
	public ImageView iv_coupon;
	public TextView tv_coupon_name;
	public TextView tv_coupon_period;
	public TextView tv_coupon_explain;
	public CheckBox cb_coupon;

	public CheckBoxCoupon(int coupon_type_id, String coupon_type_name, boolean is_duplicated, String coupon_type_info, String student_coupon_valid_date) {

		this.coupon_type_id = coupon_type_id;
		this.coupon_type_name = coupon_type_name;
		this.coupon_type_info = coupon_type_info;
		this.is_duplicated = is_duplicated;
		this.student_coupon_valid_date = student_coupon_valid_date;
	}

	public int getPrimeNum() {
		return coupon_type_id;
	}

	public String getCouponName() {
		return coupon_type_name;
	}

	public String getCouponExplain() {
		return coupon_type_info;
	}

	public Boolean isDuplicatable() {
		return is_duplicated;
	}

	public String getValidDate() {
		return student_coupon_valid_date;
	}

	@Override
	public int getViewType() {
		return 0;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.item_coupon_inapp, null);
		} else {
			view = convertView;
		}
		ll_item_coupon = (LinearLayout) view.findViewById(R.id.ll_item_coupon);
		tv_coupon_name = (TextView)view.findViewById(R.id.tv_coupon_name);
		tv_coupon_period = (TextView)view.findViewById(R.id.tv_coupon_period);
		iv_coupon = (ImageView)view.findViewById(R.id.iv_coupon);
		cb_coupon = (CheckBox)view.findViewById(R.id.cb_coupon);
		tv_coupon_explain = (TextView)view.findViewById(R.id.tv_coupon_explain);

		tv_coupon_name.setText(coupon_type_name);
		tv_coupon_period.setText(student_coupon_valid_date+"까지");
		iv_coupon.setImageResource(R.drawable.droid);
		cb_coupon.setChecked(false);
		if(coupon_type_info.equals("")){
			iv_coupon.setImageResource(R.drawable.icn_purchase_coupon_launching);
			ll_item_coupon.setBackgroundResource(R.drawable.bg_purchase_coupon_blue_ready);
			tv_coupon_explain.setVisibility(View.GONE);
		}else{
			iv_coupon.setImageResource(R.drawable.icn_purchase_coupon_will);
			ll_item_coupon.setBackgroundResource(R.drawable.bg_purchase_coupon_red_ready);
			tv_coupon_explain.setText(""+coupon_type_info);
		}

		return view;
	}
}
