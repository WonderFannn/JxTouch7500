package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

@Table(LocalConstant.WH_RPODUCT_UNFRSRED)
public class WHproductUnfrared extends BaseModel {

	private Integer funId;

	private String whId;

	private String recordName;

	private String infraRedCode;

	private Integer enable;

	private Integer serCode;

	private String funIds;

	private String updateTime;

	public WHproductUnfrared() {

	}

	public WHproductUnfrared(Integer funId, String whId, String recordName, String infraRedCode, Integer enable, Integer serCode, String funIds, String updateTime
	) {
		super();
		this.funId = funId;
		this.whId = whId;
		this.recordName = recordName;
		this.infraRedCode = infraRedCode;
		this.enable = enable;
		this.serCode = serCode;
		this.funIds = funIds;
		this.updateTime = updateTime;

	}


	public String getInfraRedCode() {
		return infraRedCode;
	}


	public void setInfraRedCode(String infraRedCode) {
		this.infraRedCode = infraRedCode;
	}


	public Integer getFunId() {
		return funId;
	}


	public void setFunId(Integer funId) {
		this.funId = funId;
	}


	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public Integer getEnable() {
		return enable;
	}


	public void setEnable(Integer enable) {
		this.enable = enable;
	}


	public String getWhId() {
		return whId;
	}


	public void setWhId(String whId) {
		this.whId = whId;
	}


	public String getRecordName() {
		return recordName;
	}


	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}


	public Integer getSerCode() {
		return serCode;
	}


	public void setSerCode(Integer serCode) {
		this.serCode = serCode;
	}


	public String getFunIds() {
		return funIds;
	}


	public void setFunIds(String funIds) {
		this.funIds = funIds;
	}


}
