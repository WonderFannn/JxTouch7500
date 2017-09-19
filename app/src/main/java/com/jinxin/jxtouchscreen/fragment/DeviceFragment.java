package com.jinxin.jxtouchscreen.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.music.MusicActivity;
import com.jinxin.jxtouchscreen.activity.wirelessairconditionoutlet.WirelessAirConditionOutletActivity;
import com.jinxin.jxtouchscreen.adapter.DeviceFancyCoverFlowAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseFragment;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.FuntypeEvent;
import com.jinxin.jxtouchscreen.fragment.cable.CurtainFragment;
import com.jinxin.jxtouchscreen.fragment.cable.EnvironmentControlFragment;
import com.jinxin.jxtouchscreen.fragment.gateway.DeviceGatewayFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.DoubleSwitchFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.FiveSwitchFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.IlluminationFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.InfraredTranpondFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.SecurityAlarmFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.SixKeyFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.ThreeKeyFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.WirelessAirconditionFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.WirelessCurtainFragment;
import com.jinxin.jxtouchscreen.fragment.wireless.ZGLockFragment;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductState;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.fancycoverflow.FancyCoverFlow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Create By Ly At 2016/12/15
 */
public class DeviceFragment extends BaseFragment {

	public static final int UPDATE_UI = 0;
	public static final int UPDATE_STATE = 1;

	private List<ProductFun> productFunList;

	@Bind(R.id.fancyCoverFlow)
	FancyCoverFlow fancyCoverFlow;

	private DeviceFancyCoverFlowAdapter deviceFancyCoverFlowAdapter;

	BaseFragmentUIHandler mHandler;
	private ProductState ps;

	@Override
	public View inflate(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.device_control_fragment, container, false);
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		mHandler = new BaseFragmentUIHandler(this);

		initFancySetting();

		if (getArguments() != null)
			productFunList = (List<ProductFun>) getArguments().getSerializable("product_fun_list");

		if (productFunList == null || productFunList.size() < 1)
			return;

		updateFancyCoverFlow(productFunList);

		fancyCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				deviceHandle(productFunList.get(position));
			}
		});
	}

	/**
	 * 初始化控件参数
	 */
	private void initFancySetting() {
		this.fancyCoverFlow.setSpacing(-110);//child间距
		this.fancyCoverFlow.setMaxRotation(60);// 旋转度数
		this.fancyCoverFlow.setUnselectedAlpha(0.8f);// 未选中的透明度
		this.fancyCoverFlow.setUnselectedSaturation(0f);//设置未选中的饱和状态
		this.fancyCoverFlow.setUnselectedScale(0.7f);//子子之间的缩放
		this.fancyCoverFlow.setScaleDownGravity(0.5f);// 非选中的重心偏移,负的向上
		this.fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
	}

	/**
	 * 更新视图
	 *
	 * @param productFuns 产品列表
	 */
	private void updateFancyCoverFlow(List<ProductFun> productFuns) {
		productFunList = productFuns;
		if (productFunList!=null) {
			if (deviceFancyCoverFlowAdapter == null) {
				deviceFancyCoverFlowAdapter = new DeviceFancyCoverFlowAdapter(getContext(), productFuns);
				fancyCoverFlow.setAdapter(deviceFancyCoverFlowAdapter);
			} else {
				deviceFancyCoverFlowAdapter.replace(productFuns);
			}
			fancyCoverFlow.setSelection(productFuns.size() / 2);
		}
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onUpdateFancyCoverFlow(FuntypeEvent event) {
		productFunList = DBM.getProductFunListByFunType(event.getFuntype());
		mHandler.obtainMessage(UPDATE_UI).sendToTarget();
	}

	@Override
	protected void uiHandleMessage(Message msg) {
		switch (msg.what) {
			case UPDATE_UI:
				updateFancyCoverFlow(productFunList);
				break;
			case UPDATE_STATE:
				deviceFancyCoverFlowAdapter.notifyDataSetChanged();
				break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 具体分别处理
	 *
	 * @param productFun 产品类
	 */
	private void deviceHandle(ProductFun productFun) {
		String funtype = productFun.getFunType();
		FunDetail funDetail = DBM.getFunDetailByFunType(funtype);
		Fragment fragment = null;
		switch (funtype) {
			//有线窗帘
			case ProductConstants.FUN_TYPE_CURTAIN:
				fragment = new CurtainFragment();
				break;
			//双路智能开关
			case ProductConstants.FUN_TYPE_DOUBLE_SWITCH:
				fragment = new DoubleSwitchFragment();
				break;
			//有线锁
			case ProductConstants.FUN_TYPE_AUTO_LOCK:
				doorLockControl(BaseApp.getContext(), productFun, funDetail, null);
				break;
			//无线锁
			case ProductConstants.FUN_TYPE_ZG_LOCK:
				fragment = new ZGLockFragment();
				break;
			//无线窗帘
			case ProductConstants.FUN_TYPE_WIRELESS_CURTAIN:
				fragment = new WirelessCurtainFragment();
				break;
			//五路开关
			case ProductConstants.FUN_TYPE_FIVE_SWITCH:
				fragment = new FiveSwitchFragment();
				break;
			//三路开关
			case ProductConstants.FUN_TYPE_THREE_SWITCH_THREE:
			case ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR:
			case ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE:
			case ProductConstants.FUN_TYPE_THREE_SWITCH_SIX:
				fragment = new ThreeKeyFragment();
				break;
			//功放
			case ProductConstants.FUN_TYPE_POWER_AMPLIFIER:
				startActivity(new Intent(BaseApp.getContext(), MusicActivity.class));
				break;
			//无线红外转发
			case ProductConstants.FUN_TYPE_INFRARED_TRASPOND:
				fragment = new InfraredTranpondFragment();
				break;
			//无线空调
			case ProductConstants.FUN_TYPE_AIRCONDITION:
				fragment = new WirelessAirconditionFragment();
				break;
			//彩灯
			case ProductConstants.FUN_TYPE_COLOR_LIGHT:
				fragment = new IlluminationFragment();
				break;
			//环境控制
			case ProductConstants.FUN_TYPE_ENV:
				fragment = new EnvironmentControlFragment();
				break;
			//灯光
			case ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT:
				ProductState psLight = getProductState(productFun);
				sendLightCmd(funDetail, productFun, psLight);
				break;
			case ProductConstants.FUN_TYPE_CEILING_LIGHT://吸顶灯
			case ProductConstants.FUN_TYPE_POP_LIGHT://球泡灯
			case ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT://无线射灯
			case ProductConstants.FUN_TYPE_LIGHT_BELT://灯带
			case ProductConstants.FUN_TYPE_CRYSTAL_LIGHT://水晶灯
				ProductState psLight2 = getProductState(productFun);
//				fragment = new IlluminationFragment();
				operatePopLight(productFun, psLight2, funDetail);
				break;
			case ProductConstants.FUN_TYPE_WIRELESS_SOCKET://无线插座
				ProductState psSocket = getProductState(productFun);
				operateWirelessSocket(productFun, psSocket, funDetail);
				break;
			case ProductConstants.FUN_TYPE_GAS_SENSE:
			case ProductConstants.FUN_TYPE_INFRARED:
			case ProductConstants.FUN_TYPE_SMOKE_SENSE:
			case ProductConstants.FUN_TYPE_DOOR_CONTACT://门磁
				fragment = new SecurityAlarmFragment();
				break;
			case ProductConstants.FUN_TYPE_WIRELESS_GATEWAY://无线网关
				fragment = new DeviceGatewayFragment();
				break;
			case ProductConstants.FUN_TYPE_ONE_SWITCH://单路交流开关
				//fragment = new OneSocketFragment();
				ps = getProductState(productFun);
				operateWirelessOneSocket(productFun, ps, funDetail);
				break;
			case ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET://无线空调插座
				Intent intent = new Intent(getActivity(), WirelessAirConditionOutletActivity.class);
				intent.putExtra("productFun", productFun);
				intent.putExtra("funDetail", funDetail);
				startActivity(intent);
				break;
			//六键开关
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO:
			case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE:
				fragment = new SixKeyFragment();
				break;
		}
		showDialog(fragment, productFun, funDetail);
	}

	private void showDialog(Fragment fragment, ProductFun productFun, FunDetail funDetail) {
		if (fragment != null && productFun != null && funDetail != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("productFun", productFun);
			bundle.putSerializable("funDetail", funDetail);
			fragment.setArguments(bundle);
			addDialogFragment(fragment, productFun.getFunName());
		}
	}

	/**
	 * 添加Fragment
	 */
	private void addDialogFragment(Fragment fragment, String tag) {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.appear_center_in, R.anim.disappear_center_out, R.anim.disappear_center_out, R.anim.appear_center_in);
		//生成DialogFragment对象
		Fragment childFragment = getChildFragmentManager().findFragmentByTag("dialog");
		if (childFragment != null) {
			ft.remove(childFragment);
		}
		ft.addToBackStack(null);
		DialogFragment dialogFragment = (DialogFragment) fragment;
		dialogFragment.show(ft, tag);
	}

	/**
	 * 有线锁控制
	 *
	 * @param context    上下文
	 * @param productFun 产品对象
	 * @param funDetail  设备详情
	 * @param params     命令参数
	 */
	protected void doorLockControl(Context context, final ProductFun productFun, FunDetail funDetail,
	                               Map<String, Object> params) {
		String type = productFun.isOpen() ? "close" : "open";
		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, null, type);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(getActivity(), NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 1);
		onlineCmdSenderLong.addListener(new TaskListener<Task>() {
			@Override
			public void onFail(Task task, Object[] arg) {
				if (arg == null || arg.length < 1) {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				} else {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (TextUtils.isEmpty(result))
						return;
					if ("01".equals(result) || "02".equals(result)) {
						ToastUtil.showShort(BaseApp.getContext(), R.string.gateway_offline);
						return;
					}
				}
				productFun.setOpen(!productFun.isOpen());
				DBM.getCurrentOrm().update(productFun);
			}
		});
		onlineCmdSenderLong.send();
	}

	/**
	 * 发送灯的开关指令
	 *
	 * @param funDetail  设备
	 * @param productFun 产品
	 * @param ps         状态
	 */
	private void sendLightCmd(final FunDetail funDetail, final ProductFun productFun, final ProductState ps) {
		String type = productFun.isOpen() ? "close" : "open";
		List<byte[]> listCmd = CmdBuilder.build().generateCmd(productFun, funDetail, null, type);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(getActivity(), NetConstant.hostFor485(), CmdType.CMD_485, listCmd, true, 0);
		onlineCmdSenderLong.addListener(new TaskListener<Task>() {
			@Override
			public void onFail(Task task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo remote = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), remote.validResultInfo);
				} else {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				productFun.setOpen(!productFun.isOpen());
				DBM.getCurrentOrm().update(productFun);
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
				mHandler.obtainMessage(UPDATE_STATE).sendToTarget();
			}
		});
		onlineCmdSenderLong.send();
		//改变状态
		/*productFun.setOpen(!productFun.isOpen());
		DBM.getCurrentOrm().update(productFun);
		mHandler.obtainMessage(UPDATE_STATE).sendToTarget();*/
	}

	/**
	 * 无线彩灯 指令发送
	 *
	 * @param productFun
	 * @param productState
	 */
	private void operatePopLight(final ProductFun productFun, final ProductState productState, final FunDetail funDetail) {

		/* 球泡等命令参数说明： 1.类型(type) close-关闭 / mvToLevel-亮度调节 / hueandsat-设置颜色
		 * 				 2.参数：light-亮度 / dstColor-设备目标地址 / mode-类型1【彩灯】2【白灯】/ time-时间 / src-设备源地址 / dst-设备目标地址
		 * */
		String type = null;
		Map<String, Object> params = new HashMap<String, Object>();
		if (productFun.isOpen()) {
		    /* 操作类型(关灯) */
			type = "mvToLevel";
			/* 亮度 */
			params.put("light", StringUtils.integerToHexString(0));
			/* 关白灯 */
			params.put("mode", StringUtils.integerToHexString(2));
			/* 关彩灯 */
			params.put("dst", "0x01");
		} else {
			/* 操作类型(调节亮度 )*/
			type = "mvToLevel";
			/* 亮度 */
			params.put("light", StringUtils.integerToHexString(100));
			/* 设备开的目标（彩灯）*/
			params.put("dst", StringUtils.integerToHexString(1));
			/* 设备关的目标（白灯）*/
			params.put("dstColor", "0x02");
			/* 亮度关*/
			params.put("lightColor", StringUtils.integerToHexString(0));
			/* 关的时间 */
			params.put("timeColor", StringUtils.integerToHexString(1));
		}
		/* 操作时间  */
		params.put("time", StringUtils.integerToHexString(1));
		/* 设备目标地址  */
		params.put("src", "0x01");
		List<byte[]> listCmd = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG, listCmd, true, 0);
		onlineCmdSenderLong.addListener(new TaskListener<Task>() {
			@Override
			public void onFail(Task task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
				} else {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				productFun.setOpen(!productFun.isOpen());
				DBM.getCurrentOrm().update(productFun);
				if (productFun.isOpen()) {
					Fragment fragment = new IlluminationFragment();
					showDialog(fragment, productFun, funDetail);
				}
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
				mHandler.obtainMessage(UPDATE_STATE).sendToTarget();
			}
		});
		onlineCmdSenderLong.send();
	}

	/**
	 * 操作无线插座
	 *
	 * @param productFun   产品对象
	 * @param productState 产品状态
	 * @param funDetail    设备对象
	 */
	protected void operateWirelessSocket(final ProductFun productFun, final ProductState productState, FunDetail funDetail) {
		String type = productFun.isOpen() ? "off" : "on";
		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, null, type);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 1);
		onlineCmdSenderLong.addListener(new TaskListener<Task>() {
			@Override
			public void onFail(Task task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
				} else {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				L.d("开关操作成功");
				productFun.setOpen(!productFun.isOpen());
				DBM.getCurrentOrm().update(productFun);
				mHandler.obtainMessage(UPDATE_STATE).sendToTarget();
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
			}
		});
		onlineCmdSenderLong.send();
	}

	/**
	 * 解析结果状态字符
	 *
	 * @param result 状态结果
	 */
	protected void decodeState(String result) {
		if (!TextUtils.isEmpty(result)) {
			String state = result.substring(3, 4);
			if (state.equals("0") || state.equals("1")) {
			}
		} else {
			ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateFancyCoverFlow(productFunList);
	}

	/**
	 * 获取有状态返回的设备
	 *
	 * @param pf
	 * @return
	 */
	public ProductState getProductState(ProductFun pf) {
		if (pf == null)
			return null;
		ProductState tempState = DBM.getProductStateByFunId(pf.getFunId());
		//如果数据库中没有当前操作设备的状态则新建
		if (tempState == null) {
			tempState = new ProductState(pf.getFunId(), "0");
			DBM.getCurrentOrm().save(tempState);
		}
		return tempState;
	}
	/**
	 * 单路开关指令发送
	 *
	 * @param productFun
	 */
	private void operateWirelessOneSocket(final ProductFun productFun, final ProductState productState, FunDetail funDetail) {
		TaskListener<Task> listener = new TaskListener<Task>() {
			@Override
			public void onFail(Task task, Object[] arg) {
				if (arg[0] != null) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(getActivity(), resultObj.validResultInfo);
				} else {
					ToastUtil.showShort(getActivity(), "操作失败");
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				ToastUtil.showShort(getActivity(), "操作成功");
				productFun.setOpen(!productFun.isOpen());
				DBM.getCurrentOrm().update(productFun);
				mHandler.obtainMessage(UPDATE_STATE).sendToTarget();
			}


		};
		Map<String, Object> params = new HashMap<String, Object>();
		if (productFun == null) {
			return;
		}
		params.put("src", "0x01");
		params.put("dst", "0x01");
		String type = productFun.isOpen() ? "off" : "on";

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlinesender = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 0);
		onlinesender.addListener(listener);
		onlinesender.send();
	}
}
