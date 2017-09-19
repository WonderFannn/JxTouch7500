package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by XTER on 2017/03/07.
 * 大网关日志
 */
@Table(LocalConstant.GATEWAY_LOG)
public class GatewayLog extends BaseModel {
	private String time;
	private String message;

	public GatewayLog(){}

	public GatewayLog(String time, String message) {
		this.time = time;
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
