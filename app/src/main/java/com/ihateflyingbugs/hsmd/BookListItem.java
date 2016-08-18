package com.ihateflyingbugs.hsmd;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.BookAdapter.RowType;

public class BookListItem implements BookItem {

	private final String title;
	private final String word;
	private final String date;

	public BookListItem(String title, String word, String date) {
		this.title = title;
		this.word = word;
		this.date = date;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		if (convertView == null) {
			view = (View)inflater.inflate(R.layout.adapter_hit_list_book, null);
			// Do some initialization
		} else {
			view = convertView;
		}

		TextView title = (TextView) view.findViewById(R.id.title_tv);
		TextView word = (TextView) view.findViewById(R.id.word_tv);
		TextView date = (TextView) view.findViewById(R.id.date_tv);
		title.setText(this.title);
		word.setText(this.word);
		date.setText(this.date);

		return view;
	}

}
