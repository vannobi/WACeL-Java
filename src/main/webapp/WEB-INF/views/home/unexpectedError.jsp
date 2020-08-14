<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Unexpected Error</title>
	<!-- Before Bootstrap JS --> 
	<link href="<c:url value='/assets/css/bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
	<script src="<c:url value='/assets/js/jquery/jquery.min.js' />" ></script>
	<script src="<c:url value='/assets/js/popper/popper.js' />" ></script>
	<script src="<c:url value='/assets/js/bootstrap/js/bootstrap.min.js' />" ></script>
</head>

<body>
	<%@include file="../home/authHeader.jsp" %>	
	<!-- Messages -->
	 <%@include file="../home/messageFragment.jsp" %>
	<div class="container-fluid">
      	<!-- Grid Content -->
		<div class="row flex-xl-nowrap">
			<!-- Left Bar -->
			<%@include file="../home/leftSideBar.jsp"%>

			<!-- Main Content -->
			<div class="container">
				
				<div class="card">
				    <!-- Default panel contents -->
				    <div class="card-header"><span class="lead"><fmt:message key="system.resource.not.found"> </fmt:message></span> </div>
				 </div>   j
			</div>
			<!-- Right Side Bar -->
		</div>
	</div>
	
   	<%@include file="../home/footer.jsp" %>

</body>
</html>