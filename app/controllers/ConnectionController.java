package controllers;

import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;
import views.html.connections.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionController extends AuthController {

    private final FormFactory formFactory;

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result addCSVConnection() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        CSVConnection connection = connectionForm.get();
        connection.save();
        return redirect(controllers.routes.ConnectionController.index());
    }

    public Result addSQLConnection() {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class).bindFromRequest();
        SQLConnection connection = connectionForm.get();
        connection.save();
        return redirect(controllers.routes.ConnectionController.index());

    }

    public Result getSQLConnection(Long id) {
        return ok(sqlConnectionDetail.render(getCurrentUser(),
                formFactory.form(SQLConnection.class).fill(SQLConnection.find.byId(id)),
                getCSVSidebarElements(),
                getSQLSidebarElement()));
    }

    public Result getCSVConnection(Long id) {
        return ok(csvConnectionDetail.render(getCurrentUser(),
                formFactory.form(CSVConnection.class).fill(CSVConnection.find.byId(id)),
                getCSVSidebarElements(),
                getSQLSidebarElement()));
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
                        s.getConnectionName(), s.getConnectionDescription()))
                .collect(Collectors.toList());
    }
}
