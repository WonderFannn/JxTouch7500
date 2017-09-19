package com.jinxin.jxtouchscreen.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.jinxin.jxtouchscreen.util.SysUtil;


/**
 * Created by Administrator on 2016/11/27.
 */
public class ColorPickerView extends View {
    private Paint mPaint;// 渐变色环画笔
    private Paint mClickPaint;// 触控点画笔
    private Paint mLinePaint;// 触控点画笔
    private int clickX = 0;
    private int clickY = 0;
    public OnColorChangedListener mListener;
    //颜色数组，用这个值来画圆的颜色
    private final int[] mCircleColors = new int[]{0xFFFF0000, 0xFFFF00FF,
            0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
    //色圆的尺寸
    private int mHeight;// View高
    private int mWidth;// View宽
    //可以画成色环
    private float r;// 色环半径(paint中部)

    private boolean downInCircle = true;// 按在渐变环上


    public ColorPickerView(Context context) {
        super(context);
        init(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
//        mHeight = 227;
//        mWidth = 227;
        mWidth = SysUtil.getScreenHeight()/3;
        mHeight = SysUtil.getScreenHeight()/3;
        setFocusable(true);
        setFocusableInTouchMode(true);

        //准备色圆的画笔
        Shader s = new SweepGradient(0, 0, mCircleColors, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿
        mPaint.setShader(s);
        mPaint.setStyle(Paint.Style.FILL); // 设置实心


        r = mWidth / 2 * 1.0f;

        //控制点的画笔
        mClickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClickPaint.setStyle(Paint.Style.STROKE);
        mClickPaint.setStrokeWidth(3f);
        mClickPaint.setColor(Color.parseColor("#ffffff"));

        //线的画笔ine
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(1f);
        mLinePaint.setColor(Color.parseColor("#ffffff"));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        // 移动中心
        canvas.translate(mWidth / 2, mHeight / 2);
        //画色圆， r是半径
        canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
        //画触控点
        canvas.drawCircle(clickX, clickY, 12, mClickPaint);
        canvas.drawLine(clickX - 5, clickY, clickX + 5, clickY, mClickPaint);
        canvas.drawLine(clickX, clickY - 5, clickX, clickY + 5, mClickPaint);
        canvas.save();
        canvas.restore();
//        Bitmap bitmap = ((BitmapDrawable) getContext().getResources().getDrawable(R.drawable.illumination_color_disk)).getBitmap();
//        canvas.drawBitmap(bitmap,0,0,mPaint);
//        canvas.translate(mWidth / 2, mHeight / 2);
//        Bitmap bitmapBt = ((BitmapDrawable) getContext().getResources().getDrawable(R.drawable.illumination_color_disk_bt)).getBitmap();
//        canvas.drawBitmap(bitmapBt, 0, 0, mClickPaint);
//        canvas.save();
//        canvas.restore();
        super.onDraw(canvas);
    }

    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取触摸点坐标，看是否在圆内
        float x = event.getX() - mWidth / 2;
        float y = event.getY() - mHeight / 2 + 50;
        boolean inCircle = inColorCircle(x, y, r);

        //判断事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downInCircle = inCircle;
                if (downInCircle) {
                    clickX = (int) x;
                    clickY = (int) y;
                    invalidate();
                    mListener.onColorChanged(getColor());
                }
            case MotionEvent.ACTION_MOVE:
                if (downInCircle && inCircle) {// down按在渐变色环内, 且move也在渐变色环内
                    clickX = (int) x;
                    clickY = (int) y;
                    invalidate();
                    mListener.onColorChanged(getColor());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (downInCircle) {
                    downInCircle = false;
                    invalidate();
                }
                break;
        }
        return true;
    }

    //测量view的尺寸
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = SysUtil.getScreenHeight()/3;
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(widthSize, mWidth);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = SysUtil.getScreenHeight()/3;
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(heightSize, mHeight);
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    //将触摸点的坐标转换成颜色值
    private int getColor() {
        //得出触摸点与X轴的弧度角
        float angle = (float) Math.atan2(clickY, clickX);
        //得出百分比
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }
        return interpCircleColor(mCircleColors, unit);
    }

    private boolean inColorCircle(float x, float y, float outRadius) {
        double outCircle = Math.PI * outRadius * outRadius;
        // double inCircle = Math.PI * inRadius * inRadius;
        double fingerCircle = Math.PI * (x * x + y * y);
        // && fingerCircle > inCircle
        if (fingerCircle < outCircle) {
            return true;
        } else {
            return false;
        }
    }

    private boolean inCenter(float x, float y, float centerRadius) {
        double centerCircle = Math.PI * centerRadius * centerRadius;
        double fingerCircle = Math.PI * (x * x + y * y);
        if (fingerCircle < centerCircle) {
            return true;
        } else {
            return false;
        }
    }

    //获取圆环上颜色
    private int interpCircleColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }


    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    //获取listener
    public OnColorChangedListener getColorChangedListener() {
        return mListener;
    }

    //设置颜色变化listener
    public void setColorChangedListener(OnColorChangedListener mListener) {
        this.mListener = mListener;
    }

}
