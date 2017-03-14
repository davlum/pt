package controllers;

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

public class SourceController extends AuthController {
    private final FormFactory formFactory;

    @Inject
    public SourceController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        return ok(views.html.sources.index.render(
                getCurrentUser(),
                formFactory.form(SQLSource.class),
                formFactory.form(CSVSource.class),
                getSQLSidebarElements(),
                getCSVSidebarElements(),
                false));
    }

    public Result indexCSV() {
        return ok(views.html.sources.index.render(
                getCurrentUser(),
                formFactory.form(SQLSource.class),
                formFactory.form(CSVSource.class),
                getSQLSidebarElements(),
                getCSVSidebarElements(),
                true));
    }

    private List<SidebarElement> getSQLSidebarElements() {
        return SQLSource.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.SourceController.getSQLSource(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }

    private List<SidebarElement> getCSVSidebarElements() {
        return CSVSource.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.SourceController.getCSVSource(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }

    public Result addSQLSource() {
        Form<SQLSource> sourceForm = formFactory.form(SQLSource.class).bindFromRequest();
        if (sourceForm.hasErrors()) {
            flash("error", "Error: Could not add connection. " +
                    "Please check the information you entered." +
                    sourceForm.errors());
            return ok(views.html.sources.index.render(
                    getCurrentUser(),
                    sourceForm,
                    formFactory.form(CSVSource.class),
                    getSQLSidebarElements(),
                    getCSVSidebarElements(),
                    false));
        } else {
            SQLSource source = sourceForm.get();
            source.save();
            return redirect(controllers.routes.SourceController.index());
        }
    }

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

    public Result getCSVSource(Long id) {
        CSVSource source = CSVSource.find.byId(id);
        if(source != null) {
            return ok(csvSourceDetail.render(getCurrentUser(),
                    formFactory.form(CSVSource.class).fill(source),
                    getCSVSidebarElements(),
                    getSQLSidebarElements()));
        } else {
            flash("error", "Connection Does Not Exist");
            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    public Result updateSQLSource(Long id) {
        Form<SQLSource> connectionForm = formFactory.form(SQLSource.class).bindFromRequest();
        if (connectionForm.hasErrors()) {
            flash("error", "Error: Could not update the sql source. " +
                    "Please check the information you entered.");
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
