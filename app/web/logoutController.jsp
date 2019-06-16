<%-- 
    Document   : processLogout
    Created on : Sep 11, 2017, 8:05:36 PM
    Author     : tanms.2015
--%>
<%@include file="loginAuth.jsp"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    session.invalidate(); 
    response.sendRedirect("loginView.jsp");
%>
