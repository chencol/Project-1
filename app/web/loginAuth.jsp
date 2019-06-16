<%-- 
    Document   : loginAuth
    Created on : Sep 11, 2017, 7:56:54 PM
    Author     : tanms.2015
--%>

<%@page import="is203.se.Entity.Demographic"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
  
    <%
        /*Delete the comment when all class ready*/
        
        Demographic user = (Demographic)session.getAttribute("user");
        if(user == null) {
            response.sendRedirect("loginView.jsp");
        }
    %>
