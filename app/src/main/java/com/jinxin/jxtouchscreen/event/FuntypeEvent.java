package com.jinxin.jxtouchscreen.event;

/**
 * Created by XTER on 2017/01/09.
 * 传递设备类型事件
 */
public class FuntypeEvent {
	public String funtype;

	public FuntypeEvent(String funtype) {
		this.funtype = funtype;
	}

	public String getFuntype() {
		return funtype;
	}

	public void setFuntype(String funtype) {
		this.funtype = funtype;
	}
}
