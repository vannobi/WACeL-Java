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
	<title>Projects List</title>
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
			
			<!-- Main Content -->
			<div class="container">

				<div class="card">
					<!-- Default panel contents -->
					<div class="card-header">
						<span class="lead"><fmt:message key="project.list.form.title">
							</fmt:message></span>
					</div>
					<table class="table table-hover">

						<thead>
							<tr>
								<th><fmt:message key="project.name" /></th>
								<th><fmt:message key="project.description" /></th>
								<th><fmt:message key="project.casesensitive" /></th>
								<th width="100"></th>
								<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
									<th width="100"></th>
								</sec:authorize>
								<th width="100"></th>
								<sec:authorize access="hasRole('ADMIN')">
									<th width="100"></th>
								</sec:authorize>

							</tr>
						</thead>
						<tbody>
							<c:forEach items="${projects}" var="project">
								<tr>
									<td>${project.name}</td>
									<td>${project.description}</td>
									<td>${project.caseSensitive}</td>
									<td><a href="<c:url value='/project/show-project-${project.id}' />"
										class="btn btn-info custom-width"><fmt:message
												key="system.form.show" /></a></td>
									<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
										<td><a
											href="<c:url value='/project/edit-project-${project.id}' />"
											class="btn btn-success custom-width"><fmt:message
													key="system.form.update" /></a></td>
									</sec:authorize>
									
									<td><a href="<c:url value='/analysis/analyze-project-${project.id}' />"
										class="btn btn-warning custom-width" target="_blank"><fmt:message
												key="system.form.analyze" /></a></td>
																				
									<sec:authorize access="hasRole('ADMIN')">
										<td><a
											href="<c:url value='/project/delete-project-${project.id}-${project.name}' />"
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
							<a class="btn btn-link" href="<c:url value='/project/newproject' />"> <fmt:message key="project.add.new.option" /> </a>
						</sec:authorize>
						
					</div>
				</nav>

			</div>
			<!-- Right Side Bar -->
		</div>
		
	</div>
	
   	<%@include file="../home/footer.jsp" %>
</body>
</html>