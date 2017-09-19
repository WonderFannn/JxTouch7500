package com.jinxin.jxtouchscreen.constant;

public class ProductConstants {
	public static final String CHARSET_UTF8 = "UTF-8";
	/**
	 * 音乐播放模块中用到的sp存储名称
	 */
	public static final String SP_APP = "JXSH";

	public static final String SP_MUSIC = SP_APP + "SP_MUSIC";

	public static final String SP_SETTINGS = SP_APP + "SP_SETTINGS";
	public static final String SP_SETTINGS_OFFLINE = "sp_settings_offline";

	// 扬声器设置
	public static final String SP_MUSIC_SPEAKER = "SP_MUSIC_SPEAKER";
	public static final String SP_MUSIC_INPUT = "SP_MUSIC_INPUT";

	/**
	 * 设备巡检"0Search"
	 */
	public static final String FUN_TYPE_DEVICE_DETECT = "0Search";
	
	/**
	 * ZG设备巡检"0Search"
	 */
	public static final String FUN_TYPE_ZG_DEVICE_DETECT = "0Search_ZG";

	/**
	 * 灯"00101"
	 */
	public static final String FUN_TYPE_MASTER_CONTROLLER_LIGHT = "00101";

	/**
	 * 窗帘"00A"
	 */
	public static final String FUN_TYPE_MASTER_CONTROLLER_CURTAIN = "00A";

	/**
	 * 人体感应 "002"
	 */
	public static final String FUN_TYPE_HUMAN_CAPTURE = "002";
	/**
	 * 人体感应 "00901"
	 */
	public static final String FUN_TYPE_HUMAN_CAPTURE_NEW = "00901";
	/**
	 * 人体感应 "00902" 新红外
	 */
	public static final String FUN_TYPE_HUMAN_CAPTURE_NEW2 = "00902";

	/**
	 * 触摸屏 "00301"
	 */
	public static final String FUN_TYPE_TFT_LIGHT = "00301";

	/**
	 * 触摸屏 "00302"
	 */
	public static final String FUN_TYPE_TFT_CURTAIN = "00302";

	/**
	 * 功放 "004"
	 */
	public static final String FUN_TYPE_POWER_AMPLIFIER = "004";
	// 静音
	public static final String POWER_AMPLIFIER_MUTE = "0041";
	// 取消静音
	public static final String POWER_AMPLIFIER_UNMUTE = "0047";
	// 上一曲
	public static final String POWER_AMPLIFIER_PROVIOUS = "0042";
	// 下一曲
	public static final String POWER_AMPLIFIER_NEXT = "0043";
	// 音量 +
	public static final String POWER_AMPLIFIER_SOUND_ADD = "0044";
	// 音量 -
	public static final String POWER_AMPLIFIER_SOUND_SUB = "0045";
	// 获取播放列表
	public static final String POWER_AMPLIFIER_SOUND_LIST = "0046";
	// 获取歌曲名称
	public static final String POWER_AMPLIFIER_SOUND_SONG = "0048";
	// 播放
	public static final String POWER_AMPLIFIER_SOUND_PLAY = "0049";
	// 暂停
	public static final String POWER_AMPLIFIER_SOUND_PAUSE = "00411";
	// 播放指定歌曲
	public static final String POWER_AMPLIFIER_SOUND_PLAY_SONG = "00412";
	// 列表循环播放
	public static final String POWER_AMPLIFIER_SOUND_REPEAT_ALL = "00413";
	// 单曲循环播放
	public static final String POWER_AMPLIFIER_SOUND_REPEAT_SINGLE = "00414";
	// 设备连接状态
	public static final String POWER_AMPLIFIER_DEVICE_LINK = "00415";
	// 设备获取音量
	public static final String POWER_AMPLIFIER_GET_VOLUME = "00416";
	// 设备播放状态
	public static final String POWER_AMPLIFIER_PLAY_STATUS = "00417";
	// 设备设置音量
	public static final String POWER_AMPLIFIER_SET_VOLUME = "00418";
	// 获取功放版本
	public static final String POWER_AMPLIFIER_GET_VERSION = "00419";
	// 输入输出绑定关系
	public static final String POWER_AMPLIFIER_GET_BIND = "00420";
	// 功放静音状态
	public static final String POWER_AMPLIFIER_GET_MUTE_STATUS = "00421";

	/**
	 * 飞碟 "00501"
	 */
	public static final String FUN_TYPE_UFO1 = "00501";
	/**
	 * 飞碟遥控 "00506"
	 */
	public static final String FUN_TYPE_UFO6 = "00506";
	/**
	 * 飞碟遥控 "00507"
	 */
	public static final String FUN_TYPE_UFO7 = "00507";
	/**
	 * 环境 00502
	 */
	public static final String FUN_TYPE_ENV = "00502";
	/**
	 * 获取温度和湿度 00501
	 */
	public static final String FUN_TYPE_UFO1_TEMP_HUMI = "0051";

	/**
	 * 自动锁 "006"
	 */
	public static final String FUN_TYPE_AUTO_LOCK = "006";
	/**
	 * 插座 "00103"
	 */
	public static final String FUN_TYPE_AUTO_CHA_ZUO = "00103";
	// 获取锁的状态
	public static final String FUN_TYPE_AUTO_LOCK_SEARCH = "0061";
	// 打开自动锁
	public static final String FUN_TYPE_AUTO_LOCK_OPEN = "0062";
	// 关闭自动锁
	public static final String FUN_TYPE_AUTO_LOCK_CLOSE = "0063";
	/**
	 * 网关 "007"
	 */
	public static final String FUN_TYPE_GATEWAY = "007";
	/**
	 * 语音方言切换控制 "0070"
	 */
	public static final String FUN_TYPE_GATEWAY_VOICE_LANGUAGE = "0070";
	/**
	 * 打开智能语音控制 "0071"
	 */
	public static final String FUN_TYPE_GATEWAY_WATCH = "0071";

	/**
	 * 视频分析器 "008"
	 */
	public static final String FUN_TYPE_VEDIO_ANALYZER = "008";
	/**
	 * 彩灯 "008"
	 */
	public static final String FUN_TYPE_COLOR_LIGHT = "008";

	/**
	 * 麦克风
	 */
	public static final String FUN_TYPE_MIC = "088";

	/**
	 * 窗帘 "00A"
	 */
	public static final String FUN_TYPE_CURTAIN = "00A";
	/**
	 * 安防
	 */
	public static final String FUN_TYPE_SECUTIRY = "00702";
	/**
	 * 手势识别
	 */
	public static final String FUN_TYPE_GESTURE = "0072";
	/**
	 * 语音识别
	 */
	public static final String FUN_TYPE_VOICE = "0073";
	/**
	 * 无线插座
	 */
	public static final String FUN_TYPE_WIRELESS_SOCKET = "I304";
	/**
	 * 无线窗帘
	 */
	public static final String FUN_TYPE_WIRELESS_CURTAIN = "I309";
	/**
	 * 球泡灯
	 */
	public static final String FUN_TYPE_POP_LIGHT = "I305";
	/**
	 * 水晶灯
	 */
	public static final String FUN_TYPE_CRYSTAL_LIGHT = "I308";
	/**
	 * 吸顶灯
	 */
	public static final String FUN_TYPE_CEILING_LIGHT = "I307";
	/**
	 * 无线射灯
	 */
	public static final String FUN_TYPE_WIRELESS_SEND_LIGHT = "027";
	/**
	 * 无线网关
	 */
	public static final String FUN_TYPE_WIRELESS_GATEWAY = "G102";
	/**
	 * 门磁
	 */
	public static final String FUN_TYPE_DOOR_CONTACT = "016";
	/**
	 * ZG人体感应
	 */
	public static final String FUN_TYPE_INFRARED = "019";
	/**
	 * 烟雾传感器
	 */
	public static final String FUN_TYPE_SMOKE_SENSE = "017";
	/**
	 * 可燃气体传感器
	 */
	public static final String FUN_TYPE_GAS_SENSE = "018";
	/**
	 * 灯带
	 */
	public static final String FUN_TYPE_LIGHT_BELT = "01A";
	/**
	 * 双路开关
	 */
	public static final String FUN_TYPE_DOUBLE_SWITCH = "01C";
	/**
	 * 多路开关
	 */
	public static final String FUN_TYPE_MULITIPLE_SWITCH = "026";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_FIVE = "038";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_FOUR = "039";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_THREE = "040";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_TWO = "041";
	public static final String FUN_TYPE_MULITIPLE_SWITCH_ONE = "042";
	public static final String[] FUN_TYPE_MULITIPLE_SWITCHES = {
			FUN_TYPE_MULITIPLE_SWITCH_ONE,FUN_TYPE_MULITIPLE_SWITCH_TWO,
			FUN_TYPE_MULITIPLE_SWITCH_THREE,FUN_TYPE_MULITIPLE_SWITCH_FOUR, 
			FUN_TYPE_MULITIPLE_SWITCH_FIVE,FUN_TYPE_MULITIPLE_SWITCH};
	/**
	 * 语音盒子
	 */
	public static final String FUN_TYPE_VIOCE_BOX = "020";
	/**
	 * 空调控制器
	 */
	public static final String FUN_TYPE_AIRCONDITION = "021";
	/**
	 * ZG锁
	 */
	public static final String FUN_TYPE_ZG_LOCK = "024";
	/**
	 * 智能手表
	 */
	public static final String FUN_TYPE_WATCH = "025";

	/**
	 * Pluto音箱 
	 */
	public static final String FUN_TYPE_PLUTO_SOUND_BOX = "036";
	
	/**
	 * 五路交流开关 
	 */
	public static final String FUN_TYPE_FIVE_SWITCH = "037";
	
	/**
	 * 无线智能空调插座
	 */
	public static final String FUN_TYPE_WIRELESS_AIRCODITION_OUTLET = "029";
	
	/**
	 * 红外转发
	 */
	public static final String FUN_TYPE_WIRELESS_INFRARED_TRANDPOND = "032";
	
	/**
	 * 三路开关
	 */
	public static final String FUN_TYPE_THREE_SWITCH_THREE = "047";
	public static final String FUN_TYPE_THREE_SWITCH_FOUR = "046";
	public static final String FUN_TYPE_THREE_SWITCH_FIVE = "045";
	public static final String FUN_TYPE_THREE_SWITCH_SIX = "044";
	public static final String[] FUN_TYPE_THREE_SWITCHES = {
			FUN_TYPE_THREE_SWITCH_THREE,FUN_TYPE_THREE_SWITCH_FOUR,
			FUN_TYPE_THREE_SWITCH_FIVE,FUN_TYPE_THREE_SWITCH_SIX};
	/**
	 * 射灯
	 */
	public static final String FUN_TYPE_SPOTLIGHT = "027";
	/**
	 * 单路开关
	 */
	public static final String FUN_TYPE_ONE_SWITCH = "053";

	/**
	 * 无线红外转发器
	 */
	public static final String FUN_TYPE_INFRARED_TRASPOND = "032";
	
}
