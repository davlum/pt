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
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is for the management of CSV and SQL database connections
 */
public class ConnectionController extends AuthController {

    private final FormFactory formFactory;

    /**
     * Constructor for the Connection Controller Class
     * @param formFactory  FormFactory injection
     */
    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    /**
     * Display the connection home page
     * @return HTTP status
     */
    public Result index() {
        return ok(index.render(getCurrentUser(),
                formFactory.form(SQLConnection.class),
                formFactory.form(CSVConnection.class),
                getCSVSidebarElements(),
                getSQLSidebarElement(),
                false));
    }

    /**
     * Display the connection home page (CSV Form tab activated)
     * @return HTTP status
     */
    public Result indexCSV() {
        return ok(index.render(getCurrentUser(),
                formFactory.form(SQLConnection.class),
                formFactory.form(CSVConnection.class),
                getCSVSidebarElements(),
                getSQLSidebarElement(),
                true));
    }

    /**
     * Method gets all the CSV connections to be displayed on the sidebar
     * @return a list of CSV sidebar elements
     */
    private List<SidebarElement> getCSVSidebarElements() {
        return CSVConnection.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.ConnectionController.getCSVConnection(s.getId()).url(),
                        s.getConnectName(),
                        s.getConnectDescription()))
                .collect(Collectors.toList());
    }

    /**
     * Method gets all the SQL connections to be displayed on the sidebar
     * @return a list of SQL sidebar elements
     */
    private List<SidebarElement> getSQLSidebarElement() {
        return SQLConnection.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.ConnectionController.getSQLConnection(s.getId()).url(),
                        s.getConnectionName(),
                        s.getConnectionDescription()))
                .collect(Collectors.toList());
    }

    /**
     * Method that adds a new connection
     * @return page redirect or HTTP result with status code
     * depending on whether the connection was successful.
     */
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
                flash("error", "Error: Could not connect to the database.");
                return redirect(controllers.routes.ConnectionController.index());
            }
            flash("success", "New Connection Added");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    /**
     * Method that adds a connection to a CSV file.
     * @return page redirect or HTTP result with status code
     * depending on whether the connection was successful.
     */
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

    /**
     * Get an existing database connection based on its id
     * @param id of connection
     * @return HTTP status or redirect
     */
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

    /**
     * Get an existing CSV connection based on its id
     * @param id of connection
     * @return HTTP status or redirect
     */
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

    /**
     * Updates the connection to a database
     * @param id of connection
     * @return HTTP status or redirect
     */
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

    /**
     * Updates the connection to CSV file
     * @param id of connection
     * @return HTTP status or redirect
     */
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

    /**
     * Delete a connection to a database
     * @param id of connection
     * @return HTTP redirect
     */
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

    /**
     * Delete a connection to a CSV file
     * @param id of connection
     * @return HTTP redirect
     */
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

    /**
     * Gets the data about the tables from database connection
     * and puts it in an Array List.
     * @param conn database connection
     * @return List of table data
     * @throws SQLException exception
     */
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

    /**
     * Gets the data about the columns from database connection
     * and puts it in an Array List.
     * @param conn database connection
     * @return List of column data
     * @throws SQLException
     */
    private List<ColumnMetadata> reflectColumns(TableMetadata tbl, Connection conn)
        throws SQLException
    {
        String query = "SELECT column_name, data_type "
                + "FROM information_schema.columns "
                + "WHERE table_name = '" + tbl.getTableName() + "'"
                + "AND table_schema = '" + tbl.getSchemaName() + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<ColumnMetadata> columnMetadataList = new ArrayList<>();
        while (rs.next()) {
            columnMetadataList.add(
                    new ColumnMetadata(
                            tbl,
                            rs.getString("column_name"),
                            rs.getString("data_type"))
            );
        }
        return columnMetadataList;
    }

    /**
     * Gets the data about the columns from the csv file
     * and puts it in an Array List.
     * @param tableId CSV Id
     * @return HTTP 400 status
     */
    public Result getJsonReflectedColumns(Long tableId) {
        List<ObjectNode> columns = ColumnMetadata
                .find
                .where().eq("tableMetadata.id", tableId)
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

    /**
     * Gets the data about the tables from the csv file
     * and puts it in an Array List.
     * @param connectionId CSV Id
     * @return HTTP 400 status
     */
    public Result getJsonReflectedTables(Long connectionId) {
        List<ObjectNode> tables = TableMetadata
                .find
                .where().eq("sqlConnection.id", connectionId)
                .findList().stream().map(TableMetadata::getJson).collect(Collectors.toList());

        ObjectNode jsonTables = Json.newObject();
        ArrayNode jsonArray = Json.newArray();
        for (ObjectNode t: tables) {
            jsonArray.add(t);
        }
        jsonTables.set("tables", jsonArray);

        return ok(jsonTables);
    }

    /**
     * Method with which to upload a CSV file.
     * @param id of CSV file
     * @return path of the file or null
     */
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
