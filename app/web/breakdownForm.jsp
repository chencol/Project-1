<%@page import="java.util.ArrayList"%>
<%@include file="loginAuth.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Breakdown</title>
        <style>
            #error{
                color:red;
            }
        </style>
    </head>
    <body>
        <jsp:include page="header.jsp"></jsp:include>
        <center>
            <%
                ArrayList<String> errorMsg = (ArrayList<String>)(request.getAttribute("errMsgs"));
                if(errorMsg!=null){
                    for(String str : errorMsg) { %>
                        <a id="error"> <%=str%> </a>
            <%      }
                } %>
            
            <form method="get" action="basic-loc-report" onsubmit="return validation()">
                <div class ="container">
                    <div class="row text-center well well-lg">
                        <h1>Breakdown by Year, Gender, School</h1>
                    </div>
                </div>
                <div id="result" style="color:red">
                </div>
                <h4>This basic report shows the breakdown of students in the SIS building by their year (2013/2014/2015/2016/2017), by their gender(male/female), and by their school.<br>
                The advanced report shows the breakdown of students by a combination of 2 or more of the 3 attributes and their order. </h4><br>
                Option 1
                <select name="option1" id="option1">
                    <option value="none">None</option>
                    <option value="year">Year</option>
                    <option value="gender">Gender</option>
                    <option value="school">School</option>
                </select>
                <br/>
                <br/>
                Option 2
                <select name="option2" id="option2">
                    <option value="none">None</option>
                    <option value="year">Year</option>
                    <option value="gender">Gender</option>
                    <option value="school">School</option>
                </select>
                <br/>
                <br/>
                Option 3
                <select name="option3" id="option3">
                    <option value="none">None</option>
                    <option value="year">Year</option>
                    <option value="gender">Gender</option>
                    <option value="school">School</option>
                </select>

                <br/>
                <br/>
                Date:
                        <input type="datetime-local" style="width:224px" name="date" step="1" max="2017-10-06T00:00" min="2013-06-06T00:00" required>
                <br />
                <br />
                <input id="r1" name="type" type="radio" value="Basic" checked>Basic
                <input id="r2" name="type" type="radio" value="Advanced">Advanced
                <br>
                <br>
                <input type="submit" value="Submit" class="btn btn-primary">
            </form>
        </center>
                <script>
                    function validation(){
                        var currentSelection=[];
                        con1 = $("#option1 option:selected").html().trim();
                        con2 = $("#option2 option:selected").html().trim();
                        con3 = $("#option3 option:selected").html().trim();
                        currentSelection.push(con1);
                        currentSelection.push(con2);
                        currentSelection.push(con3);
                        pass=true;
                        var count=0;
                        for(i=0;i<currentSelection.length;i++){
                            count=0;
                            curr = currentSelection[i];
                            if(curr!="None"){
                                for(t=0;t<currentSelection.length;t++){
                                    if(curr==currentSelection[t]){
                                        count=count+1;
                                    }
                                }
                                if(count>1){
                                    pass=false;
                                    break;
                                }
                            }
                        }
                        if(!pass){
                            document.getElementById("result").innerHTML="You are not allow to repeat the selection!";
                            return false;
                        }
                        else{
                            return true;
                        }
                    }

                </script>
        </center>
            
    </body>
</html>