<%@page import="is203.se.Entity.Demographic"%>
<%
    
    Demographic user = (Demographic)(session.getAttribute("user"));
    if(user!=null){
        response.sendRedirect("home.jsp");
    }

%>