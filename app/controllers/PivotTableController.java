package controllers;

import models.pivottable.*;
import models.pivottable.PivotTable;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import utils.SidebarElement;
import utils.forms.CSVTableForm;
import utils.forms.FieldForm;
import utils.forms.SQLTableForm;
import utils.forms.ValueForm;
import utils.pivotTableHandler.PivotTableHandler;
import views.html.tables.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PivotTableController extends AuthController {

    private Result goTable(Long id) {
        return redirect(controllers.routes.PivotTableController.getTable(id));
    }

    private final FormFactory formFactory;

    @Inject
    public PivotTableController(FormFactory formFactory){
        this.formFactory = formFactory;
    }

    public Result index(){
        return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                formFactory.form(CSVTableForm.class), getSidebarElements(), false));
    }

    public Result indexCSV(){
        return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                formFactory.form(CSVTableForm.class), getSidebarElements(), true));
    }


    private List<SidebarElement> getSidebarElements() {
        return PivotTable.find.all()
                .stream().map(s -> SidebarElement.newInstance(
                        controllers.routes.PivotTableController.getTable(s.getId()).url(),
                        s.getName(),
                        s.getDescription()))
                .collect(Collectors.toList());
    }

    public Result addSQLTable(){
        return ok();
    }

    public Result addCSVTable(){
        Form<CSVTableForm> tableForm = formFactory.form(CSVTableForm.class).bindFromRequest();
        if (tableForm.hasErrors()) {
            flash("error", "Error: Could not add table. Please check the information you entered.");
            return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                    tableForm, getSidebarElements(), false));
        } else {
            CSVTableForm table = tableForm.get();
            if (PivotTable.find.where().eq("name", table.getCsvTableName()).findCount() > 0){
                flash("error", "Error: Please select a unique table name!");
                return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                        tableForm, getSidebarElements(), true));
            }

            PivotTable pt = new PivotTable(table);
            flash("success", "New Table Added");
            return redirect(controllers.routes.PivotTableController.getTable(pt.getId()));
        }
    }

    public Result getTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        DynamicForm filterForm = formFactory.form();

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(),
                    pageForm,
                    rowForm,
                    columnForm,
                    valueForm,
                    filterForm,
                    false));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }

    public Result deleteTable(Long id){
        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            table.delete();
            flash("success", "Source successfully deleted!");
        } else {
            flash("error", "Source Does Not Exist.");
        }
        return redirect(controllers.routes.PivotTableController.index());
    }

    public Result addPage(Long id){
        Form<FieldForm> fieldForm = formFactory.form(FieldForm.class).bindFromRequest();
        if (!fieldForm.hasErrors()) {
            PivotTable table = PivotTable.find.byId(id);
            if (table != null) {
                table.addPage(fieldForm.get().getFieldID());
            }
        }

        return goTable(id);
    }

    public Result deletePage(Long id, Long pageID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deletePage(pageID);
        }
        return goTable(id);
    }

    public Result addRow(Long id){
        Form<FieldForm> fieldForm = formFactory.form(FieldForm.class).bindFromRequest();
        if (!fieldForm.hasErrors()) {
            PivotTable table = PivotTable.find.byId(id);
            if (table != null) {
                table.addRow(fieldForm.get().getFieldID());
            }
        }

        return goTable(id);
    }

    public Result deleteRow(Long id, Long rowID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteRow(rowID);
        }
        return goTable(id);
    }

    public Result addColumn(Long id){
        Form<FieldForm> fieldForm = formFactory.form(FieldForm.class).bindFromRequest();
        if (!fieldForm.hasErrors()) {
            PivotTable table = PivotTable.find.byId(id);
            if (table != null) {
                table.addColumn(fieldForm.get().getFieldID());
            }
        }

        return goTable(id);
    }

    public Result deleteColumn(Long id, Long columnID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteColumn(columnID);
        }
        return goTable(id);
    }

    public Result addValue(Long id){
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class).bindFromRequest();
        if (!valueForm.hasErrors()) {
            Field field = Field.find.byId(valueForm.get().getFieldID());
            PivotValueType type = PivotValueType.find.byId(valueForm.get().getValueTypeID());
            if(field != null && FieldType.onlyCount(field.getFieldType())
                    && type != null && !type.getValueType().equals("count")){
                flash("error", "Chosen value type does not apply to the chosen field!");
            } else {
                PivotTable table = PivotTable.find.byId(id);
                if (table != null && field != null && type != null) {
                    table.addValue(field.getId(), type.getId());
                }
            }
        }

        return goTable(id);
    }

    public Result deleteValue(Long id, Long valueID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteValue(valueID);
        }
        return goTable(id);
    }

    public Result addFilter(Long id){
        Map<String, String> filterData = formFactory.form().bindFromRequest().data();
        filterData.remove("csrfToken");
        String fieldID = filterData.get("fieldID");
        filterData.remove("fieldID");

        if (fieldID != null && filterData.keySet().size() > 0) {
            PivotTable table = PivotTable.find.byId(id);
            List<FilterValidValue> list = new ArrayList<>();
            filterData.keySet().forEach(val -> {
                FilterValidValue validValue = new FilterValidValue();
                validValue.setSpecificValue(val);
                list.add(validValue);
            });
            if (table != null) table.addFilter(Long.parseLong(fieldID), list);
        } else {
            flash("error", "Could not apply the requested filter");
        }

        return goTable(id);
    }

    public Result deleteFilter(Long id, Long filterID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteFilter(filterID);
        }
        return goTable(id);
    }

    public Result displayPivotTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        DynamicForm filterForm = formFactory.form();

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(),
                    pageForm,
                    rowForm,
                    columnForm,
                    valueForm,
                    filterForm,
                    true));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }

    public Result contents(Long id){
        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            PivotTableHandler handler = new PivotTableHandler(table.mapList(), table);
            String contents = handler.tableHtml();
            if (contents.length() <= 1e6){
                return ok(contents);
            } else {
                return ok("<h4>Table too large. Cannot be displayed.</h4>");
            }
        }

        return ok("<h4>An error has occurred.</h4>");
    }

    public Result fieldOptions(Long fieldID){
        Field field = Field.find.byId(fieldID);
        StringBuilder sb = new StringBuilder();
        if(field != null) {
            for(String val : field.getPivotTable().mapList().stream().map(l -> l.get(field.getFieldName())).distinct()
                    .sorted(PivotTableHandler.comparator(field.getFieldType())).collect(Collectors.toList())){
                sb.append("<div class=\"checkbox\">")
                        .append("<label><input type=\"checkbox\" name=\"").append(val).append("\" value=\"").append(val).append("\">")
                        .append(val).append("</label>").append("</div>");
            }
        }

        return ok(sb.toString());

    }

}
