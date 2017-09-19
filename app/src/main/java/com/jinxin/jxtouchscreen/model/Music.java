package com.jinxin.jxtouchscreen.model;


import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

@Table(LocalConstant.MUSIC)
public class Music extends BaseModel {
	
	private String musicName;			// 名称
	
	private String customerId;			// 歌手

	private String address485;			// 时长
	
	private String source;  		// 来源
	
	private Integer PlayNo;					// 播放序号
	
//	private String iyric;			// 歌词
	
	private String createTime;		// 创建时间
	
	public Music() {
		
	}

	public Music(String musicName, String customerId, String source, Integer PlayNo, String createTime) {
		this.musicName=musicName;
		this.customerId = customerId;
		this.source = source;
		this.PlayNo = PlayNo;
		this.createTime = createTime;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAddress485() {
		return address485;
	}

	public void setAddress485(String address485) {
		this.address485 = address485;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public Integer getPlayNo() {
		return PlayNo;
	}

	public void setPlayNo(Integer playNo) {
		PlayNo = playNo;
	}

	
	
	
	
}
