package com.jinxin.jxtouchscreen.db;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.model.CloudSetting;
import com.jinxin.jxtouchscreen.model.Customer;
import com.jinxin.jxtouchscreen.model.CustomerArea;
import com.jinxin.jxtouchscreen.model.CustomerProduct;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.FunDetailConfig;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductPatternOperation;
import com.jinxin.jxtouchscreen.model.ProductRegister;
import com.jinxin.jxtouchscreen.model.ProductState;
import com.jinxin.jxtouchscreen.model.WakeWord;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理 --DataBaseManager
 */
public class DBM {

	private static LiteOrm liteOrm;

	/**
	 * 获取默认的orm
	 *
	 * @return DefaultOrm
	 */
	public static LiteOrm getGatewayOrm() {
		if (liteOrm == null) {
			liteOrm = LiteOrm.newSingleInstance(new DataBaseConfig(BaseApp.getContext(), LocalConstant.DB_GATEWAY_NAME,false,30,null));
		}
		return liteOrm;
	}

	public static LiteOrm getDefaultOrm(){
		if (liteOrm == null) {
			liteOrm = LiteOrm.newSingleInstance(BaseApp.getContext(), LocalConstant.DB_DEFAULT_NAME);
		}
		return liteOrm;
	}

	/**
	 * 获取和用户关联的数据库引用
	 *
	 * @return liteorm
	 */
	public synchronized static LiteOrm getCurrentOrm() {
//		if (TextUtils.isEmpty(AppUtil.getCurrentAccount())) {
			return getDefaultOrm();
//		}
		//数据库名
//		String dbName = "jxtouch_" + AppUtil.getCurrentAccount() + ".db";
//		if (liteOrm == null || !liteOrm.getDataBaseConfig().dbName.equals(dbName)) {
//			liteOrm = LiteOrm.newSingleInstance(BaseApp.getContext(), dbName);
//		}
//		return liteOrm;
	}

	/**
	 * 清除数据库引用
	 */
	public static void clearAccout() {
		liteOrm = null;
	}

	/**
	 * 根据funType查询
	 *
	 * @param funType 设备类型
	 * @return FunDetail
	 */
	public static FunDetail getFunDetailByFunType(String funType) {
		List<FunDetail> funDetailList = getCurrentOrm().query(new QueryBuilder<>(FunDetail.class).where("funType = ?", new String[]{funType}));
		if (funDetailList != null && funDetailList.size() > 0) {
			return funDetailList.get(0);
		}
		return null;
	}

	/**
	 * 根据funType查询产品
	 *
	 * @param funType 设备类型
	 * @return ProductFun
	 */
	public static ProductFun getProductFunByFunType(String funType) {
		List<ProductFun> productFunList = getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funType = ?", new String[]{funType}));
		if (productFunList != null && productFunList.size() > 0) {
			return productFunList.get(0);
		}
		return null;
	}

	/**
	 * 根据funType查询产品
	 *
	 * @param whId 设备id
	 * @return ProductFun
	 */
	public static ProductFun getProductFunByWhId(String whId) {
		List<ProductFun> productFunList = getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("whId = ?", new String[]{whId}));
		if (productFunList != null && productFunList.size() > 0) {
			return productFunList.get(0);
		}
		return null;
	}

	/**
	 * 根据funId查询产品
	 *
	 * @param funId 设备id
	 * @return ProductFun
	 */
	public static ProductFun getProductFunByFunId(int funId) {
		List<ProductFun> productFunList = getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funId = ?", new String[]{String.valueOf(funId)}));
		if (productFunList != null && productFunList.size() > 0) {
			return productFunList.get(0);
		}
		return null;
	}

	/**
	 * 根据whId查询设备参数设置
	 *
	 * @param whId 设备id
	 * @return FunDetailConfig
	 */
	public static FunDetailConfig getFunDetailConfigByWhId(String whId) {
		List<FunDetailConfig> funDetailConfigList = getCurrentOrm().query(new QueryBuilder<>(FunDetailConfig.class).where("whId = ?", new String[]{whId}));
		if (funDetailConfigList != null && funDetailConfigList.size() > 0) {
			return funDetailConfigList.get(0);
		}
		return null;
	}

	/**
	 * 根据whId查询设备注册信息
	 *
	 * @param whId 设备id
	 * @return ProductRegister
	 */
	public static ProductRegister getProductRegisterByWhId(String whId) {
		List<ProductRegister> productRegisterList = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{whId}));
		if (productRegisterList != null && productRegisterList.size() > 0) {
			return productRegisterList.get(0);
		}
		return null;
	}

	/**
	 * 根据whId查询设备注册信息
	 *
	 * @param whId 设备id
	 * @return CustomerProduct
	 */
	public static CustomerProduct getCustomerProductByWhId(String whId) {
		List<CustomerProduct> list = getCurrentOrm().query(new QueryBuilder<>(CustomerProduct.class).where("whId = ?", new String[]{whId}));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据funId查询设备当前状态
	 *
	 * @param funId 设备id
	 * @return ProductState
	 */
	public static ProductState getProductStateByFunId(int funId) {
		List<ProductState> list = getCurrentOrm().query(new QueryBuilder<>(ProductState.class).where("funId = ?", new String[]{String.valueOf(funId)}));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据某一个设备查询其关联的网关设备
	 *
	 * @param whId 设备id
	 * @return ProductRegister
	 */
	public static ProductRegister getGetwayByWhId(String whId) {
		List<ProductRegister> ProductRegisterList = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{whId}));
		if (ProductRegisterList != null && ProductRegisterList.size() > 0) {
			ProductRegister pr = ProductRegisterList.get(0);
			ArrayList<ProductRegister> gatewayList = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{pr.getGatewayWhId()}));
			if (gatewayList.size() > 0) return gatewayList.get(0);
		}
		return null;
	}

	/**
	 * 根据某一个设备查询其关联的网关设备whid
	 *
	 * @param whId 设备id
	 * @return gatewayWhid
	 */
	public static String getGetwayWhIdByProductWhId(String whId) {
		List<ProductRegister> ProductRegisterList = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{whId}));
		if (ProductRegisterList != null && ProductRegisterList.size() > 0) {
			ProductRegister pr = ProductRegisterList.get(0);
			List<ProductRegister> gatewayList = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{pr.getGatewayWhId()}));
			if (gatewayList.size() > 0) {
				return gatewayList.get(0).getGatewayWhId();
			}
		} else {
			L.e("没有找到相应注册网关whid");
		}
		return "";
	}

	/**
	 * 根据groupId返回区域对象
	 * @param groupId 组id
	 * @return customerarea
	 */
	public static CustomerArea getCustomerAreaByGroupId(int groupId){
		List<CustomerArea> customerAreaList = getCurrentOrm().query(new QueryBuilder<>(CustomerArea.class).where("idd = ?", new String[]{String.valueOf(groupId)}));
		if(customerAreaList!=null&&customerAreaList.size()>0)
			return customerAreaList.get(0);
		return null;
	}

	/**
	 * 根据某一个设备查询其关联的网关设备whid
	 *
	 * @param whId 设备id
	 * @return gatewayMAC
	 */
	public static String getGetwayMACByProductWhId(String whId) {
		//先通过设备whId查找网关whId
		List<ProductRegister> list = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{whId}));
		if (list != null && list.size() > 0) {
			ProductRegister pr = list.get(0);
			//再通过网关whId查找该网关下所有的设备，包括网关
			List<ProductRegister> list1 = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("whId = ?", new String[]{pr.getGatewayWhId()}));
			if (list1 != null && list1.size() > 0) {
				//最后找到网关的MAC地址取匹配无线网关的本地IP
				for (ProductRegister _pr : list1) {
					if (_pr.getCode().equals(ProductConstants.FUN_TYPE_WIRELESS_GATEWAY)) {
						return list1.get(0).getMac();
					}
				}
			}
		} else {
			L.e("没有找到相应注册网关mac");
		}
		return "";
	}

	/**
	 * 得到485网关地址
	 *
	 * @param whId 设备id
	 * @return address485
	 */
	public static String getAddress485ByWhId(String whId) {
		List<CustomerProduct> customerProductLis = getCurrentOrm().query(new QueryBuilder<>(CustomerProduct.class).where("whId = ?", new String[]{whId}));
		if (customerProductLis != null && customerProductLis.size() > 0) {
			CustomerProduct cp = customerProductLis.get(0);
			if (cp != null)
				return cp.getAddress485();
		}
		return null;
	}

	/**
	 * 寻找网关
	 *
	 * @return ProductRegister
	 */
	public static ProductRegister getGetway() {
		ArrayList<ProductRegister> list = getCurrentOrm().query(new QueryBuilder<>(ProductRegister.class).where("code = ?", new String[]{ProductConstants.FUN_TYPE_GATEWAY}));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 得到用户id
	 *
	 * @return customerid
	 */
	public static String getCustomerId() {
		String customerId = null;
		List<Customer> cList = getCurrentOrm().query(Customer.class);
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = (c.getCustomerId() == null ? "" : c.getCustomerId());
			}
		}
		return customerId;
	}

	/**
	 * 返回唤醒词列表
	 *
	 *  @return wakewords
	 */
	public static List<WakeWord> loadWakeWordFromDb(){
	List<WakeWord> wakewords = getCurrentOrm().query(new QueryBuilder<>(WakeWord.class));
			return wakewords;
	}

	/**
	 * 返回音乐列表
	 *
	 * @return musics
	 */
	public static List<Music> loadMusicDataFromDb() {
		List<Music> musics = getCurrentOrm().query(new QueryBuilder<>(Music.class).groupBy("musicName").orderBy("playNo"));
		if (musics != null && musics.size() > 0) {
			return musics;
		}
		return musics;
	}

	/**
	 * 根据type取云端设置
	 *
	 * @param type type
	 * @return cloudsetting
	 */
	public static List<CloudSetting> getCloudSetting(String type) {
		List<CloudSetting> csList = getCurrentOrm().query(new QueryBuilder<>(CloudSetting.class).where("customerId=? and items=?", new String[]{getCustomerId(), type}));
		if (csList != null && csList.size() > 0)
			return csList;
		return null;
	}

	/**
	 * 根据funType查询产品列表
	 *
	 * @param funType 设备类型
	 * @return ProductFun
	 */
	public static List<ProductFun> getProductFunListByFunType(String funType) {
		List<ProductFun> productFunList = getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funType = ? and enable= ?", new String[]{funType,"1"}));
		if (productFunList != null && productFunList.size() > 0) {
			return productFunList;
		}
		return null;
	}

	/**
	 * 根据funType查询产品列表
	 *
	 * @param whId 设备id
	 * @return ProductFun
	 */
	public static List<CustomerProduct> getCustomerProductListByWhId(String whId) {
		List<CustomerProduct> customerProductList = getCurrentOrm().query(new QueryBuilder<>(CustomerProduct.class).where("whId = ?", new String[]{whId}));
		if (customerProductList != null && customerProductList.size() > 0) {
			return customerProductList;
		}
		return null;
	}

	/**
	 * 根据funid查fununit
	 * @param funId
	 * @return
	 */
	public static String getFunUnitByFunId(int funId){
		ProductFun productFun = getProductFunByFunId(funId);
		if(productFun!=null)
			return productFun.getFunUnit();
		return "";
	}

	public static String getFunTypeByProductWhId(String whId){
		ProductFun productFun = getProductFunByWhId(whId);
		if(productFun!=null)
			return productFun.getFunType();
		return null;
	}

	/**
	 * 根据funtype寻找字符串
	 *
	 * @param funType 设备类型
	 * @return speakernames
	 */
	public static String getSpeakerNamesFromDb(String funType) {
		List<FunDetailConfig> fdc4Amplifiers = getCurrentOrm().query(new QueryBuilder<>(FunDetailConfig.class).where("funType = ?", new String[]{funType}));
		if (fdc4Amplifiers != null && fdc4Amplifiers.size() > 0) {
			FunDetailConfig fdc4Amplifier = fdc4Amplifiers.get(0);
			if (fdc4Amplifier != null && !TextUtils.isEmpty(fdc4Amplifier.getParams())) {
				return fdc4Amplifier.getParams();
			}
		}
		return "";
	}

}
