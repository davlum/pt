package utils.forms;

/**
 * Pivot table sharing form
 */
public class PermissionForm {

    private Long userID;

    private String permission;


    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
