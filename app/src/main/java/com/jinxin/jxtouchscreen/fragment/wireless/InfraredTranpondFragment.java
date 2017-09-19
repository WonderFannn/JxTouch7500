package com.jinxin.jxtouchscreen.fragment.wireless;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;


import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.InfraredTranspondGridViewAdapter;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.WHproductUnfrared;
import com.jinxin.jxtouchscreen.net.HttpManager;
import com.jinxin.jxtouchscreen.net.base.Callback;
import com.jinxin.jxtouchscreen.net.request.InfraedTranpondRequest;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 灯光
 */
public class InfraredTranpondFragment extends DialogFragment {

	@Bind(R.id.infrared_traspond_title_tv)
	TextView infraredTraspondTitleTv;
	@Bind(R.id.infrared_traspond_gv)
	GridView infraredTraspondGv;

	private ProductFun productFun;
	private FunDetail funDetail;
	private InfraredTranspondGridViewAdapter adapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_infrared_transpond, container, false);
		ButterKnife.bind(this, view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		initData();
	}


	public void initData() {
		wHproductUnfrareds = new ArrayList<>();
		productFun = (ProductFun) getArguments().getSerializable("productFun");
		funDetail = (FunDetail) getArguments().getSerializable("funDetail");
		doUpgradeTask();
		adapter = new InfraredTranspondGridViewAdapter(getContext(), R.layout.infrared_transpond_gv, wHproductUnfrareds, productFun, funDetail);
		infraredTraspondGv.setAdapter(adapter);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick(R.id.back_btn)
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				getDialog().dismiss();
				break;
		}
	}

	private List<WHproductUnfrared> wHproductUnfrareds;

	private void doUpgradeTask() {
		List<WHproductUnfrared> wHproductUnfraredList = DBM.getCurrentOrm().query(new QueryBuilder<>(WHproductUnfrared.class).where("whId = ?", new String[]{productFun.getWhId()}).groupBy("infraRedCode"));
		if (wHproductUnfraredList != null && wHproductUnfraredList.size() > 0) {
			wHproductUnfrareds.addAll(wHproductUnfraredList);
			ToastUtil.showShort(getContext(), "获取成功");
			handler.sendEmptyMessage(1);
		} else {
			ToastUtil.showShort(getContext(), "暂无数据");
		}
	}


	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					if (wHproductUnfrareds.size() > 0) {
						adapter.notifyDataSetChanged();
					}
					break;
				default:
			}
		}
	};

}
