package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
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
import com.jinxin.jxtouchscreen.model.WHproductUnfrared;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/26.
 */
public class InfraredTranspondGridViewAdapter extends QuickAdapter<WHproductUnfrared> {

	private ProductFun productFun;
	private FunDetail funDetail;
	private Context mContext;

	public InfraredTranspondGridViewAdapter(Context context, int res, List<WHproductUnfrared> data, ProductFun productFun, FunDetail funDetail) {
		super(context, res, data);
		mContext = context;
		this.productFun = productFun;
		this.funDetail = funDetail;
	}

	@Override
	public View getItemView(int position, View convertView, ViewHolder holder) {
		ImageView imageView = holder.getView(R.id.infrared_traspond_gv_imag);
		final WHproductUnfrared wHproductUnfrared = data.get(position);
		TextView textView = holder.getView(R.id.infrared_traspond_gv_iv);
		textView.setText(wHproductUnfrared.getRecordName());
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				plutoSoundBoxControl(productFun, funDetail, "0C", wHproductUnfrared.getInfraRedCode());
			}
		});
		return convertView;
	}

	/**
	 * 发送控制命令
	 *
	 * @param productFun 产品
	 * @param funDetail  产品详情
	 * @param cmdStr     命令
	 */
	public void plutoSoundBoxControl(ProductFun productFun, FunDetail funDetail, String cmdStr, String infraredCode) {
		Map<String, Object> params = new HashMap<>();
		params.put("src", "0x01");
		params.put("dst", "0x01");
		params.put("type", "00 32");
		params.put("op", cmdStr);
		StringBuffer buffer = new StringBuffer(infraredCode);
		int count = 0;
		for (int i = 2; i < infraredCode.length(); i += 2) {
			buffer.insert(i + count, " ");
			count += 1;
		}
		params.put(StaticConstant.PARAM_TEXT, buffer.toString() + " ");
		String type = StaticConstant.OPERATE_COMMON_CMD;
		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(mContext, NetConstant.hostForZigbee(),
				CmdType.CMD_ZG, cmdList, true, 0);
		onlineSender.addListener(listener);
		onlineSender.send();
	}

	/**
	 * 应答监听
	 */
	TaskListener<Task> listener = new TaskListener<Task>() {

		@Override
		public void onFail(Task task, Object[] arg) {
			ToastUtil.showShort(mContext, R.string.operate_failed);
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
			} else {
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
			}
		}

		@Override
		public void onSuccess(Task task, Object[] arg) {
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//			if ("-1".equals(resultObj.validResultInfo)) {
//				ToastUtil.showShort(mContext, R.string.operate_failed);
//				return;
//			}
			String resultStr = resultObj.validResultInfo;
			String payload = resultStr.substring(resultStr.indexOf("[") + 1, resultStr.indexOf("]"));
			ToastUtil.showShort(mContext, getResponseStr(payload));
		}

	};

	/**
	 * 获取状态字符串
	 *
	 * @param result 应答字符串
	 * @return string 状态字符
	 */
	protected String getResponseStr(String result) {
		if (result == null)
			return null;
		StringBuffer stb = new StringBuffer();
		int length = result.length();
		String statusCode = result.substring(length - 9, length - 1);
		String[] code = statusCode.split(" ");
		for (int i = 0; i < code.length; i++) {
			System.out.println("code=" + code[i] + "==");
		}
		if (code[code.length - 1].equals("00")) {//读取功率数值
			stb.append("发送成功");
		} else {
			stb.append("发送失败");
		}

		return stb.toString();
	}

}
