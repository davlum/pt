package models.connections;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by hal on 2017-02-17.
 */
@Entity
public class SQLConnection extends Model {
    @Id
    @GeneratedValue
    private Integer id;

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
}