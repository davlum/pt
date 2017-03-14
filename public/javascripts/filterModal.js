$(document).ready(function(){

    $( "#filterForm #fieldID").on("change", function () {
        var val = $("#filterForm #fieldID").val();
        $.get("/pivot-tables/" + val + "/options", function(data) {
            $( "#filterOptions" ).html(data);
        });
    });
});
