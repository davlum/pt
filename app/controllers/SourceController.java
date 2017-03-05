package controllers;

import models.source.SQL;
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
                formFactory.form(SQL.class)));
    }


    public Result get(Long id) {

        return ok();
    }

    public Result add() {
        return ok("Not implemented yet");
    }

    public Result save(Long id) {
        return ok("Not implemented yet");
    }

    public Result del(Long id) {
        return ok("Not implemented yet");
    }

    private List<SidebarElement> getSidebarElements() {
        return SQL.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.SourceController.get(s.getId()).url(),
                        s.getSourceName(),
                        s.getSourceDescription()))
                .collect(Collectors.toList());
    }
}
