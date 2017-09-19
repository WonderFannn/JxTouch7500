package com.jinxin.jxtouchscreen;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.activity.SettingActivity;
import com.jinxin.jxtouchscreen.adapter.DeviceHorListViewAdapter;
import com.jinxin.jxtouchscreen.adapter.ScenceHorListViewAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.AreaEvent;
import com.jinxin.jxtouchscreen.event.FuntypeEvent;
import com.jinxin.jxtouchscreen.event.PrepareEvent;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.fragment.DeviceFragment;
import com.jinxin.jxtouchscreen.fragment.SenceControlFragment;
import com.jinxin.jxtouchscreen.model.CustomerArea;
import com.jinxin.jxtouchscreen.model.CustomerPattern;
import com.jinxin.jxtouchscreen.model.Environment;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.net.loader.EnvironmentLoader;
import com.jinxin.jxtouchscreen.service.CustomWakeService;
import com.jinxin.jxtouchscreen.service.OfficialWakeService;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.widget.HorizontalListView;
import com.jinxin.jxtouchscreen.widget.VoiceService;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity {

	@Bind(R.id.txt_temp)
	TextView txtTemp;

	@Bind(R.id.txt_cloud)
	TextView txtCloud;

	@Bind(R.id.txt_netdegree)
	TextView txtNetdegree;

	@Bind(R.id.rely_2)
	RelativeLayout rlUFO;

	@Bind(R.id.space_horizon_listview)
	HorizontalListView hListView;

	@Bind(R.id.txt_topic_space)
	TextView tvTitle;

	@Bind(R.id.space_btn)
	ImageButton btn_space;

	@Bind(R.id.device_btn)
	ImageButton btn_device;

//	@Bind(R.id.sound_btn)
//	ImageButton btn_sound;

	@Bind(R.id.device_setup)
	ImageView iv;

	private List<CustomerArea> customerAreas;
	private ScenceHorListViewAdapter scenceHorListViewAdapter;
	private List<FunDetail> funDetails;
	private DeviceHorListViewAdapter deviceHorListViewAdapter;

	private SenceControlFragment sceceControlFragment;
	private DeviceFragment deviceControlFragment;

	public static Boolean currentWakeMode;

	FunDetail funDetail;
	ProductFun productFun;

	@Override
	@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	protected void initView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_main);
		startService(new Intent(getApplicationContext(), VoiceService.class));
		//开启监听服务

		Log.d("TAG", "预备开启服务");
		currentWakeMode = SPM.getBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, false);
		if(currentWakeMode){
			//启动自定义唤醒
			Log.d("TAG", "启动自定义唤醒");
			startService(new Intent(getApplicationContext(), CustomWakeService.class));
//			this.bindService(new Intent("com.jinxin.jxtouchscreen.service.CustomWakeService"),
//					this.serviceConnection, BIND_AUTO_CREATE);
		}else {
			//启动官方唤醒
			Log.d("TAG", "启动官方唤醒");
			startService(new Intent(getApplicationContext(), OfficialWakeService.class));
//			this.bindService(new Intent("com.jinxin.jxtouchscreen.service.OfficialWakeService"),
//					this.serviceConnection, BIND_AUTO_CREATE);
		}

		if (sceceControlFragment == null)
			sceceControlFragment = new SenceControlFragment();
		if (deviceControlFragment == null)
			deviceControlFragment = new DeviceFragment();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	protected void initData() {
		//设备列表
		funDetails = DBM.getCurrentOrm().query(new QueryBuilder<>(FunDetail.class).where("funType not in ('00103','020','025','00301','00501','00502','00901','00902') and funType in (select funType from productfun group by funType)", new String[]{}).groupBy("funType").orderBy("_id"));

		//模式区域列表（在用）
		customerAreas = new ArrayList<>();
		initArea();

		//为模式控制页面和设备控制页面准备数据
		EventBus.getDefault().post(new PrepareEvent());

//        startService(new Intent(getApplicationContext(),UpdateDataService.class));
	}


	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onPrepareData(PrepareEvent event) {
		//场景页面首次所需数据
		List<CustomerPattern> customerPatternList = DBM.getCurrentOrm().query(CustomerPattern.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("customer_pattern_list_all", (Serializable) customerPatternList);
		sceceControlFragment.setArguments(bundle);

		//设备页面首次所需数据
		if (funDetails != null && funDetails.size() > 0) {
			String funtype = funDetails.get(0).getFunType();
			List<ProductFun> productFunList = DBM.getProductFunListByFunType(funtype);
			Bundle bundle1 = new Bundle();
			bundle1.putSerializable("product_fun_list", (Serializable) productFunList);
			deviceControlFragment.setArguments(bundle1);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDisconnect(UpdateDataServiceDisconnectEvent event) {
		BaseApp.IpFlag = true;
		stopService(new Intent(getApplicationContext(), UpdateDataService.class));
		finish();
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onUpdateData(UpdateFinishEvent event) {
		funDetails = DBM.getCurrentOrm().query(new QueryBuilder<>(FunDetail.class).where("funType not in ('00103','020','025','00301','00501','00502','00901','00902') and funType in (select funType from productfun group by funType)", new String[]{}).groupBy("funType").orderBy("_id"));
		if (deviceHorListViewAdapter != null) {
			deviceHorListViewAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		startService(new Intent(getApplicationContext(), VoiceService.class));
		//请求一次环境数据
		getEnvironmentData();

	}


	/**
	 * 得到模式区域列表
	 */
	private void initArea() {
		CustomerArea caAll = new CustomerArea();
		caAll.setAreaName("全部");
		caAll.setId(100);
		customerAreas.add(caAll);
		List<CustomerPattern> customerPatterns = DBM.getCurrentOrm().query(new QueryBuilder<>(CustomerPattern.class).groupBy("patternGroupId"));
		for (int i = 0; i < customerPatterns.size(); i++) {
			CustomerArea ca = DBM.getCustomerAreaByGroupId(customerPatterns.get(i).getPatternGroupId());
			if (ca != null)
				customerAreas.add(DBM.getCustomerAreaByGroupId(customerPatterns.get(i).getPatternGroupId()));
		}
	}

	/**
	 * 获取当前环境监测数据
	 */
	protected void getEnvironmentData() {

		productFun = DBM.getProductFunByFunType(ProductConstants.FUN_TYPE_ENV);
		if (productFun == null)
			return;
		funDetail = DBM.getFunDetailByFunType(productFun.getFunType());
		new EnvironmentLoader(this, new EnvironmentLoader.OnDataLoadListener() {
			@Override
			public void onDataLoaded(Environment data) {
				L.w("环境:" + data.toString());
				StringBuilder sb = new StringBuilder();
				if (!TextUtils.isEmpty(data.getKq())) {
					sb.append(data.getKq());
					sb.append(",");
				} else {
					sb.append("0");
					sb.append(",");
				}
				if (!TextUtils.isEmpty(data.getWd())) {
					sb.append(data.getWd());
					sb.append(",");
				} else {
					sb.append("0");
					sb.append(",");
				}
				if (!TextUtils.isEmpty(data.getSd())) {
					sb.append(data.getSd());
				} else {
					sb.append("0");
				}
				productFun.setState(sb.toString());
				if (rlUFO == null)
					return;
				if (productFun.getState() != null) {
					String[] items = productFun.getState().split(",");
					if (items[0].equals("无") || items[1].equals("无") || items[2].equals("无") ||
							items[0].equals("0") || items[1].equals("0") || items[2].equals("0") ||
							TextUtils.isEmpty(items[0]) || TextUtils.isEmpty(items[1]) || TextUtils.isEmpty(items[2]))
						rlUFO.setVisibility(View.INVISIBLE);
					else
						rlUFO.setVisibility(View.VISIBLE);
					txtTemp.setText(items[0]);
					txtCloud.setText(items[1]);
					txtNetdegree.setText(items[2]);
				}
			}
		}).loadData(productFun, funDetail);
	}

	@OnClick({R.id.space_btn, R.id.device_btn, R.id.device_setup/*,R.id.sound_btn*/})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.space_btn:
				btn_space.setSelected(true);
				btn_device.setSelected(false);

				tvTitle.setText("全部");

				scenceHorListViewAdapter = new ScenceHorListViewAdapter(getApplicationContext(), R.layout.item_hor_listview, customerAreas);
				scenceHorListViewAdapter.setSelectedPos(0);
				hListView.setAdapter(scenceHorListViewAdapter);
				iv.setVisibility(View.VISIBLE);
				hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						scenceHorListViewAdapter.setSelectedPos(position);
						//设置场景区域名字
						tvTitle.setText(customerAreas.get(position).getAreaName());
						//传递场景区域groupId
						EventBus.getDefault().post(new AreaEvent(id));
					}
				});

				switchContent(deviceControlFragment, sceceControlFragment, "sence");

				L.i(sceceControlFragment.isVisible() ? "场景可见" : "场景不可见");
				L.i(deviceControlFragment.isVisible() ? "设备可见" : "设备不可见");
				break;
			case R.id.device_btn:
				btn_space.setSelected(false);
				btn_device.setSelected(true);

				deviceHorListViewAdapter = new DeviceHorListViewAdapter(getApplicationContext(), R.layout.item_hor_listview, funDetails);
				deviceHorListViewAdapter.setSelectedPos(0);
				hListView.setAdapter(deviceHorListViewAdapter);
				iv.setVisibility(View.INVISIBLE);
				hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						deviceHorListViewAdapter.setSelectedPos(position);
						//设置当前设备类型名称
						tvTitle.setText(funDetails.get(position).getFunName());
						//传递当前设备类型funtype
						EventBus.getDefault().post(new FuntypeEvent(funDetails.get(position).getFunType()));
					}
				});

				switchContent(sceceControlFragment, deviceControlFragment, "device");
				L.i(sceceControlFragment.isVisible() ? "场景可见" : "场景不可见");
				L.i(deviceControlFragment.isVisible() ? "设备可见" : "设备不可见");
				break;
			case R.id.device_setup:
				startActivity(new Intent(MainActivity.this, SettingActivity.class));
				break;
			/*case R.id.sound_btn:
				startActivity(new Intent(MainActivity.this, VoiceRecognitionActivity.class));
				break;*/
		}
	}

	/**
	 * 从一个fragment跳转到另一个
	 *
	 * @param from 来时
	 * @param to   去处
	 * @param tag  标记
	 */
	public void switchContent(Fragment from, Fragment to, String tag) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// 先判断是否被add过
		if (!to.isAdded()) {
			// 隐藏当前的fragment，add下一个到Activity中
			if (from == null)
				ft.add(R.id.liner_main, to, tag);
			else
				ft.hide(from).add(R.id.liner_main, to, tag);
			ft.show(to);
//            ft.addToBackStack(tag);
		} else {
			// 隐藏当前的fragment，show下一个
			if (from == null)
				ft.show(to);
			else
				ft.hide(from).show(to);
		}
		ft.commit();
	}

	@Override
	public void onBackPressed() {
		AppUtil.exit();
		stopService(new Intent(getApplicationContext(), UpdateDataService.class));
		stopService(new Intent(getApplicationContext(),VoiceService.class));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		stopService(new Intent(getApplicationContext(), UpdateDataService.class));
		stopService(new Intent(getApplicationContext(),VoiceService.class));
		if(currentWakeMode){
			stopService(new Intent(getApplicationContext(),CustomWakeService.class));
		}else{
			stopService(new Intent(getApplicationContext(),OfficialWakeService.class));
		}


	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (BaseApp.isBackground(MainActivity.this)){
			stopService(new Intent(MainActivity.this,VoiceService.class));
		}
	}

}
