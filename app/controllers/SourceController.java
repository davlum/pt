package controllers;

import models.connections.SQLConnection;
import models.sources.CSVSource;
import models.sources.SQLSource;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;
import views.html.sources.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for controlling the sources of the pivot Tables, whether
 * that is a database or CSV connection. Although the methods names
 * here indicate SQL, a variety of database connections are available.
 */
public class SourceController extends AuthController {
    private final FormFactory formFactory;

    /**
     * Constructor for the class
     * @param formFactory
     */
    @Inject
    public SourceController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    /**
     * Method to update page with new database information
     * @return Http status
     */
    public Result index() {
        return ok(views.html.sources.index.render(
                getCurrentUser(),
                formFactory.form(SQLSource.class),
                formFactory.form(CSVSource.class),
                getSQLSidebarElements(),
                getCSVSidebarElements(),
                false));
    }

    /**
     * Method to update page with new CSV information
     * @return Http status
     */
    public Result indexCSV() {
        return ok(views.html.sources.index.render(
                getCurrentUser(),
                formFactory.form(SQLSource.class),
                formFactory.form(CSVSource.class),
                getSQLSidebarElements(),
                getCSVSidebarElements(),
                true));
    }

    /**
     * Get the database elements to display on the sidebar
     * @return list of SQL elements
     */
    private List<SidebarElement> getSQLSidebarElements() {
        return SQLSource.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.SourceController.getSQLSource(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }

    /**
     * Get the CSV elements to display on the sidebar
     * @return list of CSV elements
     */
    private List<SidebarElement> getCSVSidebarElements() {
        return CSVSource.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.SourceController.getCSVSource(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }

    /**
     * Add a source for a pivot table from a database
     * @return HTTP redirect or status depending on input
     */
    public Result addSQLSource() {
        Form<SQLSource> sourceForm = formFactory.form(SQLSource.class).bindFromRequest();
        if (sourceForm.hasErrors()) {
            flash("error", "Error: Could not add connection. Please check the information you entered.");
            return ok(views.html.sources.index.render(
                    getCurrentUser(),
                    sourceForm,
                    formFactory.form(CSVSource.class),
                    getSQLSidebarElements(),
                    getCSVSidebarElements(),
                    false));
        } else {
            SQLSource source = sourceForm.get();
            if (SQLSource.find.where().eq("sourceName", source.getSourceName()).findCount() > 0){
                flash("error", "Error: Please select a unique source name!");
                return ok(views.html.sources.index.render(getCurrentUser(), sourceForm,
                        formFactory.form(CSVSource.class), getSQLSidebarElements(), getCSVSidebarElements(), false));
            }
            source.setConnection(SQLConnection.find.byId(Long.parseLong(sourceForm.data().get("sqlconnection_id"))));
            source.save();
            flash("success", "New Source Added");
            return redirect(controllers.routes.SourceController.index());
        }
    }

    /**
     * Add a source for a pivot table for a CSV connection
     * @return HTTP redirect or status depending on input
     */
    public Result addCSVSource() {
        Form<CSVSource> sourceForm = formFactory.form(CSVSource.class).bindFromRequest();
        if (sourceForm.hasErrors()) {
            flash("error", "Error: Could not add source. Please check the information you entered.");
            return ok(views.html.sources.index.render(getCurrentUser(), formFactory.form(SQLSource.class),
                    sourceForm, getSQLSidebarElements(), getCSVSidebarElements(), true));
        } else {
            CSVSource source = sourceForm.get();
            if (CSVSource.find.where().eq("sourceName", source.getSourceName()).findCount() > 0){
                flash("error", "Error: Please select a unique source name!");
                return ok(views.html.sources.index.render(getCurrentUser(), formFactory.form(SQLSource.class),
                        sourceForm, getSQLSidebarElements(), getCSVSidebarElements(), true));
            }
            source.save();
            flash("success", "New Source Added");
            return redirect(controllers.routes.SourceController.index());
        }
    }

    /**
     * Get database source with specified id
     * @param id id of the source
     * @return Http redirect or status
     */
    public Result getSQLSource(Long id) {
        SQLSource src = SQLSource.find.byId(id);
        if(src != null) {
            return ok(views.html.sources.sqlSourceDetail.render(getCurrentUser(),
                    formFactory.form(SQLSource.class).fill(src),
                    getSQLSidebarElements(),
                    getCSVSidebarElements()));
        } else {
            flash("error", "Connection Does Not Exist");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    /**
     * Get CSV source with specified id
     * @param id id of the source
     * @return Http redirect or status
     */
    public Result getCSVSource(Long id) {
        CSVSource source = CSVSource.find.byId(id);
        if(source != null) {
            return ok(views.html.sources.csvSourceDetail.render(getCurrentUser(),
                    formFactory.form(CSVSource.class).fill(source),
                    getCSVSidebarElements(),
                    getSQLSidebarElements()));
        } else {
            flash("error", "Connection Does Not Exist");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    /**
     * Update a database source
     * @param id id of the source
     * @return Http redirect
     */
    public Result updateSQLSource(Long id) {
        Form<SQLSource> connectionForm = formFactory.form(SQLSource.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not update the sql source. Please check the information you entered.");
            return redirect(controllers.routes.ConnectionController.getSQLConnection(id));
        } else {
            SQLSource source = connectionForm.get();

            SQLSource src = SQLSource.find.byId(id);
            if(src != null) {
                if (SQLSource.find.where().eq("sourceName", source.getSourceName())
                        .ne("id", id).findCount() > 0){
                    flash("error", "Error: Please select a unique connection name!");
                } else {
                    src.updateSQLSource(source);
                    flash("success", "Connection Updated!");
                }
            }
            return redirect(controllers.routes.SourceController.getSQLSource(id));
        }
    }

    /**
     * Update a CSV source
     * @param id id of the source
     * @return Http redirect
     */
    public Result updateCSVSource(Long id) {
        Form<CSVSource> sourceForm = formFactory.form(CSVSource.class).bindFromRequest();
        if (sourceForm.hasErrors()) {
            flash("error", "Error: Could not update. Please check the information you entered.");
            return redirect(controllers.routes.SourceController.getCSVSource(id));
        } else {
            CSVSource source = sourceForm.get();

            CSVSource s = CSVSource.find.byId(id);
            if(s != null) {
                if (CSVSource.find.where().eq("sourceName", source.getSourceName()).ne("id", id).findCount() > 0){
                    flash("error", "Error: Please select a unique source name!");
                } else {
                    s.csvUpdateSource(source);
                    flash("success", "Source Updated!");
                }
            }
            return redirect(controllers.routes.SourceController.getCSVSource(id));
        }
    }

    /**
     * delete a database source
     * @param id of the source
     * @return Http redirect
     */
    public Result deleteSQLSource(Long id) {
        SQLSource src = SQLSource.find.byId(id);
        if(src != null) {
            src.delete();
            flash("success", "Source successfully deleted!");
        } else {
            flash("error", "Source does not exist");
        }
        return redirect(controllers.routes.SourceController.index());
    }

    /**
     * delete a CSV source
     * @param id of the source
     * @return Http redirect
     */
    public Result deleteCSVSource(Long id) {
        CSVSource source = CSVSource.find.byId(id);
        if(source != null) {
            source.delete();
            flash("success", "Source successfully deleted!");
        } else {
            flash("error", "Source Does Not Exist.");
        }
        return redirect(controllers.routes.SourceController.index());
    }
}
