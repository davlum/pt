package models.sources;

import com.avaje.ebean.Model;
import models.connections.SQLConnection;
import models.pivottable.Field;
import models.pivottable.FieldType;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class SQLSource extends Model {
    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    private String sourceName;

    private String sourceDescription;

    @ManyToOne
    @JoinColumn(name = "sqlconnection_id")
    private SQLConnection connection;

    @Transient
    private Long connectionId;

    @Constraints.Required
    private String factTable;

    @Constraints.Required
    private String fromClause;

    public static Model.Finder<Long, SQLSource> find = new Model.Finder<>(SQLSource.class);

    public Long getConnectionId() {
        return connection.getId();
    }

    public void setConnectionId(Long id)
    {
        this.connection = SQLConnection.find.byId(id);
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public void setConnection(SQLConnection connection) {
        this.connection = connection;
    }

    public void setFactTable(String factTable) {
        this.factTable = factTable;
    }

    public void setFromClause(String fromClause) {
        this.fromClause = fromClause;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public Long getId() {
        return id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getFactTable() {
        return factTable;
    }

    public String getFromClause() {
        return fromClause;
    }

    public static FieldType mapDatabaseFieldType(String dbType)
    {
        switch (dbType) {
            case "bigint":
            case "smallint":
            case "int":
            case "integer":
                return FieldType.Integer;

            case "boolean":
                return FieldType.Boolean;

            case "date":
                return FieldType.Date;

            case "timestamp with time zone":
            case "timestamp without time zone":
                return FieldType.DateTime;

            case "text":
            case "character":
            case "character varying":
                return FieldType.String;

            case "double precicion":
            case "numeric":
            case "real":
                return FieldType.Number;
            default:
                throw new IllegalArgumentException("Field Type not Supported");
        }
    }


    public ResultSet executeQuery(List<Field> dimensions,
                        List<AggregateExpression> aggregates)
            throws SQLException
    {
        ArrayList<String> fields = new ArrayList<>();
        for (Field s: dimensions) {
            fields.add(s.qualifiedName());
        }

        for (AggregateExpression s: aggregates) {
            fields.add(s.toString());
        }
        String stmt = "SELECT "
                + String.join(", ", fields) + " "
                + fromClause
                + " GROUP BY "
                + String.join(", ", dimensions.stream().map(Field::qualifiedName).collect(Collectors.toList()));
        Connection conn = connection.connect();
        Statement s = conn.createStatement();
        ResultSet r =  s.executeQuery(stmt);
        conn.close();
        return r;
    }

    public void updateSQLSource(SQLSource src)
    {
        this.setSourceName(src.getSourceName());
        this.setSourceDescription(src.getSourceDescription());
        this.setConnectionId(src.getConnectionId());
        this.setFromClause(src.getFromClause());
        this.update();
    }

}
