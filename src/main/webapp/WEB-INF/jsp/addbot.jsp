<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="icon" type="image/x-icon" href="/images/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;</title>
<link href="<c:url value='/css/login.css' />" rel="stylesheet"></link>
<link rel="stylesheet" type="text/css"
	href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
<link rel="stylesheet"
	href="https://storage.googleapis.com/code.getmdl.io/1.0.4/material.red-purple.min.css" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link
	href='https://fonts.googleapis.com/css?family=Roboto:400,500,300,100,700,900'
	rel='stylesheet' type='text/css'>
<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
	rel="stylesheet">
<link rel="stylesheet" href="/css/lib/getmdl-select.min.css">
<link rel="stylesheet" href="/css/lib/nv.d3.css">
<link rel="stylesheet" href="/css/application.css">

</head>

<body>
	<div
		class="mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header is-small-screen">
		<header class="mdl-layout__header">
			<div class="mdl-layout__header-row">
				<div class="mdl-layout-spacer"></div>
				<!-- Search-->

				<li class="mdl-list__item mdl-list__item--two-line"><span
					class="mdl-list__item-primary-content"> <a href="logout">
							<i
							class="material-icons mdl-list__item-icon text-color--secondary">exit_to_app</i>
					</a>
				</span></li>


			</div>
		</header>

				<div class="mdl-layout__drawer">
			<header>
				<img width="20%" src="/images/bitskull.jpg">
			</header>
			<nav class="mdl-navigation">
					<a class="mdl-navigation__link" href="indexadmin"> <i
					class="material-icons" role="presentation">dashboard</i> Dashboard
				</a>
				 <a class="mdl-navigation__link" href="newuser"> <i
					class="material-icons" role="presentation"><i
						class="fa fa-address-book-o" aria-hidden="true"></i></i> Registration
				</a> 
				<a class="mdl-navigation__link mdl -navigation__link--current" href="addbot">
					<i class="material-icons" role="presentation"><i
						class="fa fa-plus-square-o" aria-hidden="true"></i></i> <i>Add Bot</i>
				</a>
				<a class="mdl-navigation__link mdl -navigation__link--current" href="removeallbot">
					<i class="material-icons" role="presentation"><i
						class="fa fa-trash-o" aria-hidden="true"></i></i> <i>Delete Bot</i>
				</a>
			</nav>

		</div>



		<!--                                               PAGES                                                                                     -->



		<div id="mainWrapper" align="center">
			<div class="login-container">
				<div class="login-card">
					<div class="login-form">




						<div>
							Matching bots with users:


							<div class="generic-container">
								<%-- 	<%@include file="authheader.jsp" %> --%>

								<div class="well lead">User Registration Form</div>
								<form:form method="POST" modelAttribute="linkBot"
									class="form-horizontal">

									<div class="row">
										<div class="form-group col-md-12">
											<label class="col-md-3 control-lable" for="idBotL">bots</label>
											<div class="col-md-7">
												<form:select path="idBotL" items="${bots}" multiple="true"
													itemValue="id" itemLabel="idBot"
													class="form-control input-sm" />

											</div>
										</div>
									</div>
									<div class="row">
										<div class="form-group col-md-12">
											<label class="col-md-3 control-lable" for="idUsrL">user</label>
											<div class="col-md-7">
												<form:select path="idUsrL" items="${users}" multiple="true"
													itemValue="id" itemLabel="ssoId"
													class="form-control input-sm" />

											</div>
										</div>
									</div>

									<div class="row">
										<div class="form-actions floatRight">

											<input type="submit" value="Update"
												class="btn btn-primary btn-sm" /> 
											

										</div>
									</div>
								</form:form>
							</div>

					
						
					</div>
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