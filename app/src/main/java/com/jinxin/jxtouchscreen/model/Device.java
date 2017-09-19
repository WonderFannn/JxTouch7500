package com.jinxin.jxtouchscreen.model;

public class Device {

public String getAddress485() {
return address485;
}
public void setAddress485(String address485) {
this.address485 = address485;
}
public String getWhId() {
return whId;
}
public void setWhId(String whId) {
this.whId = whId;
}
public String getAlias() {
return alias;
}
public void setAlias(String alias) {
this.alias = alias;
}
public String getFunType() {
return funType;
}
public void setFunType(String funType) {
this.funType = funType;
}
public String getCode() {
return code;
}
public void setCode(String code) {
this.code = code;
}
public String getState() {
return state;
}
public void setState(String state) {
this.state = state;
}
public boolean isZg() {
return isZg;
}
public void setZg(boolean isZg) {
this.isZg = isZg;
}
public String getGatewayId() {
return gatewayId;
}
public void setGatewayId(String gatewayId) {
this.gatewayId = gatewayId;
}
String address485;//485地址
String whId;//序列号
String alias;//别名
String funType;//功能点
String code;//设备类型
String state;//设备状态
String gatewayId;//网关序列号
boolean isZg;//zigbee设备

@Override
public String toString() {
return "Device{" +
"address485='" + address485 + '\'' +
", whId='" + whId + '\'' +
", alias='" + alias + '\'' +
", funType='" + funType + '\'' +
", code='" + code + '\'' +
", state='" + state + '\'' +
", gatewayId='" + gatewayId + '\'' +
", isZg=" + isZg +
'}';
}
}
