package controllers;

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
            source.save();
            return redirect(controllers.routes.SourceController.index());
        }
    }

    public Result addCSVSource() {
        return ok();
    }


    public Result getSQLSource(Long id) {
        return ok();
    }

    public Result getCSVSource(Long id) {
        return ok();
    }

    public Result updateSQLSource(Long id) {
        return ok();
    }

    public Result updateCSVSource(Long id) {
        return ok();
    }

    public Result deleteSQLSource(Long id) {
        return ok();
    }

    public Result deleteCSVSource(Long id) {
        return ok();
    }
}
