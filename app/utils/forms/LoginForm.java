package utils.forms;

import exceptions.UserException;
import models.users.User;
import play.data.validation.Constraints;

/**
 * Form of the login page
 */
public class LoginForm {

    @Constraints.Required
    private String inputEmail;

    @Constraints.Required
    private String inputPassword;

    public String validate() {

        User user;
        try {
            user = User.authenticate(inputEmail, inputPassword);
            if (user == null) {
                return "Invalid User or password. Please, try again.";
            }
        } catch (UserException e) {
            return "An error has occurred.";
        }
        return null;
    }

    public String getInputEmail() {
        return inputEmail;
    }

    public void setInputEmail(String inputEmail) {
        this.inputEmail = inputEmail;
    }

    public String getInputPassword() {
        return inputPassword;
    }

    public void setInputPassword(String inputPassword) {
        this.inputPassword = inputPassword;
    }
}
