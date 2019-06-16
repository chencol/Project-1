<%-- 
    Document   : TopKNextPlaceForm
    Created on : Sep 26, 2017, 7:06:13 PM
    Author     : Heuer
--%>
<%@include file="loginAuth.jsp" %>
<%@page import="is203.se.DAO.LocationLookupDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Top Next Place</title>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <center>
            <form action="TopKNextPlacesController" method="get">    
                <div class ="container">
                    <div class="row text-center well well-lg">
                        <h1>TopK Next Place</h1>
                    </div>
                </div>
                <h4>This functionality shows the top next popular places that users located at place A are likely to visit in the next 15 minutes.</h4><br>
                Top:
                <!--Set the default value as 3-->
                <select name="k" value="3">
                    <%
                        for(int i=1;i<=10;i++){
                            if(i==3){
                                out.println("<option value='" + i +"' selected>" + i +"</option>");
                            }
                            else{
                                out.println("<option value='" + i +"'>" + i +"</option>");
                            }
                        }
                    %>
                </select>
                <br>
                <br>
                Date:
                <!--The range of time can be set by the min and max-->
                <input style="width:224px"  type="datetime-local" name="date" step="1" max="2017-06-06T00:00" min="2013-06-06T00:00" required>
                <br>
                <br>
                SemanticPlace:
                <select name="origin">
                    <%
                        ArrayList<String> semanticPlaces = LocationLookupDAO.getAllSemanticPlaces();
                        if(semanticPlaces!=null){
                            for(String semanticPlace: semanticPlaces){
                                out.println("<option value='" + semanticPlace +"'>" + semanticPlace +"</option>");
                            }
                        }
                    %>
                </select>
                <br>
                <br>
                <input type="submit" value="Submit" class="btn btn-primary">
            </form>
        </center>
        
    </body>
</html>
