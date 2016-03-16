/*
   Ryan Halbrook, 2/18/16
*/

$.getScript("/js/flatfish.js");


function formatDate(timestamp) {
    var m = moment(timestamp);
    var dateString = m.format("MMM DD") + " at " + m.format("h:mm A");
    return dateString;
}


function buildEventHTML(eventName, eventDescription, communityName, formattedDate, removeButtonId, subscribeButtonId) {

    var outerDivStart = "<div style=\"height:100px; margin-bottom:10px;\">";

    var firstDiv = "<div style=\"height:100%;border-radius:3px;padding:5px;padding-top:0px;padding-left:12px;width:20%;float:left; background-color:#F3F3F3;\"><h5>" + eventName + "</h5><h6 style=\"color:gray;\">" + communityName + "</h6><span style=\"font-size:9pt;color:blue;\">" + formattedDate + "</span></div>";

    var secondDiv = "<div style=\"float:left;height:100%;border-radius:3px;padding:10px;width:60%;background-color:#F3F3F3;\">" + eventDescription + "</div>";

    var thirdDiv = "<div style=\"float:left;height:100%;border-radius:3px;width:10%;background-color:#F3F3F3;text-align:center;\"><button id=\"" + subscribeButtonId + "\" style=\"margin-top:25px;border:none;background-color:#F3F3F3;\" type=\"button\" class=\"btn btn-default btn-sm\"><span class=\"glyphicon glyphicon-floppy-disk\" aria-hidden=\"true\"></span></button></div>";

    var fourthDiv = "<div style=\"float:left;height:100%;border-radius:3px;width:10%;background-color:#F3F3F3;text-align:center;\"><button id=\"" + removeButtonId + "\" style=\"margin-top:25px;border:none;background-color:#F3F3F3;\" type=\"button\" class=\"btn btn-default btn-sm\"><span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span></button></div>";

    var outerDivEnd = "<br style=\"clear: left;\" /></div>"

    return outerDivStart + firstDiv + secondDiv + thirdDiv + fourthDiv + outerDivEnd;

}

function fetchUserFeed(username) {
                $.ajax({url: "../rest/feed/events",
                                    method:"GET",
                                    beforeSend: function(xhr){xhr.setRequestHeader('userid', username);},
                                    dataType: "json"}).done(

                                        function (data) {

                                            $( "#events-region" ).html("");

                                            for (var i = 0; i < data.length; i++) {
                                                var event = data[i];
                                                var removeButtonId = "remove-event-" + i;
                                                var subscribeButtonId = "subscribe-event-" + i;
                                                $( "#events-region" ).append(buildEventHTML(event.name, event.description, event.communityName, formatDate(event.eventDate), removeButtonId, subscribeButtonId));

                                                $("#" + removeButtonId).click(function() {
                                                      deleteResourceWithUsername("/rest/memberships/" + event.communityName + "/events/" + event.name, username, function (){console.log("Deleted Event"); location.reload();});
                                                });
                                                $("#" + subscribeButtonId).click(function() {
                                                        var url = "/rest/subscriptions?eventid=" + event.name;
                                                        $.ajax({url: url, method:"POST", contentType:"application/x-www-form-urlencoded", beforeSend: function(xhr){xhr.setRequestHeader('userid', username);}, success:function() {alert("Subscribed!");}});
                                                });

                                            }
                                        }

                                    );


                $.ajax({url: "../rest/feed/announcements",
                                    method:"GET",
                                    beforeSend: function(xhr){xhr.setRequestHeader('userid', username);},
                                    dataType: "json"}).done(

                                        function (data) {

                                            $( "#announcements-body" ).html("");

                                            for (var i = 0; i < data.length; i++) {
                                                var announcement = data[i];

                                                $( "#announcements-body" ).append("<tr><td>" + announcement.eventId + "</td><td>" + announcement.title + "</td><td>" + announcement.description + "</td></tr>");
                                            }
                                        }

                                    );
}

$( document ).ready(function() {

    $.getJSON("../rest/user/activeuser").done(
        function (data) {


            if (data) {

                //$( "#user-info" ).append("User Name: " + data.userName);
                $( "#user-dropdown-title" ).html(data.emailAddress);

                $.getJSON("../rest/user/logouturl").done(
                    function (data) {
                        $( "#logouturl" ).html("<a href=\"" + data.string + "\">Logout</a>");
                        $("#user-dropdown").css("visibility", "visible");
                    }
                );

                $("#upcoming-events").css("visibility", "visible");
                $("#announcements").css("visibility", "visible");

                fetchUserFeed(data.userId);

            } else {

                $.getJSON("../rest/user/loginurl").done(
                    function (data) {
                        $( "#user-info" ).html("Please <a href=\"" + data.string + "\">Sign In</a> with Google.");
                        $("#user-login").css("visibility", "visible");
                        $("#loginurl").html("<a href=\"" + data.string + "\">Sign In</a>");
                    }
                );


            }

        }
    );

});
