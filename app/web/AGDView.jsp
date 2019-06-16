
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="is203.se.BasicLocationReports.locationChunk.LocationGroup"%>
<%@page import="java.util.Map"%>
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
        <title>AGD</title>
        <style>
            table, th, td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            th, td {
                padding: 5px;
                text-align: center;    
            }
    </style>
    </head>
    
    <body>
        <center>
            <jsp:include page="header.jsp"></jsp:include>
            <div class ="container">
                    <div class="row text-center well well-lg">
                        <h1>Automatic Group Detection</h1>
                    </div>
            </div>

            <h4>This functionality will discover potential groups in the SIS building at a give date/time based on users' location traces.</h4></br>
            <div name="formContainer">
                <form method ="get" action="${pageContext.request.contextPath}/group_detect">
                    Date:
                    <!--The range of time can be set by the min and max-->
                    <input style="width:224px"  type="datetime-local" name="date" step="1" required>
                    <br/><br/>
                    <input type="submit" value="Submit" class="btn btn-primary">
                </form>
            </div>
                        
            <br><br>
                        
            <div name="display">
                <%  Integer totalUsers = (Integer)request.getAttribute("total-users");
                    Integer totalGroups = (Integer)request.getAttribute("total-groups");
                    ArrayList<LocationGroup> groups = (ArrayList<LocationGroup>)request.getAttribute("groups"); %>
                   
                    <% if(totalUsers != null && totalGroups != null && groups != null) { %>
                        <h2>Results</h2>
                        Number of users in SIS building: <%=totalUsers%> <br>
                        Total groups: <%=totalGroups%> <br>
                        Groups: <br>
                        <% for(int i = 0; i < groups.size(); i++) { %>
                            <%
                                LocationGroup group = groups.get(i);
                                int size = group.getSize();
                                int totalTimeSpent = group.getTotalTimeSpent();
                                ArrayList<Demographic> members = group.getMembers();
                                TreeMap<Integer, Long> locations = group.getLocations();
                            %>
                            <table>
                                <tr>
                                    <th colspan="2">Group <%=i+1%></th>
                                </tr>
                                <tr>
                                    <td>Size</td>
                                    <td><%=size%></td>
                                </tr>
                                <tr>
                                    <td>Total Time Spent</td>
                                    <td><%=totalTimeSpent%></td>
                                </tr>
                                <tr>
                                    <td>Members: </td>
                                    <td>
                                        <% for(Demographic demo : members) { %>
                                            <b>Email:</b> <%=demo.getEmail()%> <br> 
                                            <b>Mac-address:</b> <%=demo.getMacAddress()%> <br><br>
                                        <% } %>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Locations: </td>
                                    <td>
                                        <% for(Map.Entry<Integer, Long> entrySet : locations.entrySet()) { %>
                                            <b>Location:</b> <%=entrySet.getKey()%> <br> 
                                            <b>Time Spent:</b> <%=entrySet.getValue()%> <br><br>
                                        <% } %>
                                    </td>
                                </tr>
                            </table> <br>
                            
                        <% } %>
                    <% } %>  
            </div>
            <br>
        </center>
    </body>
</html>