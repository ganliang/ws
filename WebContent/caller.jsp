<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>webService方法调用</title>
<link rel="stylesheet" type="text/css"
	href="./themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="./themes/icon.css">
<script type="text/javascript" src="./js/jquery.min.js"></script>
<script type="text/javascript" src="./js/jquery.easyui.min.js"></script>
<style type="text/css">
body {
	height: 100%;
	width: 100%;
	margin: 0px;
	padding: 0px;
	overflow: hidden;
}
#methodCallDiv {
	height:100%; 
	position:absolute; 
	width:100%;
} 
</style>
<script type="text/javascript">
//var wsdlLocation="http://192.168.8.144:9999/services/helloWord?wsdl";
//var methodName="sayHi";
//localhost:8080/wsdl/caller.jsp?wsdlLocation=http://192.168.8.144:9999/services/helloWord?wsdl&methodName=sayHi
var wsdlLocation='${param.wsdlLocation}';
var methodName='${param.methodName}';
$(document).ready(function() {
	//获取参数
	$.ajax({
    	type: "POST", 
    	url:'./ServiceCallServlet?type=getParameter&wsdlLocation='+wsdlLocation+'&methodName='+methodName,
    	dataType: "text",
    	success:function(data){
			$("#parameterTextArea").html(data);
			$("#parameterTextArea").val($("#parameterTextArea").text());
			$("#resultTextArea").val("");
    	}
    });
	
	//点击运行 
	$("#runComand").bind("click",function(){
		var parameterXML=$("#parameterTextArea").val();
		$.ajax({
		    	type: "POST", 
		    	url:'./ServiceCallServlet?type=methodCall&wsdlLocation='+wsdlLocation+'&methodName='+methodName+'&parameterXML='+parameterXML,
		    	dataType: "text",
		    	success:function(data){
					$("#resultTextArea").html(data);
					$("#resultTextArea").val($("#resultTextArea").text());
		    	},
		    	error:function(XMLHttpRequest,textStatus,errorThrown){
		    		$("#resultTextArea").html(textStatus+"\r\n"+errorThrown);
		    		$("#resultTextArea").val($("#resultTextArea").text());
		    	}
		});
	});
});
</script>
</head>
<body>
	<div id="methodCallDiv" class="easyui-layout">
		<div region="north" style="text-align: left; height: 30px">
			<a id="runComand" class="easyui-linkbutton">测试</a>
		</div>
		<div class="easyui-panel" region="center" style="width: 40%;">
			<textarea id="parameterTextArea" style="width: 99%;height: 99%"></textarea>
		</div>
		<div class="easyui-panel" region="east"
			style="width: 60%; height: 100px;padding:0px;margin:0px">
			<textarea id="resultTextArea" style="width: 99%;height: 99%"></textarea>
		</div>
	</div>
</body>
</html>