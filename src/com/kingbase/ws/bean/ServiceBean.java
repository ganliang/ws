package com.kingbase.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务实体
 * @author ganliang
 */
public class ServiceBean {

	private String documentation;//wsdl简介
	
	private String targetNamespace;//目标命名空间
	
	private String endpointURI;//发布的wsdl地址
	
	private String serviceName;//服务名称
	
	private String wsdlType;//soap xsd
	
	//一个服务包含多个 port
	private List<BindingBean> bindingBean=new ArrayList<BindingBean>();

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getEndpointURI() {
		return endpointURI;
	}

	public void setEndpointURI(String endpointURI) {
		this.endpointURI = endpointURI;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<BindingBean> getBindingBean() {
		return bindingBean;
	}

	public void setBindingBean(List<BindingBean> bindingBean) {
		this.bindingBean = bindingBean;
	}

	public String getWsdlType() {
		return wsdlType;
	}

	public void setWsdlType(String wsdlType) {
		this.wsdlType = wsdlType;
	}

}
