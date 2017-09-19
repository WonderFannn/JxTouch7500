package com.jinxin.jxtouchscreen.fragment.wireless;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductRegister;
import com.jinxin.jxtouchscreen.model.ProductState;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.util.Utils;
import com.jinxin.jxtouchscreen.widget.ColorPickerView;
import com.jinxin.jxtouchscreen.widget.ManaMySeekBar;
import com.jinxin.jxtouchscreen.widget.wheelview.ArrayWheelAdapter;
import com.jinxin.jxtouchscreen.widget.wheelview.OnWheelChangedListener;
import com.jinxin.jxtouchscreen.widget.wheelview.WheelView;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 灯光
 */
public class IlluminationFragment extends DialogFragment {

	@Bind(R.id.illumination_lamp)
	ImageButton illuminationLamp;
	@Bind(R.id.illumination_color_lamp)
	ImageButton illuminationColorLamp;
	@Bind(R.id.illumination_color_disk)
	ColorPickerView illuminationColorDisk;
	@Bind(R.id.illumination_tv_current_color)
	TextView illuminationTvCurrentColor;
	@Bind(R.id.illumination_tv_color_changed)
	TextView illuminationTvColorChanged;
	@Bind(R.id.illumination_checkbox)
	ImageButton illuminationCheckbox;
	@Bind(R.id.illumination_tv_brightness_subtract)
	TextView illuminationTvBrightnessSubtract;
	@Bind(R.id.illumination_seecbar)
	SeekBar illuminationSeecbar;
	@Bind(R.id.illumination_tv_brightness_plus)
	TextView illuminationTvBrightnessPlus;
	@Bind(R.id.illumination_brightness_prl)
	PercentRelativeLayout illuminationBrightnessPrl;
	@Bind(R.id.illumination_tv_transparent_subtract)
	TextView illuminationTvTransparentSubtract;
	@Bind(R.id.illumination_transparent_seckbar)
	SeekBar illuminationTransparentSeckbar;
	@Bind(R.id.illumination_tv_transparent_plus)
	TextView illuminationTvTransparentPlus;
	@Bind(R.id.illumination_color_current_im)
	ImageView illuminationColorCurrentIm;
	@Bind(R.id.illumination_color_light)
	PercentRelativeLayout illuminationColorLight;
	@Bind(R.id.illumination_light)
	PercentRelativeLayout illuminationLight;
	@Bind(R.id.illumination_color_changed)
	PercentRelativeLayout illuminationColorChanged;
	@Bind(R.id.illumination_color_unchanged)
	PercentRelativeLayout illuminationColorUnchanged;
	@Bind(R.id.illumination_seecbar_light)
	ManaMySeekBar illuminationSeecbarLight;
	@Bind(R.id.illumination_seecbar_changed)
	ManaMySeekBar illuminationSeecbarChanged;
	@Bind(R.id.illumination_left_wheelview)
	WheelView illuminationLeftWheelview;
	@Bind(R.id.illumination_right_wheelview)
	WheelView illuminationRightWheelview;
	private boolean clickLampFlag;
	private boolean clickCheckBoxFlag;
	private List<ProductRegister> productRegisters;
	private List<ProductState> productStates;
	private Context context;
	private static String[] MODES = new String[]{"", "渐亮", "渐暗", "魔幻灯光", ""};
	private static String[] MODE_TIME = new String[]{"01", "02",
			"03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
			"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
			"25", "26", "27", "28", "29", "30"};

	private static int a, r, g, b;
	private static int mColor = 0xffffffff;
	private String type = "";
	private ProductFun productFun;
	private FunDetail funDetail;
	private String fun_type = null;
	List<byte[]> cmdAll = new ArrayList<>();
	private int controlType = 0;    // 控制类型：0-彩灯，1-白灯
	private int fadeMode = 1;
	private String fadeTime = "1";
	private boolean colorChangedFlag;
	private int whiteLight = 0;
	private int funId = -1;
	private int fadeValueNow;
	private boolean isClickable = true;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		context = getContext();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_illumination, container, false);
		ButterKnife.bind(this, view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		onClick(illuminationColorLamp);
		illuminationColorDisk.setColorChangedListener(new ColorPickerView.OnColorChangedListener() {
			@Override
			public void onColorChanged(int color) {
				illuminationColorCurrentIm.setBackgroundColor(color);
				ToastUtil.showShort(context, R.string.light_color_switch_success);

				mColor = color;

				r = Color.red(mColor);
				g = Color.green(mColor);
				b = Color.blue(mColor);

				Map<String, Object> params = new HashMap<String, Object>();
				float[] hueAndSat = StringUtils.rgb2hsb(r, g, b);
				params.put("hue", StringUtils.integerToHexString(Math.round(hueAndSat[0])));
				params.put("sat", StringUtils.integerToHexString(Math.round(hueAndSat[1] * 100)));
				params.put("light", StringUtils.integerToHexString(Math.round(hueAndSat[2] * 100)));
				params.put("time", StringUtils.integerToHexString(1));
				type = "hueandsat";
				sendCmd(type, params);
			}
		});
		initData();
		illuminationSeecbarLight.setMax(100);
		//设置白灯的亮度
		illuminationSeecbarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
				// TODO Auto-generated method stub
				illuminationSeecbarLight.setSeekBarText("\t\n" + progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				List<ProductFun> productFunList = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funId = ?", new String[]{String.valueOf(funId)}));
				if (productFunList != null && productFunList.size() > 0) {
					whiteLight = seekBar.getProgress();
					/* 操作类型(关灯) */
					type = "mvToLevel";
				    /* 操作参数  */
					Map<String, Object> params = new HashMap<>();
			        /* 亮度 */
					params.put("light", StringUtils.integerToHexString(seekBar.getProgress()));
			        /* 操作时间  */
					params.put("time", StringUtils.integerToHexString(1));
			       /* 设备目标地址  */
					params.put("src", "0x01");
			        /* 亮度关*/
					params.put("lightColor", StringUtils.integerToHexString(0));
			       /* 关的时间 */
					params.put("timeColor", StringUtils.integerToHexString(1));
					params.put("dstColor", "0x02");
					sendCmd(type, params, productFunList);
				}
			}

		});
		illuminationSeecbarChanged.setMax(100);
		//设置颜色变换的亮度
		illuminationSeecbarChanged.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				fadeValueNow = (int) (seekBar.getProgress() * 255 / 100.0);
				if (fadeMode == 2) { //魔幻灯光
					operateColorAutoMode();
				} else { //颜色渐变切换
					operateColorFade(fadeMode, fadeTime);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
			                              boolean fromUser) {
				// TODO Auto-generated method stub
				illuminationSeecbarChanged.setSeekBarText("\t\n" + "\t\n" + progress);

			}

		});
		//亮度设置（颜色选择）
		illuminationSeecbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int colorAtLightness = Utils.colorAtLightness(mColor, progress / 100f);
				L.i("OnValueChangedListener：" + Utils.colorAtLightness(colorAtLightness, progress / 100f));
				mColor = colorAtLightness;
				illuminationColorCurrentIm.setBackgroundColor(mColor);
				r = Color.red(mColor);
				g = Color.green(mColor);
				b = Color.blue(mColor);

				Map<String, Object> params = new HashMap<String, Object>();
				float[] hueAndSat = StringUtils.rgb2hsb(r, g, b);
				params.put("hue", StringUtils.integerToHexString(Math.round(hueAndSat[0])));
				params.put("sat", StringUtils.integerToHexString(Math.round(hueAndSat[1] * 100)));
				params.put("light", StringUtils.integerToHexString(Math.round(hueAndSat[2] * 100)));
				params.put("time", StringUtils.integerToHexString(1));
				type = "hueandsat";
				sendCmd(type, params);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		//透明度设置（颜色选择）
		illuminationTransparentSeckbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int colorAtLightness = Utils.colorAtLightness(mColor, progress / 100f);
				L.i(progress / 100f + "OnValueChangedListener：" + Utils.colorAtLightness(colorAtLightness, progress / 100f));
				mColor = colorAtLightness;
				illuminationColorCurrentIm.setAlpha(progress / 100f);
				illuminationColorDisk.setAlpha(progress / 100f);
				r = Color.red(mColor);
				g = Color.green(mColor);
				b = Color.blue(mColor);

				Map<String, Object> params = new HashMap<String, Object>();
				float[] hueAndSat = StringUtils.rgb2hsb(r, g, b);
				params.put("hue", StringUtils.integerToHexString(Math.round(hueAndSat[0])));
				params.put("sat", StringUtils.integerToHexString(Math.round(hueAndSat[1] * 100)));
				params.put("light", StringUtils.integerToHexString(Math.round(hueAndSat[2] * 100)));
				params.put("time", StringUtils.integerToHexString(1));
				type = "hueandsat";
				sendCmd(type, params);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		/*************************彩灯模式变换设置***************************/
		illuminationLeftWheelview.setCyclic(true);
		illuminationRightWheelview.setCyclic(true);
		illuminationLeftWheelview.setLabel("");
		illuminationRightWheelview.setLabel("S");
		illuminationLeftWheelview.setAdapter(new ArrayWheelAdapter<String>(MODES));
		illuminationRightWheelview.setAdapter(new ArrayWheelAdapter<String>(MODE_TIME));
		illuminationLeftWheelview.setCurrentItem(1);
		illuminationRightWheelview.setCurrentItem(1);
		illuminationLeftWheelview.addChangingListener(changedListener);
		illuminationRightWheelview.addChangingListener(changedListener);
		/********************end*****************************************/
	}

	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			switch (wheel.getId()) {
				case R.id.illumination_left_wheelview:
					fadeMode = illuminationLeftWheelview.getCurrentItem();
					if (fadeMode == 0 || fadeMode == 4) {
						illuminationRightWheelview.setVisibility(View.INVISIBLE);
					} else {
						illuminationRightWheelview.setVisibility(View.VISIBLE);
					}
					if (fadeMode == 3) { //魔幻灯光
						operateColorAutoMode();
					} else { //颜色渐变切换
						operateColorFade(fadeMode, fadeTime);
					}
					break;
				case R.id.illumination_right_wheelview:
					fadeTime = MODE_TIME[illuminationRightWheelview.getCurrentItem()];
					if (fadeMode == 3) { //魔幻灯光
						operateColorAutoMode();
					} else { //颜色渐变切换
						operateColorFade(fadeMode, fadeTime);
					}
					break;

				default:
					break;
			}
		}
	};


	public void initData() {
		productRegisters = DBM.getCurrentOrm().query(ProductRegister.class);
		productStates = DBM.getCurrentOrm().query(ProductState.class);
		Bundle arguments = getArguments();
		productFun = (ProductFun) arguments.getSerializable("productFun");
		funDetail = (FunDetail) arguments.getSerializable("funDetail");
		if (funDetail != null)
			fun_type = funDetail.getFunType();
		funId = productFun.getFunId();
		if (productFun.getFunType().equals("01A")) {
			illuminationLamp.setVisibility(View.INVISIBLE);
			illuminationColorLamp.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick({R.id.back_btn, R.id.illumination_lamp, R.id.illumination_color_lamp, R.id.illumination_color_disk,
			R.id.illumination_checkbox, R.id.illumination_seecbar, R.id.illumination_transparent_seckbar, R.id.illumination_color_current_im})
	public void onClick(View view) {


		switch (view.getId()) {
			case R.id.back_btn:
				getDialog().dismiss();
				break;
			case R.id.illumination_lamp:
				colorChangedFlag = !colorChangedFlag;
				clickLampFlag = !clickLampFlag;
				illuminationLight.setVisibility(View.VISIBLE);
				illuminationColorLight.setVisibility(View.INVISIBLE);
				illuminationLamp.setSelected(clickLampFlag);
				illuminationColorLamp.setSelected(!clickLampFlag);
				onSwitchStateChange(colorChangedFlag);
				clickLampFlag = !clickLampFlag;
				if (productFun.getFunType().equals("01A")) {
					Toast.makeText(getActivity(), "当前为灯带的关闭状态", Toast.LENGTH_SHORT).show();
					illuminationLight.setVisibility(View.GONE);
				}
				break;
			case R.id.illumination_color_lamp:
				colorChangedFlag = !colorChangedFlag;
				clickLampFlag = !clickLampFlag;
				illuminationLight.setVisibility(View.INVISIBLE);
				illuminationColorLight.setVisibility(View.VISIBLE);
				illuminationLamp.setSelected(!clickLampFlag);
				illuminationColorLamp.setSelected(clickLampFlag);
				onSwitchStateChange(colorChangedFlag);
				clickLampFlag = !clickLampFlag;
				break;
			case R.id.illumination_color_disk:
				break;
			case R.id.illumination_checkbox:
				clickCheckBoxFlag = !clickCheckBoxFlag;
				if (clickCheckBoxFlag) {
					illuminationColorUnchanged.setVisibility(View.VISIBLE);
					illuminationColorChanged.setVisibility(View.INVISIBLE);
					illuminationTvColorChanged.setText("颜色变换");
				} else {
					illuminationColorUnchanged.setVisibility(View.INVISIBLE);
					illuminationColorChanged.setVisibility(View.VISIBLE);
					illuminationTvColorChanged.setText("颜色选择");

					/** 发送关闭指令（如果切换为彩灯，关白灯，反之亦然） */
		             /* 操作类型(关灯) */
					type = "mvToLevel";
		           /* 操作参数  */
					Map<String, Object> params = new HashMap<String, Object>();
		          /* 亮度 */
					//	params.put("light",  StringUtils.integerToHexString(whiteLight));
		           /* 操作时间  */
					params.put("time", StringUtils.integerToHexString(1));
		           /* 设备目标地址  */
					params.put("src", "0x01");
		           /* 亮度关*/
					params.put("lightColor", StringUtils.integerToHexString(0));
		          /* 关的时间 */
					params.put("timeColor", StringUtils.integerToHexString(1));

					List<ProductFun> productFunList = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funId = ?", new String[]{String.valueOf(funId)}));
					if (!productFun.getFunType().equals("01A")) {
						ToastUtil.showShort(context, "白灯转彩灯");
					}
					controlType = 0;
                       /* 亮度 */
					params.put("light", StringUtils.integerToHexString(100));
			          /* 设备关的目标（白灯）*/
					params.put("dstColor", "0x02");
					sendCmd(type, params, productFunList);
				}
				L.e(clickCheckBoxFlag + "");
				illuminationCheckbox.setSelected(clickCheckBoxFlag);
				break;
			case R.id.illumination_seecbar:
				break;
			case R.id.illumination_transparent_seckbar:
				break;
			case R.id.illumination_color_current_im:
				break;
		}
	}

	private void sendCmd(String type, Map<String, Object> params) {
		List<ProductFun> productFunList = new ArrayList<>();
		productFunList.add(productFun);
		Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
		for (int i = productFunList.size() - 1; i >= 0; i--) {
			_ppoStack.push(productFunList.get(i));
		}

		cmdAll.clear();
		L.e(_ppoStack.toString());
		patternOperationSendToChangColor(_ppoStack, type, params);
	}

	/**
	 * 模式指令发送（递归发送）
	 */
	private void patternOperationSendToChangColor(Stack<ProductFun> pfs, final String type, final Map<String, Object> params) {
//		if (!isClickable) {
//			ToastUtil.showShort(getContext(), R.string.operate_invalid_work);
//			return;
//		}
		isClickable = false;
		final Stack<ProductFun> _pfs = pfs;
		final ProductFun _pf = _pfs.pop();
		if (_pf != null) {
			params.put("src", "0x01");
			if (controlType == 0) {  // 控制类型：0-彩灯，1-白灯
				params.put("dst", "0x01");
			} else {
				params.put("dst", "0x02");
			}
			TaskListener<Task> listener = new TaskListener<Task>() {

				@Override
				public void onFail(Task task, Object[] arg) {

				}

				@Override
				public void onSuccess(Task task, Object[] arg) {

				}
			};
			/***********************************************************/
			//设置关白灯的指令参数， 控制魔幻灯光需要先执行关白灯操作
			Map<String, Object> _param = new HashMap<>();
			String closeType = "mvToLevel";
			if ("autoMode".equals(type)) {
				/* 操作类型(关白灯) */
				/* 操作参数  */
				/* 亮度 */
				_param.put("light", StringUtils.integerToHexString(100));
				/* 操作时间  */
				_param.put("time", StringUtils.integerToHexString(1));
				/* 设备目标地址  */
				_param.put("src", "0x01");
				/* 亮度关*/
				_param.put("lightColor", StringUtils.integerToHexString(0));
				/* 关的时间 */
				_param.put("timeColor", StringUtils.integerToHexString(1));
				/* 设备关的目标（白灯）*/
				_param.put("dstColor", "0x02");
			}
			/***********************************************************/
			if (_param != null && _param.size() > 0) {   // 生成关白灯指令，仅炫彩灯光控制时需要
				List<byte[]> closeList = CmdBuilder.build().generateCmd(_pf, funDetail, _param, closeType);
				cmdAll.addAll(closeList);
			}
			List<byte[]> cmdList = CmdBuilder.build().generateCmd(_pf, funDetail, params, type);
			cmdAll.addAll(cmdList);

			/***************打印生成的命令*********************************/
//			byte[] allCmd = null;
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			try {
//				for (int i = 0; i < cmdList.size(); i++) {
//					bos.write(cmdList.get(i));
//				}
//				allCmd = bos.toByteArray();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

			if (!pfs.isEmpty()) {
				patternOperationSendToChangColor(_pfs, type, params);
			} else {
				OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG,
						cmdAll, true, 0);
				onlineSender.addListener(listener);
				if (cmdAll != null && cmdAll.size() > 0) {
					onlineSender.send();
				}
			}
		}
	}

	// 控制白灯按钮
	public void onSwitchStateChange(boolean isOn) {
//        tgSwitchLight.setBackgroundResource(isOn?R.drawable.btn_toggle_big_on:R.drawable.btn_toggle_big_off);
		/** 发送关闭指令（如果切换为彩灯，关白灯，反之亦然） */
		             /* 操作类型(关灯) */
		type = "mvToLevel";
		           /* 操作参数  */
		Map<String, Object> params = new HashMap<String, Object>();
		          /* 亮度 */
		//	params.put("light",  StringUtils.integerToHexString(whiteLight));
		           /* 操作时间  */
		params.put("time", StringUtils.integerToHexString(1));
		           /* 设备目标地址  */
		params.put("src", "0x01");
		           /* 亮度关*/
		params.put("lightColor", StringUtils.integerToHexString(0));
		          /* 关的时间 */
		params.put("timeColor", StringUtils.integerToHexString(1));
		List<ProductFun> productFunList = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funId = ?", new String[]{String.valueOf(funId)}));
//                DBConfig.getCurrentUserOrm(context).query(new QueryBuilder<>(ProductFun.class)
//                .where("funId=?",new String[]{String.valueOf(funId)}));
		if (productFunList != null && productFunList.size() > 0) {
			if (!isOn && !productFun.getFunType().equals("01A")) {
				ToastUtil.showShort(context, "彩灯转白灯");
//                this.radio_color_select.setSelected(false);
//                this.radio_color_fade.setSelected(false);
//
//                this.ll_white_light_layout.setVisibility(View.VISIBLE);
//                this.color_select_layout.setVisibility(View.GONE);
//                this.color_fade_layout.setVisibility(View.GONE);
//                this.rl_select_light_color.setVisibility(View.VISIBLE);  // 显示当前颜色和白灯模式控件
//                tv_current_color.setBackgroundColor(Color.WHITE);

				controlType = 1;
                       /* 亮度 */
				params.put("light", StringUtils.integerToHexString(whiteLight + 1));
			           /* 设备关的目标（彩灯）*/
				params.put("dstColor", "0x01");
				sendCmd(type, params, productFunList);
			} else {
				if (!productFun.getFunType().equals("01A")) {
					ToastUtil.showShort(context, "白灯转彩灯");
				}
				controlType = 0;
                       /* 亮度 */
				params.put("light", StringUtils.integerToHexString(100));
			          /* 设备关的目标（白灯）*/
				params.put("dstColor", "0x02");
				sendCmd(type, params, productFunList);
			}
		}
	}

	private void sendCmd(String type, Map<String, Object> params, List<ProductFun> productFunList) {
		Stack<ProductFun> _ppoStack = new Stack<ProductFun>();
		for (int i = productFunList.size() - 1; i >= 0; i--) {
			_ppoStack.push(productFunList.get(i));
		}

		cmdAll.clear();
		patternOperationSendToChangColor(_ppoStack, type, params);
//        JxshApp.isClinkable = true;
	}

	/**
	 * 魔幻灯光
	 */
	private void operateColorAutoMode() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hue", StringUtils.integerToHexString(0));
		params.put("sat", StringUtils.integerToHexString(254));
		params.put("light", StringUtils.integerToHexString(1));
		params.put("step", StringUtils.integerToHexString(45));
		params.put("time", StringUtils.integerToHexString(5));
		sendCmd("autoMode", params);
	}

	/**
	 * 颜色渐变
	 *
	 * @param mode
	 * @param time
	 */
	private void operateColorFade(int mode, String time) {
//		Editable timeEditor = mFadeTime.getText();
		String timeEditor = time;
		String timeText = "1";
		if (timeEditor != null) timeText =
				TextUtils.isEmpty(timeEditor) ? "1" : timeEditor;
		int fadeTime = Integer.parseInt(timeText);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", StringUtils.integerToHexString(mode));
		params.put("step", StringUtils.integerToHexString(fadeValueNow));
		params.put("time", StringUtils.integerToHexString(fadeTime * 10));
		sendCmd("step", params);
	}
}
