package models.connections;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;

/**
 * Persistent class for the table metadata
 * Is stored in the database.
 */
@Entity
public class TableMetadata extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    private String schemaName;

    @Constraints.Required
    private String tableName;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sqlconnection_id")
    @JsonIgnore
    private SQLConnection sqlConnection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {

        return schemaName;
    }


    public String getTableName() {
        return tableName;
    }

    public SQLConnection getSqlConnection() {
        return sqlConnection;
    }

    public void setSqlConnection(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public TableMetadata(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }
    public static Model.Finder<Long, TableMetadata> find = new Model.Finder<>(TableMetadata.class);

    public List<TableMetadata> getTablesByConnectionId(Long id) {
        return find.where().eq("sqlconnection_id", id).findList();
    }

    public ObjectNode getJson() {
        ObjectNode o = Json.newObject();
        o.put("tableId", this.id);
        o.put("tableName", this.tableName);
        o.put("schemaName", this.schemaName);
        o.put("connectionId", this.sqlConnection.getId());
        return o;
    }
}
