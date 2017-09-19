package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 客户模式列表单元
 *
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(LocalConstant.CUSTOMER_PATTERN)
public class CustomerPattern extends BaseModel {

	private int patternId;
	private String paternName;
	private String paternType;
	private String customerId;
	private String createUser;
	private String createTime;
	private String status;
	private String icon;
	private String ttsStart;
	private String ttsEnd;
	/**
	 * 点击次数
	 */
	private Integer clickCount;

	private Integer patternGroupId;
	/**
	 * 置顶
	 */
	private Integer stayTop;
	/**
	 * 备注
	 */
	private String memo;

	public CustomerPattern() {

	}

	public CustomerPattern(int patternId, String paternName, String paternType,
	                       String customerId, String createUser, String createTime,
	                       String status, String icon, String ttsStart, String ttsEnd,
	                       Integer patternGroupId, String memo) {
		super();
		this.patternId = patternId;
		this.paternName = paternName;
		this.paternType = paternType;
		this.customerId = customerId;
		this.createUser = createUser;
		this.createTime = createTime;
		this.status = status;
		this.icon = icon;
		this.ttsStart = ttsStart;
		this.ttsEnd = ttsEnd;
		this.patternGroupId = patternGroupId;
		this.memo = memo;
	}

	@Override
	public String toString() {
		return "CustomerPattern [patternId=" + patternId + ", paternName="
				+ paternName + ", paternType=" + paternType + ", customerId="
				+ customerId + ", createUser=" + createUser + ", createTime="
				+ createTime + ", status=" + status + ", icon=" + icon
				+ ", ttsStart=" + ttsStart + ", ttsEnd=" + ttsEnd
				+ ", clickCount=" + clickCount + ", patternGroupId="
				+ patternGroupId + ", stayTop=" + stayTop + ", memo=" + memo
				+ "]";
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public String getPaternName() {
		return paternName;
	}

	public void setPaternName(String paternName) {
		this.paternName = paternName;
	}

	public String getPaternType() {
		return paternType;
	}

	public void setPaternType(String paternType) {
		this.paternType = paternType;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTtsStart() {
		return ttsStart;
	}

	public void setTtsStart(String ttsStart) {
		this.ttsStart = ttsStart;
	}

	public String getTtsEnd() {
		return ttsEnd;
	}

	public void setTtsEnd(String ttsEnd) {
		this.ttsEnd = ttsEnd;
	}

	public Integer getClickCount() {
		return clickCount;
	}

	public void setClickCount(Integer clickCount) {
		this.clickCount = clickCount;
	}

	public Integer getPatternGroupId() {
		return patternGroupId;
	}

	public void setPatternGroupId(Integer patternGroupId) {
		this.patternGroupId = patternGroupId;
	}

	public Integer getStayTop() {
		return stayTop;
	}

	public void setStayTop(Integer stayTop) {
		this.stayTop = stayTop;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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
			if (o instanceof CustomerPattern) {
				CustomerPattern customerPattern = (CustomerPattern) o;
				if (customerPattern.patternId == this.patternId) {
					return true;
				}
			}
		}
		return false;
	}
}
