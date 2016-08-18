package com.ihateflyingbugs.hsmd;

import com.ihateflyingbugs.hsmd.HitListAdapter.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class HitListHeader implements HitItem {

	public HitListHeader() {
	}

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convert) {
		// TODO Auto-generated method stub
		View view;
		if (convert == null) {
			view = (View) inflater.inflate(R.layout.adapter_hit_head, null);
			// Do some initialization
		} else {
			view = convert;
		}

		return view;
	}

}
