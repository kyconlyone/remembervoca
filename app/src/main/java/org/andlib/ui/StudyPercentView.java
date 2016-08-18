package org.andlib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ihateflyingbugs.hsmd.R;

public class StudyPercentView extends View {
	String TAG = "KARAM";

	private int STROKE_WIDTH = 60;
	private int STROKE_WIDTH_ = 50;

	public StudyPercentView (Context context) {
		super(context);
		Log.e(TAG, "StudyPercentView1");
		init();
	}
	public StudyPercentView (Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e(TAG, "StudyPercentView2");
		init();
	}
	public StudyPercentView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.e(TAG, "StudyPercentView3");
		init();
	}
	private void init() {
		STROKE_WIDTH = getResources().getDimensionPixelSize(R.dimen.big_demen);
		STROKE_WIDTH_= getResources().getDimensionPixelSize(R.dimen.small_demen);

		paint = new Paint();
		paint.setColor(Color.argb(40, 0, 0, 0));
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
		paint.setStrokeCap(Paint.Cap.BUTT);

		bgpaint = new Paint();
		bgpaint.setColor(Color.parseColor("#e1aa25"));
		bgpaint.setAntiAlias(true);
		bgpaint.setStyle(Paint.Style.STROKE);
		bgpaint.setStrokeWidth(STROKE_WIDTH_);

		c_bgpaint = new Paint();
		c_bgpaint.setColor(Color.rgb(0, 181, 105));
		c_bgpaint.setAntiAlias(true);
		c_bgpaint.setStyle(Paint.Style.STROKE);
		c_bgpaint.setStrokeWidth(STROKE_WIDTH_);

		c_paint = new Paint();
		c_paint.setColor(Color.rgb(0, 181, 105));
		c_paint.setAntiAlias(true);
		c_paint.setStyle(Paint.Style.STROKE);
		c_paint.setStrokeWidth(STROKE_WIDTH);
		c_paint.setStrokeCap(Paint.Cap.BUTT);
		RightRect = new RectF();
		LeftRect = new RectF();
		setPercentage(0, false);
	}
	Paint paint;
	Paint bgpaint;
	Paint c_paint;
	Paint c_bgpaint;
	RectF RightRect;
	RectF LeftRect;
	float percentage = 0;
	boolean current_contents ;
	Thread thread;
	boolean f_color;
	boolean f_excolor = false;
	int big =STROKE_WIDTH;
	int i=2;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//draw background circle anyway
		int left = 0;
		int width = getWidth();
		int top = 0;
		i+=2;


		if(f_color){
			if(f_excolor!=f_color){
				bgpaint.setStrokeWidth(STROKE_WIDTH+i);
				paint.setStrokeWidth(STROKE_WIDTH+30-i);

			}else{
				bgpaint.setStrokeWidth(STROKE_WIDTH+30);
				paint.setStrokeWidth(STROKE_WIDTH);
			}
		}else{
			if(f_excolor!=f_color){
				paint.setStrokeWidth(STROKE_WIDTH+i);
				bgpaint.setStrokeWidth(STROKE_WIDTH+30-i);
			}else{
				paint.setStrokeWidth(STROKE_WIDTH+30);
				bgpaint.setStrokeWidth(STROKE_WIDTH);
			}

		}

		if(f_color){
			RightRect.set(left + STROKE_WIDTH+15, top + STROKE_WIDTH+15, left+width - STROKE_WIDTH-15, top + width -STROKE_WIDTH-15); 
			LeftRect.set(left + STROKE_WIDTH, top + STROKE_WIDTH, left+width - STROKE_WIDTH, top + width -STROKE_WIDTH); 
		}else{
			LeftRect.set(left + STROKE_WIDTH+15, top + STROKE_WIDTH+15, left+width - STROKE_WIDTH-15, top + width -STROKE_WIDTH-15); 
			RightRect.set(left + STROKE_WIDTH, top + STROKE_WIDTH, left+width - STROKE_WIDTH, top + width -STROKE_WIDTH); 
		}



		Log.e("RightRect", ""+(left + STROKE_WIDTH)+"    "+ (top + STROKE_WIDTH)+"    "+(left+width - STROKE_WIDTH)+"    "+(top + width -STROKE_WIDTH));

		if(percentage >= 0) 
		{
			//			RightRect.set(left, top, left+width, top + width); 
			if(current_contents){
				canvas.drawArc(LeftRect, 90 + (360*percentage), 360 - (360*percentage), false, bgpaint);
				canvas.drawArc(RightRect, 90, (360*percentage), false, paint);
			}else{
				canvas.drawArc(LeftRect, 90, (360*percentage), false, bgpaint);
				canvas.drawArc(RightRect, 90 + (360*percentage), 360 - (360*percentage), false, paint);
			}
			//			canvas.drawArc(RightRect, (360*percentage), (360*(100 - percentage)), true, bgpaint);
		}		
		Log.e("flag_test", "ex "+f_excolor+"  current  "+f_color );

		if(f_excolor!=f_color){
			if(i<30){
				invalidate();
			}else{
				i=2;
				f_excolor = f_color;
			}
		}
	}

	public void setPercentage(float percentage, boolean current_contents) {
		this.current_contents = current_contents;
		this.percentage = percentage / 100;
		invalidate();
	}

	public void setPercentage(float percentage, boolean current_contents, boolean f_color) {
		this.current_contents = current_contents;
		this.percentage = percentage / 100;
		this.f_color = f_color;
		invalidate();
	}

}