$.getScript("/js/flatfish.js");

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

    $("#event-form").submit(function(event) {

        event.preventDefault();

        var urlParams = getUrlParams(window.location.href);
        var communityid = urlParams["communityid"]

        var name = $( "#name" ).val();
        var desc = $( "#description" ).val();
        var date = $( "#date" ).val();

        var url = "/rest/communities/" + communityid + "/events?name=" + name + "&description=" + desc + "&date=" + date;
        $.ajax({url: url,
                method:"POST",
                contentType:"application/x-www-form-urlencoded",
                beforeSend: function(xhr){xhr.setRequestHeader('userId', window.user.userId);},
                success: function(data, textStatus, xhr) {alert("Event Created.");window.location.href = "/html/event.html?name=" + name;},
                error: function(xhr, textStatus, errorThrown) {alert("Sorry, the event could not be created:" + errorThrown);}

                });

    });

});