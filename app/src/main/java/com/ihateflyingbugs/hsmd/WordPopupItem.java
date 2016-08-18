package com.ihateflyingbugs.hsmd;

import android.view.LayoutInflater;
import android.view.View;

public interface WordPopupItem {
	public int getViewType();

	public View getView(LayoutInflater inflater, View convert);

}
