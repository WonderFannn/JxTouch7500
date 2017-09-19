package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 产品模式操作列表单元
 *
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(LocalConstant.PRODUCT_PATTERN_OPERATION)
public class ProductPatternOperation extends BaseModel {

	private int funId;
	private String whId;
	private int patternId;
	private String operation;
	private String paraDesc;
	private String updateTime;

	public ProductPatternOperation() {

	}

	public ProductPatternOperation(int funId, String whId,
	                               int patternId, String operation, String paraDesc, String updateTime) {
		super();
		this.funId = funId;
		this.whId = whId;
		this.patternId = patternId;
		this.operation = operation;
		this.paraDesc = paraDesc;
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "ProductPatternOperation{" +
				"id=" + id +
				", funId=" + funId +
				", whId='" + whId + '\'' +
				", patternId=" + patternId +
				", operation='" + operation + '\'' +
				", paraDesc='" + paraDesc + '\'' +
				", updateTime='" + updateTime + '\'' +
				'}';
	}

	public int getFunId() {
		return funId;
	}

	public void setFunId(int funId) {
		this.funId = funId;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getParaDesc() {
		return paraDesc;
	}

	public void setParaDesc(String paraDesc) {
		this.paraDesc = paraDesc;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public boolean equals(Object o) {
		//首先判断是否为空
		if (o != null) {
			//自己和自己比较时,直接返回true
			if (o == this) {
				return true;
			}
			//判断是否是同类型的对象进行比较
			if (o instanceof ProductPatternOperation) {
				ProductPatternOperation productPatternOperation = (ProductPatternOperation) o;
				if (productPatternOperation.patternId == this.patternId &&productPatternOperation.funId == this.funId) {
					return true;
				}
			}
		}
		return false;
	}
}
