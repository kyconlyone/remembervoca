package com.ihateflyingbugs.hsmd;

import android.graphics.Paint.FontMetricsInt;
import android.text.style.LineHeightSpan;

public class LineSpacing implements LineHeightSpan {

	private final int height;

	public LineSpacing(int height) {
		// TODO Auto-generated constructor stub
		this.height = height;
	}

	@Override
	public void chooseHeight(CharSequence text, int start, int end,
							 int spanstartv, int v, FontMetricsInt fm) {
		// TODO Auto-generated method stub
		fm.bottom += height;
		fm.descent += height;
	}

}
