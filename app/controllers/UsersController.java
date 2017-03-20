package controllers;


import play.mvc.Result;

/**
 * Controller for the users
 */
public class UsersController extends AuthController {

    public Result users() {
        return ok();
    }

}
