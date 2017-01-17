<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <link rel="icon" type="image/x-icon" href="/images/favicon.ico">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>&#9617;&#9617;&#9617;&#9617;&#9617;&#9617;&#9617;&#9617;&#9617;&#9617;</title>

    <!-- Add to homescreen for Chrome on Android -->
    <meta name="mobile-web-app-capable" content="yes">


    <!-- Add to homescreen for Safari on iOS -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-title" content="Material Design Lite">


    <!-- Tile icon for Win8 (144x144 + tile color) -->
    <meta name="msapplication-TileImage" content="/images/touch/ms-touch-icon-144x144-precomposed.png">
    <meta name="msapplication-TileColor" content="#3372DF">

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

<main class="mdl-layout__content"></main>
	<font color="white"><p align="right"><font size="15"><i class="fa fa-hand-spock-o" aria-hidden="true"></i></font> <br>Control panel: <br><i>check your own business!</i></p></font>
</div>

<!-- inject:js -->
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
<!-- endinject -->

</body>
</html>
