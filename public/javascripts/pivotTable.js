$(document).ready(function(){
    var tableID = $("#tableID").text();
    $.ajax({
        url: "/pivot-tables/"+tableID+"/contents/" + $("#pageSelect").val(),
        error: function(){
            $("#tableHere").html("<h3>An error has occurred</h3>");
        },
        success: function(data){
            $("#tableHere").html(data);
        },
        timeout: 60000 // sets timeout to 3 seconds
    });

    $( "#pageSelect").on("change", function () {
        $.ajax({
            url: "/pivot-tables/"+tableID+"/contents/" + $("#pageSelect").val(),
            error: function(){
                $("#tableHere").html("<h3>An error has occurred</h3>");
            },
            success: function(data){
                $("#tableHere").html(data);
            },
            timeout: 60000 // sets timeout to 3 seconds
        });
    });
});