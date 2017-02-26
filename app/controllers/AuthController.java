package controllers;

import models.users.User;
import play.mvc.Controller;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class AuthController extends Controller {

    User getCurrentUser(){
        return User.find.byId(Long.parseLong(session("userID")));
    }
}
