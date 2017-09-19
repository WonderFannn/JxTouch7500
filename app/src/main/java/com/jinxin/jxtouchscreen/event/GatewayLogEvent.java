package com.jinxin.jxtouchscreen.event;

/**
 * Created by XTER on 2017/03/08.
 * 网关日志
 */
public class GatewayLogEvent {
	private String time;
	private String log;

	public GatewayLogEvent(String time, String log) {
		this.time = time;
		this.log = log;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
}
