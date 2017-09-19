package com.jinxin.jxtouchscreen.cmd;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.CustomerProduct;
import com.jinxin.jxtouchscreen.model.Device;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.FunDetailConfig;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductPatternOperation;
import com.jinxin.jxtouchscreen.model.ProductRegister;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XTER on 2016/12/27.
 * 命令组装
 */
public class CmdBuilder {

	private static class CmdBuilderHolder {
		private static final CmdBuilder INSTANCE = new CmdBuilder();
	}

	private CmdBuilder() {
	}

	public static CmdBuilder build() {
		return CmdBuilderHolder.INSTANCE;
	}


	/**
	 * 获取原始命令
	 *
	 * @param productFun 产品类
	 * @param funDetail  设备类
	 * @param params     命令参数
	 * @param type       命令类型
	 * @return List<String>
	 */
	protected List<String> generateOriginalCmd(ProductFun productFun,
	                                           FunDetail funDetail, Map<String, Object> params, String type) {
		if (productFun == null || funDetail == null) {
			L.e("某项参数为空");
			return Collections.emptyList();
		}

		String addr485 = null;

		// 获取funType
		String funType = productFun.getFunType();

		// 获取addr485
		List<CustomerProduct> cpList = DBM.getCurrentOrm().query(new QueryBuilder<>(CustomerProduct.class).where("whId = ?", new String[]{productFun.getWhId()}));
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

		String gatewayWhId = getGatewayWhIdByProductWhId(productFun.getWhId());

		List<String> funUnits = new ArrayList<>();
		funUnits.add(productFun.getFunUnit());

		// 生成原始命令
		return CmdOriginGenerator.createCmdWithNotAccount(gatewayWhId, addr485, type, funUnits, funType, params);
	}

	/**
	 * 寻找网关
	 *
	 * @param whid 某产品
	 * @return gatewayWhId
	 */
	public String getGatewayWhIdByProductWhId(String whid) {
		List<ProductRegister> productList = DBM.getCurrentOrm()
				.query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{whid}));
		if (productList != null && productList.size() > 0) {
			ProductRegister pr = productList.get(0);
			//查询本身为网关的设备
			List<ProductRegister> gatewayList = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{pr.getGatewayWhId()}));
			if (gatewayList.size() > 0) {
				return gatewayList.get(0).getGatewayWhId();
			} else {
				L.e("无法找到对应网关注册信息");
			}
		} else {
			L.e("无法找到设备注册信息");
		}
		return "";
	}

	/**
	 * 获取无线网关的whid
	 */
	public List<String> getZegbingGatewayWhid() {
		// 获取无线网关的whid
		List<String> whIds = new ArrayList<String>();
		List<FunDetailConfig> fdcList = DBM.getCurrentOrm().query(new QueryBuilder<>(FunDetailConfig.class)
				.where("funType=?", new String[]{"G102"}));
		if (fdcList.size() > 0) {
			for (int i = 0; i < fdcList.size(); i++) {
				whIds.add(fdcList.get(i).getWhId());
			}
		}
		return whIds;
	}

	/**
	 * 获取网关地址
	 *
	 * @param whid 产品id
	 * @return
	 */
	public String getAddressByProductWhId(String whid) {
		List<CustomerProduct> list = DBM.getCurrentOrm().query(new QueryBuilder<>(CustomerProduct.class).where("whId = ?", new String[]{whid}));
		if (list != null && list.size() > 0) {
			return list.get(0).getAddress485();
		}
		return null;
	}

	/**
	 * 合并命令
	 */
	protected byte[] combineCmd(String prefix, String splitor, List<String> cmdArr) {
		if (cmdArr == null || cmdArr.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(splitor);
		for (String cmd : cmdArr) {
			sb.append(cmd);
			sb.append(splitor);
		}
		// 组拼完成后，删除最后一个字符(",")
		sb.deleteCharAt(sb.length() - 1);
		try {
			int len = sb.toString().getBytes("utf-8").length;
			ByteBuffer bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1);
			bbf.putInt(len);
			bbf.put(sb.toString().getBytes("utf-8"));
			bbf.flip();
			return bbf.array();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 生成带长度的指令(带账号)
	 *
	 * @param customerId 用户id
	 * @param addr485    设备的485地址
	 * @param operation  操作
	 * @param funUnits   设备的funUnits
	 * @param funType    设备的funType
	 * @param params     生成命令需要的参数
	 * @return 生成的命令
	 */
	public List<byte[]> createCmdWithLength(String customerId,
	                                        String addr485, String operation, List<String> funUnits,
	                                        String funType, Map<String, Object> params) {

		List<byte[]> byteList = new ArrayList<>();

		// 在线模式，返回平台命令
		List<String> _cmdList = CmdOriginGenerator.createCmdWithAccount(customerId, addr485,
				operation, funUnits, funType, params);
		try {
			if (_cmdList != null) {

				for (int i = 0; i < _cmdList.size(); i++) {
					int len;
					String _cmd = _cmdList.get(i).toString();
					L.d("cmd: " + _cmd);

					len = _cmd.getBytes("utf-8").length;

					ByteBuffer bbf = ByteBuffer.allocate(len + 8);
					bbf.putInt(1);
					bbf.putInt(len);
					bbf.put(_cmd.getBytes("utf-8"));
					bbf.flip();
					byteList.add(bbf.array());
//					byteList.add("\r\n".getBytes("utf-8"));
					L.d("send cmd: " + new String(bbf.array()));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return byteList;
	}

	/**
	 * 组合命令
	 *
	 * @param account 账号或地址
	 * @param _cmd    命令
	 * @return cmd
	 */
	public List<byte[]> createCmdWithLength(String account,
	                                        String _cmd) {
		List<byte[]> byteList = new ArrayList<>();
		StringBuffer sb = new StringBuffer(account);
		sb.append(_cmd);
		String cmd = sb.toString();
		try {
			L.d("cmd: " + _cmd);
			int len = cmd.getBytes("utf-8").length;
			ByteBuffer bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1);
			// 命令或响应类型，0=web向平台发送执行命令请求/应答，
			// 1=手机向平台发送执行命令请求/应答,
			// 2= web向平台发送查询命令请求/应答，
			// 3=手机向平台发送查询命令请求/应答，
			// 4=平台主动向终端推送消息
			bbf.putInt(len);
			bbf.put(cmd.getBytes("utf-8"));
			bbf.flip();
			byteList.add(bbf.array());
			L.d("send cmd: " + new String(bbf.array()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return byteList;
	}

	/**
	 * 为原始命令增加长度和头信息
	 */
	protected byte[] generateCmdWithPrefixAndLength(String prefix, String splitor, String cmd) {
		StringBuffer sb = new StringBuffer(prefix);
		sb.append(splitor);
		sb.append(cmd);
		try {
			int len = sb.toString().getBytes("utf-8").length;
			ByteBuffer bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1);
			bbf.putInt(len);
			bbf.put(sb.toString().getBytes("utf-8"));
			bbf.flip();
			return bbf.array();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	/**
	 * 生成命令1
	 *
	 * @param productFun 产品类
	 * @param params     命令参数
	 * @param type       命令操作类型
	 * @return cmd
	 */
	public List<byte[]> generateCmd1(ProductFun productFun, Map<String, Object> params, String type) {
		if (params == null || params.size() < 1) {
			params = new HashMap<>();
			params.put(StaticConstant.PARAM_INDEX, productFun.getFunUnit());
		}
		List<String> funUnits = new ArrayList<>();
		funUnits.add(productFun.getFunUnit());
		String addr485 = getAddressByProductWhId(productFun.getWhId());
		/********2016-01-26 修改多网关指令*********/
		String whId485 = getGatewayWhIdByProductWhId(productFun.getWhId());
		/*************END**********************/
		return createCmdWithLength(whId485,
				addr485, type, funUnits, productFun.getFunType(), params);
	}

	/**
	 * 生成命令2
	 *
	 * @param funDetail  设备类
	 * @param productFun 产品类
	 * @param params     命令参数
	 * @param type       命令操作类型
	 * @return cmd
	 */
	public List<byte[]> generateCmd2(ProductFun productFun, FunDetail funDetail, Map<String, Object> params, String type) {
		List<String> originalCmdList = generateOriginalCmd(productFun, funDetail, params, type);
		List<byte[]> cmdList = new ArrayList<>();
		String gatewayWhId = getGatewayWhIdByProductWhId(productFun.getWhId());
		if (TextUtils.isEmpty(gatewayWhId)) {
			ToastUtil.showShort(BaseApp.getContext(), "设备网关注册信息异常");
			return null;
		}
		byte[] cmdBytes = combineCmd(gatewayWhId, ",", originalCmdList);
		if (cmdBytes != null) {
			cmdList.add(cmdBytes);
		}
		return cmdList;
	}

	/**
	 * 生成命令3
	 *
	 * @param funDetail  设备类
	 * @param productFun 产品类
	 * @param params     命令参数
	 * @param type       命令操作类型
	 * @return cmd
	 */
	public List<byte[]> generateCmd3(ProductFun productFun, FunDetail funDetail, Map<String, Object> params, String type) {
		List<String> originalCmdList = generateOriginalCmd(productFun, funDetail, params, type);
		StringBuilder sb = new StringBuilder();
		if (originalCmdList != null && originalCmdList.size() > 0) {
			for (int i = 0; i < originalCmdList.size(); i++) {
				sb.append("|");
				sb.append(originalCmdList.get(i));
			}
		} else {
			return null;
		}
		String whId485 = getGatewayWhIdByProductWhId(productFun.getWhId());
		return createCmdWithLength(whId485, sb.toString());
	}

	/**
	 * 无线巡检专用命令
	 *
	 * @param device
	 * @param params
	 * @param type
	 * @return
	 */
	private static List<String> generateOriginalCmd(Device device, Map<String, Object> params,
	                                                String type) {
		if (device == null) {
			L.e("parameter error: productFun or funDetail is null");
			return Collections.emptyList();
		}

		String customerId = DBM.getCustomerId();

		String addr485 = device.getAddress485();

		String funType = device.getFunType();

		// 生成原始命令
		return CmdOriginGenerator.createCmdWithNotAccount(customerId, addr485, type, null, funType, params);
	}

	/**
	 * 生成命令--无线巡检
	 *
	 * @param device 巡检专用
	 * @param params 命令参数
	 * @param type   命令类型
	 * @return
	 */
	public List<byte[]> generateCmdDetectForZg(Device device, Map<String, Object> params, String type) {
		List<String> originalCmdList = generateOriginalCmd(device, params, type);
		List<byte[]> cmdList = new ArrayList<>();
		String gatewayWhId = device.getGatewayId();
		if (TextUtils.isEmpty(gatewayWhId)) {
			ToastUtil.showShort(BaseApp.getContext(), "设备网关注册信息异常");
			return null;
		}
		byte[] cmdBytes = combineCmd(gatewayWhId, ",", originalCmdList);
		if (cmdBytes != null) {
			cmdList.add(cmdBytes);
		}
		return cmdList;
	}

	/**
	 * 生成命令--有线巡检
	 *
	 * @param whId 设备id
	 * @param code 参数
	 * @return cmd
	 */
	public List<byte[]> generateCmdDetectFor485(String whId, String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.OPERATE_SET_TYPE, code);

		String type = "inspection";
		String addr485 = getAddressByProductWhId(whId);

		/********2016-01-27 修改多网关指令*********/
		String whId485 = getGatewayWhIdByProductWhId(whId);
		/*************END**********************/
		return createCmdWithLength(whId485,
				addr485, type, null, ProductConstants.FUN_TYPE_DEVICE_DETECT, params);
	}

	/**
	 * 根据产品类型归纳生成命令
	 *
	 * @param funDetail  设备类
	 * @param productFun 产品类
	 * @param params     命令参数
	 * @param type       命令操作类型
	 * @return cmd
	 */
	public List<byte[]> generateCmd(ProductFun productFun, FunDetail funDetail, Map<String, Object> params, String type) {
		if (productFun == null || funDetail == null)
			return new ArrayList<>();
		switch (productFun.getFunType()) {
			//灯光
			case ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT:
				return generateCmd1(productFun, params, type);
			//有线窗帘
			case ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN:
				return generateCmd1(productFun, params, type);
			//有线锁
			case ProductConstants.FUN_TYPE_AUTO_LOCK:
				return generateCmd1(productFun, params, type);
			//环境监测
			case ProductConstants.FUN_TYPE_ENV:
			case ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI:
				return generateCmd1(productFun, params, type);
			//无线锁
			case ProductConstants.FUN_TYPE_ZG_LOCK:
				return generateCmd2(productFun, funDetail, params, type);
			//彩灯、吸顶灯、球泡灯、无线射灯、灯带
			case ProductConstants.FUN_TYPE_COLOR_LIGHT:
			case ProductConstants.FUN_TYPE_CEILING_LIGHT:
			case ProductConstants.FUN_TYPE_POP_LIGHT:
			case ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT:
			case ProductConstants.FUN_TYPE_LIGHT_BELT:
			case ProductConstants.FUN_TYPE_CRYSTAL_LIGHT:
				return generateCmd2(productFun, funDetail, params, type);
			//无线插座
			case ProductConstants.FUN_TYPE_WIRELESS_SOCKET:
				return generateCmd2(productFun, funDetail, params, type);
			//无线网关
			case ProductConstants.FUN_TYPE_WIRELESS_GATEWAY:
				return generateCmd2(productFun, funDetail, params, type);
			//无线窗帘
			case ProductConstants.FUN_TYPE_WIRELESS_CURTAIN:
				return generateCmd2(productFun, funDetail, params, type);
			//双路开关
			case ProductConstants.FUN_TYPE_DOUBLE_SWITCH:
				return generateCmd2(productFun, funDetail, params, type);
			//五路交流开关
			case ProductConstants.FUN_TYPE_FIVE_SWITCH:
				return generateCmd2(productFun, funDetail, params, type);
			//无线空调插座
			case ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET:
				return generateCmd2(productFun, funDetail, params, type);
			//二路一到六键
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE:
				return generateCmd2(productFun, funDetail, params, type);
			//安防设备
			case ProductConstants.FUN_TYPE_GAS_SENSE:
			case ProductConstants.FUN_TYPE_INFRARED:
			case ProductConstants.FUN_TYPE_SMOKE_SENSE:
			case ProductConstants.FUN_TYPE_DOOR_CONTACT:
				return generateCmd2(productFun, funDetail, params, type);
			//三路三到六键
			case ProductConstants.FUN_TYPE_THREE_SWITCH_THREE:
			case ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR:
			case ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE:
			case ProductConstants.FUN_TYPE_THREE_SWITCH_SIX:
				return generateCmd2(productFun, funDetail, params, type);
			//无线红外转发
			case ProductConstants.FUN_TYPE_INFRARED_TRASPOND:
				return generateCmd2(productFun, funDetail, params, type);
			//单路交流开关
			case ProductConstants.FUN_TYPE_ONE_SWITCH:
				return generateCmd2(productFun, funDetail, params, type);
			//无线空调
			case ProductConstants.FUN_TYPE_AIRCONDITION:
				return generateCmd2(productFun, funDetail, params, type);
			//功放
			case ProductConstants.FUN_TYPE_POWER_AMPLIFIER:
			case ProductConstants.POWER_AMPLIFIER_MUTE:
			case ProductConstants.POWER_AMPLIFIER_UNMUTE:
			case ProductConstants.POWER_AMPLIFIER_PROVIOUS:
			case ProductConstants.POWER_AMPLIFIER_NEXT:
			case ProductConstants.POWER_AMPLIFIER_SOUND_ADD:
			case ProductConstants.POWER_AMPLIFIER_SOUND_SUB:
			case ProductConstants.POWER_AMPLIFIER_SOUND_LIST:
			case ProductConstants.POWER_AMPLIFIER_SOUND_SONG:
			case ProductConstants.POWER_AMPLIFIER_SOUND_PLAY:
			case ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE:
			case ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG:
			case ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL:
			case ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE:
			case ProductConstants.POWER_AMPLIFIER_DEVICE_LINK:
			case ProductConstants.POWER_AMPLIFIER_SET_VOLUME:
			case ProductConstants.POWER_AMPLIFIER_GET_VOLUME:
			case ProductConstants.POWER_AMPLIFIER_PLAY_STATUS:
			case ProductConstants.POWER_AMPLIFIER_GET_VERSION:
			case ProductConstants.POWER_AMPLIFIER_GET_BIND:
			case ProductConstants.POWER_AMPLIFIER_GET_MUTE_STATUS:
				return generateCmd3(productFun, funDetail, params, type);
		}
		return null;
	}

	private String getCmdLength(String content) {
		String result = "";
		if (content.length() < 10) {
			result = "0" + content.length();
		} else {
			result = String.valueOf(content.length());
		}
		return result;
	}

	public List<Command> generateModeCommand(int patternId) {
		List<Command> commandList = new ArrayList<>();
		List<ProductPatternOperation> productPatternOperations = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductPatternOperation.class).where("patternId = ?", new String[]{String.valueOf(patternId)}));

		// 将模式中的设备按网关类型（无线网关-zegbing网关/有线网关-普通网关)分组
		Map<String, List<ProductPatternOperation>> cmdMap = new HashMap<>();
		for (int i = 0; i < productPatternOperations.size(); i++) {
			String sn = getGatewayWhIdByProductWhId(productPatternOperations.get(i).getWhId());
			if (!cmdMap.containsKey(sn)) {
				cmdMap.put(sn, new ArrayList<ProductPatternOperation>());
			}
			cmdMap.get(sn).add(productPatternOperations.get(i));

		}

		// 将模式中的设备按网关类型（无线网关-zegbing网关/有线网关-普通网关)分组
		List<String> zigbeeGatewayWhIds = getZegbingGatewayWhid();
		Set<String> keys = cmdMap.keySet();
		Iterator<String> iterator = keys.iterator();
		List<Command> zigCommands = new ArrayList<Command>();
		List<Command> onlineCommands = new ArrayList<Command>();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (zigbeeGatewayWhIds.contains(key)) {
				// Zigbee网关下的设备
				List<byte[]> cmdList = generateZigbeeDeviceCmdInPattern(cmdMap.get(key));
				Command command = new Command(key, cmdList);
				command.setGatewayRemoteIp(NetConstant.hostForZigbee());
				zigCommands.add(command);
				command.setZigbee(true);
			} else {
				// 普通网关下的设备
				List<byte[]> cmdList = productPatternOperationToCMD_V1(cmdMap.get(key), null);
				Command command = new Command(key, cmdList);
				command.setGatewayRemoteIp(NetConstant.hostFor485());
				onlineCommands.add(command);
				command.setZigbee(false);
			}
		}
		commandList.addAll(zigCommands);
		commandList.addAll(onlineCommands);

		return commandList;
	}


	/**
	 * 模式操作命令生成
	 *
	 * @param ppoList 模式操作列表
	 * @param params  生成命令需要的参数
	 * @return 生成的命令
	 */
	public List<byte[]> productPatternOperationToCMD_V1(List<ProductPatternOperation> ppoList, Map<String, Object> params) {
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1) {
			L.e("模式内容为空");
			return Collections.emptyList();
		}

		// 对模式控制操作做对于控制单元的分组（根据whid设备）
		Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<>();

		for (ProductPatternOperation ppo : ppoList) {
			String key = ppo.getWhId();
			if (groupedOperation.containsKey(key)) {
				groupedOperation.get(key).add(ppo);
			} else {
				groupedOperation.put(key,
						new ArrayList<ProductPatternOperation>());
				groupedOperation.get(key).add(ppo);
			}
		}

		// 对分组后的模式操作按控制单元生成命令
		StringBuilder sb = new StringBuilder();
		List<byte[]> result = new ArrayList<>();
		Set<String> keySet = groupedOperation.keySet();
		if (keySet.size() > 0) {
			for (String key : keySet) {
				List<ProductPatternOperation> groupList = groupedOperation
						.get(key);
				String cmdOriginal = generateOneGroudOperationToCMD(groupList);
				sb.append(cmdOriginal);
			}
		}

		String cmdStr = sb.toString();

		/********2016-01-26 修改多网关指令*********/
		String whId485 = getGatewayWhIdByProductWhId(ppoList.get(0).getWhId());
		/*************END**********************/

		result = createCmdWithLength(whId485, cmdStr);

		return result;
	}

	/**
	 * 针对功能单元生成命令(zj说明：只针对普通网关在线使用)
	 *
	 * @param ppoList
	 * @return
	 */
	public String generateOneGroudOperationToCMD(List<ProductPatternOperation> ppoList) {
		// 获取whid
		String whId = ppoList.get(0).getWhId();

		String address485 = getAddressByProductWhId(whId);
		if (TextUtils.isEmpty(address485)) {
			return null;
		}

		// 获取customerid
		String customerId = DBM.getCustomerId();
		if (TextUtils.isEmpty(customerId)) {
			return null;
		}

		// 获取funType
		String funType = null;

		// 生成针对于控制单元的命令
		String result = null;
		List<String> openList = new ArrayList<String>();
		List<String> closeList = new ArrayList<String>();
		List<String> setList = new ArrayList<String>();
		Map<String, Object> musicMap = new HashMap<String, Object>();
		List<String> upList = new ArrayList<String>();
		List<String> downList = new ArrayList<String>();
		List<String> securityList = new ArrayList<String>();
		List<String> redList = new ArrayList<String>();
		Map<String, Object> popLightList = new HashMap<String, Object>();
		List<String> wirelessSocketOnList = new ArrayList<String>();
		List<String> wirelessSocketOffList = new ArrayList<String>();

		for (ProductPatternOperation ppo : ppoList) {
			ProductFun pf = DBM.getProductFunByFunId(ppo.getFunId());
			if (funType == null) {
				funType = pf.getFunType();
			}

			if (ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT
					.equals(funType) ||
					funType.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) { // 电灯
				if ("open".equals(ppo.getOperation())) {
					openList.add(pf.getFunUnit());
				} else if ("close".equals(ppo.getOperation())) {
					closeList.add(pf.getFunUnit());
				}
			} else if (ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(funType)) { // 彩灯
				if ("set".equals(ppo.getOperation())) {
					setList.add(ppo.getParaDesc());
				} else if ("close".equals(ppo.getOperation())) {
					setList.add("000000000000");
				}
			} else if (ProductConstants.FUN_TYPE_CURTAIN.equals(funType)) {
				if ("open".equals(ppo.getOperation())) {
					upList.add(pf.getFunUnit());
				} else if ("close".equals(ppo.getOperation())) {
					downList.add(pf.getFunUnit());
				}
			} else if (ProductConstants.FUN_TYPE_POWER_AMPLIFIER
					.equals(funType)) { // 功放
				String desc = ppo.getParaDesc();
				if (!TextUtils.isEmpty(desc)) {
					try {
						JSONObject jo = JSON.parseObject(desc);
						if ("play".equals(ppo.getOperation()) || "playByLushu".equals(ppo.getOperation())) {
							musicMap.put("operation",
									ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(
										StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(
										StaticConstant.PARAM_MUSIC_SELECT_INPUT,
										input);
								musicMap.put(
										StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("pause".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(
										StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(
										StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("preSong".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_PROVIOUS);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("nextSong".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_NEXT);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("soundAdd".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_SOUND_ADD);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("soundSub".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_SOUND_SUB);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("muteSingle".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_MUTE);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("unmuteSingle".equals(ppo.getOperation())) {
							musicMap.put("operation", ProductConstants.POWER_AMPLIFIER_UNMUTE);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (TextUtils.isEmpty(selectLine)
									|| TextUtils.isEmpty(input)) {
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}

					} catch (JSONException e) {
						musicMap.clear();
					}
				} else {
					musicMap.clear();
				}
			} else if (ProductConstants.FUN_TYPE_SECUTIRY.equals(funType)) {
				if ("open".equals(ppo.getOperation())) {
					securityList.add(String.valueOf((pf.getFunId())));
					securityList.add(StaticConstant.OPERATE_OPEN_SECURITY);
				} else if ("close".equals(ppo.getOperation())) {
					securityList.add(String.valueOf((pf.getFunId())));
					securityList.add(StaticConstant.OPERATE_CLOSE_SECURITY);
				}

			} else if (ProductConstants.FUN_TYPE_UFO1.equals(funType) ||
					ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2.equals(funType)) {
				if ("send".equals(ppo.getOperation())) {
					redList.add(pf.getFunUnit());
				}
			} else if (ProductConstants.FUN_TYPE_POP_LIGHT.equals(funType)) {
				String desc = ppo.getParaDesc();
				if (!TextUtils.isEmpty(desc)) {

				}
			} else if (ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funType) ||
					ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funType)) {
				if ("off".equals(ppo.getOperation())) {
					wirelessSocketOffList.add(pf.getFunUnit());
				} else if ("on".equals(ppo.getOperation())) {
					wirelessSocketOnList.add(pf.getFunUnit());
				}
			}// zj:普通网关在线控制，如果需要对模式中其他类型的设备做扩展，在这里添加代码 else if()...
		}

		StringBuilder _sb = new StringBuilder();
		if (openList.size() > 0) {
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, "open", openList, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (closeList.size() > 0) {
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, "close", closeList, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (setList.size() > 0) {
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, StaticConstant.OPERATE_SET_COLOR, setList,
					funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (musicMap.size() > 0) {
			String operation = (String) musicMap.get("operation");
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, operation, null, operation, musicMap);
			if (cmd != null && cmd.size() > 0) {
				// _sb.append("|" + cmd.get(0));
				for (String c : cmd) {
					_sb.append("|" + c);
				}
			}
		}
		if (upList.size() > 0) {
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, StaticConstant.OPERATE_UP, upList, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (downList.size() > 0) {
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, StaticConstant.OPERATE_DOWN, downList, funType,
					null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (securityList.size() > 1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(StaticConstant.PARAM_FUN_ID, securityList.get(0));
			List<String> cmd = CmdOriginGenerator
					.createCmdWithNotAccount(customerId, address485,
							securityList.get(1), securityList, funType, map);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (redList.size() > 0) {
			for (int i = 0; i < redList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(StaticConstant.PARAM_INDEX, redList.get(i));
				List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
						address485, null, securityList, funType, map);
				if (cmd != null && cmd.size() > 0) {
					_sb.append("|" + cmd.get(0));
				}
			}
		}
		if (wirelessSocketOffList.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, "off", securityList, funType, map);
			if (cmd != null && cmd.size() > 0) {
				for (String c : cmd) _sb.append("|" + c);
			}
		}
		if (wirelessSocketOnList.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dst", "0x01");
			map.put("src", "0x01");
			List<String> cmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
					address485, "on", securityList, funType, map);
			if (cmd != null && cmd.size() > 0) {
				for (String c : cmd) _sb.append("|" + c);
			}
		}

		result = _sb.toString();
		return result;
	}

	/**
	 * 模式中所有指令
	 *
	 * @param ppoList 模式
	 * @return cmd
	 */
	protected List<byte[]> generateZigbeeDeviceCmdInPattern(List<ProductPatternOperation> ppoList) {
		List<byte[]> cmdList = new ArrayList<byte[]>();
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1) {
			L.e("模式内容为空");
			return cmdList;
		}
		// 对模式控制操作做对于控制单元的分组（根据whid设备）
		Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<>();

		for (ProductPatternOperation ppo : ppoList) {
			String key = ppo.getWhId();
			if (groupedOperation.containsKey(key)) {
				groupedOperation.get(key).add(ppo);
			} else {
				groupedOperation.put(key,
						new ArrayList<ProductPatternOperation>());
				groupedOperation.get(key).add(ppo);
			}
		}

		// 对分组后的模式操作按控制单元生成命令
		List<byte[]> result = new ArrayList<>();
		Set<String> keySet = groupedOperation.keySet();
		if (keySet.size() > 0) {
			for (String key : keySet) {
				List<ProductPatternOperation> groupList = groupedOperation
						.get(key);
				String cmdOriginal = generateZigbeeOneGroudOperationToCMD(groupList);
				String zegbingWhId = getGatewayWhIdByProductWhId(groupList.get(0).getWhId());
				if (!TextUtils.isEmpty(cmdOriginal)) {
					cmdList.add(generateCmdWithPrefixAndLength(zegbingWhId, ",", cmdOriginal));
				} else {
					L.e("无法生成模式原始指令");
				}
			}
		}
		return cmdList;
	}

	/**
	 * 将一个zg设备的指令合并成一条（在线使用）
	 *
	 * @param ppoList 一个zg设备的多条operation
	 * @return
	 */
	protected String generateZigbeeOneGroudOperationToCMD(List<ProductPatternOperation> ppoList) {
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1) {
			L.e("模式内容 为空");
			return null;
		}
		StringBuilder _sb = new StringBuilder();
		for (ProductPatternOperation ppo : ppoList) {
			// 获取whid
			String whId = ppo.getWhId();
			if (TextUtils.isEmpty(whId)) {
				return null;
			}

			String funType = DBM.getFunTypeByProductWhId(whId);
			String address485 = getAddressByProductWhId(whId);

			if (TextUtils.isEmpty(address485) || TextUtils.isEmpty(funType)) {
				L.e("address485 或 funtype 为空");
				return null;
			}

			// 获取customerid
			String customerId = DBM.getCustomerId();
			if (TextUtils.isEmpty(customerId)) {
				return null;
			}

			List<String> originalCmd = new ArrayList<>();
			// 双向插座需要生成2次原始指令
			if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funType) ||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funType) ||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funType) ||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funType) ||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funType) ||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(funType) ||
					ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funType)) {
				JSONObject jb;
				try {
					jb = JSON.parseObject(ppo.getParaDesc());
					String key;
					String value;
					String leftType = "";
					String rightType = "";
					for (Map.Entry<String, Object> entry : jb.entrySet()) {
						key = entry.getKey();
						value = (String) entry.getValue();
						if ("left".equals(key)) {
							leftType = value;
						} else if ("right".equals(key)) {
							rightType = value;
						} else if (StaticConstant.TYPE_SOCKET_KEY1.equals(key)) {
							leftType = value;
						} else if (StaticConstant.TYPE_SOCKET_KEY2.equals(key)) {
							rightType = value;
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					if (!TextUtils.isEmpty(leftType)) {// 生成左开关指令
						params.put("src", "0x01");
						params.put("dst", "0x01");
						List<String> leftCmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
								address485, leftType, null, funType,
								params);
						originalCmd.addAll(leftCmd);
					}
					if (!TextUtils.isEmpty(rightType)) {// 生成右开关指令
						params.put("src", "0x01");
						params.put("dst", "0x02");
						List<String> rightCmd = CmdOriginGenerator.createCmdWithNotAccount(customerId,
								address485, rightType, null, funType,
								params);
						originalCmd.addAll(rightCmd);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(funType)) {
				JSONObject jb;
				try {
					jb = JSON.parseObject(ppo.getParaDesc());
					String key;
					String value;
					String[] switchStatusStr = new String[StaticConstant.FIVE_KEY_MAP.length];
					for (Map.Entry<String, Object> entry : jb.entrySet()) {
						key = entry.getKey();
						value = (String) jb.get(key);
						for (int i = 0; i < StaticConstant.FIVE_KEY_MAP.length; i++) {
							if (StaticConstant.FIVE_KEY_MAP[i].equals(key)) {
								switchStatusStr[i] = value;
							}
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					for (int i = 0; i < switchStatusStr.length; i++) {
						if (!TextUtils.isEmpty(switchStatusStr[i])) {
							params.put("src", "0x01");
							params.put("dst", StaticConstant.FIVE_DST_MAP[i]);
							List<String> fiveCmd = CmdOriginGenerator
									.createCmdWithNotAccount(customerId,
											address485, switchStatusStr[i], null, funType,
											params);
							originalCmd.addAll(fiveCmd);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(funType) ||
					ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(funType) ||
					ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(funType) ||
					ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(funType)) {
				JSONObject jb;
				try {
					jb = JSON.parseObject(ppo.getParaDesc());
					String key;
					String value;
					String[] switchStatusStr = null;
					for (int i = 0; i < ProductConstants.FUN_TYPE_THREE_SWITCHES.length; i++) {
						if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCHES[i]))
							switchStatusStr = new String[i + 3];
					}
					for (Map.Entry<String, Object> entry : jb.entrySet()) {
						key = entry.getKey();
						value = (String) entry.getValue();
						for (int i = 0; i < StaticConstant.TYPE_SOCKET_KEYS.length; i++) {
							if (StaticConstant.TYPE_SOCKET_KEYS[i].equals(key)) {
								switchStatusStr[i] = value;
							}
						}
					}
					Map<String, Object> params = new HashMap<String, Object>();
					for (int i = 0; i < switchStatusStr.length; i++) {
						if (!TextUtils.isEmpty(switchStatusStr[i])) {
							params.put("src", "0x01");
							params.put("dst", StaticConstant.TYPE_DST_MAP[i]);
							List<String> threeCmd = CmdOriginGenerator
									.createCmdWithNotAccount(customerId,
											address485, switchStatusStr[i], null, funType,
											params);
							originalCmd.addAll(threeCmd);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funType)) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("src", "0x01");
				params.put("dst", "0x01");
				params.put("type", "00 32");
				params.put("op", "0C");
				String code = ppo.getParaDesc();
				StringBuffer buffer = new StringBuffer(code);
				int count = 0;
				for (int i = 2; i < code.length(); i += 2) {
					buffer.insert(i + count, " ");
					count += 1;
				}
				params.put(StaticConstant.PARAM_TEXT, buffer.toString() + " ");
				List<String> infrCmd = CmdOriginGenerator.createCmdWithNotAccount(customerId, address485, "commonCmd",
						null, funType, params);
				originalCmd.addAll(infrCmd);
			} else {// 其他设备生成原始命令
				if ("autoMode".equals(ppo.getOperation())
						|| "automode".equals(ppo.getOperation())) {// 炫彩灯光模式指令生成

					List<String> openCmd = CmdOriginGenerator
							.createCmdWithNotAccount(customerId, address485,
									"mvToLevel", null, funType,
									openColorLight());
					originalCmd.addAll(openCmd);

					List<String> autoCmd = CmdOriginGenerator
							.createCmdWithNotAccount(
									customerId,
									address485,
									"autoMode",
									null,
									funType,
									parseZigbeeDeviceParams4AutoMode(funType,
											ppo.getParaDesc()));
					originalCmd.addAll(autoCmd);
				} else {
					originalCmd = CmdOriginGenerator
							.createCmdWithNotAccount(
									customerId,
									address485,
									ppo.getOperation(),
									null,
									funType,
									parseZigbeeDeviceParams(funType, ppo));
				}
			}
			// 合并成一条
			if (originalCmd != null && originalCmd.size() > 0) {
				for (String _cmd : originalCmd)
					_sb.append(",").append(_cmd);
			}
		}
		String tempCmds = _sb.toString();
		if (tempCmds.length() > 1)
			tempCmds = tempCmds.substring(1);
		return tempCmds;
	}

	protected Map<String, Object> parseZigbeeDeviceParams(String funType, ProductPatternOperation ppo) {
		Map<String, Object> params = new HashMap<>();
		if (ppo == null) return params;

		String paramsDesc = ppo.getParaDesc();
		if (ProductConstants.FUN_TYPE_POP_LIGHT.equals(funType) ||
				ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funType) ||
				ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funType) ||
				ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funType) ||
				ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT.equals(funType)) {
			if (TextUtils.isEmpty(paramsDesc)) return params;
			try {
				JSONObject jb = JSON.parseObject(paramsDesc);
				params.put("hue", StringUtils.integerToHexString(jb.getIntValue("hue")));
				params.put("sat", StringUtils.integerToHexString(jb.getIntValue("sat")));
				params.put("light", StringUtils.integerToHexString(jb.getIntValue("light")));
				params.put("time", StringUtils.integerToHexString(1));
				params.put("src", "0x01");
				params.put("dst", "0x01");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funType) ||
				ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funType) ||
				ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(funType)) {
			params.put("src", "0x01");
			params.put("dst", "0x01");
		} else if (ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funType)) {
			params.put("src", "0x01");
			String units = DBM.getFunUnitByFunId(ppo.getFunId());
			if (!TextUtils.isEmpty(units)) params.put("dst", units);
			else params.put("dst", "1");
		} else if (ProductConstants.FUN_TYPE_ZG_LOCK.equals(funType)) {
			params.put("op", "C2");
			params.put("src", "0x01");
			params.put("dst", "0x01");
		}
		return params;
	}

	protected Map<String, Object> openColorLight() {
		/* 操作参数  */
		Map<String, Object> params = new HashMap<String, Object>();
		/* 亮度 */
		params.put("light", StringUtils.integerToHexString(100));
		/* 操作时间  */
		params.put("time", StringUtils.integerToHexString(1));
		/* 设备目标地址  */
		params.put("src", "0x01");
		/* 亮度关*/
		params.put("lightColor", StringUtils.integerToHexString(0));
		/* 关的时间 */
		params.put("timeColor", StringUtils.integerToHexString(1));
		/* 设备关的目标（白灯）*/
		params.put("dstColor", "0x02");

		params.put("dst", "0x01");

		return params;
	}

	/**
	 * 炫彩灯光设备参数
	 *
	 * @param funType
	 * @param paramsDesc
	 * @return
	 */
	protected Map<String, Object> parseZigbeeDeviceParams4AutoMode(String funType, String paramsDesc) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hue", StringUtils.integerToHexString(0));
		params.put("sat", StringUtils.integerToHexString(254));
		params.put("light", StringUtils.integerToHexString(1));
		params.put("step", StringUtils.integerToHexString(45));
		params.put("time", StringUtils.integerToHexString(5));
		params.put("src", "0x01");
		params.put("dst", "0x01");
		return params;
	}

}
