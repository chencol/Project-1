<%-- 
    Document   : BreakdownAdvancedView
    Created on : Oct 3, 2017, 10:02:04 PM
    Author     : tanms.2015
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="is203.se.BasicLocationReports.breakdown.Content"%>
<%@page import="is203.se.BasicLocationReports.breakdown.Breakdown"%>
<%@include file="loginAuth.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Breakdown Advanced View</title>
        <style>
            table, th, td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            th, td {
                padding: 5px;
                text-align: left;    
            }
        </style>
        
           
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <center>
             
            <% Breakdown breakdown1 = (Breakdown)request.getAttribute("breakdown"); %>
            <% int totalNumUsers = (Integer)request.getAttribute("totalNumUsers"); %>
            <% ArrayList<String> types = (ArrayList<String>)request.getAttribute("types"); %>

            <%
                int type1Length = 1;
                int type2Length = 1;
                //Level 2 size
                if(breakdown1.getContents().get(0).getBreakdown() != null) {
                    type1Length = breakdown1.getContents().get(0).getBreakdown().getContents().size();

                    //Level 3 size
                    if(breakdown1.getContents().get(0).getBreakdown().getContents().get(0).getBreakdown() != null) {
                        type2Length = breakdown1.getContents().get(0).getBreakdown().getContents().get(0).getBreakdown().getContents().size();
                        type1Length = type1Length * type2Length;     

                    }
                }
            %>
            <p>Total Users: <%= totalNumUsers %> </p>
            
            <br><br>
            
            <!--table style="width:20%"-->
            <table style='width:30%' class='table table-bordered'>
                <tr>
                    <% for(String type : types) { %>
                    <th><%=Character.toUpperCase(type.charAt(0)) + type.substring(1)%></th>
                    <th>No</th>
                    <th>%</th>
                    <% } %>
                </tr>
                <% for(Content content1 : breakdown1.getContents()) { %>
                    <tr>
                        <td rowspan = <%=type1Length%>> <%=content1.getHeader()%> </td>
                        <td rowspan = <%=type1Length%>> <%=content1.getCount()%> </td>
                        <td rowspan = <%=type1Length%>> <%=Math.round( 1.0 * content1.getCount() / totalNumUsers * 100) %> </td>
                    
                    
                    <% Breakdown breakdown2 = content1.getBreakdown(); %>
                    <% if(breakdown2 != null) { %>
                        <% for(Content content2 : breakdown2.getContents()) { %>
                            
                                <td rowspan = <%=type2Length%>> <%=content2.getHeader()%> </td>
                                <td rowspan = <%=type2Length%>> <%=content2.getCount()%> </td>
                                <td rowspan = <%=type2Length%>> <%=Math.round( 1.0 * content2.getCount() / totalNumUsers * 100) %> </td>
                            
                            
                            <% Breakdown breakdown3 = content2.getBreakdown(); %>
                            <% if(breakdown3 != null) { %>
                                <% for(Content content3 : breakdown3.getContents()) { %>
                                    
                                        <td> <%=content3.getHeader()%> </td>
                                        <td> <%=content3.getCount()%> </td>
                                        <td> <%=Math.round( 1.0 * content3.getCount() / totalNumUsers * 100) %> </td>
                                    </tr> <tr>
                                <% } %>
                            <% } else { %>
                                </tr> <tr>
                            <% } %>
                    <% } %>
                <% } %>
                </tr> </tr>
            <% } %>  
            </table>
        </center>
    </body>
</html>
