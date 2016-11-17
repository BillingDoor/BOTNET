<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<title>Index</title>
	
	<link href="/resources/quakeSlider/css/quake.slider.css" rel="stylesheet" type="text/css" />
    <link href="/resources/quakeSlider/skins/dark-room/quake.skin.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="/resources/css/bootstrap.css" type="text/css" media="screen">
	
	<script type="text/javascript" src="/resources/js/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="/resources/js/script_nav.js"></script>
	<script type="text/javascript" src="/resources/js/script_tab.js"></script>
	<script type="text/javascript" src="/resources/js/bootstrap.js"></script>
    <script src="/resources/quakeSlider/js/quake.slider-min.js" type="text/javascript"></script>
	
	<script>
$(document).ready(function () {
	console.log("entrato in quake");
	$('.quake-slider').quake({
	            thumbnails: true,
                animationSpeed: 500,
                applyEffectsRandomly: true,
                navPlacement: 'inside',
                navAlwaysVisible: true,
                captionOpacity: '0.3',
                captionsSetup: [
                                 {
                                     "orientation": "top",
                                     "slides": [0, 1],
                                     "callback": captionAnimateCallback
                                 },
                                  {
                                      "orientation": "left",
                                      "slides": [2, 3],
                                      "callback": captionAnimationCallback1
                                  }
                                  ,
                                  {
                                      "orientation": "bottom",
                                      "slides": [4, 5],
                                      "callback": captionAnimateCallback
                                  }
                                  ,
                                  {
                                      "orientation": "right",
                                      "slides": [6, 7],
                                      "callback": captionAnimationCallback1
                                  }
                                ]

            });
			console.log("uscito da quake");
			
			});
			
            function captionAnimateCallback(captionWrapper, captionContainer, orientation) {
                captionWrapper.css({ left: '-990px' }).stop(true, true).animate({ left: 0 }, 500);
                captionContainer.css({ left: '-990px' }).stop(true, true).animate({ left: 0 }, 500);
            }
            function captionAnimationCallback1(captionWrapper, captionContainer, orientation) {
                captionWrapper.css({ top: '-330px' }).stop(true, true).animate({ top: 0 }, 500);
                captionContainer.css({ top: '-330px' }).stop(true, true).animate({ top: 0 }, 500);
            }

</script>
</head>

 <body data-spy="scroll" data-target="#navbar" data-offset='0'>
  <!--==============================header=================================-->
        <header>
		<div id="navbar" class="navbar navbar-fixed-top">	
			<nav class="navbar-inner" style="padding:0.1%">
				<a class="brand">IngSoftware</a>
				<ul class="nav" id="navLink">
					<!-- Content NavBar -->
				</ul>
			<!-- Form da disattivare quando siamo loggati   -->
			<a href="#myModal" role="button" class="btn pull-right" data-toggle="modal" style="margin-left: 5px;margin-right: 5px;">Register</a>
			<!-- <button type="submit" class="btn pull-right" style="margin-left: 5px;margin-right: 5px;">Register</button> -->
			<form class="navbar-form pull-right"  action="<c:url value='j_spring_security_check'/>" method="post" >
              <input class="span2" type="text"  placeholder="E-mail" name="j_username" id="j_username"size="30" maxlength="40"/>
              <input class="span2" type="password" placeholder="Password" name="j_password" id="j_password" size="30" maxlength="32" />
              <button type="submit" class="btn" value="Login">Sign in</button>
            </form>   

<!-- Modal -->
<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="myModalLabel">Pagina di Registrazione</h3>
  </div>
  <div class="modal-body">
    
	<script>
		$('.modal-body').load("registrazione", function(responseTxt,statusTxt,xhr){
		 if(statusTxt=="success"){
		
			$('.modal-body').fadeIn('fast');
			}
			if(statusTxt=="error")alert("Error: "+xhr.status+": "+xhr.statusText);
		});
	</script>
  </div>
  
</div>
		
			</nav>
		</div>
		
        </header>
    <!--==============================content================================ -->
	<div id="loader">

<div class="container" id="Home" style="margin-top: 6%;">	
		 <!-- Quake Image Slider -->
    <div class="quake-slider">
        <div class="quake-slider-images">
		 <img src="<%=request.getContextPath()%>/resources/quakeSlider/images/png/banner.png" alt="" /> 
		 <img src="<%=request.getContextPath()%>/resources/quakeSlider/images/png/slider-bg1.jpg" alt="" /> 
		
           
        </div>
        <div class="quake-slider-captions">
            <div class="quake-slider-caption">
                This is a caption. You can put HTML here.
            </div>
            <div class="quake-slider-caption">
                This is a caption. You can put HTML here.
            </div>
            <div class="quake-slider-caption">
                This is a caption. You can put HTML here.
            </div>
            <div class="quake-slider-caption">
                This is a caption. You can put HTML here.
            </div>
            <div class="quake-slider-caption">
                This is a caption. You can put HTML here.
            </div>
        </div>
    </div>
    <!-- /Quake Image Slider -->
    

      <!-- Example row of columns -->
      <div class="row-fluid">
        <div class="span12">
          <h2 id="Features">Features</h2>
          <h4>Caratteristiche dell'applicazione </h4>
		  <div class="row-fluid">
			<div class="span6"><figure>
				<img src="<%=request.getContextPath()%>/resources/img/page1-img1.jpg" class="img-polaroid">
				</figure>
			</div>
			
			<div class="span6"><h5>Trova tutto ciò che ti interessa sul tuo telefono!</h5><p>
			L'applicazione è pensata per farti trovare ciò che ti interessa dovunque tu ti trovi, anche in una città lontana
			o dietro la porta di casa. Hai la possibilità di visualizzarli sulla mappa e utilizzare lo street view per non
			perderti in nessun momento.</p>
			</div>
        </div>
		
		<div class="row-fluid">
			<div class="span6"><h5>Crei i tuoi personali punti di interesse!</h5>
			<p>Vuoi far sapere a tutti dove si trova il tuo ristorante preferito? Vuoi ricordarti dove hai trascorso
			la tua ultima uscita?<br> Con l'applicazione OgGì sarai in grado di poter creare i tuoi punti di interesse e condividerli con i tuoi
			amici o tenerli per te per non dimenticare in nessun momento un luogo particolare</p>
	</div>
			<div class="span6"><figure>
				<img src="<%=request.getContextPath()%>/resources/img/page1-img1.jpg" class="img-polaroid">
				</figure>
			</div>
        </div>
		</div>
		  
		<div class="row-fluid">
			<div class="span12">
				<h2 id="Support">Support</h2>
				<h4>Guida all'Installazione </h4>
				<div class="row-fluid">
		
			<div class="span6 offset2"><h5>L'installazione dell'applicazione OgGì è molto semplice!</h5>
			<p>- Basta scaricare comodamente l'applicazione dal GooglePlayStore sul tuo smartphone Android<br>
			- Una volta installata, registrarsi sul sito dell'applicazione da qualsiasi browser web!<br>
			- Dopodichè potrai subito usufruire dei nostri fantastici servizi approvati dalla comunità europea e da Lupo</p>
			</div>
        </div> 
       </div>
	   </div>
	<div class="row-fluid">
	<div class="span12">
          <h2 id="Download">Download</h2>
		  <div class="row-fluid">		
			<div class="span6"><h5>Clicca sul pulsante per eseguire il dowonload dell'applicazione</h5><p>
				<p><a class="btn btn-info btn-large btn-block" href="#">Download App &raquo;</a></p>
			</div>
		</div>
	</div>
	</div>
	
        <!-- <div class="span4">
          <h2>Support</h2>
          <p>Support e changelog </p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
       </div>
        <div class="span4">
          <h2>Download</h2>
		  <p>Scaricare la nostra applicazione ÃƒÂ¨ semplicissimo: </p>
          <p><a class="btn" href="#">Download &raquo;</a></p>
        </div>   ---->
      </div>

      <hr>

      <footer>
        <p>&copy; Company 2013</p>
      </footer>

    </div> <!-- /container -->  
		
	</div>

  
   
  
          
  </body>
</html>
