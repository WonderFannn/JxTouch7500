package com.jinxin.jxtouchscreen.cmd;


import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.net.util.URLParser;
import com.jinxin.jxtouchscreen.util.L;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by XTER on 2017/01/19.
 * 命令连接器
 */
public class CmdConnector {
	public Socket socket;

	public int cmdConnect(String url, CmdResponse response) {
		int state = 0;
		InputStream is = null;
		try {
			is = connect(url, response.toOutputBytes());
			if (is == null) {
				RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
				resultInfo.validResultCode = "0404";
				resultInfo.validResultInfo = "网络异常";
				response.setResultInfo(resultInfo);
				state = Task.STATE_FAIL;
			} else {
				//解析
				response.response(is);
				if ("0000".equals(response.getResultInfo().validResultCode)) {
					L.d("CmdConnector Connect Successfully");
					state = Task.STATE_SUCCESS;
				} else {
					L.d("CmdConnector Connect Failed");
					state = Task.STATE_FAIL;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			state = Task.STATE_FAIL;
		} finally {
			closeInputStream(is);
			closeSocket();
		}
		return state;
	}

	/**
	 * 连接获取回应数据
	 *
	 * @param appURL  地址
	 * @param request 请求数据
	 * @return inputstream
	 */
	public InputStream connect(String appURL, byte[] request) {
		URLParser urlParser = new URLParser(appURL);
		String ip = urlParser.host;
		int port = urlParser.port;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			//创建socket连接
			if (socket == null) {
				try {
					//解析域名
					java.net.InetAddress[] x = java.net.InetAddress
							.getAllByName(ip);
					ip = x[0].getHostAddress();
				} catch (UnknownHostException ex) {
					L.e(ex.toString());
				}
				//设置地址与超时时间
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port),
						NetConstant.DEFAULT_CONNECT_TIME_OUT);
			}
			//获取当前输入流，准备输入
			outputStream = socket.getOutputStream();
			//如果处于连接状态，则写入数据
			if (socket.isConnected()) {
				if (request != null)
					outputStream.write(request);

				outputStream.flush();
				//得到当前输出流，即服务端回应数据
				inputStream = socket.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * 清socket
	 */
	public void closeSocket() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception ex) {
			L.e(ex.toString());
		}
	}

	/**
	 * 清输出流
	 */
	public void closeOutputStream(OutputStream outputStream) {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception ex) {
			L.e(ex.toString());
		}
	}

	/**
	 * 清输入流
	 */
	public void closeInputStream(InputStream inputStream) {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception ex) {
			L.e(ex.toString());
		}
	}

}
