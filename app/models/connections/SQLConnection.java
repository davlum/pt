package models.connections;

import com.avaje.ebean.Model;
import models.sources.SQLSource;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Persistent class representing the database connections.
 * Is stored in the database.
 */
@Entity
public class SQLConnection extends Model {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "sqlConnection")
    public List<TableMetadata> tables;

    @Constraints.Required
    private String connectionDriver;

    @Column(unique=true)
    @Constraints.Required
    private String connectionName;

    @Constraints.Required
    private String connectionDescription;

    @Constraints.Required
    private String connectionHost;

    @Constraints.Required
    private Integer connectionPort;

    @Constraints.Required
    private String connectionUser;

    @Constraints.Required
    private String connectionPassword;

    @Constraints.Required
    private String connectionDBName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SQLSource> sources;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TableMetadata> tableMetadataList;

    public Long getId() {
        return id;
    }

    public String getConnectionDriver() { return connectionDriver; }

    public String getConnectionName() {
        return connectionName;
    }

    public String getConnectionDescription() {
        return connectionDescription;
    }

    public String getConnectionHost() {
        return connectionHost;
    }

    public Integer getConnectionPort() {
        return connectionPort;
    }

    public String getConnectionUser() {
        return connectionUser;
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public String getConnectionDBName() {
        return connectionDBName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setConnectionDriver(String connectionDriver) {
        this.connectionDriver = connectionDriver;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public void setConnectionDescription(String connectionDescription) {
        this.connectionDescription = connectionDescription;
    }

    public void setConnectionHost(String connectionHost) {
        this.connectionHost = connectionHost;
    }

    public void setConnectionPort(Integer connectionPort) {
        this.connectionPort = connectionPort;
    }

    public void setConnectionUser(String connectionUser) {
        this.connectionUser = connectionUser;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public void setConnectionDBName(String connectionDBName) {
        this.connectionDBName = connectionDBName;
    }

    public List<TableMetadata> getTableMetadataList() {
        return tableMetadataList;
    }

    public void setTableMetadataList(List<TableMetadata> tableMetadataList) {
        this.tableMetadataList = tableMetadataList;
    }

    public SQLConnection(String name, String driver, String desc, String host, Integer port,
                         String db, String user, String password)
    {
        connectionName = name;
        connectionDescription = desc;
        connectionHost = host;
        connectionPort = port;
        connectionDBName = db;
        connectionUser = user;
        connectionPassword = password;
        connectionDriver = driver;
    }

    public void updateSQLConnection(SQLConnection conn, List<TableMetadata> metadataList)
    {
        this.setConnectionName(conn.getConnectionName());
        this.setConnectionDescription(conn.getConnectionDescription());
        this.setConnectionHost(conn.getConnectionHost());
        this.setConnectionPort(conn.getConnectionPort());
        this.setConnectionDBName(conn.getConnectionDBName());
        this.setConnectionUser(conn.getConnectionUser());
        this.setConnectionPassword(conn.getConnectionPassword());
        this.setConnectionDriver(conn.getConnectionDriver());
        this.setTableMetadataList(metadataList);
        this.update();
    }

    public static Model.Finder<Long, SQLConnection> find = new Model.Finder<>(SQLConnection.class);

    public String getUrl()
    {
        return "jdbc:" + connectionDriver + "://" + connectionHost + ":" + connectionPort + "/" + connectionDBName;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(
                getUrl(),
                connectionUser,
                connectionPassword);
    }
}
