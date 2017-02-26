package controllers;

import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import views.html.datasources.datasources;
import views.html.datasources.addCsv;
import views.html.datasources.addSql;

import javax.inject.Inject;

public class ConnectionController extends AuthController {

    private final FormFactory formFactory;

    private final String activeTab = "datasources";

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
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
        SQLConnection existing = SQLConnection.find.byId(1L); // find.all(); find.eq("desc", "desc value")
        if (existing == null) {
            (new SQLConnection(
                    "bixi", "database of bixi trips",
                    "ec2-52-90-93-63.compute-1.amazonaws.com",
                    5432, "bixi_pivot","bixi_select",
                    "select_bixi"
            )).save();
        }
        return ok("ok");

    }
}
