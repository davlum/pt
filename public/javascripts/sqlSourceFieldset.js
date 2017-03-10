$(document).ready(function(){
    $( "#connId").on("focusout", function () {
        var val = $(" #connId").val();
        $.get("/connections/sql/" + val + "/tables", function(data) {

            var tables = data["tables"];
            var optionsAsString = "";
            for (var i=0; i<tables.length; i++) {
                var t = tables[i]
                optionsAsString += "<option value='" + t.tableId + "'>"
                    + t.schemaName + "." + t.tableName + "</option>";
            }
            $( "#factTable" ).find("option").remove().end().append($(optionsAsString));
            $( "#dimensionTables" ).find("option").remove().end().append($(optionsAsString));
        });
    });
});
