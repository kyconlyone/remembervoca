/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ihateflyingbugs.hsmd;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ListActivity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * A {@link View.OnTouchListener} that makes any {@link View} dismissable when
 * the user swipes (drags her finger) horizontally across the view.
 *
 * <p>
 * <em>For {@link ListView} list items that don't manage their own touch events
 * (i.e. you're using
 * {@link ListView#setOnItemClickListener(AdapterView.OnItemClickListener)}
 * or an equivalent listener on {@link ListActivity} or
 * {@link ListFragment}, use {@link SwipeDismissListViewTouchListener} instead.</em>
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * view.setOnTouchListener(new SwipeDismissTouchListener(view, null, // Optional
 * 																	// token/cookie
 * 																	// object
 * 		new SwipeDismissTouchListener.OnDismissCallback() {
 * 			public void onDismiss(View view, Object token) {
 * 				parent.removeView(view);
 * 			}
 * 		}));
 * </pre>
 *
 * <p>
 * This class Requires API level 12 or later due to use of
 * {@link android.view.ViewPropertyAnimator}.
 * </p>
 *
 * @see SwipeDismissListViewTouchListener
 */
public class SwipeDismissTouchListener implements View.OnTouchListener {
	// Cached ViewConfiguration and system-wide constant values
	private int mSlop;
	private int mMinFlingVelocity;
	private int mMaxFlingVelocity;
	private long mAnimationTime;

	private long animationTime = 200;

	private int dismissAnimationRefCount = 0;

	// Fixed properties
	private View mView;
	private View mView1;
	private View mView2;

	private ImageView iv_wc;

	private DismissCallbacks mCallbacks;
	private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

	// Transient properties
	private float mDownX;
	private float mDownY;
	private boolean mSwiping;
	private Object mToken;
	private VelocityTracker mVelocityTracker;
	private float mTranslationX;
	protected boolean flag_state;
	protected boolean flag_swipe_mode;

	/**
	 * The callback interface used by {@link SwipeDismissTouchListener} to
	 * inform its client about a successful dismissal of the view for which it
	 * was created.
	 */
	public interface DismissCallbacks {
		/**
		 * Called to determine whether the view can be dismissed.
		 */


		boolean canDismiss(View view, Object token);

		/**
		 * Called when the user has indicated they she would like to dismiss the
		 * view.
		 *
		 * @param view
		 *            The originating {@link View} to be dismissed.
		 * @param token
		 *            The optional token passed to this object's constructor.
		 */

		void onLeftDismiss(View view, Object token, boolean flag);

		void onRightDismiss(View view, Object token, boolean flag);

		void onLeftMovement(View view);

		void onRightMovement();

		void showFlipAnimation(boolean Direction, float deltaX);
	}

	/**
	 * Constructs a new swipe-to-dismiss touch listener for the given view.
	 *
	 * @param forward
	 *            The view to make dismissable.
	 * @param token
	 *            An optional token/cookie object to be passed through to the
	 *            callback.
	 * @param callbacks
	 *            The callback to trigger when the user has indicated that she
	 *            would like to dismiss this view.
	 */

	public SwipeDismissTouchListener(DismissCallbacks callbacks) {
		mCallbacks = callbacks;
	}

	public void setSwipeTouchListener(View forward, View unknown, View known,
									  ImageView iv_wc, boolean flag_swipe_mode, Object token) {

		ViewConfiguration vc = ViewConfiguration.get(forward.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		// 속도 조절
		mAnimationTime = 100;
		mView = forward;
		mView1 = unknown;
		mView2 = known;
		this.iv_wc = iv_wc;
		mToken = token;
		this.flag_swipe_mode = flag_swipe_mode;
	}


	public SwipeDismissTouchListener(View forward, View unknown, View known,
									 ImageView iv_wc, boolean flag_swipe_mode, Object token,
									 DismissCallbacks callbacks) {

		ViewConfiguration vc = ViewConfiguration.get(forward.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		// 속도 조절
		mAnimationTime = 100;
		mView = forward;
		mView1 = unknown;
		mView2 = known;
		this.iv_wc = iv_wc;
		mToken = token;
		mCallbacks = callbacks;
		this.flag_swipe_mode = flag_swipe_mode;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		// offset because the view is translated during swipe

		motionEvent.offsetLocation(mTranslationX, 0);

		if (mViewWidth < 2) {
			mViewWidth = mView.getWidth();
		}

		switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				// TODO: ensure this is a finger, and set a flag
				mDownX = motionEvent.getRawX();
				mDownY = motionEvent.getRawY();
				// iv_wc.setVisibility(View.VISIBLE);

				if (mCallbacks.canDismiss(mView, mToken)) {
					mVelocityTracker = VelocityTracker.obtain();
					mVelocityTracker.addMovement(motionEvent);
				}

				return false;
			}

			case MotionEvent.ACTION_UP: {

				if (mVelocityTracker == null) {
					break;
				}

				float deltaX = motionEvent.getRawX() - mDownX;
				float deltaY = motionEvent.getRawX() - mDownY;

				mVelocityTracker.addMovement(motionEvent);
				mVelocityTracker.computeCurrentVelocity(1000);
				float velocityX = mVelocityTracker.getXVelocity();
				float absVelocityX = Math.abs(velocityX);
				float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
				boolean dismiss = false;
				boolean dismissRight = false;
				if (Math.abs(deltaX) > mViewWidth / 2) {
					dismiss = true;

					dismissRight = deltaX > 0;
					if (deltaX > 0) {
						if (flag_swipe_mode) {
							dismiss = false;
						}
						flag_state = true;
						mView1.setVisibility(View.VISIBLE);
						mView2.setVisibility(View.GONE);
					} else {
						flag_state = false;
						mView1.setVisibility(View.GONE);
						mView2.setVisibility(View.VISIBLE);
						if (!flag_swipe_mode) {
							return true;
						}
					}
				} else if (mMinFlingVelocity <= absVelocityX
						&& absVelocityX <= mMaxFlingVelocity
						&& absVelocityY < absVelocityX) {
					// dismiss only if flinging in the same direction as dragging
					if (deltaX > 0) {
						flag_state = true;
						mView1.setVisibility(View.VISIBLE);
						mView2.setVisibility(View.GONE);
					} else {
						if (flag_swipe_mode) {
							dismiss = false;
						}
						flag_state = false;
						mView1.setVisibility(View.GONE);
						mView2.setVisibility(View.VISIBLE);
						if (!flag_swipe_mode) {
							return true;
						}

					}

					dismiss = (velocityX < 0) == (deltaX < 0);
					if (flag_swipe_mode && flag_state) {
						dismiss = false;
					}
					dismissRight = mVelocityTracker.getXVelocity() > 0;
				}

				if (dismiss) {
					// dismiss
					mView.animate()
							.translationX(dismissRight ? mViewWidth : -mViewWidth)
							.alpha(0).setDuration(mAnimationTime)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {

									// mView1.setBackgroundColor(Color.rgb(0, 181,
									// 105));
									performDismiss();
								}
							});
				} else {
					if (!dismiss && deltaX > 0 && Math.abs(deltaX) > mSlop) {
						mCallbacks.onLeftMovement(mView);
					} else {
						mCallbacks.onRightMovement();
					}
					// cancel
					mView.animate().translationX(0).alpha(1)
							.setDuration(mAnimationTime).setListener(null);
				}

				mVelocityTracker.recycle();
				mVelocityTracker = null;
				mTranslationX = 0;
				mDownX = 0;
				mDownY = 0;
				mSwiping = false;
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				if (mVelocityTracker == null) {
					break;
				}

				mVelocityTracker.addMovement(motionEvent);
				float deltaX = motionEvent.getRawX() - mDownX;
				float deltaY = motionEvent.getRawY() - mDownY;

				if ((deltaX > -20 && deltaX < 20) && (deltaY > -20 && deltaY < 20)
						&& (Math.abs(deltaY) < mSlop)) {
					// iv_wc.setVisibility(View.VISIBLE);
				} else {
					// iv_wc.setVisibility(View.INVISIBLE);
				}

				if (deltaX > 0) {
					flag_state = true;
					mView1.setVisibility(View.VISIBLE);
					mView2.setVisibility(View.GONE);
					mCallbacks.showFlipAnimation(true, deltaX);
				} else {
					// Known
					flag_state = false;
					mView1.setVisibility(View.GONE);
					mView2.setVisibility(View.VISIBLE);

					Log.e("deltax", "deltax : "+ deltaX + " width : "+mViewWidth);
					mCallbacks.showFlipAnimation(false, deltaX);

					if (!flag_swipe_mode) {
						return true;
					}
				}

				if (Math.abs(deltaX) > mViewWidth / 2) {
					mView1.setBackgroundColor(Color.rgb(234, 78, 55));
				} else {
					mView1.setBackgroundColor(Color.rgb(234, 78, 55));
				}

				if (Math.abs(deltaX) > mSlop) {
					mSwiping = true;
					mView.getParent().requestDisallowInterceptTouchEvent(true);

					// Cancel listview's touch
					MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
					cancelEvent
							.setAction(MotionEvent.ACTION_CANCEL
									| (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					mView.onTouchEvent(cancelEvent);
					cancelEvent.recycle();
				}

				if (mSwiping) {

					mTranslationX = deltaX;

					// 13.12.24 아는단어모드에서 안다고 하는 제스쳐 중단.
					// if((WordListFragment.mode ==
					// WordListFragment.MODE_EXAM_KNOWN_LIST ||
					// WordListFragment.mode ==
					// WordListFragment.MODE_NORMAL_KNOWN_LIST) && !flag_state)
					// {
					// mVelocityTracker = null;
					// return true;
					// }
					// else
					// mView.setTranslationX(deltaX);

					mView.setTranslationX(deltaX);

					// TODO: use an ease-out interpolator or such
					// mView.setAlpha(Math.max(0f, Math.min(1f,
					// 1f - 2f * Math.abs(deltaX) / mViewWidth)));
					return true;
				}
				break;
			}
		}
		return false;
	}

	private void performDismiss() {
		// Animate the dismissed view to zero-height and then fire the dismiss
		// callback.
		// This triggers layout on each animation frame; in the future we may
		// want to do something
		// smarter and more performant.

		final ViewGroup.LayoutParams lp = mView.getLayoutParams();
		final int originalHeight = mView.getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1)
				.setDuration(mAnimationTime);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (flag_state) {
					if (!flag_swipe_mode) {
						mCallbacks.onLeftDismiss(mView, mToken, flag_state);
					}
				} else {
					mCallbacks.showFlipAnimation(false, (float)(mViewWidth*0.25));
					mCallbacks.onRightDismiss(mView, mToken, flag_state);
				}

				// Reset view presentation
				mView.setAlpha(1f);
				mView.setTranslationX(0);
				lp.height = originalHeight;
				mView.setLayoutParams(lp);
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				mView.setLayoutParams(lp);
			}
		});

		animator.start();
	}

	/**
	 * Class that saves pending dismiss data
	 */
	class PendingDismissData implements Comparable<PendingDismissData> {
		public int position;
		public View view;

		public PendingDismissData(int position, View view) {
			this.position = position;
			this.view = view;
		}

		@Override
		public int compareTo(PendingDismissData other) {
			// Sort by descending position
			return other.position - position;
		}
	}

	private List<PendingDismissData> pendingDismisses = new ArrayList<PendingDismissData>();

	/**
	 * Perform dismiss action
	 *
	 * @param dismissView
	 *            View
	 * @param dismissPosition
	 *            Position of list
	 */
	protected void performDismiss(final View dismissView,
								  final int dismissPosition, boolean doPendingDismiss) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		final int originalHeight = dismissView.getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1)
				.setDuration(animationTime);

		if (doPendingDismiss) {
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					--dismissAnimationRefCount;
					if (dismissAnimationRefCount == 0) {
						removePendingDismisses(originalHeight);
					}
				}
			});
		}

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

		pendingDismisses.add(new PendingDismissData(dismissPosition,
				dismissView));
		animator.start();
	}

	protected void resetPendingDismisses() {
		pendingDismisses.clear();
	}

	protected void handlerPendingDismisses(final int originalHeight) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				removePendingDismisses(originalHeight);
			}
		}, animationTime + 100);
	}

	private void removePendingDismisses(int originalHeight) {
		// No active animations, process all pending dismisses.
		// Sort by descending position
		Collections.sort(pendingDismisses);

		int[] dismissPositions = new int[pendingDismisses.size()];
		for (int i = pendingDismisses.size() - 1; i >= 0; i--) {
			dismissPositions[i] = pendingDismisses.get(i).position;
		}

		ViewGroup.LayoutParams lp;
		for (PendingDismissData pendingDismiss : pendingDismisses) {
			// Reset view presentation
			if (pendingDismiss.view != null) {
				setAlpha(pendingDismiss.view, 1f);
				setTranslationX(pendingDismiss.view, 0);
				lp = pendingDismiss.view.getLayoutParams();
				lp.height = originalHeight;
				pendingDismiss.view.setLayoutParams(lp);
			}
		}

		resetPendingDismisses();

	}


}
