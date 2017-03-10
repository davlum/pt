package models.connections;

import javax.persistence.*;

import com.avaje.ebean.Model;

@Entity
public class CSVConnection extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private String connectName;

    private String connectionPath;

    private String connectDescription;

    public CSVConnection(String connectionName, String connectionDescription) {
        this.connectName = connectionName;
        this.connectDescription = connectionDescription;
    }

    public void updateCSVConnection(CSVConnection conn)
    {
        this.setConnectName(conn.getConnectName());
        this.setConnectDescription(conn.getConnectDescription());
        this.update();
    }

    public static Model.Finder<Long, CSVConnection> find = new Model.Finder<>(CSVConnection.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConnectName() {
        return connectName;
    }

    public void setConnectName(String connectName) {
        this.connectName = connectName;
    }

    public String getConnectionPath() {
        return connectionPath;
    }

    public void setConnectionPath(String connectionPath) {
        this.connectionPath = connectionPath;
    }

    public String getConnectDescription() {
        return connectDescription;
    }

    public void setConnectDescription(String connectDescription) {
        this.connectDescription = connectDescription;
    }
}
