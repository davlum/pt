package controllers;

import javafx.geometry.Side;
import models.connections.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;
import views.html.datasources.datasources;
import views.html.datasources.addCsv;
import views.html.datasources.addSql;
import views.html.connections;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionController extends AuthController {

    private final String activeTab = "datasources";

    private final FormFactory formFactory;
    private Form<SQLConnection> sqlConnectionForm;
    private Form<CSVConnection> csvConnectionForm;
    private List<SidebarElement> sidebarElements;

    @Inject
    public ConnectionController(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.sqlConnectionForm = formFactory.form(SQLConnection.class);
        this.csvConnectionForm = formFactory.form(CSVConnection.class);
    }

    public Result datasources(){
        return ok(datasources.render(getCurrentUser(),
                getCSVSidebarElements(),
                getSQLSidebarElement()));
    }

    public Result addCSVFile() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        return ok(addCsv.render(getCurrentUser(), connectionForm,
                getCSVSidebarElements(),
                getSQLSidebarElement()));
    }

    public Result saveCSVConnection() {
        Form<CSVConnection> connectionForm = formFactory.form(CSVConnection.class);
        return ok(addCsv.render(getCurrentUser(), connectionForm,
                getCSVSidebarElements(),
                getSQLSidebarElement()));
    }

    public Result addSQLConnection() {
        Form<SQLConnection> connectionForm = formFactory.form(SQLConnection.class);
        SQLConnection connection = connectionForm.bindFromRequest().get();
        return ok(addSql.render(getCurrentUser(), connectionForm,
                getCSVSidebarElements(),
                getSQLSidebarElement()));
    }

    public Result index() {
        return ok(connections.render(getCurrentUser(),
                sqlConnectionForm,
                getCSVSidebarElements(),
                getSQLSidebarElement()
                ));
    }

    private List<SidebarElement> getCSVSidebarElements() {
        return CSVConnection.find.all()
                .stream().map(s -> new SidebarElement("/connection/csv/" + s.getId(),
                        s.getConnectionName(), s.getConnectionDescription()))
                .collect(Collectors.toList());
    }

    private List<SidebarElement> getSQLSidebarElement() {
        return SQLConnection.find.all()
                .stream().map(s -> new SidebarElement("/connection/sql/" + s.getId(),
                        s.getConnectionName(), s.getConnectionDescription()))
                .collect(Collectors.toList());
    }

}
