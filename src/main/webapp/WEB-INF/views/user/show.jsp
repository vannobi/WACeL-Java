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
	<title>User Details</title>
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
				    <div class="card-header"><span class="lead"><fmt:message key="user.show.form.title"> </fmt:message></span> </div>
				
						
						<form class="form-horizontal" >
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="firstName"><fmt:message key="user.firstName" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="firstName" id="firstName" class="form-control form-control-sm" value="${user.firstName}"/>
									
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="lastName"><fmt:message key="user.lastName" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="lastName" id="lastName" class="form-control form-control-sm" value="${user.lastName}"/>
									
								</div>
							</div>
						
				
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="login"><fmt:message key="user.login" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="login" id="login" class="form-control form-control-sm"  readonly="true"  value="${user.login}"/>
										
								</div>
							</div>
						
								
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="email"><fmt:message key="user.email" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="email" id="email" class="form-control form-control-sm"  value="${user.email}"/>
									
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="institutionName"><fmt:message key="user.institution" /></label>
								<div class="col-lg-10">
									<input type="text"  disabled="disabled" path="institutionName" id="institutionName" class="form-control form-control-sm"  value="${user.institutionName}"/>
									
								</div>
							</div>
						
							<div class="form-group row col-lg-12">
								<label class="col-md-2"><fmt:message key="user.userProfiles" /></label>
								<div class="col-lg-10">
									<ul>
									<c:forEach items="${user.userProfiles}" var="role">
										<li> ${role.type} </li>
									</c:forEach>
									</ul>
								</div>
							</div>
						
							<div class="row">
								<div class="form-group offset-sm-2 col-md-12">
	
									<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
										<a href="<c:url value='/user/edit-user-${user.id}' />"
											class="btn btn-success custom-width"><fmt:message
												key="system.form.update" /></a>
									</sec:authorize>
									<sec:authorize access="hasRole('ADMIN')">
										<a
											href="<c:url value='/user/delete-user-${user.id}-${user.login}' />"
											class="btn btn-danger custom-width"><fmt:message
												key="system.form.remove" /></a>
									</sec:authorize>
										
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
									<fmt:message key="system.go.to" /> <a href="<c:url value='/user/list' />" class="btn btn-link"><fmt:message key="user.list.option" /></a>
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