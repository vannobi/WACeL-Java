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
	<title>Scenario Details</title>
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
			<div id="div_main_content" class="container">
				<div class="card">
				    <!-- Default panel contents -->
				    <div class="card-header"><span class="lead"><fmt:message key="scenario.show.form.title"> </fmt:message></span> </div>
					<form class="form-horizontal" >
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="title"><b><fmt:message key="scenario.title" /></b></label>
								<div class="col-lg-10">
									<!-- 
									<input type="text"  disabled="disabled" path="title" id="title" class="form-control form-control-sm" value="${scenario.title}"/>
									 -->
									<span>${scenario.title}</span> 
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="goal"><b><fmt:message key="scenario.goal" /></b></label>
								<div class="col-lg-10">
									<!-- 
									<textarea  disabled="disabled" path="goal" id="goal" rows="2" class="form-control form-control-sm">${scenario.goal}</textarea>
									 -->
									<span>${scenario.goal}</span> 
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="context"><b><fmt:message key="scenario.context" /></b></label>
								<div class="col-lg-10">
									<!-- 
									<textarea  disabled="disabled" path="context" id="context" rows="5" class="form-control form-control-sm" >${scenario.context}</textarea>
									 -->
									<span>${scenario.context}</span> 
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="actors"><b><fmt:message key="scenario.actors" /></b></label>
								<div class="col-lg-10">
									<!-- 
									<input type="text"  disabled="disabled" path="actors" id="actors" class="form-control form-control-sm" value="${scenario.actors}"/>
									 -->
									<span>${scenario.actors}</span> 
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="resources"><b><fmt:message key="scenario.resources" /></b></label>
								<div class="col-lg-10">
									<!-- 
									<input type="text"  disabled="disabled" path="resources" id="resources" class="form-control form-control-sm" value="${scenario.resources}"/>
									 -->
									<span>${scenario.resources}</span> 
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="episodes"><b><fmt:message key="scenario.episodes" /></b></label>
								<div class="col-lg-10">
									<!--
									<textarea  disabled="disabled" path="episodes" id="episodes" rows="5" class="form-control form-control-sm" >${scenario.episodes}</textarea>
									-->
									<span>${scenario.episodes}</span>
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="alternative"><b><fmt:message key="scenario.alternative" /></b></label>
								<div class="col-lg-10">
									<!-- 
									<textarea  disabled="disabled" path="alternative" id="alternative" rows="5" class="form-control form-control-sm" >${scenario.alternative}</textarea>
									 -->
									<span>${scenario.alternative}</span> 
								</div>
							</div>
						
				
						<div class="row">
							<div class="form-group offset-sm-2 col-md-12">

								<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
									<a
										href="<c:url value='/scenario/edit-scenario-${scenario.id}' />"
										class="btn btn-success custom-width"><fmt:message
											key="system.form.update" /></a>
								</sec:authorize>
								<sec:authorize access="hasRole('ADMIN')">
									<a
										href="<c:url value='/scenario/delete-scenario-${scenario.id}-${scenario.title}' />"
										class="btn btn-danger custom-width"><fmt:message
											key="system.form.remove" /></a>
								</sec:authorize>
								
								<a href="<c:url value='/analysis/analyze-scenario-${scenario.id}' />"
										class="btn btn-warning custom-width" target="_blank"><fmt:message
											key="system.form.analyze" /></a>
											
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