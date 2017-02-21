package controllers;

import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;
/**
 * Created by hal on 2017-02-17.
 */
public class ConnectionController extends Controller {
//    private Result GO_TABLE = redirect(controllers.routes.DBConnectionController.index());

    private final FormFactory formFactory;

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        return ok("hello");
    }

    public Result addSQLConnection() {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class);
        SQLConnection connection = connectionForm.bindFromRequest().get();
        return null;
    }

    public Result addCSVConnection() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        CSVConnection connection = connectionForm.bindFromRequest().get();
        return null;
    }

}
