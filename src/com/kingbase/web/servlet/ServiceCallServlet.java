package com.kingbase.web.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

import com.kingbase.ws.bean.ServiceBean;
import com.kingbase.ws.caller.AxisCaller;
import com.kingbase.ws.parser.SOAPParser;
import com.kingbase.ws.utils.ParameterUtil;
import com.kingbase.ws.utils.XMLUtil;

@WebServlet(urlPatterns={"/ServiceCallServlet"})
public class ServiceCallServlet extends HttpServlet{

	private static final long serialVersionUID = -994122804849307179L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		String type=request.getParameter("type");
		String wsdllocation=request.getParameter("wsdlLocation");//wsdl url
		String methodName=request.getParameter("methodName"); //方法名称
		String json="";
		//获取方法的参数
		if("getParameter".equals(type)){
			SOAPParser soapParser=new SOAPParser();
			try {
				//解析wsdl
				ServiceBean serviceBean = soapParser.parse(wsdllocation);
				//获取放大的输入参数map集合
				Map<String, Object> parameterMap = ParameterUtil.getInParameter(serviceBean, methodName);
				//获取 参数xml
				OMElement parameterElement = XMLUtil.createParameterElement(serviceBean.getTargetNamespace(), serviceBean.getWsdlType(), methodName, parameterMap);
				//编码xml 添加空格换行
				json = XMLUtil.encodeParameterXML(parameterElement.toString());
			} catch (AxisFault e) {
				json="{\"success\":false,msg:\""+e.getLocalizedMessage()+"\"}";
			}
		}
		//方法调用
		else if("methodCall".equals(type)){
			String parameterXML=request.getParameter("parameterXML");
			SOAPParser soapParser=new SOAPParser();
			AxisCaller axisCaller=new AxisCaller();
			try {
				//解析wsdl
				ServiceBean serviceBean = soapParser.parse(wsdllocation);
				//获取参数
				Map<String, Object> parameterMap = ParameterUtil.getInParameter(parameterXML);
				
				//调用 返回结果
				json = axisCaller.caller(wsdllocation, serviceBean.getTargetNamespace(), serviceBean.getWsdlType(), methodName, parameterMap);
				
				//将xml添加空格 换行
				json=XMLUtil.encodeResultXML(json);
			} catch (AxisFault e) {
				json="{\"success\":false,msg:\""+e.getLocalizedMessage()+"\"}";
			}catch(Exception e){
				json="{\"success\":false,msg:\""+e.getLocalizedMessage()+"\"}";
			}
		}
		response.getWriter().print(json);
	}
}
