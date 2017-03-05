package models.connections;

import javax.persistence.*;
import com.avaje.ebean.Model;

@Entity
public class CSVConnection extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private String connectionName;

    private String connectionPath;

    private String connectionDescription;

    private String delimiter;

    private String quoteCharacter;

    private Boolean header;

    public Long getId() {
        return id;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getConnectionPath() {
        return connectionPath;
    }

    public String getConnectionDescription() {
        return connectionDescription;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getQuoteCharacter() {
        return quoteCharacter;
    }


    public CSVConnection(String connectionPath, String connectionName, String delimiter, String quoteCharacter, String connectionDescription) {
        this.connectionPath = connectionPath;
        this.connectionName = connectionName;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
        this.header = false;
        this.connectionDescription = connectionDescription;
    }

    public CSVConnection(String connectionPath, String connectionName, String delimiter, String quoteCharacter, String connectionDescription, Boolean header) {
        this.connectionPath = connectionPath;
        this.connectionName = connectionName;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
        this.connectionDescription = connectionDescription;
        this.header = header;
    }

    public static Model.Finder<Long, CSVConnection> find = new Model.Finder<>(CSVConnection.class);
}
