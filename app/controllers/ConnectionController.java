package controllers;

import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import views.html.connections;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by hal on 2017-02-17.
 */
public class ConnectionController extends Controller {

    private final FormFactory formFactory;
    private Form<SQLConnection> sqlConnectionForm;
    private Form<CSVConnection> csvConnectionForm;

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.sqlConnectionForm = formFactory.form(SQLConnection.class);
        this.csvConnectionForm = formFactory.form(CSVConnection.class);
    }

    public Result index() {
        List<SQLConnection> sqlConnections = SQLConnection.find.all();
        List<CSVConnection> csvConnections = CSVConnection.find.all();
        return ok(connections.render(sqlConnections, csvConnections, sqlConnectionForm));
    }

    public Result addSQLConnection() {

        SQLConnection connection = sqlConnectionForm.bindFromRequest().get();
        return null;
    }

    public Result addCSVConnection() {

        CSVConnection connection = csvConnectionForm.bindFromRequest().get();
        return null;
    }

}
