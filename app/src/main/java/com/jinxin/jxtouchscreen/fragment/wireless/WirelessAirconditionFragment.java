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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by XTER on 2016/8/24.
 * 无线空调控制页面
 */
public class WirelessAirconditionFragment extends DialogFragment {

	public static final String WIND_ALLOPEN = "allOpen";
	public static final String WIND_ALLCLOSE = "allClose";
	public static final String WIND_LEVEL = "windLevel";
	public static final String LEVEL_NONE = "00";
	public static final String LEVEL_LOW = "01";
	public static final String LEVEL_MID = "02";
	public static final String LEVEL_HIGH = "03";

	public static final int UPDATE_UI = 0;
	public static final int UPDATE_SEEKBAR_STATE = 1;
	public static final int RESUME_UI = 2;

	public static final int SEEKBAR1_SCALE_1 = 5;
	public static final int SEEKBAR1_SCALE_2 = 92;
	public static final int SEEKBAR2_SCALE_1 = 1;
	public static final int SEEKBAR2_SCALE_2 = 33;
	public static final int SEEKBAR2_SCALE_3 = 65;
	public static final int SEEKBAR2_SCALE_4 = 99;

	/**
	 * 产品对象
	 */
	private ProductFun productFun;
	/**
	 * 设备对象
	 */
	private FunDetail funDetail;
	/**
	 * 设备状态
	 */
	private ProductState productState;
	/**
	 * 命令参数
	 */
	private Map<String, Object> params;
	/**
	 * 是否可操作
	 */
	private boolean isClickable = true;
	/**
	 * 模式状态：制冷、制热
	 */
	private String modeState = "off";
	/**
	 * 当前风速
	 */
	private String levelWindState = LEVEL_NONE;

	/**
	 * 全开/全关 开关
	 */
	@Bind(R.id.tb_switch_1)
	ToggleButton tbSwitch1;

	/**
	 * 当前开关状态
	 */
	@Bind(R.id.iv_air_condition_switch_state)
	ImageView ivCircleSwitchState;

	/**
	 * 当前风速状态
	 */
	@Bind(R.id.iv_air_condition_wind_speed_state)
	ImageView ivWindSpeedState;

	/**
	 * 制冷制热开关
	 */
	@Bind(R.id.tb_switch_2)
	ToggleButton tbSwitch2;

	/**
	 * 风速档位
	 */
	@Bind(R.id.sb_air_switch_3)
	SeekBar sbSwitch3;

	private UIHandler mHandler = new UIHandler(this);

	static class UIHandler extends Handler {
		WeakReference<WirelessAirconditionFragment> wirelessAirconditionFragmentWeakReference;

		UIHandler(WirelessAirconditionFragment fragment) {
			wirelessAirconditionFragmentWeakReference = new WeakReference<>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			WirelessAirconditionFragment fragment = wirelessAirconditionFragmentWeakReference.get();
			switch (msg.what) {
				case UPDATE_UI:
					fragment.updateUI(fragment.modeState, fragment.levelWindState);
					break;
				case UPDATE_SEEKBAR_STATE:
					fragment.tbSwitch1.setEnabled(true);
					//fragment.sbSwitch2.setEnabled(true);
					fragment.sbSwitch3.setEnabled(true);
					break;
				case RESUME_UI:
					fragment.resumeUI();
					break;
			}
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		//getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
		initData();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_wireless_aircondition, container, false);
		ButterKnife.bind(this, rootView);
		initView(rootView);
		//if (getDialog()!=null) {
		//getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
		//}
		return rootView;
	}

	public void initData() {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		params = new HashMap<>();
		if (productFun != null) {
			productState = getStateByProductFun(productFun);
		}

	}

	protected void initView(View view) {
//        btnHomeTitle.setText(getString(R.string.air_condition));
		//初始化选中状态
		if (productState != null) {
			modeState = productState.getState().subSequence(0, 2).equals("00") ? "off" : "on";
			levelWindState = productState.getState().subSequence(2, 4).toString();
			updateUI(modeState, levelWindState);
		}

		tbSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (!isClickable) {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_invalid_work);
					return;
				}
				String type = b ? WIND_ALLOPEN : WIND_ALLCLOSE;
				operateAircondition(productFun, type, type);
			}
		});

		tbSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				tbSwitch2.setChecked(isChecked);
				if (isChecked) {
					String type = "on";
					modeState = "on";
					operateAircondition(productFun, type, type);
				} else {
					String type = "off";
					modeState = "off";
					operateAircondition(productFun, type, type);
				}
			}
		});
		sbSwitch3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				if (i < SEEKBAR2_SCALE_1)
					seekBar.setProgress(SEEKBAR2_SCALE_1);
				if (i > SEEKBAR2_SCALE_4)
					seekBar.setProgress(SEEKBAR2_SCALE_4);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if (!isClickable) {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_invalid_work);
					seekBar.setEnabled(false);
					return;
				}
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (seekBar.getProgress() < seekBar.getMax() / 4) {
					seekBar.setProgress(SEEKBAR2_SCALE_1);
					levelWindState = LEVEL_NONE;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_NONE);
					Toast.makeText(getActivity(), "关", Toast.LENGTH_SHORT).show();
				} else if (seekBar.getProgress() > seekBar.getMax() / 4 && seekBar.getProgress() < seekBar.getMax() / 2) {
					seekBar.setProgress(SEEKBAR2_SCALE_2);
					levelWindState = LEVEL_LOW;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_LOW);
					Toast.makeText(getActivity(), "低", Toast.LENGTH_SHORT).show();
				} else if (seekBar.getProgress() > seekBar.getMax() / 2 && seekBar.getProgress() < seekBar.getMax() * 0.75) {
					seekBar.setProgress(SEEKBAR2_SCALE_3);
					levelWindState = LEVEL_MID;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_MID);
					Toast.makeText(getActivity(), "中", Toast.LENGTH_SHORT).show();
				} else {
					seekBar.setProgress(SEEKBAR2_SCALE_4);
					levelWindState = LEVEL_HIGH;
					operateAircondition(productFun, WIND_LEVEL, LEVEL_HIGH);
					Toast.makeText(getActivity(), "高", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 根据状态更新UI
	 *
	 * @param modeState 制冷制热开关状态
	 * @param windState 风速状态
	 */
	protected void updateUI(String modeState, String windState) {
		if (TextUtils.isEmpty(modeState)) {
			return;
		}
		if (modeState.equals("off")) {
			//sbSwitch2.setProgress(SEEKBAR1_SCALE_1);
			tbSwitch2.setChecked(false);
			ivCircleSwitchState.setBackgroundResource(R.drawable.air_condition_circle_off);
		}
		if (modeState.equals("on")) {
			//sbSwitch2.setProgress(SEEKBAR1_SCALE_2);
			tbSwitch2.setChecked(true);
			ivCircleSwitchState.setBackgroundResource(R.drawable.air_condition_circle_on);
		}

		if (TextUtils.isEmpty(windState)) {
			return;
		}
		switch (windState) {
			case LEVEL_NONE:
				sbSwitch3.setProgress(SEEKBAR2_SCALE_1);
				ivWindSpeedState.setBackgroundResource(R.drawable.air_condition_wind_speed_none);
				break;
			case LEVEL_LOW:
				sbSwitch3.setProgress(SEEKBAR2_SCALE_2);
				ivWindSpeedState.setBackgroundResource(R.drawable.air_condition_wind_speed_low);
				break;
			case LEVEL_MID:
				sbSwitch3.setProgress(SEEKBAR2_SCALE_3);
				ivWindSpeedState.setBackgroundResource(R.drawable.air_condition_wind_speed_mid);
				break;
			case LEVEL_HIGH:
				sbSwitch3.setProgress(SEEKBAR2_SCALE_4);
				ivWindSpeedState.setBackgroundResource(R.drawable.air_condition_wind_speed_high);
				break;
		}
	}

	/**
	 * 恢复初始UI
	 */
	protected void resumeUI() {
		tbSwitch1.setChecked(false);
		//sbSwitch2.setProgress(SEEKBAR1_SCALE_1);
		tbSwitch2.setChecked(false);
		sbSwitch3.setProgress(SEEKBAR2_SCALE_1);
		ivCircleSwitchState.setBackgroundResource(R.drawable.air_condition_circle_off);
		ivWindSpeedState.setBackgroundResource(R.drawable.air_condition_wind_speed_none);
	}

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
			mHandler.obtainMessage(UPDATE_SEEKBAR_STATE).sendToTarget();
			mHandler.obtainMessage(RESUME_UI).sendToTarget();
		}

		@Override
		public void onSuccess(Task task, Object[] arg) {
			isClickable = true;
			mHandler.obtainMessage(UPDATE_SEEKBAR_STATE).sendToTarget();
//            RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//            if ("-1".equals(resultObj.validResultInfo)) {
//                ToastUtil.showShort(BaseApp.getContext(), R.string.gateway_offline);
//                return;
//            }
			String state = modeState.equals("off") ? "00" : "01" + levelWindState;
			if (!TextUtils.isEmpty(state)) {
				productState.setState(state);
				DBM.getCurrentOrm().update(productState);
				mHandler.obtainMessage(UPDATE_UI).sendToTarget();
			}
		}

	};

	/**
	 * 无线空调操控
	 *
	 * @param productFun 产品对象
	 * @param type       命令类型
	 * @param text       命令文本参数
	 */
	private void operateAircondition(ProductFun productFun, String type, String text) {
		if (productFun == null || TextUtils.isEmpty(type)) return;
		isClickable = false;
		tbSwitch1.setEnabled(false);
		//全开全关标记
		boolean isAll = false;
		params.clear();
		params.put("src", "0x01");
		params.put("dst", "0x01");
		if (WIND_ALLOPEN.equals(type)) {
			isAll = true;
			type = "on";
			modeState = "on";
			levelWindState = LEVEL_MID;
			params.put("text", LEVEL_MID);
		} else if (WIND_ALLCLOSE.equals(type)) {
			isAll = true;
			type = "off";
			modeState = "off";
			levelWindState = LEVEL_NONE;
			params.put("text", LEVEL_NONE);
		} else {
			params.put("text", text);
		}

		List<byte[]> cmdAll = new ArrayList<byte[]>();
		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		cmdAll.addAll(cmdList);
		//生成中/关风速指令，仅全开/关时
		if (isAll) {
			List<byte[]> levelList = CmdBuilder.build().generateCmd(productFun, funDetail, params, WIND_LEVEL);
			cmdAll.addAll(levelList);
		}
		OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
				NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdAll, true, 0);

		onlineSender.addListener(listener);
		onlineSender.send();
	}

	@OnClick({R.id.back_btn})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				getDialog().dismiss();
				break;
		}
	}


	/**
	 * 获取设备状态
	 *
	 * @param productFun 产品对象
	 * @return productState
	 */
	protected ProductState getStateByProductFun(ProductFun productFun) {
		if (productFun == null) {
			return null;
		}
		ProductState ps = DBM.getProductStateByFunId(productFun.getFunId());

		if (ps == null) {
			ps = new ProductState();
			ps.setState("0000");
		} else {
			if (ps.getState().length() < 4) {
				if (ps.getState().equals("00")) {
					ps.setState("0000");
				} else {
					ps.setState("0100");
				}
			}
		}
		ps.setFunId(productFun.getFunId());
		DBM.getCurrentOrm().update(ps);
		return ps;
	}

    /*@Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }*/

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
}
