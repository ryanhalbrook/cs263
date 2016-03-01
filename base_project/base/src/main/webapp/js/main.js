/*
   Ryan Halbrook, 2/18/16
*/
function formatDate(timestamp) {
    var m = moment(timestamp);
    var dateString = m.format("MMM DD") + " at " + m.format("h:mm A");
    return dateString;
}

function fetchUserFeed(username) {
                $.ajax({url: "../rest/feed",
                                    method:"GET",
                                    beforeSend: function(xhr){xhr.setRequestHeader('username', username);},
                                    dataType: "json"}).done(

                                        function (data) {

                                            $( "#events-body" ).html("");

                                            for (var i = 0; i < data.length; i++) {
                                                var event = data[i];
                                                var cssClass = "event-row";
                                                $( "#events-body" ).append("<tr class=\"" + cssClass + "\"><td>" + event.name + "</td><td>" + event.description + "</td><td>" + event.communityName + "</td><td>" + formatDate(event.eventDate) + "</td></tr>");
                                            }
                                        }

                                    );
}

$( document ).ready(function() {

    $.getJSON("../rest/user/activeuser").done(
        function (data) {


            if (data) {

                $( "#user-info" ).append("User Name: " + data.userName);
                $( "#user-dropdown-title" ).html(data.emailAddress);

                $.getJSON("../rest/user/logouturl").done(
                    function (data) {
                        $( "#logouturl" ).html("<a href=\"" + data.string + "\">Logout</a>");
                    }
                );

                fetchUserFeed(data.userName);

            } else {

                //$( "#user-dropdown" ).remove();
                $.getJSON("../rest/user/loginurl").done(
                    function (data) {
                        $( "#user-info" ).html("Please <a href=\"" + data.string + "\">Sign in</a> with Google.");
                    }
                );


            }

        }
    );

});
