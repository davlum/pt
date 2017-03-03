package models.connections;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;


public class ColumnMetadata extends Model {
    @Id
    @GeneratedValue
    private Long columnId;

    @ManyToOne
    @JoinColumn(name = "tablemetadata_id")
    private TableMetadata tableMetadata;

    public ColumnMetadata(TableMetadata tableMetadata, String columnName, String columnType, String columnAlias) {
        this.tableMetadata = tableMetadata;
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnAlias = columnAlias;
    }

    public void setTableMetadata(TableMetadata tableMetadata) {

        this.tableMetadata = tableMetadata;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public Long getColumnId() {

        return columnId;
    }

    public TableMetadata getTableMetadata() {
        return tableMetadata;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    @Constraints.Required
    private String columnName;

    @Constraints.Required
    private String columnType;

    @Constraints.Required
    private String columnAlias;
    public static Model.Finder<Long, ColumnMetadata> find = new Model.Finder<>(ColumnMetadata.class);
}
