package com.jinxin.jxtouchscreen.fragment.gateway;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.event.UpdateFailedEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.net.HttpManager;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by zj on 2016/10/18.
 */
public class DeviceGatewayFragment extends DialogFragment {
	@Bind(R.id.tv_message_time)
	TextView tvMessageTime;
	@Bind(R.id.iv_networking)
	ImageView ivNetworking;
	@Bind(R.id.iv_query)
	ImageView ivQuery;
	@Bind(R.id.tv_action_build_net_success)
	TextView tvActionBuildNetSuccess;
	@Bind(R.id.iv_action_build_net_success)
	ImageView ivActionBuildNetSuccess;
	private ProductFun productFun;
	private FunDetail funDetail;
	/**
	 * 当前是否可点击
	 */
	private boolean isClickable = true;

	private TaskListener<Task> listener = new TaskListener<Task>() {
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
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				L.d(null, resultObj.toString());

				// "0000":正常的返回   "-1":结果不需要做解析
				/*if (resultObj.validResultCode.equals("0000") && !"-1".equals(resultObj.validResultInfo)) {
					ToastUtil.showShort(getActivity(), "组网成功");*/
				handler.obtainMessage(0).sendToTarget();
				/*} else {
					handler.obtainMessage(1).sendToTarget();
				}*/
			}
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_gateway_fragment, container, false);
		ButterKnife.bind(this, rootView);
		initView(rootView);
		return rootView;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new Dialog(getContext(), getTheme());
	}

	private void initView(View rootView) {
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		tvMessageTime.setText(productFun.getFunName());

		ivNetworking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (productFun == null || funDetail == null) {
					return;
				}
				if (!isClickable) {
					ToastUtil.showShort(getActivity().getApplicationContext(), R.string.operate_invalid_work);
					return;
				}
				isClickable = false;
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("text", "0x3c");//十六进制时间:0x3c = 60s
				String type = "networking";
				List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
				OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
						NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 0);
				onlineSender.addListener(listener);
				onlineSender.send();
			}
		});
		ivQuery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isClickable) {
					ToastUtil.showShort(getActivity().getApplicationContext(), R.string.operate_invalid_work);
					return;
				}
				isClickable = false;
				if (productFun != null) {
					JSONObject jo = new JSONObject();
					jo.put("area", "gateway_state");
					jo.put("time", SysUtil.getNow2());
					JSONObject jo1 = new JSONObject();
					jo1.put("gatewayWhId", productFun.getWhId());
					jo.put("content", jo1);
					UpdateDataService.updateSpecialData(jo);
//					HttpManager.getInstance().addRequest(new StringRequest(Request.Method.GET, NetConstant.GATEWAY_PATH + productFun.getWhId(), new Response.Listener<String>() {
//						@Override
//						public void onResponse(String response) {
//							L.d(response);
//							if (response.contains("ip"))
//								ToastUtil.showShort(getActivity(), "网关在线");
//							isClickable = true;
//						}
//					}, new Response.ErrorListener() {
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							ToastUtil.showShort(getActivity(), "无法获取网关状态");
//							isClickable = true;
//							L.e(error.getMessage());
//						}
//					}));
				}
			}
		});
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onQueryGateway(UpdateFinishEvent event) {
		ToastUtil.showShort(getActivity(), "网关在线");
		isClickable = true;
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onQueryGateway(UpdateFailedEvent event) {
		ToastUtil.showShort(getActivity(), "无法获取网关状态");
		isClickable = true;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					ivActionBuildNetSuccess.setImageResource(R.drawable.tick);
					tvActionBuildNetSuccess.setText("组网成功");
					break;
				case 1:
					ivActionBuildNetSuccess.setImageResource(R.drawable.fork);
					tvActionBuildNetSuccess.setText("组网失败");
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
	}

	@OnClick({R.id.back_btn})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				getDialog().dismiss();
				break;
		}
	}
}
