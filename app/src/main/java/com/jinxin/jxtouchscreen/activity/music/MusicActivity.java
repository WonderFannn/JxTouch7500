package com.jinxin.jxtouchscreen.activity.music;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.ListDetailDialogAdapter;
import com.jinxin.jxtouchscreen.adapter.ListDialogAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.MusicSetting;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.ChangeSongEvent;
import com.jinxin.jxtouchscreen.event.InputEvent;
import com.jinxin.jxtouchscreen.event.PosEvent;
import com.jinxin.jxtouchscreen.event.SpeakerEvent;
import com.jinxin.jxtouchscreen.event.UpdateFailedEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.model.CloudSetting;
import com.jinxin.jxtouchscreen.model.CustomerProduct;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductRegister;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.CustomerDialogCreater;
import com.jinxin.jxtouchscreen.widget.VerticalSeekBar;
import com.jinxin.jxtouchscreen.widget.WrapListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicActivity extends BaseActivity {

	public static final int MUSIC_PLAY = 1;
	public static final int MUSIC_PAUSE = 2;
	public static final int MUSIC_PREV = 3;
	public static final int MUSIC_NEXT = 4;
	public static final int MUSIC_OUTPUT = 5;
	public static final int MUSIC_INPUT = 6;
	public static final int MUSIC_MODE_SWITCH = 7;
	public static final int MUSIC_VOLUME = 8;
	public static final int MUSIC_STATUS = 9;
	public static final int MUSIC_QUERY_STATUS = 10;
	public static final int MUSIC_SYNC_MUSIC_LIST = 11;
	public static final int MUSIC_SYN_VOLUME = 12;
	public static final int MUSIC_LOAD_MUSIC_LIST = 13;
	public static final int MUSIC_REFRESH_MUSIC_LIST = 14;
	public static final int MUSIC_UPDATE_UI = 15;
	public static final int MUSIC_CHECK_LINK = 16;
	public static final int TOAST = 20;

	/**
	 * 产品参数
	 */
	private ProductFun productFun;

	/**
	 * 产品详情参数
	 */
	private FunDetail funDetail;

	/**
	 * 命令参数
	 */
	private Map<String, Object> params;

	/**
	 * 歌曲列表
	 */
	private List<Music> musicList;

	/**
	 * 组件可点击状态
	 */
	private boolean isClicklable = true;

	/**
	 * 当前音量
	 */
	private int currVolume = -1;

	/**
	 * 音乐光盘界面，有动画
	 */
	private MusicDiskFragment musicDiskFragment;

	/**
	 * 音乐列表界面
	 */
	private MusicListFragment musicListFragment;
	/**
	 * 音乐状态信息界面
	 */
	private PowerAmplifierPageThree powerAmplifierPageThree;
	/**
	 * 播放按钮
	 */
	@Bind(R.id.btn_music_play)
	Button btnPlay;

	/**
	 * 暂停按钮
	 */
	@Bind(R.id.btn_music_pause)
	Button btnPause;

	/**
	 * 上一首按钮
	 */
	@Bind(R.id.btn_music_prev)
	Button btnPrev;

	/**
	 * 下一着按钮
	 */
	@Bind(R.id.btn_music_next)
	Button btnNext;

	/**
	 * 控制音量按钮
	 */
	@Bind(R.id.btn_music_volume)
	Button btnVolume;

	/**
	 * 切换为单曲循环模式
	 */
	@Bind(R.id.btn_music_mode_single)
	Button btnModeSingle;

	/**
	 * 切换为列表循环模式
	 */
	@Bind(R.id.btn_music_mode_cycle)
	Button btnModeCycle;

	/**
	 * 设置扬声器按钮
	 */
	@Bind(R.id.btn_music_output)
	Button btnOutput;

	/**
	 * 设置输入源按钮
	 */
	@Bind(R.id.btn_music_input)
	Button btnInput;

	/**
	 * 查询播放状态
	 */
	@Bind(R.id.btn_music_status)
	Button btnStatu;

	/**
	 * 界面控制器
	 */
	@Bind(R.id.vp_music_fragment)
	ViewPager vpMusicFragment;

	/**
	 * 界面页眉标志--左
	 */
	@Bind(R.id.iv_dot_left)
	ImageView ivDotLeft;

	/**
	 * 界面页眉标志--右
	 */
	@Bind(R.id.iv_dot_right)
	ImageView ivDotRight;

	/**
	 * 音量条所在布局
	 */
	@Bind(R.id.rl_music_volume_bar)
	RelativeLayout rlVolumeLayout;

	/**
	 * 音量进度条
	 */
	@Bind(R.id.vs_volume_seekbar)
	VerticalSeekBar vsVolumeSeekBar;

	private static List<Music> musics;
	private int currentPlayingSongIndex;//当前播放的音乐

	@Bind(R.id.iv_dot_last)
	ImageView ivDotLast;
	@Bind(R.id.rl_music_bottom_bar)
	PercentRelativeLayout rlMusicBottomBar;
	@Bind(R.id.rl_music_bg)
	PercentRelativeLayout rlMusicBg;
	private UIHandler mUIHander = new UIHandler(MusicActivity.this);

	@OnClick({R.id.btn_music_play, R.id.btn_music_pause, R.id.btn_music_prev, R.id.btn_music_next, R.id.btn_music_mode_single, R.id.btn_music_mode_cycle, R.id.btn_music_output, R.id.btn_music_input, R.id.btn_music_volume, R.id.btn_music_status, R.id.back_btn})
	public void onClick(View view) {
		if (!isClicklable) {
			mUIHander.obtainMessage(TOAST, getString(R.string.operate_invalid_work)).sendToTarget();
			return;
		}
		switch (view.getId()) {
			case R.id.btn_music_play:
				mUIHander.obtainMessage(MUSIC_PLAY).sendToTarget();
				break;
			case R.id.btn_music_pause:
				mUIHander.obtainMessage(MUSIC_PAUSE).sendToTarget();
				break;
			case R.id.btn_music_prev:
				mUIHander.obtainMessage(MUSIC_PREV).sendToTarget();
				break;
			case R.id.btn_music_next:
				mUIHander.obtainMessage(MUSIC_NEXT).sendToTarget();
				break;
			case R.id.btn_music_mode_single:
			case R.id.btn_music_mode_cycle:
				mUIHander.obtainMessage(MUSIC_MODE_SWITCH).sendToTarget();
				break;
			case R.id.btn_music_output:
				mUIHander.obtainMessage(MUSIC_OUTPUT).sendToTarget();
				break;
			case R.id.btn_music_input:
				mUIHander.obtainMessage(MUSIC_INPUT).sendToTarget();
				break;
			case R.id.btn_music_volume:
				mUIHander.obtainMessage(MUSIC_VOLUME).sendToTarget();
				break;
			case R.id.btn_music_status:
				mUIHander.obtainMessage(MUSIC_STATUS).sendToTarget();
				break;
			case R.id.back_btn:
				finish();
				break;
		}
	}

	static class UIHandler extends Handler {
		WeakReference<MusicActivity> mMusicActivity;

		UIHandler(MusicActivity activity) {
			mMusicActivity = new WeakReference<MusicActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MusicActivity activity = mMusicActivity.get();
			switch (msg.what) {
				case MUSIC_PLAY:
					activity.updatePlayUIState(true);
					activity.sendControlCmd(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);
					break;
				case MUSIC_PAUSE:
					activity.updatePlayUIState(false);
					activity.sendControlCmd(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE);
					break;
				case MUSIC_PREV:
					activity.updatePlayUIState(true);
					activity.sendControlCmd(ProductConstants.POWER_AMPLIFIER_PROVIOUS);
					break;
				case MUSIC_NEXT:
					activity.updatePlayUIState(true);
					activity.sendControlCmd(ProductConstants.POWER_AMPLIFIER_NEXT);
					break;
				case MUSIC_MODE_SWITCH:
					activity.doModeSwitch();
					break;
				case MUSIC_INPUT:
					activity.showChooseInputDialog();
					break;
				case MUSIC_OUTPUT:
					activity.showChooseOutputDialog();
					break;
				case MUSIC_VOLUME:
					activity.toggleVolumeBar();
					break;
				case MUSIC_SYN_VOLUME:
					activity.synVolume((Integer) msg.obj);
					break;
				case MUSIC_STATUS:
					activity.showPlayStatusDialog();
					break;
				case MUSIC_QUERY_STATUS:
					activity.sendPlayStatusCmd();
					break;
				case MUSIC_SYNC_MUSIC_LIST:
					activity.updateMusicListFragment((List<Music>) msg.obj);
					break;
				case MUSIC_LOAD_MUSIC_LIST:
					activity.loadMusicList();
					break;
				case MUSIC_REFRESH_MUSIC_LIST:
					activity.setCurrentIndex((int) msg.obj);
					musics = DBM.loadMusicDataFromDb();
					for (Music m : musics) {
						if (m.getPlayNo() == (int) msg.obj) {
							EventBus.getDefault().post(new ChangeSongEvent(m.getMusicName()));
						}
					}
					break;
				case MUSIC_UPDATE_UI:
					activity.updateGeneralUI();
					break;
				case MUSIC_CHECK_LINK:
					activity.sendCheckLinkStatusCmd();
					break;
				case TOAST:
					ToastUtil.showShort(activity, (String) msg.obj);
					break;
			}
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_music);
		EventBus.getDefault().register(this);
		initParam();
	}

	@Override
	protected void initData() {
		initWidgets();
		initSetting();
		initFragments();
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


	/**
	 * 初始化参数
	 */
	protected void initParam() {
		productFun = DBM.getProductFunByFunType(ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		funDetail = DBM.getFunDetailByFunType(ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		params = new HashMap<>();
	}

	protected void initWidgets() {
		setTitle(getString(R.string.music_title));

		vsVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (isClicklable) {
					sendSetVolumeCmd(100 - seekBar.getProgress());
				}
			}
		});

	}

	/**
	 * 初始化碎片页面
	 */
	protected void initFragments() {

		ArrayList<Fragment> fragments = new ArrayList<>();

		musicListFragment = new MusicListFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("music_list", (Serializable) musicList);
		musicListFragment.setArguments(bundle);
		fragments.add(musicListFragment);
		musicDiskFragment = new MusicDiskFragment();
		fragments.add(musicDiskFragment);
		powerAmplifierPageThree = new PowerAmplifierPageThree();
		fragments.add(powerAmplifierPageThree);

		FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
		vpMusicFragment.setOffscreenPageLimit(2);
		vpMusicFragment.setAdapter(adapter);

		vpMusicFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					ivDotLeft.setBackgroundResource(R.drawable.dot_check);
					ivDotRight.setBackgroundResource(R.drawable.dot);
					ivDotLast.setBackgroundResource(R.drawable.dot);
					rlMusicBottomBar.setVisibility(View.VISIBLE);
				}
				if (position == 1) {
					ivDotLeft.setBackgroundResource(R.drawable.dot);
					ivDotRight.setBackgroundResource(R.drawable.dot_check);
					ivDotLast.setBackgroundResource(R.drawable.dot);
					rlMusicBottomBar.setVisibility(View.VISIBLE);
				}
				if (position == 2) {
					ivDotLeft.setBackgroundResource(R.drawable.dot);
					ivDotRight.setBackgroundResource(R.drawable.dot);
					ivDotLast.setBackgroundResource(R.drawable.dot_check);
					rlMusicBottomBar.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	protected void initSetting() {
		mUIHander.obtainMessage(MUSIC_CHECK_LINK).sendToTarget();

		mUIHander.obtainMessage(MUSIC_LOAD_MUSIC_LIST).sendToTarget();
	}


	/**
	 * 更新播放与暂停的UI与动画
	 *
	 * @param toPlay
	 */
	protected void updatePlayUIState(boolean toPlay) {
		if (toPlay) {
			btnPlay.setVisibility(View.INVISIBLE);
			btnPlay.setEnabled(false);
			btnPause.setVisibility(View.VISIBLE);
			btnPause.setEnabled(true);

			musicDiskFragment.startDiskAnim();
			musicDiskFragment.startHandAnim();
		} else {
			btnPlay.setVisibility(View.VISIBLE);
			btnPlay.setEnabled(true);
			btnPause.setVisibility(View.INVISIBLE);
			btnPause.setEnabled(false);

			musicDiskFragment.stopDiskAnim();
			musicDiskFragment.stopHandAnim();
		}
	}

	/**
	 * 更新通常UI
	 */
	protected void updateGeneralUI() {
		if (SPM.getBoolean(MusicSetting.SP_NAME, MusicSetting.CLOUD_MUSIC_PLAY_MODE, true)) {
			btnModeCycle.setVisibility(View.VISIBLE);
			btnModeSingle.setVisibility(View.GONE);
		} else {
			btnModeSingle.setVisibility(View.VISIBLE);
			btnModeCycle.setVisibility(View.GONE);
		}
		if (musicList != null && musicList.size() > 0) {
			String input = musicList.get(0).getSource();
			SPM.saveStr(MusicSetting.SP_NAME, MusicSetting.INPUT, input);
		}
		btnInput.setText(SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT, "usb"));
	}

	/**
	 * 设置输入源
	 *
	 * @param input
	 */
	protected void setInput4Voice(final String input) {
		//同步数据库设置
		List<CloudSetting> csList = DBM.getCloudSetting(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);

		if (csList != null && csList.size() > 0) {
			CloudSetting cs = csList.get(0);
			cs.setParams(input.toLowerCase(Locale.CHINA));
			DBM.getCurrentOrm().update(cs);
		} else {
			CloudSetting cs = new CloudSetting(AppUtil.getCurrentAccount(),
					"004", StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, input.toLowerCase(Locale.CHINA), null, null, null);
			DBM.getCurrentOrm().save(cs);
		}


		//提交云端任务
		if (productFun == null) return;

		ProductRegister pr = DBM.getProductRegisterByWhId(productFun.getWhId());
		CustomerProduct cp = DBM.getCustomerProductByWhId(productFun.getWhId());
		String gatewayWhid = "";
		String address485 = "";
		if (pr != null && cp != null) {
			gatewayWhid = pr.getGatewayWhId();
			address485 = cp.getAddress485();
		}

		JSONObject jo = new JSONObject();
		jo.put("area", "music");
		jo.put("time", SysUtil.getNow2());
		JSONObject jo1 = new JSONObject();
		jo1.put("input", input);
		jo1.put("productWhId", productFun.getWhId());
		jo1.put("gatewayWhId", gatewayWhid);
		jo1.put("address485", address485);
		jo.put("content", jo1);
		UpdateDataService.updateSpecialData(jo);
	}

	/**
	 * 界面初始化时，加载歌曲列表
	 */
	public void loadMusicList() {
		musicList = DBM.loadMusicDataFromDb();

		mUIHander.obtainMessage(MUSIC_SYNC_MUSIC_LIST, musicList).sendToTarget();
		mUIHander.obtainMessage(MUSIC_UPDATE_UI).sendToTarget();
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onUpdateMusicEvent(UpdateFinishEvent event) {
		List<Music> musicList1 = DBM.loadMusicDataFromDb();
		if (musicList1.size() != musicList.size() && !musicList1.equals(musicList)){
			musicList.clear();
			musicList = DBM.loadMusicDataFromDb();
		}
		else{
			EventBus.getDefault().post(new UpdateFailedEvent());
			return ;
		}

		// 更新界面
		mUIHander.obtainMessage(MUSIC_SYNC_MUSIC_LIST, musicList).sendToTarget();
		mUIHander.obtainMessage(MUSIC_UPDATE_UI).sendToTarget();
		mUIHander.obtainMessage(MUSIC_QUERY_STATUS).sendToTarget();
		mUIHander.obtainMessage(TOAST, getString(R.string.music_loading_success)).sendToTarget();
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void updateMusicFailed(UpdateFailedEvent event) {
		mUIHander.obtainMessage(MUSIC_UPDATE_UI).sendToTarget();
		mUIHander.obtainMessage(TOAST, getString(R.string.music_set_input_failed)).sendToTarget();
	}

	/**
	 * 更新音乐列表视图
	 *
	 * @param musicList 音乐列表
	 */
	protected void updateMusicListFragment(List<Music> musicList) {
		musicListFragment.updateMusicList(musicList);
	}

	protected void setCurrentIndex(int index) {
		musicListFragment.updateListFocus(index);
	}

	/**
	 * 切换播放循环模式
	 */
	protected void doModeSwitch() {
		if (btnModeSingle.getVisibility() == View.VISIBLE) {
			btnModeSingle.setVisibility(View.INVISIBLE);
			btnModeSingle.setEnabled(false);
			btnModeCycle.setVisibility(View.VISIBLE);
			btnModeCycle.setEnabled(true);

			sendControlCmd(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL);
			mUIHander.obtainMessage(TOAST, getString(R.string.music_play_all_mode)).sendToTarget();
			SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.CLOUD_MUSIC_PLAY_MODE, true);
		} else {
			btnModeSingle.setVisibility(View.VISIBLE);
			btnModeSingle.setEnabled(true);
			btnModeCycle.setVisibility(View.INVISIBLE);
			btnModeCycle.setEnabled(false);

			sendControlCmd(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE);
			mUIHander.obtainMessage(TOAST, getString(R.string.music_play_single_mode)).sendToTarget();
			SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.CLOUD_MUSIC_PLAY_MODE, false);
		}
	}

	/**
	 * 弹出选择输入源对话框
	 */
	protected void showChooseInputDialog() {
		final String[] items = getResources().getStringArray(R.array.input_items);
		final boolean[] status = new boolean[items.length];
		for (int i = 0; i < items.length; i++) {
			if (items[i].equalsIgnoreCase(SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT,
					StaticConstant.INPUT_TYPE_USB))) {
				status[i] = true;
			}
		}

		View view = getLayoutInflater().inflate(R.layout.dialog_list_ok, null);
		WrapListView wlvInput = (WrapListView) view.findViewById(R.id.lv_radio_dialog_list);
		final ListDialogAdapter adapter = new ListDialogAdapter(this, items, status);
		wlvInput.setAdapter(adapter);

		final CustomerDialogCreater dialogCreater = new
				CustomerDialogCreater(this, R.style.CustomerDialogStyle, view, 0.5f, 0f);

		Button btnSure = (Button) view.findViewById(R.id.btn_radio_dialog_ok);
		wlvInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				for (int i = 0; i < items.length; i++) {
					status[i] = false;
				}
				status[position] = true;
				adapter.notifyDataSetChanged(items, status);
			}
		});

		btnSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for (int i = 0; i < items.length; i++) {
					if (status[i]) {
						setInput4Voice(items[i]);
					}
				}
				dialogCreater.dismiss();
			}
		});
		dialogCreater.show();
	}

	/**
	 * 弹出选择扬声器对话框
	 */
	protected void showChooseOutputDialog() {
		List<String> speakers = AppUtil.getSpeakerNames();
		final String[] items = speakers.toArray(new String[speakers.size()]);
		final boolean[] status = new boolean[items.length];
		String speakerStr = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00000000");
		for (int i = 0; i < speakerStr.length(); i++) {
			if (speakerStr.charAt(i) == '0') {
				status[i] = false;
			} else {
				status[i] = true;
			}
		}

		View view = getLayoutInflater().inflate(R.layout.dialog_list_ok, null);
		WrapListView wlvInput = (WrapListView) view.findViewById(R.id.lv_radio_dialog_list);
		final ListDialogAdapter adapter = new ListDialogAdapter(this, items, status);
		wlvInput.setAdapter(adapter);

		final CustomerDialogCreater dialogCreater = new
				CustomerDialogCreater(this, R.style.CustomerDialogStyle, view, 0.5f, 0f);

		Button btnSure = (Button) view.findViewById(R.id.btn_radio_dialog_ok);
		wlvInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				status[position] = !status[position];
				adapter.notifyDataSetChanged(items, status);
			}
		});

		btnSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < status.length; i++) {
					if (status[i]) {
						sb.append(1);
					} else {
						sb.append(0);
					}
				}
				saveSpeakerSettingsAndSendCmd(sb.toString());
				dialogCreater.dismiss();
			}
		});
		dialogCreater.show();
	}

	/**
	 * 音量条显示与隐藏
	 */
	protected void toggleVolumeBar() {
		if (rlVolumeLayout.getVisibility() == View.GONE) {
			rlVolumeLayout.setVisibility(View.VISIBLE);
			sendGetVolumeCmd();
		} else {
			rlVolumeLayout.setVisibility(View.GONE);
		}
	}


	/**
	 * 弹出播放状态对话框
	 */
	protected void showPlayStatusDialog() {
		mUIHander.obtainMessage(MUSIC_QUERY_STATUS).sendToTarget();

		final String[] items = getResources().getStringArray(R.array.input_items);
		final boolean[] status = new boolean[items.length];

		View view = getLayoutInflater().inflate(R.layout.dialog_list_ok, null);
		WrapListView wlvInput = (WrapListView) view.findViewById(R.id.lv_radio_dialog_list);
		final ListDetailDialogAdapter adapter = new ListDetailDialogAdapter(this, items, status);
		wlvInput.setAdapter(adapter);

		final CustomerDialogCreater dialogCreater = new
				CustomerDialogCreater(this, R.style.CustomerDialogStyle, view, 0.5f, 0f);

		view.findViewById(R.id.btn_radio_dialog_ok).setVisibility(View.GONE);
		wlvInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				for (int i = 0; i < items.length; i++) {
					status[i] = false;
				}
				status[position] = true;
				adapter.notifyDataSetChanged(items, status);
			}
		});
		dialogCreater.show();
	}

	/**
	 * 发送控制命令
	 */
	private void sendControlCmd(String type) {
		if (productFun == null)
			return;
//		isClicklable = false;
		productFun.setFunType(type);

		params.clear();

		String input = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT,
				StaticConstant.INPUT_TYPE_USB).toLowerCase(Locale.CHINA);
		String speaker = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				"00010000");

		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speaker);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, input);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_INPUT, input);

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onSuccess(Task task, Object[] arg) {
				isClicklable = true;
				mUIHander.obtainMessage(MUSIC_QUERY_STATUS).sendToTarget();
			}

			@Override
			public void onFail(Task task, Object[] arg) {
				isClicklable = true;
				if (arg == null) {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				} else {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
				}
			}
		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(this, NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}

	/**
	 * 发送控制音量命令
	 *
	 * @param volume
	 */
	private void sendSetVolumeCmd(final int volume) {
		if (productFun == null) {
			return;
		}

//		isClicklable = false;

		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SET_VOLUME);

		String v = resetParam(volume);
		params.clear();
		String speaker = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				"00010000");
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speaker);
		params.put(StaticConstant.PARAM_TEXT, v);

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onSuccess(Task task, Object[] arg) {
				SPM.saveInt(MusicSetting.SP_NAME,
						MusicSetting.CURRENT_VOLUME, volume);
				isClicklable = true;
				L.d("set volume successs" + volume);
			}

			@Override
			public void onFail(Task task, Object[] arg) {
				isClicklable = true;
				mUIHander.obtainMessage(TOAST, getString(R.string.music_volume_set_failed)).sendToTarget();
			}
		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, null);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(this, NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}

	/**
	 * 同步音量值
	 *
	 * @param progress
	 */
	protected void synVolume(int progress) {
		vsVolumeSeekBar.setProgress(progress);
	}

	/**
	 * 重置音量参数
	 *
	 * @param volume
	 * @return
	 */
	private String resetParam(int volume) {
		if (volume < 10) {
			return "00" + volume;
		} else if (volume > 10 && volume < 100) {
			return "0" + volume;
		}
		return "100";
	}

	/**
	 * 发送获取音量命令
	 */
	private void sendGetVolumeCmd() {
		if (productFun == null)
			return;

//		isClicklable = false;

		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_GET_VOLUME);

		final String speaker = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00001000");

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onFail(Task task, Object[] arg) {
				isClicklable = true;
				mUIHander.obtainMessage(TOAST, getString(R.string.music_volume_query_failed)).sendToTarget();
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				isClicklable = true;
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						try {
							int index = speaker.indexOf("1") + 1;
							if (index > 0) {
								currVolume = Integer.valueOf(result.substring(
										index * 3 - 2, index * 3 + 1).replaceFirst(
										"^0+", ""));
								currVolume = currVolume > 100 ? 100 : currVolume;
								L.d("volume result-->" + currVolume);
								SPM.saveInt(MusicSetting.SP_NAME,
										MusicSetting.CURRENT_VOLUME, 100 - currVolume);
							} else {
								currVolume = 100;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (currVolume > -1) {
							mUIHander.obtainMessage(MUSIC_SYN_VOLUME, 100 - currVolume).sendToTarget();
						}
					} else {
						mUIHander.obtainMessage(TOAST, getString(R.string.music_volume_query_failed)).sendToTarget();
					}
				}
			}

		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, null);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(this, NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}

	@Subscribe
	public void onEventMainThread(SpeakerEvent event) {
		saveSpeakerSettingsAndSendCmd(event.getSpeaker().toString());
	}

	@Subscribe
	public void onEventMainThread(InputEvent event) {
		setInput4Voice(event.getInputString());
	}

	/**
	 * 保存扬声器设置，并发送切换命令
	 *
	 * @param speakerSettingStr
	 */
	private void saveSpeakerSettingsAndSendCmd(String speakerSettingStr) {
		List<CloudSetting> csList = DBM.getCloudSetting(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
		L.w("cloud size" + csList.size());
		if (csList.size() > 0) {
			CloudSetting cs = csList.get(0);
			cs.setParams(speakerSettingStr);
			DBM.getCurrentOrm().update(cs);
		} else {
			CloudSetting cs = new CloudSetting(AppUtil.getCurrentAccount(),
					"004", StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speakerSettingStr, null, null, null);
			DBM.getCurrentOrm().save(cs);
		}

		SPM.saveStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER,
				speakerSettingStr);

		mUIHander.obtainMessage(MUSIC_PLAY).sendToTarget();
	}

	/**
	 * 发送获取当前连接状态命令
	 */
	protected void sendCheckLinkStatusCmd() {
		if (productFun == null)
			return;

		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_DEVICE_LINK);

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onFail(Task task, Object[] arg) {
				mUIHander.obtainMessage(TOAST, getString(R.string.music_check_link_failed)).sendToTarget();
				mUIHander.obtainMessage(MUSIC_QUERY_STATUS).sendToTarget();
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				mUIHander.obtainMessage(MUSIC_QUERY_STATUS).sendToTarget();
			}

		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, null);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(this, NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}


	/**
	 * 发送查询当前播放状态命令
	 */
	private void sendPlayStatusCmd() {
		if (productFun == null)
			return;

//		isClicklable = false;

		params.clear();

		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_PLAY_STATUS);

		String input = SPM.getStr(MusicSetting.SP_NAME,
				MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB);
		params.put(StaticConstant.PARAM_INDEX,
				input.equalsIgnoreCase("USB") ? "1" : "2");

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onFail(Task task, Object[] arg) {
				isClicklable = true;
				if (arg == null) {
					ToastUtil.showShort(BaseApp.getContext(), R.string.music_query_song_failed);
				} else {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
					L.e("msg :" + resultObj.validResultInfo);
				}
				SPM.saveInt(MusicSetting.SP_NAME,
						MusicSetting.CURRENT_PLAYING_SONG, -1);
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				isClicklable = true;
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					String result = resultObj.validResultInfo;
					if (result.startsWith("T")) {
						if (result.length() > 2) {
							decodePlayState(String.valueOf(result.charAt(2)));
						}

						String playIndex = result.substring(3, 10);
						playIndex = playIndex.replaceFirst("^0+", "");
						if (TextUtils.isEmpty(playIndex)) {
							playIndex = "0";
						}

						if (result.length() > 14) {
							SPM.saveStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, result.substring(result.length() - 14, result.length() - 6));
						}
						SPM.saveInt(MusicSetting.SP_NAME,
								MusicSetting.CURRENT_PLAYING_SONG, Integer.valueOf(playIndex));
						mUIHander.obtainMessage(MUSIC_REFRESH_MUSIC_LIST, Integer.valueOf(playIndex)).sendToTarget();
					} else {
						SPM.saveBoolean(MusicSetting.SP_NAME,
								MusicSetting.AMPLIFIER_PALY_STATE, false);
					}
				}
			}

		};
		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, null);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(this, NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}

	/**
	 * 解析应答播放状态信息
	 *
	 * @param state
	 */
	private void decodePlayState(String state) {
		if ("1".equals(state)) {

		} else if ("2".equals(state)) {//播放状态
			SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, true);
		} else if ("3".equals(state) || "5".equals(state)) {//暂停或停止
			SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
		} else if ("4".equals(state)) {//静音
			SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_MUTE_STATE, false);
		} else {
			SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, false);
		}
	}

	/**
	 * 按歌曲索引播放
	 *
	 * @param index 歌曲索引
	 */
	protected void sendPlayCmdByIndex(final int index) {
		if (index < 0) {
			return;
		}

//		isClicklable = false;

		params.clear();

		productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG);

		String source = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT, StaticConstant.INPUT_TYPE_USB).toLowerCase(Locale.CHINA);
		;
		String speaker = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00010000");
		params.put(StaticConstant.PARAM_MUSIC_INDEX, index);
		params.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, speaker);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, source);
		params.put(StaticConstant.PARAM_MUSIC_SELECT_INPUT, source);

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onFail(Task task, Object[] arg) {
				isClicklable = true;
				mUIHander.obtainMessage(TOAST, getString(R.string.operate_failed)).sendToTarget();
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
				isClicklable = true;
				SPM.saveBoolean(MusicSetting.SP_NAME, MusicSetting.AMPLIFIER_PALY_STATE, true);
				mUIHander.obtainMessage(MUSIC_REFRESH_MUSIC_LIST, index).sendToTarget();
				mUIHander.obtainMessage(MUSIC_PLAY).sendToTarget();
			}

		};

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, null);
		OnlineCmdSenderLong onlineCmdSenderLong = new OnlineCmdSenderLong(this, NetConstant.hostFor485(), CmdType.CMD_485, cmdList, true, 0);
		onlineCmdSenderLong.addListener(listener);
		onlineCmdSenderLong.send();
	}

	@Subscribe
	public void onEventMainThread(PosEvent event) {
		if (musicList.size() < 1) {
			return;
		}
		Music m = musicList.get(event.getPos());
		if (m != null)
			sendPlayCmdByIndex(m.getPlayNo());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
	}
}
