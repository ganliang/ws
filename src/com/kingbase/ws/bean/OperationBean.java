package com.kingbase.ws.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法
 * @author ganliang
 *
 */
public class OperationBean {

	private String name;//方法的名称
	
	private String documentation;//方法的简介
	
	private String style;//样式
	
	private String soapAction;//
	
	//输入参数
	private List<ParameterBean> inParameters=new ArrayList<ParameterBean>();
	
	//输出参数
	private List<ParameterBean> outParameters=new ArrayList<ParameterBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public List<ParameterBean> getInParameters() {
		return inParameters;
	}

	public void setInParameters(List<ParameterBean> inParameters) {
		this.inParameters = inParameters;
	}

	public List<ParameterBean> getOutParameters() {
		return outParameters;
	}

	public void setOutParameters(List<ParameterBean> outParameters) {
		this.outParameters = outParameters;
	}

	@Override
	public String toString() {
		return "OperationBean [name=" + name + ", documentation=" + documentation + ", style=" + style + ", soapAction="
				+ soapAction + ", inParameters=" + inParameters + ", outParameters=" + outParameters + "]";
	}
}
