package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.connections.ColumnMetadata;
import models.connections.TableMetadata;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hal on 2017-03-27.
 */
public class TableController extends AuthController {

    private final FormFactory formFactory;

    @Inject
    public TableController(FormFactory formFactory) {
            this.formFactory = formFactory;
    }

    public Result getJsonReflectedColumns(Long connectionId, String qualifiedTableName)
    {
        String[] parts = qualifiedTableName.split(".");
        String schemaName = parts[0];
        String tableName = parts[1];
        List<ObjectNode> columns = ColumnMetadata
                .find
                .where().eq("tableMetadata.schemaName", schemaName)
                .where().eq("tableMetadata.tableName", tableName)
                .where().eq("tableMetadata.sqlconnection_id", connectionId)
                .findList()
                .stream()
                .map(ColumnMetadata::getJson).collect(Collectors.toList());

        ObjectNode jsonColumns = Json.newObject();
        ArrayNode jsonArray = Json.newArray();
        for (ObjectNode c: columns) {
            jsonArray.add(c);
        }
        jsonColumns.set("columns", jsonArray);

        return ok(jsonColumns);
    }
}
