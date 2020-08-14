	  
	<!-- Header Page to all views -->
	<!-- Responsive Header Nav Bar -->
	<div class="navbar navbar-expand-md navbar-dark bg-primary mb-4 text-white" role="navigation">
			    
		<!-- Left side -->
		<!-- APP LOGO -->
		<div class="col-12 col-md-3 col-xl-2 title-bar" > 
			<a href="<c:url value='/home/mainPage' />" title="APP Logo" class="avatar-sm-container pull-left  text-white">
				<img src="<c:url value='/assets/img/logo_CEL.png' />" class="img-rounded user-avatar-sm" alt="C&L" height="50" width="50" data-atf="3">
				<br>
                <small> <fmt:message key="system.welcome.title" /></small>
			</a>
			
		</div>
				
		<!-- Brand and toggle get grouped for better mobile display -->
		<!-- RESPONSIVE: RESIZE -->
		<button class="navbar-toggler border border-white" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
	        <span class="navbar-toggler-icon"></span>
	    </button>
		
		<!-- main Content -->
		<div class="navbar-nav-scroll">
		</div>
		
		<!-- Right content -->
		<div class="collapse navbar-collapse" id="navbarCollapse">
			
            <!-- TBD: SEARCH -->
            <!-- 
            <ul class="nav navbar-nav navbar-left mr-auto">
	            <li class="nav-item">
	                <form class="form-inline mt-2 mt-md-0">
				        <input class="form-control mr-sm-2" type="text" placeholder="Search" aria-label="Search">
				    	<button class="btn btn-info  border border-white  my-2 my-sm-0  text-white" type="submit"><fmt:message key="system.form.query" /></button>
				    </form>
	            </li>
	        </ul>
	        -->
	        
	        <!-- TBD: Select project -->
            <ul id="ul_select_project_menu" class="nav navbar-nav navbar-left mr-auto">
	            <li class="nav-item">
	            	<div class="form-group">
					  <label for="selProject"><fmt:message key="project.select.option"/></label>
					  <select title="<fmt:message key="project.select.option"></fmt:message>" 
					  			class="form-control"  id="selProject" onchange="create_menu_scenarios_lexicons(this.options[this.selectedIndex].value);" >
					      <option value="-1"><fmt:message key="system.form.select.first"></fmt:message></option>
						  <c:forEach items="${projectsUser}" var="project">
						  	<option value=${project.id} <c:if test = "${project.id == selectedProjectId}"> selected="selected" </c:if>>
						  	  	${project.name}
						  	</option>
						  	
						  </c:forEach>
					  </select>
					</div>
	               
	            </li>
	        </ul>
	        
	        <!-- MENUS -->
			<ul id="ul_main_menu" class="nav navbar-nav navbar-right mr-auto">
	            
	            <!-- Main Page -->
	            <li class="nav-item">
	                <a class="nav-link  text-white" href="<c:url value='/home/mainPage' />" ><fmt:message key="home.main.page.title" /> </a>
	            </li>
	            
	            <!-- Lexicon -->
	           	<li id="li_nav_lexicon" class="nav-item" >
	                <a class="nav-link  text-white" href="<c:url value='/lexicon/newlexicon' />" ><fmt:message key="lexicon.add.new.option" /> </a>
	            </li>
	            	            
	           	<!-- Scenario -->
	           	<li id="li_nav_scenario" class="nav-item">
		                <a  class="nav-link  text-white" href="<c:url value='/scenario/newscenario' />" ><fmt:message key="scenario.add.new.option" /> </a>
		         </li>
	            
	           
	           	<!-- Projects -->
	            <li class="nav-item">
	                <a class="nav-link  text-white" href="<c:url value='/project/list' />" ><fmt:message key="project.module.title" /> </a>
	            </li>
	            
	           	<!-- MENU -->
				<li class="nav-item dropdown">
	                <!-- More... -->
	                <!-- Logged User -->
	                <a class="nav-link dropdown-toggle my-2 my-sm-0 btn border border-white rounded text-white" id="dropdown1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> 
	                	<i class="glyphicon glyphicon-task"></i>
	                	${loggedinuser}
	                </a>
	                <ul class="dropdown-menu" aria-labelledby="dropdown1">
	                	<!-- Update User Menu Item -->
	                    <li class="dropdown-item" >
	                    	<a class="nav-item" href="<c:url value='/user/update-user-${loggedinuser}' />"><fmt:message key="user.update.option"></fmt:message></a>
	                    </li>
	                    <!-- Manage Users -->
	                    <li class="dropdown-item" >
	                    	<a class="nav-item" href="<c:url value='/user/list' />"> <fmt:message key="user.module.title" /> </a>
	                    </li>
	                	<!-- Logout Menu Item -->
	                    <li class="dropdown-item" >
	                    	<a class="active nav-item" href="<c:url value='/home/logout' />"><fmt:message key="logout.title"></fmt:message></a>
	                    </li>
	                    <!-- Menu Item with sub items -->
	                    <!-- 
	                    <li class="dropdown-item dropdown">
	                        <a class="dropdown-toggle" id="dropdown1-1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Dropdown1.1</a>
	                        <ul class="dropdown-menu" aria-labelledby="dropdown1-1">
	                            <li class="dropdown-item" href="#"><a>Action 1.1</a></li>
	                            <li class="dropdown-item dropdown">
	                                <a class="dropdown-toggle" id="dropdown1-1-1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Dropdown1.1.1</a>
	                                <ul class="dropdown-menu" aria-labelledby="dropdown1-1-1">
	                                    <li class="dropdown-item" href="#"><a>Action 1.1.1</a></li>
	                                </ul>
	                            </li>
	                        </ul>
	                    </li>
	                    -->
	                </ul>
	            </li>
            
            </ul>
            
		</div>
		<!-- Right side -->
	</div>
	
	<script type="text/javascript">
	//Hide Lexicons or Scenarios for Options: USER, PROJECT 		
	$(document).ready(function(){
		//Hide Lexicon and Scenario Menu
		$('#li_nav_lexicon').hide();
		$('#li_nav_scenario').hide();
	
	});	
	</script>
	
