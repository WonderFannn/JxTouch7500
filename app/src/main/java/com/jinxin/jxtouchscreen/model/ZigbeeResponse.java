package com.jinxin.jxtouchscreen.model;

/**
 * 窗帘的应答
 * @author TangLong
 * @company 金鑫智慧
 */
public class ZigbeeResponse {
	private String len;
	private String sender;
	private String profile;
	private String cluster;
	private String dest;
	private String sour;
	private String payload;
	
	@Override
	public String toString() {
		return "WirelessCurtainResponse [len=" + len + ", sender=" + sender
				+ ", profile=" + profile + ", cluster=" + cluster + ", dest="
				+ dest + ", sour=" + sour + ", payload=" + payload + "]";
	}
	
	public String getLen() {
		return len;
	}
	public void setLen(String len) {
		this.len = len;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getSour() {
		return sour;
	}
	public void setSour(String sour) {
		this.sour = sour;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
