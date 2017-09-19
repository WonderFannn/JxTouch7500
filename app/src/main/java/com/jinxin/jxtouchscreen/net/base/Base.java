package com.jinxin.jxtouchscreen.net.base;

import java.io.Serializable;

/**
 * Created by XTER on 2016/9/20.
 * 基类
 */
public abstract class Base implements Serializable {
	public int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

