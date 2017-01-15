
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
	<head>
		<link rel="icon" type="image/x-icon" href="/images/favicon.ico">
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;</title>
		<link href="<c:url value='/css/app.css' />" rel="stylesheet"></link>
		<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
	</head>

	<body>
		<div id="mainWrapper" align="center">
			<div class="login-container">
				<div class="login-card">
					<div class="login-form">
						<c:url var="loginUrl" value="" />
						<form action="${loginUrl}" method="post" class="form-horizontal">
							<c:if test="${param.error != null}">
								<div class="alert alert-danger" style="color: red">
									<p>The fields can not be empty.</p>
								</div>
							</c:if>
							<c:if test="${param.logout != null}">
								<div class="alert alert-success" style="color: green">
									<p>You have been registered successfully.</p>
								</div>
							</c:if>
							<div>
								<input type="text" class="hiddbox" id="firstname" name="firstname" onfocus="if(this.placeholder == 'First Name') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'First Name'; } " placeholder="First Name"><span class="highlight"></span><span class="bar"></span>
							</div>
							<div>
								<input type="text" class="hiddbox" id="lastname" name="lastname" onfocus="if(this.placeholder == 'Last Name') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = 'Last Name'; }" placeholder="Last Name"><span class="highlight"></span><span class="bar"></span>
                          	</div>
                          	<div>
								<input type="text" class="hiddbox" id="ssoId" name="Username" onfocus="if(this.placeholder == '&#xf007;  Username') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = '&#xf007;  Username'; }" placeholder="&#xf007;  Username"><span class="highlight"></span><span class="bar"></span>
                          	</div>
                          	<div>
								<input type="password" class="hiddbox" id="password" name="password" onfocus="if(this.placeholder == '&#xf023;  Password') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = '&#xf023;  Password'; }" placeholder="&#xf023;  Password"><span class="highlight"></span><span class="bar"></span>
                          	</div>
                          	<div>
								<input type="text" class="hiddbox" id="email" name="email" onfocus="if(this.placeholder == '&#xf0e0;  E-Mail') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = '&#xf0e0;  E-Mail'; }" placeholder="&#xf0e0;  E-Mail"><span class="highlight"></span><span class="bar"></span>
                          	</div>
                          	<p></p>
                          	<p></p>
                          	<div>
								Select a role: <form:select path="roles" items="${roles}" multiple="false" itemValue="id" itemLabel="type" class="minimal" />
                          	</div>
							<p></p>

							<!-- <input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />-->
 
							<form action="/white" method="">
								<button value="Register">Register</button>
								<a href="/white"><button value="Cancel">Cancel</button></a>
							</form>
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>