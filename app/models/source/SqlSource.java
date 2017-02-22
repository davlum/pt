package models.source;

import com.avaje.ebean.Model;
import models.connections.SQLConnection;
import models.pivottable.Field;
import models.pivottable.FieldType;

import javax.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SqlSource extends Model {
    @Id
    @GeneratedValue
    private Integer sourceId;

    private String sourceName;

    @ManyToOne
    @JoinColumn(name = "sqlconnection_id")
    private SQLConnection connection;

    private String factTable;

    private ArrayList<String> dimensionTables;

    private String fromClause;

    public SqlSource(Long connectionId, String name, String fact, String fromClause)
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

        String stmt = "SELECT " + String.join(", ", fields) + " " + fromClause + " GROUP BY " + String.join(", dimensions");
        Connection conn = connection.connect();
        Statement s = conn.createStatement();
        ResultSet r =  s.executeQuery(stmt);
        conn.close();
        return r;
    }
}
