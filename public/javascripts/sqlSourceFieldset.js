$(document).ready(function(){

    $( "#connectionId").on("focusout", function () {
        var val = $(" #connectionId").val();
        $.get("/connections/sql/" + val + "/tables", function(data) {

            var tables = data["tables"];
            var optionsAsString = "";
            for (var i=0; i<tables.length; i++) {
                var t = tables[i];
                optionsAsString += "<option value='" + t.schemaName + "." + t.tableName + "'>"
                    + t.schemaName + "." + t.tableName + "</option>";
            }
            $( "#factTable" ).find("option").remove().end().append($(optionsAsString));
        });
    });
});
