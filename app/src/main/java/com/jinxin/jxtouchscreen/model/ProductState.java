package com.jinxin.jxtouchscreen.model;


import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

@Table(LocalConstant.PRODUCT_STATE)
public class ProductState extends BaseModel {

	private int funId;

	private String state;

	public ProductState() {
	}

	public String toString() {
		return "{" + "funId" + funId + ",state:" + state + "}";
	}

	public ProductState(int funId, String state) {
		super();
		this.funId = funId;
		this.state = state;
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

}
