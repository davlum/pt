package models.connections;

import javax.persistence.*;

import com.avaje.ebean.Model;
import models.sources.CSVSource;
import models.sources.CSVSourceLink;
import play.data.validation.Constraints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent class representing CSV connection.
 */
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

    /**
     * Constructor for a CSV connection.
     */
    public CSVConnection(String connectionName, String connectionDescription) {
        this.connectName = connectionName;
        this.connectDescription = connectionDescription;
    }

    /**
     * Update a CSV connection.
     */
    public void updateCSVConnection(CSVConnection conn)
    {
        this.setConnectName(conn.getConnectName());
        this.setConnectDescription(conn.getConnectDescription());
        if (conn.getConnectionPath() != null) {
            this.setConnectionPath(conn.getConnectionPath());
        }
        this.update();
    }

    /**
     * Get all the data as a list of maps
     */
    public List<Map<String, String>> getMapList(){
        try {
            File file = new File(getConnectionPath());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] keys = new String[]{};
            List<Map<String, String>> allValues = new ArrayList<>();
            boolean firstLine = true;
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine) {
                    keys = line.split(",");
                    firstLine = false;
                } else {
                    int i = 0;
                    Map<String, String> map = new HashMap<>();
                    for (String s : line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")) {
                        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length() - 1);
                        String key = keys[i];
                        if (key.startsWith("\"") && key.endsWith("\"")) key = key.substring(1, key.length() - 1);
                        map.put(key, s);
                        i++;
                    }
                    allValues.add(map);
                }
            }
            fileReader.close();

            return allValues;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
