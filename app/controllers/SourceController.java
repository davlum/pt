package controllers;

import models.sources.SQLSource;
import play.data.FormFactory;
import play.mvc.*;
import utils.SidebarElement;

import javax.inject.Inject;
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
                getSidebarElements(),
                formFactory.form(SQLSource.class)));
    }


    public Result getSQLSource(Long id) {

        return ok();
    }

    public Result addSQLSource() {
        return ok("Not implemented yet");
    }

    public Result saveSQLSource(Long id) {
        return ok("Not implemented yet");
    }

    public Result del(Long id) {
        return ok("Not implemented yet");
    }

    private List<SidebarElement> getSidebarElements() {
        return SQLSource.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.SourceController.getSQLSource(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }
}
