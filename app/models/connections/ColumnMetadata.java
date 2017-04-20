package models.connections;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.*;

/**
 * Persistent class for the column meta data.
 */
@Entity
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
    public ColumnMetadata(TableMetadata tableMetadata, String columnName, String columnType) {
        this.tableMetadata = tableMetadata;
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnAlias = columnName;
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

    private String columnAlias;
    public static Model.Finder<Long, ColumnMetadata> find = new Model.Finder<>(ColumnMetadata.class);

    public ObjectNode getJson() {
        ObjectNode o = Json.newObject();
        o.put("columnId", this.columnId);
        o.put("columnType", this.columnType);
        o.put("columnName", this.columnName);
        o.put("columnAlias", this.columnAlias);
        o.put("tableName", this.tableMetadata.getTableName());
        o.put("schemaName", this.tableMetadata.getSchemaName());
        o.put("tableId", this.tableMetadata.getId());
        return o;
    }
}
