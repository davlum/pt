package utils.forms;

import play.data.validation.Constraints;

public class ValueForm {

    @Constraints.Required
    private Long fieldID;

    private Long valueTypeID;

    public Long getFieldID() {
        return fieldID;
    }

    public void setFieldID(Long fieldID) {
        this.fieldID = fieldID;
    }

    public Long getValueTypeID() {
        return valueTypeID;
    }

    public void setValueTypeID(Long valueTypeID) {
        this.valueTypeID = valueTypeID;
    }
}
