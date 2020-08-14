<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Lexicon Registration</title>
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
			<%@include file="../home/leftSideBar.jsp"%>

			<!-- Main Content -->
		 	<div class="container">
				<div class="card">
					<div class="card-header"><span class="lead"><fmt:message key="lexicon.registration.form.title"> </fmt:message></span> </div>
					
		
					<form:form method="POST" modelAttribute="lexicon" class="form-horizontal" >
						<form:input type="hidden" path="id" id="id"/>
					
								
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="name"><fmt:message key="lexicon.name" /></label>
								<div class="col-lg-10">
									<form:input type="text" path="name" id="name" class="form-control form-control-sm"/>
									<div class="has-error">
										<form:errors path="name" class="text-danger"/>
									</div>
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="notion"><fmt:message key="lexicon.notion" /></label>
								<div class="col-lg-10">
									<form:textarea path="notion" id="notion" rows="5" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="notion" class="text-danger"/>
									</div>
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="impact"><fmt:message key="lexicon.impact" /></label>
								<div class="col-lg-10">
									<form:textarea path="impact" id="impact" rows="5" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="impact" class="text-danger"/>
									</div>
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="lexiconType"><fmt:message key="lexicon.type" /></label>
								<div class="col-lg-10">
									<form:select path="lexiconType"  items="${lexiconTypes}" itemValue="description" itemLabel="description" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="lexiconType" class="text-danger"/>
									</div>
								</div>
							</div>
						
						
						
							<div class="form-group offset-sm-2 col-md-12">
								<c:choose>
									<c:when test="${edit}">
										<input type="submit" value="<fmt:message key="system.form.update" />" class="btn btn-primary custom-width"/>  <a href="<c:url value='/home/mainPage' />" class="btn btn-secondary custom-width"><fmt:message key="system.form.cancel" /></a>
									</c:when>
									<c:otherwise>
										<input type="submit" value="<fmt:message key="system.form.add" />" class="btn btn-primary custom-width"/>  <a href="<c:url value='/home/mainPage' />" class="btn btn-secondary custom-width"><fmt:message key="system.form.cancel" /></a>
									</c:otherwise>
								</c:choose>
							</div>
						
					</form:form>
				</div>
			</div>
			<!-- Right Side bar -->
			<%@include file="../home/rightSideBar.jsp"%>
		</div>
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