package utils.forms;

import play.data.validation.Constraints;


public class UserForm {

    private Long id;

    @Constraints.Required
    private String fullName;
    @Constraints.Required
    private String email;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
