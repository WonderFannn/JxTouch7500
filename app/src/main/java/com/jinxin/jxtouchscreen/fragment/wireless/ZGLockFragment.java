package com.jinxin.jxtouchscreen.fragment.wireless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

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
 * Created by XTER on 2016/8/9.
 * 无线智能锁
 */
public class ZGLockFragment extends DialogFragment {

	public static final int LOCK_OPEN = 0;
	public static final int LOCK_CLOSE = 1;
	public static final int LOCK_LOW_BATTERY = 2;
	public static final int LOCK_NORMAL_BATTERY = 3;
	public static final int LOCK_WARRING = 4;

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
	 * 设备状态
	 */
	private ProductState productState;

	private boolean isClosed;

	/**
	 * 低电量显示
	 */
	@Bind(R.id.zg_lock_low_battery)
	TextView tvLowBattery;

	/**
	 * 当前状态显示
	 */
	@Bind(R.id.tv_lock_current_state)
	TextView tvCurrentState;

	/**
	 * 锁警告
	 */
	@Bind(R.id.tv_lock_warnning)
	TextView tvLockWarnning;

	/**
	 * 锁
	 */
	@Bind(R.id.iv_zg_lock)
	ImageView ivZgLock;

	/**
	 * 是否可操作
	 */
	private boolean isClickable = true;

	private UIHandler mHandler = new UIHandler(this);

	static class UIHandler extends Handler {
		WeakReference<ZGLockFragment> zgLockFragmentWeakReference;

		UIHandler(ZGLockFragment fragment) {
			zgLockFragmentWeakReference = new WeakReference<>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			ZGLockFragment fragment = zgLockFragmentWeakReference.get();
			switch (msg.what) {
				case LOCK_OPEN:
					if (!fragment.isClosed)
						return;
					fragment.updateUI();
					fragment.autoClose();
					break;
				case LOCK_CLOSE:
					fragment.isClosed = true;
					fragment.updateUI();
					break;
				case LOCK_LOW_BATTERY:
					fragment.showLowBattery(true);
					break;
				case LOCK_NORMAL_BATTERY:
					fragment.showLowBattery(false);
					break;
				case LOCK_WARRING:
					fragment.showWarnning();
					break;

			}
		}
	}

	/* 自定义广播，用于接受推送信息的改变 */
	private BroadcastReceiver mShowBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastManager.MESSAGE_WARN_LOW_POWER.equals(intent.getAction())) {
				String power = intent.getStringExtra("power");
				String whId = intent.getStringExtra("whId");
				if (!TextUtils.isEmpty(whId) && whId.equals(productFun.getWhId())) {
					productState.setState(power);
					if ("FF".equals(power)) {
						mHandler.obtainMessage(LOCK_LOW_BATTERY).sendToTarget();
					} else {
						mHandler.obtainMessage(LOCK_NORMAL_BATTERY).sendToTarget();
					}
				}
			} else if (BroadcastManager.MESSAGE_WARN_ERROR.equals(intent.getAction())) {

				String msg = intent.getStringExtra("msg");
				String whId = intent.getStringExtra("whId");
				if (!TextUtils.isEmpty(whId)) {
					if (whId.equals(productFun.getWhId()) && !TextUtils.isEmpty(msg)) {
						if (!msg.endsWith("开")) {
							mHandler.obtainMessage(LOCK_WARRING).sendToTarget();
						}
					}
				}
			}
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		initData();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_zg_lock, container, false);
		ButterKnife.bind(this, rootView);
		initView(rootView);
		return rootView;
	}

	public void initData() {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		params = new HashMap<>();
		productState = getBatteryState(productFun);

		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastManager.MESSAGE_WARN_LOW_POWER);
		filter.addAction(BroadcastManager.MESSAGE_WARN_ERROR);
		// 刷新设备状态
		getActivity().registerReceiver(mShowBroadcastReceiver, filter);
	}

	protected void initView(View view) {
		//低电量显示
		if (productState != null) {
			if ("FF".equals(productState.getState())) {
				tvLowBattery.setVisibility(View.VISIBLE);
			} else {
				tvLowBattery.setVisibility(View.INVISIBLE);
			}
		}
	}

	protected void updateUI() {
		if (isClosed) {
			tvCurrentState.setText(getString(R.string.zg_lock_current_state_off));
			ivZgLock.setBackgroundResource(R.drawable.lock_close);
		} else {
			tvCurrentState.setText(getString(R.string.zg_lock_current_state_on));
			ivZgLock.setBackgroundResource(R.drawable.lock_open);
		}
	}

	/**
	 * 锁自动关闭
	 */
	protected void autoClose() {
		isClosed = true;
		mHandler.sendMessageDelayed(mHandler.obtainMessage(LOCK_CLOSE), 5000);
	}

	/**
	 * 显示低电量图标
	 *
	 * @param flag 标记
	 */
	protected void showLowBattery(boolean flag) {
		tvLowBattery.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * 显示警报
	 */
	protected void showWarnning() {
		tvLockWarnning.setVisibility(View.VISIBLE);
	}

	@OnClick({R.id.btn_zg_lock_open, R.id.btn_zg_lock_query_state, R.id.back_btn})
	public void onClick(View v) {
//		if (!isClickable) {
//			ToastUtil.showShort(BaseApp.getContext(), R.string.operate_invalid_work);
//			return;
//		}
		params.clear();
		switch (v.getId()) {
			case R.id.btn_zg_lock_open:
				params.put("src", "0x01");
				params.put("dst", "0x01");
				doorLockControl(getActivity(), productFun, funDetail, params, "open");
				break;
			case R.id.btn_zg_lock_query_state:
				params.put("src", "0x01");
				params.put("dst", "0x01");
				params.put("op", "C2");
				doorLockControl(getActivity(), productFun, funDetail, params, "readStatus");
				break;
			case R.id.back_btn:
				getDialog().dismiss();
				break;
		}
	}

	/**
	 * 无线锁控制
	 *
	 * @param context    上下文
	 * @param productFun 产品对象
	 * @param funDetail  设备详情
	 * @param params     命令参数
	 * @param type       命令类型
	 */
	public void doorLockControl(final Context context, final ProductFun productFun, FunDetail funDetail, Map<String, Object> params, String type) {

		if (productFun == null || funDetail == null) {
			return;
		}
		isClickable = false;

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);

		OnlineCmdSenderLong onlinesender = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG_LOCK, cmdList, true, 0);
		onlinesender.addListener(new TaskListener<Task>() {
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
				isClickable = true;
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];

					if (resultObj.validResultCode.equals("0000")) {
						String result = resultObj.validResultInfo;

						if ("01".equals(result) || "02".equals(result) /*|| "-1".equals(result)*/) {
							ToastUtil.showShort(BaseApp.getContext(), R.string.gateway_offline);
							return;
						}
						ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);

						decodeState(result);
						BroadcastManager.sendBroadcast(
								BroadcastManager.MESSAGE_RECEIVED_ACTION, null);
					}
				}
			}
		});
		onlinesender.send();
	}

	/**
	 * 解析结果状态字符
	 *
	 * @param result 状态结果
	 */
	protected void decodeState(String result) {
		if (!TextUtils.isEmpty(result)) {
			String[] resultArr = result.split(" ");
			if (resultArr.length > 4) {
				if ("C2".equals(resultArr[3])) {
					if (resultArr.length == 10) {//状态查询
						String state = resultArr[7];
						if ("00".equals(state)) {
							mHandler.obtainMessage(LOCK_OPEN).sendToTarget();
						} else {
							mHandler.obtainMessage(LOCK_CLOSE).sendToTarget();
						}
					} else if (resultArr.length == 6) {//开锁
						String state = resultArr[5];
						if ("00".equals(state)) {
							mHandler.obtainMessage(LOCK_OPEN).sendToTarget();
						} else {
							ToastUtil.showShort(BaseApp.getContext(), result);
						}
					}
				}
			} else {
				ToastUtil.showShort(BaseApp.getContext(), result);
			}
		} else {
			ToastUtil.showShort(BaseApp.getContext(), result);
		}
	}

	/**
	 * 得到设备状态
	 *
	 * @param pf 产品对象
	 * @return productState
	 */
	protected ProductState getBatteryState(ProductFun pf) {
		if (productFun == null) {
			return null;
		}
		ProductState productState = DBM.getProductStateByFunId(pf.getFunId());
		if (productState == null) {
			productState = new ProductState(pf.getFunId(), "64");
			DBM.getCurrentOrm().save(productState);
		}
		return productState;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mShowBroadcastReceiver);
		super.onDestroy();
	}
}
