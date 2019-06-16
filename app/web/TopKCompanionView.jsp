<%-- 
    Document   : TopKCompanionForm
    Created on : Sep 26, 2017, 4:10:40 PM
    Author     : Heuer
--%>
<%@page import="is203.se.BasicLocationReports.topKCompanions.Companion"%>
<%@include file="loginAuth.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Top Companion</title>
        <style>
            table, th, td {
                text-align: center;
            }
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <center>
            <% 
                ArrayList<String> errorMsg = (ArrayList<String>)(request.getAttribute("errMsgs"));
                if(errorMsg != null) {
                    for(String str: errorMsg) { %>
                        <a id="error"> <%=str%></a>     
            <%      }
                } %>
                <form method ="get" action="${pageContext.request.contextPath}/top-k-companions">
                <div class ="container">
                    <div class="row text-center well well-lg">
                        <h1>TopK Companions</h1>
                    </div>
                </div>
                <h4>This functionality shows the top other users who were co-located with a specified user (using MAC address) in a specified query window.</h4><br>
                Top:
                <!--Set the default value as 3-->
                <select name="k" value='3'>
                <% for(int i=1;i<=10;i++) {  %>
                <% if(i!=3){ %>
                        <option value=<%=i%>> <%=i%> </option>
                    <% } else{ %>
                        <option value=<%=i%> selected><%=i%> </option>;
                    <% } %>
                <% } %>
                </select>
                <br>
                <br>
                User:
                <input type="text" name="macAddress">

                <br>
                <br>
                Date:
                    <!--The range of time can be set by the min and max-->
                    <input style="width:224px"  type="datetime-local" name="date" step="1" max="2017-06-06T00:00" min="2013-06-06T00:00" >
                    </br>
                    </br>

                    <input type='submit' value='Submit' class="btn btn-primary"></input>
                </form>
        </center>
                
        <br>        
                
        <center>
            <% ArrayList<Companion> companions = (ArrayList<Companion>)request.getAttribute("companions"); %>
            <% if(companions != null) {%>
                <table style='width:30%' class='table table-bordered'>
                    <tr>
                        <th class ='text-center'>Rank</th>
                        <th class ='text-center'>Companion</th>
                        <th class ='text-center'>Mac-Address</th>
                        <th class ='text-center'>Time-Together</th>
                    </tr>
                <% for(Companion companion : companions) { %>
                    <tr>
                        <td><%= companion.getRank() %></td>
                        <td><%= companion.getEmail()%></td>
                        <td><%= companion.getMacAddress()%></td>
                        <td><%= companion.getTimeTogether()%></td>
                    </tr>
                <% } %>
                </table>
            <% } %>
        </center>
    </body>
</html>
