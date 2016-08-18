package com.ihateflyingbugs.hsmd;

import java.util.ArrayList;
import java.util.List;

import com.ihateflyingbugs.hsmd.BookAdapter.RowType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HitListAdapter extends ArrayAdapter<HitItem> {
	private LayoutInflater inflater;

	public enum RowType {
		LIST_ITEM, HEADER_ITEM
	}

	private ArrayList<HitListItem> itemList;

	public HitListAdapter(Context context, List<HitItem> items) {
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
