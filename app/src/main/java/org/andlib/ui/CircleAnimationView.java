package org.andlib.ui;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.animtuto.WillTutoActivity;

public class CircleAnimationView extends View implements View.OnClickListener, View.OnTouchListener {
	String TAG = "CircleAnimationView";

	public interface ClickCallbacks {

		void onClick();
	}

	String text = "Hello";
	Paint paint;
	RectF rect;
	Context context;
	
	int frames = 0;
	Paint textPaint;
	float fps = 0;
	
	boolean isBoldText = false;

	ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
	long startTime, prevTime;
	
	SpannableStringBuilder sb;

	public CircleAnimationView(Context context) {
		super(context);
		this.context = context;
		this.setOnClickListener(this);
		this.setOnTouchListener(this);

		Log.e(TAG, "1");
	}

	public CircleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		Log.e(TAG, "2");
	}

	public CircleAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		Log.e(TAG, "3");
		
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				long nowTime = System.currentTimeMillis();
				float secs = (float) (nowTime - prevTime) / 1000f;
				prevTime = nowTime;
				invalidate();
			}
		});
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setDuration(3000);

		init();
	}

	ImageView imageview;
	ClickCallbacks callbacks;

	private void init() {
		paint = new Paint();
		circlepaint = new Paint();

		imageview = new ImageView(context);
		imageview.setBackgroundResource(R.drawable.shape_circle);
		imageview.setVisibility(View.GONE);

		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(WillTutoActivity.spTopx(getContext(), 18));
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		circlepaint.setAntiAlias(true);
		circlepaint.setARGB(0xff, 0xA9, 0x43, 0x31);
		rect = new RectF();
	}

	boolean isAnimation = false;
	private Paint circlepaint;
	int res_Background = R.drawable.actionbar;
	Bitmap mBackgroundImage;

	int width, height;
	float startX, startY;
	private long now;
	private int i = 10;
	int dv = 1;
	int v = 0;
	int r = 0;
	int alpha = 255;
	float vAlpha;
	int MAXradius = 0;
	int limitX = 0;
	Integer[] radius = new Integer[4];
	private boolean isEndAnimation = true;

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		// draw background circle anyway
		canvas.drawColor(Color.argb(0, 255, 0, 255));

		mBackgroundImage = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);

		// layout.draw(canvas);
		//
		// layout.measure(canvas.getWidth(), canvas.getHeight());
		// layout.layout(0, 0, canvas.getWidth(), canvas.getHeight());

		// Bitmap background =
		// BitmapFactory.decodeResource(getResources(),res_Background); //Load
		// background.;
		Rect dest = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
		Paint bpaint = new Paint();
		bpaint.setFilterBitmap(true);

		canvas.drawBitmap(mBackgroundImage, null, dest, bpaint);

		int xPos = (int) ((canvas.getWidth() / 2));
		int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
		if (isBoldText) {
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(sb.toString(), xPos, yPos, paint);
		} else {
			canvas.drawText(text, xPos, yPos, paint);
		}
		if (isAnimation) {
			if (r < MAXradius) {
				// Log.d("OnDraw", "r " + r + ", MAXRadius " + MAXradius +
				// ", v " + v + ", dv " + dv + ", alpha " + alpha);
				// canvas.drawColor(Color.rgb(0xff, 0xff, 0xff));
				canvas.save(); // Save the position of the canvas matrix.

				if (alpha < 0) {
					alpha = 0;
					// canvas.drawBitmap(mBackgroundImage, null, dest, bpaint);

				}
				circlepaint.setAlpha(alpha);
				// canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				canvas.drawCircle(startX, startY, r, circlepaint);
				// if(alpha == 0){
				// stopThread();
				// }
				alpha -= (int) (vAlpha + 1);

				v = v + dv;
				r = r + v;
				if (r > MAXradius) {
					r = MAXradius;
				}
				canvas.restore(); // Rotate the canvas matrix back to its saved
									// position - only the ball bitmap
				now = System.currentTimeMillis();
			} else if (r == MAXradius) {
				isAnimation =!isAnimation;
				isEndAnimation = !isEndAnimation;

				canvas.save(); // Save the position of the canvas matrix.

				if (alpha < 0) {
					alpha = 0;
				}
				circlepaint.setAlpha(alpha);
				canvas.drawCircle(startX, startY, r, circlepaint);
				alpha -= (int) (vAlpha);

				now = System.currentTimeMillis();
				callbacks.onClick();

			}
		} else {
			width = canvas.getWidth();
			height = canvas.getHeight();
		}
        ++frames;
        long nowTime = System.currentTimeMillis();
        long deltaTime = nowTime - startTime;
        //if (deltaTime > 1000) {
            float secs = (float) deltaTime / 1000f;
            fps = (float) frames / secs;
            startTime = nowTime;
            frames = 0;
            Log.d("Animator", "fps : " + fps);
        //}

		Log.d("OnDraw", "r " + r + ", MAXRadius " + MAXradius + ", v " + v + ", dv " + dv + ", alpha " + alpha);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		isAnimation = !isAnimation;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		dv = 1;
		this.v = 0;
		r = 0;
		alpha = 255;
		vAlpha = 0;
		MAXradius = 0;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			i = 0;
			startX = event.getX();
			startY = event.getY();

			break;

		case MotionEvent.ACTION_MOVE:

			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			Log.e("startX", "" + startX + "      " + startY);
			Log.e("startX", "" + width + "      " + height + "      " + Math.sqrt(4));

			radius[0] = (int) Math.sqrt(Math.pow(startX, 2) + Math.pow(startY, 2));
			radius[1] = (int) Math.sqrt(Math.pow(startX, 2) + Math.pow(height - startY, 2));
			radius[2] = (int) Math.sqrt(Math.pow(width - startX, 2) + Math.pow(startY, 2));
			radius[3] = (int) Math.sqrt(Math.pow(width - startX, 2) + Math.pow(height - startY, 2));

			MAXradius = getMaxRadius();

			vAlpha = (float) (255 / ((-1 + Math.sqrt(1 + 8 * (float) MAXradius)) / 2));

			startAnimation();

			break;

		default:
			break;
		}
		return false;
	}

	private int getMaxRadius() {
		// TODO Auto-generated method stub
		Integer temp = null;
		for (int i = 0; i < radius.length - 1; i++) {
			if (radius[i + 1] < radius[i]) {
				radius[i + 1] = temp;
				radius[i + 1] = radius[i];
				radius[i] = temp;
			}
		}
		return radius[3];
	}

	public void setAnimButton(Paint textPaint, int resId, ClickCallbacks callback) {
		this.callbacks = callback;
		this.paint = textPaint;
		this.res_Background = resId;
	}

	public void pause() {
		// Make sure the animator's not spinning in the background when the
		// activity is paused.
		animator.cancel();
	}

	public void resume() {
		animator.start();
	}

	public void stopAnimation() {
		animator.cancel();
	}

	public void startAnimation() {
		animator.start();
	}

	public void setColor(int r, int g, int b) {
		circlepaint.setARGB(0xff, r, g, b);
	}
	
	public void setTextColor(int a, int r, int g, int b) {
		paint.setARGB(a, r, g, b);
	}
	
	public void setTextSize(int size){
		paint.setTextSize(WillTutoActivity.spTopx(getContext(), size));
	}
	
	public void setText(String text) {
		this.text = text;
		invalidate();
	}

	public void setBoldText(String text, boolean isBoldText){
		this.isBoldText = isBoldText;
		sb = new SpannableStringBuilder();
		sb.append(text);
		sb.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	
}
