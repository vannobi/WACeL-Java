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
	<title>Main Page</title>
	<!-- Before Bootstrap JS --> 
	<link href="<c:url value='/assets/css/bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
	<script src="<c:url value='/assets/js/jquery/jquery.min.js' />" ></script>
	<script src="<c:url value='/assets/js/popper/popper.js' />" ></script>
	<script src="<c:url value='/assets/js/bootstrap/js/bootstrap.min.js' />" ></script>
	
	<!-- APP -->
	<script src="<c:url value='/assets/js/app/ajax_functions.js' />" ></script>
		
</head>

<body>
	<%@include file="../home/authHeader.jsp" %>
	 <!-- Messages -->
	 <%@include file="../home/messageFragment.jsp" %>
	 <div class="container-fluid">
      	<!-- Grid Content -->
      	<div class="row flex-xl-nowrap">
	        <!-- Left Bar -->
			<%@include file="../home/leftSideBar.jsp" %>


			<!-- Main Content -->
			<div id="div_main_content" class="container">
				
		   	</div>
		   	<!-- Right Bar -->
		   	<%@include file="../home/rightSideBar.jsp"%>
		</div>
		<!-- Nav Bar - Bottom -->
	   	
   	</div>

   	<%@include file="../home/footer.jsp" %>
   	
   	<script type="text/javascript">
	//Load Lexicons or Scenarios		
	$(document).ready(function(){
		var element = document.getElementById("selProject");
		var selectProjectId = element.options[element.selectedIndex].value;
		create_menu_scenarios_lexicons(selectProjectId)	;
	
	});	
	</script>
   	
</body>
</html>