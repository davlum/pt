package controllers;

import models.pivottable.PivotTable;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import utils.FieldForm;
import utils.ValueForm;
import views.html.*;

import javax.inject.Inject;

public class PivotTableController extends Controller {

    private Result GO_TABLE = redirect(controllers.routes.PivotTableController.index());

    private final FormFactory formFactory;

    @Inject
    public PivotTableController(FormFactory formFactory){
        this.formFactory = formFactory;
    }

    public Result index() {
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        return ok(pivot.render(PivotTable.pivotTable(), rowForm, columnForm, valueForm));
    }

    public Result addRow(){
        Form<FieldForm> fieldForm = formFactory.form(FieldForm.class).bindFromRequest();
        if (!fieldForm.hasErrors()) {
            PivotTable.pivotTable().addRow(fieldForm.get().getFieldID());
        }

        return GO_TABLE;
    }

    public Result deleteRow(Long rowID){
        PivotTable.pivotTable().deleteRow(rowID);
        return GO_TABLE;
    }

    public Result addColumn(){
        Form<FieldForm> fieldForm = formFactory.form(FieldForm.class).bindFromRequest();
        if (!fieldForm.hasErrors()) {
            PivotTable.pivotTable().addColumn(fieldForm.get().getFieldID());
        }

        return GO_TABLE;
    }

    public Result deleteColumn(Long columnID){
        PivotTable.pivotTable().deleteColumn(columnID);
        return GO_TABLE;
    }

    public Result addValue(){
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class).bindFromRequest();
        if (!valueForm.hasErrors()) {
            PivotTable.pivotTable().addValue(valueForm.get().getFieldID(),
                    valueForm.get().getValueTypeID());
        }

        return GO_TABLE;
    }

    public Result deleteValue(Long columnID){
        PivotTable.pivotTable().deleteValue(columnID);
        return GO_TABLE;
    }

}
