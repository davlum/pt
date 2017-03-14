$(document).ready(function(){
    var tableID = $("#tableID").text();
    $.ajax({
        url: "/pivot-tables/"+tableID+"/contents",
        error: function(){
            $("#display").append("<h3>An error has occurred</h3>");
        },
        success: function(data){
            $("#display").append(data);
        },
        timeout: 60000 // sets timeout to 3 seconds
    });
});