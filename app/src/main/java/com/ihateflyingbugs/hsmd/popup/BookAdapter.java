package com.ihateflyingbugs.hsmd.popup;

import java.util.ArrayList;

import com.ihateflyingbugs.hsmd.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookAdapter extends BaseAdapter {
	Book book;
	Context mContext;

	TextView tvYear, tvBook, tvCount;
	TextView divider;

	ArrayList<Book> bookData;

	boolean flag;

	public BookAdapter(Context context) {
		super();
		mContext = context;
		bookData = new ArrayList<Book>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bookData.size();
	}

	@Override
	public Book getItem(int position) {
		// TODO Auto-generated method stub
		return bookData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;

		if (v == null) {
			v = ((LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.info_popup_list1, null);
		}

		tvYear = (TextView) v.findViewById(R.id.year);
		tvBook = (TextView) v.findViewById(R.id.book);
		tvCount = (TextView) v.findViewById(R.id.count);
		divider = (TextView) v.findViewById(R.id.divider);

		book = getItem(position);

		if (book != null) {
			tvYear.setText(book.getYear());
			tvBook.setText(book.getBook());
			tvCount.setText(book.getCount());
		}
		return v;
	}

	public void add(Book book) {
		bookData.add(book);
	}

}
