package com.jinxin.jxtouchscreen.constant;

import android.text.TextUtils;

import com.jinxin.jxtouchscreen.db.SPM;

/**
 * 关于网络的常量
 */
public class NetConstant {
	/* 默认访问地址 */
	public static final String HTTP_ACCESSPATH = "http://www.beonehome.com:6100/smarthome/mobile/service";

	public final static String HOST_INSPECT = "www.beonehome.com:6300";// 设备巡检暂时使用（云平台）数据分析服务器地址，后面由大网关代替

	public final static String HOST_ZEGBING = "www.beonehome.com:12307";// 设备巡检暂时使用（云平台）数据分析服务器地址，后面由大网关代替
	public static final String HTTP_ICON_PATH = "http://www.beonehome.com:6100/smarthome/";


	public static final String GATEWAY_PATH = HTTP_ICON_PATH + "zigbeeGatewayService/findStatus/";
	/**
	 * 界面过期需刷新
	 * 访问权限认证失败
	 */
	public static final String SERVER_ERROR_MSG_0101 = "0101";
	//用户已登录
	public static final String SERVER_ERROR_MSG_0115 = "0115";
	//	00	操作成功
	//	01	网关处于离线
	//	02	处理超时
	//	03	服务端业务处理失败
	//	04	未知异常
	public static final String SERVER_ERROR_MSG_00 = "00";
	public static final String SERVER_ERROR_MSG_01 = "01";
	public static final String SERVER_ERROR_MSG_02 = "02";

	// ///////////联网参数/////////////////////////////
	public static final String appURL = "tcp://192.168.60.211:3333";// 联网地址
	public static String wapGateway = "10.0.0.200:80";// 电信端口号
	public static String rootURL = "";// 固定URL部分
	public static boolean isCmWap = false;

	public static final int DEFAULT_CONNECT_TIME_OUT = 10000;
	public static final int DEFAULT_READ_TIME_OUT = 10000;

	/**
	 * 获取数据成功/设备操作开
	 */
	public final static String CONNECTION_SUCCESS = "0000";
	// 获取数据失败
	public final static String CONNECTION_ERROR = "0001";

	/**
	 * 业务编码
	 ***************************/
	public static final String BS_ACCOUNT_MANAGER = "B000";
	public static final String BS_USER_MANAGER = "B001";
	public static final String BS_USER_PATTERN = "B002";
	public static final String BS_USER_PRODUCT_MANAGER = "B004";
	public static final String BS_USER_PATTERN_MANAGER = "B005";
	public static final String BS_FUN_MANAGER = "B006";
	public static final String BS_UPGRADE_MANAGER = "B007";
	public static final String BS_FUN_CONFIG = "B008";
	public static final String BS_PRODUCT_STATE = "B010";// 设备状态列表
	public static final String BS_CUSTOMER_TIMER = "B012";// 定时任务列表
	public static final String BS_CUSTOMER_TIMER_OPERATION = "B013";// 定时任务操作列表
	public static final String BS_CUSTOMER_AREA_OPERATION = "B015";// 获取用户区域分组
	public static final String BS_CUSTOMER_PRODUCT_AREA_OPERATION = "B016";// 获取设备区域分组
	public static final String BS_CUSTOMER_DATA_SYNC_OPERATION = "B014";// 获取需要删除的数据
	public static final String BS_WAKE_WORD_BROWSE = "V019";//唤醒词查看
	// 网关注册信息更新
	public static final String BS_UPDATE_PRODUCT_REGISTER = "B018";
	public static final String BS_PRODUCT_REMOTE_CONFIG_OPERATION = "B019";// 获取设备区域分组
	public static final String BS_PRODUCT_REMOTE_CONFIG_FUN_OPERATION = "B020";// 获取需要删除的数据
	public static final String BS_ADD_SUGGEST_FEEDBACK = "B021";// 新增意见反馈信息
	public static final String BS_CLOUD_MUSIC_SINGER = "B024";// 音乐查询
	public static final String BS_CLOUD_MUSIC_STORE = "B025";// 音乐查询
	public static final String BS_DOOR_CONTACT_STATUS = "B027";// 门磁状态
	public static final String BS_DEVICE_TYPE = "B029";// 遥控设备类型
	public static final String BS_DEVICE_BRANDS = "B030";// 遥控设备品牌
	public static final String BS_OEM_VERSION = "B032";// OEM
	public static final String BS_ADD_CONNECTION_OPERATION = "B031";// 新增设备联动
	public static final String BS_ADD_GEO_LOCATION = "B033";// 新增设备联动
	public static final String BS_CODE_STATISTICS = "B034";
	public static final String INFRARED_TRANSPORT_TEST_BIPCODE = "B037";
	// 更新音乐播放列表
	public static final String BS_UPDATE_MUSIC_LIST = "B009";
	// 更新音乐播放列表
	public static final String TRD_UPDATE_MUSIC_LIST = "T901";
	// 云设置同步和更新
	public static final String BS_SYNC_CLOUD_SETTING = "B011";
	// 更新（获取）云同步设置
	public static final String TRD_GET_USER_CLOUND_SETTING = "T203";


	/**
	 * 交易编码
	 ***************************/
	// 账号登陆
	public static final String TRD_ACCOUNT_LOGIN = "T000";
	public static final String TR_CODE_STATISTICS = "T101";
	public static final String TR_UP_OPERATION = "T102";
	// 模式删除
	public static final String TRD_CUSTOMER_PATTERN_DELETE = "T103";
	// 用户设备列表
	public static final String TRD_USER_DEVICE = "T901";
	// 客户模式列表
	public static final String TRD_CUSTOMER_PATTERN_LIST = "T203";
	// 用户详情
	public static final String TRD_USER_DETAIL = "T204";
	// 获取密保信息
	public static final String TRD_SECRET_SECURITY = "T201";
	// 获取客户设备操作命令
	public static final String TRD_USER_DEVICE_COMMAND = "T902";
	// 密码修改
	public static final String TRD_CHANGE_PASSWORD = "T102";
	// 修改密保问题
	public static final String TRD_USER_SAFE_QUESITON = "T904";
	// 用户详情
	public static final String TRD_USER_INFO = "T903";
	// 唤醒词
	public static final String TRD_WAKE_WORD_BROWSE = "T203";
	/**
	 * 子账号列表key
	 */
	public final static String KEY_SUB_ACCOUNT = "key_sub_account";
	public final static String DM_ANDROID_PHONE = "A001";
	public final static String DM_HOME = "P000";
	public final static String BS_VER = "1.0";

	public final static String KEY_WARN_SHAKE = "warnShake";// 报警震动
	public final static String KEY_NOTICE_ON_OFF = "noticeOnOrOff";// 分时段报警开关

	/**
	 * 存储配置表key
	 *********************/

	public final static String KEY_ACCOUNT = "curr_ur";
	public static final String ORDINARY_CONSTANTS = "ordinary_constants";
	public static final String KEY_SECRETKEY = "secretKey";
	public final static String KEY_NICKNAME = "nickyName";
	public final static String KEY_SUNACCOUNT = "sunAccount";
	public final static String KEY_NOT_FIRST_LOGING = "notFirstLogin";// 是否第一次登录
	public final static String KEY_ACCOUNT_HISTORY = "accountList";// 账户登录历史
	public final static String KEY_FIRST_INSTALL = "firstInstall";// 是否首次安装
	/**
	 * 客户模式列表更新key
	 */
	public final static String KEY_CUSTOMER_PATTERN_LIST = "key_customer_pattern_list";
	/**
	 * 云设置key
	 */
	public final static String KEY_CLOUD_SETTING = "key_cloud_setting";
	/**
	 * 产品模式列表更新key
	 */
	public final static String KEY_PRODUCT_PATTERN_OPERATION_LIST = "key_product_pattern_operation_list";
	/**
	 * 用户设备更新key
	 */
	public final static String KEY_CUSTOMER_PRODUCT_LIST = "key_customer_product_list";
	/**
	 * 用户设备指令更新key
	 */
	public final static String KEY_CUSTOMER_PRODUCT_CMD_LIST = "key_customer_product_cmd_list";
	/**
	 * 客户详情更新key
	 */
	public final static String KEY_CUSTOMER_DETAIL_LIST = "key_customer_detail_list";
	/**
	 * // 唤醒模式
	 */
	public final static String KEY_WAKE_MODE = "keyWakeMode";
	/**
	 * 用户详情更新key
	 */
	public final static String KEY_USER_DETAIL_LIST = "key_user_detail_list";
	/**
	 * 客户模式指令列表更新key
	 */
	public final static String KEY_CUSTOMER_PATTERN_CMD_LIST = "KEY_CUSTOMER_PATTERN_CMD_LIST";
	/**
	 * 产品功能列表更新key
	 */
	public final static String KEY_PRODUCT_FUN_LIST = "key_product_fun_list";
	/**
	 * 产品功能详情列表更新key
	 */
	public final static String KEY_FUN_DETAIL_LIST = "key_fun_detail_list";
	/**
	 * 产品功能明显配置列表更新key
	 */
	public final static String KEY_PRODUCT_FUN_DETAIL_LIST = "key_product_fun_detail_list";
	/**
	 * 获取需要删除的数据
	 */
	public static final String TRD_CUSTOMER_DATA_SYNC = "T203";
	/**
	 * 删除数据
	 */
	public final static String KEY_CUSTOMER_DATA_SYNC = "key_customer_data_sync";
	/**
	 * 设备状态表更新key
	 */
	public final static String KEY_PRODUCT_STATE_LIST = "key_product_stae_list";
	/**
	 * 网关注册信息更新
	 */
	public final static String KEY_PRODUCT_REGISTER = "key_product_register";
	/**
	 * 用户区域分组
	 */
	public final static String KEY_CUSTOMER_AREA = "key_customer_area";
	/**
	 * 当前功放版本
	 */
	public final static String KEY_CURR_MUSIC_VERSION = "curr_music_version";
	/**
	 * 是否离线模式
	 */
	public final static String KEY_OFF_LINE_MODE = "offLineMode";
	/**
	 * 彩灯List
	 */
	public final static String KEY_COLOR_LIGHT_LIST = "key_color_light_list";
	// 为语音合成保存输入源
	public static final String BS_SAVE_INPUT_VOICE = "B009";
	// 为语音合成保存输入源
	public static final String TRD_SAVE_INPUT_VOICE = "T902";
	// 语音自动发送 or 点击发送
	public final static String KEY_VOICE_SEND_SWITCH = "sendSwitch";

	public final static String KEY_VEIN_ONLY = "veinOnly";// 只进行静脉纹登录
	/**
	 * 静脉纹开关
	 */
	public final static String KEY_ENABLE_PASSWORD_VEIN = "key_enable_password_vein";
	/**
	 * 设备区域分组
	 */
	public final static String KEY_CUSTOMER_PRODUCT_AREA = "key_customer_product_area";

	public final static String ACTION_REQUEST = "0";
	public final static String ACTION_RESPONSE = "1";

	public final static String TEST_TRUE = "1";
	public final static String TEST_FALSE = "0";

	//语音合成类型
	public static final String BS_UPDATE_PRODUCT_VOICE = "B022";
	//新增语音配置
	public static final String BS_UPDATE_PRODUCT_VOICE_CONFIGER = "B023";
	//语音转文字
	public static final String BS_UPDATE_VOICE_TO_TEXT = "B026";
	// 语音合成识别
	public static final String TRD_SET_VOICE_IDENTIFY = "T901";

	/**
	 * 默认更新时间
	 */
	public final static String DEFAULT_UPDATE_TIME = "20130808122323";


	/**
	 * 位置
	 */
	public static final String CITY = "current_city";
	public static final String ADDRESS = "current_address";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";

	public static final String HAVE_SECRET_SAFE = "have_secret_safe";
	public static final String SECRET_SAFE_JUMP = "secret_safe_jump_code";

	public final static String KEY_GATEWAYIP = "gatewayIP"; //更改的大网关ip
	public final static String KEY_GATEWAYPORT = "gatewayPort"; //更改的大网关端口

	public final static String KEY_ISLOADING = "isLoading";// 是否登录过应用
	public final static String KEY_PASSWORD = "share_key";
	public final static String KEY_ENABLE_OFFLINE_MODE = "offlineSwitch";// 打开离线切换

	public static final String GATEWAY_IP = "192.168.60.220";

	//获取更新的网关ip add by rp
	public static String hostFor485() {
		String gatewayIP = SPM.getStr(SPM.CONSTANT, KEY_GATEWAYIP, "");
		if (!TextUtils.isEmpty(gatewayIP)) {
			return gatewayIP + ":3965";
		}
		return HOST_INSPECT;
	}

	//获取更新的zigbee网关ip add by rp
	public static String hostForZigbee() {
		String gatewayIP = SPM.getStr(SPM.CONSTANT, KEY_GATEWAYIP, "");
		if (!TextUtils.isEmpty(gatewayIP)) {
			return gatewayIP + ":12305";
		}
		return HOST_ZEGBING;
	}
}
