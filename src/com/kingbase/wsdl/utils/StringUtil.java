package com.kingbase.wsdl.utils;

public class StringUtil {

	
	public static void main(String[] args) {
		String str="> <_field>0</_field> <_count>0</_count> <_avg>0</_avg> <_sum>0</_sum> <";
		str = str.replaceAll("> +", ">");
		System.out.println(str);
	}
}
