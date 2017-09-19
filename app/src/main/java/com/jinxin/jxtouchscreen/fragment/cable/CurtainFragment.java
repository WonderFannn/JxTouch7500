package com.jinxin.jxtouchscreen.fragment.cable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.CircleSeekBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by XTE on 2016/8/18.
 * 窗帘详细控制
 */
public class CurtainFragment extends DialogFragment {

	@Bind(R.id.curtain_fragment_controle)
	ImageButton curtainFragmentControle;

	@Bind(R.id.curtain_fragment_onrkey_location)
	ImageButton curtainFragmentOnrkeyLocation;
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
	 * 窗帘开
	 */
	@Bind(R.id.btn_curtain_on)
	Button btnCurtainOn;

	/**
	 * 窗帘关
	 */
	@Bind(R.id.btn_curtain_off)
	Button btnCurtainOff;

	/**
	 * 窗帘暂停
	 */
	@Bind(R.id.btn_curtain_stop)
	Button btnCurtainStop;

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
	 * 一档
	 */
	@Bind(R.id.btn_curtain_step_1)
	Button btnCurtainStep1;

	/**
	 * 二档
	 */
	@Bind(R.id.btn_curtain_step_2)
	Button btnCurtainStep2;

	/**
	 * 三档
	 */
	@Bind(R.id.btn_curtain_step_3)
	Button btnCurtainStep3;

	/**
	 * 四档
	 */
	@Bind(R.id.btn_curtain_step_4)
	Button btnCurtainStep4;

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

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		initData();
	}

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
//                csbCurtainSeekBarNoSeek.setProgressBackgroundColor();
			}

			@Override
			public void onStopTrackingTouch(CircleSeekBar seekBar) {
				params.put(StaticConstant.PARAM_TEXT, seekBar.getCurrentProgress());
				curtainControl(getActivity(), productFun, funDetail, params, "offset");
			}
		});
		onClick(curtainFragmentControle);
	}

	public void initData() {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		params = new HashMap<>();
	}

	/**
	 * 窗帘控制命令
	 *
	 * @param context    上下文
	 * @param productFun 产品对象
	 * @param funDetail  设备对象
	 * @param params     命令参数
	 */
	public void curtainControl(Context context,
	                           ProductFun productFun, FunDetail funDetail, Map<String, Object> params, String type) {
		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
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
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
				isClickable = true;
			}
		};

		byte[] cmd = cmdList.get(0);
		//直接在线发送命令
		OnlineCmdSenderLong ocstask = new OnlineCmdSenderLong(getActivity(), NetConstant.hostFor485(), CmdType.CMD_485, cmdList,true, 0);
		ocstask.addListener(listener);
		ocstask.send();
	}

	@OnClick({R.id.btn_curtain_on, R.id.btn_curtain_off, R.id.btn_curtain_stop,
			R.id.btn_curtain_step_1, R.id.btn_curtain_step_2, R.id.btn_curtain_step_3,
			R.id.btn_curtain_step_4, R.id.back_btn, R.id.curtain_fragment_controle, R.id.curtain_fragment_onrkey_location})
	public void onClick(View v) {
		if (!isClickable) {
			ToastUtil.showShort(BaseApp.getContext(), R.string.operate_invalid_work);
			return;
		}
		switch (v.getId()) {
			case R.id.btn_curtain_on:
				productFun.setOpen(false);
				btnCurtainOn.setSelected(true);
				btnCurtainOff.setSelected(false);
				btnCurtainStop.setSelected(false);
				curtainControl(getActivity(), productFun, funDetail, null, "up");
				csbCurtainSeekBarNoSeek.setProgress(100);
				break;
			case R.id.btn_curtain_off:
				productFun.setOpen(true);
				btnCurtainOn.setSelected(false);
				btnCurtainOff.setSelected(true);
				btnCurtainStop.setSelected(false);
				curtainControl(getActivity(), productFun, funDetail, null, "down");
				csbCurtainSeekBarNoSeek.setProgress(0);
				break;
			case R.id.btn_curtain_stop:
				btnCurtainOn.setSelected(false);
				btnCurtainOff.setSelected(false);
				btnCurtainStop.setSelected(true);
				curtainControl(getActivity(), productFun, funDetail, null, "stop");
				csbCurtainSeekBarNoSeek.setProgress(50);
				break;
			case R.id.btn_curtain_step_1:
				params.put(StaticConstant.PARAM_TEXT, 33);
				curtainControl(getActivity(), productFun, funDetail, params, "offset");
				updateProgress(33);
				btnCurtainStep1.setSelected(true);
				btnCurtainStep2.setSelected(false);
				btnCurtainStep3.setSelected(false);
				btnCurtainStep4.setSelected(false);
				break;
			case R.id.btn_curtain_step_2:
				params.put(StaticConstant.PARAM_TEXT, 50);
				curtainControl(getActivity(), productFun, funDetail, params, "offset");
				updateProgress(50);
				btnCurtainStep1.setSelected(false);
				btnCurtainStep2.setSelected(true);
				btnCurtainStep3.setSelected(false);
				btnCurtainStep4.setSelected(false);
				break;
			case R.id.btn_curtain_step_3:
				params.put(StaticConstant.PARAM_TEXT, 66);
				curtainControl(getActivity(), productFun, funDetail, params, "offset");
				updateProgress(66);
				btnCurtainStep1.setSelected(false);
				btnCurtainStep2.setSelected(false);
				btnCurtainStep3.setSelected(true);
				btnCurtainStep4.setSelected(false);
				break;
			case R.id.btn_curtain_step_4:
				params.put(StaticConstant.PARAM_TEXT, 75);
				curtainControl(getActivity(), productFun, funDetail, params, "offset");
				updateProgress(75);
				btnCurtainStep1.setSelected(false);
				btnCurtainStep2.setSelected(false);
				btnCurtainStep3.setSelected(false);
				btnCurtainStep4.setSelected(true);
				break;
			case R.id.back_btn:
				getDialog().dismiss();
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
}
