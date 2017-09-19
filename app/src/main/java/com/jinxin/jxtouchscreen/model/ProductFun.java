package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 产品功能列表单元
 *
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(LocalConstant.PRODUCT_FUN)
public class ProductFun extends BaseModel {

	private int funId;
	private String whId;
	private String funUnit;
	private String code;
	private String funName;
	private String comments;
	private String funType;
	private String lightColor;
	private int brightness;
	private int intColor;
	private long beHomepage;
	private int enable;
	private String funParams;
	private String state;

	private String tempFunName;
	private boolean isOpen = false;

	public ProductFun() {

	}

	public ProductFun(int funId, String whId, String funUnit, String code,
	                  String funName, String comments, String funType,
	                  String lightColor, int brightness, int intColor, int enable, String funParams) {
		super();
		this.funId = funId;
		this.whId = whId;
		this.funUnit = funUnit;
		this.code = code;
		this.funName = funName;
		this.comments = comments;
		this.funType = funType;
		this.lightColor = lightColor;
		this.brightness = brightness;
		this.intColor = intColor;
		this.enable = enable;
		this.funParams = funParams;
	}

	@Override
	public String toString() {
		return "ProductFun [funId=" + funId + ", whId=" + whId + ", funUnit="
				+ funUnit + ", code=" + code + ", funName=" + funName
				+ ", comments=" + comments + ", funType=" + funType
				+ ", lightColor=" + lightColor + ", brightness=" + brightness
				+ ", intColor=" + intColor + ", beHomepage=" + beHomepage
				+ ", enable=" + enable + ", tempFunName=" + tempFunName
				+ ", isOpen=" + isOpen + "]";
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

	public String getFunUnit() {
		return funUnit;
	}

	public void setFunUnit(String funUnit) {
		this.funUnit = funUnit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getFunType() {
		return funType;
	}

	public void setFunType(String funType) {
		this.funType = funType;
	}

	public String getTempFunName() {
		return tempFunName;
	}

	public void setTempFunName(String tempFunName) {
		this.tempFunName = tempFunName;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getLightColor() {
		return lightColor;
	}

	public void setLightColor(String lightColor) {
		this.lightColor = lightColor;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getIntColor() {
		return intColor;
	}

	public void setIntColor(int intColor) {
		this.intColor = intColor;
	}

	public long getBeHomepage() {
		return beHomepage;
	}

	public void setBeHomepage(long beHomepage) {
		this.beHomepage = beHomepage;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFunParams() {
		return funParams;
	}

	public void setFunParam(String funParam) {
		this.funParams = funParam;
	}

	public void setFunParams(String funParams) {
		this.funParams = funParams;
	}
}
