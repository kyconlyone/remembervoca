package com.ihateflyingbugs.hsmd;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.WordPopupAdapter.RowType;

public class WordPopupBody implements WordPopupItem {
	private final String title, count;

	public WordPopupBody(String title, String count) {
		this.title = title;
		this.count = count;
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
		view = (View) inflater.inflate(R.layout.adapter_word_popup_body, null);
		// Do some initialization

		TextView title = (TextView) view.findViewById(R.id.title);
		TextView count = (TextView) view.findViewById(R.id.count);

		title.setText(this.title);
		count.setText(this.count);

		return view;
	}
}
