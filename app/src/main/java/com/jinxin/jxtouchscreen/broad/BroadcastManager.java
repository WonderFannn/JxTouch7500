package com.jinxin.jxtouchscreen.broad;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.jinxin.jxtouchscreen.app.BaseApp;

/**
 * 应用内部广播管理器
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class BroadcastManager {
	/*********广播类型************************************/
	/**
	 * 更新设备信息
	 */
	public static final String ACTION_UPDATE_DEVICE_MESSAGE = "action_update_device_message";
	/**
	 * 普通信息
	 */
	public static final String ACTION_COMMON_MESSAGE = "action_common_message";
	
	/**
	 * 推送状态消息
	 */
	public static final String MESSAGE_RECEIVED_ACTION = "action_message_received";
	/**
	 * 推送报警消息
	 */
	public static final String MESSAGE_WARN_RECEIVED_ACTION = "action_warn_message_received";
	/**
	 * 推送ZG锁电量消息
	 */
	public static final String MESSAGE_WARN_LOW_POWER = "action_warn_low_power";
	/**
	 * 推送ZG锁异常报警消息
	 */
	public static final String MESSAGE_WARN_ERROR = "action_warn_error";
	/**
	 * 退出信息
	 */
	public static final String ACTION_EXIT_MESSAGE = "action_exit_message";
	/**
	 * 退出所有打开界面，重新登录
	 */
	public static final String ACTION_EXIT_MESSAGE_RELOGIN = "action_exit_message_relogin";
	/**
	 * 更新模式
	 */
	public static final String ACTION_UPDATE_MODE_MESSAGE = "action_update_mode_message";
	/**
	 * 更新定时任务页面列表数据
	 */
	public static final String ACTION_UPDATE_TASK_MESSAGE = "action_update_task_message";
	/**
	 * 彩灯批量控制
	 */
	public static final String ACTION_COLOR_LIGHT_MESSAGE = "action_color_light_message";
	/**
	 * 取消彩灯批量控制
	 */
	public static final String ACTION_COLOR_LIGHT_CANCLE_MESSAGE = "action_color_light_cancle_message";
	/**
	 * 模式彩灯颜色设置
	 */
	public static final String ACTION_MODE_COLOR_SET_MESSAGE = "action_mode_color_set_message";
	
	public static final String ACTION_MODE_MUSIC_SET_MESSAGE = "action_mode_music_set_message";
	
	public static final String ACTION_MODE_MUSIC_CLOUD_MESSAGE = "action_mode_music_cloud_message";
	
	/**
	 * 离线转在线Action
	 */
	public static final String ACTION_CHANGE_ON_LINE_MODE = "action_chang_on_line_mode";
	/**
	 * 在线转离线Action
	 */
	public static final String ACTION_CHANGE_OFF_LINE_MODE = "action_chang_off_line_mode";
	
	/**
	 * Upnp音乐播放控制
	 */
	public static final String ACTION_MUSIC_PLAY_CONTROL = "action_music_play_control";
	/**
	 * Upnp音乐暂停
	 */
	public static final String ACTION_MUSIC_PLAY_PAUSE = "action_music_play_pause";
	/**
	 * Upnp音乐暂停
	 */
	public static final String ACTION_MUSIC_PLAY_SEEK = "action_music_play_seek";
	/**
	 * Upnp上一曲
	 */
	public static final String ACTION_MUSIC_PRE_SONG = "action_music_pre_song";
	/**
	 * Upnp下一曲
	 */
	public static final String ACTION_MUSIC_NEXT_SONG = "action_music_next_song";
	/**
	 * Upnp Device列表添加
	 */
	public static final String ACTION_DLNA_DEVICE_LIST_ADD = "action_dlna_device_list_add";
	/**
	 * Upnp Device列表删除
	 */
	public static final String ACTION_DLNA_DEVICE_LIST_REMOVE = "action_dlna_device_list_remove";
	/**
	 * Upnp Device搜索
	 */
	public static final String ACTION_DLNA_DEVICE_SEARCH = "action_dlna_device_search";
	/**
	 * Upnp Device选中
	 */
	public static final String ACTION_DLNA_DEVICE_CHOOSE = "action_dlna_device_choose";
	/**
	 * 更新界面音乐播放时间
	 */
	public static final String ACTION_MUSIC_UPDATE_TIME = "action_music_update_time";
	/**
	 * 音乐切换下一首
	 */
	public static final String ACTION_MUSIC_CHANGE_NEXT_SONG= "action_music_change_next_song";
	/**
	 * 音乐暂停
	 */
	public static final String ACTION_MUSIC_PAUSE= "action_music_pause";
	/**
	 * 音乐暂停
	 */
	public static final String ACTION_CHANG_MUSIC_IN_BACKGROUND= "action_change_music_in_background";
	/**
	 * 语音播放
	 */
	public static final String ACTION_VOICE_MULTIPLE_PLAY= "action_voice_multiple_play";
	/**
	 * 播放下一条语音
	 */
	public static final String ACTION_VOICE_MULTIPLE_PLAY_NEXT= "action_voice_multiple_play_next";
	/**
	 * 语音定时触发
	 */
	public static final String ACTION_DO_VOICE_TIMER= "action_do_voice_timer";
	/**
	 * 语音定时触发
	 */
	public static final String ACTION_VOICE_TIMER_CANCLE= "action_voice_timer_cancle";
	/**
	 * 语音定时添加
	 */
	public static final String ACTION_VOICE_TIMER_ADD= "action_voice_timer_add";
	/**
	 * 语音定时修改
	 */
	public static final String ACTION_VOICE_TIMER_AMEND= "action_voice_timer_amend";
	/**
	 * 非听写界面重启唤醒服务广播
	 */
	public static final String ACTION_VOICE_WAKE= "action_voice_wake";
	/**
	 * 关闭唤醒服务广播
	 */
	public static final String ACTION_VOICE_WAKE_CLOSE= "action_voice_wake_close";
	/**
	 * 听写界面重启唤醒服务广播
	 */
	public static final String ACTION_VOICE_WAKE_AGAIN = "action_voice_wake_again";
	/**
	 * 刷新联动界面
	 */
	public static final String ACTION_REFRESH_CONNECTION_UI= "action_refresh_connection_ui";
	
	/**
	 * 发送广播消息
	 * 
	 * @param action
	 *            广播消息名称
	 * @param bundle
	 *            传参
	 */
	public static void sendBroadcast(String action, Bundle bundle) {
		if (action == null)
			return;
		// 指定广播目标Action
		Intent _itent = new Intent(action);
		// 可通过Intent携带消息
		if (bundle != null) {
			_itent.putExtras(bundle);
		}
		// 发送广播消息
		BaseApp.getContext().sendBroadcast(_itent);
	}
	/**
	 * 注册广播
	 */
	public static void registerBoradcastReceiver1(BroadcastReceiver broadcastReceiver,String... action) {
		if(broadcastReceiver == null || action == null)return;
		IntentFilter myIntentFilter = new IntentFilter();
			// 添加捕获的广播事件
			for(int i = 0;i < action.length;i++){
				myIntentFilter.addAction(action[i]);
			}

		// 注册广播
		BaseApp.getContext().registerReceiver(broadcastReceiver, myIntentFilter);
	}
	/**
	 * 注册广播
	 */
	public static void registerBoradcastReceiver2(BroadcastReceiver broadcastReceiver,String[] action) {
		if(broadcastReceiver == null || action == null)return;
		IntentFilter myIntentFilter = new IntentFilter();
			// 添加捕获的广播事件
			for(int i = 0;i < action.length;i++){
				myIntentFilter.addAction(action[i]);
			}

		// 注册广播
		BaseApp.getContext().registerReceiver(broadcastReceiver, myIntentFilter);
	}
	/**
	 * 注销广播接收器
	 * @param broadcastReceiver
	 */
	public static void unregisterBoradcastReceiver(BroadcastReceiver broadcastReceiver){
		if(broadcastReceiver == null)return;
		BaseApp.getContext().unregisterReceiver(broadcastReceiver);
	}
}
