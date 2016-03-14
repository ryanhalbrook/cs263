/*
   Ryan Halbrook, 2/29/16
*/

$.getScript("/js/flatfish.js");

function fetchEvent(communityid, eventid) {

    var setEvent = function(data) {
        window.event = data;
        $("#community-name").html(window.event.communityName);
        $("#event-name").html(window.event.name);
        $("#event-desc").html(window.event.description);
        $("#event-date").html(window.event.eventDate);

    }
    getResourceWithUsername("/rest/communities/" + communityid + "/events/" + eventid + ":" + communityid, window.user.userId, setEvent);

}

$( document ).ready(function() {


    $.getJSON("/rest/user/activeuser").done(
        function (data) {

            if (data) {

                //$( "#user-info" ).append("User Name: " + data.userName);
                $( "#user-dropdown-title" ).html(data.emailAddress);

                window.user = data;

                $.getJSON("../rest/user/logouturl").done(
                    function (data) {
                        $( "#logouturl" ).html("<a href=\"" + data.string + "\">Logout</a>");
                    }
                );

                var urlParams = getUrlParams(window.location.href);

                if (urlParams["communityid"] && urlParams["eventid"]) {
                    fetchEvent(urlParams["communityid"], urlParams["eventid"]);
                }

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
