package com.jinxin.jxtouchscreen.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jinxin.jxtouchscreen.R;


public class CircleSeekBar extends View {

	public interface OnCircleSeekBarChangeListener {
		void onStartTrackingTouch(CircleSeekBar seekBar);

		void onProgressChanged(CircleSeekBar seekBar, int progress);

		void onStopTrackingTouch(CircleSeekBar seekBar);
	}

	/**
	 * 游标
	 */
	private Drawable mThumb;
	/**
	 * 游标宽度
	 */
	private int mThumbWidth;
	/**
	 * 游标高度
	 */
	private int mThumbHeight;
	/**
	 * 游标起点坐标--左
	 */
	private float mThumbPosLeft;
	/**
	 * 游标起点坐标--上
	 */
	private float mThumbPosTop;

	/**
	 * 进度条是否可用
	 */
	private boolean mIsEnable;
	/**
	 * 起始角度
	 */
	private double mStartAngle;
	/**
	 * 进度条颜色
	 */
	private int mProgressForegroundColor;
	/**
	 * 进度条背景色
	 */
	private int mProgressBackgroundColor;
	/**
	 * 进度条宽度
	 */
	private float mProgressWidth;
	/**
	 * 进度条最大值
	 */
	private int mProgressMax;
	/**
	 * 进度条当前进度值
	 */
	private int mCurrentProgress;

	/**
	 * 组件坐标--X
	 */
	private int mCentreX;
	/**
	 * 组件坐标--Y
	 */
	private int mCentreY;
	/**
	 * 进度条绘制半径
	 */
	private int mRadius;
	/**
	 * 进度条游标度数
	 */
	private float mDegree;

	/**
	 * 进度条弧度辅助矩形
	 */
	private RectF mArcRectF;

	/**
	 * 进度条前景画笔
	 */
	private Paint mProgressForegroundPaint;
	/**
	 * 进度条背景画笔
	 */
	private Paint mProgressBackgroundPaint;

	private OnCircleSeekBarChangeListener mOnCircleSeekBarChangeListener;

	public CircleSeekBar(Context context) {
		super(context);
		initAttr(context, null);
	}

	public CircleSeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initAttr(context, attrs);
	}

	public CircleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(context, attrs);
	}

	protected void initAttr(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleSeekBar);
		final int count = a.getIndexCount();
		for (int i = 0; i < count; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.CircleSeekBar_android_thumb:
					mThumb = a.getDrawable(attr);
					break;
				case R.styleable.CircleSeekBar_progress_enable:
					mIsEnable = a.getBoolean(attr, false);
					break;
				case R.styleable.CircleSeekBar_progress_start_angle:
					mStartAngle = a.getInt(attr, 0);
					break;
				case R.styleable.CircleSeekBar_progress_foreground:
					mProgressForegroundColor = a.getColor(attr, Color.RED);
					break;
				case R.styleable.CircleSeekBar_progress_background:
					mProgressBackgroundColor = a.getColor(attr, Color.BLACK);
					break;
				case R.styleable.CircleSeekBar_progress_width:
					mProgressWidth = a.getDimension(attr, 5.0f);
					break;
				case R.styleable.CircleSeekBar_progress_max:
					mProgressMax = a.getInt(attr, 100);
					break;
			}
		}
		a.recycle();

		mArcRectF = new RectF();

		mProgressForegroundPaint = new Paint();
		mProgressBackgroundPaint = new Paint();

		// 设置画笔颜色
//		mProgressForegroundPaint.setColor(mProgressForegroundColor);
		mProgressBackgroundPaint.setColor(mProgressBackgroundColor);

		// 设置画笔为抗锯齿
		mProgressForegroundPaint.setAntiAlias(true);
		mProgressBackgroundPaint.setAntiAlias(true);

		// 设置画笔为描边
		mProgressForegroundPaint.setStyle(Paint.Style.STROKE);
		mProgressBackgroundPaint.setStyle(Paint.Style.STROKE);

		// 设置画笔粗细
		mProgressForegroundPaint.setStrokeWidth(mProgressWidth);
		mProgressBackgroundPaint.setStrokeWidth(mProgressWidth);

		// 游标图片大小
		if (mThumb != null) {
			mThumbWidth = mThumb.getIntrinsicWidth();
			mThumbHeight = mThumb.getIntrinsicHeight();
		}

		// 将起始角度控制在-180,180之间
		if (mStartAngle > 360 || mStartAngle < -360) {
			mStartAngle = mStartAngle % 360;
		}
		if (mStartAngle > 180) {
			mStartAngle = mStartAngle - 360;
		}
		if (mStartAngle < -180) {
			mStartAngle = mStartAngle + 360;
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (getWidth() == 0 || getHeight() == 0)
			return;

		mRadius = Math.min(getWidth(), getHeight()) / 2 - (int) mProgressWidth - 10;

		mCentreX = getWidth() / 2;
		mCentreY = getHeight() / 2;

		// 确定进度条圆弧所在位置
		mArcRectF.left = mCentreX - mRadius;
		mArcRectF.top = mCentreY - mRadius;
		mArcRectF.right = mCentreX + mRadius;
		mArcRectF.bottom = mCentreY + mRadius;

		// 只有确定自身的宽高大小后才能确定游标的位置
		setThumbPos(Math.toRadians(mDegree + mStartAngle));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制进度条背景
		canvas.drawCircle(mCentreX, mCentreY, mRadius, mProgressBackgroundPaint);
		// 绘制进度条前景--可动
		LinearGradient lg = new LinearGradient(0, 400, 0, 200, 0xff24B7E3, 0xFF9500c2, Shader.TileMode.MIRROR);
		mProgressForegroundPaint.setShader(lg);
		canvas.drawArc(this.mArcRectF, (int) mStartAngle, mDegree, false, mProgressForegroundPaint);

		if (mThumb != null && mThumbPosLeft > 0 && mThumbPosTop > 0) {
			mThumb.setBounds((int) mThumbPosLeft, (int) mThumbPosTop, (int) (mThumbPosLeft + mThumbWidth),
					(int) (mThumbPosTop + mThumbHeight));
			mThumb.draw(canvas);
		}
	}

	/**
	 * 设置游标位置大小
	 *
	 * @param radian 弧度
	 */
	protected void setThumbPos(double radian) {
		double x = mCentreX + mRadius * Math.cos(radian);
		double y = mCentreY + mRadius * Math.sin(radian);

		mThumbPosLeft = (float) (x - mThumbWidth / 2);
		mThumbPosTop = (float) (y - mThumbHeight / 2);
		if (mThumbPosLeft > 0 && mThumbPosTop > 0)
			invalidate();
	}

	/**
	 * 是否处于拖动状态
	 *
	 * @param eventX 触摸坐标--X
	 * @param eventY 触摸坐标--Y
	 * @return boolean
	 */
	protected boolean isDragging(float eventX, float eventY) {
		double distance = Math.sqrt(Math.pow(eventX - mCentreX, 2) + Math.pow(eventY - mCentreY, 2));
		if (distance > mRadius / 2 - mProgressWidth && distance < mRadius + mProgressWidth)
			return true;
		return false;
	}

	/**
	 * @param eventX
	 * @param eventY
	 */
	protected void onProgressRefresh(float eventX, float eventY) {
		if (isDragging(eventX, eventY) && mIsEnable) {
			double radian = Math.atan2(eventY - mCentreY, eventX - mCentreX);

			// 为补齐 360 开始计算的角
			if (radian <= Math.PI * (mStartAngle / 180)) {
				radian = radian + Math.PI * 2;
			}

			setThumbPos(radian);

			// 为适应从 起始角 开始计算的角 -- 进度条需要画的角度sweepAngle
			radian = radian - Math.PI * (mStartAngle / 180);

			mDegree = (float) Math.round(Math.toDegrees(radian));
			mCurrentProgress = (int) (mProgressMax * mDegree / 360);

			if (mOnCircleSeekBarChangeListener != null)
				mOnCircleSeekBarChangeListener.onProgressChanged(this, mCurrentProgress);
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				onStartTrackingTouch();
				onProgressRefresh(eventX, eventY);
				break;
			case MotionEvent.ACTION_MOVE:
				onProgressRefresh(eventX, eventY);
				break;
			case MotionEvent.ACTION_UP:
				onStopTrackingTouch();
				break;
		}

		return true;
	}

	protected void onStartTrackingTouch() {
		if (mOnCircleSeekBarChangeListener != null) {
			mOnCircleSeekBarChangeListener.onStartTrackingTouch(this);
		}
	}

	protected void onStopTrackingTouch() {
		if (mOnCircleSeekBarChangeListener != null) {
			mOnCircleSeekBarChangeListener.onStopTrackingTouch(this);
		}
	}

	public void setOnCircleSeekBarChangeListener(OnCircleSeekBarChangeListener l) {
		mOnCircleSeekBarChangeListener = l;
	}

	public Drawable getThumb() {
		return mThumb;
	}

	public void setThumb(Drawable thumb) {
		this.mThumb = thumb;
	}

	public int getProgressForegroundColor() {
		return mProgressForegroundColor;
	}

	public void setProgressForegroundColor(int progressForegroundColor) {
		this.mProgressForegroundColor = progressForegroundColor;
	}

	public int getProgressBackgroundColor() {
		return mProgressBackgroundColor;
	}

	public void setProgressBackgroundColor(int progressBackgroundColor) {
		this.mProgressBackgroundColor = progressBackgroundColor;
	}

	public float getProgressWidth() {
		return mProgressWidth;
	}

	public void setProgressWidth(float progressWidth) {
		this.mProgressWidth = progressWidth;
	}

	public int getProgressMax() {
		return mProgressMax;
	}

	public void setProgressMax(int progressMax) {
		this.mProgressMax = progressMax;
	}

	public int getCurrentProgress() {
		return mCurrentProgress;
	}

	public void setProgress(int progress) {
		if (progress > mProgressMax)
			progress = mProgressMax;
		if (progress < 0)
			progress = 0;
		mCurrentProgress = progress;
		mDegree = progress * 360 / mProgressMax;
		setThumbPos(Math.toRadians(mDegree + mStartAngle));
		invalidate();
	}
}
