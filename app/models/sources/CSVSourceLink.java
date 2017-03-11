package models.sources;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import models.connections.CSVConnection;

import javax.persistence.*;
import java.util.List;

@Entity
public class CSVSourceLink extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private String factField;

    private String dimensionField;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "csvconnection_id")
    @JsonIgnore
    private CSVConnection dimensionConnection;

    @ManyToMany
    private List<CSVSource> sourceList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFactField() {
        return factField;
    }

    public void setFactField(String factField) {
        this.factField = factField;
    }

    public String getDimensionField() {
        return dimensionField;
    }

    public void setDimensionField(String dimensionField) {
        this.dimensionField = dimensionField;
    }

    public CSVConnection getDimensionConnection() {
        return dimensionConnection;
    }

    public void setDimensionConnection(CSVConnection dimensionConnection) {
        this.dimensionConnection = dimensionConnection;
    }

    public List<CSVSource> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<CSVSource> sourceList) {
        this.sourceList = sourceList;
    }
}
