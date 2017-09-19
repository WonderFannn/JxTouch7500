package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickRecycleAdapter;
import com.jinxin.jxtouchscreen.adapter.base.ViewHolder;
import com.jinxin.jxtouchscreen.model.GatewayLog;

import java.util.List;

/**
 * Created by XTER on 2017/03/08.
 * 网关日志
 */
public class GatewayLogAdapter extends QuickRecycleAdapter<GatewayLog> {
	public GatewayLogAdapter(Context context, int res, List<GatewayLog> data) {
		super(context, res, data);
	}

	@Override
	public void bindView(ViewHolder holder, int position) {
		GatewayLog log = data.get(position);

		TextView tvLogTime = holder.getView(R.id.tv_gateway_log_time);
		TextView tvLogMsg = holder.getView(R.id.tv_gateway_log_msg);

		tvLogTime.setText(log.getTime());
		tvLogMsg.setText(log.getMessage());
	}
}
