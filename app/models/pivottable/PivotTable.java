package models.pivottable;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.JsonIgnore;
import models.sources.CSVSource;
import models.sources.SQLSource;
import models.users.User;
import play.data.validation.Constraints;
import utils.forms.CSVTableForm;
import utils.forms.SQLTableForm;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The pivot table class
 * It is based on a composite pattern given that
 * this class is based on a number of different components that combined together
 * define the schema to use for rendering
 */

@Entity
public class PivotTable extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SharePermission> sharedList = new ArrayList<>();

    @Column(unique = true)
    @Constraints.Required
    private String name;

    @Constraints.Required
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sqlsource_id")
    @JsonIgnore
    private SQLSource sqlSource;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "csvsource_id")
    @JsonIgnore
    private CSVSource csvSource;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Field> fieldList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotRow> pivotRowList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotColumn> pivotColumnList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotPage> pivotPageList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PivotValue> valuesList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Filter> filtersList;

    public static Model.Finder<Long, PivotTable> find = new Model.Finder<>(PivotTable.class);

    /**
     * Constructor for a CSV based pivot table
     */
    public PivotTable(CSVTableForm tableForm, User owner){
        CSVSource source = CSVSource.find.byId(tableForm.getCsvSourceID());
        this.setCsvSource(source);
        this.setName(tableForm.getCsvTableName());
        this.setDescription(tableForm.getCsvTableDescription());
        this.owner = owner;

        this.fieldList = new ArrayList<>();

        if (source != null) {
            List<Map<String, String>> maps = source.getMapList();
                if (maps.size() > 0) {
                    maps.get(0).keySet().forEach(key -> {
                        Field field = new Field();
                        field.setFieldName(key);
                        field.setFieldType(FieldType.decide(maps.stream().map(l -> l.get(key)).collect(Collectors.toList())));
                        fieldList.add(field);
                    });
            }
        }

        this.save();
    }

    /**
     * Constructor for an SQL based pivot table
     */
    public PivotTable(SQLTableForm tableForm, User owner){
        SQLSource source = SQLSource.find.byId(tableForm.getSqlSourceID());
        this.setSqlSource(source);
        this.setName(tableForm.getSqlTableName());
        this.setDescription(tableForm.getSqlTableDescription());
        if(source != null) this.setFieldList(source.getFieldList());
        this.owner = owner;
        this.save();
    }

    public List<Map<String, String>> mapList(){
        if (csvSource != null) return csvSource.getMapList();
        return sqlSource.getMapList(getFieldList());
    }

    public void addPage(long fieldID){
        PivotPage page = new PivotPage();
        page.setField(Field.find.where().eq("id", fieldID).findUnique());
        pivotPageList.add(page);
        this.update();
    }

    public void deletePage(long pageID){
        this.pivotPageList = pivotPageList.stream()
                .filter(row -> !row.getId().equals(pageID)).collect(Collectors.toList());
        this.update();
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

    public void addFilter(long fieldID, List<FilterValidValue> list){
        Filter filter = new Filter();
        filter.setField(Field.find.where().eq("id", fieldID).findUnique());
        filter.setFilterValidValues(list);
        filtersList.add(filter);
        this.update();
    }

    public void deleteFilter(long filterID){
        this.filtersList = filtersList.stream()
                .filter(filter -> !filter.getId().equals(filterID)).collect(Collectors.toList());
        this.update();
    }

    public List<Field> availableFields(){
        return fieldList.stream().filter(field ->
                !pivotPageList.stream().map(PivotPage::getField).collect(Collectors.toList()).contains(field)
                && !pivotRowList.stream().map(PivotRow::getField).collect(Collectors.toList()).contains(field)
                && !pivotColumnList.stream().map(PivotColumn::getField).collect(Collectors.toList()).contains(field)
                && !valuesList.stream().map(PivotValue::getField).collect(Collectors.toList()).contains(field)
        ).collect(Collectors.toList());
    }

    public List<Field> availableFieldsValues(){
        return fieldList.stream().filter(field ->
                !pivotPageList.stream().map(PivotPage::getField).collect(Collectors.toList()).contains(field)
                        && !pivotRowList.stream().map(PivotRow::getField).collect(Collectors.toList()).contains(field)
                        && !pivotColumnList.stream().map(PivotColumn::getField).collect(Collectors.toList()).contains(field)
        ).collect(Collectors.toList());
    }

    public enum Dimension {
        ROW, COLUMN, PAGE
    }

    public List<Field> fieldsByDimension(String dimension){
        switch (dimension){
            case "column":
                return getPivotColumnList().stream().map(PivotColumn::getField).collect(Collectors.toList());
            case "row":
                return getPivotRowList().stream().map(PivotRow::getField).collect(Collectors.toList());
            case "page":
                return getPivotPageList().stream().map(PivotPage::getField).collect(Collectors.toList());
            default:
                return null;
        }
    }

    public Boolean view(User user){
        return user.equals(getOwner()) || getSharedList().stream().map(SharePermission::getUser).anyMatch(u -> u.equals(user));
    }

    public Boolean edit(User user){
        return user.equals(getOwner()) ||
                getSharedList().stream().filter(p -> p.getPermission().equals("View & Edit"))
                        .map(SharePermission::getUser).anyMatch(u -> u.equals(user));
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

    public List<PivotPage> getPivotPageList() {
        return pivotPageList;
    }

    public void setPivotPageList(List<PivotPage> pivotPageList) {
        this.pivotPageList = pivotPageList;
    }

    public SQLSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SQLSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    public CSVSource getCsvSource() {
        return csvSource;
    }

    public void setCsvSource(CSVSource csvSource) {
        this.csvSource = csvSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<SharePermission> getSharedList() {
        return sharedList;
    }

    public void setSharedList(List<SharePermission> sharedList) {
        this.sharedList = sharedList;
    }
}
