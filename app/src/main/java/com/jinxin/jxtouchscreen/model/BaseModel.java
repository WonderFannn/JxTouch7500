package com.jinxin.jxtouchscreen.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * 基类
 */
public abstract class BaseModel implements Serializable {

	@PrimaryKey(AssignType.AUTO_INCREMENT)
	@Column("_id")
	public long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BaseModel() {
	}

}
