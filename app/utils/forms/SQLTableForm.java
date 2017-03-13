package utils.forms;

import play.data.validation.Constraints;

public class SQLTableForm {

    @Constraints.Required
    private Long sqlSourceID;

    @Constraints.Required
    private String sqlTableName;

    @Constraints.Required
    private String sqlTableDescription;

    public Long getSqlSourceID() {
        return sqlSourceID;
    }

    public void setSqlSourceID(Long sqlSourceID) {
        this.sqlSourceID = sqlSourceID;
    }

    public String getSqlTableName() {
        return sqlTableName;
    }

    public void setSqlTableName(String sqlTableName) {
        this.sqlTableName = sqlTableName;
    }

    public String getSqlTableDescription() {
        return sqlTableDescription;
    }

    public void setSqlTableDescription(String sqlTableDescription) {
        this.sqlTableDescription = sqlTableDescription;
    }
}
