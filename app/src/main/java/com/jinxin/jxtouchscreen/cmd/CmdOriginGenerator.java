package com.jinxin.jxtouchscreen.cmd;

import android.text.TextUtils;

import com.jinxin.cd.smarthome.product.service.newcmd.CmdEntry;
import com.jinxin.cd.smarthome.product.service.newcmd.CmdPolicy;
import com.jinxin.cd.smarthome.product.service.newcmd.GenColorLight;
import com.jinxin.cd.smarthome.product.service.newcmd.GenCurtain;
import com.jinxin.cd.smarthome.product.service.newcmd.GenGateway;
import com.jinxin.cd.smarthome.product.service.newcmd.GenHumanSense;
import com.jinxin.cd.smarthome.product.service.newcmd.GenLock;
import com.jinxin.cd.smarthome.product.service.newcmd.GenMCLight;
import com.jinxin.cd.smarthome.product.service.newcmd.GenPowerAmplifier2;
import com.jinxin.cd.smarthome.product.service.newcmd.GenUFO01;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGAirConditionSocket;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGAirController;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGBulb;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGCommon;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGCurtain;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGGateway;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGLock;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGSocket;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.util.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XTER on 2016/12/26.
 * 原始命令
 */
public class CmdOriginGenerator {

    /**
     * 生成原始命令(不带账号)
     *
     * @param account  账户
     * @param addr485  功能点的485地址
     * @param type     命令操作（在生成命令的原始文件中定义的，如灯的“open”“close”）
     * @param funUnits 功能点的funUnits参数
     * @param funType  功能点的类型（对应于ProductConstants）中的各个类型值
     * @param params   生成命令需要的参数
     * @return 生成的原始命令
     */

    public static List<String> createCmdWithNotAccount(String account,
                                                       String addr485, String type, List<String> funUnits, String funType,
                                                       Map<String, Object> params) {
        Map<String, Object> map = null;
        if (params == null) {
            map = new HashMap<String, Object>();
        } else {
            map = params;
        }
        /*****************指令包对象****************/
        CmdEntry cmdEntry = new CmdPolicy();
        try {
            cmdEntry.setCmdInputStream(BaseApp.getContext().
                            getResources().getAssets().open("cmd.ini"),
                    BaseApp.getContext().getResources().getAssets().open("cmdModel.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /****************************************/

        List<String> _cmdList = null;

        if (funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT) ||
                funType.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {// 灯
            map.put(StaticConstant.PARAM_UNITS, funUnits);
            _cmdList = cmdEntry.getCmd(GenMCLight.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_CURTAIN)) {// 窗帘（新接口）
            _cmdList = cmdEntry.getCmd(GenCurtain.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE)) {// 人体感应

        } else if (funType.equals(ProductConstants.FUN_TYPE_TFT_LIGHT)) {// 触摸屏

        } else if (funType.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {// 功放

        } else if (funType.equals(ProductConstants.FUN_TYPE_UFO1)) {// 飞碟一号
            map.put(StaticConstant.PARAM_INDEX, params.get(StaticConstant.PARAM_INDEX));
            _cmdList = cmdEntry.getCmd(GenUFO01.class, StaticConstant.OPERATE_SEND, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI)) {// 飞碟一号:温度和湿度
            _cmdList = cmdEntry.getCmd(GenUFO01.class, StaticConstant.OPERATE_GET_ENV, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)) {// 新人体感应遥控控制
            _cmdList = cmdEntry.getCmd(GenHumanSense.class,
                    StaticConstant.OPERATE_SEND, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK)) {// 自动锁
            map.put("units", funUnits);
            _cmdList = cmdEntry.getCmd(GenLock.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_SEARCH)) {// 自动锁状态
            _cmdList = cmdEntry.getCmd(GenLock.class, StaticConstant.OPERATE_SEARCH, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_OPEN)) {// 打开自动锁
            _cmdList = cmdEntry.getCmd(GenLock.class,
                    StaticConstant.OPERATE_OPEN, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_CLOSE)) {// 关闭自动锁
            _cmdList = cmdEntry.getCmd(GenLock.class,
                    StaticConstant.OPERATE_CLOSE, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY)) {// 网关 -文字
            map.put(StaticConstant.PARAM_TEXT, params.get(StaticConstant.PARAM_TEXT));
            addr485 = (String) map.get(StaticConstant.PARAM_ADDR);
            if (!TextUtils.isEmpty((String) params.get(StaticConstant.PARAM_TEXT)))
                _cmdList = cmdEntry.getCmd(GenGateway.class, StaticConstant.OPERATE_TEXT2SPEECH, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY_WATCH)) {// 网关:打开语音智能控制
            addr485 = (String) map.get(StaticConstant.PARAM_ADDR);
            _cmdList = cmdEntry.getCmd(GenGateway.class, "watch", null, null);
        } else if (funType.equals(ProductConstants.FUN_TYPE_SECUTIRY)) {// 网关:安防操作
            _cmdList = cmdEntry.getCmd(GenGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GESTURE)) {// 网关:手势识别
            _cmdList = cmdEntry.getCmd(GenGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) { // 彩灯
            if (map.size() < 1) {
                if (funUnits != null) {
                    String color = funUnits.get(0);
                    map.put(StaticConstant.PARAM_COLOR, color);
                }
            }
            _cmdList = cmdEntry.getCmd(GenColorLight.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY)) { // 功放-播放
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_PLAY_LUSHU, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE)) { // 功放-停止
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_PAUSE, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_MUTE)) { // 功放-静音
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_MUTE, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_UNMUTE)) { // 功放-取消静音
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_UNMUTE, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_PROVIOUS)) { // 功放-上一曲
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_PRE, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_NEXT)) { // 功放-下一曲
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_NEXT, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_ADD)) { // 功放-音量+
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_SOUND_ADD, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_SUB)) { // 功放-音量-
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_SOUND_SUB, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_LIST)) { // 功放-获取播放列表
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_COUNT, addr485, map);
        } else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_SONG)) { // 功放-获取播放歌曲名字
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_READ_NAME, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG)) { // 功放-播放指定歌曲
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_USBSD_PLAY, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL)) { // 功放-列表循环
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_REPEAT_ALL, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE)) { // 功放-单曲循环
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_REPEAT_SINGLE, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_DEVICE_LINK)) { // 功放-获取连接状态
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_GET_LINK, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_GET_VOLUME)) { // 功放-获取音量
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_GET_VOLUMN, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_SET_VOLUME)) { // 功放-设置音量
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_SET_VOLUMN, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_PLAY_STATUS)) { // 功放-获取播放状态
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_PLAY_STATUS, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_GET_VERSION)) { // 功放-获取功放版本
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_GET_VERSION, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_GET_BIND)) { // 功放-获取扬声器绑定
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_GET_BIND, addr485, map);
        } else if (funType
                .equals(ProductConstants.POWER_AMPLIFIER_GET_MUTE_STATUS)) { // 功放-扬声器静音状态
            _cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
                    StaticConstant.OPERATE_GET_MUTE_STATUS, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_MIC)) {
            _cmdList = new ArrayList<String>();
            _cmdList.add("USERKSSB");
        } else if (funType.equals(ProductConstants.FUN_TYPE_VOICE)) {
            addr485 = (String) map.get(StaticConstant.PARAM_ADDR);
            _cmdList = cmdEntry.getCmd(GenGateway.class, "setInput", addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SOCKET)) {
            map.put("dst", "0x01");
            map.put("src", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        }  else if (funType.equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)) {//双向开关
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)) {//多路开关
            Map<String, Object> map1 = new HashMap<>();
            if (map.get("dst").equals("0x01")) {
                map1.clear();
                map1.put("src", "0x01");
                map1.put("dst", "0x02");
                _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
            } else if (map.get("dst").equals("0x02")) {
                map1.clear();
                map1.put("src", "0x01");
                map1.put("dst", "0x01");
                _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
            } else {
                _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
            }
        } else if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE) ||
                funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR) ||
                funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE) ||
                funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {//三路开关
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_POP_LIGHT)) {//球灯泡
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        }else if (funType.equals(ProductConstants.FUN_TYPE_ONE_SWITCH)) {//单路交流开关
            map.put("dst", "0x01");
            map.put("src", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)) {//水晶灯
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)) {//吸顶灯
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)) {//无线射灯
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {//灯带
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)) {//无线窗帘
            _cmdList = cmdEntry.getCmd(GenZGCurtain.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_DEVICE_DETECT)) {// 终端定义 设备巡检funType
            _cmdList = cmdEntry.getCmd(GenGateway.class, "inspection", addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT)) {// 终端定义 zg设备巡检funType
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, "inspection", addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_GATEWAY)) {//无线网关
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_DOOR_CONTACT)) {//门磁
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_INFRARED)) {//ZG人体感应
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_SMOKE_SENSE)) {//烟感
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GAS_SENSE)) {//气感
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AIRCONDITION)) {//空调控制
            _cmdList = cmdEntry.getCmd(GenZGAirController.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_ZG_LOCK)) {// ZG 锁
            _cmdList = cmdEntry.getCmd(GenZGLock.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX)) {// Pluto音箱
            _cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {//五路开关
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET)) {//无线智能空调控制
            if (type == StaticConstant.OPERATE_COMMON_CMD) {
                _cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
            } else {
                _cmdList = cmdEntry.getCmd(GenZGAirConditionSocket.class, type, addr485, map);//读开关状态
            }
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)) {//无线红外转发
            _cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
        }
        if (_cmdList != null) {
            for (int i = 0; i < _cmdList.size(); i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(_cmdList.get(i));
                _cmdList.set(i, sb.toString());
                L.i("original cmd-->>>" + sb);
            }
        }

        return _cmdList;
    }

    /**
     * 通过指令算法生成(带用户名)的指令
     *
     * @param account
     * @param addr485
     * @param type
     * @param lightNumbers
     * @param funType
     * @param params
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<String> createCmdWithAccount(String account,
                                                    String addr485, String type, List<String> lightNumbers,
                                                    String funType, Map<String, Object> params) {
        Map<String, Object> map = null;
        if (params == null) {
            map = new HashMap<>();
        } else {
            map = params;
        }
        /*****************指令包对象****************/
        CmdEntry cmdEntry = new CmdPolicy();//指令包对象
        try {
            cmdEntry.setCmdInputStream(BaseApp.getContext().
                            getResources().getAssets().open("cmd.ini"),
                    BaseApp.getContext().getResources().getAssets().open("cmdModel.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /******************************************/

        List<String> _cmdList = null;

        if (funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT) ||
                funType.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {// 灯
            map.put(StaticConstant.PARAM_UNITS, lightNumbers);
            _cmdList = cmdEntry.getCmd(GenMCLight.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN)) {// 新窗帘
            map.put(StaticConstant.PARAM_TEXT, params.get(StaticConstant.PARAM_TEXT));
            _cmdList = cmdEntry.getCmd(GenCurtain.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE)) {// 人体感应

        } else if (funType.equals(ProductConstants.FUN_TYPE_TFT_LIGHT)) {// 触摸屏

        } else if (funType.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {// 功放

        } else if (funType.equals(ProductConstants.FUN_TYPE_UFO1)) {// 飞碟一号
            map.put(StaticConstant.PARAM_INDEX, params.get(StaticConstant.PARAM_INDEX));
            _cmdList = cmdEntry.getCmd(GenUFO01.class,
                    StaticConstant.OPERATE_SEND, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI)) {// 飞碟一号:温度和湿度
            _cmdList = cmdEntry.getCmd(GenUFO01.class,
                    StaticConstant.OPERATE_GET_ENV, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)) {// 新人体感应遥控控制
            _cmdList = cmdEntry.getCmd(GenHumanSense.class,
                    StaticConstant.OPERATE_SEND, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK)) {// 自动锁
            map.put("units", lightNumbers);
//			_cmdList = GenLock.generateCmd(type, addr485, map);
            _cmdList = cmdEntry.getCmd(GenLock.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_SEARCH)) {// 自动锁状态
//			_cmdList = GenLock
//					.generateCmd(GenLock.OPERATE_SEARCH, addr485, map);
            _cmdList = cmdEntry.getCmd(GenLock.class,
                    StaticConstant.OPERATE_SEARCH, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_OPEN)) {// 打开自动锁
//			_cmdList = GenLock.generateCmd(GenLock.OPERATE_OPEN, addr485, map);
            _cmdList = cmdEntry.getCmd(GenLock.class,
                    StaticConstant.OPERATE_OPEN, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_CLOSE)) {// 关闭自动锁
//			_cmdList = GenLock.generateCmd(GenLock.OPERATE_CLOSE, addr485, map);
            _cmdList = cmdEntry.getCmd(GenLock.class,
                    StaticConstant.OPERATE_CLOSE, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY)) {// 网关 文字-->语音
            map.put(StaticConstant.PARAM_TEXT, params.get(StaticConstant.PARAM_TEXT));
//			_cmdList = GenGateway.generateCmd(addr485,
//					GenGateway.OPERATE_TEXT2SPEECH, map);
            if (!TextUtils.isEmpty((String) params.get(StaticConstant.PARAM_TEXT)))
                _cmdList = cmdEntry.getCmd(GenGateway.class, StaticConstant.OPERATE_TEXT2SPEECH, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY_VOICE_LANGUAGE)) {// 网关:方言切换
            if (!TextUtils.isEmpty((String) params.get(StaticConstant.PARAM_TEXT)))
                _cmdList = cmdEntry.getCmd(GenGateway.class, StaticConstant.OPERATE_VOICE_ROLE, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY_WATCH)) {// 网关:打开语音智能控制
//			_cmdList = GenGateway.generateCmd(null, GenGateway.OPERATE_WATCH,
//					map);
            _cmdList = cmdEntry.getCmd(GenGateway.class,
                    StaticConstant.OPERATE_WATCH, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_SECUTIRY)) {// 网关:安防操作
//			_cmdList = GenGateway.generateCmd(null, type, map);
            _cmdList = cmdEntry.getCmd(GenGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_GESTURE)) {// 网关:手势识别
//			_cmdList = GenGateway.generateCmd(addr485, type, map);
            _cmdList = cmdEntry.getCmd(GenGateway.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {// 彩灯
            if (lightNumbers != null) {
                String color = lightNumbers.get(0);
                map.put(StaticConstant.PARAM_COLOR, color);
            }
//			_cmdList = GenColorLight.generateCmd(type, addr485, map);
            _cmdList = cmdEntry.getCmd(GenColorLight.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_MIC)) {
            _cmdList = new ArrayList<String>();
            _cmdList.add("USERKSSB");
        } else if (funType.equals(ProductConstants.FUN_TYPE_DEVICE_DETECT)) {// 终端定义 设备巡检funType
            _cmdList = cmdEntry.getCmd(GenGateway.class, "inspection", addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT)) {// 终端定义 zg设备巡检funType
            _cmdList = cmdEntry.getCmd(GenZGGateway.class, "inspection", addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_VOICE)) {
            _cmdList = cmdEntry.getCmd(GenGateway.class, "setInput", addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SOCKET)) {
            map.put("dst", "0x01");
            map.put("src", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        }  else if (funType.equals(ProductConstants.FUN_TYPE_POP_LIGHT)) {
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)) {
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)) {
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_ONE_SWITCH)) {//单路交流开关
            map.put("dst", "0x01");
            map.put("src", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)) {//无线射灯
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {//灯带
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)) {
            _cmdList = cmdEntry.getCmd(GenZGCurtain.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)) {//双向开关
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE) ||
                funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR) ||
                funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE) ||
                funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {//三路开关
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO) ||
                funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)) {//多路开关
            Map<String, Object> map1 = new HashMap<>();
            if (map.get("dst").equals("0x01")) {
                map1.clear();
                map1.put("src", "0x01");
                map1.put("dst", "0x02");
                _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
            } else if (map.get("dst").equals("0x02")) {
                map1.clear();
                map1.put("src", "0x01");
                map1.put("dst", "0x01");
                _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
            } else {
                _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
            }
        } else if (funType.equals(ProductConstants.FUN_TYPE_ZG_LOCK)) {// ZG 锁
            _cmdList = cmdEntry.getCmd(GenZGLock.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX)) {//Pluto音箱
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {//五路开关
            _cmdList = cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET)) {//无线智能空调控制
            if (type == StaticConstant.OPERATE_COMMON_CMD) {
                _cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
            } else {
                _cmdList = cmdEntry.getCmd(GenZGAirConditionSocket.class, type, addr485, map);//读开关状态
            }
        } else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)) {//无线红外转发
            map.put("src", "0x01");
            map.put("dst", "0x01");
            _cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
        }
        if (_cmdList != null) {
            for (int i = 0; i < _cmdList.size(); i++) {
                StringBuffer sb = new StringBuffer(account);
                sb.append("|");
                sb.append(_cmdList.get(i));
                _cmdList.set(i, sb.toString());
                L.i("original cmd: " + sb);
            }
        }
        return _cmdList;
    }
}
