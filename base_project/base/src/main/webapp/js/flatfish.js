/*
    Ryan Halbrook
    2/29/16
*/

function getResourceWithUsername(url, username, callback) {

    $.ajax({url: url, method:"GET", beforeSend: function(xhr){xhr.setRequestHeader('username', username);}, dataType: "json"}).done(callback);

}

function postResourceWithUsername(url, username, callback) {

    $.ajax({url: url, method:"POST", beforeSend: function(xhr){xhr.setRequestHeader('username', username);}, dataType: "json"}).done(callback);

}

function deleteResourceWithUsername(url, username, callback) {

    $.ajax({url: url, method:"DELETE", beforeSend: function(xhr){xhr.setRequestHeader('username', username);}, dataType: "json"}).done(callback);

}

function getUrlParams(url) {

    var params = {};

    var questionMarkIndex = url.indexOf('?');
    if (questionMarkIndex < 0) {
        return params;
    }

    var paramsString = url.slice(questionMarkIndex + 1);
    var splitParams = paramsString.split("&");

    for (var i = 0; i < splitParams.length; i++) {
        var keyValue = (splitParams[i]).split("=");
        if (keyValue.length == 2) {
            params[keyValue[0]] = keyValue[1];
        }
    }

    return params;

}