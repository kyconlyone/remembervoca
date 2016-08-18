package com.ihateflyingbugs.hsmd.indicator;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Coupon;
import com.ihateflyingbugs.hsmd.data.Item;

public final class CouponMainFragment implements Item {
    private Coupon coupon;
    
    Handler handler;
    
    ImageView iv_coupon;
	TextView tv_coupon_name;
	TextView tv_coupon_period;

    public CouponMainFragment newInstance(Coupon coupon) {
    	CouponMainFragment fragment = new CouponMainFragment();
    	fragment.coupon = coupon;
        return fragment;
    }
    
    public CouponMainFragment() {
		// TODO Auto-generated constructor stub
	}
    
    public CouponMainFragment(Coupon coupon){
    	this.coupon = coupon;
    }

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		// TODO Auto-generated method stub
		return null;
	}


}
