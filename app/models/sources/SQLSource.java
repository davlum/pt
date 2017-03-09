package models.sources;

import com.avaje.ebean.Model;
import models.connections.SQLConnection;
import models.pivottable.Field;
import models.pivottable.FieldType;

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

    private String sourceName;

    private String sourceDescription;

    @ManyToOne
    @JoinColumn(name = "sqlconnection_id")
    private SQLConnection connection;

    @Transient
    private Long connId;

    private String factTable;

    private List<String> dimensionTables;

    private String fromClause;

    public SQLSource(Long connectionId, String name, String fact, String fromClause)
    {
        connection = SQLConnection.find.byId(connectionId);
        sourceName = name;
        factTable = fact;
        this.fromClause = fromClause;
        this.dimensionTables = new ArrayList<>();
    }

    public void addDimensionTable(String dimensionTable)
    {
        dimensionTables.add(dimensionTable);
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

    public List<String> getDimensionTables() {
        return dimensionTables;
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
    public List<Field> fetchAvailableColumns() throws SQLException {

        Connection conn = connection.connect();
        String metadata_stmt = "SELECT table_name, column_name, data_type FROM information_schema.columns WHERE table_name = ";
        ArrayList<Field> fields = new ArrayList<>();
        String stmt_fact = metadata_stmt + "'" + factTable + "'";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(stmt_fact);

        while (resultSet.next()) {
            String t = resultSet.getString("table_name");
            String c = resultSet.getString("column_name");
            String d = resultSet.getString("data_type");
            try {
                FieldType fieldType = mapDatabaseFieldType(d);
                Field f = new Field(c, fieldType, t);
                fields.add(f);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        for (String dimensionTable: dimensionTables) {
            String stmt_dimension = metadata_stmt + "'" + dimensionTable + "'";
            resultSet = stmt.executeQuery(stmt_dimension);
            while (resultSet.next()) {
                String t = resultSet.getString("table_name");
                String c = resultSet.getString("column_name");
                String d = resultSet.getString("data_type");
                try {
                    FieldType fieldType = mapDatabaseFieldType(d);
                    Field f = new Field(c, fieldType, t);
                    fields.add(f);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        conn.close();
        return fields;
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

    public static Model.Finder<Long, SQLSource> find = new Model.Finder<>(SQLSource.class);
}
