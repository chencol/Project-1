<%-- 
    Document   : TOPKPopularPlaceForm
    Created on : Sep 26, 2017, 4:10:15 PM
    Author     : Heuer
--%>
<%@include file="loginAuth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Top Popular Place</title>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <center>
            <div class ="container">
                <div class="row text-center well well-lg">
                    <h1>TopK Popular Place</h1>
                </div>
            </div>
            <h4>This functionality shows the top popular places in the whole SIS building at a specified time.</h4><br>
            <form action="TopKPopularPlaceController" method="get">
                Top:
                <select name="k">
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
                <input style="width:224px"  type="datetime-local" name="date" step="1" max="2017-06-06T00:00" min="2013-06-06T00:00"required>
                </br>
                </br>
                <input type="submit" value="Submit" class="btn btn-primary">
            </form>
        </center>
    </body>
</html>
