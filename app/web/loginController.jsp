<%@page import="is203.se.DAO.DemographicDAO"%>
<%@page import="is203.se.Entity.Demographic"%>
<%@page import="java.util.*" %>
<%   
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    System.out.println("Check point1");
    Demographic u = null;
    //Demographic u = DemographicDAO.getDemographicByUsername(email, password);
    System.out.println("Check point2");
    if(email!=null&&password!=null){
        if(email.equals("admin")&&(password.equals("admin"))){
            u = new Demographic("ttt","admin","admin","admin",'M',1);
        }
    }
    System.out.println("Check point3");
    if(u!=null){     
        session.setAttribute("user",u);
        if(u.getAdminStatus()==Demographic.ADMIN_TRUE){
            session.setAttribute("adminStatus","yes");
        }
        else{
            session.setAttribute("adminStatus","no");
        }
        out.print("succeed");
    }else{
        out.print("Wrong username or password");
    }
%>

