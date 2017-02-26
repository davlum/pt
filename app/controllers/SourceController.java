package controllers;

import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.connections;

import javax.inject.Inject;

/**
 * Created by hal on 2017-02-22.
 */
public class SourceController extends Controller {
    private final FormFactory formFactory;


    @Inject
    public SourceController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        return ok();
    }

}
