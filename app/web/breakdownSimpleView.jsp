<%-- 
    Document   : BreakdownSimpleView
    Created on : Oct 3, 2017, 9:58:19 PM
    Author     : tanms.2015
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="is203.se.BasicLocationReports.breakdown.Content"%>
<%@page import="is203.se.BasicLocationReports.breakdown.Breakdown"%>
<%@include file="loginAuth.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Breakdown Simple View</title>
        <style>
            table, th, td {
                text-align: center;
            }
            
            caption {
                display: table-caption;
                font-weight: bold;
                font-size: xx-large;
            }
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        
        <center>
            <% Breakdown breakdown1 = (Breakdown)request.getAttribute("breakdown1"); %>
            <% int totalNumUsers = (Integer)request.getAttribute("totalNumUsers"); %>
            
            <p>Total Users: <%= totalNumUsers %> </p>
            
            <br><br>
            <table style='width:30%' class='table table-bordered'>
            <!--table style="width:20%"-->
                <% if(breakdown1 != null) { %>
                    <caption>
                        <!--th> <!--%= breakdown1.getType() %--> </th-->
                         <%= breakdown1.getType().substring(0,1).toUpperCase()+ breakdown1.getType().substring(1)%> 
                    </caption>
                         <tr><td><b><%= breakdown1.getType().substring(0,1).toUpperCase()+ breakdown1.getType().substring(1)%></b></td>
                        <% for(Content contents : breakdown1.getContents()) { %>
                            <td> <%= contents.getHeader() %> </td>
                        <% } %>
                    </tr>
                    <tr> <td><b>Number of people</b></td>
                        <% for(Content contents : breakdown1.getContents()) { %>
                            <td> <%= contents.getCount() %> </td>
                        <% } %>
                    </tr>
                    <tr> <td><b>Percentage of people</b></td>
                        <% for(Content contents : breakdown1.getContents()) { %>
                            <td> <%=Math.round( 1.0 * contents.getCount() / totalNumUsers * 100) + "%" %> </td>
                        <% } %>
                    </tr>
                <% } %>
            </table>

            <br><br>

            <% Breakdown breakdown2 = (Breakdown)request.getAttribute("breakdown2"); %>
            <!--table style="width:20%"-->
            <table style='width:30%' class='table table-bordered'>
                <% if(breakdown2 != null) { %>
                    <caption> 
                        <!--th> <!--%= breakdown2.getType() %--> 
                        <%= breakdown2.getType().substring(0,1).toUpperCase()+ breakdown2.getType().substring(1)%>
                    </caption>
                    <tr>
                        <td><b><%= breakdown2.getType().substring(0,1).toUpperCase()+ breakdown2.getType().substring(1)%></b></td>
                        <% for(Content contents : breakdown2.getContents()) { %>
                            <td> <%= contents.getHeader() %> </td>
                        <% } %>
                    </tr>
                    <tr>
                        <td><b>Number of people</b></td>
                        <% for(Content contents : breakdown2.getContents()) { %>
                            <td> <%= contents.getCount() %> </td>
                        <% } %>
                    </tr>
                    <tr>
                        <td><b>Percentage of people</b></td>
                        <% for(Content contents : breakdown2.getContents()) { %>
                            <td> <%= Math.round( 1.0 * contents.getCount() / totalNumUsers * 100) + "%"%> </td>
                        <% } %>
                    </tr>
                <% } %>
            </table>

            <br><br>

            <% Breakdown breakdown3 = (Breakdown)request.getAttribute("breakdown3"); %>
            <!--table style="width:20%"-->
            <table style='width:30%' class='table table-bordered'>
                <% if(breakdown3 != null) { %>
                    <caption>
                        <%= breakdown3.getType().substring(0,1).toUpperCase()+breakdown3.getType().substring(1) %>
                    </caption>
                    <tr>
                        <td><b><%= breakdown3.getType().substring(0,1).toUpperCase()+breakdown3.getType().substring(1) %></b></td>
                        <% for(Content contents : breakdown3.getContents()) { %>
                            <td> <%= contents.getHeader() %> </td>
                        <% } %>
                    </tr>
                    <tr>
                        <td><b>Number of people</b></td>
                        <% for(Content contents : breakdown3.getContents()) { %>
                            <td> <%= contents.getCount() %> </td>
                        <% } %>
                    </tr>
                    <tr>
                        <td><b>Percentage of people</b></td>
                        <% for(Content contents : breakdown3.getContents()) { %>
                        <td> <%= Math.round( 1.0 * contents.getCount() / totalNumUsers * 100)  + "%" %> </td>
                        <% } %>
                    </tr>
                <% } %>
            </table>
        </center>
    </body>
</html>
