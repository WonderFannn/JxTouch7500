package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 客户模式区域信息
 *
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(LocalConstant.CUSTOMER_AREA)
public class CustomerArea extends BaseModel {
	private String customerId;        // 用户名
	private String areaName;        // 区域名称（分组名称）
	private int areaOrder;            // 区域顺序
	private String createTime;        // 创建时间
	private String updateTime;        // 更新时间
	private int status;                // 状态(0:disable 1:enable)
	private int idd;

	public CustomerArea() {

	}

	public CustomerArea(int idd, String customerId, String areaName,
	                    int areaOrder, String createTime, String updateTime, int status) {
		this.idd = idd;
		this.customerId = customerId;
		this.areaName = areaName;
		this.areaOrder = areaOrder;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.status = status;
	}

	public int getIdd() {
		return idd;
	}

	public void setIdd(int idd) {
		this.idd = idd;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getAreaOrder() {
		return areaOrder;
	}

	public void setAreaOrder(int areaOrder) {
		this.areaOrder = areaOrder;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
