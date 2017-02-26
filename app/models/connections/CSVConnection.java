package models.connections;

import javax.persistence.*;
import com.avaje.ebean.Model;

@Entity
public class CSVConnection extends Model {

    @Id
    @GeneratedValue
    private Integer connectionId;

    private String connectionName;

    private String connectionPath;

    private String connectionDescription;

    private String delimiter;

    private String quoteCharacter;

    private String newlineCharacter;

    private Boolean header;

    public Integer getConnectionId() {
        return connectionId;
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

    public String getNewlineCharacter() {
        return newlineCharacter;
    }

    public CSVConnection(String connectionPath, String connectionName, String delimiter, String quoteCharacter, String newlineCharacter) {
        this.connectionPath = connectionPath;
        this.connectionName = connectionName;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
        this.newlineCharacter = newlineCharacter;
    }

    public CSVConnection(String connectionPath, String connectionName, String delimiter, String quoteCharacter, String newlineCharacter, Boolean header) {
        this.connectionPath = connectionPath;
        this.connectionName = connectionName;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
        this.newlineCharacter = newlineCharacter;
        this.header = header;
    }

    public static Model.Finder<Long, CSVConnection> find = new Model.Finder<>(CSVConnection.class);
}
