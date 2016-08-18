package org.andlib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;

public class PercentView extends View {

	private int STROKE_WIDTH = 8;
	private int STROKE_WIDTH_ = 8;

	private int state = 0;

	public PercentView(Context context) {
		super(context);
		init();
	}

	public PercentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PercentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

		bg_paint = new Paint();
		bg_paint.setColor(Color.rgb(252, 252, 252));
		bg_paint.setAntiAlias(true);
		bg_paint.setStyle(Paint.Style.FILL_AND_STROKE);
		bg_paint.setStrokeWidth(STROKE_WIDTH);
		bg_paint.setStrokeCap(Paint.Cap.BUTT);

		paint_0_lv = new Paint();
		paint_0_lv.setColor(Color.rgb(184, 184, 184));
		paint_0_lv.setAntiAlias(true);
		paint_0_lv.setStyle(Paint.Style.STROKE);
		paint_0_lv.setStrokeWidth(STROKE_WIDTH);
		paint_0_lv.setStrokeCap(Paint.Cap.BUTT);

		paint_1_lv = new Paint();
		paint_1_lv.setColor(Color.rgb(124, 170, 150));
		paint_1_lv.setAntiAlias(true);
		paint_1_lv.setStyle(Paint.Style.STROKE);
		paint_1_lv.setStrokeWidth(STROKE_WIDTH);
		paint_1_lv.setStrokeCap(Paint.Cap.BUTT);

		paint_2_lv = new Paint();
		paint_2_lv.setColor(Color.rgb(26, 145, 84));
		paint_2_lv.setAntiAlias(true);
		paint_2_lv.setStyle(Paint.Style.STROKE);
		paint_2_lv.setStrokeWidth(STROKE_WIDTH);
		paint_2_lv.setStrokeCap(Paint.Cap.BUTT);

		paint_3_lv = new Paint();
		paint_3_lv.setColor(Color.rgb(62, 120, 92));
		paint_3_lv.setAntiAlias(true);
		paint_3_lv.setStyle(Paint.Style.STROKE);
		paint_3_lv.setStrokeWidth(STROKE_WIDTH);
		paint_3_lv.setStrokeCap(Paint.Cap.BUTT);

		paint_4_lv = new Paint();
		paint_4_lv.setColor(Color.rgb(221, 103, 21));
		paint_4_lv.setAntiAlias(true);
		paint_4_lv.setStyle(Paint.Style.STROKE);
		paint_4_lv.setStrokeWidth(STROKE_WIDTH);
		paint_4_lv.setStrokeCap(Paint.Cap.BUTT);

		paint_5_lv = new Paint();
		paint_5_lv.setColor(Color.rgb(236, 225, 40));
		paint_5_lv.setAntiAlias(true);
		paint_5_lv.setStyle(Paint.Style.STROKE);
		paint_5_lv.setStrokeWidth(STROKE_WIDTH);
		paint_5_lv.setStrokeCap(Paint.Cap.BUTT);

		paint_6_lv = new Paint();
		paint_6_lv.setColor(Color.rgb(97, 87, 146));
		paint_6_lv.setAntiAlias(true);
		paint_6_lv.setStyle(Paint.Style.STROKE);
		paint_6_lv.setStrokeWidth(STROKE_WIDTH);
		paint_6_lv.setStrokeCap(Paint.Cap.BUTT);

		rect = new RectF();
		setPercentage(0, 1, true);
	}

	Paint paint_0_lv;
	Paint paint_1_lv;
	Paint paint_2_lv;
	Paint paint_3_lv;
	Paint paint_4_lv;
	Paint paint_5_lv;
	Paint paint_6_lv;

	Paint bg_paint;
	RectF rect;
	float percentage = 0;
	boolean current_lv;
	int percentage_lv;
	boolean background = true;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw background circle anyway
		int left = 0;
		int width = getWidth();
		int top = 0;

		rect.set(left + STROKE_WIDTH / 2, top + STROKE_WIDTH / 2, left + width
				- STROKE_WIDTH / 2, top + width - STROKE_WIDTH / 2);

		if (background == true) {

			canvas.drawArc(rect, 0, 360, false, bg_paint);
		}

		// RightRect.set(left, top, left+width, top + width);
		if (percentage_lv == 0) {

			Log.d("goal_time", "lv_0");

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_0_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_1_lv);

		} else if (percentage_lv == 1) {

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_1_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_2_lv);

		} else if (percentage_lv == 2) {

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_2_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_3_lv);

		} else if (percentage_lv == 3) {

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_3_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_4_lv);

		} else if (percentage_lv == 4) {

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_4_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_5_lv);

		} else if (percentage_lv == 5) {

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_5_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_6_lv);

		} else if (percentage_lv >= 6) {

			canvas.drawArc(rect, -90 + (3600 * percentage),
					360 - (360 * percentage), false, paint_6_lv);
			canvas.drawArc(rect, -90, (3600 * percentage), false, paint_6_lv);

		}
	}

	public void setPercentage(float percentage, int percentage_lv,
			boolean background) {
		this.percentage_lv = percentage_lv;
		this.percentage = percentage / 1000;
		this.background = background;
		invalidate();
	}
}