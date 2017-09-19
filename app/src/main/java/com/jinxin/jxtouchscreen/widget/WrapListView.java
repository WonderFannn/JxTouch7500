package com.jinxin.jxtouchscreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by XTER on 2016/9/7.
 * 适应滚动视图
 */
public class WrapListView extends ListView {
	public WrapListView(Context context) {
		super(context);
	}

	public WrapListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, height);
	}
}
