<%-- 
    Document   : TopKPopularPlaceView
    Created on : Sep 29, 2017, 6:31:06 PM
    Author     : Heuer
--%>
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
        <title>Top Popular Place</title>
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
        <%
            JSONObject js = (JSONObject)(request.getAttribute("result"));
            if(js!=null){
                out.println("<center>");
                out.println("<table style='width:30%'' class='table table-bordered'>");
                out.println("<tr>");
                out.println("<th class ='text-center'>Rank</th>");
                out.println("<th class ='text-center'>Semantic_Place</th>");
                out.println(" <th class ='text-center'>Count</th>");
                out.println("</tr>");
                Object o = js.get("result");
                JSONArray results = (JSONArray)o;
                if(results!=null){ 
                    for(int i=0;i<results.size();i++){
                        out.println("<tr>");
                        PopularPlace popularPlace = (PopularPlace)results.get(i);                   
                        if(popularPlace!=null){
                            out.println("<td>" + popularPlace.getRank() + "</td>");
                            ArrayList<String> places = popularPlace.getSemantic_place();            
                            out.println("<td>");
                            for(int t=0;t<places.size();t++){
                                String place = places.get(t);
                                if((t==0&&places.size()==1)||(t==places.size()-1)){
                                    out.println(place);
                                }
                                else{
                                    out.println(place + ",");
                                }

                            }
                            out.println("</td>");
                            out.println("<td>" + popularPlace.getCount() + "</td>");

                            out.println("</tr>");
                        }
                    }
                }
                out.println("</table>");
                out.println("</center>");
                out.println("</body>");
                out.println("</html>");
            }
        %>                        

