package com.jinxin.jxtouchscreen.activity;

import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.SysUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 版本信息及更新
 */
public class VersionInfo extends BaseActivity {

	@Bind(R.id.textViewVersionInfo)
	TextView textViewVersionInfo;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_version_info);
	}

	@Override
	protected void initData() {
		textViewVersionInfo.setText(getVersion());
	}

	public String getVersion() {
		return this.getString(R.string.app_name) + AppUtil.getAppVersionName(this);
	}

	protected void update() {
		JSONObject jo = new JSONObject();
		jo.put("area", LocalConstant.UPDATE);
		jo.put("time", SysUtil.getNow2());
		JSONObject jo1 = new JSONObject();
		jo1.put("versionName", AppUtil.getAppVersionName(BaseApp.getContext()));
		jo1.put("versionCode", AppUtil.getAppVersionCode(BaseApp.getContext()));
		jo.put("content", jo1);
		UpdateDataService.updateSpecialData(jo);
	}

	@OnClick({R.id.back_btn, R.id.tv_check_update})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.tv_check_update:
				update();
				break;
		}
	}
}
