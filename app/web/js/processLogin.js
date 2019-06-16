$(document).ready(function(){
    $( "#submitButton" ).hover(
      function() {
        $("#submitButton").css("background-color", "#42dca3");
        $("#submitButton").css("color", "white");
      }, function() {
        $("#submitButton").css("background-color", "transparent");
        $( "#submitButton").css("color", "#42dca3");
      }
    );
    
    $( "#button2" ).hover(
      function() {
        $("#button2").css("color", "#d6ba83");
      }, function() {
        $( "#button2").css("color", "white");
      }
    );
    
    $( ".blinking" ).hover(
      function() {
        $(".blinking").css("color", "#d6ba83");
      }, function() {
        $( ".blinking").css("color", "white");
      }
    );
    
        $( ".subTitle_1" ).hover(
      function() {
        $(".subTitle_1").css("color", "#d2e4af");
      }, function() {
        $( ".subTitle_1").css("color", "white");
      }
    );
    
    $( "#subTitle_2" ).hover(
      function() {
        $("#subTitle_2").css("color", "#d2e4af");
      }, function() {
        $( "#subTitle_2").css("color", "white");
      }
    );
    
})

function checking(){
    email = document.getElementById("email").value;
    password = document.getElementById("password").value;
    errorMsg = "";
    if(email == "" ){
        document.getElementById("error").innerHTML = "You can not leave email or password as blank";
    }
    else if (password == "" ){
        document.getElementById("error").innerHTML = "You can not leave email or password as blank";
    }
    else{
        document.getElementById("error").innerHTML = "";
        verify();
    }
} 
function verify(){
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    $.ajax({  
            type:"GET",  
            url:"loginController.jsp?email="+email+"&password="+password,
            success:function(data){ 
                if(data.trim()==="succeed"){
                    
                    document.getElementById("submitButton").innerHTML="Welcome";
                    var delayMillis = 2000; //1 second

                    setTimeout(function() {
                        window.location.href = "home.jsp";
                    }, delayMillis);

                }
                else{
                    document.getElementById("error").innerHTML = data;
                }

            }
    });
}