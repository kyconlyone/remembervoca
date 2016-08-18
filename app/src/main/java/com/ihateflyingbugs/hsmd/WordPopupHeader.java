package com.ihateflyingbugs.hsmd;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.WordPopupAdapter.RowType;

public class WordPopupHeader implements WordPopupItem {
	private final String year, title, count;

	public WordPopupHeader(String year, String title, String count) {
		this.year = year;
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
		view = (View) inflater.inflate(R.layout.adapter_word_popup_header,
				null);

		TextView year = (TextView) view.findViewById(R.id.year);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView count = (TextView) view.findViewById(R.id.count);

		year.setText(this.year);
		title.setText(this.title);
		count.setText(this.count);

		return view;
	}
}
