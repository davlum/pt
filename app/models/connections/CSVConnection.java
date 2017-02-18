package models.connections;

import javax.persistence.*;
import com.avaje.ebean.Model;

/**
 * Created by hal on 2017-02-17.
 */
@Entity
public class CSVConnection extends Model {
    @Id
    @GeneratedValue
    private Integer connectionId;

    private String connectionPath;

    private String connectionName;

    private String delimiter;

    private String quoteCharacter;

    private String newlineCharacter;

    public Integer getConnectionId() {
        return connectionId;
    }

    public String getConnectionPath() {
        return connectionPath;
    }

    public String getConnectionName() {
        return connectionName;
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
}
