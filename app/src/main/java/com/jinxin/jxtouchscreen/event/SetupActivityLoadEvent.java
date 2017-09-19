package com.jinxin.jxtouchscreen.event;

/**
 * Created by XTER on 2016/9/7.
 * 开关UI更新
 */
public class SetupActivityLoadEvent {
	private int pos;

	public SetupActivityLoadEvent(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
}
