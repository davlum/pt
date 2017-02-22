package models.connections;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by hal on 2017-02-17.
 */
@Entity
public class SQLConnection extends Model {
    @Id
    @GeneratedValue
    private Integer id;

    private String connectionDriver;

    private String connectionName;

    private String connectionDescription;

    private String connectionHost;

    private Integer connectionPort;

    private String connectionUser;

    private String connectionPassword;

    private String connectionDBName;

    public Integer getId() {
        return id;
    }

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

    public SQLConnection(String name, String desc, String host, Integer port, String db, String user, String password)
    {
        connectionName = name;
        connectionDescription = desc;
        connectionHost = host;
        connectionPort = port;
        connectionDBName = db;
        connectionUser = user;
        connectionPassword = password;
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
