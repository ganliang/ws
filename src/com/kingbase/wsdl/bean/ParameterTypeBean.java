package com.kingbase.wsdl.bean;

import java.util.ArrayList;
import java.util.List;

public class ParameterTypeBean {

	private String typeName;//对象名称
	private String instanceClass;//
	private List<BasicTypeBean> BasicTypeBeans=new ArrayList<BasicTypeBean>();
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getInstanceClass() {
		return instanceClass;
	}

	public void setInstanceClass(String instanceClass) {
		this.instanceClass = instanceClass;
	}

	public List<BasicTypeBean> getBasicTypeBeans() {
		return BasicTypeBeans;
	}

	public void setBasicTypeBeans(List<BasicTypeBean> basicTypeBeans) {
		BasicTypeBeans = basicTypeBeans;
	}

	
	@Override
	public String toString() {
		return "ParameterTypeBean [typeName=" + typeName + ", instanceClass=" + instanceClass + ", BasicTypeBeans="
				+ BasicTypeBeans + "]";
	}

	public static class BasicTypeBean{
		private String basicTypeName;// 参数名
		
		private String minOccurs;// 
		private String maxOccurs;// 
		private String nillable;// 
		
		private String basicType;// 参数类型

		public String getBasicTypeName() {
			return basicTypeName;
		}

		public void setBasicTypeName(String basicTypeName) {
			this.basicTypeName = basicTypeName;
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

		public String getNillable() {
			return nillable;
		}

		public void setNillable(String nillable) {
			this.nillable = nillable;
		}

		public String getBasicType() {
			return basicType;
		}

		public void setBasicType(String basicType) {
			this.basicType = basicType;
		}

		@Override
		public String toString() {
			return "BasicTypeBean [basicTypeName=" + basicTypeName + ", minOccurs=" + minOccurs + ", maxOccurs="
					+ maxOccurs + ", nillable=" + nillable + ", basicType=" + basicType + "]";
		}
	}
}
