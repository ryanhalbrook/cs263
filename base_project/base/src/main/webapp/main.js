/*
   Ryan Halbrook, 2/18/16
*/
$( document ).ready(function() {

    $.getJSON("./rest/activeuser").done(
        function (data) {

            $( "#user-info" ).append("User Name: " + data.userName);
            $( "#dropdown-title" ).html(data.emailAddress);

        }
    );

});