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
	<title>Project Registration</title>
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
					<div class="card-header"><span class="lead"><fmt:message key="project.registration.form.title"> </fmt:message></span> </div>
						
					<form:form method="POST" modelAttribute="projectDto" class="form-horizontal" >
						<form:input type="hidden" path="id" id="id"/>
								
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="name"><fmt:message key="project.name" /></label>
								<div class="col-lg-10">
									<form:input type="text" path="name" id="name" class="form-control"/>
									<div class="has-error">
										<form:errors path="name" class="text-danger"/>
									</div>
								</div>
							</div>
						
		
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="description"><fmt:message key="project.description" /></label>
								<div class="col-lg-10">
									<form:textarea path="description" id="description" rows="5" class="form-control" />
									<div class="has-error">
										<form:errors path="description" class="text-danger"/>
									</div>
								</div>
							</div>
							
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="language"><fmt:message key="project.language" /></label>
								<div class="col-lg-10">
									<div class="input-group">
									<form:radiobuttons path="language" items="${languageList}" cssClass="btn-group"/>
									<div class="has-error">
										<form:errors path="language" class="text-danger"/>
									</div>
									</div>
								</div>
							</div>
	
							<div class="form-group row col-lg-12">
								<div class="col-sm-2"><fmt:message key="project.casesensitive" /></div>
								<div class="col-lg-10">
									<div class="form-check">
										<c:choose>
											<c:when test="${edit}">
												<form:checkbox path="caseSensitive" id="caseSensitive" class="form-check-input "  />
												<div class="has-error">
													<form:errors path="caseSensitive" class="text-danger"/>
												</div>
											</c:when>
											<c:otherwise>
												<form:checkbox path="caseSensitive" id="caseSensitive" class="form-check-input" />
												<div class="has-error">
													<form:errors path="caseSensitive" class="text-danger"/>
												</div>
											</c:otherwise>
										</c:choose>
										
										
									</div>
								</div>
							</div>
											
						
							<div class="form-group row col-lg-12">
								<label class="col-sm-2 col-form-label" for="collaborators"><fmt:message key="project.collaborators" /></label>
								<div class="col-lg-10">
									<form:select path="collaborators"  items="${collaboratorCandidates}" multiple="true" itemValue="id" itemLabel="firstName" class="form-control form-control-sm" />
									<div class="has-error">
										<form:errors path="collaborators" class="text-danger"/>
									</div>
								</div>
							</div>
						
				
						<div class="row">
							<div class="form-group  offset-sm-2 col-md-12">
								<c:choose>
									<c:when test="${edit}">
										<input type="submit" value="<fmt:message key="system.form.update" />" class="btn btn-primary custom-width"/>  <a href="<c:url value='/project/list' />" class="btn btn-secondary custom-width"><fmt:message key="system.form.cancel" /></a>
									</c:when>
									<c:otherwise>
										<input type="submit" value="<fmt:message key="system.form.add" />" class="btn btn-primary custom-width"/>  <a href="<c:url value='/project/list' />" class="btn btn-secondary custom-width"><fmt:message key="system.form.cancel" /></a>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</form:form>
				</div>
				<!-- Nav Bar: Bottom -->
				<nav class="navbar navbar-light navbar-expand-md">
				</nav>
			</div>
			<!-- Right Side bar -->
		</div>
	</div>
	
	<%@include file="../home/footer.jsp" %>
</body>
</html>