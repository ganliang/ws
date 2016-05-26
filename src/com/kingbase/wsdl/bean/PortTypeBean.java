package com.kingbase.wsdl.bean;

import java.util.ArrayList;
import java.util.List;

public class PortTypeBean {

	private String portName;//
	
	List<OperationBean> operationBeans=new ArrayList<OperationBean>();

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public List<OperationBean> getOperationBeans() {
		return operationBeans;
	}

	public void setOperationBeans(List<OperationBean> operationBeans) {
		this.operationBeans = operationBeans;
	}

	@Override
	public String toString() {
		return "PortTypeBean [portName=" + portName + ", operationBeans=" + operationBeans + "]";
	}
	
	
}
