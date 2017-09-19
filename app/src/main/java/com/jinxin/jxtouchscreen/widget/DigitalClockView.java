package com.jinxin.jxtouchscreen.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.util.SysUtil;


/**
 * Created by XTER on 2016/10/18.
 * 数字时钟
 */
public class DigitalClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean isRun;

    private TextPaint mDatePaint;
    private TextPaint mTimePaint;

    private String mDateFormat;
    private String mTimeFormat;

    private float mDateTextSize;
    private float mTimeTextSize;

    private float mDateTopPadding;
    private float mTimeTopPadding;

    public DigitalClockView(Context context) {
        this(context, null);
    }

    public DigitalClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitalClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int dateTextColor = -1;
        int timeTextColor = -1;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DigitalClockView);
        //final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DigitalClockView, 0, 0);
        final int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.DigitalClockView_date_format:
                    mDateFormat = a.getString(attr);
                    break;
                case R.styleable.DigitalClockView_time_format:
                    mTimeFormat = a.getString(attr);
                    break;
                case R.styleable.DigitalClockView_date_text_size:
                    mDateTextSize = a.getDimension(attr, 18);
                    break;
                case R.styleable.DigitalClockView_time_text_size:
                    mTimeTextSize = a.getDimension(attr, 18);
                    break;
                case R.styleable.DigitalClockView_date_text_color:
                    dateTextColor = a.getColor(attr, -1);
                    break;
                case R.styleable.DigitalClockView_time_text_color:
                    timeTextColor = a.getColor(attr, -1);
                    break;
            }
        }
        a.recycle();

        //文字画笔
        mDatePaint = new TextPaint();
        mTimePaint = new TextPaint();
        //画笔是否抗锯齿
        mDatePaint.setAntiAlias(true);
        mTimePaint.setAntiAlias(true);
        //文字居中
        mDatePaint.setTextAlign(Paint.Align.CENTER);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        //文字颜色
        mDatePaint.setColor(dateTextColor);
        mTimePaint.setColor(timeTextColor);
        //文字大小
        mDatePaint.setTextSize(mDateTextSize);
        mTimePaint.setTextSize(mTimeTextSize);

        //文字绘制位置
        Paint.FontMetrics fontMetrics1 = mDatePaint.getFontMetrics();
        Paint.FontMetrics fontMetrics2 = mTimePaint.getFontMetrics();
        mDateTopPadding = Math.abs(fontMetrics1.top);
        mTimeTopPadding = fontMetrics1.bottom - fontMetrics1.top + Math.abs(fontMetrics2.ascent);

        //获取控制
        mHolder = getHolder();
        mHolder.addCallback(this);

        //背景透明
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRun = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRun = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int desiredWidth, desiredHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            desiredWidth = widthSize;
        } else {
            //以文字宽度为适配
            int dateTextWidth = (int) mDatePaint.measureText(mDateFormat + "  " + mTimeFormat);
            int timeTextWidth = (int) mTimePaint.measureText(mTimeFormat);
            desiredWidth = Math.max(dateTextWidth, timeTextWidth);
            if (widthMode == MeasureSpec.AT_MOST) {
                desiredWidth = Math.min(widthSize, desiredWidth);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            desiredHeight = heightSize;
        } else {
            //以文字高度为适配
            Paint.FontMetrics fontMetrics1 = mDatePaint.getFontMetrics();
            Paint.FontMetrics fontMetrics2 = mTimePaint.getFontMetrics();
            desiredHeight = (int) (fontMetrics1.bottom - fontMetrics1.top + fontMetrics2.bottom - fontMetrics2.top);
            if (heightMode == MeasureSpec.AT_MOST) {
                desiredHeight = Math.min(heightSize, desiredHeight);
            }
        }
        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    @Override
    public void run() {
        long start, end;
        while (isRun) {
            start = System.currentTimeMillis();
            draw();
            end = System.currentTimeMillis();

            //消除延时
            try {
                if (end - start < 1000) {
                    Thread.sleep(1000 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //刷屏
//				mCanvas.drawColor(Color.BLACK);
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
                //绘制文字
                mCanvas.drawText(SysUtil.getDate(System.currentTimeMillis(), mDateFormat + "    " + mTimeFormat), mCanvas.getWidth() / 2,
                        mDateTopPadding, mDatePaint);
                /*mCanvas.drawText(SysUtil.getDate(System.currentTimeMillis(), mTimeFormat), mCanvas.getWidth() / 2,
                        mTimeTopPadding, mTimePaint);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

}
