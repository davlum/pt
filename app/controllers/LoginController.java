package controllers;

import akka.actor.ActorSystem;
import models.users.Token;
import models.users.User;
import play.api.libs.mailer.MailerClient;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import tools.Hash;
import utils.forms.ChangePwdForm;
import utils.forms.ForgotForm;
import utils.forms.LoginForm;

import javax.inject.Inject;

import views.html.login.*;

/**
 * Controller to login or logout from the application
 */
public class LoginController extends Controller {

    private final FormFactory formFactory;
    private final MailerClient mailerClient;
    private final ActorSystem actorSystem;

    /**
     * Constructor for the class.
     * @param formFactory
     */
    @Inject
    public LoginController(FormFactory formFactory, MailerClient mailerClient, ActorSystem actorSystem) {
        this.formFactory = formFactory;
        this.mailerClient = mailerClient;
        this.actorSystem = actorSystem;
    }

    /**
     * Home page of the application with the login form
     * @return HTTP status
     */
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

    /**
     * Redirect based on login information
     * @return HTTP redirect
     */
    public Result login() {
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            flash("error", loginForm.globalError().message());
            return redirect(controllers.routes.LoginController.index());
        } else {
            User user = User.findByEmail(loginForm.get().getInputEmail());
            session().clear();
            session("userID", user.getId().toString());
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
        if (!forgotForm.hasErrors()) {
            User user = User.findByEmail(forgotForm.get().getInputEmail());
            System.out.println(forgotForm.get().getInputEmail());
            if (user != null) {
                try {
                    Token.sendMailResetPassword(user, mailerClient, actorSystem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(forgotForm.errorsAsJson());
        }
        return redirect(routes.LoginController.index());
    }

    public Result changePassword(String token) {
        Form<ChangePwdForm> changeForm = formFactory.form(ChangePwdForm.class);
        return ok(changePwd.render(changeForm, token));
    }

    public Result changePasswordSubmit(String token) {
        Form<ChangePwdForm> changeForm = formFactory.form(ChangePwdForm.class).bindFromRequest();
        if (!changeForm.hasErrors()) {
            String password = changeForm.get().getPassword();
            String confirm = changeForm.get().getConfirm();
            if (!password.equals(confirm)){
                flash("error", "Passwords do not match!");
                return redirect(routes.LoginController.changePassword(token));
            }

            User user = User.findByConfirmationToken(token);
            try {
                user.setPasswordHash(Hash.createPassword(changeForm.get().getPassword()));
                user.update();
                flash("success", "Password successfully updated!");
            } catch (Exception e){
                System.out.println(password + " " + confirm);
                e.printStackTrace();
            }
        } else {
            System.out.println(changeForm.errorsAsJson());
        }

        return redirect(routes.LoginController.index());
    }
}
