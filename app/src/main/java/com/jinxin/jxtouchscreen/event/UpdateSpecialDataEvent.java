package com.jinxin.jxtouchscreen.event;

/**
 * Created by XTER on 2017/03/02.
 * 更新某些特殊数据所用
 */
public class UpdateSpecialDataEvent {
	private boolean isComplete;
	private String type;
	private String extra;

	public UpdateSpecialDataEvent(boolean isComplete, String type) {
		this.isComplete = isComplete;
		this.type = type;
	}

	public UpdateSpecialDataEvent(boolean isComplete, String type, String extra) {
		this.isComplete = isComplete;
		this.type = type;
		this.extra = extra;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean complete) {
		isComplete = complete;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
}
