package com.jinxin.jxtouchscreen.fragment.wireless;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.broad.BroadcastManager;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductState;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by XTER on 2016/8/8.
 * 双路智能开关
 */
public class DoubleSwitchFragment extends DialogFragment {

	public static final int UPDATE_UI = 0;
	public static final int UPDATE_DATA = 1;

	/**
	 * 产品对象
	 */
	private ProductFun productFun;
	/**
	 * 设备对象
	 */
	private FunDetail funDetail;
	/**
	 * 命令参数
	 */
	private Map<String, Object> params;
	/**
	 * 状态参数
	 */
	private ProductState productState;

	/**
	 * 左开关
	 */
	@Bind(R.id.iv_double_switch_left)
	ImageView ivSwitchLeft;

	/**
	 * 右开关
	 */
	@Bind(R.id.iv_double_switch_right)
	ImageView ivSwitchRight;

	/**
	 * 状态
	 */
	@Bind(R.id.iv_double_switch_state)
	ImageView ivDoubleSwitchState;
	/**
	 * 左开关当前是否处于开状态
	 */
	private boolean leftState;
	/**
	 * 右开关当前是否处于开状态
	 */
	private boolean rightState;

	/**
	 * 当前是否可点击
	 */
	private boolean isClickable = true;

	private UIHandler mHandler = new UIHandler(this);

	static class UIHandler extends Handler {
		WeakReference<DoubleSwitchFragment> mDoubleSwitchFragmentWeakReference;

		UIHandler(DoubleSwitchFragment fragment) {
			mDoubleSwitchFragmentWeakReference = new WeakReference<>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			DoubleSwitchFragment doubleSwitchFragment = mDoubleSwitchFragmentWeakReference.get();
			switch (msg.what) {
				case UPDATE_UI:
					doubleSwitchFragment.updateUI();
					break;
				case UPDATE_DATA:
					doubleSwitchFragment.updateData();
					break;
			}
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		initData();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_double_switch_layout, container, false);
		ButterKnife.bind(this, rootView);
		initView(rootView);
		return rootView;
	}

	protected void initView(View view) {
		initState(productState);
		updateUI();
	}

	public void initData() {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		params = new HashMap<String, Object>();
		productState = getSwitchState(productFun);
	}

	/**
	 * 初始化状态标识
	 *
	 * @param ps 设备状态
	 */
	protected void initState(ProductState ps) {
		String stateStr = ps.getState();
		if (!TextUtils.isEmpty(stateStr) && stateStr.length() > 1) {
			leftState = stateStr.substring(0, 2).equals("01");
			rightState = stateStr.substring(2).equals("01");
		}
	}

	/**
	 * 更新UI
	 */
	protected void updateUI() {

		if (leftState) {
			ivSwitchLeft.setBackgroundResource(R.drawable.double_switch_open);
		} else {
			ivSwitchLeft.setBackgroundResource(R.drawable.double_switch_close);
		}

		if (rightState) {
			ivSwitchRight.setBackgroundResource(R.drawable.double_switch_open);
		} else {
			ivSwitchRight.setBackgroundResource(R.drawable.double_switch_close);
		}
		if (leftState != rightState) {
			ivDoubleSwitchState.setBackgroundResource(R.drawable.one_half_open);
		} else if (leftState == rightState && leftState == true) {
			ivDoubleSwitchState.setBackgroundResource(R.drawable.all_open);
		} else {
			ivDoubleSwitchState.setBackgroundResource(R.drawable.all_close);
		}
	}

	/**
	 * 更新数据库状态
	 */
	protected void updateData() {
		String stateLeft = leftState ? "01" : "00";
		String stateRight = rightState ? "01" : "00";
		String state = stateLeft + stateRight;
		productState.setState(state);
		DBM.getCurrentOrm().update(productState);
		BroadcastManager.sendBroadcast(
				BroadcastManager.MESSAGE_RECEIVED_ACTION, null);
	}


	/**
	 * 操作双路开关
	 *
	 * @param productFun 产品对象
	 * @param isOpen     目前状态
	 * @param isLeft     是左是右
	 */
	private void operateWirelessSocket(ProductFun productFun, boolean isOpen, final boolean isLeft) {
		if (productFun == null) {
			return;
		}

		isClickable = false;

		if (isLeft) {//控制左
			params.put("dst", "0x01");
		} else {
			params.put("dst", "0x02");
		}
		params.put("src", "0x01");
		String type = isOpen ? "off" : "on";

		TaskListener<Task> listener = new TaskListener<Task>() {
			@Override
			public void onFail(Task task, Object[] arg) {
				isClickable = true;
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
				} else {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				isClickable = true;
//				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//				if ("-1".equals(resultObj.validResultInfo)) {
//					ToastUtil.showShort(BaseApp.getContext(), R.string.gateway_offline);
//					return;
//				}
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
				if (params.get("dst").equals("0x01")) {
					leftState = !leftState;
				}
				if (params.get("dst").equals("0x02")) {
					rightState = !rightState;
				}
				mHandler.obtainMessage(UPDATE_DATA).sendToTarget();
				mHandler.obtainMessage(UPDATE_UI).sendToTarget();
			}
		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}


	@OnClick({R.id.iv_double_switch_left, R.id.iv_double_switch_right, R.id.back_btn})
	public void onClick(View v) {
		if (!isClickable) {
			ToastUtil.showShort(BaseApp.getContext(), R.string.operate_invalid_work);
			return;
		}
		switch (v.getId()) {
			case R.id.iv_double_switch_left:
				operateWirelessSocket(productFun, leftState, true);
				break;
			case R.id.iv_double_switch_right:
				operateWirelessSocket(productFun, rightState, false);
				break;
			case R.id.back_btn:
				dismiss();
				break;
		}
	}

	/**
	 * 得到设备状态
	 *
	 * @param pf 产品对象
	 * @return productState
	 */
	protected ProductState getSwitchState(ProductFun pf) {
		if (productFun == null) {
			return null;
		}
		ProductState productState = DBM.getProductStateByFunId(pf.getFunId());
		if(productState==null){
			productState = new ProductState(pf.getFunId(),"0000");
			DBM.getCurrentOrm().save(productState);
		}
		return productState;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
}
