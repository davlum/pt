package models.pivottable;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class PivotValueType extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private String valueType;

    private String displayName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotValue> pivotValues;

    public static Model.Finder<Long, PivotValueType> find = new Model.Finder<>(PivotValueType.class);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<PivotValue> getPivotValues() {
        return pivotValues;
    }

    public void setPivotValues(List<PivotValue> pivotValues) {
        this.pivotValues = pivotValues;
    }
}
