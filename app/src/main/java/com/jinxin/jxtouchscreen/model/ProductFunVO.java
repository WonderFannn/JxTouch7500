package com.jinxin.jxtouchscreen.model;

import java.io.Serializable;
import java.util.List;

/**
 * 添加模式列表显示项
 * @author yh
 * @company 金鑫智慧
 */
public class ProductFunVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5703243756339212320L;
	private boolean isSelected = false;
	private boolean isOpen = false;
	private ProductFun productFun = null;
	private ProductPatternOperation productPatternOperation = null;
	private List<ProductPatternOperation> productPatternOperations = null;
	public ProductFunVO(){
		this(false,false,null,null);
	}
	public ProductFunVO(ProductFun productFun){
		this(false,false,productFun,null);
	}
	public ProductFunVO(boolean isSelected, boolean isOpen,
			ProductFun productFun,
			ProductPatternOperation productPatternOperation) {
		super();
		this.isSelected = isSelected;
		this.isOpen = isOpen;
		this.productFun = productFun;
		this.productPatternOperation = productPatternOperation;
	}
	
	public ProductFunVO(boolean isSelected, boolean isOpen,
			ProductFun productFun,
			ProductPatternOperation productPatternOperation,
			List<ProductPatternOperation> productPatternOperations) {
		super();
		this.isSelected = isSelected;
		this.isOpen = isOpen;
		this.productFun = productFun;
		this.productPatternOperation = productPatternOperation;
	}
	
	public List<ProductPatternOperation> getProductPatternOperations() {
		return productPatternOperations;
	}
	public void setProductPatternOperations(List<ProductPatternOperation> productPatternOperations) {
		this.productPatternOperations = productPatternOperations;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public ProductFun getProductFun() {
		return productFun;
	}
	public void setProductFun(ProductFun productFun) {
		this.productFun = productFun;
	}
	public ProductPatternOperation getProductPatternOperation() {
		return productPatternOperation;
	}
	public void setProductPatternOperation(
			ProductPatternOperation productPatternOperation) {
		this.productPatternOperation = productPatternOperation;
	}
	
	@Override
	public String toString() {
		return "ProductFunVO [isSelected=" + isSelected + ", isOpen=" + isOpen + ", productFun=" + productFun
				+ ", productPatternOperation=" + productPatternOperation + ", productPatternOperations="
				+ productPatternOperations + "]";
	}
	
}
