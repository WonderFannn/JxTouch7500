package com.jinxin.jxtouchscreen.net.request;

import com.jinxin.jxtouchscreen.model.BaseModel;
import com.litesuits.orm.db.annotation.Table;

/**
 * 消息提醒
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table("customer_meassage")
public class CustomerMeassage extends BaseModel {
	private String whId;
	private String message;
	private String time;
	private int isReaded;//已读标识 0:未读 ，1：已读
	
	public CustomerMeassage() {
	}

	public CustomerMeassage(int id, String whId, String message, String time, int isReaded) {
		super();
		this.whId = whId;
		this.message = message;
		this.time = time;
		this.isReaded = isReaded;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getIsReaded() {
		return isReaded;
	}

	public void setIsReaded(int isReaded) {
		this.isReaded = isReaded;
	}

}
