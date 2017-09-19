package com.jinxin.jxtouchscreen.net.base;

import java.util.List;

/**
 * Created by XTER on 2016/9/19.
 * 接收接口
 */
public interface ICallback<T> {
	void onReceive(T t);

	void onReceive(List<T> t);

	void onError(String error);
}
