package com.jinxin.jxtouchscreen.cmd;

import java.util.List;

/**
 * 命令对象
 * @author TangLong
 * @company 金鑫智慧
 */
public class Command {
	/** 网关序列号  */
	private String gatewaySn;
	/** 网关的本地IP */
	private String gatewayLocalIp;
	/** 网关的远程IP */
	private String gatewayRemoteIp;
	/** 命令列表  */
	private List<byte[]> cmdList;
	/** 是否为Zigbee */
	private boolean isZigbee;

	public Command() {
		super();
	}

	public Command(String gatewaySn, List<byte[]> cmdList) {
		super();
		this.gatewaySn = gatewaySn;
		this.cmdList = cmdList;
	}
	
	@Override
	public String toString() {
		return "Command [gatewaySn=" + gatewaySn + ", gatewayLocalIp="
				+ gatewayLocalIp + ", gatewayRemoteIp=" + gatewayRemoteIp
				+ ", cmdList=" + cmdList + "]";
	}

	public String getGatewaySn() {
		return gatewaySn == null ? "" : gatewaySn;
	}

	public void setGatewaySn(String gatewaySn) {
		this.gatewaySn = gatewaySn;
	}

	public List<byte[]> getCmdList() {
		return cmdList;
	}

	public void setCmdList(List<byte[]> cmdList) {
		this.cmdList = cmdList;
	}

	public String getGatewayLocalIp() {
		return gatewayLocalIp == null ? "" : gatewayLocalIp;
	}

	public void setGatewayLocalIp(String gatewayLocalIp) {
		this.gatewayLocalIp = gatewayLocalIp;
	}

	public String getGatewayRemoteIp() {
		return gatewayRemoteIp == null ? "" : gatewayRemoteIp;
	}

	public void setGatewayRemoteIp(String gatewayRemoteIp) {
		this.gatewayRemoteIp = gatewayRemoteIp;
	}

	public boolean isZigbee() {
		return isZigbee;
	}

	public void setZigbee(boolean isZigbee) {
		this.isZigbee = isZigbee;
	}
}
