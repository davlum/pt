package models.pivottable;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class PivotTable extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Field> fieldList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotRow> pivotRowList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotColumn> pivotColumnList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotValue> valuesList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Filter> filtersList;

    public static Model.Finder<Long, PivotTable> find = new Model.Finder<>(PivotTable.class);

    public static PivotTable pivotTable(){
        return find.byId(1L);
    }

    public void addRow(long fieldID){
        PivotRow row = new PivotRow();
        row.setField(Field.find.where().eq("id", fieldID).findUnique());
        pivotRowList.add(row);
        this.update();
    }

    public void deleteRow(long rowID){
        this.pivotRowList = pivotRowList.stream()
                .filter(row -> !row.getId().equals(rowID)).collect(Collectors.toList());
        this.update();
    }

    public void addColumn(long fieldID){
        PivotColumn col = new PivotColumn();
        col.setField(Field.find.where().eq("id", fieldID).findUnique());
        pivotColumnList.add(col);
        this.update();
    }

    public void deleteColumn(long colID){
        this.pivotColumnList = pivotColumnList.stream()
                .filter(row -> !row.getId().equals(colID)).collect(Collectors.toList());
        this.update();
    }

    public void addValue(long fieldID, long valueTypeID){
        PivotValue val = new PivotValue();
        val.setField(Field.find.where().eq("id", fieldID).findUnique());
        val.setPivotValueType(PivotValueType.find.byId(valueTypeID));
        valuesList.add(val);
        this.update();
    }

    public void deleteValue(long valueID){
        this.valuesList = valuesList.stream()
                .filter(row -> !row.getId().equals(valueID)).collect(Collectors.toList());
        this.update();
    }

    public List<Field> availableFields(){
        return fieldList.stream().filter(field ->
                !pivotRowList.stream().map(PivotRow::getField).collect(Collectors.toList()).contains(field)
                && !pivotColumnList.stream().map(PivotColumn::getField).collect(Collectors.toList()).contains(field)
        ).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PivotRow> getPivotRowList() {
        return pivotRowList;
    }

    public void setPivotRowList(List<PivotRow> pivotRowList) {
        this.pivotRowList = pivotRowList;
    }

    public List<PivotColumn> getPivotColumnList() {
        return pivotColumnList;
    }

    public void setPivotColumnList(List<PivotColumn> pivotColumnList) {
        this.pivotColumnList = pivotColumnList;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

    public List<PivotValue> getValuesList() {
        return valuesList;
    }

    public void setValuesList(List<PivotValue> valuesList) {
        this.valuesList = valuesList;
    }

    public List<Filter> getFiltersList() {
        return filtersList;
    }

    public void setFiltersList(List<Filter> filtersList) {
        this.filtersList = filtersList;
    }
}
