package com.kingbase.wsdl.bean;

/**
 * 参数
 * @author ganliang
 *
 */
public class ParameterBean {

	private String parameterName;// 参数名
	private String minOccurs;// 
	private String maxOccurs;// 
	
	private String nillable;// 
	private String parameterType;// 参数类型

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}

	public String getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getNillable() {
		return nillable;
	}

	public void setNillable(String nillable) {
		this.nillable = nillable;
	}

	@Override
	public String toString() {
		return "ParameterBean [parameterName=" + parameterName + ", minOccurs=" + minOccurs + ", maxOccurs=" + maxOccurs
				+ ", nillable=" + nillable + ", parameterType=" + parameterType + "]";
	}
}
