<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page import="is203.se.Entity.Demographic"%>
<%@page import="is203.se.JsonAuthentication.TokenServlet"%>
<%@page import="is203.JWTUtility"%>
<%@include file="adminAuth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%  //getting token
    String token = JWTUtility.sign(TokenServlet.SHARED_SECRET, "admin");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bootstrap</title>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <center>
            <div class ="container">
                <div class="row text-center well well-lg">
                    <h1>Bootstrap View</h1>
                </div>
            </div>
            
            <h4> Create database function involves clearing all existing data in the database and replacing them with values supplied from a data file</h4>
            <h4> Update database function involves adding the values from a data file to the current database</h4><br>
            <table>
                <tr>
                    <th>Create database</th>
                    <th>Update database</th>
                </tr>
                <tr>
                    <td>
                        <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/json/bootstrap">
                            Select file to upload: <input type="file" name="bootstrap-file" /><br />
                            <input type="text" name="token" value="<%=token%>" hidden>
                            <input type="submit" name="button" value="Bootstrap"  class="btn btn-secondary"/>
                        </form>
                    </td>
                    <td>
                        <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/json/update">
                            Select file to upload: <input type="file" name="bootstrap-file" /><br />
                            <input type="text" name="token" value="<%=token%>" hidden>
                            <input type="submit" name="button" value="Bootstrap" class="btn btn-secondary" / >
                        </form>
                    </td>
                </tr>
            </table>
                
        </center>
                
        <div style="margin-left:30px">
            <% JSONObject result = (JSONObject)request.getAttribute("result"); %>
            <% if(result != null) {%>
                <h2>Number of Records Loaded </h2>
                <% JSONArray numLoaded = (JSONArray)result.get("num-record-loaded"); %>
                <%
                    JSONObject numLoaded1 = null;
                    JSONObject numLoaded2 = null;
                    JSONObject numLoaded3 = null;
                %>
                <%
                    for(int i = 0; i < numLoaded.size(); i++) {
                        if(i == 0) {
                            numLoaded1 = (JSONObject)numLoaded.get(0);
                                                    } else if (i == 1) {
                            numLoaded2 = (JSONObject)numLoaded.get(1);
                        } else if (i == 2) {
                            numLoaded3 = (JSONObject)numLoaded.get(2);
                        }
                    }
                    JSONObject[] numLoadedArray = {numLoaded1, numLoaded2, numLoaded3};
                %>
                <%  out.println("<table style='width:30%'' class='table table-bordered'>");
                    out.println("<tr>");
                    out.println("<th class ='text-center'>File</th>");
                    out.println("<th class ='text-center'>Number of Records Loaded</th>");
                    out.println("</tr>");
                    for(JSONObject numLoadedSub : numLoadedArray) {
                        if(numLoadedSub != null) {
                            for(Entry<String, Object> entry : numLoadedSub.entrySet()) {
                                String header = entry.getKey();
                                header = header.charAt(0) + header.substring(1);
                                Integer value = (Integer)entry.getValue();
                                out.println("<tr>");
                                out.println("<td class ='text-center'>" + header + "</td>");
                                out.println("<td class ='text-center'>" + value + "</td>");
                                out.println("</tr>");
                                %>
                                <!--%=header%>: <!--%=value%--> <!--br-->
                         <% }
                        %>
                     <%} %>
                <%}%>
            </table>

                <br><br>
                
                <% JSONArray error = (JSONArray)result.get("error"); %>
                <% if(error != null) { %>
                    <h2>Errors found: </h2>
                    <h3>demographics.csv</h3>
                    <% for(Object obj : error) {
                            JSONObject jsonObj = (JSONObject)obj;
                            String file = (String)jsonObj.get("file");
                            Integer line = (Integer)jsonObj.get("line");
                            JSONArray message = (JSONArray)jsonObj.get("message");
                            if(file.contains("demographic")) {      %>
                            Line <%=line%>:
                                <%=message.toJSONString().replace("[","").replace("]","")%> <br> <br>
                         <% } %>
                    <% } %>

                    <br>

                    <h3>location-lookup.csv</h3>
                    <% for(Object obj : error) {
                            JSONObject jsonObj = (JSONObject)obj;
                            String file = (String)jsonObj.get("file");
                            Integer line = (Integer)jsonObj.get("line");
                            JSONArray message = (JSONArray)jsonObj.get("message");
                            if(file.contains("location-lookup")) {      %>
                                Line <%=line%>:
                                <%=message.toJSONString().replace("[","").replace("]","")%> <br> <br>
                         <% } %>
                    <% } %>

                    <br>

                    <h3>location.csv</h3>
                    <% for(Object obj : error) {
                            JSONObject jsonObj = (JSONObject)obj;
                            String file = (String)jsonObj.get("file");
                            Integer line = (Integer)jsonObj.get("line");
                            JSONArray message = (JSONArray)jsonObj.get("message");
                            if(file.contains("location.csv")) {      %>
                                Line <%=line%>:
                                <%=message.toJSONString().replace("[","").replace("]","")%> <br> <br>
                         <% } %>
                    <% } %>
                <% } %>
            <% } %>
        </div>
    </body>
</html>