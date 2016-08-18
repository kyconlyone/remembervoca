package com.ihateflyingbugs.hsmd.flipimageview;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.service.Utils;

public class FlipImage extends View {


	int state = 1;
	Handler handler;

	public FlipImage(Context context) {
		super(context);
		forward_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_forget);
		nextstate_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_10_hidden);
		forward_with = Utils.dpToPixels(40, getResources());
		handler = new Handler();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public FlipImage(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		forward_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_forget);
		nextstate_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_10_hidden);
		forward_with = Utils.dpToPixels(40, getResources());
		handler = new Handler();

		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FlipImage(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		forward_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_forget);
		nextstate_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_10_hidden);
		forward_with = Utils.dpToPixels(40, getResources());
		handler = new Handler();

		// TODO Auto-generated constructor stub
	}


	Bitmap forward_bitmap;
	Bitmap dontknow_bitmap;
	Bitmap nextstate_bitmap;


	int forward_with = Utils.dpToPixels(40, getResources());
	int dontknow_with = 1;
	int nextstate_with = 1;
	int max_width = Utils.dpToPixels(40, getResources());

	public void onDraw(Canvas canvas) {
		canvas.drawColor(color.transparent);
		// Paint 생성 & 속성 지정

		Paint pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
		//비트맵 생성하기 ( 이곳에서 canvas의 크기를 설정 가능함 현재 700*700으로 지정)
		Bitmap draw_bitmap = Bitmap.createBitmap(80,80,Bitmap.Config.ARGB_8888);
		//static Bitmap createBitmap(int width, int height, Bitmap.Config config)
		int a=0; //-임의에 위치 만큼 옴기기 (아래부분은 이미지 불러오는 소스)

		//이부분 중요 !

		if(nextstate_with!=1){
			canvas.drawBitmap(nextstate_bitmap, (max_width-nextstate_bitmap.getWidth())/2, 0,pnt);
		}else if(forward_with!=1){
			if(state!=0){
				canvas.drawBitmap(forward_bitmap, (max_width-forward_bitmap.getWidth())/2, 0,pnt);
			}
		}

		pnt.setColorFilter(new LightingColorFilter(0xbbffffff,0x00eeaa90)); //-색상
		//		nextstate_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_state_5_hidden);
		//		canvas.drawBitmap(nextstate_bitmap, 0, 0,pnt);
		Log.i("Main","W = "+ canvas.getWidth()+" ::H = "+canvas.getHeight());
		pnt.setColorFilter(null);
		//- canvas의 전체적인 크기를 변경 안에 적용된 모든 것이 변경됨
		canvas.scale((float)0.5, (float)0.5);
		//-drawBitmap의 변경된 이미지들을 기존에 있던 canvas에 셋팅해주는 부분 (0,0)d위치에 지정
		canvas.drawBitmap(draw_bitmap, 0, 0,pnt);

	}

	public void flip_ani(float percent){
		if(Math.round((max_width*percent))>=0){

			//Log.e("flip_percent", ""+Math.round((max_width*percent)));

			if(Math.round((max_width*percent))>=max_width){
				nextstate_with = Math.round((max_width*(float)(percent-(float)0.99)))+1;
				forward_with = 1;
				Log.d("flip_percent", ""+Math.round((max_width*percent)));
			}else{
				forward_with = Math.round((max_width*(float)(1-percent)))+1;
				nextstate_with = 1;
				Log.v("flip_percent", ""+Math.round((max_width*percent)));
			}
		}else{
			Log.w("flip_percent", "else");
		}

		if(nextstate_with==0){
			nextstate_with=1;
		}else if(forward_with==0){
			forward_with=1;
		}

		if(isLog){
			//	Log.e("flip_log", ""+percent);
		}


		forward_bitmap = BitmapFactory.decodeResource(getResources(),forward_image);
		forward_bitmap = Bitmap.createScaledBitmap(forward_bitmap, forward_with,
				Utils.dpToPixels(40, getResources()), true);

		nextstate_bitmap = BitmapFactory.decodeResource(getResources(), second_image);
		nextstate_bitmap = Bitmap.createScaledBitmap(nextstate_bitmap, nextstate_with,
				Utils.dpToPixels(40, getResources()), true);


		//forward_bitmap = getResizedBitmap(forward_bitmap, forward_with, 0);
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				invalidate();
			}
		});
	}

	long time_start=0;
	long time_end=0;
	long time_passed=0;
	long timeDelta=0;
	static Thread thread;

	boolean isLog = false;

	public void flip(final float delta){
		time_end = System.currentTimeMillis()+100;
		isLog = true;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					time_start = System.currentTimeMillis();
					timeDelta = time_start-time_passed ;

					if ( timeDelta < 8) {
						try {
							//	Log.e("flip_thread", "sleep : "+timeDelta + "  "+time_start + "  "+time_passed + "  "+ (time_end-time_passed));
							Thread.sleep(8 - timeDelta);
						}
						catch(InterruptedException e) {
						}
					}

					time_passed = System.currentTimeMillis();

					if(time_end-time_passed>0){
						// TODO Auto-generated method stub

						if(((float)((time_end-time_passed)*0.01)*delta)<2){

							//	Log.e("flip_thread", "gap : "+((float)((time_end-time_passed)*0.01)*delta));
							//	Log.e("flip_thread", "delta : "+delta);
							flip_ani(((float)((time_end-time_passed)*0.01)*delta)+(float)0.1);
						}else{
							Log.v("flip_thread", "big better 2");
							flip_ani(2);
						}
					}else{
						//Log.e("flip_thread", ""+(time_end-time_passed));
						time_start=0;
						time_end=0;
						time_passed=0;
						timeDelta=0;
						flip_ani(0);
						thread.interrupt();
						isLog = false;
						break;

					}

				}

			}
		});

		thread.start();

	}

	int forward_image;
	int second_image;

	public void setImage(int state){
		switch (state) {
			case 1:
				forward_image = R.drawable.img_state_1;
				second_image = R.drawable.img_state_2_hidden;
				break;
			case 2:
				forward_image = R.drawable.img_state_2;
				second_image = R.drawable.img_state_3_hidden;
				break;
			case 3:
				forward_image = R.drawable.img_state_3;
				second_image = R.drawable.img_state_4_hidden;
				break;
			case 4:
				forward_image = R.drawable.img_state_4;
				second_image = R.drawable.img_state_5_hidden;
				break;
			case 5:
				forward_image = R.drawable.img_state_5;
				second_image = R.drawable.img_state_6_hidden;
				break;
			case 6:
				forward_image = R.drawable.img_state_6;
				second_image = R.drawable.img_state_7_hidden;
				break;
			case 7:
				forward_image = R.drawable.img_state_7;
				second_image = R.drawable.img_state_8_hidden;
				break;
			case 8:
				forward_image = R.drawable.img_state_8;
				second_image = R.drawable.img_state_9_hidden;
				break;
			case 9:
				forward_image = R.drawable.img_state_9;
				second_image = R.drawable.img_state_10_hidden;
				break;
			case 0:
				forward_image = R.drawable.img_state_0;
				second_image = R.drawable.img_state_know;
				break;
			case -1:
				forward_image = R.drawable.img_state_forget;
				second_image = R.drawable.img_state_1_hidden;
				break;
			default:
				forward_image = R.drawable.img_state_10;
				second_image = R.drawable.img_state_10_hidden;
				break;
		}

		forward_bitmap = BitmapFactory.decodeResource(getResources(),forward_image);
		forward_bitmap = Bitmap.createScaledBitmap(forward_bitmap, forward_with,
				Utils.dpToPixels(40, getResources()), true);

		nextstate_bitmap = BitmapFactory.decodeResource(getResources(), second_image);
		nextstate_bitmap = Bitmap.createScaledBitmap(nextstate_bitmap, nextstate_with,
				Utils.dpToPixels(40, getResources()), true);

		invalidate();
	}

}
