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
	<title>Project Details</title>
	<!-- Before Bootstrap JS --> 
	<link href="<c:url value='/assets/css/bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
	<script src="<c:url value='/assets/js/jquery/jquery.min.js' />" ></script>
	<script src="<c:url value='/assets/js/popper/popper.js' />" ></script>
	<script src="<c:url value='/assets/js/bootstrap/js/bootstrap.min.js' />" ></script>
	
</head>

<body>
	<%@include file="../home/authHeader.jsp"%>
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
				    <div class="card-header"><span class="lead"><fmt:message key="project.show.form.title"> </fmt:message></span> </div>
					<form class="form-horizontal" >
						
								
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="name"><fmt:message key="project.name" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="name" id="name" class="form-control" value="${project.name}"/>
									
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="description"><fmt:message key="project.description" /></label>
								<div class="col-lg-10">
									<textarea  id="description"  disabled="disabled" rows="5" class="form-control">${project.description}</textarea>
									
								</div>
							</div>
							
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="language"><fmt:message key="project.language" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="language" id="language" class="form-control" value="${project.language}"/>
								</div>
							</div>
	
							<div class="form-group row col-lg-12">
								<div class="col-sm-2"><fmt:message key="project.casesensitive" /></div>
								<div class="col-lg-10">
									<div class="form-check">
										<c:choose>
											<c:when test="${edit}">
												<input type="checkbox" disabled="disabled" path="caseSensitive" id="caseSensitive"  class="form-check-input" checked="${project.caseSensitive}"/>
											</c:when>
											<c:otherwise>
												<input type="checkbox" disabled="disabled"  path="caseSensitive" id="caseSensitive" class="form-check-input" checked="${project.caseSensitive}"/>
											</c:otherwise>
										</c:choose>
										
										
									</div>
								</div>
							</div>
											
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="collaborators"><fmt:message key="project.collaborators" /></label>
								<div class="col-lg-10">
									<ul id="collaborators">
										<c:forEach items="${project.collaborators}" var="collaborator">
											<li> ${collaborator.collaborator.firstName} </li>
										</c:forEach>
									</ul>
								</div>
							</div>

							<div class="row">
								<div class="form-group offset-sm-2 col-md-12">
	
									<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
										<a href="<c:url value='/project/edit-project-${project.id}' />"
											class="btn btn-success custom-width"><fmt:message
												key="system.form.update" /></a>
									</sec:authorize>
									<sec:authorize access="hasRole('ADMIN')">
										<a
											href="<c:url value='/project/delete-project-${project.id}-${project.name}' />"
											class="btn btn-danger custom-width"><fmt:message
												key="system.form.remove" /></a>
									</sec:authorize>
	
									<a
										href="<c:url value='/analysis/analyze-project-${project.id}' />"
										class="btn btn-warning custom-width" target="_blank"><fmt:message
											key="system.form.analyze" /></a> 
									<a
										href="<c:url value='/home/mainPage' />"
										class="btn btn-secondary custom-width"><fmt:message
											key="system.form.cancel" /></a>
	
	
								</div>
							</div>


					</form>
				</div>
				
				<!-- Nav Bar: Bottom -->
				<nav class="navbar navbar-light navbar-expand-md">
					<div class="container">
						<div id="navbar">
							<ul class="nav navbar-nav ml-auto">
								<li class="active nav-item">
									<fmt:message key="system.go.to" /> <a href="<c:url value='/project/list' />" class="btn btn-link"><fmt:message key="project.list.option" /></a>
								</li>
							</ul>
						</div>
					</div>
				</nav>	
			</div>
			<!-- Right Side Bar -->
		</div>
	</div>

	<%@include file="../home/footer.jsp"%>

</body>
</html>
				
												
						
				