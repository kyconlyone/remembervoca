package com.ihateflyingbugs.hsmd.indicator;

import java.util.List;

import com.ihateflyingbugs.hsmd.data.CheckBoxCoupon;
import com.ihateflyingbugs.hsmd.data.Coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Playstore리뷰 내용을 담고있는 ListItem을 저장하는 ArrayAdapter
 * @author DH
 *
 */
public class CheckBoxCouponArrayAdapter extends ArrayAdapter<CheckBoxCoupon> {
	private LayoutInflater mInflater;

	public enum RowType {
		LIST_ITEM, HEADER_ITEM
	}

	public CheckBoxCouponArrayAdapter(Context context, List<CheckBoxCoupon> items) {
		super(context, 0, items);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}
	
	public boolean isAllItemChecked(){
		boolean isAllItemChecked = true;
		for(int i=0;i<getCount();i++){
			isAllItemChecked = isAllItemChecked && getItem(i).cb_coupon.isChecked();
		}
		return isAllItemChecked;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView(mInflater, convertView);
	}
}
