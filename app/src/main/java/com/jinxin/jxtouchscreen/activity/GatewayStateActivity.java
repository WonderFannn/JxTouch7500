package com.jinxin.jxtouchscreen.activity;

import android.view.View;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;

/**
 * Created by XTER on 2017/03/07.
 * 大网关相关信息
 */
public class GatewayStateActivity extends BaseActivity {

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinish(UpdateDataServiceDisconnectEvent event) {
		finish();
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_gateway_state);
	}

	@Override
	protected void initData() {
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@OnClick({R.id.back_btn})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
		}
	}
}
