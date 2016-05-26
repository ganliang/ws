package com.kingbase.wsdl.utils;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

public class XMLUtil {

	/**
	 * 打印一个字符串的xml
	 * @param xml
	 * @throws DocumentException
	 */
	public static String printXML(String xml) throws DocumentException{
		Document document = DocumentHelper.parseText(xml);
		//添加根几点
		Element rootElement = document.getRootElement();
		StringBuilder builder=new StringBuilder();
		
		recursionPrintXML(rootElement,builder,1);
		return builder.toString();
	}
	
	/**
	 * 递归打印xml
	 * @param element 元素节点
	 * @param builder
	 * @param deep 节点深度
	 */
	@SuppressWarnings("unchecked")
	private static void recursionPrintXML(Element element,StringBuilder builder,int deep) {
		//获取空格数
		StringBuilder deepBuilder=new StringBuilder();
		for (int i = 1; i < deep; i++) {
			deepBuilder.append("&nbsp");
		}
		
		boolean textOnly = element.isTextOnly();
		if(textOnly){
			builder.append(deepBuilder.toString()+"<"+element.getQualifiedName()+">"+element.getText()+"</"+element.getQualifiedName()+">\r\n");
		}else{
			builder.append(deepBuilder.toString()+"<"+element.getQualifiedName());
			//命名空间
			
			Namespace namespace = element.getNamespace();
			if(namespace!=null){
				String prefix = namespace.getPrefix();
				if(prefix==null||"".equals(prefix)){
					builder.append(" xmlns=\""+namespace.getStringValue()+"\"");
				}else{
					builder.append(" xmlns:"+namespace.getPrefix()+"=\""+namespace.getStringValue()+"\"");
				}
			}
			builder.append(">\r\n");
			
			List<Element> elements = element.elements();
			for (Element ele : elements) {
				recursionPrintXML(ele, builder,deep+1);
			}
			builder.append(deepBuilder.toString()+"</"+element.getQualifiedName()+">\r\n");
		}
	}
}
