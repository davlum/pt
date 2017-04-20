package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Security class that checks the
 * session of a user.
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return Controller.session("userID");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        Controller.flash("error", "You need to sign in!");
        return redirect(controllers.routes.LoginController.index());
    }



}