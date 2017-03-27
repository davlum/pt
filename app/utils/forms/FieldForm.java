package utils.forms;

import play.data.validation.Constraints;

/**
 * Form for the fields.
 */
public class FieldForm {

    @Constraints.Required
    private Long fieldID;

    public Long getFieldID() {
        return fieldID;
    }

    public void setFieldID(Long fieldID) {
        this.fieldID = fieldID;
    }
}
