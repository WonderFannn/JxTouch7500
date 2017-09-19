package com.jinxin.jxtouchscreen.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;


public class ManaMySeekBar extends SeekBar {
	private PopupWindow mPopupWindow;

	private LayoutInflater mInflater;
	private View mView;
	private int[] mPosition;

	private Drawable mThumb;

	private TextView mTvProgress;
	public ManaMySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		mInflater = LayoutInflater.from(context);
		mView = mInflater.inflate(R.layout.popwindow_layout, null);
		mTvProgress = (TextView)mView.findViewById(R.id.tvPop);
		mPopupWindow = new PopupWindow(mView, mView.getWidth(),
				mView.getHeight(), true);
		mPosition = new int[2];

	}
	
	public void setSeekBarText(String str){
		mTvProgress.setText(str);
	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					mPopupWindow.dismiss();
					break;

			}
		}
	};
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.getLocationOnScreen(mPosition);
			mPopupWindow.showAsDropDown(this, (int) event.getX(),
					mPosition[1] - 30);

			break;
		case MotionEvent.ACTION_UP:
			new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					handler.obtainMessage(0).sendToTarget();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

			break;
		}

		return super.onTouchEvent(event);
	}

	private int getViewWidth(View v){
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w, h);   
        return v.getMeasuredWidth();
	}
	private int getViewHeight(View v){
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredHeight();
	}
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int thumb_x = this.getProgress() * (this.getWidth() - mThumb.getIntrinsicWidth())
				/ this.getMax();
		int middle = this.getHeight() / 2+40;
		super.onDraw(canvas);

		if (mPopupWindow != null) {	
			try {
				this.getLocationOnScreen(mPosition);
				mPopupWindow.update(thumb_x+mPosition[0] - getViewWidth(mView) / 2+ mThumb.getIntrinsicWidth()/2,
						middle,getViewWidth(mView),getViewHeight(mView));
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public void setThumb(Drawable thumb) {
		mThumb = thumb;
		super.setThumb(thumb);
	}
}
