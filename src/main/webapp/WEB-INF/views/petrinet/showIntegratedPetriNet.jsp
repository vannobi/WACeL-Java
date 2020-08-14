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
	<title>Integrated Petri-Net</title>
	<!-- Before Bootstrap JS --> 
	<link href="<c:url value='/assets/css/bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
	<script src="<c:url value='/assets/js/jquery/jquery.min.js' />" ></script>
	<script src="<c:url value='/assets/js/popper/popper.js' />" ></script>
	<script src="<c:url value='/assets/js/bootstrap/js/bootstrap.min.js' />" ></script>
	
	<!-- APP -->
	<script src="<c:url value='/assets/js/app/ajax_functions.js' />" ></script>
	
	<script src="<c:url value='/assets/js/app/ajax_functions_petri_nets.js' />" ></script>
		
		
		<!-- importar libs para visualizacao de grafos - cytoescape -->
		<script src="<c:url value='/assets/js/cytoscape/cytoscape.js' />" ></script>
		<link href="<c:url value='/assets/css/cytoscape/style_cytoscape.css' />" rel="stylesheet"></link>
				
		<!-- importar libs para visualizacao de tooltips dos nodes do grafo - cytoescape - qtip -->
		<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1, maximum-scale=1">
	
		<link href="<c:url value='/assets/css/cytoscape/jquery.qtip.css' />" rel="stylesheet"></link>	
		
		<script src="<c:url value='/assets/js/cytoscape/jquery.qtip.js' />" ></script>
		
		<script src="<c:url value='/assets/js/cytoscape/cytoscape-qtip.js' />" ></script>
				
		<script type="text/javascript">

		$(document).ready(function(){
			var project = document.getElementById("projectId");
			var selectedProjectId = project.value;
			var scenario = document.getElementById("scenarioId");
			var selectedScenarioId = scenario.value;
			
			show_integrated_petri_net(selectedProjectId, selectedScenarioId);
		});
	</script>
	<title>Gerando Petri-Nets do Cen&aacute;rio</title>
    </head>
    <body>
	
		<table width=457>
			<tr>
				<td align="left"><span align="left" class="pes"><fmt:message key="petrinet.show.form.scenario.integrated.title"> </fmt:message></span></td>
			</tr>
			<tr>
				<td align="left">
					<input type="button" id="dwnBtnPN" value='<fmt:message key="petrinet.show.form.download"> </fmt:message>'/>
				</td>
			</tr>
		</table>
		 		
		<input type="hidden" id="projectId" name="projectId" value=${projectId}>
 		<input type="hidden" id="scenarioId" name="scenarioId" value=${scenarioId}>

		<div id="cy" style="border:2px solid; z-index: 100; overflow:auto; overflow-x: scroll ; overflow-y: scroll;"></div>
		

        <div align=left bgcolor="#ffffff">

    </body>
</html>
