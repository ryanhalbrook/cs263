/*
   Ryan Halbrook, 2/19/16
*/

function fetchUserCommunities(username) {
                $.ajax({url: "../rest/memberships/",
                                    method:"GET",
                                    contentType:"application/x-www-form-urlencoded",
                                    beforeSend: function(xhr){xhr.setRequestHeader('username', username);},
                                    dataType: "json"}).done(

                                        function (data) {

                                            $( "#communities-body" ).html("");

                                            for (var i = 0; i < data.length; i++) {
                                                var community = data[i];
                                                var cssClass = "user-community";
                                                $( "#communities-body" ).append("<tr class=\"" + cssClass + "\"><td>" + community.id + "</td><td>" + community.description + "</td></tr>");
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

                fetchUserCommunities(data.userName);

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




    function searchCommunities(query) {
        $.ajax({url: "../rest/communities/search?prefix=" + query,
                            method:"POST",
                            contentType:"application/x-www-form-urlencoded",
                            dataType: "json"}).done(

                                function (data) {

                                    $( "#results-body" ).html("");

                                    for (var i = 0; i < data.length; i++) {
                                        var community = data[i];
                                        $( "#results-body" ).append("<tr><td>" + community.id + "</td><td>" + community.description + "</td></tr>");
                                    }
                                    $( "#search-info" ).html("showing results for " + query);
                                }

                            );
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
