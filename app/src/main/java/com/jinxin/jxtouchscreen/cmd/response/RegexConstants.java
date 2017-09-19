package com.jinxin.jxtouchscreen.cmd.response;

/**
 * 各种设备返回结果格式通配符常量
 * @author TangLong
 * @company 金鑫智慧
 */
public class RegexConstants {
	
	// Zigbee窗帘
	public static final String ZG_BASIC_CONTENT_REP = "\\s*(len)\\s*=\\s*(\\S+)\\s*(sender)\\s*=\\s*(\\S+)\\s*(profile)\\s*=\\s*(\\S+)\\s*(cluster)\\s*=\\s*(\\S+)\\s*(dest)\\s*=\\s*(\\S+)\\s*(sour)\\s*=\\s*(\\S+)\\s*(payload)\\s*\\[\\s*(\\S.+)\\]";

}
