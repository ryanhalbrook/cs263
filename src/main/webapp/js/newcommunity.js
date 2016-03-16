$.getScript("/js/flatfish.js");

function userInfoCallback(data) {
    if (data) {

        $( "#user-dropdown-title" ).html(data.emailAddress);

        $.getJSON("/rest/user/logouturl").done(
            function (data) {
                $( "#logouturl" ).html("<a href=\"" + data.string + "\">Logout</a>");
            }
        );
        window.user = data;

    } else {

        $.getJSON("/rest/user/loginurl").done(
            function (data) {
                $( "#user-info" ).html("Please <a href=\"" + data.string + "\">Sign in</a> with Google.");
            }
        );


    }

}

$( document ).ready(function() {

    $.getJSON("/rest/user/activeuser").done(userInfoCallback);

    $("#cform").submit(function(event) {

        event.preventDefault();

        var name = $( "#communityid" ).val();
        var desc = $( "#description" ).val();

        window.postCallback = function(data) {
            console.log("OK");
            alert("Status:" + xhr.status);
        }

        var url = "/rest/communities?name=" + name + "&description=" + desc;
        $.ajax({url: url,
                method:"POST",
                contentType:"application/x-www-form-urlencoded",
                beforeSend: function(xhr){xhr.setRequestHeader('userId', window.user.userId);},
                success: function(data, textStatus, xhr) {alert("Community Created.");window.location.href = "/html/community.html?communityid=" + name;},
                error: function(xhr, textStatus, errorThrown) {alert("Sorry, the community could not be created:" + errorThrown);}

                });



    });

});