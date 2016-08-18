package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class BookAdapter extends ArrayAdapter<BookItem> {
	private LayoutInflater inflater;

	public enum RowType {
		LIST_ITEM, HEADER_ITEM
	}

	public BookAdapter(Context context, List<BookItem> items) {
		super(context, 0, items);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;

	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView(inflater, convertView);
	}
}
