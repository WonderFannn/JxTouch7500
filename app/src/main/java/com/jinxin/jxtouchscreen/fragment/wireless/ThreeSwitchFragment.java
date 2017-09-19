package com.jinxin.jxtouchscreen.fragment.wireless;/**
 * Created by admin on 2016/12/21.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.ThreeSwitchGridViewAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create By Ly At 2016/12/21
 */
public class ThreeSwitchFragment extends DialogFragment {
	@Bind(R.id.three_switch_gv)
	GridView threeSwitchGv;
	@Bind(R.id.three_switch_title_tv)
	TextView threeSwitchTitleTv;
	private Bundle arguments;
	private ProductFun productFun;
	private FunDetail funDetail;
	private ThreeSwitchGridViewAdapter gridViewAdapter;
	private List<Boolean> switchStatus;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.fragment_three_switch, container);
		ButterKnife.bind(this, inflate);
		initView();
		return inflate;
	}

	private void initView() {
		arguments = getArguments();
		productFun = (ProductFun) arguments.getSerializable("productFun");
		funDetail = (FunDetail) arguments.getSerializable("funDetail");
		if (funDetail != null)
			threeSwitchTitleTv.setText(funDetail.getFunName());
		switchStatus = new ArrayList<>();
		for (int i = 0; i < ProductConstants.FUN_TYPE_THREE_SWITCHES.length; i++) {
			if (productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCHES[i])) {
				for (int j = 0; j < i + 3; j++) {
					switchStatus.add(false);
				}
			}
		}
		gridViewAdapter = new ThreeSwitchGridViewAdapter(BaseApp.getContext(), R.layout.three_switch_gv, switchStatus, productFun, funDetail);
		threeSwitchGv.setAdapter(gridViewAdapter);
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
}
