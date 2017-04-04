/**
 * Created by davidlum on 3/28/17.
 */
$(document).ready(function() {

    var pivotTable = $("#sideBarBuilder").attr('name');

    function fillTable(){
        var jsonTables = {"columns": [], "rows": [], "page": [], "values": []};
        $("#rowFrm").find(".padB10").each(function(){jsonTables.rows.push($(this).attr("id"))});
        $("#columnFrm").find(".padB10").each(function(){jsonTables.columns.push($(this).attr("id"))});
        $("#pageFrm").find(".padB10").each(function(){jsonTables.page.push($(this).attr("id"))});
        $("#valueFrm").find(".padB10").each(function(){jsonTables.values.push($(this).attr("id"))});
        return jsonTables;
    }

    var oldJson = fillTable();


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
            .on('drop', function(el, target) {
                if (target.id === "pageFrm") {
                    var pages = target.getElementsByClassName("padB10");
                    if (pages[0].id === el.id) {
                        $("#fieldFrm").append(pages[1]);
                    }
                    else {
                        $("#fieldFrm").append(pages[0]);
                    }
                }
            })
            .on('cancel', function(el){
                $("#fieldFrm").append(el);
            });

    function arrDiff(a1, a2){
        var diff = [[],[]], a1len = a1.length, a2len = a2.length;
        for (var i = 0; i < a1len; i++) {
            if ($.inArray(a1[i], a2) === -1) { diff[0].push(a1[i]); }
        }
        for (var j = 0; j < a2len; j++) {
            if ($.inArray(a2[j], a1) === -1) { diff[1].push(a2[j]); }
        }
        return diff;
    }

    function getFieldIds(idArr){
        var len = idArr.length, fieldIds = [];
        for (var i = 0; i < len; i++) {
            fieldIds.push($("#sideBarBuilder").find("#"+idArr[i]).data("field-id"));
        }
        return fieldIds;
    }


    $("#sideBarBuilder").on("click", function(e) {
        var newJson = fillTable(), sendJson = {},
            upRows = arrDiff(oldJson.rows, newJson.rows),
            upCols = arrDiff(oldJson.columns, newJson.columns),
            upPages = arrDiff(oldJson.page, newJson.page),
            upVals = arrDiff(oldJson.values, newJson.values);
        upRows[0] = getFieldIds(upRows[0]);
        upCols[0] = getFieldIds(upCols[0]);
        upPages[0] = getFieldIds(upPages[0]);
        upVals[0] = getFieldIds(upVals[0]);
        sendJson.rows = upRows;
        console.log(sendJson.rows);
        sendJson.columns = upCols;
        sendJson.page = upPages;
        sendJson.values = upVals;
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
