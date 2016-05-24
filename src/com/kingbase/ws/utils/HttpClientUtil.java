package com.kingbase.ws.utils;

import java.io.InputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

/**
 * wsdl为网络上发布的服务 可以缓存wsdl
 * @author ganliang
 *
 */
public class HttpClientUtil {

	private static final Logger log = Logger.getLogger(HttpClientUtil.class);
    
    //发送httpClient请求
	public static InputStream send(String uri) {
   		InputStream stream=null;
		try {
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();

			HttpGet http = new HttpGet(uri);
			CloseableHttpResponse response = httpClient.execute(http);
			stream=response.getEntity().getContent();
		}catch (Exception e) {
			log.error("连接wsdl【"+uri+"】失败", e);
			e.printStackTrace();
		}
		return stream;
	}
	
}
