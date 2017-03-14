package models.sources;

import com.avaje.ebean.Model;
import models.connections.SQLConnection;
import models.pivottable.Field;
import models.pivottable.FieldType;
import models.pivottable.PivotTable;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Constraints.Required
    private String factTable;

    private String fromClause;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotTable> pivotTable;

    public static Model.Finder<Long, SQLSource> find = new Model.Finder<>(SQLSource.class);

    public static FieldType mapDatabaseFieldType(String dbType)
    {
        switch (dbType) {
            case "bigint":
            case "smallint":
            case "int":
            case "integer":
            case "serial":
            case "bigserial":
                return FieldType.Long;

            case "boolean":
                return FieldType.Boolean;

            case "date":
                return FieldType.Date;

            case "timestamp with time zone":
            case "timestamp without time zone":
                return FieldType.DateTime;

            case "text":
            case "varchar":
            case "character":
            case "character varying":
                return FieldType.String;

            case "double precicion":
            case "numeric":
            case "real":
            case "decimal":
                return FieldType.Double;
            default:
                return FieldType.String;
        }
    }

    public List<Map<String, String>> getMapList(){
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(getConnection().getUrl(),
                    getConnection().getConnectionUser(), getConnection().getConnectionPassword());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+factTable+";");
            ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    map.put(key, resultSet.getString(key));
                }
                list.add(map);
            }
            resultSet.close();
            statement.close();
            connection.close();

            //list.forEach(System.out::println);
        } catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    public List<Field> getFieldList(){
        List<Field> list = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(getConnection().getUrl(),
                    getConnection().getConnectionUser(), getConnection().getConnectionPassword());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+factTable+" LIMIT 1;");
            ResultSetMetaData meta = resultSet.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                FieldType type = mapDatabaseFieldType(meta.getColumnTypeName(i));
                Field field = new Field();
                field.setFieldName(key);
                field.setFieldType(type);
                list.add(field);
            }
            resultSet.close();
            statement.close();
            connection.close();

            //list.forEach(System.out::println);
        } catch (SQLException e){
            e.printStackTrace();
        }

        return list;
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
        this.setConnection(src.getConnection());
        this.setFromClause(src.getFromClause());
        this.update();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public void setConnection(SQLConnection connection) {
        this.connection = connection;
    }

    public String getFactTable() {
        return factTable;
    }

    public void setFactTable(String factTable) {
        this.factTable = factTable;
    }

    public String getFromClause() {
        return fromClause;
    }

    public void setFromClause(String fromClause) {
        this.fromClause = fromClause;
    }

    public List<PivotTable> getPivotTable() {
        return pivotTable;
    }

    public void setPivotTable(List<PivotTable> pivotTable) {
        this.pivotTable = pivotTable;
    }
}
