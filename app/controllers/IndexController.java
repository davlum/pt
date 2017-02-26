package controllers;

import play.mvc.Result;
import play.mvc.Controller;
import views.html.index;

public class IndexController extends Controller {
    public Result index() {
        return ok(index.render());
    }
}
