package models.users;

import akka.actor.ActorSystem;
import com.avaje.ebean.Model;
import play.libs.mailer.MailerClient;
import tools.Mail;
import play.data.format.Formats;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import static play.mvc.Controller.request;

/**
 * Token that is sent by email to the user when they're newly created
 * or when their password requires a change
 */

@Entity
public class Token extends Model {

    /**
     * Enum of the possibilities when a password is sent
     */
    public enum TypeToken {
        PASSWORD("reset"), EMAIL("reset"), NEWUSER("reset");
        private String urlPath;

        TypeToken(String urlPath) {
            this.urlPath = urlPath;
        }

        public String getUrlPath() {
            return urlPath;
        }
    }

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    private Long userId;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    public TypeToken type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreation;

    // -- Queries
    public static Model.Finder<String, Token> find = new Model.Finder<>(Token.class);

    /**
     * Return a new Token.
     *
     * @param user  user
     * @param type  type of token
     * @return a reset token
     */
    private static Token getNewToken(User user, TypeToken type) {
        Token token = new Token();
        token.setDateCreation(new Date());
        switch (type) {
            case NEWUSER:
                token.setToken(user.getConfirmationToken());
                break;
            default:
                token.setToken(UUID.randomUUID().toString());
                break;
        }
        token.setUserId(user.getId());
        token.setType(type);
        token.save();
        return token;
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user the current user
     * @throws java.net.MalformedURLException if token is wrong.
     */
    public static void sendMailNewUserForReset(User user, MailerClient mailerClient, ActorSystem actorSystem) throws MalformedURLException {
        sendMail(user, TypeToken.NEWUSER, mailerClient, actorSystem);
    }


    /**
     * Send the Email to confirm ask new password.
     *
     * @param user the current user
     * @throws java.net.MalformedURLException if token is wrong.
     */
    public static void sendMailResetPassword(User user, MailerClient mailerClient, ActorSystem actorSystem) throws MalformedURLException {
        sendMail(user, TypeToken.PASSWORD, mailerClient, actorSystem);
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user  the current user
     * @throws java.net.MalformedURLException if token is wrong.
     */
    public static void sendMailChangeMail(User user, MailerClient mailerClient, ActorSystem actorSystem) throws MalformedURLException {
        sendMail(user, TypeToken.EMAIL, mailerClient,actorSystem);
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user  the current user
     * @param type  token type
     * @throws java.net.MalformedURLException if token is wrong.
     */
    private static void sendMail(User user, TypeToken type, MailerClient mailerClient, ActorSystem actorSystem) throws MalformedURLException {

        Token token = getNewToken(user, type);

        String subject = null;
        String message = null;

        String urlString = request().getHeader("Referer").replace("/users", "").replace("/forgot-password", "")
                + "/" + type.getUrlPath() + "/" + token.getToken();

        URL url = new URL(urlString); // validate the URL

        switch (type) {
            case NEWUSER:
                subject = "Welcome New User!";
                message = "Welcome to our pivot table application! Click here to get started: " + url.toString();
                break;
            case PASSWORD:
                subject = "Password Change Request";
                message = "You requested a password change! Click here to get started: " + url.toString();
                break;
            case EMAIL:
                subject = "Email Change";
                message = "Your email address has changed! Click here to reset your password: " + url.toString();
                break;
        }
        String toMail = user.getEmail();
        Mail.Envelop envelop = new Mail.Envelop(subject, message, toMail);
        Mail.sendMail(envelop,mailerClient,actorSystem);
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TypeToken getType() {
        return type;
    }

    public void setType(TypeToken type) {
        this.type = type;
    }

    private Date getDateCreation() {
        return dateCreation;
    }

    private void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}