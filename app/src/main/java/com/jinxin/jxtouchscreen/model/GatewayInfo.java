package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by XTER on 2017/03/07.
 * 大网关固定信息
 */
@Table(LocalConstant.GATEWAY_INFO)
public class GatewayInfo extends BaseModel {

	private String gateway_name;
	private String gateway_account;
	private String gateway_ip;
	private String gateway_sn;
	private String gateway_mac;

	public GatewayInfo(){}

	public GatewayInfo(String gateway_name, String gateway_account, String gateway_ip, String gateway_sn, String gateway_mac) {
		this.gateway_name = gateway_name;
		this.gateway_account = gateway_account;
		this.gateway_ip = gateway_ip;
		this.gateway_sn = gateway_sn;
		this.gateway_mac = gateway_mac;
	}

	public String getGateway_name() {
		return gateway_name;
	}

	public void setGateway_name(String gateway_name) {
		this.gateway_name = gateway_name;
	}

	public String getGateway_account() {
		return gateway_account;
	}

	public void setGateway_account(String gateway_account) {
		this.gateway_account = gateway_account;
	}

	public String getGateway_ip() {
		return gateway_ip;
	}

	public void setGateway_ip(String gateway_ip) {
		this.gateway_ip = gateway_ip;
	}

	public String getGateway_sn() {
		return gateway_sn;
	}

	public void setGateway_sn(String gateway_sn) {
		this.gateway_sn = gateway_sn;
	}

	public String getGateway_mac() {
		return gateway_mac;
	}

	public void setGateway_mac(String gateway_mac) {
		this.gateway_mac = gateway_mac;
	}
}
