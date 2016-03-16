/*
   Ryan Halbrook, 2/29/16
*/

$.getScript("/js/flatfish.js");

function fetchCommunity(communityid, username) {

    var setCommunity = function(data) {
        window.community = data;
        $("#community-name").html(window.community.id);
        $("#community-desc").html(window.community.description);

        var adminUserId = window.community.adminUserId;

        if (adminUserId == window.user.userId) {
            $("#new-event-button").click(function() {
                window.location.href = "/html/newevent.html?communityid=" + communityid;
            });
            $("#actions-menu").html("Community Admin");
            $("#new-event-button").show();
        } else {
            $("#actions-menu").html("Community Membership");
            $("#join-button").click(joinCommunity(window.community.id, username));
            $("#join-button").show();
        }

    }
    getResourceWithUsername("/rest/communities/" + communityid, username, setCommunity);

}

function joinCommunity(communityid, username) {

    var callback = function(data) {console.log(data);}
    putResourceWithUsername("/rest/memberships/" + communityid, username, callback);

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
                var communityid = urlParams["communityid"]

                if (urlParams["communityid"]) {
                    fetchCommunity(communityid, data.userId);
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
