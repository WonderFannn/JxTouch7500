package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickRecycleAdapter;
import com.jinxin.jxtouchscreen.adapter.base.ViewHolder;
import com.jinxin.jxtouchscreen.model.GatewayInfo;

import java.util.List;

/**
 * Created by XTER on 2017/03/07.
 * 网关信息适配器
 */
public class GatewaySubAdapter extends QuickRecycleAdapter<GatewayInfo> {
	public GatewaySubAdapter(Context context, int res, List<GatewayInfo> data) {
		super(context, res, data);
	}

	@Override
	public void bindView(ViewHolder holder, int position) {
		GatewayInfo gatewayInfo = data.get(position);

		TextView tvGatewayAccount = holder.getView(R.id.tv_gateway_account);
		TextView tvGatewayIp = holder.getView(R.id.tv_gateway_ip);
		TextView tvGatewaySn = holder.getView(R.id.tv_gateway_sn);
		TextView tvGatewayMac = holder.getView(R.id.tv_gateway_mac);

		tvGatewayAccount.setText(gatewayInfo.getGateway_account());
		tvGatewayIp.setText(gatewayInfo.getGateway_ip());
		tvGatewaySn.setText(gatewayInfo.getGateway_sn());
		tvGatewayMac.setText(gatewayInfo.getGateway_mac());
	}
}
