package com.jinxin.jxtouchscreen.fragment.wireless;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.ThreeKeyGridAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.event.PosEvent;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by XTER on 2017/03/27.
 * 三键多路
 */
public class ThreeKeyFragment extends DialogFragment {

	public static final int UPDATE_VIEW = 1;

	@Bind(R.id.three_switch_gv)
	GridView gvThreeKey;

	@Bind(R.id.three_switch_title_tv)
	TextView tvTitle;

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
	 * 是否可操作
	 */
	private boolean isClickable = true;

	/**
	 * 开关状态列表
	 */
	private boolean[] switchStatus = null;

	/**
	 * 命令字符集
	 */
	public static String[] cmds = {"0x01", "0x02", "0x03", "0x04", "0x05", "0x06"};

	/**
	 * 适配器
	 */
	public ThreeKeyGridAdapter threeKeyGridAdapter;

	/**
	 * UI线程
	 */
	static class SwitchHandler extends Handler {
		WeakReference<ThreeKeyFragment> mFragment;

		public SwitchHandler(ThreeKeyFragment threeKeyFragment) {
			mFragment = new WeakReference<>(threeKeyFragment);
		}

		public void handleMessage(Message msg) {
			ThreeKeyFragment fragment = mFragment.get();
			switch (msg.what) {
				// 更新列表数据
				case UPDATE_VIEW:
					fragment.threeKeyGridAdapter.notifyDataSetChanged(fragment.switchStatus);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * UI Handler
	 */
	private SwitchHandler mHandler = new SwitchHandler(this);

	@Override
	public void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_three_switch, container, false);
		ButterKnife.bind(this, rootView);
		initView(rootView);
		return rootView;
	}

	public void initData() {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		params = new HashMap<>();
		for(int i = 0; i< ProductConstants.FUN_TYPE_THREE_SWITCHES.length; i++){
			if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCHES[i])){
				switchStatus = new boolean[i+3];
			}
		}
	}

	protected void initView(View view) {
		if (funDetail != null) {
			tvTitle.setText(funDetail.getFunName());
		}

		threeKeyGridAdapter = new ThreeKeyGridAdapter(productFun, switchStatus);

		gvThreeKey.setAdapter(threeKeyGridAdapter);
	}

	@OnClick({R.id.back_btn})
	public void onClick(View v) {
		if (!isClickable) {
			ToastUtil.showShort(getActivity().getApplicationContext(), R.string.operate_invalid_work);
			return;
		}
		switch (v.getId()) {
			case R.id.back_btn:
				getDialog().dismiss();
				break;
		}
	}

	/**
	 * 应答监听
	 */
	TaskListener<Task> listener = new TaskListener<Task>() {

		@Override
		public void onFail(Task task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
			} else {
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
			}
			isClickable = true;
		}

		@Override
		public void onSuccess(Task task, Object[] arg) {
			isClickable = true;
//			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//			if ("-1".equals(resultObj.validResultInfo)) {
//				ToastUtil.showShort(getActivity().getApplicationContext(), R.string.gateway_offline);
//				return;
//			}
			ToastUtil.showShort(getActivity().getApplicationContext(), R.string.operate_success);
			updateSwitchStatus();
		}

	};

	/**
	 * 更新列表视图
	 */
	protected void updateSwitchStatus() {
		for (int i = 0; i < cmds.length; i++) {
			if (params.get("dst").equals(cmds[i])){
				switchStatus[i] = !switchStatus[i];
			}
		}
		mHandler.obtainMessage(UPDATE_VIEW).sendToTarget();
	}

	/**
	 * 开关控制
	 *
	 * @param productFun 产品
	 * @param cmdStr     命令
	 * @param open       状态
	 */
	protected void switchControl(ProductFun productFun, String cmdStr, boolean open) {
		if (productFun == null) return;
		isClickable = false;

		params.put("src", "0x01");
		params.put("dst", cmdStr);

		String type = open ? "off" : "on";

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);

		OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
				NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 0);
		onlineSender.addListener(listener);
		onlineSender.send();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
	}

	@Subscribe
	public void onEventMainThread(PosEvent event) {
		switchControl(productFun, cmds[event.getPos()], switchStatus[event.getPos()]);
	}
}
