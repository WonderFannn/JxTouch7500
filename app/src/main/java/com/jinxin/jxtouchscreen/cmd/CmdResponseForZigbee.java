package com.jinxin.jxtouchscreen.cmd;

import android.text.TextUtils;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Created by XTER on 2017/01/20.
 * 命令应答解析--无线
 */
public class CmdResponseForZigbee extends CmdResponse {

	private Task task;
	private byte[] requestBytes;
	private CmdType parseStrategy;
	/**
	 * 1-ZG锁结果解析，0-其他无线设备解析
	 */
	private int requestType = 0;

	public CmdResponseForZigbee(Task task, byte[] requestBytes, CmdType parseStrategy, int requestType) {
		this.task = task;
		this.requestBytes = requestBytes;
		this.parseStrategy = parseStrategy;
		this.requestType = requestType;
	}

	@Override
	public void response(InputStream is) {
		RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
		if (is != null) {
			DataInputStream dis = new DataInputStream(is);
			String tempStr = "-1";
			boolean isSuccess = true;
			/* Zigbee 解析，偶数行解析  */
			if (parseStrategy == CmdType.CMD_ZG) {
				int cmdLength = getCmdArrayLength();
				if (cmdLength > 0) {
					for (int i = 1; i <= cmdLength; i++) {
						try {
							// 当指令超过一条时，为奇数行命令时，不对结果做解析，但仍然会有返回
							if (cmdLength > 1 && i % 2 != 0) {
								break;
							}
							int type = dis.readInt();
							int len = dis.readInt(); // 内容长度
							L.d("response length: " + len);
							if (len != 0) {
								//长度过大不进行解析
								if (len < 1024 * 10) {
									byte[] buf = new byte[len];

									dis.read(buf);
									tempStr = new String(buf);
									L.d("cmdResult: " + tempStr);
									if (tempStr.startsWith("01") || tempStr.startsWith("02")) {
										isSuccess = false;
										break;
									} else {
										//解析返回结果，最后两位“00”代表成功，其他代表失败
										if (!TextUtils.isEmpty(tempStr)) {
											//如果返回结果包含此字符串则为巡检指令，不做进一步判断
											if (tempStr.contains("cluster=0x0000")) {
												isSuccess = true;
											} else {
												//对结果进一步解析 以“00”结尾代表成功
												String payload = tempStr.substring(tempStr.indexOf("[") + 1,
														tempStr.indexOf("]")).trim();
												L.d("payload:" + payload);
												if (!TextUtils.isEmpty(payload)) {
													if (this.requestType == LocalConstant.CMD_ZG_LOCK) {
														tempStr = payload;
														isSuccess = true;
													} else {
														if (payload.endsWith("00")) {
															isSuccess = true;
														}
													}
												} else {
													isSuccess = false;
													tempStr = "操作失败";
												}
											}
										}

									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							this.task.setState(isSuccess ? Task.STATE_SUCCESS : Task.STATE_FAIL);
							if (isSuccess) {
								resultInfo.validResultCode = "0000";
								resultInfo.validResultInfo = tempStr;
								L.d(resultInfo.toString());
								this.setResultInfo(resultInfo);
								this.task.onSuccess(resultInfo);
							} else {
								L.e("操作失败");
								resultInfo.validResultCode = tempStr.substring(0, 2);
								L.d(resultInfo.validResultCode);
								if (NetConstant.SERVER_ERROR_MSG_01.equals(resultInfo.validResultCode)) {
									resultInfo.validResultInfo = "无线网关离线";
								} else if (NetConstant.SERVER_ERROR_MSG_02.equals(resultInfo.validResultCode)) {
									resultInfo.validResultInfo = "处理超时";
								} else {
									resultInfo.validResultInfo = tempStr;
								}
								L.d(resultInfo.toString());
								this.setResultInfo(resultInfo);
								this.task.onFailed(resultInfo);
							}
						}
					}
				}
			} else if (parseStrategy == CmdType.CMD_ZG_INSPECT) {//巡检
				try {
					int len = dis.readInt(); // 内容长度
					if (len != 0) {
						byte[] buf = new byte[len];
						L.d("-->len:" + len);
						dis.read(buf);
						tempStr = new String(buf);
						L.d("zg inspect content:" + tempStr);
					}
					if (tempStr.startsWith("01") || tempStr.startsWith("02")) {
						isSuccess = false;
					}
					resultInfo.validResultCode = "0000";
					resultInfo.validResultInfo = tempStr;
					L.d(resultInfo.toString());
				} catch (Exception e) {
					L.d(e.getMessage());
					resultInfo.validResultCode = "0001";
					resultInfo.validResultInfo = "解析失败";
				} finally {
					this.setResultInfo(resultInfo);
					this.task.onSuccess(resultInfo);
				}
			}

		} else {
			L.d("call onFail");
			resultInfo.validResultCode = "0001";
			resultInfo.validResultInfo = "请求失败";
			this.setResultInfo(resultInfo);
			this.task.onFailed(resultInfo);
		}
		this.task.onFinish();
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

	@Override
	public void onCmdResponse(String result) {
		RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
		boolean isSuccess = true;
		if(TextUtils.isEmpty(result)){
			isSuccess = false;
			resultInfo.validResultCode = "0001";
			resultInfo.validResultInfo = "请求失败";
			this.setResultInfo(resultInfo);
			this.task.onFailed(resultInfo);
		}else{
			/* Zigbee 解析，偶数行解析  */
			if (parseStrategy == CmdType.CMD_ZG) {
				int cmdLength = getCmdArrayLength();
				L.d("cmdLength = " + cmdLength);
				if(cmdLength>0){
					for(int i = 1; i <= cmdLength; i++) {
						if (result.startsWith("01") || result.startsWith("02")) {
							isSuccess = false;
							break;
						} else {
							//解析返回结果，最后两位“00”代表成功，其他代表失败
							if (!TextUtils.isEmpty(result)) {
								//如果返回结果包含此字符串则为巡检指令，不做进一步判断
								if (result.contains("cluster=0x0000")) {
									isSuccess = true;
								} else {
									//对结果进一步解析 以“00”结尾代表成功
									String payload = result.substring(result.indexOf("[") + 1,
											result.indexOf("]")).trim();
									L.d("payload:" + payload);
									if (!TextUtils.isEmpty(payload)) {
										if (this.requestType == LocalConstant.CMD_ZG_LOCK) {
											result = payload;
											isSuccess = true;
										} else {
											if (payload.endsWith("00")) {
												isSuccess = true;
											}
										}
									} else {
										isSuccess = false;
										result = "操作失败";
									}
								}
							}
						}
						if (isSuccess) {
							resultInfo.validResultCode = "0000";
							resultInfo.validResultInfo = result;
							this.setResultInfo(resultInfo);
							this.task.onSuccess(resultInfo);
						} else {
							resultInfo.validResultCode = result.substring(0, 2);
							if (NetConstant.SERVER_ERROR_MSG_01.equals(resultInfo.validResultCode)) {
								resultInfo.validResultInfo = "无线网关离线";
							} else if (NetConstant.SERVER_ERROR_MSG_02.equals(resultInfo.validResultCode)) {
								resultInfo.validResultInfo = "处理超时";
							} else {
								resultInfo.validResultInfo = result;
							}
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);
						}
					}
				}

			}else if (parseStrategy == CmdType.CMD_ZG_INSPECT) {//巡检
				L.d("-->content:" + result);
				isSuccess = true;
				resultInfo.validResultCode = "0000";
				resultInfo.validResultInfo = result;
				L.d(resultInfo.toString());
				this.setResultInfo(resultInfo);
				this.task.onSuccess(resultInfo);
			}
		}
		this.task.setState(isSuccess ? Task.STATE_SUCCESS : Task.STATE_FAIL);
		this.task.onFinish();
	}

	/**
	 * 获取命令的长度
	 */
	private int getCmdArrayLength() {
		if (requestBytes != null) {
			String cmdArrayStr = new String(requestBytes);
			return cmdArrayStr.split(",").length - 1;
		}
		return 0;
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		L.w("无读写，进入闲置，命令通道关闭");
		if(!session.isClosing()){
			RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
			resultInfo.validResultCode = "1111";
			resultInfo.validResultInfo = "操作失败";
			this.setResultInfo(resultInfo);
			this.task.onFailed(resultInfo);
			this.task.setState(Task.STATE_FAIL);
			this.task.onFinish();
			session.close(true);
		}
	}
}
