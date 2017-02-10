package models.pivottable;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Filter extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    @JsonIgnore
    private Field field;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "pivot_table_id")
    @JsonIgnore
    private PivotTable pivotTable;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FilterValidValue> filterValidValues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public PivotTable getPivotTable() {
        return pivotTable;
    }

    public void setPivotTable(PivotTable pivotTable) {
        this.pivotTable = pivotTable;
    }

    public List<FilterValidValue> getFilterValidValues() {
        return filterValidValues;
    }

    public void setFilterValidValues(List<FilterValidValue> filterValidValues) {
        this.filterValidValues = filterValidValues;
    }
}
