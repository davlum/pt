package controllers;

import play.mvc.Result;
import play.mvc.Controller;
import views.html.index;
/**
 * Created by hal on 2017-02-22.
 */
public class IndexController extends Controller {
    public Result index() {
        return ok(index.render());
    }
}
