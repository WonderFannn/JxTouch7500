package com.jinxin.jxtouchscreen.cmd;

import android.text.TextUtils;

import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Created by XTER on 2017/01/20.
 * 命令应答解析--有线
 */
public class CmdResponseFor485 extends CmdResponse {

	private Task task;
	private byte[] requestBytes;
	private int requestType = 0;
	private int cmdCount = 0;// 指令数目
	private String result;

	public CmdResponseFor485(Task task, byte[] requestBytes, int requestType) {
		this.task = task;
		this.requestBytes = requestBytes;
		if (this.requestBytes != null) {
			String _cmd = new String(requestBytes);
			this.cmdCount = cCount(_cmd, '|');
		}
		this.requestType = requestType;
	}

	/**
	 * 统计字符方法
	 *
	 * @param str 被检测的字符串
	 * @param c   被统计的字符
	 */
	public int cCount(String str, char c) {
		if (str == null) return 0;
		int i = 0;
		char[] _charArray = str.toCharArray();
		for (char temp : _charArray) {
			if (temp == c) i++;
		}
		return i;
	}

	/**
	 * 检测结果成功与否初级策略
	 *
	 * @return
	 */
	private boolean checkResult() {
		return !TextUtils.isEmpty(result) && result.length() > 2;
//		return !TextUtils.isEmpty(result);
	}

	/**
	 * 指令结果集中是否有用该code打头的返回信息
	 *
	 * @param code
	 * @return
	 */
	private boolean rusultStartWith(String code) {
		return !TextUtils.isEmpty(result) && result.startsWith(code);
	}

	@Override
	public void response(InputStream is) {
		if (is != null) {
			L.d("cmd count: " + cmdCount);
			boolean isSuccess = false;
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(is);
				for (int i = 0; i < this.cmdCount; i++) {
					// 响应类型
					int cmd = dis.readInt();
					// 内容长度
					int len = dis.readInt();
					L.d("response length: " + len);
					if (len != 0) {
						//长度过大不进行解析
						if (len < 1024 * 10) {
							byte[] buf = new byte[len];
							dis.read(buf);
							result = new String(buf);
							L.d("cmdResult: " + result);
						} else {
							result = "";
						}
					} else {
						result = "";
					}
				}
				isSuccess = this.checkResult();

			} catch (Exception ex) {
				ex.printStackTrace();
				result = "";
				isSuccess = false;
			} finally {
				this.task.setState(isSuccess ? Task.STATE_SUCCESS : Task.STATE_FAIL);
				RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
				if (isSuccess) {
					if (requestType == 1) {
						resultInfo.validResultCode = "0000";
						String _content = result;
						resultInfo.validResultInfo = _content;
						this.setResultInfo(resultInfo);
						this.task.onSuccess(resultInfo);
					} else {
						String _content = result;
						resultInfo.validResultCode = "0000";
						resultInfo.validResultInfo = _content;
						this.setResultInfo(resultInfo);
						this.task.onSuccess(resultInfo);
					}
				} else {
					if (requestType == 1) {
						resultInfo.validResultCode = "0000";
						String _content = result;
						resultInfo.validResultInfo = _content;
						this.setResultInfo(resultInfo);
						this.task.onFailed(resultInfo);
					} else {
						L.e("操作失败");
						if (this.rusultStartWith("01")) {
							resultInfo.validResultCode = "01";
							resultInfo.validResultInfo = "网关处于离线";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);

						} else if (this.rusultStartWith("02")) {
							resultInfo.validResultCode = "02";
							resultInfo.validResultInfo = "处理超时，请检查设备与网关连接是否正常";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);

						} else if (this.rusultStartWith("03")) {
							resultInfo.validResultCode = "03";
							resultInfo.validResultInfo = "服务端业务处理失败";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);

						} else if (this.rusultStartWith("04")) {
							resultInfo.validResultCode = "04";
							resultInfo.validResultInfo = "未知异常";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);

						} else if (this.rusultStartWith("T")) {
							resultInfo.validResultCode = "0000";
							resultInfo.validResultInfo = "操作成功";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);
						} else if (this.rusultStartWith("F")) {
							resultInfo.validResultCode = "0000";
							resultInfo.validResultInfo = "处理失败";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);
						} else {
							resultInfo.validResultCode = "1111";
							resultInfo.validResultInfo = "操作失败";
							this.setResultInfo(resultInfo);
							this.task.onFailed(resultInfo);
						}
					}
				}
				this.task.onFinish();
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

	@Override
	public void onCmdResponse(String result) {
		this.result = result;
		boolean isSuccess = this.checkResult();
		this.task.setState(isSuccess ? Task.STATE_SUCCESS : Task.STATE_FAIL);
		RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
		if (isSuccess) {
			if (requestType == 1) {
				resultInfo.validResultCode = "0000";
				resultInfo.validResultInfo = result;
				this.setResultInfo(resultInfo);
				this.task.onSuccess(resultInfo);
			} else {
				resultInfo.validResultCode = "0000";
				resultInfo.validResultInfo = result;
				this.setResultInfo(resultInfo);
				this.task.onSuccess(resultInfo);
			}
		} else {
			if (requestType == 1) {
				resultInfo.validResultCode = "0000";
				resultInfo.validResultInfo = result;
				this.setResultInfo(resultInfo);
				this.task.onFailed(resultInfo);
			} else {
				L.e("操作失败");
				if (this.rusultStartWith("01")) {
					resultInfo.validResultCode = "01";
					resultInfo.validResultInfo = "网关处于离线";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);

				} else if (this.rusultStartWith("02")) {
					resultInfo.validResultCode = "02";
					resultInfo.validResultInfo = "处理超时，请检查设备与网关连接是否正常";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);

				} else if (this.rusultStartWith("03")) {
					resultInfo.validResultCode = "03";
					resultInfo.validResultInfo = "服务端业务处理失败";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);

				} else if (this.rusultStartWith("04")) {
					resultInfo.validResultCode = "04";
					resultInfo.validResultInfo = "未知异常";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);

				} else if (this.rusultStartWith("T")) {
					resultInfo.validResultCode = "0000";
					resultInfo.validResultInfo = "操作成功";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);
				} else if (this.rusultStartWith("F")) {
					resultInfo.validResultCode = "0000";
					resultInfo.validResultInfo = "处理失败";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);
				} else {
					resultInfo.validResultCode = "1111";
					resultInfo.validResultInfo = "操作失败";
					this.setResultInfo(resultInfo);
					this.task.onFailed(resultInfo);
				}
			}
		}
		this.task.onFinish();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		L.w(cause.getMessage());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if (!session.isClosing()) {
			L.w("无读写，进入闲置，命令通道关闭");
			RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
			resultInfo.validResultCode = "1111";
			resultInfo.validResultInfo = "操作失败";
			this.setResultInfo(resultInfo);
			this.task.setState(Task.STATE_FAIL);
			this.task.onFailed(resultInfo);
			this.task.onFinish();
			session.close(true);
		}
	}
}
