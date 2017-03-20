package controllers;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
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

/**
 * Class for the pivot table
 */
public class PivotTable extends AuthController {

    private final FormFactory formFactory;

    /**
     * Constructor for the class
     * @param formFactory
     */
    @Inject
    public PivotTable(FormFactory formFactory) {
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

        PivotTableHandler handler = new PivotTableHandler(list, models.pivottable.PivotTable.pivotTable());

        return ok(pivot.render(handler, models.pivottable.PivotTable.pivotTable(), rowForm, columnForm, valueForm));
    }
}
