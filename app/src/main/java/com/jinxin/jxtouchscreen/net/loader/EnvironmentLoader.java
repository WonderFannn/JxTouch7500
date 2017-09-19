package com.jinxin.jxtouchscreen.net.loader;

import android.app.Activity;
import android.content.Context;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.model.Environment;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.StringUtils;

import java.util.List;


public class EnvironmentLoader {
	private Context context;
	private OnDataLoadListener loadListener;

	public EnvironmentLoader(Context context, OnDataLoadListener loadListener) {
		this.context = context;
		this.loadListener = loadListener;
	}

	private TaskListener<Task> listener = new TaskListener<Task>() {

		@Override
		public void onFail(Task task, Object[] arg) {
			final Environment data = new Environment();
			data.setKq(BaseApp.getContext().getString(R.string.empty));
			data.setWd(BaseApp.getContext().getString(R.string.empty));
			data.setSd(BaseApp.getContext().getString(R.string.empty));
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loadListener.onDataLoaded(data);
				}
			});
		}

		@Override
		public void onSuccess(Task task, Object[] arg) {
			if (arg != null && arg.length > 0) {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				L.d(resultObj.toString());
				if (resultObj.validResultCode.equals("0000")) {
					String result = resultObj.validResultInfo;
					if (result != null) {
						final Environment data = parseEnvironmentData(result);
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loadListener.onDataLoaded(data);
							}
						});
					}
				}
			}
		}
	};

	public void loadData(ProductFun productFun, FunDetail funDetail) {
		productFun.setFunType(ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI);
		List<byte[]> cmds = CmdBuilder.build().generateCmd(productFun, funDetail, null, null);
		OnlineCmdSenderLong onlineCmdSender = new OnlineCmdSenderLong(context, NetConstant.hostFor485(), CmdType.CMD_485, cmds, false, 1);
		onlineCmdSender.addListener(listener);
		onlineCmdSender.send();
	}

	private Environment parseEnvironmentData(String result) {
		Environment en = new Environment();
		if (!StringUtils.isEmpty(result) && result.length() >= 11) {

			String airQuality = result.substring(5, 7);
			String temperature = result.substring(7, 9);
			String humidity = result.substring(9, 11);

			StringBuilder sb = new StringBuilder();
			try {
				if (Integer.parseInt(airQuality) < 50) {
					sb.append(BaseApp.getContext().getString(R.string.air_quality_level_1));
				} else if (Integer.parseInt(airQuality) < 100) {
					sb.append(BaseApp.getContext().getString(R.string.air_quality_level_2));
				} else if (Integer.parseInt(airQuality) < 200) {
					sb.append(BaseApp.getContext().getString(R.string.air_quality_level_3));
				} else if (Integer.parseInt(airQuality) < 300) {
					sb.append(BaseApp.getContext().getString(R.string.air_quality_level_4));
				} else if (Integer.parseInt(airQuality) > 300) {
					sb.append(BaseApp.getContext().getString(R.string.air_quality_level_5));
				}
				sb.append(airQuality);
				en.setKq(sb.toString());
				en.setWd(temperature + "℃");
				en.setSd(humidity + "%");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return en;
	}

	public interface OnDataLoadListener {
		void onDataLoaded(Environment data);
	}

}
