package com.kingbase.wsdl.utils;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * dom4j元素节点工具类
 * @author Administrator
 *
 */
public class ElementUtil {

	/**
	 * 查询子节点
	 * @param rootElement 
	 * @param name
	 * @return
	 */
	public static List<Element> findElements(Element element,String name){
		List<Element> list=new ArrayList<Element>();
		
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();
		for (Element ele : elements) {
			if(ele.getName().contains(name)){
				list.add(ele);
			}
		}
		return list;
	}

	/**
	 * 获取文档的根节点
	 * @param document
	 * @return
	 */
	public static Element getRootElement(Document document){
		if(document==null){
			throw new IllegalArgumentException("根节点不能为空");
		}
		return document.getRootElement();
	}
}
