<%-- 
    Document   : TopKPopularPlaceView
    Created on : Sep 29, 2017, 6:31:06 PM
    Author     : Heuer
--%>
<%@page import="is203.se.BasicLocationReports.topKPlaces.JsonPopularPlace"%>
<%@page import="is203.se.BasicLocationReports.topKPlaces.PopularPlace"%>
<%@include file="loginAuth.jsp" %>
<%@page import="java.util.ArrayList"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Top-k Next Places</title>
        <style>
            table, th, td {
                text-align: center;
            }
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <br>
        <br>       
        
        <%  JSONObject result = (JSONObject)request.getAttribute("result");
            Integer totalUsers = (Integer)result.get("total-users");
            Integer nextPlaceUsers = (Integer)result.get("total-next-place-users");
            JSONArray results = (JSONArray)result.get("results");
        %>
        <% if(result != null && totalUsers != null) { %>
            <center>
                <h1>Top-k Next Places</h1>
                <h2>User Analysis</h2>
                <table style='width:30%' class='table table-bordered'>
                    <tr>
                        <th class = 'text-center'>Total users</th>
                        <th class ='text-center'>Total next-place users</th>
                    </tr>
                    <tr>
                        <td><%=totalUsers%></td>
                        <td><%=nextPlaceUsers%></td>
                    </tr>
                </table>

                <br><br>

                <h2>Location Analysis</h2>
                <table style='width:50%' class='table table-bordered'>
                    <tr>
                        <th class ='text-center'>Rank</th>
                        <th class ='text-center'>Semantic_Place</th>
                        <th class ='text-center'>Count</th>
                        <th class ='text-center'>%</th>
                    </tr>
                    <%//results will never be null because it has been initalized in the TopKNextPlaceController,
                      //using results != null will always be true, therefore we have to check using .toString
                     if(!results.toString().equals("[null]")) { %>
                        <% for(Object obj : results) { %>
                            <%  PopularPlace popularPlace = (PopularPlace)obj; 
                                int rank = popularPlace.getRank();
                                int count = popularPlace.getCount();
                                ArrayList<String> semanticPlaces = popularPlace.getSemantic_place(); 
                            %>
                            <% for(String semanticPlace : semanticPlaces) { %>
                                <tr>
                                    <td><%=rank%></td>
                                    <td><%=semanticPlace%></td>
                                    <td><%=count%></td>
                                    <td><%=Math.round(count*100/totalUsers)%></td>
                                </tr>
                            <% } %>

                        <% } %>
                    <% } %>
                </table>
            </center>
        <% } %>
            
    </body>
</html>
