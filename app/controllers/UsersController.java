package controllers;


import akka.actor.ActorSystem;
import models.users.Token;
import models.users.User;
import play.api.libs.mailer.MailerClient;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import utils.SidebarElement;
import utils.forms.UserForm;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for the users
 */
public class UsersController extends AuthController {

    private final FormFactory formFactory;
    private final MailerClient mailerClient;
    private final ActorSystem actorSystem;

    /**
     * Constructor for the class
     * @param formFactory FormFactory injection
     * @param mailerClient MailerClient injection
     * @param actorSystem ActorSystem injection
     */
    @Inject
    public UsersController(FormFactory formFactory, MailerClient mailerClient, ActorSystem actorSystem) {
        this.formFactory = formFactory;
        this.mailerClient = mailerClient;
        this.actorSystem = actorSystem;
    }

    /**
     * Display the list of users
     * @return Http status
     */
    public Result users() {
        return ok(views.html.users.index.render(getCurrentUser(),
                formFactory.form(UserForm.class), getUserSidebarElements()));
    }

    /**
     * Get the database elements to display on the sidebar
     * @return list of SQL elements
     */
    private List<SidebarElement> getUserSidebarElements() {
        return User.find.all()
                .stream().filter(u -> !u.getId().equals(getCurrentUser().getId()))
                .map(s -> new SidebarElement(
                        controllers.routes.UsersController.getUser(s.getId()).url(),
                        s.getFullName(),
                        s.getEmail()))
                .collect(Collectors.toList());
    }


    /**
     * Add a new user
     * @return Http status
     */
    public Result addUser(){
        Form<UserForm> userForm = formFactory.form(UserForm.class).bindFromRequest();
        if (userForm.hasErrors()) {
            flash("error", "Error: Could not add user. Please check the information you entered.");
            return ok(views.html.users.index.render(getCurrentUser(),
                    formFactory.form(UserForm.class), getUserSidebarElements()));
        } else {
            UserForm user = userForm.get();
            if (User.find.where().eq("email", user.getEmail()).findCount() > 0){
                flash("error", "Error: Please select a unique email address!");
                return ok(views.html.users.index.render(getCurrentUser(),
                        formFactory.form(UserForm.class), getUserSidebarElements()));
            }

            User newUser = new User();
            newUser.setFullName(user.getFullName());
            newUser.setEmail(user.getEmail());
            newUser.setConfirmationToken(UUID.randomUUID().toString());
            newUser.save();


            try {
                InternetAddress internetAddress = new InternetAddress(user.getEmail());
                internetAddress.validate();

                Token.sendMailNewUserForReset(newUser, mailerClient, actorSystem);
            } catch (Exception e){
                e.printStackTrace();
            }


            flash("success", "New User Added");
            return redirect(controllers.routes.UsersController.users());
        }
    }

    /**
     * Retrieve user info
     * @param id of the user
     * @return Http status
     */
    public Result getUser(Long id){
        User user = User.find.byId(id);
        UserForm userForm = new UserForm();
        if(user != null) {
            userForm.setId(user.getId());
            userForm.setEmail(user.getEmail());
            userForm.setFullName(user.getFullName());
            return ok(views.html.users.userDetail.render(getCurrentUser(),
                    formFactory.form(UserForm.class).fill(userForm),
                    getUserSidebarElements()));
        } else {
            flash("error", "User Does Not Exist");
            return redirect(controllers.routes.UsersController.users());
        }
    }

    /**
     * Update user info
     * @param id of the user
     * @return Http status
     */
    public Result updateUser(Long id){
        Form<UserForm> userForm = formFactory.form(UserForm.class).bindFromRequest();
        if (userForm.hasErrors()) {
            flash("error", "Error: Could not update the user. Please check the information you entered.");
            return redirect(controllers.routes.UsersController.getUser(id));
        } else {
            UserForm userF = userForm.get();

            User user = User.find.byId(id);
            if (User.find.where().eq("email", userF.getEmail())
                    .ne("id", id).findCount() > 0){
                flash("error", "Error: Please select a unique email address!");
                return redirect(controllers.routes.UsersController.getUser(id));
            }

            if (user != null) {
                String oldEmail = user.getEmail();

                user.setFullName(userF.getFullName());
                user.setEmail(userF.getEmail());

                if(!oldEmail.equals(user.getEmail())) {
                    user.setConfirmationToken(UUID.randomUUID().toString());
                    try {
                        InternetAddress internetAddress = new InternetAddress(user.getEmail());
                        internetAddress.validate();

                        Token.sendMailChangeMail(user, mailerClient, actorSystem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                user.save();

                flash("success", "User Updated!");
            }
            return redirect(controllers.routes.UsersController.users());
        }
    }

    /**
     * Delete a user
     * @param id of the user
     * @return Http status
     */
    public Result deleteUser(Long id){
        User user = User.find.byId(id);
        if(user != null) {
            user.delete();
            flash("success", "User successfully deleted!");
        } else {
            flash("error", "User does not exist");
        }
        return redirect(controllers.routes.UsersController.users());
    }

}
