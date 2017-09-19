package com.jinxin.jxtouchscreen.fragment.devicenispection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.PrepareEvent;
import com.jinxin.jxtouchscreen.model.Device;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by XTER on 2017/03/13.
 * 设备巡检
 */
public class DeviceInspectFragment extends DialogFragment {

	@Bind(R.id.btn_instant_inspect)
	Button btnInstantInspect;

	@Bind(R.id.lv_device_inpection)
	Button lvDeviceInpection;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
		EventBus.getDefault().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_inspection_fragment, container, false);
		ButterKnife.bind(this, rootView);
		initData();
		return rootView;
	}

	protected void initData() {

	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onPrepareEvent(PrepareEvent event){
		List<String> zgDeviceList = new ArrayList<>();

		SQLiteDatabase db = DBM.getCurrentOrm().getWritableDatabase();
		Cursor cursorAll = db
				.rawQuery(
						"SELECT DISTINCT b.id AS _id,b.address485, b.whId, c.alias, c.funType, b.code,a.gatewayWhId FROM productregister AS a INNER JOIN customerproduct AS b ON a.whId = b.whId INNER JOIN fundetailconfig AS c ON a.whId = c.whId ORDER BY c.funType DESC",
						new String[] {});

		Cursor cursorZg = db.rawQuery(
				"SELECT whId, code FROM productregister WHERE gatewayWhId IN (SELECT whId FROM productregister WHERE code = 'G102')", new String[] {});

		if (cursorZg != null && cursorZg.moveToNext()) {
			do {
				int col = 0;
				String whId = cursorZg.getString(col);

				col = 1;
				if (!cursorZg.getString(col).equals("G102")) { // 过滤网关
					zgDeviceList.add(whId);
				}
			} while (cursorZg.moveToNext());
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
