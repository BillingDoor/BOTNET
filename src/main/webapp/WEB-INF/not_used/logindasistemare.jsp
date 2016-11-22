<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login Form</title>
<!-- inject:css -->
<!-- < <link rel="stylesheet" href="css/lib/getmdl-select.min.css">
    <link rel="stylesheet" href="css/lib/nv.d3.css"> -->
   <!--  <link rel="stylesheet" href="css/application.css"> -->
<link rel="stylesheet" href="https://code.getmdl.io/1.2.1/material.red-deep_orange.min.css" />
    <link rel="stylesheet" href="css/mycss.css">
    <!-- endinject -->
     <link href='https://fonts.googleapis.com/css?family=Roboto:400,500,300,100,700,900' rel='stylesheet'
          type='text/css'>
    <!-- Adding Material Icons -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>


<body>

<div class="mdl-layout mdl-js-layout mdl-color--grey-100" >
	<main class="mdl-layout__content">
	
		<div class="mdl-card mdl-shadow--6dp">
			<div class="mdl-card__title mdl-color--primary mdl-color-text--white">
				<h2 class="mdl-card__title-text">Login Form</h2>
			</div>
	  	
	  	<div class="mdl-card__supporting-text">
	  	
	  	<c:url var="loginUrl" value="/login" />
				<form action="${loginUrl}" method="post" class="form-horizontal">
							<c:if test="${param.error != null}">
								<div class="alert alert-danger">
									<p>Invalid username and password.</p>
								</div>
							</c:if>
							<c:if test="${param.logout != null}">
								<div class="alert alert-success">
									<p>You have been logged out successfully.</p>
								</div>
							</c:if>
					<div class="mdl-textfield mdl-js-textfield">
						<input class="mdl-textfield__input" type="text" id="username" />
						<label class="mdl-textfield__label" for="username">Username</label>
					</div>
					<div class="mdl-textfield mdl-js-textfield">
						<input class="mdl-textfield__input" type="password" id="userpass" />
						<label class="mdl-textfield__label" for="userpass">Password</label>
					</div>
				
				
					<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-2">
  				<input type="checkbox" id="rememberme" name="remember-me" class="mdl-checkbox__input">
  					<span class="mdl-checkbox__label">Checkbox</span>
					</label>
			
			  	<input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />
			
			<div class="mdl-card__actions mdl-card--border">
				<button class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect">Log in</button>
			</div>		
				</form>
			</div>		
		</div>
	</main>
</div>
</body>














<!-- <body>
	<div class="mdl-layout mdl-layout--fixed-header mdl-js-layout  mdl-color--white-100">
		<main class="mdl-layout__content main_content">
		    <h3><strong>Login</strong></h3>
			<div class="login-form-div mdl-grid mdl-shadow--2dp">
				<div class="mdl-cell mdl-cell--12-col cell_con">
					<i class="material-icons">person</i>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						<input class="mdl-textfield__input" type="text" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$" id="sample2">
						<label class="mdl-textfield__label" for="sample2">Enter valid Email</label>
						<span class="mdl-textfield__error">Invalid Email...!</span>
			        </div>
				</div>				
				<div class="mdl-cell mdl-cell--12-col cell_con">
					<i class="material-icons">lock</i>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						<input class="mdl-textfield__input" type="text" id="sample2">
						<label class="mdl-textfield__label" for="sample2">Enter Password</label>
			        </div>
				</div>				
				<div class="mdl-cell cell_con">
					<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-2">
						<input type="checkbox" id="checkbox-2" class="mdl-checkbox__input">
						<span class="mdl-checkbox__label">Remember Me</span>
				    </label>
				</div>
				<div class="mdl-cell mdl-cell--12-col  login-btn-con">
					<button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--primary btn">Login</button>
				</div>
				<div class="mdl-cell mdl-cell--6-col mdl-cell--8-col-tablet links">
					<a class="mdl-button--primary">Register now !</a>
				</div>
				<div class="mdl-cell mdl-cell--6-col mdl-cell--8-col-tablet links">
					<a class="mdl-button--primary">Forgot password ?</a>
				</div>		
			</div>
	    </main>
    </div>	
</body> -->


</html>