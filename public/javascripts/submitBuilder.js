/**
 * Created by davidlum on 3/28/17.
 */
$(document).ready(function() {

    var pivotTable = $("#sideBarBuilder").attr('name');

    function fillTable(){
        var jsonTables = {"columns": [], "rows": [], "page": [], "values": {}};
        $("#rowFrm").find(".padB10").each(function(){jsonTables.rows.push($(this).attr("id"))});
        $("#columnFrm").find(".padB10").each(function(){jsonTables.columns.push($(this).attr("id"))});
        $("#pageFrm").find(".padB10").each(function(){jsonTables.page.push($(this).attr("id"))});
        $("#valueFrm").find(".padB10").each(function(){
            jsonTables.values[$(this).attr("id")] = $(this).children(":first").val();
        });
        return jsonTables;
    }

    var oldJson = fillTable();

    console.log(oldJson);


    $.ajaxPrefilter(function( options ) {
        if ( !options.beforeSend) {
            options.beforeSend = function (xhr) {
                xhr.setRequestHeader('Csrf-Token', $('input[name=\'csrfToken\']').val());
            }
        }
    });

    dragula([document.getElementById("fieldFrm"), document.getElementById("rowFrm"),
            document.getElementById("pageFrm"), document.getElementById("valueFrm"),
            document.getElementById("columnFrm")])
            .on('drop', function(el, target, source) {
                if (source.id === "valueFrm"){
                    $("#"+el.id).find(".typeFrm").css("display", "none");
                }
                if (target.id === "pageFrm") {
                    var pages = target.getElementsByClassName("padB10");
                    if (pages[0].id === el.id) {
                        $("#fieldFrm").append(pages[1]);
                    }
                    else {
                        $("#fieldFrm").append(pages[0]);
                    }
                }
                if (target.id === "valueFrm") {
                    var vals = $("#"+el.id).find(".typeFrm");
                    if (vals.length > 0) {
                        vals.css("display", "inline");
                    }
                    else {
                        var valsClone = $("#typeFrmhide").find(".typeFrm");
                        valsClone.each(function () {
                            $(this).clone(true).appendTo(el).css("display", "inline");
                        });
                    }
                }
            })
            .on('cancel', function(el, container, source){
                if (source.id === "valueFrm"){
                    $("#"+el.id).find(".typeFrm").css("display", "none");
                }
                $("#fieldFrm").append(el);
            });

    function getFieldIds(idArr){
        var len = idArr.length, fieldIds = [];
        for (var i = 0; i < len; i++) {
            fieldIds.push($("#sideBarBuilder").find("#"+idArr[i]).data("field-id"));
        }
        return fieldIds;
    }

    function arrDiff(a1, a2){
        var diff = [[],[],[]], a1len = a1.length, a2len = a2.length;
        for (var i = 0; i < a1len; i++) {
            if ($.inArray(a1[i], a2) === -1) { diff[0].push(a1[i]); }
        }
        for (var j = 0; j < a2len; j++) {
            if ($.inArray(a2[j], a1) === -1) { diff[1].push(a2[j]); }
        }
        diff[0] = getFieldIds(diff[0]);
        return diff;
    }

    function valDiff(map1, map2){
        var diff = [[],[],[]], keys1 = Object.keys(map1), keys2 = Object.keys(map2),
            m1len = keys1.length, m2len = keys2.length;
        for (var i = 0; i < m1len; i++){
            if ($.inArray(keys1[i], keys2) === -1) { diff[0].push(keys1[i]); }
        }
        for (var j = 0; j < m2len; j++){
            var ind = $.inArray(keys2[j], keys1);
            if (ind === -1) {
                diff[1].push(keys2[j]);
                diff[2].push(map2[keys2[j]]); // If it wasn't in the old table, add it
            }
            else {                                  // else check to see that they share the same type
                if (!(map2[keys2[j]] === map1[keys1[ind]])) {
                    diff[0].push(keys1[ind]);
                    diff[1].push(keys2[j]);
                    diff[2].push(map2[keys2[j]]);
                }
            }
        }
        diff[0] = getFieldIds(diff[0]);
        return diff;
    }

    $("#sidebarButton").on("click", function(){
        var valsInput = $("#valueFrm").find(".typeFrm");
        var newJson = fillTable(), sendJson = {},
            upRows = arrDiff(oldJson.rows, newJson.rows),
            upCols = arrDiff(oldJson.columns, newJson.columns),
            upPages = arrDiff(oldJson.page, newJson.page),
            upVals = valDiff(oldJson.values, newJson.values);
        if (upPages[1].length > 1){
            location.reload(true); // If pages is submitting more than one addition, reload the page.
        }
        sendJson.rows = upRows;
        sendJson.columns = upCols;
        sendJson.page = upPages;
        sendJson.values = upVals;
        console.log(sendJson.values);
        $.ajax({
            url: "/pivot-tables/" + pivotTable + "/update-table",
            data: JSON.stringify(sendJson),
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            error: function () {
                $("#display").append("<h3>An error has occurred</h3>");
            },
            success: function (data) {
                $("#display").append(data);
            },
            timeout: 60000 // sets timeout to 3 seconds
        });
        location.reload(true);
    });
});
