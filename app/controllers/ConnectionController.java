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
