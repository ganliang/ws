package com.kingbase.wsdl.bean;

import java.util.ArrayList;
import java.util.List;

public class BindingBean {

	private String bindingName;//
	private String bindingType;//
	
	private List<OperationBean> operationBeans=new ArrayList<OperationBean>();
	
	public String getBindingName() {
		return bindingName;
	}
	public void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}
	public String getBindingType() {
		return bindingType;
	}
	public void setBindingType(String bindingType) {
		this.bindingType = bindingType;
	}
	public List<OperationBean> getOperationBeans() {
		return operationBeans;
	}
	public void setOperationBeans(List<OperationBean> operationBeans) {
		this.operationBeans = operationBeans;
	}
	@Override
	public String toString() {
		return "BindingBean [bindingName=" + bindingName + ", bindingType=" + bindingType + ", operationBeans="
				+ operationBeans + "]";
	}
}
