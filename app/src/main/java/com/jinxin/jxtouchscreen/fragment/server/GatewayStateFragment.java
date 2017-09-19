package com.jinxin.jxtouchscreen.fragment.server;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.GatewayLogAdapter;
import com.jinxin.jxtouchscreen.adapter.GatewaySubAdapter;
import com.jinxin.jxtouchscreen.adapter.base.QuickItemDecoration;
import com.jinxin.jxtouchscreen.base.BaseFragment;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.GatewayLogEvent;
import com.jinxin.jxtouchscreen.model.GatewayInfo;
import com.jinxin.jxtouchscreen.model.GatewayLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by XTER on 2017/03/07.
 * 动态信息
 */
public class GatewayStateFragment extends BaseFragment {

	public static final int UPDATE_UI = 1;

	private BaseFragmentUIHandler mHandler = new BaseFragmentUIHandler(this);

	@Bind(R.id.rv_gateway_log)
	RecyclerView rvLog;

	GatewayLogAdapter gatewayLogAdapter;

	private List<GatewayLog> gatewayLogList;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public View inflate(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_gateway_state, container, false);
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		gatewayLogList = DBM.getCurrentOrm().query(GatewayLog.class);
		rvLog.setLayoutManager(new LinearLayoutManager(getContext()));
		rvLog.setItemAnimator(new DefaultItemAnimator());
//		rvLog.addItemDecoration(new QuickItemDecoration(getContext(),QuickItemDecoration.VERTICAL_LIST));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (gatewayLogAdapter == null) {
			gatewayLogAdapter = new GatewayLogAdapter(getContext(), R.layout.item_gateway_log, gatewayLogList);
			rvLog.setAdapter(gatewayLogAdapter);
		} else {
			gatewayLogAdapter.replaceAll(gatewayLogList);
		}
	}

	@Override
	protected void uiHandleMessage(Message msg) {
		switch (msg.what) {
			case UPDATE_UI:
				gatewayLogAdapter.notifyDataSetChanged();
				break;
		}
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onLogEvent(GatewayLogEvent event) {
		if(gatewayLogList==null)
			gatewayLogList = new ArrayList<>();
		gatewayLogList.add(0, new GatewayLog(event.getTime(), event.getLog()));
		if (gatewayLogList.size() > 300) {
			gatewayLogList.remove(gatewayLogList.size() - 1);
		}
		mHandler.obtainMessage(UPDATE_UI).sendToTarget();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		DBM.getCurrentOrm().deleteAll(GatewayLog.class);
		DBM.getCurrentOrm().save(gatewayLogList);
	}
}
