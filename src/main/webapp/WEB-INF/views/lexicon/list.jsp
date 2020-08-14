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
	<title>Lexicons List</title>
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
					<!-- Default panel contents -->
					<div class="card-header">
						<span class="lead"><fmt:message key="lexicon.list.form.title">
							</fmt:message></span>
					</div>
					<table class="table table-hover">

						<thead>
							<tr>
								<th><fmt:message key="lexicon.name" /></th>
								<th><fmt:message key="lexicon.notion" /></th>
								<th><fmt:message key="lexicon.impact" /></th>
								<th><fmt:message key="lexicon.type" /></th>
								<th width="100"></th>
								<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
									<th width="100"></th>
								</sec:authorize>
								<sec:authorize access="hasRole('ADMIN')">
									<th width="100"></th>
								</sec:authorize>

							</tr>
						</thead>
						<tbody>
							<c:forEach items="${lexicons}" var="lexicon">
								<tr>
									<td>${lexicon.name}</td>
									<td>${lexicon.notion}</td>
									<td>${lexicon.impact}</td>
									<td>${lexicon.lexiconType}</td>
									<td><a href="<c:url value='/lexicon/show-lexicon-${lexicon.id}' />"
										class="btn btn-info custom-width"><fmt:message
												key="system.form.show" /></a></td>
									<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
										<td><a
											href="<c:url value='/lexicon/edit-lexicon-${lexicon.id}' />"
											class="btn btn-success custom-width"><fmt:message
													key="system.form.update" /></a></td>
									</sec:authorize>
									<sec:authorize access="hasRole('ADMIN')">
										<td><a
											href="<c:url value='/lexicon/delete-lexicon-${lexicon.id}-${lexicon.name}' />"
											class="btn btn-danger custom-width"><fmt:message
													key="system.form.remove" /></a></td>
									</sec:authorize>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<!-- Nav Bar : Bottom -->
				<nav class="navbar navbar-light navbar-expand-md">
					<div class="container">
						<sec:authorize access="hasRole('ADMIN')">
							<a class="btn btn-link" href="<c:url value='/lexicon/newlexicon' />"> <fmt:message key="lexicon.add.new.option" /> </a>
						</sec:authorize>
						
					</div>
				</nav>

			</div>
			<!-- Right Side Bar -->
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