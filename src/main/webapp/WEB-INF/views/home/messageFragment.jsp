	  
	<!-- Message fragment to all views -->
	<!-- include in main content <div class="container"> -->
	
	<!-- Sub Header -->
	<div class="container-fluid">
      	<!-- Grid Content -->
	    <div class="row flex-xl-nowrap">
	    	<!-- left side -->
	    	<div class="col-12 col-md-3 col-xl-2 bd-sidebar">
	    	</div>
	    	<!-- Main content -->
	    	<div class="container">	
				<!-- SUB-NAVIGATION OPTIONS -->
				<!-- Messages for User Operations: Create, Update, Delete -->
				<c:if test="${not empty success}">
					<div class="alert alert-success alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close">
							<span aria-hidden="true">x</span>
						</button>
						<strong>${success}</strong>
					</div>
				</c:if>
				<!-- Exceptions: Custom messages -->
				<c:if test="${not empty error}">
					<div class="alert alert-danger alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close">
							<span aria-hidden="true">x</span>
						</button>
						<strong>${error}</strong>
					</div>
				</c:if>
				
			</div>
			<!-- Right side -->
			<div class="col-12 col-sm-3 col-xl-2 bd-sidebar">
			</div>
		</div>
	</div>