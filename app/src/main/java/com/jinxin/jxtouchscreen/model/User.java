package com.jinxin.jxtouchscreen.model;


import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 用户类
 */
@Table(LocalConstant.USER)
public class User extends BaseModel {

	/**
	 * 自动映射到关系表
	 * 声明为private
	 */
	private String account;

	private String secretKey;

	private String nickyName;

	private String subAccunt;

	public User() {
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getNickyName() {
		return nickyName;
	}

	public void setNickyName(String nickyName) {
		this.nickyName = nickyName;
	}

	public String getSubAccunt() {
		return subAccunt;
	}

	public void setSubAccount(String subAccount) {
		this.subAccunt = subAccount;
	}

	@Override
	public String toString() {
		return "User{" +
				"account='" + account + '\'' +
				", secretKey='" + secretKey + '\'' +
				", nickyName='" + nickyName + '\'' +
				", subAccunt='" + subAccunt + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		//首先判断是否为空
		if (obj != null) {
			//自己和自己比较时,直接返回true
			if (obj == this) {
				return true;
			}
			//判断是否是同类型的对象进行比较
			if (obj instanceof User) {
				User user = (User) obj;
				if (user.account.equals(this.account)) {
					return true;
				}
			}
		}
		return false;
	}
}