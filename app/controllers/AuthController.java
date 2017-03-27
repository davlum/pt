package controllers;

import models.users.User;
import play.mvc.Controller;
import play.mvc.Security;

/**
 * Class for user authentification.
 */
@Security.Authenticated(Secured.class)
public class AuthController extends Controller {

    /**
     * Method that returns the current user
     * @return User
     */
    User getCurrentUser(){
        return User.find.byId(Long.parseLong(session("userID")));
    }
}
