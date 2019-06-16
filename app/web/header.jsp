<%-- 
    Document   : myInclude
    Created on : Sep 24, 2017, 11:43:20 AM
    Author     : Heuer
--%>

<%@page import="is203.se.Entity.Demographic"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script src ="${pageContext.request.contextPath}/js/jquery-3.2.1.min.js"></script>
        <link href='${pageContext.request.contextPath}/css/bootstrap.css' rel='stylesheet'>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <% 
            Demographic u = (Demographic)(session.getAttribute("user"));
        %>
        <nav class="navbar navbar-inverse" style="border-top:0px; color:white">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand"><b>SLOCA </b></a>
                </div>
                <ul class="nav navbar-nav">
                    <li class="active"><a href="${pageContext.request.contextPath}/home.jsp"><span class="glyphicon glyphicon-home"></span> Home</a></li>
                    <li><a href=”#”  data-toggle="dropdown"><span class ="glyphicon glyphicon-map-marker"></span> Basic Location Reports<span class='caret'></span></a>
                        <ul class='dropdown-menu' role="menu">
                             <li><a href="${pageContext.request.contextPath}/breakdownForm.jsp">Breakdown by year, gender or school</a></li>
                             <li><a href="${pageContext.request.contextPath}/TopKPopularPlaceForm.jsp">Top popular places</a></li>
                             <li><a href="${pageContext.request.contextPath}/TopKCompanionView.jsp">Top companions</a></li>
                             <li><a href="${pageContext.request.contextPath}/TopKNextPlaceForm.jsp">Top next places</a></li>
                        </ul>
                    </li>
                    <li><a href="${pageContext.request.contextPath}/heatmapView.jsp"><span class="glyphicon glyphicon-globe"></span> Heatmap</a></li>
                    <li><a href="${pageContext.request.contextPath}/AGDView.jsp"><span class="glyphicon glyphicon-user"></span> Automatic Group Identification</a></li>
                    
                    <%
                        
                        if (u != null && u.getAdminStatus() == 1) {
                            out.println("<li><a href=' " + request.getContextPath() + "/bootstrapView.jsp" + "'" + "><span class='glyphicon glyphicon-open'></span>Bootstrap</a></li>");
                        }
                        
                    %>
                    
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="${pageContext.request.contextPath}/logoutController.jsp"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
                </ul>
            </div>
        </nav>
    </body>
    <!--script src='${pageContext.request.contextPath}/js/bootstrap.js'></script-->
    <script src='${pageContext.request.contextPath}/js/bootstrap.js'></script>
</html>
