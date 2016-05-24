package com.kingbase.ws.bean;

import java.util.ArrayList;
import java.util.List;

public class BindingBean {

	private String name;//
	private String type;//
	
	//一个binding绑定了多个方法
	private List<OperationBean> operations=new ArrayList<OperationBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<OperationBean> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationBean> operations) {
		this.operations = operations;
	}

	@Override
	public String toString() {
		return "BindingBean [name=" + name + ", type=" + type + "]";
	}
	
}
