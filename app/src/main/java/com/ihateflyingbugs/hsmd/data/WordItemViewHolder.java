package com.ihateflyingbugs.hsmd.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;

public class WordItemViewHolder {

	public boolean needInflate;
	public LinearLayout linearForward;
	public LinearLayout linearKnown;
	public LinearLayout linearUnknown;

	public LinearLayout ll_known_first_mean;
	public LinearLayout ll_known_second_mean;
	public LinearLayout ll_known_third_mean;
	public LinearLayout ll_known_forth_mean;

	public LinearLayout ll_first_mean;
	public LinearLayout ll_second_mean;
	public LinearLayout ll_third_mean;
	public LinearLayout ll_forth_mean;

	public TextView tvForward;
	public TextView tvKnownWord;
	public TextView tvUnknownWord;

	//�ܾ� �� ���°�
	public TextView tv_known_first_mean_title;
	public TextView tv_known_second_mean_title;
	public TextView tv_known_third_mean_title;
	public TextView tv_known_forth_mean_title;

	public TextView tv_known_first_mean;
	public TextView tv_known_second_mean;
	public TextView tv_known_third_mean;
	public TextView tv_known_forth_mean;

	public TextView tv_first_mean_title;
	public TextView tv_second_mean_title;
	public TextView tv_third_mean_title;
	public TextView tv_forth_mean_title;

	public TextView tv_first_mean;
	public TextView tv_second_mean;
	public TextView tv_third_mean;
	public TextView tv_forth_mean;

	public ImageView tvUnknownCount;
	public ImageView tvUnknownCount1;

	public ImageView ivKnown;
	public ImageView iv_wc;
	
	public ImageView iv_back_del;
	public FlipImage iv_flip_image;
	
	public View mLineView;
	
	
}
