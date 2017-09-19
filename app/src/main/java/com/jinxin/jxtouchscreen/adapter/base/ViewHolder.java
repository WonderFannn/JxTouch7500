package com.jinxin.jxtouchscreen.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by XTER on 2016/10/27.
 * recycleview--holder
 */
public class ViewHolder extends RecyclerView.ViewHolder {
	private SparseArray<View> views = new SparseArray<>();
	private View convertView;

	public ViewHolder(View convertView) {
		super(convertView);
		this.convertView = convertView;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int resId) {
		View v = views.get(resId);
		if (null == v) {
			v = convertView.findViewById(resId);
			views.put(resId, v);
		}
		return (T) v;
	}
}
