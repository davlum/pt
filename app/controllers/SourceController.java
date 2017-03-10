package controllers;

import models.sources.SQLSource;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hal on 2017-02-22.
 */
public class SourceController extends AuthController {
    private final FormFactory formFactory;

    @Inject
    public SourceController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {

        return ok(views.html.sources.index.render(
                getCurrentUser(),
                getSQLSidebarElements(),
                getCSVSidebarElements(),
                formFactory.form(SQLSource.class),false));
    }


    public Result getSQLSource(Long id) {

        return ok();
    }

    public Result addSQLSource() {
        Form<SQLSource> sourceForm = formFactory.form(SQLSource.class).bindFromRequest();
        if (sourceForm.hasErrors()) {
            flash("error", "Error: Could not add connection. Please check the information you entered.");
            return ok(views.html.sources.index.render(
                    getCurrentUser(),
                    getSQLSidebarElements(),
                    getCSVSidebarElements(),
                    sourceForm,
                    false));
        } else {
            SQLSource source = sourceForm.get();
            source.save();
            return redirect(controllers.routes.SourceController.index());
        }
    }

    public Result saveSQLSource(Long id) {
        return ok("Not implemented yet");
    }

    public Result del(Long id) {
        return ok("Not implemented yet");
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
        List<SidebarElement> elements = new ArrayList<>();
        return elements;
    }
}
