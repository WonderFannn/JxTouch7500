package com.jinxin.jxtouchscreen.cmd;
/**
 * 返回服务器联网结果状态
 * 返回数据格式类似为：{"ret":1;"msg":"ok"}
 *      ret = 1 表示成功，其它值为失败，msg 包含失败原因。
 * @author zj
 *
 */
public class RemoteJsonResultInfo {
    public RemoteJsonResultInfo(){
    }
    public String validResultCode;
    public String validResultInfo;
    
    @Override
    public String toString() {
    	return "validResultCode:" + validResultCode + "," + "validResultInfo:" + validResultInfo;
    }
}
