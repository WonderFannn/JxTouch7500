package com.jinxin.jxtouchscreen.model;

/**
 * Created by XTER on 2017/02/27.
 * 万物互联状态
 */
public class EventState extends BaseModel {

	private int funId;

	private String state;

	private int taskId;

	public EventState(int funId, String state, int taskId) {
		this.funId = funId;
		this.state = state;
		this.taskId = taskId;
	}

	public int getFunId() {
		return funId;
	}

	public void setFunId(int funId) {
		this.funId = funId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
}
