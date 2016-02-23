/*
   Ryan Halbrook, 2/18/16
*/
$( document ).ready(function() {

    $.getJSON("./rest/user/activeuser").done(
        function (data) {


            if (data) {

                $( "#user-info" ).append("User Name: " + data.userName);
                $( "#user-dropdown-title" ).html(data.emailAddress);

                $.getJSON("./rest/user/logouturl").done(
                    function (data) {
                        $( "#logouturl" ).html("<a href=\"" + data.string + "\">Logout</a>");
                    }
                );

            } else {

                //$( "#user-dropdown" ).remove();
                $.getJSON("./rest/user/loginurl").done(
                    function (data) {
                        $( "#user-info" ).html("Please <a href=\"" + data.string + "\">Sign in</a> with Google.");
                    }
                );


            }

        }
    );

});
