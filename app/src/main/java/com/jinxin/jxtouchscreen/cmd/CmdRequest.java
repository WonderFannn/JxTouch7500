package com.jinxin.jxtouchscreen.cmd;

/**
 * Created by XTER on 2017/01/19.
 * 命令请求信息
 */
public class CmdRequest {

	/**
	 * 命令归处
	 */
	private String host;

	/**
	 * 命令应答解析
	 */
	private CmdResponse cmdResponse;


	public CmdRequest(String host, CmdResponse cmdResponse) {
		this.host = host;
		this.cmdResponse = cmdResponse;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}


	public CmdResponse getCmdResponse() {
		return cmdResponse;
	}

	public void setCmdResponse(CmdResponse cmdResponse) {
		this.cmdResponse = cmdResponse;
	}
}
