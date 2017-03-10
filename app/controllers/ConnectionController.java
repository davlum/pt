package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.connections.*;

import play.Configuration;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.*;
import utils.SidebarElement;
import views.html.connections.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ConnectionController extends AuthController {

    private final FormFactory formFactory;

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        return ok(index.render(getCurrentUser(),
                formFactory.form(SQLConnection.class),
                formFactory.form(CSVConnection.class),
                getCSVSidebarElements(),
                getSQLSidebarElement(),
                false));
    }

    public Result indexCSV() {
        return ok(index.render(getCurrentUser(),
                formFactory.form(SQLConnection.class),
                formFactory.form(CSVConnection.class),
                getCSVSidebarElements(),
                getSQLSidebarElement(),
                true));
    }

    private List<SidebarElement> getCSVSidebarElements() {
        return CSVConnection.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.ConnectionController.getCSVConnection(s.getId()).url(),
                        s.getConnectName(),
                        s.getConnectDescription()))
                .collect(Collectors.toList());
    }

    private List<SidebarElement> getSQLSidebarElement() {
        return SQLConnection.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.ConnectionController.getSQLConnection(s.getId()).url(),
                        s.getConnectionName(),
                        s.getConnectionDescription()))
                .collect(Collectors.toList());
    }

    public Result addSQLConnection() {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not add connection. Please check the information you entered.");
            return ok(index.render(getCurrentUser(), connectionForm, formFactory.form(CSVConnection.class),
                    getCSVSidebarElements(), getSQLSidebarElement(), false));
        } else {
            SQLConnection connection = connectionForm.get();
            if (SQLConnection.find.where().eq("connectionName", connection.getConnectionName()).findCount() > 0){
                flash("error", "Error: Please select a unique connection name!");
                return ok(index.render(getCurrentUser(), connectionForm, formFactory.form(CSVConnection.class),
                        getCSVSidebarElements(), getSQLSidebarElement(), false));
            }
            try (Connection conn = connection.connect()){
                connection.setTableMetadataList(reflectTables(conn));
                connection.save();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            flash("success", "New Connection Added");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    public Result addCSVConnection() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not add connection. Please check the information you entered.");
            return ok(index.render(getCurrentUser(), formFactory.form(SQLConnection.class), connectionForm,
                    getCSVSidebarElements(), getSQLSidebarElement(), true));
        } else {
            CSVConnection connection = connectionForm.get();
            if (CSVConnection.find.where().eq("connectName", connection.getConnectName()).findCount() > 0){
                flash("error", "Error: Please select a unique connection name!");
                return ok(index.render(getCurrentUser(), formFactory.form(SQLConnection.class), connectionForm,
                        getCSVSidebarElements(), getSQLSidebarElement(), true));
            }
            connection.save();
            connection.setConnectionPath(handleUpload(connection.getId()));
            connection.update();
            flash("success", "New Connection Added");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    public Result getSQLConnection(Long id) {
        SQLConnection connection = SQLConnection.find.byId(id);
        if(connection != null) {
            return ok(sqlConnectionDetail.render(getCurrentUser(),
                    formFactory.form(SQLConnection.class).fill(connection),
                    getCSVSidebarElements(),
                    getSQLSidebarElement()));
        } else {
            flash("error", "Connection Does Not Exist");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    public Result getCSVConnection(Long id) {
        CSVConnection connection = CSVConnection.find.byId(id);
        if(connection != null) {
            return ok(csvConnectionDetail.render(getCurrentUser(),
                    formFactory.form(CSVConnection.class).fill(connection),
                    getCSVSidebarElements(),
                    getSQLSidebarElement()));
        } else {
            flash("error", "Connection Does Not Exist");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    public Result updateSQLConnection(Long id) {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not update the connection. Please check the information you entered.");
            return redirect(controllers.routes.ConnectionController.getSQLConnection(id));
        } else {
            SQLConnection connection = connectionForm.get();

            SQLConnection conn = SQLConnection.find.byId(id);
            if(conn != null) {
                if (SQLConnection.find.where().eq("connectionName", connection.getConnectionName())
                        .ne("id", id).findCount() > 0){
                    flash("error", "Error: Please select a unique connection name!");
                } else {
                    List<TableMetadata> list = conn.getTableMetadataList();
                    conn.updateSQLConnection(connection, list);

                    flash("success", "Connection Updated!");
                }
            }
            return redirect(controllers.routes.ConnectionController.getSQLConnection(id));
        }
    }

    public Result updateCSVConnection(Long id) {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not add connection. Please check the information you entered.");
            return redirect(controllers.routes.ConnectionController.getSQLConnection(id));
        } else {
            CSVConnection connection = connectionForm.get();

            CSVConnection conn = CSVConnection.find.byId(id);
            if(conn != null) {
                if (CSVConnection.find.where().eq("connectName", connection.getConnectName())
                        .ne("id", id).findCount() > 0){
                    flash("error", "Error: Please select a unique connection name!");
                } else {
                    connection.setConnectionPath(handleUpload(id));
                    conn.updateCSVConnection(connection);
                    flash("success", "Connection Updated!");
                }
            }
            return redirect(controllers.routes.ConnectionController.getCSVConnection(id));
        }
    }

    public Result deleteSQLConnection(Long id) {
        SQLConnection connection = SQLConnection.find.byId(id);
        if(connection != null) {
            connection.delete();
            flash("success", "Connection successfully deleted!");
        } else {
            flash("error", "Connection Does Not Exist");
        }
        return redirect(controllers.routes.ConnectionController.index());
    }

    public Result deleteCSVConnection(Long id) {
        CSVConnection connection = CSVConnection.find.byId(id);
        if(connection != null) {
            connection.delete();
            flash("success", "Connection successfully deleted!");
        } else {
            flash("error", "Connection Does Not Exist");
        }
        return redirect(controllers.routes.ConnectionController.index());
    }

    private List<TableMetadata> reflectTables(Connection conn)
            throws SQLException
    {
        String query = "SELECT table_schema, table_name "
                     + "FROM information_schema.tables "
                     + "WHERE table_schema NOT IN ('information_schema', 'pg_catalog')";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<TableMetadata> tableMetadataList = new ArrayList<>();
        while (rs.next()) {
            tableMetadataList.add(new TableMetadata(rs.getString("table_schema"),
                               rs.getString("table_name")));
        }
        rs.close();
        stmt.close();
        conn.close();

        return tableMetadataList;
    }

    public Result getJsonReflectedTables(Long connectionId) {
        List<TableMetadata> tables = TableMetadata
                .find
                .where().eq("sqlConnection.id", connectionId)
                .findList();

        ObjectNode jsonTables = Json.newObject();
        ArrayNode jsonArray = Json.newArray();
        for (TableMetadata t: tables) {
            ObjectNode o = getJsonTable(t);
            jsonArray.add(o);
        }
        jsonTables.set("tables", jsonArray);

        return ok(jsonTables);
    }

    private ObjectNode getJsonTable(TableMetadata table) {
        ObjectNode o = Json.newObject();
        o.put("tableId", table.getId());
        o.put("tableName", table.getTableName());
        o.put("schemaName", table.getSchemaName());
        o.put("connectionId", table.getSqlConnection().getId());
        return o;
    }
    private String handleUpload(Long id){
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> document = body.getFile("csvFile");
        String fileSystemRoot = Configuration.root().getString("file.system.root");
        String fullPath = fileSystemRoot + "/CSV/" + id + "/";
        if (!Files.isDirectory(Paths.get(fullPath))) {
            try {
                Files.createDirectories(Paths.get(fullPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (document != null) {
            File file = document.getFile();

            int startExtension = document.getFilename().lastIndexOf(".");
            String nameOnly = startExtension > 0 ? document.getFilename().substring(0, startExtension) : document.getFilename();
            String extOnly = startExtension > 0 ? document.getFilename().substring(startExtension) : "";

            if(file.renameTo(new File(fullPath + nameOnly + extOnly))) {
                return fullPath + nameOnly + extOnly;
            }
        } else {
            CSVConnection conn = CSVConnection.find.where().eq("id", id).findUnique();
            if (conn != null) return conn.getConnectionPath();
        }

        return null;
    }
}
