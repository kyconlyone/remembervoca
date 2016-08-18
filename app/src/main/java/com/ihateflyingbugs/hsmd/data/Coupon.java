package com.ihateflyingbugs.hsmd.data;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

public class Coupon implements Item{
	int prime_num;
	String coupon_name;
	String coupon_explain;
	boolean is_duplicatable;
	String valid_date;

	public Coupon(int prime_num, String coupon_name, boolean is_duplicatable, String coupon_explain, String valid_date) {

		this.prime_num = prime_num;
		this.coupon_name = coupon_name;
		this.coupon_explain = coupon_explain;
		this.is_duplicatable = is_duplicatable;
		this.valid_date = valid_date;
	}

	public int getPrimeNum() {
		return prime_num;
	}

	public String getCouponName() {
		return coupon_name;
	}

	public String getCouponExplain() {
		return coupon_explain;
	}

	public Boolean isDuplicatable() {
		return is_duplicatable;
	}

	public String getValidDate() {
		return valid_date;
	}

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.item_coupon_main, null);
		} else {
			view = convertView;
		}
    	
        ImageView iv_coupon;
    	TextView tv_coupon_name;
    	TextView tv_coupon_period;
    	
    	tv_coupon_name = (TextView)view.findViewById(R.id.tv_coupon_name);
    	tv_coupon_period = (TextView)view.findViewById(R.id.tv_coupon_period);
    	iv_coupon = (ImageView)view.findViewById(R.id.iv_coupon);

    	tv_coupon_name.setText(coupon_name);
    	tv_coupon_period.setText(valid_date+"까지");
		iv_coupon.setImageResource(R.drawable.droid);
		
		if(coupon_explain.equals("")){
			iv_coupon.setImageResource(R.drawable.icn_feed_coupon_launching);
		}else{
			iv_coupon.setImageResource(R.drawable.icn_feed_coupon_will);
		}
    	
    	return view;
	}
}
