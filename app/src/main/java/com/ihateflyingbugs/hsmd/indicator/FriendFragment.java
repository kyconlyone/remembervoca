package com.ihateflyingbugs.hsmd.indicator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.FriendCard;

import java.io.InputStream;
import java.net.URL;

public final class FriendFragment extends Fragment {
	private final String KEY_CONTENT = "FriendFragment:FriendCard";
	private FriendCard friendCard;

	Handler handler;

	TextView tv_friend_card_name;
	TextView tv_friend_card_school;
	TextView tv_friend_card_grade;
	TextView tv_friend_card_period;
	TextView tv_friend_card_goal;
	ImageView iv_friend_card_profile;

	private Bitmap bmp;

	public FriendFragment newInstance(FriendCard fCard) {
		FriendFragment fragment = new FriendFragment();
		fragment.friendCard = fCard;
		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.item_friendcard, container, false);

		handler = new Handler();

		tv_friend_card_name = (TextView)view.findViewById(R.id.tv_friend_card_name);
		tv_friend_card_school = (TextView)view.findViewById(R.id.tv_friend_card_school);
		tv_friend_card_grade = (TextView)view.findViewById(R.id.tv_friend_card_grade);
		tv_friend_card_period = (TextView)view.findViewById(R.id.tv_friend_card_period);
		tv_friend_card_goal = (TextView)view.findViewById(R.id.tv_friend_card_goal);
		iv_friend_card_profile = (ImageView)view.findViewById(R.id.iv_friend_card_profile);
		try {
			tv_friend_card_name.setText(friendCard.getName());
			tv_friend_card_school.setText(friendCard.getFriendSchool());
			tv_friend_card_grade.setText(friendCard.getYears());
			tv_friend_card_period.setText(friendCard.getDate() > 1 ? "밀당 " + (int)friendCard.getDate() + "일째" : "밀당 신규 유저");
			tv_friend_card_goal.setText("매일 "+friendCard.getGoaltime()+"분 암기를\n계획하셨습니다.");
		} catch (Exception e) {
			// TODO: handle exception
			return view;
		}


		if(friendCard.getDate()<=1){
			view.setBackgroundResource(R.drawable.bg_yourfriend_new);
		}else{
			view.setBackgroundResource(R.drawable.bg_yourfriend_default);
		}

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					InputStream in = new URL(friendCard.getProfileUrl()).openStream();

					bmp = BitmapFactory.decodeStream(in);
				} catch (Exception e) {
					// log error
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (bmp != null)
					iv_friend_card_profile.setImageBitmap(round(bmp));
			}

		}.execute();

		return view;
	}

	private Bitmap round(Bitmap source) {
		// TODO Auto-generated method stub
		Bitmap output = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2,
				source.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		return output;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
}
