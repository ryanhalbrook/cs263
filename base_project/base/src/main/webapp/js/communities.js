/*
   Ryan Halbrook, 2/19/16
*/

$.getScript("/js/flatfish.js");

function leaveCommunity(event) {
    console.log("Leaving community");
    console.log("Leaving community " + event.data.communityid);
    var callback = function(data) {console.log(data);};
    deleteResourceWithUsername("/rest/communities/" + event.data.communityid + "/membership", event.data.username, callback);
    setTimeout(function(){ fetchUserCommunities(event.data.username); }, 500);

}

function fetchUserCommunities(userId) {

    var displayCommunities = function (data) {

        $( "#communities-body" ).html("");

        for (var i = 0; i < data.length; i++) {
            var community = data[i];

            var id = "leave-button-" + community.id.replace(/ /g, '_');

            var leaveButton = "<button type=\"submit\" class=\"btn btn-default\" id=\"" + id + "\">Leave Community</button>";
            $( "#communities-body" ).append("<tr><td>" + community.id + "</td><td>" + leaveButton + "</td></tr>");
            $("#" + id).bind("click", {communityid: community.id, username: userId}, leaveCommunity);


        }

    }

    getResourceWithUsername("/rest/memberships", userId, displayCommunities);

}

function userInfoCallback(data) {
    if (data) {

        //$( "#user-info" ).append("User Name: " + data.userName);
        $( "#user-dropdown-title" ).html(data.emailAddress);

        $.getJSON("/rest/user/logouturl").done(
            function (data) {
                $( "#logouturl" ).html("<a href=\"" + data.string + "\">Logout</a>");
            }
        );
        window.user = data;
        fetchUserCommunities(data.userId);

    } else {

        //$( "#user-dropdown" ).remove();
        $.getJSON("/rest/user/loginurl").done(
            function (data) {
                $( "#user-info" ).html("Please <a href=\"" + data.string + "\">Sign in</a> with Google.");
            }
        );


    }

}

$( document ).ready(function() {

    $.getJSON("/rest/user/activeuser").done(userInfoCallback);

    function searchCommunities(query) {

        var displayQueryResults = function(data) {
            $( "#results-body" ).html("");

            for (var i = 0; i < data.length; i++) {
                var community = data[i];

                var viewButton = "<a href=\"/html/community.html?communityid=" + community.id + "\">View</a>";
                $( "#results-body" ).append("<tr><td>" + community.id + "</td><td>" + viewButton + "</td></tr>");
            }
            $( "#search-info" ).html("showing results for " + query);
        }

        $.ajax({url: "/rest/communities/search?prefix=" + query,
                method:"POST",
                contentType:"application/x-www-form-urlencoded",
                dataType: "json"}).done(displayQueryResults);
    }

    $( "#search-button" ).click( function() {
        var text = $( "#search-field" ).val();

        if (text.length > 0) {
            searchCommunities(text);
        } else {
            $( "#results-body" ).html("");
            $( "#search-info" ).html("");
        }

    });

    $( "#search-field" ).keyup( function() {
        var text = $( "#search-field" ).val();

        if (text.length > 0) {
            searchCommunities(text);
        } else {
            $( "#results-body" ).html("");
            $( "#search-info" ).html("");
        }

    });

});
