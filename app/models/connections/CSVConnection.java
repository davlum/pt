package models.connections;

import javax.persistence.*;

import com.avaje.ebean.Model;
import models.sources.CSVSource;
import models.sources.CSVSourceLink;
import play.data.validation.Constraints;

import java.util.List;

@Entity
public class CSVConnection extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    @Column(unique=true)
    private String connectName;

    private String connectionPath;

    @Constraints.Required
    private String connectDescription;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CSVSource> csvSources;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CSVSourceLink> sourceLinks;

    public CSVConnection(String connectionName, String connectionDescription) {
        this.connectName = connectionName;
        this.connectDescription = connectionDescription;
    }

    public void updateCSVConnection(CSVConnection conn)
    {
        this.setConnectName(conn.getConnectName());
        this.setConnectDescription(conn.getConnectDescription());
        if (conn.getConnectionPath() != null) {
            this.setConnectionPath(conn.getConnectionPath());
        }
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

    public List<CSVSourceLink> getSourceLinks() {
        return sourceLinks;
    }

    public void setSourceLinks(List<CSVSourceLink> sourceLinks) {
        this.sourceLinks = sourceLinks;
    }

    public List<CSVSource> getCsvSources() {
        return csvSources;
    }

    public void setCsvSources(List<CSVSource> csvSources) {
        this.csvSources = csvSources;
    }
}
