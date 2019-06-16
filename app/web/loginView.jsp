<!DOCTYPE html>
<%@include file="loginProtector.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>SLOCA-Location Analysis</title>
    <!-- Bootstrap core CSS -->
    <!--link href="css/bootstrap.css" rel="stylesheet"-->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/grayscale.css" rel="stylesheet">
    <link href="css/grayscale.min.css" rel="stylesheet">
    <script src="js/jquery-3.2.1.min.js"></script>
    <script src='js/bootstrap.js'></script>
  </head>

  <body id="page-top">

    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light fixed-top" id="mainNav">
        <div class="container" >
        <!--a class="navbar-brand js-scroll-trigger" href="#page-top">SMU Location Analysis</a-->
        <a href="#page-top" class="subTitle_1">SMU Location Analysis</a>
            <div class="collapse navbar-collapse" id="navbarResponsive">
              <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                  <a class="nav-link js-scroll-trigger" href="#about" id="subTitle_2">About SMU</a>
                </li>
              </ul>
            </div>
        </div>
    </nav>
    <header class="masthead">
      <div class="intro-body">
        <div class="container">
          <div class="row">
            <div class="col-lg-8 mx-auto">
                <h1 class="brand-heading">Locatio<a class="blinking">n</a> <a class="blinking">A</a>nalysis</h1>
              <p class="intro-text">SLOCA is an web application that can be used by any valid user to obtain diverse statistics of the locations of people inside the SIS building.
              <br><a style="color: #42dca3">Powered by G3T2 NULL.</a></p>
              <button id="button2" style="color:white; opacity: 0.8; border-color: black; background-color: black" class="btn btn-primary" data-toggle="modal" data-target="#loginModal">Log in</button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- About Section -->
    <section id="about" class="content-section text-center">
      <div class="container">
        <div class="row">
          <div class="col-lg-8 mx-auto">
            <h2>About SMU</h2>
            <p>The Singapore Management University is an autonomous university in Singapore that specialises in business and management studies. The university provides an American-style education modelled by the Wharton School of the University of Pennsylvania.
            </p>
          </div>
        </div>
      </div>
    </section>
    <div>
      <div class="container text-center">
        <p>Copyright &copy SMU G3T2 Software Engineering 2017</p>
      </div>
    </div>
    <!--script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script-->
    <!------------------------------------------------------------------->
    <script src="js/jquery-3.2.1.min.js"></script>
    <!-------------------------------------------------------------------!>
    <!-- Bootstrap core JavaScript -->
    <!--script src="vendor/jquery/jquery.min.js"></script-->
    <script src="js/bootstrap.bundle.min.js"></script>

    <!-- Plugin JavaScript -->
    <script src="js/jquery.easing.min.js"></script>
    <!-- Custom scripts for this template -->
    <script src="js/grayscale.min.js"></script>
    <div style="width: 500px; margin: 100px auto 0 auto; opacity:1;" >
        <div id="container">
            <div class="row modal fade" id="loginModal" role="dialog">
                <div class="modal-dialog" role="document">
                    <div class="modal-content" style="background-color: #0c1c23" >
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" style="color:white">&times;</button>
                        </div>
                        <div class="modal-body">
                            <div id="error" style="color:red">
                            </div>
                            <!--form method="post" action="loginController.jsp"-->
                                <div>
                                    <label for="inputEmail">Email</label>
                                    <input name="email" type="text" class="form-control" id="email" placeholder="e.g john.2016@sis.smu.edu.sg" required>
                                </div>
                                <div>
                                    <label for="inputPassword">Password</label> 
                                    <input id = "password" name="password" type ="password" class="form-control" id="password" placeholder="e.g ********** " required>
                                </div><br>
                                <div class="text-center">
                                    <button id="submitButton" value="Submit" style="color: #42dca3; background-color: transparent; border-color: #42dca3 ; border: 1px solid " onclick="checking()">Submit</button>
                                </div>
                            <!--/form-->
                        </div>
                        
                        <div class="modal-footer">
                            <br>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="js/processLogin.js"></script>
    </body>
</html>
