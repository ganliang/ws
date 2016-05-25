<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>WSDL</title>
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
</style>
<script type="text/javascript">
	var submitForm;
	var closeForm;
	var serverName;//服务名称
	var methodName;//方法名称
	$(document).ready(function() {
		$('#win').window('close');
		$('#methodCallWindow').window('close');
		//获取wsdl解析树
		$('#wsdlTree').tree({
			url : './WsdlLoadServlet?type=load',
			method : 'POST',
			lines : true,
			loadFilter : function(data, parent) {
				return data;
			},
			onClick : function(node) {
				if (node.children) {//如果服务节点 显示整个服务的详情
					serverName = node.text;
				} else {//如果方法节点  显示这个方法的详情
					methodName = node.text;
					serverName = node.parentName;
				}
				//加载详细信息
				/* $.getJSON('./WsdlLoadServlet?type=load', function(json) {
					//$("#content").html("<div style='float:left'><b>"+JSON.stringify(json)+"</b></div>");
				}); */
			},
			onDblClick : function(node) {//双击方法节点   显示执行
				$('#methodCallWindow').window('open');
			    //获取参数xml数据
			    $.ajax({
			    	type: "POST", 
			    	url:'./WsdlLoadServlet?type=getParameterDATA&serverName='+serverName+'&methodName='+methodName,
			    	dataType: "text",
			    	success:function(data){
						$("#parameterPanel").html(data);
						$("#parameterPanel").val($("#parameterPanel").text());
						$("#resultPanel").val("");
			    	}
			    });
			}
		});
        
		//点击soap按钮
		$('#soapButton').bind('click', function() {
			$('#win').window('open');
		});

		//点击导入按钮
		$('#importButton').bind('click', function() {
			alert('importButton');
		});

		//提交表单 
		submitForm = function() {
			$('#soapForm').form('submit', {
				url : './WsdlLoadServlet?type=soapSubmit',
				onSubmit : function() {
				},
				success : function(data) {
					if(data==null||data==''){
						alert('url解析失败');
					}else{
						data = eval("(" + data + ")");
						closeForm();
						$('#wsdlTree').tree({
							data : data
						});
					}
				},
			});
		}
		//点击运行 
		$("#runComand").bind("click",function(){
			var value=$("#parameterPanel").val();
			/* value=value.replace("\r\n","");
			value=value.replace("\r","");
			value=value.replace("\n","");
			value=value.replace("&nbsp",""); */
			$.ajax({
			    	type: "POST", 
			    	data:{parameterXML:value},
			    	url:'./WsdlLoadServlet?type=getResultDATA&serverName='+serverName+'&methodName='+methodName,
			    	dataType: "text",
			    	success:function(data){
						$("#resultPanel").html(data);
						$("#resultPanel").val($("#resultPanel").text());
			    	},
			    	error:function(XMLHttpRequest,textStatus,errorThrown){
			    		$("#resultPanel").html(textStatus+"\r\n"+errorThrown);
			    		$("#resultPanel").val($("#resultPanel").text());
			    	}
			});
		});

		//关闭soapWindow弹框
		closeForm = function() {
			$('#soapForm').form("reset");
			$('#win').window('close');
		}
	});
</script>
</head>
<body>
	<div style="padding: 5px 5px 5px 5px;">
		<a id="soapButton" class="easyui-linkbutton"
			data-options="iconCls:'icon-add'">SOAP</a> <a id="importButton"class="easyui-linkbutton"
			data-options="iconCls:'icon-remove'" style="margin-left: 10px">导入</a>
	</div>

	<div class="easyui-layout" style="width: 100%; height: 900px">
		<div region="west" title="WSDL解析" style="width: 20%;">
			<ul id="wsdlTree" class="easyui-tree"></ul>
		</div>
		<div id="content" region="center" title="" style="padding: 0px;">
			<div id="methodCallWindow" class="easyui-window" title="方法调用"
				style="width: 65%; height:596px">
				<div class="easyui-layout" style="width: 100%; height: 560px;padding:0px;margin:0px">
				    <div region="north" style="text-align: left;height: 30px">
						<a id="runComand" class="easyui-linkbutton">运行</a> 
					</div>
					<div class="easyui-panel" region="center" style="width:42%;">
					 	<textarea id="parameterPanel" rows="25" cols="51"></textarea>
				    </div>
					<div class="easyui-panel" region="east" style="width:58%;height: 100px">
					    <textarea id="resultPanel" rows="25" cols="73"></textarea>
					</div>
			    </div>
			</div>
		</div>
	</div>

	<div id="win" class="easyui-window" title="New SOAP Project"
		style="width: 500px; height: 250px; padding: 0px"
		data-options="iconCls:'icon-save',modal:true">
		<form id="soapForm" method="post" style="padding: 10px">
			<div style="margin-top: 20px">
				<label for="" style="margin: 20px">New SOAP Project:</label>
			</div>
			<div style="margin-top: 20px">
				<label for="name" style="margin: 20px">Project Name:</label> <input
					class="easyui-validatebox" style="width: 300px" type="text"
					name="projectName" data-options="" />
			</div>
			<div style="margin-top: 20px">
				<label for="email" style="margin: 20px">Initial WSDL:</label> <input
					class="easyui-validatebox" style="margin-left: 9px; width: 300px"
					type="text" name="wsdlUri" data-options="" />
			</div>
		</form>
		<div style="text-align: center; padding: 5px; margin-top: 20px">
			<a href="javascript:void(0)" class="easyui-linkbutton"
				onclick="submitForm()">Submit</a> <a href="javascript:void(0)"
				class="easyui-linkbutton" onclick="closeForm()">Close</a>
		</div>
	</div>
</body>
</html>