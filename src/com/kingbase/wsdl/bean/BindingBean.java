package com.kingbase.wsdl.bean;

import java.util.ArrayList;
import java.util.List;

public class BindingBean {

	private String bindingName;//
	private String bindingType;//
	
	List<OperationBean> operationBeans=new ArrayList<OperationBean>();
	
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
}