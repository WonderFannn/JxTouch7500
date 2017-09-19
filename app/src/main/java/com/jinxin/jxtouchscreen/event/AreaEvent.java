package com.jinxin.jxtouchscreen.event;

/**
 * Created by XTER on 2017/01/06.
 * 传递区域所在位置
 */
public class AreaEvent {
	private long groudId;

	public AreaEvent(long groudId) {
		this.groudId = groudId;
	}

	public long getGroudId() {
		return groudId;
	}

	public void setGroudId(long groudId) {
		this.groudId = groudId;
	}
}
