package com.jinxin.jxtouchscreen.net.data;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.GatewayLogEvent;
import com.jinxin.jxtouchscreen.event.UpdateFailedEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.event.UpdateGatewayInfoEvent;
import com.jinxin.jxtouchscreen.model.CloudSetting;
import com.jinxin.jxtouchscreen.model.Customer;
import com.jinxin.jxtouchscreen.model.CustomerArea;
import com.jinxin.jxtouchscreen.model.CustomerPattern;
import com.jinxin.jxtouchscreen.model.CustomerProduct;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.FunDetailConfig;
import com.jinxin.jxtouchscreen.model.GatewayInfo;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.model.ProductDoorContact;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductPatternOperation;
import com.jinxin.jxtouchscreen.model.ProductRegister;
import com.jinxin.jxtouchscreen.model.User;
import com.jinxin.jxtouchscreen.model.WHproductUnfrared;
import com.jinxin.jxtouchscreen.model.WakeWord;
import com.jinxin.jxtouchscreen.serialport.UpdataSerialPortService;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.FileUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by XTER on 2017/2/14.
 * 数据通信
 */
public class UpdateDataHandler extends IoHandlerAdapter {
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof byte[]) {
			update((byte[]) message);
		}
		if (message instanceof String) {
			String content = (String) message;
			JSONObject jsonObject = JSON.parseObject(content);
			updateData(jsonObject);
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		L.d("进入空置状态");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		L.w(cause.getMessage());
	}

	public void updateData(JSONObject jo) {
		if (jo.containsKey("rspCode")) {
			String rspCode = jo.getString("rspCode");
			if (!rspCode.equals("0000")) {
				L.i(jo.toString());
				ToastUtil.showLong(BaseApp.getContext(), "数据异常，请重新连接");
				EventBus.getDefault().post(new UpdateFailedEvent());
				return;
			}
		}

		//普通数据更新或网关信息日志
		if (jo.containsKey("area") && jo.containsKey("content")) {
			String area = jo.getString("area");
			String content = jo.getString("content");
			String time = jo.getString("time");

			switch (area) {
				//无线网关查询在线信息
				case LocalConstant.GATEWAY_STATE:
					L.d("gateway_state:" + content);
					if (content.contains("ip"))
						EventBus.getDefault().post(new UpdateFinishEvent());
					else
						EventBus.getDefault().post(new UpdateFailedEvent());
					break;
				//网关动态日志等
				case LocalConstant.GATEWAY_LOG:
					L.d("【gateway_log】" + content);
					updateGatewayLog(time, content);
					break;
				//网关信息等
				case LocalConstant.GATEWAY_INFO:
					L.d("【gateway_info】" + content);
					saveData(content);
					break;
				case LocalConstant.UPDATE:
					if(AppUtil.compareVersion(AppUtil.getAppVersionName(BaseApp.getContext()),content)<0){
						AppUtil.showNotification("开始更新");
						UpdateDataService.updateData(LocalConstant.UPDATE_YES);

						//信息下发7500串口
						try {
							UpdataSerialPortService.sendMessageByArea(LocalConstant.UPDATE_YES);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					break;
				//传输语音识别的命令
				case LocalConstant.VOICE_UPLOAD:
					if (content.contains("ip"))
						EventBus.getDefault().post(new UpdateFinishEvent());
					else
						EventBus.getDefault().post(new UpdateFailedEvent());
					break;
				//更新数据库
				case LocalConstant.AREA_ALL:

				default:
					boolean isFinished = saveData(content);
					if (isFinished)
						EventBus.getDefault().post(new UpdateFinishEvent());
					else
						EventBus.getDefault().post(new UpdateFailedEvent());
					break;
			}
		}else if(jo.containsKey("area")&&!jo.containsKey("content")){
			//由大网关主动推送而更新数据
			UpdateDataService.updateData(jo.getString("area"));

			//信息下发到7500串口
			try {
				UpdataSerialPortService.sendMessageByArea(jo.getString("area"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 更新存储数据
	 * {
	 * "rspCode": "0000",
	 * "area": "all",
	 * "time": "20170214174713",
	 * "content": [
	 * {
	 * "tableName": "could_setting",
	 * "tableContent": [... ]
	 * }
	 * ]
	 *
	 * @param jsonStr json字段
	 */
	public boolean saveData(String jsonStr) {
		JSONArray jsonArray = JSON.parseArray(jsonStr);

		for (int i = 0; i < jsonArray.size(); i++) {

			JSONObject jsonObject1 = jsonArray.getJSONObject(i);

			if (jsonObject1 != null && jsonObject1.containsKey("tableName") && jsonObject1.containsKey("tableContent")) {

				String tableName = jsonObject1.getString("tableName");
				String tableContent = jsonObject1.getString("tableContent");


				L.d(tableName);
				L.d(tableContent);

				switch (tableName) {
					case LocalConstant.CLOUD_SETTING:
						DBM.getCurrentOrm().deleteAll(CloudSetting.class);
						List<CloudSetting> cloudSettingList = JSON.parseArray(tableContent, CloudSetting.class);
						DBM.getCurrentOrm().save(cloudSettingList);
						L.i(tableName + " size " + cloudSettingList.size());
						break;
					case LocalConstant.CUSTOMER:
						DBM.getCurrentOrm().deleteAll(Customer.class);
						List<Customer> customerList = JSON.parseArray(tableContent, Customer.class);
						DBM.getCurrentOrm().save(customerList);
						if (customerList.size() > 0)
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, customerList.get(0).getCreateUser());
						L.i(tableName + " size " + customerList.size());
						break;
					case LocalConstant.CUSTOMER_AREA:
						DBM.getCurrentOrm().deleteAll(CustomerArea.class);
						List<CustomerArea> customerAreaList = JSON.parseArray(tableContent, CustomerArea.class);
						DBM.getCurrentOrm().save(customerAreaList);
						L.i(tableName + " size " + customerAreaList.size());
						break;
					case LocalConstant.CUSTOMER_PATTERN:
						DBM.getCurrentOrm().deleteAll(CustomerPattern.class);
						List<CustomerPattern> customerPatternList = JSON.parseArray(tableContent, CustomerPattern.class);
						DBM.getCurrentOrm().save(customerPatternList);
						L.i(tableName + " size " + customerPatternList.size());
						break;
					case LocalConstant.CUSTOMER_PRODUCT:
						DBM.getCurrentOrm().deleteAll(CustomerProduct.class);
						List<CustomerProduct> customerProductList = JSON.parseArray(tableContent, CustomerProduct.class);
						DBM.getCurrentOrm().save(customerProductList);
						L.i(tableName + " size " + customerProductList.size());
						if (customerProductList.size() < 1)
							return false;
						break;
					case LocalConstant.FUN_DETAIL:
						DBM.getCurrentOrm().deleteAll(FunDetail.class);
						List<FunDetail> funDetailList = JSON.parseArray(tableContent, FunDetail.class);
						DBM.getCurrentOrm().save(funDetailList);
						L.i(tableName + " size " + funDetailList.size());
						if (funDetailList.size() < 1)
							return false;
						break;
					case LocalConstant.FUN_DETAIL_CONFIG:
						DBM.getCurrentOrm().deleteAll(FunDetailConfig.class);
						List<FunDetailConfig> funDetailConfigList = JSON.parseArray(tableContent, FunDetailConfig.class);
						DBM.getCurrentOrm().save(funDetailConfigList);
						L.i(tableName + " size " + funDetailConfigList.size());
						break;
					case LocalConstant.MUSIC:
						DBM.getCurrentOrm().deleteAll(Music.class);
						List<Music> musicList = JSON.parseArray(tableContent, Music.class);
						DBM.getCurrentOrm().save(musicList);
						L.i(tableName + " size " + musicList.size());
						break;
					case LocalConstant.PRODUCT_DOOR_CONTACT:
						DBM.getCurrentOrm().deleteAll(ProductDoorContact.class);
						List<ProductDoorContact> productDoorContactList = JSON.parseArray(tableContent, ProductDoorContact.class);
						DBM.getCurrentOrm().save(productDoorContactList);
						L.i(tableName + " size " + productDoorContactList.size());
						break;
					case LocalConstant.PRODUCT_FUN:
						DBM.getCurrentOrm().deleteAll(ProductFun.class);
						List<ProductFun> productFunList = JSON.parseArray(tableContent, ProductFun.class);
						DBM.getCurrentOrm().save(productFunList);
						L.i(tableName + " size " + productFunList.size());
						if (productFunList.size() < 1)
							return false;
						break;
					case LocalConstant.PRODUCT_PATTERN_OPERATION:
						DBM.getCurrentOrm().deleteAll(ProductPatternOperation.class);
						List<ProductPatternOperation> productPatternOperationList = JSON.parseArray(tableContent, ProductPatternOperation.class);
						DBM.getCurrentOrm().save(productPatternOperationList);
						L.i(tableName + " size " + productPatternOperationList.size());
						break;
					case LocalConstant.PRODUCT_REGISTER:
						DBM.getCurrentOrm().deleteAll(ProductRegister.class);
						List<ProductRegister> productRegisterList = JSON.parseArray(tableContent, ProductRegister.class);
						DBM.getCurrentOrm().save(productRegisterList);
						L.i(tableName + " size " + productRegisterList.size());
						//此表数据为空，则更新数据失败
						if (productRegisterList.size() < 1)
							return false;
						break;
					case LocalConstant.USER:
						DBM.getCurrentOrm().deleteAll(User.class);
						List<User> userList = JSON.parseArray(tableContent, User.class);
						for (User user : userList) {
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, user.getAccount());
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_SUB_ACCOUNT, user.getSubAccunt());
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, user.getSecretKey());
						}
						DBM.getCurrentOrm().save(userList);
						L.i(tableName + " size " + userList.size());
						break;
					case LocalConstant.WH_RPODUCT_UNFRSRED:
						DBM.getCurrentOrm().deleteAll(WHproductUnfrared.class);
						List<WHproductUnfrared> wHproductUnfraredList = JSON.parseArray(tableContent, WHproductUnfrared.class);
						DBM.getCurrentOrm().save(wHproductUnfraredList);
						L.i(tableName + " size " + wHproductUnfraredList.size());
						break;
					case LocalConstant.SUB_GATEWAY_INFO:
						DBM.getCurrentOrm().deleteAll(GatewayInfo.class);
						List<GatewayInfo> gatewayInfoList = JSON.parseArray(tableContent, GatewayInfo.class);
						DBM.getCurrentOrm().save(gatewayInfoList);
						L.i(tableName + " size " + gatewayInfoList.size());
						EventBus.getDefault().post(new UpdateGatewayInfoEvent());
						break;
					case LocalConstant.WAKE_WORD:
						DBM.getCurrentOrm().deleteAll(WakeWord.class);
						List<WakeWord> wakeWordsList = JSON.parseArray(tableContent, WakeWord.class);
						DBM.getCurrentOrm().save(wakeWordsList);
						L.i(tableName + " size " + wakeWordsList.size());
						EventBus.getDefault().post(new UpdateGatewayInfoEvent());
				}
			}
		}
		return true;
	}


	/**
	 * 更新文件
	 *
	 * @param bytes 数据
	 */
	protected void update(byte[] bytes) {
		FileUtil.deleteAllFileFromFolder(Environment.getExternalStorageDirectory().getAbsolutePath()+"/apk/");
		String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/apk/" + "jxtouch_"+SysUtil.getNow2()+".apk";
		L.d("更新apk:" + path);
		try {
			File file = new File(path);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AppUtil.showNotification("更新完成");
//		AppUtil.installAPK(BaseApp.getContext(), path);
		AppUtil.installAPKBySystem();
		AppUtil.backupAppFile(path);
//		AppUtil.exit();
	}

	/**
	 * 网关状态
	 *
	 * @param content 网关信息
	 */
	protected void updateGatewayInfo(String content) {
		List<GatewayInfo> gatewayInfoList = JSON.parseArray(content, GatewayInfo.class);
		DBM.getCurrentOrm().save(gatewayInfoList);
	}

	/**
	 * 网关日志
	 *
	 * @param time 时间
	 * @param log  日志
	 */
	protected void updateGatewayLog(String time, String log) {
		EventBus.getDefault().post(new GatewayLogEvent(SysUtil.getTime(time), log));
	}
}
