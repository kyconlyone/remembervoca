package com.ihateflyingbugs.hsmd;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.BookAdapter.RowType;

public class BookListHeader implements BookItem {
	private final String title;

	public BookListHeader(String title) {
		this.title = title;
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
			view = (View) inflater
					.inflate(R.layout.adapter_hit_list_year, null);
			// Do some initialization
		} else {
			view = convert;
		}

		TextView title = (TextView) view.findViewById(R.id.title_tv);
		title.setText(this.title);

		return view;
	}

}
