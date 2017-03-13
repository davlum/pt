package utils.forms;

import play.data.validation.Constraints;

public class CSVTableForm {

    @Constraints.Required
    private Long csvSourceID;

    @Constraints.Required
    private String csvTableName;

    @Constraints.Required
    private String csvTableDescription;

    public Long getCsvSourceID() {
        return csvSourceID;
    }

    public void setCsvSourceID(Long csvSourceID) {
        this.csvSourceID = csvSourceID;
    }

    public String getCsvTableName() {
        return csvTableName;
    }

    public void setCsvTableName(String csvTableName) {
        this.csvTableName = csvTableName;
    }

    public String getCsvTableDescription() {
        return csvTableDescription;
    }

    public void setCsvTableDescription(String csvTableDescription) {
        this.csvTableDescription = csvTableDescription;
    }
}
