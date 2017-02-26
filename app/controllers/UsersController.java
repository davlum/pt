package controllers;


import play.mvc.Result;

public class UsersController extends AuthController {

    public Result users() {
        return ok();
    }

}
