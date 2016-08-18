package com.ihateflyingbugs.hsmd;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ihateflyingbugs.hsmd.BookAdapter.RowType;

public class WordPopupAdapter extends ArrayAdapter<WordPopupItem> {
	private LayoutInflater inflater;

	public enum RowType {
		LIST_ITEM, HEADER_ITEM
	}

	public WordPopupAdapter(Context context, List<WordPopupItem> items) {
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
