package models.sources;


import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class SQLDimension extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private String dimensionTable;
    private String factField;
    private String dimensionField;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sqlsource_id")
    @JsonIgnore
    private SQLSource sqlSource;

    public String getDimensionTable() {
        return dimensionTable;
    }

    public void setDimensionTable(String dimensionTable) {
        this.dimensionTable = dimensionTable;
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
}
