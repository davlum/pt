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

    @Constraints.Required
    private String columnName;

    @Constraints.Required
    private String columnType;

    @Constraints.Required
    private String columnAlias;
    public static Model.Finder<Long, ColumnMetadata> find = new Model.Finder<>(ColumnMetadata.class);
}
