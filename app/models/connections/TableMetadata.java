package models.connections;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TableMetadata extends Model {
    @Id
    @GeneratedValue
    private Long tableId;

    @Constraints.Required
    private String schemaName;

    @Constraints.Required
    private String tableName;

    public TableMetadata(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }
    public static Model.Finder<Long, TableMetadata> find = new Model.Finder<>(TableMetadata.class);
}
