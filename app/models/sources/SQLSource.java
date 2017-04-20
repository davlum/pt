package models.sources;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import models.connections.SQLConnection;
import models.pivottable.Field;
import models.pivottable.FieldType;
import models.pivottable.PivotTable;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL Source Class
 */
@Entity
public class SQLSource extends Model {
    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    private String sourceName;

    private String sourceDescription;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sqlconnection_id")
    @JsonIgnore
    private SQLConnection connection;

    @Constraints.Required
    private String factTable;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SQLDimension> dimensionTables;

    private String fromClause;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotTable> pivotTable;

    public static Model.Finder<Long, SQLSource> find = new Model.Finder<>(SQLSource.class);

    /**
     * Maps dbTypes to FieldTypes
     * @param dbType value
     * @return a FieldType
     */
    private static FieldType mapDatabaseFieldType(String dbType)
    {
        switch (dbType) {
            case "bigint":
            case "smallint":
            case "int":
            case "int8":
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

    /**
     * Given a list of fields return the data associated with those fields
     * as a map of string field names to strings of the data. Currently limited
     * to 10,000 lines per field.
     * @param fields a list of fields
     * @return map of field and their data.
     */
    public List<Map<String, String>> getMapList(List<Field> fields){
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(getConnection().getUrl(),
                    getConnection().getConnectionUser(), getConnection().getConnectionPassword());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+factTable+" LIMIT 10000;");
            ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    Field field = fields.stream().filter(f -> f.getFieldName().equals(key)).findFirst().orElse(null);
                    if (field != null && field.getFieldType().equals(FieldType.Date)){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date val = resultSet.getDate(key);
                        map.put(key, val != null? format.format(val) : "null");
                    } else if(field != null && field.getFieldType().equals(FieldType.Date)){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date val = resultSet.getDate(key);
                        map.put(key, val != null? format.format(val) : "null");
                    } else {
                        map.put(key, resultSet.getString(key));
                    }
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

    /**
     * Gets a list of fields from the given database source
     * @return list of fields
     */
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
                System.out.println(meta.getColumnName(i) + " " + meta.getColumnTypeName(i));
                FieldType type = mapDatabaseFieldType(meta.getColumnTypeName(i));
                Field field = new Field();
                field.setFieldName(key);
                field.setFieldType(type);
                list.add(field);
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Method that return a list of column names
     * @param table table name
     * @return list of strings
     */
    public List<String> fieldNames(String table){
        List<String> list = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(getConnection().getUrl(),
                    getConnection().getConnectionUser(), getConnection().getConnectionPassword());
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+table+" LIMIT 1;");
            ResultSetMetaData meta = resultSet.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                list.add(key);
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Given a list of fields and a list of aggregate functions,
     * return a result set
     * @param dimensions of the data
     * @param aggregates list of aggregate functions
     * @return a ResultSet
     * @throws SQLException if an connection error occurs
     */
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
