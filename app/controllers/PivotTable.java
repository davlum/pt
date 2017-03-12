package controllers;

import play.data.FormFactory;
import play.mvc.Result;
import utils.SidebarElement;
import utils.forms.CSVTableForm;
import utils.forms.SQLTableForm;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class PivotTable extends AuthController {

    private final FormFactory formFactory;

    @Inject
    public PivotTable(FormFactory formFactory) {
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
        return models.pivottable.PivotTable.find.all()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.PivotTable.getTable(s.getId()).url(),
                        s.getName(),
                        s.getDescription()))
                .collect(Collectors.toList());
    }

    public Result addSQLTable(){
        return ok();
    }

    public Result addCSVTable(){
        return ok();
    }

    public Result getTable(Long id){
        return ok();
    }

    public Result deleteTable(Long id){
        return ok();
    }
}
