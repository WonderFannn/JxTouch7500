package com.jinxin.jxtouchscreen.model;


import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

@Table(LocalConstant.PRODUCT_DOOR_CONTACT)
public class ProductDoorContact extends BaseModel {

	private String whId;

	private String status;// 00:关 ,01：开

	/**
	 * 电量
	 */
	private String electric;

	/**
	 * 是否开启警告
	 */
	private int isWarn = 1; //是否开启警报 0：关， 1： 开

	public ProductDoorContact() {
	}

	public ProductDoorContact(String whId, String status, String electric) {
		super();
		this.whId = whId;
		this.status = status;
		this.electric = electric;
		this.isWarn = 1;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getElectric() {
		return electric;
	}

	public void setElectric(String electric) {
		this.electric = electric;
	}

	public int getIsWarn() {
		return isWarn;
	}

	public void setIsWarn(int isWarn) {
		this.isWarn = isWarn;
	}


}
