<%-- 
    Document   : home
    Created on : Sep 11, 2017, 7:55:40 PM
    Author     : tanms.2015
--%>
<%@include file="loginAuth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link href='css/bootstrap.css' rel='stylesheet'>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home Page</title>
        <style>
            #icon1{
                position:relative;
                top:10px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <br>
        <br>
        
        <div class ="container">
            <div class="row text-center well well-lg">
                <h1>
        <% 
            Demographic u = (Demographic)(session.getAttribute("user"));
            if(u!=null){
                String name = u.getName();
                out.println("Welcome " + name);
            }
            
            //boolean isAdmin = (User)session.getAttribute("user").getAdmin();
        %>
                </hl>
            </div>
        <div class ="row">
            <div class ="col-xs-3">
                <h3><div class="glyphicon glyphicon-map-marker"></div>Basic Location Reports </h3>
                <p>This functionality will allow a user to see four different types of basic location reports on a given date and time. </p>
                <ul style="list-style: none; align:left;padding: 0px">
                    <li><a href='breakdownForm.jsp'>Break Down By Year and Gender</a></li>
                    <li><a href='TopKPopularPlaceForm.jsp'>TopK Popular Place</a></li>
                    <li><a href='TopKCompanionView.jsp'>TopK Companion</a></li>
                    <li><a href='TopKNextPlaceForm.jsp'>TopK Next Place</a></li>
                </ul>
            </div>
            <div class="col-xs-3">
                <h3><div class="glyphicon glyphicon-globe"></div><a href="heatmapView.jsp">Heatmap </a></h3>
                <p>This functionality will allow a user to view the crowd density of a specified floor in the SIS building, given a particular date and time. </p>
            </div>
            <div class="col-xs-3">
                <h3><div class="glyphicon glyphicon-user"></div><a href="AGDView.jsp">Automatic Group Detection </h3></a>
                <p>This functionality will discover potential groups in the SIS building at a give date/time based on users' location traces. </p>
            </div>
            <div class="col-xs-3">
                <h3><div class='glyphicon glyphicon-open'></div>BootStrap</h3>
                <p>The administrator can bootstrap the SLOCA system with location & demographics data. </p>
                <%if (u != null && u.getAdminStatus() == 1) {
                        out.println("<li style='list-style: none'><a href='bootstrapView.jsp'>BootStrap</a></li>");
                    }
                %>
            </div>

    </div>
    </body>
    
    
</html>
