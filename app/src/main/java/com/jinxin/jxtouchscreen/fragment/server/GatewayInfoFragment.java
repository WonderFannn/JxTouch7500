package com.jinxin.jxtouchscreen.fragment.server;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.GatewaySubAdapter;
import com.jinxin.jxtouchscreen.adapter.base.QuickItemDecoration;
import com.jinxin.jxtouchscreen.base.BaseFragment;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.UpdateGatewayInfoEvent;
import com.jinxin.jxtouchscreen.model.GatewayInfo;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

/**
 * Created by XTER on 2017/03/07.
 * 网关固定信息
 */
public class GatewayInfoFragment extends BaseFragment {

	@Bind(R.id.tv_gateway_account)
	TextView tvGatewayAccount;

	@Bind(R.id.tv_gateway_ip)
	TextView tvGatewayIp;

	@Bind(R.id.tv_gateway_sn)
	TextView tvGatewaySn;

	@Bind(R.id.tv_gateway_mac)
	TextView tvGatewayMac;

	@Bind(R.id.rv_gateway_sub)
	RecyclerView rvGatewaySub;

	GatewaySubAdapter gatewaySubAdapter;

	@Override
	public View inflate(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_gateway_info, container, false);
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		rvGatewaySub.setLayoutManager(new LinearLayoutManager(getContext()));
		rvGatewaySub.setItemAnimator(new DefaultItemAnimator());
		rvGatewaySub.addItemDecoration(new QuickItemDecoration(getContext(),QuickItemDecoration.VERTICAL_LIST));
	}

	@Override
	public void onResume() {
		super.onResume();

		updateMainGateway();

		updateSubGateway();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUpdateGatewayInfo(UpdateGatewayInfoEvent event){
		updateMainGateway();
		updateSubGateway();
	}

	/**
	 * 主网关信息展示
	 */
	protected void updateMainGateway() {
		List<GatewayInfo> gatewayInfoMainList = DBM.getCurrentOrm().query(new QueryBuilder<>(GatewayInfo.class).where("gateway_name = ?", new String[]{"main"}));
		GatewayInfo gatewayInfo;
		if (gatewayInfoMainList.size() > 0) {
			gatewayInfo = gatewayInfoMainList.get(0);
			if (gatewayInfo != null) {
				tvGatewayAccount.setText(gatewayInfo.getGateway_account());
				tvGatewayIp.setText(gatewayInfo.getGateway_ip());
				tvGatewaySn.setText(gatewayInfo.getGateway_sn());
				tvGatewayMac.setText(gatewayInfo.getGateway_mac());
			}
		} else {
			tvGatewayAccount.setText(SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, ""));
			tvGatewayIp.setText(SPM.getStr(SPM.CONSTANT, NetConstant.KEY_GATEWAYIP, ""));
		}
	}

	/**
	 * 子网关信息展示
	 */
	protected void updateSubGateway() {
		List<GatewayInfo> gatewayInfoList = DBM.getCurrentOrm().query(new QueryBuilder<>(GatewayInfo.class).where("gateway_name = ?", new String[]{"sub"}).groupBy("gateway_sn"));
		if (gatewaySubAdapter == null) {
			gatewaySubAdapter = new GatewaySubAdapter(getContext(), R.layout.item_gateway_info, gatewayInfoList);
			rvGatewaySub.setAdapter(gatewaySubAdapter);
		} else {
			gatewaySubAdapter.replaceAll(gatewayInfoList);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
}
