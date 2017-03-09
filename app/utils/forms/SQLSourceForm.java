package utils.forms;

import play.data.validation.Constraints;

import java.util.List;

/**
 * Created by hal on 2017-03-07.
 */
public class SQLSourceForm {
    public String getSqlSourceName() {
        return sqlSourceName;
    }

    public Long getSqlConnectionID() {
        return sqlConnectionID;
    }

    public String getFactTable() {
        return factTable;
    }

    public List<String> getDimensionTables() {
        return dimensionTables;
    }

    public String getFromClause() {
        return fromClause;
    }

    @Constraints.Required
    private String sqlSourceName;

    @Constraints.Required
    private Long sqlConnectionID;

    @Constraints.Required
    private String factTable;

    private List<String> dimensionTables;

    @Constraints.Required
    private String fromClause;
}
