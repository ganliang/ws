package com.kingbase.services.complex;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name="ComplexModelService")
public class ComplexModelService {

	public String get(@WebParam(name="model") ComplexModel model,@WebParam(name="name") String name){
		
		return model.toString()+name;
	}
	
	public String update(@WebParam(name="name") String name,@WebParam(name="password")String password){
		return name+"  "+password;
	}
}
