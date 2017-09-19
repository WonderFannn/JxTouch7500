package com.jinxin.jxtouchscreen.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

@Table("environment")
public class Environment {

	@Column("location")
	private String location;

	@Column("wd")
	private String Wd;

	@Column("kq")
	private String Kq;

	@Column("sd")
	private String Sd;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWd() {
		return Wd;
	}

	public void setWd(String wd) {
		Wd = wd;
	}

	public String getKq() {
		return Kq;
	}

	public void setKq(String kq) {
		Kq = kq;
	}

	public String getSd() {
		return Sd;
	}

	public void setSd(String sd) {
		Sd = sd;
	}

	@Override
	public String toString() {
		return "Environment{" +
				"location='" + location + '\'' +
				", Wd='" + Wd + '\'' +
				", Kq='" + Kq + '\'' +
				", Sd='" + Sd + '\'' +
				'}';
	}
}
