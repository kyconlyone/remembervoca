package com.ihateflyingbugs.hsmd;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.HitListAdapter.RowType;
import com.ihateflyingbugs.hsmd.data.MainValue;

public class HitListItem implements HitItem {

	private String title;
	private String date;
	private Bitmap img;

	public HitListItem(String title, String date, Bitmap img) {
		this.title = title;
		this.date = date;
		this.img = img;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDate() {
		return this.date;
	}

	public Bitmap getImg() {
		return this.img;
	}

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		// TODO Auto-generated method stub
		View view;
		if (convertView == null) {
			if(MainValue.isLowDensity(VOCAconfig.context)){
				view = (View) inflater.inflate(R.layout.adapter_hit_small, null);
			}else{
				view = (View) inflater.inflate(R.layout.adapter_hit, null);
			}
			// Do some initialization
		} else {
			view = convertView;
		}

		TextView title = (TextView) view.findViewById(R.id.tv_title);
		TextView word = (TextView) view.findViewById(R.id.tv_date);
		ImageView img = (ImageView) view.findViewById(R.id.iv_img);
		// title.setText(this.title);
		// word.setText(this.word);
		// date.setText(this.date);

		return view;
	}
}
