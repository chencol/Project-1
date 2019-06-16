<%-- 
    Document   : adminAuth
    Created on : Sep 11, 2017, 8:11:03 PM
    Author     : tanms.2015
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String isAdmin = (String)(session.getAttribute("adminStatus")); //possible null
        if(!(isAdmin != null && isAdmin.equals("yes"))) { 
            response.sendRedirect("home.jsp");
        }
    %>
