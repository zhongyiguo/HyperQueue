<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<!-- Import jQuery -->
<script type="text/javascript"
	src="<spring:url value='/resources/jquery-1.9.1/jquery-1.9.1.js' />"></script>

<title>Aggregator</title>
</head>
<body>
	<input name="sid" value="<%=request.getAttribute("sid")%>"
		type="hidden">
	<table>
		<tr>
			<td>Consume Topic:</td>
			<td><input id="topic" type="text"
				value=<%=request.getAttribute("t")%> /></td>
			<td>Message:</td>
			<td><input type="text" id="m" value=<%=request.getAttribute("m")%> maxlength="50"
				style="width: 217px; margin-left: 2px;" /></td>
			<td><input type="button" value="Consume message"
				onclick='consume(document.getElementById("topic").value);' /></td>
		</tr>
		<tr>
			<td>Post Topic:</td>
			<td><input id="posttopic" type="text"
				value=<%=request.getAttribute("t")%> /></td>
			<td>Message:</td>
			<td><input type="text" id="pm" maxlength="50"
				style="width: 217px; margin-left: 2px;" /></td>
			<td><input type="button" value="Post message"
				onclick='postmessage(document.getElementById("posttopic").value,document.getElementById("pm").value);' /></td>
		</tr>
	</table>
</body>
</html>

<script type="text/javascript">
	function consume(topicname) {
		$.ajax({
			type : "GET",
			url : "<spring:url value='/' />" + topicname,
			dataType : "json",
			success : function(data, status, xhr) {
				if (data.key == "success") {
					var obj = jQuery.parseJSON(data.value[0]);
					document.getElementById("m").value = data.value[0];
				} else {
					alert(data.value);
				}
			},
			fail : function(data, status, xhr) {
				alert("FAILURE " + data);
			}
		});
	};

	function postmessage(topicname,message) {
		$.ajax({
			type : "POST",
			url : "<spring:url value='/' />"+topicname+"?message="+message,
			dataType : "json",
			success : function(data, status, xhr) {
				if (data.key == "success") {
					//TODO: status check
				} else {
					//TODO: status check
					//alert(data.value);
				}
			},
			fail : function(data, status, xhr) {
				alert("FAILURE " + data);
			}
		});
	};
</script>
