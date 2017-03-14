package controllers;

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
import java.util.List;
import java.util.stream.Collectors;
import models.pivottable.PivotTable;

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

            new PivotTable(table);
            flash("success", "New Source Added");
            return redirect(controllers.routes.PivotTableController.indexCSV());
        }
    }

    public Result getTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(),
                    pageForm,
                    rowForm,
                    columnForm,
                    valueForm,
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
            PivotTable table = PivotTable.find.byId(id);
            if (table != null) {
                table.addValue(valueForm.get().getFieldID(),
                        valueForm.get().getValueTypeID());
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
       return ok();
    }

    public Result deleteFilter(Long id, Long columnID){
        return ok();
    }

    public Result displayPivotTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(),
                    pageForm,
                    rowForm,
                    columnForm,
                    valueForm,
                    true));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }

}
