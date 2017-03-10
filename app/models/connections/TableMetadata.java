package models.connections;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;


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
}
