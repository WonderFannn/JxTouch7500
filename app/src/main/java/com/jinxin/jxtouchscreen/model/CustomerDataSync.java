package com.jinxin.jxtouchscreen.model;

import com.litesuits.orm.db.annotation.Table;

/**
 * Created by XTER on 2016/12/22.
 * 用户数据同步请求
 */
@Table("customer_data_sync")
public class CustomerDataSync extends BaseModel {
	private String customerId;            // 用户id
	private String actionId;            // 执行删除的记录的id
	private String actionFieldName;        // 执行的数据库名称
	private String actionFieldType;        // 1=Integer，2=String
	private Integer action;                // 1=插入、0=删除、2=更新
	private String model;                // 需要操作的表名
	private String updateTime;            // 更新时间

	public CustomerDataSync() {
	}

	public CustomerDataSync(String customerId, String actionId, String actionFieldName, String actionFieldType, Integer action, String model, String updateTime) {
		this.customerId = customerId;
		this.actionId = actionId;
		this.actionFieldName = actionFieldName;
		this.actionFieldType = actionFieldType;
		this.action = action;
		this.model = model;
		this.updateTime = updateTime;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getActionFieldName() {
		return actionFieldName;
	}

	public void setActionFieldName(String actionFieldName) {
		this.actionFieldName = actionFieldName;
	}

	public String getActionFieldType() {
		return actionFieldType;
	}

	public void setActionFieldType(String actionFieldType) {
		this.actionFieldType = actionFieldType;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "actionId:" + actionId + ",actionFieldName:" + actionFieldName
				+ ",actionFieldType" + actionFieldType + "action:" + action + ",model:" + model
				+ ",updateTime:" + updateTime;
	}
}
