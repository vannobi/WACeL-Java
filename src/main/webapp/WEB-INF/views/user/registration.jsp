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
	<title>User Registration</title>
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
					<div class="card-header"><span class="lead"><fmt:message key="user.registration.form.title"> </fmt:message></span> </div>
					
		
					<form:form method="POST" modelAttribute="user" class="form-horizontal" >
						<form:input type="hidden" path="id" id="id"/>
					
								
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="firstName"><fmt:message key="user.firstName" /></label>
								<div class="col-lg-10">
									<form:input type="text" path="firstName" id="firstName" class="form-control form-control-sm"/>
									<div class="has-error">
										<form:errors path="firstName" class="text-danger"/>
									</div>
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="lastName"><fmt:message key="user.lastName" /></label>
								<div class="col-lg-10">
									<form:input type="text" path="lastName" id="lastName" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="lastName" class="text-danger"/>
									</div>
								</div>
							</div>
						
				
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="login"><fmt:message key="user.login" /></label>
								<div class="col-lg-10">
									<c:choose>
										<c:when test="${edit}">
											<form:input type="text" path="login" id="login" class="form-control form-control-sm"  readonly="true"/>
										</c:when>
										<c:otherwise>
											<form:input type="text" path="login" id="login" class="form-control form-control-sm" />
											<div class="has-error">
												<form:errors path="login" class="text-danger"/>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						
				
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="password"><fmt:message key="user.password" /></label>
								<div class="col-lg-10">
									<form:input type="password" path="password" id="password" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="password" class="text-danger"/>
									</div>
								</div>
							</div>
						
				
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="email"><fmt:message key="user.email" /></label>
								<div class="col-lg-10">
									<form:input type="text" path="email" id="email" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="email" class="text-danger"/>
									</div>
								</div>
							</div>
						
						
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="institutionName"><fmt:message key="user.institution" /></label>
								<div class="col-lg-10">
									<form:input type="text" path="institutionName" id="institutionName" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="institutionName" class="text-danger"/>
									</div>
								</div>
							</div>
						
				
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="userProfiles"><fmt:message key="user.userProfiles" /></label>
								<div class="col-lg-10">
									<form:select path="userProfiles"  items="${roles}" multiple="true" itemValue="id" itemLabel="type" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="userProfiles" class="text-danger"/>
									</div>
								</div>
							</div>
						
				
						
							<div class="form-group offset-sm-2 col-md-12">
								<c:choose>
									<c:when test="${edit}">
										<input type="submit" value="<fmt:message key="system.form.update" />" class="btn btn-primary custom-width"/>  <a href="<c:url value='/user/list' />" class="btn btn-secondary custom-width"><fmt:message key="system.form.cancel" /></a>
									</c:when>
									<c:otherwise>
										<input type="submit" value="<fmt:message key="system.form.add" />" class="btn btn-primary custom-width"/>  <a href="<c:url value='/user/list' />" class="btn btn-secondary custom-width"><fmt:message key="system.form.cancel" /></a>
									</c:otherwise>
								</c:choose>
							</div>
						
					</form:form>
				</div>
			</div>
			<!-- Right Side bar -->
			
		</div>
	</div>
	
	<%@include file="../home/footer.jsp" %>
</body>
</html>