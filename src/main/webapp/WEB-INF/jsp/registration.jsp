<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

<head>
	<link rel="icon" type="image/x-icon" href="/images/favicon.ico">
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;</title>
	<link href="<c:url value='/css/app.css' />" rel="stylesheet"></link>
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.4/material.red-purple.min.css" /> 
   	<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
	<link href='https://fonts.googleapis.com/css?family=Roboto:400,500,300,100,700,900' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/application.css">
</head>

<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header is-small-screen">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <div class="mdl-layout-spacer"></div>
            <!-- Search-->
       			
       			<li class="mdl-list__item mdl-list__item--two-line">
       				<span class="mdl-list__item-primary-content" >
                        <a href="../logout">
                        <i class="material-icons mdl-list__item-icon text-color--secondary" >exit_to_app</i>
                       </a>
                      </span>
    		 	</li>
     

        </div>
    </header>

    <div class="mdl-layout__drawer">
        <header> <img width="20%" src="/images/bitskull.jpg"></header>
        <nav class="mdl-navigation">
				
						<a class="mdl-navigation__link" href="../admin/index"> <i
					class="material-icons" role="presentation">dashboard</i> Dashboard
				</a>
				<a class="mdl-navigation__link" href="../admin/newuser"> <i
					class="material-icons" role="presentation"><i
						class="fa fa-address-book-o" aria-hidden="true"></i></i> Registration
				</a> 
				<a class="mdl-navigation__link mdl -navigation__link--current" href="../admin/addbot">
					<i class="material-icons" role="presentation"><i
						class="fa fa-plus-square-o" aria-hidden="true"></i></i> <i>Add Bot</i>
				</a>
				<a class="mdl-navigation__link mdl -navigation__link--current" href="../admin/removeallbot">
					<i class="material-icons" role="presentation"><i
						class="fa fa-trash-o" aria-hidden="true"></i></i> <i>Delete Bot</i>
				</a>
                <div class="mdl-layout-spacer"></div>
            <a class="mdl-navigation__link" href="https://github.com/xXCiccioXx/BOTNET">
                <i class="material-icons" role="presentation">link</i>
                GitHub
            </a>
        </nav>
        
    </div>
    
    
    
    
	<div id="mainWrapper" align="center">
 	<div class="general-container">
 	<div class="general-form">

		<div class="well lead">Registration Form:</div><br>
	 	<form:form method="POST" modelAttribute="user">
			<form:input type="hidden" path="id" id="id"/>
			
			<div>
			<form:input type="text" path="firstName" id="firstName" class="hiddbox" onfocus="if(this.placeholder == 'First Name') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'First Name'; } " placeholder="First Name"/><span class="highlight"></span><span class="bar"></span>
			<div class="has-error">
				<form:errors path="firstName" class="help-inline"/>
			</div>
			</div>
	
			<div>
			<form:input type="text" path="lastName" id="lastName" class="hiddbox" onfocus="if(this.placeholder == 'Last Name') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'Last Name'; }" placeholder="Last Name"/><span class="highlight"></span><span class="bar"></span>
			<div class="has-error">
				<form:errors path="lastName" class="help-inline"/>
			</div>
			</div>
	
			<div>
			<form:input type="text" path="ssoId" id="ssoId" class="hiddbox" onfocus="if(this.placeholder == 'SSO ID') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'SSO ID'; }" placeholder="SSO ID"/><span class="highlight"></span><span class="bar"></span>
			<div class="has-error">
				<form:errors path="ssoId" class="help-inline"/>
			</div>
			</div>
	
			<div>
			<form:input type="password" path="password" id="password" class="hiddbox" onfocus="if(this.placeholder == 'Password') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'Password'; }" placeholder="Password"/><span class="highlight"></span><span class="bar"></span>
			<div class="has-error">
				<form:errors path="password" class="help-inline"/>
			</div>
			</div>
	
			<div>
			<form:input type="text" path="email" id="email" class="hiddbox" onfocus="if(this.placeholder == 'E-Mail') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'E-Mail'; }" placeholder="E-Mail"/><span class="highlight"></span><span class="bar"></span>
			<div class="has-error">
				<form:errors path="email" class="help-inline"/>
			</div>
			</div>	

			<br>
			
			<div>
			<label for="userRoles">Select a role:</label>
			<form:select path="userRoles" items="${roles}" multiple="false" itemValue="id" itemLabel="type" class="minimal"  />
			<div class="has-error">
				<form:errors path="userRoles" class="help-inline"/>
			</div>
			</div>
	
			<br>
			
			<div>
				<button type="submit" value="Update">Join BiTViR</button>
			</div>
		</form:form>
	
	</div>
	</div>
	</div>
</div>

<script src="/js/d3.js"></script>
<script src="/js/getmdl-select.min.js"></script>
<script src="/js/material.js"></script>
<script src="/js/nv.d3.js"></script>
<script src="/js/widgets/employer-form/employer-form.js"></script>
<script src="/js/widgets/line-chart/line-chart-nvd3.js"></script>
<script src="/js/widgets/map/maps.js"></script>
<script src="/js/widgets/pie-chart/pie-chart-nvd3.js"></script>
<script src="/js/widgets/table/table.js"></script>
<script src="/js/widgets/todo/todo.js"></script>

</body>
</html>