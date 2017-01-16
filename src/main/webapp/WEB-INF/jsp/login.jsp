
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
			<div class="general-container">
				<div class="general-card">
					<div class="general-form">
						<c:url var="loginUrl" value="" />
						<form action="${loginUrl}" method="post" class="form-horizontal">
							<c:if test="${param.error != null}">
								<div class="alert alert-danger" style="color: red">
									<p>Invalid username and password.</p>
								</div>
							</c:if>
							<c:if test="${param.logout != null}">
								<div class="alert alert-success" style="color: green">
									<p>You have been logged out successfully.</p>
								</div>
							</c:if>
							<div>
								<input type="text" class="hiddbox" id="username" name="ssoId" onfocus="if(this.placeholder == '&#xf007';  Username') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = '&#xf007';  Username'; } " placeholder="&#xf007;  Username"><span class="highlight"></span><span class="bar"></span>
							</div>
							<div>
								<input type="password" class="hiddbox" id="password" name="password" onfocus="if(this.placeholder == '&#xf023';  Password') { this.placeholder = ''; }" onblur="if(this.placeholder == '') { this.placeholder = '&#xf023';  Password'; }" placeholder="&#xf023;  Password"><span class="highlight"></span><span class="bar"></span>
                          	</div>
							<p>
   							<input type="checkbox" id="rememberme" name="remember-me"/>  							
   							<label for="rememberme">Remember me</label>
							</p><p></p>
							<input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />

							<div class="form-actions">
								<button value="Log in">login</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>