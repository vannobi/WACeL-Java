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
	<title>Lexicon Details</title>
	<!-- Before Bootstrap JS --> 
	<link href="<c:url value='/assets/css/bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
	<script src="<c:url value='/assets/js/jquery/jquery.min.js' />" ></script>
	<script src="<c:url value='/assets/js/popper/popper.js' />" ></script>
	<script src="<c:url value='/assets/js/bootstrap/js/bootstrap.min.js' />" ></script>
	
	<!-- APP -->
	<script src="<c:url value='/assets/js/app/ajax_functions.js' />" ></script>
	
</head>

<body>
	<%@include file="../home/authHeader.jsp"%>
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
				    <div class="card-header"><span class="lead"><fmt:message key="lexicon.show.form.title"> </fmt:message></span> </div>
				
					<form class="form-horizontal" >
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="name"><fmt:message key="lexicon.name" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="name" id="name" class="form-control form-control-sm" value="${lexicon.name}"/>
									
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="notion"><fmt:message key="lexicon.notion" /></label>
								<div class="col-lg-10">
									<textarea  disabled="disabled" path="notion" id="notion" rows="5" class="form-control form-control-sm" >${lexicon.notion}</textarea>
									
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="impact"><fmt:message key="lexicon.impact" /></label>
								<div class="col-lg-10">
									<textarea  disabled="disabled" path="impact" id="impact" rows="5" class="form-control form-control-sm" >${lexicon.impact}</textarea>
									
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="lexiconType"><fmt:message key="lexicon.type" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="lexiconType" id="lexiconType" class="form-control form-control-sm"  value="${lexicon.lexiconType}"/>
									
								</div>
							</div>
						
						<div class="row">
							<div class="form-group offset-sm-2 col-md-12">

								<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
									<a
										href="<c:url value='/lexicon/edit-lexicon-${lexicon.id}' />"
										class="btn btn-success custom-width"><fmt:message
											key="system.form.update" /></a>
								</sec:authorize>
								<sec:authorize access="hasRole('ADMIN')">
									<a
										href="<c:url value='/lexicon/delete-lexicon-${lexicon.id}-${lexicon.name}' />"
										class="btn btn-danger custom-width"><fmt:message
											key="system.form.remove" /></a>
								</sec:authorize>
	
								<a href="<c:url value='/home/mainPage' />"
									class="btn btn-secondary custom-width"><fmt:message
										key="system.form.cancel" /></a>


							</div>
						</div>						
				
						
					</form>
						
						
				</div>
				
			</div>
			<!-- Right Side Bar -->
			<%@include file="../home/rightSideBar.jsp"%>
		</div>
	</div>

	<%@include file="../home/footer.jsp"%>
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