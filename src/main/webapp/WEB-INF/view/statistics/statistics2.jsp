<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="<c:url value="/js/jquery-3.2.1.min.js" />"></script>
<script src="https://d3js.org/d3.v3.min.js"></script>
<script src="<c:url value="/c3/c3.min.js" />"></script>
<link rel="stylesheet" href="<c:url value="/c3/c3.min.css"/>" />
<script>
$().ready(function() {
	var chart = c3.generate({
		binto: '#chart',
		data: {
			columns: [${data}]
		},
		axis: {
			x: {
				type: 'category',
				categories: ${category}
			}
		}
	});
})
</script>
</head>
<body>
	<div>
		<div id="chart">
		</div>
	</div>
	<hr/>
	<div>
		<table>
			<tr>
				<th>DateTime</th>
				<th>User Name</th>
				<th>Request Count</th>
			</tr>
			<c:forEach items="${statisticsList }" var="st">
			<tr>
				<td>${st.dateTime}</td>
				<td>${st.userName}</td>
				<td>${st.count}</td>
			</tr>
			</c:forEach>
			
		</table>
	</div>
</body>
</html>