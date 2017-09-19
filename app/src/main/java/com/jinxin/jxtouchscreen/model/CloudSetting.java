package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 云设置
 * Created by admin on 2016/6/3.
 */

@Table(LocalConstant.CLOUD_SETTING)
public class CloudSetting extends BaseModel {
	private String server_id;            // 平台端的id
	private String customerId;            // 用户id
	private String category;            // 设备类型
	private String items;                // 设置项
	private String params;                // 设置项的参数
	private String update_time;            // 更新时间
	private String create_time;            // 建立时间

	public CloudSetting() {

	}

	public CloudSetting(String server_id, String customerId, String category, String items, String params,
	                    String update_time, String create_time) {
		super();
		this.server_id = server_id;
		this.customerId = customerId;
		this.category = category;
		this.items = items;
		this.params = params;
		this.update_time = update_time;
		this.create_time = create_time;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof CloudSetting) {
			final CloudSetting other = (CloudSetting) object;
			return this.category.equals(other.category) && this.items.equals(other.category) && this.params.equals(other.params);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "CloudSetting [id=" + id + ", server_id=" + server_id
				+ ", customerId=" + customerId + ", category=" + category
				+ ", items=" + items + ", params=" + params + ", update_time="
				+ update_time + ", create_time=" + create_time + "]";
	}

	public String getServer_id() {
		return server_id;
	}

	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}

