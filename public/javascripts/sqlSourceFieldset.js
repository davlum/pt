"use strict";

document.addEventListener('DOMContentLoaded', function() {
    // page globals
    let joinsAdded = 0;
    let tables = {};
    let tableFields = {}

    function deNest(data) {
        let denested = [];
        for (let d of data) {
            let nested = d["nest"];
            let rows = d["rows"];

            if (JSON.stringify(rows) === '[{}]') {
                denested.push(nested);
            } else {
                for (let r of rows) {
                    let o = Object.assign({}, nested, r);
                    denested.push(o);
                }
            }
        }
        return denested;
    }

    function nest(data, nestBy) {
        let nested = [];
        if (typeof nestBy === "string") {
            nestBy=[nestBy];
        }
        let sorted = data.sort(function(a, b) {
            for (let n of nestBy) {
                if (a[n] === b[n]) { continue; }
                return a[n] > b[n];
            }
        });
        let current = {};
        for (let d of sorted) {
            let dNest = nestBy.reduce(
                function(o, k) {
                    o[k] = d[k];
                    return o;
                    }
                , {}
            );
            let dRow = {};
            for (let k in d) {
                if (nestBy.indexOf(k) < 0) {
                    dRow[k] = d[k];
                }
            }
            if (JSON.stringify(current["nest"]) === JSON.stringify(dNest)) {
                current["rows"].push(dRow);
            } else {
                if (JSON.stringify(current) !== "{}") {
                    nested.push(current);
                }
                current = {};
                current["nest"] = dNest;
                current["rows"] = [dRow];
            }
        }
        nested.push(current);
        return nested;
    }

    function createCloseButton() {
        let btn = document.createElement("button");
        btn.className = "close";
        btn.setAttribute('aria-label',"Close");
        btn.innerHTML = '<span aria-hidden="true">&times;</span>';
        btn.type = "button";
        btn.addEventListener("click", function() {
            let el = this.parentNode.parentNode;
            while (el.hasChildNodes()) {
                el.removeChild(el.firstChild);
            }
            return false;
        });
        return btn;
    }

    function getLeftTable() {
        let leftTable = {};
        leftTable["table"] = document
            .getElementById("from-clause-fromtable")
            .value;
        leftTable["alias"] = document
            .getElementById("from-clause-fromtable-alias")
            .value;
        return leftTable;
    }

    function getRightTable(id) {
        let rightTable = {};
        rightTable["table"] = document
            .getElementById("from-clause-join-table-" + id)
            .value;
        rightTable["alias"] = document
            .getElementById("from-clause-join-alias-" + id)
            .value;
        return rightTable;
    }

    function createAddConditionButton(joinDivId) {
        let btn = document.createElement("button");
        btn.className = "btn btn-success btn-sm";
        btn.id = "add-condition-" + joinDivId;
        btn.type = "button";
        btn.innerText= 'Add Join Condition';

        btn.addEventListener("click", function() {
            const leftTable = getLeftTable();
            const rightTable = getRightTable(joinDivId);
            let cond = createJoinCondition(joinDivId, leftTable, rightTable, 'AND');
            let rem = createRemoveConditionButton();
            cond.appendChild(rem);
            let conditions = document
                .getElementById("join-conditions-"+joinDivId);
            conditions.appendChild(cond);
        });
        return btn;

    }

    function createRemoveConditionButton() {
        let btn = document.createElement("button");
        btn.className = "btn btn-danger remove-me btn-sm";
        btn.type = "button";
        btn.innerHTML = '<span class="glyphicon glyphicon-minus"></span>';
        btn.addEventListener("click", function(){
            this.parentNode.parentNode.removeChild(this.parentNode);
        });
        return btn;
    }

    function createJoinContent(joinDivId) {
        let row = document.createElement("div");
        row.className = "form-inline";
        row.id = "join-table-div-" + joinDivId;

        let joinType = document.createElement("select");
        joinType.className = "form-control input-group-addon input-sm";
        joinType.required = "true";

        const joinTypeOptions = ["LEFT JOIN", "INNER JOIN"];
        for (let o of joinTypeOptions) {
            let op = document.createElement("option");
            op.value = o;
            op.innerText = o;
            joinType.appendChild(op);
        }
        joinType.value = "LEFT JOIN";

        let joinTable = document.createElement("select");
        joinTable.id = "from-clause-join-table-" + joinDivId;
        joinTable.className = "form-control input-sm";
        let fromTable = document.getElementById("from-clause-fromtable");
        joinTable.innerHTML = fromTable.innerHTML;

        let joinAliasDiv = document.createElement("div");
        joinAliasDiv.className = "input-group mb-2 mr-sm-2 mb-sm-0";
        let joinAliasLabel = document.createElement("div");
        joinAliasLabel.className ="input-group-addon";
        joinAliasLabel.innerText = "AS";
        let joinAlias = document.createElement("input");
        joinAlias.type = "text";
        joinAlias.className = "input-group form-control input-sm";
        joinAlias.required = "true";
        joinAlias.id = "from-clause-join-alias-" + joinDivId;
        joinAliasDiv.appendChild(joinAliasLabel);
        joinAliasDiv.appendChild(joinAlias);

        row.appendChild(joinType);
        row.appendChild(joinTable);
        row.appendChild(joinAliasDiv);

        return row;
    }

    function createJoinCondition(joinDivId, leftTable, rightTable, label) {
        let row = document.createElement("div");
        row.id = "join-table-condition-div-" + joinDivId;
        row.className = "form-inline";
        let val = document.getElementById("sqlconnection_id").value;
        let lbl = document.createElement("div");
        lbl.innerText = label;
        lbl.className = "input-group-addon form-control input-sm";

        let leftField = document.createElement("select");
        leftField.className = "input-group form-control input-sm";

        let operator = document.createElement("select");
        operator.className = "input-group form-control input-sm";

        let operatorOptions =
            ["="
            , ">"
            , ">="
            , "<"
            , "<="
            , "!="
            , "IS NULL"
            , "IS NOT NULL"];

        for (let o of operatorOptions) {
            let op = document.createElement("option");
            op.value = o;
            op.innerText = o;
            operator.appendChild(op);
        }

        let rightField = document.createElement("select");
        rightField.className = "input-group form-control input-sm";
        let leftURL = "/tables/sql/" + val + "/tables/" + leftTable["table"];
        let rightURL = "/tables/sql/" + val + "/tables/" + rightTable["table"];
        $.get(leftURL, function(data){
            for (let d of data) {
                d["tableAlias"] = leftTable["alias"]
            }
            refreshOptionGroups(leftField, data,
                "tableAlias", "columnId", "columnAlias", "Choose Field From " + d["tableAlias"]);
        });

        $.get(rightURL, function(data){
            for (let d of data) {
                d["tableAlias"] = rightTable["alias"]
            }
            refreshOptionGroups(rightField, data,
                "tableAlias", "columnId", "columnAlias", d["tableAlias"]);
        });
        row.appendChild(lbl);
        row.appendChild(leftField);
        row.appendChild(operator);
        row.appendChild(rightField);

        return row;
    }

    document.getElementById("add-join").addEventListener("click", function() {
        // check from table field & alias are specified
        if (document.getElementById('from-clause-fromtable').value === "") {

        }
        let condB = document.getElementById('from-clause-fromtable-alias').value === ""
        if (condA || condB) {

        } else {
            // do the layout first
            let joins = document.getElementById("joins");
            let row = document.createElement("div");
            let content = document.createElement("div");
            let close = document.createElement("div");
            joins.appendChild(row);
            row.appendChild(content);
            row.appendChild(close);
            row.className = "row";
            content.className = "col-sm-11";
            close.className = "col-sm-1";

            joinsAdded++;

            let add = createAddConditionButton(joinsAdded);

            let closeBtn = createCloseButton();
            close.appendChild(closeBtn);

            let joinTables = createJoinContent(joinsAdded);

            let conditions = document.createElement("div");
            conditions.id = "join-conditions-" + joinsAdded;
            const leftTable = getLeftTable();
            let cond = createJoinCondition(joinsAdded, leftTable, {}, 'ON');
            conditions.appendChild(cond);
            content.appendChild(joinTables);
            content.appendChild(conditions);
            content.appendChild(add);
        }
    });

    function createOptionGroup(dataNested, grpLabel, value, innerText) {
        if (JSON.stringify(dataNested.rows)!=='[{}]') {
            let grp = document.createElement('optgroup');
            grp.label = dataNested.nest[grpLabel];
            for (const t of dataNested.rows) {
                let o = document.createElement('option');
                o.value = t[value];
                o.innerText = t[innerText];
                grp.appendChild(o);
            }
            return grp;
        }
    }

    function refreshOptionGroups(el, data, groupKey, valueKey, textKey, placeholder) {
        while(el.firstChild) el.removeChild(el.firstChild);
        let emptyOpt = document.createElement("option");
        emptyOpt.value = "";
        emptyOpt.innerText = placeholder;
        el.appendChild(emptyOpt);
        for (const nested of data) {
            const grp = createOptionGroup(nested, groupKey, valueKey, textKey);
            el.appendChild(grp);
        }
    }

    function getConnectionTables(id) {
        $.get("/connections/sql/" + id + "/tables", function(data) {
            tables = nest(data["tables"], "schemaName");

            let fromTable = document
                .getElementById("from-clause-fromtable");
            refreshOptionGroups(fromTable,
                tables, "schemaName",
                "qualifiedName", "tableName", "Choose Table");
        });
    }

    document.getElementById("sqlconnection_id")
        .addEventListener("focusout", function () {
        const id = this.value;
        getConnectionTables(id);
    });

    if (document.getElementById("sqlconnection_id").value !== "") {
        getConnectionTables(document.getElementById("sqlconnection_id").value);
    }
}, false);
