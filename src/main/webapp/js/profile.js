/*
   Ryan Halbrook, 3/03/16
*/

$.getScript("/js/flatfish.js");

function updateUserInfo(user) {
    $("#fullname").html(user.firstName + " " + user.lastName);
    $("#email").html(user.emailAddress);
    $("#username").html(user.userName);
    fetchUserCommunities(user.userId);
}

function fetchUserCommunities(userId) {

    var displayMembershipCount = function (data) {

        $("#membership-count").html(data.length);

    }

    getResourceWithUsername("/rest/memberships", userId, displayMembershipCount);

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
                    }
                );

                updateUserInfo(data);

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
