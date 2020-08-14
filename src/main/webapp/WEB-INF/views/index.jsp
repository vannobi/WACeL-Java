<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    	<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Login page</title>
		<!-- Before Bootstrap JS --> 
		<link href="<c:url value='/assets/css/bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
  		<script src="<c:url value='/assets/js/jquery/jquery.min.js' />" ></script>
		<script src="<c:url value='/assets/js/popper/popper.js' />" ></script>
  		<script src="<c:url value='/assets/js/bootstrap/js/bootstrap.min.js' />" ></script>
		
	</head>

	<body>
	
    <div class="container">
    	<c:url var="loginUrl" value="/login" />
        <form class="form-horizontal" role="form" method="POST" action="${loginUrl}">
            <!-- APP IMG -->
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                	<div class="form-group ">
            			<img src="<c:url value='/assets/img/logo_CEL.png' />" class="img-fluid"  alt="C&L" height="150" width="150" data-atf="3">
            		</div>
            	</div>
            </div>		
            
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                    <h2>Login</h2>
                    <hr>
                </div>
            </div>
            
			<!-- LOGIN/LOGOUT MESSAGES -->
			<c:if test="${param.error != null}">
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="alert alert-danger alert-dismissible" role="alert">
							<button type="button" class="close" data-dismiss="alert"
								aria-label="Close">
								<span aria-hidden="true">x</span>
							</button>
							<p>
								<fmt:message key="login.invalid" />
							</p>
						</div>
					</div>
				</div>

			</c:if>

			<c:if test="${param.logout != null}">
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="alert alert-success alert-dismissible" role="alert">
							<button type="button" class="close" data-dismiss="alert"
								aria-label="Close">
								<span aria-hidden="true">x</span>
							</button>
							<p>
								<fmt:message key="logout.success" />
							</p>
						</div>

					</div>
				</div>
			</c:if>

			<!-- Inputs -->
			<div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                    <div class="form-group has-danger">
                        <label class="sr-only" for="username"><fmt:message key="user.login" /></label>
                        <div class="input-group mb-2 mr-sm-2 mb-sm-0">
                            <div class="input-group-addon" style="width: 2.6rem"><i class="fa fa-at"></i></div>
                            <input type="text" class="form-control" id="login" name="login" placeholder="Enter Login" required autofocus>
                        </div>
                    </div>
                </div>
                
            </div>
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="sr-only" for="password"><fmt:message key="user.password" /></label>
                        <div class="input-group mb-2 mr-sm-2 mb-sm-0">
                            <div class="input-group-addon" style="width: 2.6rem"><i class="fa fa-key"></i></div>
                            <input type="password" name="password" class="form-control" id="password" placeholder="Password" required>
                        </div>
                    </div>
                </div>
                
            </div>
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-6" style="padding-top: .35rem">
                    <div class="form-check mb-2 mr-sm-2 mb-sm-0">
                        <label class="form-check-label">
                            <input class="form-check-input" id="rememberme" name="remember-me" type="checkbox" >
                            <span style="padding-bottom: .15rem"><fmt:message key="user.rememberme" /></span>
                        </label>
                    </div>
                </div>
            </div>
            <!-- Security -->
            <input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />
            
            <div class="row" style="padding-top: 1rem">
                <div class="col-md-3"></div>
                <div class="col-md-6">
                     
                    <input type="submit" class="btn btn-lg btn-primary btn-block" value="<fmt:message key="login.title" />">
                    <a class="btn btn-link" href="#">Forgot Your Password?</a>
                </div>
            </div>
        </form>
    </div>
   
		 
</body>
</html>