package com.jinxin.jxtouchscreen.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.jinxin.jxtouchscreen.R;

public class SyncHorizontalScrollView extends HorizontalScrollView {

	private View view;
	private ImageView leftImage;
	private ImageView rightImage;
	private int windowWitdh = 2236;
	private Activity mContext;

	public void setSomeParam(View view, ImageView leftImage,
			ImageView rightImage, Activity context) {
		this.mContext = context;
		this.view = view;
		this.leftImage = leftImage;
		this.rightImage = rightImage;
		leftImage.setImageResource(R.drawable.left_n);
		rightImage.setImageResource(R.drawable.right_d);
//		leftImage.setClickable(false);
//		rightImage.setClickable(true);
	}

	public SyncHorizontalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
//		if (!mContext.isFinishing() && view != null && rightImage != null
//				&& leftImage != null) {
//			Log.e("Tag","l============" + l);
//			if (view.getWidth() <= windowWitdh) {
//				leftImage.setImageResource(R.drawable.left_n);
//				rightImage.setImageResource(R.drawable.right_n);
//				leftImage.setClickable(false);
//				rightImage.setClickable(false);
//			} else {
//				if (l == 0) {
//					leftImage.setImageResource(R.drawable.left_n);
//					rightImage.setImageResource(R.drawable.right_d);
//					leftImage.setClickable(false);
//					rightImage.setClickable(true);
//				} else if (l == windowWitdh) {
//					leftImage.setImageResource(R.drawable.left_d);
//					rightImage.setImageResource(R.drawable.right_n);
//					leftImage.setClickable(true);
//					rightImage.setClickable(false);
//				} else {
//					leftImage.setImageResource(R.drawable.left_d);
//					rightImage.setImageResource(R.drawable.right_d);
//					leftImage.setClickable(true);
//					rightImage.setClickable(true);
//				}
//			}
//		}
	}
}
