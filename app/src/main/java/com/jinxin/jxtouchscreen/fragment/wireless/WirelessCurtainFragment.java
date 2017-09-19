package com.jinxin.jxtouchscreen.fragment.wireless;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductState;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.CircleSeekBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by XTER on 2016/8/23.
 * 无线窗帘详细控制
 */
public class WirelessCurtainFragment extends DialogFragment {

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
	 * 当前操作类型
	 */
	private String curType;

	/**
	 * 窗帘控制布局
	 */
	@Bind(R.id.rl_curtain_control_layout_radio_no_seek)
	RelativeLayout rlCutainLeft;

	/**
	 * 一键定位布局
	 */
	@Bind(R.id.rl_curtain_control_layout_radio_seek)
	RelativeLayout rlCutainRight;

	/**
	 * 一键定位 圆形进度条
	 */
	@Bind(R.id.csb_curtain_control_seek)
	CircleSeekBar csbCurtainSeekBarSeek;

	/**
	 * 窗帘控制 圆形进度条
	 */
	@Bind(R.id.csb_curtain_control_no_seek)
	CircleSeekBar csbCurtainSeekBarNoSeek;

	/**
	 * 一键定位 档位进度值
	 */
	@Bind(R.id.tv_curtain_seek_progress)
	TextView tvCurtainProgress;

	@Bind(R.id.curtain_fragment_controle)
	ImageButton curtainFragmentControle;

	@Bind(R.id.curtain_fragment_onrkey_location)
	ImageButton curtainFragmentOnrkeyLocation;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		initData();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_curtain_layout, container, false);
		ButterKnife.bind(this, rootView);
		initView(rootView);
		return rootView;
	}

	protected void initView(View view) {
		rlCutainRight.setVisibility(View.INVISIBLE);

		csbCurtainSeekBarSeek.setOnCircleSeekBarChangeListener(new CircleSeekBar.OnCircleSeekBarChangeListener() {
			@Override
			public void onStartTrackingTouch(CircleSeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(CircleSeekBar seekBar, int progress) {
				tvCurtainProgress.setText(progress + " %");
			}

			@Override
			public void onStopTrackingTouch(CircleSeekBar seekBar) {
				params.put(StaticConstant.PARAM_TEXT, seekBar.getCurrentProgress());
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "percent");
			}
		});
		onClick(curtainFragmentControle);
	}

	public void initData() {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		params = new HashMap<>();
		if (productFun != null)
			productState = getProductState(productFun);
	}

	/**
	 * 无线窗帘控制
	 *
	 * @param context    上下文
	 * @param productFun 产品对象
	 * @param funDetail  设备对象
	 * @param params     命令参数
	 * @param type       命令类型
	 */
	public void wirelessCurtainControl(Context context,
	                                   final ProductFun productFun, FunDetail funDetail, Map<String, Object> params, String type) {
		if (productFun == null || funDetail == null) {
			return;
		}
		isClickable = false;

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
//                RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//                if ("-1".equals(resultObj.validResultInfo)) {
//                    ToastUtil.showShort(BaseApp.getContext(), R.string.gateway_offline);
//                    return;
//                }
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
				if (curType.equals("down")) {
					productState.setState("01");
				} else {
					productState.setState("00");
				}
				DBM.getCurrentOrm().update(productState);
				BroadcastManager.sendBroadcast(
						BroadcastManager.MESSAGE_RECEIVED_ACTION, null);
			}
		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlinesender = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 0);
		onlinesender.addListener(listener);
		onlinesender.send();
	}

	@OnClick({R.id.btn_curtain_on, R.id.btn_curtain_off, R.id.btn_curtain_stop, R.id.btn_curtain_step_1, R.id.btn_curtain_step_2, R.id.btn_curtain_step_3, R.id.btn_curtain_step_4, R.id.back_btn,
			R.id.curtain_fragment_onrkey_location, R.id.curtain_fragment_controle})
	public void onClick(View v) {
		if (!isClickable) {
			ToastUtil.showShort(BaseApp.getContext(), R.string.operate_invalid_work);
			return;
		}
		params.put("src", "0x01");
		params.put("dst", StringUtils.integerToHexString(Integer.parseInt(productFun.getFunUnit())));
		switch (v.getId()) {
			case R.id.btn_curtain_on:
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "open");
				csbCurtainSeekBarNoSeek.setProgress(100);
				break;
			case R.id.btn_curtain_off:
				productFun.setOpen(true);
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "down");
				csbCurtainSeekBarNoSeek.setProgress(0);
				break;
			case R.id.btn_curtain_stop:
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "stop");
				csbCurtainSeekBarNoSeek.setProgress(50);
				break;
			case R.id.btn_curtain_step_1:
				params.put(StaticConstant.PARAM_TEXT, StringUtils.integerToHexString(33));
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "percent");
				updateProgress(33);
				break;
			case R.id.btn_curtain_step_2:
				params.put(StaticConstant.PARAM_TEXT, StringUtils.integerToHexString(50));
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "percent");
				updateProgress(50);
				break;
			case R.id.btn_curtain_step_3:
				params.put(StaticConstant.PARAM_TEXT, StringUtils.integerToHexString(66));
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "percent");
				updateProgress(66);
				break;
			case R.id.btn_curtain_step_4:
				params.put(StaticConstant.PARAM_TEXT, StringUtils.integerToHexString(75));
				wirelessCurtainControl(getActivity(), productFun, funDetail, params, "percent");
				updateProgress(75);
				break;
			case R.id.curtain_fragment_controle:
				curtainFragmentControle.setSelected(true);
				curtainFragmentOnrkeyLocation.setSelected(false);
				rlCutainLeft.setVisibility(View.VISIBLE);
				rlCutainRight.setVisibility(View.INVISIBLE);
				break;
			case R.id.curtain_fragment_onrkey_location:
				curtainFragmentControle.setSelected(false);
				curtainFragmentOnrkeyLocation.setSelected(true);
				rlCutainLeft.setVisibility(View.INVISIBLE);
				rlCutainRight.setVisibility(View.VISIBLE);
				break;
			case R.id.back_btn:
				getDialog().dismiss();
				break;
		}
	}

	/**
	 * 更新进度条
	 *
	 * @param progress 进度值
	 */
	protected void updateProgress(int progress) {
		csbCurtainSeekBarSeek.setProgress(progress);
		tvCurtainProgress.setText(progress + " %");
	}

	/**
	 * 得到设备状态
	 *
	 * @param pf 产品对象
	 * @return productState
	 */
	protected ProductState getProductState(ProductFun pf) {
		if (productFun == null) {
			return null;
		}
		ProductState productState = DBM.getProductStateByFunId(pf.getFunId());
		if (productState == null) {
			productState = new ProductState(pf.getFunId(), "00");
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
