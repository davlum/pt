package controllers;


import controllers.*;
import models.sources.CSVSource;
import models.sources.SQLSource;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;

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
                .stream().map(s -> SidebarElement.newInstance(
                        controllers.routes.SourceController.getSQLSource(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }

    private List<SidebarElement> getCSVSidebarElements() {
        return CSVSource.find.all()
                .stream().map(s -> SidebarElement.newInstance(
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
        return ok();
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

    public Result getCSVSource(Long id) {
        return ok();
    }

    public Result updateCSVSource(Long id) {
        return ok();
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
        return ok();
    }
}
