package com.jinxin.jxtouchscreen.fragment.devicenispection;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.cmd.response.RegexConstants;
import com.jinxin.jxtouchscreen.cmd.response.ResponseParserFactory;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.event.UpdateFailedEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.model.Device;
import com.jinxin.jxtouchscreen.model.ZigbeeResponse;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by XTER on 2016/8/29.
 * 设备巡检
 */
public class DeviceInspectionFragment extends DialogFragment {
	private static final String STATE = "STATE";
	private static final String DID = "DEVICEID";
	private static final String RESULT = "RESULT";

	private Button btnInstantInspect;
	private ListView lvDeviceInspection;
	private DeviceInpectionAdapter deviceInpectionAdapter;

	private static final int OPERATE_BEGIN = 1000;
	private static final int OPERATE_SUCCESS = 1001;
	private static final int OPERATE_FAIL = 1002;
	private static final int OPERATE_UPDATE_STATE = 1003;
	private static final int OPERATE_UPDATE_WIRELESS_GATEWAY = 1005;
	private static final int OPERATE_END = 1004;

	private List<Device> devices = new ArrayList<>();
	private List<String> zgDevices;
	private ConcurrentLinkedQueue<Device> priorDevice = new ConcurrentLinkedQueue<Device>();
	private volatile boolean oneKeyDetecting = false;

	boolean isRunning = false;
	private int WG_NULL = -1;

	//设备状态判断
	TextView tvStateAbnormal;
	TextView tvStateNormal;

	private Context friendContext;
	private SQLiteDatabase db;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		EventBus.getDefault().register(this);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_inspection_fragment, container, false);
		initView(rootView);
		return rootView;
	}

	protected void initData() {
		devices = new ArrayList<>();
		zgDevices = new ArrayList<>();
		getDataFromDb();
	}

	protected void getDataFromDb() {

        /*设置适配*/
		//得到无线设备
		db = BaseApp.getContext().openOrCreateDatabase(LocalConstant.DB_DEFAULT_NAME, Context.MODE_WORLD_READABLE, null);
		Cursor zgDeviceCursor = null;
		try {
			zgDeviceCursor = db.rawQuery(
					"select whId, code from productregister where gatewayWhId in (select whId from productregister where code = 'G102')", new String[]{});
		} catch (Exception e) {
			e.printStackTrace();
			db = BaseApp.getContext().openOrCreateDatabase(LocalConstant.DB_DEFAULT_NAME, Context.MODE_WORLD_READABLE, null);
			zgDeviceCursor = db.rawQuery(
					"select whId, code from productregister where gatewayWhId in (select whId from productregister where code = 'G102')", new String[]{});
		} finally {
			if (zgDeviceCursor != null) {
				while (zgDeviceCursor.moveToNext()) {
					String whId = zgDeviceCursor.getString(0);
					zgDevices.add(whId);
				}
				zgDeviceCursor.close();
			}
			//得到所有设备
			Cursor deviceCursor = db.rawQuery(
					"SELECT DISTINCT b.whId, b._id,b.address485, c.alias, c.funType, b.code,a.gatewayWhId FROM productregister AS a INNER JOIN customerproduct AS b on a.whId = b.whId INNER JOIN fundetailconfig AS c on a.whId = c.whId ORDER BY c.funType",
					new String[]{});
			devices.clear();
			//一一过滤
			Device device;
			if (deviceCursor != null) {
				while (deviceCursor.moveToNext()) {
					device = new Device();
					device.setAddress485(deviceCursor.getString(2));
					device.setWhId(deviceCursor.getString(0));
					device.setAlias(deviceCursor.getString(3));
					device.setFunType(deviceCursor.getString(4));
					if (zgDevices.contains(device.getWhId())) {
						device.setCode(deviceCursor.getString(5));
						device.setZg(true);
					} else {
						device.setCode("0".concat(deviceCursor.getString(5)));
						device.setZg(false);
					}
					device.setGatewayId(deviceCursor.getString(6));
					devices.add(device);
				}
				deviceCursor.close();

			}
			db.close();
		}

	}

	protected void initView(View view) {
		btnInstantInspect = (Button) view.findViewById(R.id.btn_instant_inspect);
		lvDeviceInspection = (ListView) view.findViewById(R.id.lv_device_inpection);
		view.findViewById(R.id.iv_backbtn_inspection).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//CommUtil.hideFragment(getFragmentManager(),"deviceInspectionFragment");
				getDialog().dismiss();
			}
		});
		btnInstantInspect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//一键巡检
				deviceDetect();
			}
		});

		deviceInpectionAdapter = new DeviceInpectionAdapter(getActivity(), R.layout.item_device_inspection_layout, devices);
		lvDeviceInspection.setAdapter(deviceInpectionAdapter);
		isRunning = true;
	}

	//一键巡检
	private void deviceDetect() {
		//无线网关单独巡检
		int index = findWG();
		if (WG_NULL != index) {
			getWirelessGatewaystate(devices.get(index).getWhId());
		}
		sendDeviceDetectCmd(devices);
	}


	class DeviceInpectionAdapter extends QuickAdapter<Device> {
		public DeviceInpectionAdapter(Context context, int res, List<Device> data) {
			super(context, res, data);
			this.context = context;
		}

		@Override
		public View getItemView(final int position, View convertView, ViewHolder holder) {
			TextView tvName = holder.getView(R.id.tv_device_name);
			TextView tvWhId = holder.getView(R.id.tv_device_whid);
			Button btnInspect = holder.getView(R.id.btn_device_inspect);

			tvStateNormal = holder.getView(R.id.tv_device_state_normal);
			tvStateAbnormal = holder.getView(R.id.tv_device_state_abnormal);

			tvName.setText(data.get(position).getAlias());
			tvWhId.setText(data.get(position).getWhId());
			btnInspect.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//单个设备巡检
					Device dev = data.get(position);
					if (dev.getCode().equals("G102")) {
						getWirelessGatewaystate(dev.getWhId());
					} else {
						priorDevice.add(dev);
					}
					if (!oneKeyDetecting) {
						sendDeviceDetectCmd(new ArrayList<Device>());
					}
				}
			});

			String state = data.get(position).getState();
			if (!TextUtils.isEmpty(state)) {
				if (state.equals("设备正常")) {
					tvStateNormal.setVisibility(View.VISIBLE);
					tvStateAbnormal.setVisibility(View.INVISIBLE);
				} else {
					tvStateNormal.setVisibility(View.INVISIBLE);
					tvStateAbnormal.setVisibility(View.VISIBLE);
				}
			} else {
				tvStateNormal.setVisibility(View.INVISIBLE);
				tvStateAbnormal.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}
	}

	//发送设备巡检指令
	private void sendDeviceDetectCmd(List<Device> devices) {
		List<byte[]> cmds = new ArrayList<byte[]>();
		for (Device device : devices) {
			if (device.isZg()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(StaticConstant.OPERATE_SET_TYPE, device.getCode());
				device.setFunType(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT);
				cmds.addAll(CmdBuilder.build().generateCmdDetectForZg(device, params, null));
			} else {
				cmds.addAll(CmdBuilder.build().generateCmdDetectFor485(device.getWhId(), device.getCode()));
			}
		}
		if (cmds == null || cmds.size() < 1) {
			//return;
		}
		Stack<byte[]> ppos = new Stack<byte[]>();
		for (int i = cmds.size() - 1; i >= 0; i--) {
			ppos.push(cmds.get(i));
		}
		L.d(ppos.size() + "");
		if (isRunning) {
			patternOperationSend(ppos);
		}
	}

	//CommonDeviceControlByServerTask cdcbsTask;
	private String currentWhId = "";
	Stack<byte[]> ppos;

	//处理单个巡检
	private void patternOperationSend(final Stack<byte[]> ppos) {
		TaskListener listener = new TaskListener() {
			@Override
			public void onFail(Task task, Object[] arg) {
				L.i("单个巡检失败了");

				Message message = Message.obtain();
				message.what = OPERATE_FAIL;
				Bundle data = new Bundle();
				data.putString(STATE, "设备离线");
				data.putString(DID, currentWhId);
				message.setData(data);
				handler.sendMessage(message);

				if (isRunning) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					patternOperationSend(ppos);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				L.i("单个巡检成功了");
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					if (resultObj.validResultCode.equals("0000")) {
						String result = resultObj.validResultInfo;
						L.d(result);
						if ("01".equals(result) || "".equals(result)) {
							L.i("----", "网关离线");
							//finishOneKeyDetect();
						} else {
							Message msg = Message.obtain();
							Bundle data = new Bundle();
							data.putString(DID, currentWhId);
							if (result != null) {
								data.putString(RESULT, result);
								msg.what = OPERATE_UPDATE_STATE;
							} else {
								msg.what = OPERATE_FAIL;
							}
							msg.setData(data);
							handler.sendMessage(msg);
						}
					}
				}
				if (isRunning) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					patternOperationSend(ppos);
				}
			}
		};
		this.ppos = ppos;
		if (priorDevice.size() > 0) {
			//从队列中取出一个设备
			Device prior = priorDevice.poll();
			//组装命令
			List<byte[]> cmds = new ArrayList<>();
			if (prior.isZg()) {
				Map<String, Object> params = new HashMap<>();
				params.put(StaticConstant.OPERATE_SET_TYPE, prior.getCode());
				prior.setFunType(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT);
				cmds.addAll(CmdBuilder.build().generateCmdDetectForZg(prior, params, null));
			} else {
				cmds.addAll(CmdBuilder.build().generateCmdDetectFor485(prior.getWhId(), prior.getCode()));
			}
			//压入命令栈中
			for (int i = 0; i < cmds.size(); i++) {
				ppos.push(cmds.get(i));
			}
			currentWhId = prior.getWhId();
		} else {// 一键巡检
			if (ppos == null || ppos.empty()) {
				//finishOneKeyDetect();
				return;
			}
			Device dev = devices.get(devices.size() - ppos.size());
			currentWhId = dev.getWhId();
		}

		byte[] _ppo = ppos.pop();

		if (_ppo != null) {

			List<byte[]> cmdAdd = new ArrayList<>();
			cmdAdd.add(_ppo);
			if (zgDevices.contains(currentWhId)) {// zg
				OnlineCmdSenderLong onlinesender = new OnlineCmdSenderLong(getActivity(), NetConstant.hostForZigbee(), CmdType.CMD_ZG_INSPECT, cmdAdd, true, 1);
				onlinesender.addListener(listener);
				onlinesender.send();
			} else {
				OnlineCmdSenderLong onlinesender = new OnlineCmdSenderLong(getActivity(), NetConstant.hostFor485(), CmdType.CMD_485, cmdAdd, true, 1);
				onlinesender.addListener(listener);
				onlinesender.send();
			}
		}
	}

	//无线网关单个巡检
	private void getWirelessGatewaystate(String whId) {
		JSONObject jo = new JSONObject();
		jo.put("area", "gateway_state");
		jo.put("time", SysUtil.getNow2());
		JSONObject jo1 = new JSONObject();
		jo1.put("gatewayWhId", whId);
		jo.put("content", jo1);
		UpdateDataService.updateSpecialData(jo);
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onQueryGateway(UpdateFinishEvent event) {
		Message msg = Message.obtain();
		msg.what = OPERATE_UPDATE_WIRELESS_GATEWAY;
		Bundle data = new Bundle();
		data.putString(STATE, "设备正常");
		msg.setData(data);
		handler.sendMessage(msg);
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onQueryGateway(UpdateFailedEvent event) {
		Message msg = Message.obtain();
		msg.what = OPERATE_UPDATE_WIRELESS_GATEWAY;
		Bundle data = new Bundle();
		data.putString(STATE, "网关离线");
		msg.setData(data);
		handler.sendMessage(msg);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case OPERATE_UPDATE_WIRELESS_GATEWAY:
					int index = findWG();
					if (WG_NULL == index) {
						return;
					}
					Device wgDevice = devices.get(index);
					Bundle wgBundle = msg.getData();
					String wgState = wgBundle.getString(STATE);
					wgDevice.setState(wgState);
					if ("网关离线".equals(wgState)) {
						break;
					}
					deviceInpectionAdapter.notifyDataSetChanged();
					break;
				case OPERATE_FAIL:
					Bundle bundleF = msg.getData();
					Device deviceF = findByWhId(bundleF.getString(DID));
					if (deviceF != null) {
						if (TextUtils.isEmpty(bundleF.getString(STATE)))
							deviceF.setState("连接超时");
						else
							deviceF.setState(bundleF.getString(STATE));
					}
					tvStateAbnormal.setVisibility(View.VISIBLE);
					tvStateAbnormal.setText(deviceF.getState());
					deviceInpectionAdapter.notifyDataSetChanged();
					break;
				case OPERATE_UPDATE_STATE:
					Bundle bundle = msg.getData();
					String response = bundle.getString(RESULT);
					Device device = findByWhId(bundle.getString(DID));
					if (device != null && device.getCode().equals("G102")) {
						break;
					}
					if (zgDevices.contains(bundle.getString(DID))) {
						if (parseResponseData(response)) {
							device = findByWhId(bundle.getString(DID));
							if (device != null) {
								device.setState("设备正常");
								//lvDeviceInspection.setAdapter(deviceInpectionAdapter);
								tvStateNormal.setVisibility(View.VISIBLE);
								deviceInpectionAdapter.notifyDataSetChanged();
								break;
							}
						}
					} else if (response != null && response.length() >= 24) {
						String whId = response.substring(3, 19);
						String state = response.substring(19, 23);
						if (response.length() < 29) {
							state = response.substring(19, 21);
						}
						device = findByWhId(whId);
						if (device != null) {
							device.setState(convertState(state));
							deviceInpectionAdapter.notifyDataSetChanged();
							break;
						} else {
							device = findByWhId(bundle.getString(DID));
							if (device != null) {
								device.setState(convertState(state));
								deviceInpectionAdapter.notifyDataSetChanged();
								break;
							}
						}
						tvStateAbnormal.setVisibility(View.VISIBLE);
						deviceInpectionAdapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
			}
		}
	};

	private String convertState(String val) {
		if (val == null || val.equals("")) {
			return "";
		}
		if ("0000".equals(val)) {
			return "设备正常";
		} else if ("0001".equals(val)) {
			return "连接超时";
		} else if ("0002".equals(val)) {
			return "设备损坏";
		} else if ("0003".equals(val)) {
			return "未知异常";
		} else if ("00".equals(val)) {
			return "设备正常";
		}

		return "其他";
	}

	private Device findByWhId(String whId) {
		if (TextUtils.isEmpty(whId)) {
			return null;
		}
		for (Object object : devices) {
			Device device = (Device) object;
			if (whId.equalsIgnoreCase(device.getWhId())) {
				return device;
			}
		}
		return null;
	}

	//查找无线网关
	private int findWG() {
		for (int i = 0; i < devices.size(); i++) {
			if (devices.get(i).getCode().equals("G102")) {
				return i;
			}
		}
		return WG_NULL;
	}

	//解析数据，返回结果
	private boolean parseResponseData(String result) {
		ZigbeeResponse data = ResponseParserFactory.parseContent(result, RegexConstants.ZG_BASIC_CONTENT_REP);

		String payload = data.getPayload();
		if (!TextUtils.isEmpty(payload)) {
			String[] items = payload.split(" ");
			if (items.length > 6) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
