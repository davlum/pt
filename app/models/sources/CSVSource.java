package models.sources;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import models.connections.CSVConnection;
import models.pivottable.PivotTable;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

@Entity
public class CSVSource extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Constraints.Required
    private String sourceName;

    @Constraints.Required
    private String sourceDescription;

    @Constraints.Required
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "csvconnection_id")
    @JsonIgnore
    private CSVConnection factTable;

    @ManyToMany(mappedBy = "sourceList")
    private List<CSVSourceLink> sourceLinkList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotTable> pivotTable;

    public static Model.Finder<Long, CSVSource> find = new Model.Finder<>(CSVSource.class);

    public void csvUpdateSource(CSVSource source){
        this.setSourceName(source.getSourceName());
        this.setSourceDescription(source.getSourceDescription());
        this.setFactTable(source.getFactTable());
        this.update();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public CSVConnection getFactTable() {
        return factTable;
    }

    public void setFactTable(CSVConnection factTable) {
        this.factTable = factTable;
    }

    public List<CSVSourceLink> getSourceLinkList() {
        return sourceLinkList;
    }

    public void setSourceLinkList(List<CSVSourceLink> sourceLinkList) {
        this.sourceLinkList = sourceLinkList;
    }

    public List<PivotTable> getPivotTable() {
        return pivotTable;
    }

    public void setPivotTable(List<PivotTable> pivotTable) {
        this.pivotTable = pivotTable;
    }
}
