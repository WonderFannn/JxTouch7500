package com.jinxin.jxtouchscreen.net.base;

import java.util.List;

/**
 * Created by XTER on 2016/9/19.
 * 基类
 */
public abstract class Callback<T> implements ICallback<T> {
	@Override
	public void onReceive(T t) {

	}

	@Override
	public void onReceive(List<T> t) {

	}
}
