package com.kingbase.wsdl.caller;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;

import com.kingbase.wsdl.utils.XMLUtil;

/**
 * 使用http方式调用webservice
 * @author ganliang
 *
 */
public class HttpCaller {

	private static final String SOAP_BODY_PREFIX="<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header></soapenv:Header><soapenv:Body>";
	
	private static final String SOAP_BODY_SUFFEX="</soapenv:Body></soapenv:Envelope>";
	
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
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws DocumentException 
	 */
	public String printResponse(CloseableHttpResponse response) throws ParseException, IOException, DocumentException{
		String body = EntityUtils.toString(response.getEntity());
		Header[] headers = response.getHeaders("Content-Type");
		if(headers==null||headers.length==0){
			throw new IllegalArgumentException("服务返回数据格式不明确");
		}
		String contentType = headers[0].getValue();
		if(contentType.contains("text/xml")){
			contentType="text/xml";
			//添加空格 换行
			body=XMLUtil.printXML(body);
		}else {
			//
		}
		return contentType+":"+body;
	}
	
	/**
	 * 方法调用
	 * @param wsdlURL
	 * @param targetNamespace
	 * @param wsdlType
	 * @param operationName
	 * @param value
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 * @throws DocumentException
	 */
	public String caller(String wsdlURL, String targetNamespace, String wsdlType, String operationName, String value) throws ClientProtocolException, IOException, ParseException, DocumentException {
		
		//获取http连接
		HttpPost httpPost = getHttpConnection(wsdlURL, targetNamespace+operationName);
		
		//调用
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpEntity entity=new StringEntity(SOAP_BODY_PREFIX+value+SOAP_BODY_SUFFEX,"UTF-8");
		httpPost.setEntity(entity);
		
		CloseableHttpResponse response = httpClient.execute(httpPost);
		
		//打印返回体
		String body=printResponse(response);
		
		return body;		
	}
	
	public static void main(String[] args) {
	  
		HttpCaller caller=new HttpCaller();
		try {
			String cal = caller.caller("http://192.168.1.36:8080/default/orgbizService?wsdl", "http://www.primeton.com/orgbizService","soap", "addOrg", "");
	        System.out.println(cal);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
}
