package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 设备功能明细列表单元
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(LocalConstant.FUN_DETAIL_CONFIG)
public class FunDetailConfig extends BaseModel{
	private String whId;
	private String funType;
	private String params;
	private String alias;
	private String updateTime;
	
	public FunDetailConfig() {

	}
	
	@Override
	public String toString() {
		return "id:" + id + "whId:" + whId + ",funType:" + funType + ",params:" + params + ",updateTime:" + updateTime;
	}
	
	public FunDetailConfig(String funType, String whId, String params,
	                       String alias, String updateTime) {
		super();
		this.funType = funType;
		this.whId = whId;
		this.params = params;
		this.alias = alias;
		this.updateTime = updateTime;
	}


	public String getFunType() {
		return funType;
	}

	public void setFunType(String funType) {
		this.funType = funType;
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
