package utils;

import play.data.validation.Constraints;

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
