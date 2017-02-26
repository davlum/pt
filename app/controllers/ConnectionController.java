package controllers;

import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import views.html.datasources.datasources;
import views.html.datasources.addCsv;
import views.html.datasources.addSql;
import views.html.connections;

import javax.inject.Inject;
import java.util.List;

public class ConnectionController extends AuthController {

    private final String activeTab = "datasources";

    private final FormFactory formFactory;
    private Form<SQLConnection> sqlConnectionForm;
    private Form<CSVConnection> csvConnectionForm;

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.sqlConnectionForm = formFactory.form(SQLConnection.class);
        this.csvConnectionForm = formFactory.form(CSVConnection.class);
    }

    public Result datasources(){
        return ok(datasources.render(getCurrentUser(), activeTab));
    }

    public Result addCSVFile() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        return ok(addCsv.render(getCurrentUser(), activeTab, connectionForm));
    }

    public Result saveCSVConnection() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        return ok(addCsv.render(getCurrentUser(), activeTab, connectionForm));
    }

    public Result addSQLConnection() {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class);
        SQLConnection connection = connectionForm.bindFromRequest().get();
        return ok(addSql.render(getCurrentUser(), activeTab));
    }

    public Result index() {
        List<SQLConnection> sqlConnections = SQLConnection.find.all();
        List<CSVConnection> csvConnections = CSVConnection.find.all();
        return ok(connections.render(sqlConnections, csvConnections, sqlConnectionForm));
    }

    /*public Result addSQLConnection() {

        SQLConnection connection = sqlConnectionForm.bindFromRequest().get();
        return null;
    }

    public Result addCSVConnection() {

        CSVConnection connection = csvConnectionForm.bindFromRequest().get();
        return null;
    }*/
}
