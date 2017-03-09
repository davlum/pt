package controllers;

import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;
import views.html.connections.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
                getSQLSidebarElement()));
    }

    private List<SidebarElement> getCSVSidebarElements() {
        return CSVConnection.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.ConnectionController.getCSVConnection(s.getId()).url(),
                        s.getConnectionName(),
                        s.getConnectionDescription()))
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

    public Result addCSVConnection() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        CSVConnection connection = connectionForm.get();
        connection.save();
        return redirect(controllers.routes.ConnectionController.index());
    }

    public Result addSQLConnection() {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not add connection. Please check the information you entered.");
            return ok(index.render(getCurrentUser(), connectionForm, formFactory.form(CSVConnection.class),
                    getCSVSidebarElements(), getSQLSidebarElement()));
        } else {
            SQLConnection connection = connectionForm.get();
            if (SQLConnection.find.where().eq("connectionName", connection.getConnectionName()).findCount() > 0){
                flash("error", "Error: Please select a unique connection name!");
                return ok(index.render(getCurrentUser(), connectionForm, formFactory.form(CSVConnection.class),
                        getCSVSidebarElements(), getSQLSidebarElement()));
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
        return ok(csvConnectionDetail.render(getCurrentUser(),
                formFactory.form(CSVConnection.class).fill(CSVConnection.find.byId(id)),
                getCSVSidebarElements(),
                getSQLSidebarElement()));
    }

    public Result updateSQLConnection(Long id) {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not update the connection. Please check the information you entered.");
            return redirect(controllers.routes.ConnectionController.getCSVConnection(id));
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
                    System.out.println(conn.getConnectionName());
                    flash("success", "Connection Updated!");
                }
            }
            return redirect(controllers.routes.ConnectionController.getSQLConnection(id));
        }
    }

    public Result updateCSVConnection(Long id) {
        return ok();
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
        return ok();
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
}
