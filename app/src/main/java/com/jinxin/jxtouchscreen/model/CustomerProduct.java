package com.jinxin.jxtouchscreen.model;



import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * 客户设备信息
 *
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(LocalConstant.CUSTOMER_PRODUCT)
public class CustomerProduct extends BaseModel {
	private String whId;
	private String code;
	private String proTime;
	private String typeNo;
	private int batch;
	private String version;
	private String qcReport;
	private String checker;
	private String checkTime;
	private String eqDesc;
	private String producer;
	private String recordTime;
	private String recorder;
	private String comments;
	private String mac;
	private String updateUser;
	private String updateTime;
	private String icon;
	private String typeName;
	private String address485;
	private String path = "";

	private boolean isLoading = false;//是否加载中

	public CustomerProduct() {

	}

	public CustomerProduct(String whId, String code, String proTime,
	                       String typeNo, int batch, String version, String qcReport,
	                       String checker, String checkTime, String eqDesc, String producer,
	                       String recordTime, String recorder, String comments, String mac,
	                       String updateUser, String updateTime, String icon, String typeName,
	                       String address485) {
		super();
		this.whId = whId;
		this.code = code;
		this.proTime = proTime;
		this.typeNo = typeNo;
		this.batch = batch;
		this.version = version;
		this.qcReport = qcReport;
		this.checker = checker;
		this.checkTime = checkTime;
		this.eqDesc = eqDesc;
		this.producer = producer;
		this.recordTime = recordTime;
		this.recorder = recorder;
		this.comments = comments;
		this.mac = mac;
		this.updateUser = updateUser;
		this.updateTime = updateTime;
		this.icon = icon;
		this.typeName = typeName;
		this.address485 = address485;
		this.path = "";
	}

	public String getWhId() {
		return whId;
	}

	public void setWhId(String whId) {
		this.whId = whId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProTime() {
		return proTime;
	}

	public void setProTime(String proTime) {
		this.proTime = proTime;
	}

	public String getTypeNo() {
		return typeNo;
	}

	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getQcReport() {
		return qcReport;
	}

	public void setQcReport(String qcReport) {
		this.qcReport = qcReport;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getEqDesc() {
		return eqDesc;
	}

	public void setEqDesc(String eqDesc) {
		this.eqDesc = eqDesc;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getRecorder() {
		return recorder;
	}

	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getAddress485() {
		return address485;
	}

	public void setAddress485(String address485) {
		this.address485 = address485;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}


}
