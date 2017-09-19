package com.jinxin.jxtouchscreen.fragment.cable;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.EnvironmentControlAdapter;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.Environment;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.FunDetailConfig;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.net.loader.EnvironmentLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zj on 2016/12/5.
 */
public class EnvironmentControlFragment extends DialogFragment {
	@Bind(R.id.lv_environment)
	ListView lvEnvieonment;
	private EnvironmentControlAdapter environmentControlAdapter;
	private List<Environment> environments;
	String m;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_environment_control, container, false);
		ButterKnife.bind(this, view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		getEnvironmentData();
	}

	/**
	 * 获取当前环境监测数据
	 */
	protected void getEnvironmentData() {
		List<ProductFun> pfs = DBM.getProductFunListByFunType(ProductConstants.FUN_TYPE_ENV);
		if (pfs != null && pfs.size() > 0 /*&& !TextUtils.isEmpty(CommUtil.getCurrentLoginAccount())*/) {
			final ProductFun productFun = pfs.get(0);
			FunDetail funDetail = DBM.getFunDetailByFunType(productFun.getFunType());
			new EnvironmentLoader(getActivity(), new EnvironmentLoader.OnDataLoadListener() {
				@Override
				public void onDataLoaded(Environment data) {
					Log.e("------环境", data.toString());
					StringBuilder sb = new StringBuilder();
					if (!TextUtils.isEmpty(data.getKq())) {
						sb.append(data.getKq());
						sb.append(",");
					} else {
						sb.append("无");
						sb.append(",");
					}
					if (!TextUtils.isEmpty(data.getWd())) {
						sb.append(data.getWd());
						sb.append(",");
					} else {
						sb.append("无");
						sb.append(",");
					}
					if (!TextUtils.isEmpty(data.getSd())) {
						sb.append(data.getSd());
					} else {
						sb.append("无");
					}
					productFun.setState(sb.toString());
					if (productFun.getState() != null) {
						String[] items = productFun.getState().split(",");
						Log.e("------环境+tvAirQuality", items[0]);
						if (environments == null) {
							environments = new ArrayList<Environment>();
						}
						m = getAlias(productFun.getWhId());
						//温度k    质量s    湿度w
						Environment environment = new Environment();
						environment.setKq(items[1]);
						environment.setSd(items[0]);
						environment.setWd(items[2]);
						environments.add(environment);
					}
					if (environmentControlAdapter == null) {
						environmentControlAdapter = new EnvironmentControlAdapter(getActivity(), R.layout.item_environment_control, environments, m);
					}
					lvEnvieonment.setAdapter(environmentControlAdapter);
				}
			}).loadData(productFun, funDetail);
		}
	}

	private String getAlias(String whId) {
		FunDetailConfig funDetailConfig = DBM.getFunDetailConfigByWhId(whId);
		if (funDetailConfig != null)
			return funDetailConfig.getAlias();
		return "";
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
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
