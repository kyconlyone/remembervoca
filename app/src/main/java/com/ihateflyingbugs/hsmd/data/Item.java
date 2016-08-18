package com.ihateflyingbugs.hsmd.data;

import android.view.LayoutInflater;
import android.view.View;

public interface Item {
	public int getViewType();

	/**
	 * 
	 * @param inflater
	 * @param convertView
	 * @return
	 */
	public View getView(LayoutInflater inflater, View convertView);
}