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
	<title>Defects List</title>
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
					<!-- Default panel contents: project -->
					<div class="card-header"><span class="lead"><fmt:message key="analysis.show.form.project.title"></fmt:message>: ${project.name}</span></div>
					<!-- for each scenario -->
					<c:forEach items="${scenarios}" var="scenario">
							<!-- scenario -->
							<div class="card">
								<!-- Default panel contents -->
								<div class="card-header"><span class="lead">${scenario.title}</span></div>
							</div>
							<!-- Defects -->	
							<div class="card">
								
								<!-- Unambiguity analysis -->
								<table class="table table-hover">
									<thead>
										<tr>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.property.unambiguity.title"></fmt:message></th>
											
										</tr>
										<tr>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.property.title"></fmt:message></th>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.defect.title"></fmt:message></th>
											
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${unambiguityProperties}" var="property">
											<tr>
												<td class="border border-dark bg-secondary text-white">${property}</td>
												<td>
													<ul>
													<c:forEach items="${unambiguityDefects[scenario.id]}" var="defect">
														<c:if test = "${defect.qualityProperty eq property}"> 
															<li>
																${defect.description}
															</li>
														</c:if>
													
													</c:forEach>
													</ul>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								
								<!-- Completeness analysis -->
								<table class="table table-hover">
									<thead>
										<tr>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.property.completeness.title"></fmt:message></th>
											
										</tr>
										<tr>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.property.title"></fmt:message></th>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.defect.title"></fmt:message></th>
											
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${completenessProperties}" var="property">
											<tr>
												<td class="border border-dark bg-secondary text-white">${property}</td>
												<td>
													<ul>
													<c:forEach items="${completenessDefects[scenario.id]}" var="defect">
														<c:if test = "${defect.qualityProperty eq property}"> 
															<li>
																${defect.description}
															</li>
														</c:if>
													
													</c:forEach>
													</ul>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								
								<!-- Consistency analysis -->
								<table class="table table-hover">
									<thead>
										<tr>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.property.consistency.title"></fmt:message></th>
											<th>
													<a href="<c:url value='/petrinet/show-petriNet-${scenario.id}' />"
														class="btn btn-success custom-width" target="_blank"><fmt:message key="petrinet.show.form.scenario.title"> </fmt:message></a>
											</th>
										</tr>
										<tr>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.property.title"></fmt:message></th>
											<th class="border border-dark bg-secondary text-white"><fmt:message key="analysis.show.form.defect.title"></fmt:message></th>
											
										</tr>
									</thead>
									<tbody>
											
										<c:forEach items="${consistencyProperties}" var="property">
											<tr>
												<td class="border border-dark bg-secondary text-white">${property}</td>
												<td>
													<ul>
													<c:forEach items="${consistencyDefects[scenario.id]}" var="defect">
														<c:if test = "${defect.qualityProperty eq property}"> 
															<li>
																${defect.description}
															</li>
														</c:if>
													
													</c:forEach>
													</ul>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
					</c:forEach>
				</div>
				
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