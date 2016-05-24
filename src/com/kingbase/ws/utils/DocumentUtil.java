package com.kingbase.ws.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class DocumentUtil {

	private static final Logger log = Logger.getLogger(DocumentUtil.class);
	/**
	 * 获取文档
	 * @param InputStream
	 * @return
	 * @throws WSDLException
	 */
	public static Document getDocument(InputStream inputStream){
		SAXReader reader=new SAXReader();
		Document document=null;
		try {
			document = reader.read(inputStream);
		} catch (DocumentException e) {
			log.error("解析文档出现异常");
		}finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}
}
