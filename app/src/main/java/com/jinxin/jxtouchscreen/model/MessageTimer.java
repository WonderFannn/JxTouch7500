package com.jinxin.jxtouchscreen.model;


import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * 消息定时接收
 *
 * @author YangJiJun
 * @company 金鑫智慧
 */
@Table("messagetimer")
public class MessageTimer extends BaseModel {

	private String timeRange;

	public MessageTimer() {
	}

	public MessageTimer(String timeRange) {
		super();
		this.timeRange = timeRange;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

}
