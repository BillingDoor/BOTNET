<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
   <link rel="icon" type="image/x-icon" href="/images/favicon.ico">
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
   <title>&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;&#9618;</title>
   
   <!-- Add to homescreen for Chrome on Android -->
    <meta name="mobile-web-app-capable" content="yes">

    <!-- Add to homescreen for Safari on iOS -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-title" content="Material Design Lite">

    <!-- Tile icon for Win8 (144x144 + tile color) -->
    <meta name="msapplication-TileImage" content="/images/touch/ms-touch-icon-144x144-precomposed.png">
    <meta name="msapplication-TileColor" content="#3372DF">
    
   <script src="https://storage.googleapis.com/code.getmdl.io/1.0.4/material.min.js"></script>
   <link href="<c:url value='/css/app.css' />" rel="stylesheet"></link>
   <link rel="stylesheet" href="https://storage.googleapis.com/code.getmdl.io/1.0.4/material.red-purple.min.css" /> 
   <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
   <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
	<link href='https://fonts.googleapis.com/css?family=Roboto:400,500,300,100,700,900' rel='stylesheet' type='text/css'>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
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
			<header>
				<img width="20%" src="/images/bitskull.jpg">
			</header>
			<nav class="mdl-navigation">
		  <a class="mdl-navigation__link mdl -navigation__link--current" href="index">
                <i class="material-icons" role="presentation">dashboard</i>
                Dashboard
            </a>
            <a class="mdl-navigation__link" href="showbot">
                <i class="material-icons" role="presentation">person</i>
                Bot List
            </a>
            
                      
        <a class="mdl-navigation__link" href="attack">
                <i class="material-icons" role="presentation">&#9760;</i>
                Attack
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
	<div class="general-card">
	<div class="general-form">
		
	<div class="generic-container">
	<div><p>Lista Bot</p></div>

			<tbody>
				<c:forEach items="${bots}" var="bot">
					<tr>
						<td>IP: </td><td>${bot.ip}</td>
						<td>Architecture: </td><td>${bot.arch}</td>
						<td>OS: </td><td>${bot.os}</td>
						<td>Version: </td><td>${bot.ver}</td>
						<td>Username: </td><td>${bot.usernameOS}</td>
					</tr>
				</c:forEach>
	    		</tbody>

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