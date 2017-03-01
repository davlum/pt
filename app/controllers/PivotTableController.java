package controllers;

import models.pivottable.PivotTable;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import utils.forms.FieldForm;
import utils.forms.ValueForm;
import utils.pivotTableHandler.PivotTableHandler;
import views.html.*;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-52-90-93-63.compute-1.amazonaws.com/bixi_pivot",
                    "bixi_select", "select_bixi");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM trips LIMIT 10;");
            ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    map.put(key, resultSet.getString(key));
                }
                list.add(map);
            }
            resultSet.close();
            statement.close();
            connection.close();

            //list.forEach(System.out::println);
        } catch (SQLException e){
            e.printStackTrace();
        }

        PivotTableHandler handler = new PivotTableHandler(list, PivotTable.pivotTable());

        return ok(pivot.render(handler, PivotTable.pivotTable(), rowForm, columnForm, valueForm));
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
