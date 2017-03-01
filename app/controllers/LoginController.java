package controllers;

import models.users.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import utils.forms.ForgotForm;
import utils.forms.LoginForm;

import javax.inject.Inject;
import java.util.Date;

import views.html.login.*;

public class LoginController extends Controller {

    private final FormFactory formFactory;

    @Inject
    public LoginController(FormFactory formFactory){
        this.formFactory = formFactory;
    }

    public Result index() {
        // Check that the email matches a confirmed user before we redirect
        String userID = session("userID");

        if (userID != null) {
            User user = User.find.byId(Long.parseLong(session("userID")));
            if (user != null) {
                return redirect(controllers.routes.ConnectionController.index());
            } else {
                session().clear();
            }
        }
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class);
        return ok(loginView.render(loginForm));
    }

    public Result login() {
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            flash("error", loginForm.globalError().message());
            return redirect(controllers.routes.LoginController.index());
        } else {
            User user = User.findByEmail(loginForm.get().getInputEmail());
            session().clear();
            session("userID", user.getId().toString());
            user.setLastLogin(new Date());
            user.update();

            return redirect(controllers.routes.ConnectionController.index());
        }
    }

    /**
     * Logout and clean the session.
     *
     * @return Index page
     */
    public Result logout() {
        session().clear();
        flash("success", "You've been logged out!");
        return redirect(routes.LoginController.index());
    }

    public Result forgotPassword() {
        Form<ForgotForm> forgotForm = formFactory.form(ForgotForm.class);
        return ok(forgotView.render(forgotForm));
    }

    public Result forgotSubmit() {
        Form<ForgotForm> forgotForm = formFactory.form(ForgotForm.class).bindFromRequest();
        return ok();
    }

    public Result changePassword() {
        Form<ForgotForm> changeForm = formFactory.form(ForgotForm.class);
        return ok();
    }

    public Result changePasswordSubmit() {
        Form<ForgotForm> changeForm = formFactory.form(ForgotForm.class).bindFromRequest();
        return ok();
    }
}
