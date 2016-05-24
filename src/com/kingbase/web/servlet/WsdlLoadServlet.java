package com.kingbase.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingbase.ws.bean.ServiceBean;
import com.kingbase.ws.caller.AxisCaller;
import com.kingbase.ws.parser.SOAPParser;
import com.kingbase.ws.utils.ParameterUtil;
import com.kingbase.ws.utils.XMLUtil;

@WebServlet(urlPatterns={"/WsdlLoadServlet"})
public class WsdlLoadServlet extends HttpServlet{

	private static final Logger log=Logger.getLogger(WsdlLoadServlet.class);
	private static final long serialVersionUID = -3198990186345076733L;

	private static final ConcurrentHashMap<String, ServiceBean> serviceBeans=new ConcurrentHashMap<String, ServiceBean>();
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		String type=request.getParameter("type");
		String json="";
	    if(type==null){
	    	throw new IllegalArgumentException();
	    }
	    switch (type) {
	    //加载解析过的 wsdl
		case "load":
			json=load();
			break;
		//提交一个 soap
		case "soapSubmit":
			String wsdlUri=request.getParameter("wsdlUri");
			try {
				ServiceBean serviceBean = serviceBeans.get(wsdlUri);
				if(serviceBean==null){
					SOAPParser soapParser=new SOAPParser();
					serviceBean = soapParser.parse(wsdlUri);
					serviceBeans.put(wsdlUri, serviceBean);
				}
				json=load();
			}catch (Exception e) {
				log.error("提交wsdl【"+wsdlUri+"】出现异常", e);
			}
			break;
		//获取参数
		case "getParameterDATA":
			json=getParameterDATA(request);
			json=XMLUtil.encodeParameterXML(json);
			break;
		//调用方法 返回结果
		case "getResultDATA":
			try {
				json=getResultDATA(request);
				json=XMLUtil.encodeResultXML(json);
			} catch (DocumentException e) {
				log.error("调用异常", e);
				json=e.getLocalizedMessage();
			}
			break;
		default:
			break;
		}
	    response.getWriter().print(json);
	}

	/**
	 * 加载数据
	 * @return
	 */
	private String load() {
		String json="";
		if(serviceBeans.size()==0){
			json="[{\"text\":\"Projects\"}]";
		}else{
			String printServices = ParameterUtil.printServices(new ArrayList<ServiceBean>(serviceBeans.values()));
			json="[{\"text\":\"Projects\",\"children\":"+printServices+"}]";
		}
		return json;
	}
	
	/**
	 * 获取参数
	 * @param request 
	 */
	private String getParameterDATA(HttpServletRequest request) {
		String serverName=request.getParameter("serverName");
		String methodName=request.getParameter("methodName");
		
		ServiceBean serviceBean = getServiceBean(serverName);
		if(serviceBean==null){
			throw new IllegalArgumentException();
		}
		
		Map<String, Object> parameterMap = ParameterUtil.getInParameter(serviceBean, methodName);
		
		OMElement omElement = XMLUtil.createParameterElement(serviceBean.getTargetNamespace(),serviceBean.getWsdlType(), methodName, parameterMap);
	    return omElement.toString();
	}
	
	/**
	 * 获取方法调用的结果
	 * @param request
	 * @return
	 * @throws AxisFault 
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	private String getResultDATA(HttpServletRequest request) throws AxisFault, DocumentException {
		String serverName=request.getParameter("serverName");
		String methodName=request.getParameter("methodName");
		String value=request.getParameter("parameterXML");
		String json=null;
		ServiceBean serviceBean = getServiceBean(serverName);
		if(serviceBean==null){
			throw new IllegalArgumentException();
		}
		
		Document document = DocumentHelper.parseText(value);
		Element rootElement = document.getRootElement();
		List<Element> elements = rootElement.elements();
		
		Map<String,Object> parameterMap=new HashMap<String,Object>();
		for (Element element : elements) {
			parameterMap.put(element.getName(), element.getTextTrim());
		}
		//调用
		AxisCaller caller=new AxisCaller();
		json = caller.caller(serviceBean.getEndpointURI(), serviceBean.getTargetNamespace(),serviceBean.getWsdlType(),methodName, parameterMap);	
		return json;
	}
	
	/**
	 * 获取serviceBean
	 * @param serverName
	 * @return
	 */
	private static ServiceBean getServiceBean(String serverName){
		Iterator<Entry<String, ServiceBean>> iterator = serviceBeans.entrySet().iterator();
		ServiceBean serviceBean=null;
		while(iterator.hasNext()){
			Entry<String, ServiceBean> entry = iterator.next();
			ServiceBean bean = entry.getValue();
			if(bean.getServiceName().equals(serverName)){
				serviceBean=bean;
				break;
			}
		}
		return serviceBean;
	}
}
