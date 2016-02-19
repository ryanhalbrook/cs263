/*
   Ryan Halbrook, 2/19/16
*/

$( document ).ready(function() {

    $.getJSON("./rest/activeuser").done(
        function (data) {

            $( "#dropdown-title" ).html(data.emailAddress);

        }
    );

    function searchCommunities(query) {
        $.ajax({url: "./rest/communities/search?prefix=" + query,
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