package com.kingbase.ws.caller;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * 使用http方式调用webservice
 * @author ganliang
 *
 */
public class HttpCaller {

	private static final String SOAP_BODY_PREFIX="<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header></soapenv:Header><soapenv:Body>";
	
	private static final String SOAP_BODY_SUFFEX="</soapenv:Body></soapenv:Envelope>";
	
	/**
	 * 组装soap请求体
	 * @param targetNamespace 命名空间
	 * @param operationName 方法名称
	 * @param parameterMap 参数map
	 * @param wsdlType 
	 * @return
	 */
	public String buildBody(String targetNamespace,String operationName,Map<String,String> parameterMap, String wsdlType){
		StringBuilder soapBody=new StringBuilder(SOAP_BODY_PREFIX);
		
		String xmlns="xmlns";
		if("xsd".equalsIgnoreCase(wsdlType)){
			operationName="xsd:"+operationName;
			xmlns="xmlns:xsd";
		}
		
		soapBody.append("<"+operationName+" "+xmlns+"=\""+targetNamespace+"\">");
		
		Set<Entry<String,String>> entrySet = parameterMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			soapBody.append("<"+entry.getKey()+">"+entry.getValue()+"</"+entry.getKey()+">");
		}
		
		soapBody.append("</"+operationName+">");
		soapBody.append(SOAP_BODY_SUFFEX);
		return soapBody.toString();
	}
	
	/**
	 * 获取http连接  并且设置SOAPAction Content-Type等http头
	 * @param wsdlURL
	 * @param SOAPAction
	 * @return
	 */
	public HttpPost getHttpConnection(String wsdlURL,String SOAPAction){
		HttpPost postMethod = new HttpPost(wsdlURL);
		postMethod.addHeader("Content-Type", "text/xml; charset=UTF-8");
		postMethod.addHeader("SOAPAction", SOAPAction);
		
		return postMethod;
	}
	
	/**
	 * 获取返回的Content-Type
	 * @param response
	 * @return
	 */
	public String getContentType(CloseableHttpResponse response){
		Header[] headers = response.getHeaders("Content-Type");
		if(headers==null||headers.length==0){
			throw new IllegalArgumentException("服务区返回数据格式不明确");
		}
		String contentType = headers[0].getValue();
		if(contentType.contains("text/xml")){
			contentType="text/xml";
		}else {
			//image/Gif
			//
		}
		return contentType;
	}
	
	public String caller(String wsdlURL,String targetNamespace,String operationName,String wsdlType,Map<String,String> parameterMap) throws IOException{
		//构建soap体
		String soapBody = buildBody(targetNamespace, operationName, parameterMap,wsdlType);
		
		//获取http连接
		HttpPost httpPost = getHttpConnection(wsdlURL, targetNamespace+operationName);
		
		//调用
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpEntity entity=new StringEntity(soapBody,"UTF-8");
		httpPost.setEntity(entity);
		
		CloseableHttpResponse response = httpClient.execute(httpPost);
		String body = EntityUtils.toString(response.getEntity());
		String contentType = getContentType(response);
		System.out.println(contentType);
		return body;
	}
	
}
