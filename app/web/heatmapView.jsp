<%-- 
    Document   : heatmapView
    Created on : Oct 12, 2017, 12:28:28 PM
    Author     : tanms.2015
--%>

<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.HashMap"%>
<%@page import="is203.se.Heatmap.LocationDensity"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="loginAuth.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Heatmap</title>
        <style>
            table, th, td {
                text-align: center;
            }
        </style>
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>

        <div>
            <center>
                <div class ="container">
                    <div class="row text-center well well-lg">
                        <h1>HeatMap</h1>
                    </div>
                </div>
                <h4>This functionality will allow a user to view the crowd density of a specified floor in the SIS building, given a particular date and time. </h4></br>
                
                <form action="${pageContext.request.contextPath}/heatmap" method="get">
                    Level:
                    <select name="floor">
                        <% for(int i=0;i<=5;i++){
                                if(i==3){           %>
                                    <option value=<%=i%> selected> <%=i%> </option>"
                             <% } else { %>
                                    "<option value=<%=i%>> <%=i%> </option>"
                             <% } %>                 
                         <% }  %>
                    </select>
                    
                    <br><br>
                    
                    Date:
                    <!--The range of time can be set by the min and max-->
                    <input style="width:224px"  type="datetime-local" name="date" step="1" max="2017-06-06T00:00" min="2013-06-06T00:00" required>
                    
                    <br><br>
                    
                    <input type="submit" value="Submit" class="btn btn-primary">
                </form>
            </center>  
        </div>
        
        <div>
            <br>
            <center>
                <% HashMap<String, LocationDensity> heatmap = (HashMap)request.getAttribute("heatmap"); %>
                <% if(heatmap != null) { 
                    out.println("<table style='width:40%'' class='table table-bordered'>");
                %>
                        <tr>
                            <th class ='text-center'>Semantic Place</th>
                            <th class ='text-center'>Number of people</th>
                            <th class ='text-center'>Density</th>
                        </tr>
                        <% for(Entry<String, LocationDensity> entrySet : heatmap.entrySet()) {
                            LocationDensity locDen = entrySet.getValue();
                            String semPlace = locDen.getSemanticPlace();
                            int numPeople = locDen.getNumberOfPeople();
                            int density = locDen.getDensity(); %>

                            <tr>
                                <td><%=semPlace%></td>
                                <td><%=numPeople%></td>
                                <td><%=density%></td>
                            </tr>
                        <% } %>
                    </table>
                <% } %>
            </center>
        </div>
        
        
        
    </body>
</html>
