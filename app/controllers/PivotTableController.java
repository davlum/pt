package controllers;

import akka.actor.ActorSystem;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import models.pivottable.*;
import models.pivottable.PivotTable;
import models.users.User;
import play.Application;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.mailer.MailerClient;
import play.mvc.*;

import tools.Mail;

import play.mvc.BodyParser;
import com.fasterxml.jackson.databind.JsonNode;

import utils.SidebarElement;
import utils.forms.*;
import utils.pivotTableHandler.ExcelHandler;
import utils.pivotTableHandler.PivotTableHandler;
import views.html.tables.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Iterator;

/**
 * Controller for the pivot table generation and handling
 */
public class PivotTableController extends AuthController {

    /**
     * Go to the table specified by Id
     * @param id
     * @return HTTP redirect to the table
     */
    private Result goTable(Long id) {
        return redirect(controllers.routes.PivotTableController.getTable(id));
    }

    private final FormFactory formFactory;
    private final MailerClient mailerClient;
    private final ActorSystem actorSystem;
    private final Provider<Application> application;

    /**
     * Constructor for the class
     * @param formFactory
     */
    @Inject
    public PivotTableController(FormFactory formFactory, MailerClient mailerClient, ActorSystem actorSystem,
                                Provider<Application> application){
        this.formFactory = formFactory;
        this.mailerClient = mailerClient;
        this.actorSystem = actorSystem;
        this.application = application;
    }

    /**
     * Renders page
     * @return HTTP status 400
     */
    public Result index(){
        return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                formFactory.form(CSVTableForm.class), getSidebarElements(), false));
    }

    public Result indexCSV(){
        return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                formFactory.form(CSVTableForm.class), getSidebarElements(), true));
    }

    /**
     * Gets a list of the pivot tables to be displayed on the sidebar
     * @return list of pivot tables characteristics
     */
    private List<SidebarElement> getSidebarElements() {
        List<SidebarElement> myTables = getCurrentUser().getPivotTables()
                .stream().map(s -> new SidebarElement(
                        controllers.routes.PivotTableController.getTable(s.getId()).url(),
                        s.getName(),
                        s.getDescription()))
                .collect(Collectors.toList());

        myTables.addAll(getCurrentUser().getSharePermissions().stream().map(SharePermission::getPivotTable)
                .map(s -> new SidebarElement(
                        controllers.routes.PivotTableController.getTable(s.getId()).url(),
                        s.getName(),
                        s.getDescription()))
                .collect(Collectors.toList()));

        return myTables;
    }

    /**
     * Adds a pivot table from a database connection
     * @return HTTP redirect or status
     */
    public Result addSQLTable(){
        Form<SQLTableForm> tableForm = formFactory.form(SQLTableForm.class).bindFromRequest();
        if (tableForm.hasErrors()) {
            flash("error", "Error: Could not add table. Please check the information you entered.");
            return ok(views.html.tables.index.render(getCurrentUser(),
                    tableForm, formFactory.form(CSVTableForm.class), getSidebarElements(), false));
        } else {
            SQLTableForm table = tableForm.get();
            if (PivotTable.find.where().eq("name", table.getSqlTableName()).findCount() > 0){
                flash("error", "Error: Please select a unique table name!");
                return ok(views.html.tables.index.render(getCurrentUser(),
                        tableForm, formFactory.form(CSVTableForm.class), getSidebarElements(), false));
            }

            PivotTable pt = new PivotTable(table, getCurrentUser());
            flash("success", "New Table Added");
            return redirect(controllers.routes.PivotTableController.getTable(pt.getId()));
        }
    }

    /**
     * Adds a pivot table from a CSV connection
     * @return HTTP redirect or status
     */
    public Result addCSVTable(){
        Form<CSVTableForm> tableForm = formFactory.form(CSVTableForm.class).bindFromRequest();
        if (tableForm.hasErrors()) {
            flash("error", "Error: Could not add table. Please check the information you entered.");
            return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                    tableForm, getSidebarElements(), true));
        } else {
            CSVTableForm table = tableForm.get();
            if (PivotTable.find.where().eq("name", table.getCsvTableName()).findCount() > 0){
                flash("error", "Error: Please select a unique table name!");
                return ok(views.html.tables.index.render(getCurrentUser(), formFactory.form(SQLTableForm.class),
                        tableForm, getSidebarElements(), true));
            }

            PivotTable pt = new PivotTable(table, getCurrentUser());
            flash("success", "New Table Added");
            return redirect(controllers.routes.PivotTableController.getTable(pt.getId()));
        }
    }

    /**
     * Get pivot table with specified id
     * @param id of a pivot table
     * @return HTTP redirect
     */
    public Result getTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        Form<PermissionForm> permissionForm = formFactory.form(PermissionForm.class);
        DynamicForm filterForm = formFactory.form();

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(), pageForm, rowForm, columnForm,
                    valueForm, filterForm, permissionForm, "main",
                    table.view(getCurrentUser()), table.edit(getCurrentUser())));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }

    /**
     * delete a table
     * @param id of a pivot table
     * @return HTTP redirect
     */
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

    /**
     * Method to add a page to the pivot table
     * @param id of the table
     * @return a HTTP redirect to the new table
     */
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


    private static void updateUtil(String paramName, JsonNode json, PivotTable pt) {
        Iterator<JsonNode> param = json.findValue(paramName).elements();
        Iterator<JsonNode> delParam = param.next().elements();
        Iterator<JsonNode> addParam = param.next().elements();
        Iterator<JsonNode> paramTypes = param.next().elements();
        while (delParam.hasNext()) {
            Long paramId = delParam.next().asLong(-1);
            switch (paramName) {
                case "rows":
                    pt.deleteRow(paramId);
                    break;
                case "columns":
                    pt.deleteColumn(paramId);
                    break;
                case "page":
                    pt.deletePage(paramId);
                    break;
                case "values":
                    pt.deleteValue(paramId);
                default:
                    break;
            }
        }
        while (addParam.hasNext()) {
            Long paramId = addParam.next().asLong(-1);
            switch (paramName) {
                case "rows":
                    pt.addRow(paramId);
                    break;
                case "columns":
                    pt.addColumn(paramId);
                    break;
                case "page":
                    pt.addPage(paramId);
                    break;
                case "values":
                    Long paramType = paramTypes.next().asLong(-1);
                    Field field = Field.find.byId(paramId);
                    PivotValueType type = PivotValueType.find.byId(paramType);
                    if(field != null && FieldType.onlyCount(field.getFieldType())
                            && type != null && !type.getValueType().equals("count")) {
                        paramType = PivotValueType.findCount();
                    }
                    pt.addValue(paramId, paramType);
                default:
                    break;
            }
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result updateTable(Long id) {
        JsonNode json = request().body().asJson();
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            updateUtil("columns", json, table);
            updateUtil("rows", json, table);
            updateUtil("page", json, table);
            updateUtil("values", json, table);
        }
        return goTable(id);
    }

    /**
     * Method to delete a page of the Table
     * @param id of the table
     * @param pageID of the page to be deleted
     * @return HTTP redirect to the new table
     */
    public Result deletePage(Long id, Long pageID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deletePage(pageID);
        }
        return goTable(id);
    }

    /**
     * Method to add a row to the table
     * @param id id of the table
     * @return HTTP redirect to the new table
     */
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

    /**
     * Method to delete a row of the table
     * @param id of the table
     * @param rowID id of the row
     * @return HTTP redirect to the new table
     */
    public Result deleteRow(Long id, Long rowID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteRow(rowID);
        }
        return goTable(id);
    }

    /**
     * Method to add a column to the table
     * @param id of the table
     * @return HTTP redirect to the new table
     */
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

    /**
     * Method to delete a column from the table
     * @param id of the table
     * @param columnID id of the column
     * @return HTTP redirect to the new table
     */
    public Result deleteColumn(Long id, Long columnID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteColumn(columnID);
        }
        return goTable(id);
    }

    /**
     * Method to add a value to the table
     * @param id of the table
     * @return HTTP redirect to the new table
     */
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

    /**
     * Method to delete a value from the table
     * @param id of the table
     * @param valueID of the value
     * @return HTTP redirect to the new table
     */
    public Result deleteValue(Long id, Long valueID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteValue(valueID);
        }
        return goTable(id);
    }

    /**
     * Filters the data of the pivot table
     * @param id of the table
     * @return HTTP redirect to new table
     */
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

    /**
     * Deletes the filter
     * @param id of the table
     * @param filterID id of the filter
     * @return HTTP redirect to the new table
     */
    public Result deleteFilter(Long id, Long filterID){
        PivotTable table = PivotTable.find.byId(id);
        if (table != null) {
            table.deleteFilter(filterID);
        }
        return goTable(id);
    }

    /**
     * Method to display the table
     * @param id of the table
     * @return HTTP redirect to the page of the table
     */
    public Result displayPivotTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        Form<PermissionForm> permissionForm = formFactory.form(PermissionForm.class);
        DynamicForm filterForm = formFactory.form();

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(), pageForm, rowForm, columnForm, valueForm,
                    filterForm, permissionForm, "display",
                    table.view(getCurrentUser()), table.edit(getCurrentUser())));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }


    public Result infoPivotTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        Form<PermissionForm> permissionForm = formFactory.form(PermissionForm.class);
        DynamicForm filterForm = formFactory.form();

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(), pageForm, rowForm, columnForm, valueForm,
                    filterForm, permissionForm, "info",
                    table.view(getCurrentUser()), table.edit(getCurrentUser())));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }

    public Result sharePivotTable(Long id){
        Form<FieldForm> pageForm = formFactory.form(FieldForm.class);
        Form<FieldForm> rowForm = formFactory.form(FieldForm.class);
        Form<FieldForm> columnForm = formFactory.form(FieldForm.class);
        Form<ValueForm> valueForm = formFactory.form(ValueForm.class);
        Form<PermissionForm> permissionForm = formFactory.form(PermissionForm.class);
        DynamicForm filterForm = formFactory.form();

        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            return ok(tableDetail.render(getCurrentUser(),
                    new PivotTableHandler(table.mapList(), table),
                    getSidebarElements(), pageForm, rowForm, columnForm, valueForm,
                    filterForm, permissionForm, "share",
                    table.view(getCurrentUser()), table.edit(getCurrentUser())));
        } else {
            flash("error", "Table Does Not Exist");
            return redirect(controllers.routes.PivotTableController.index());
        }
    }

    public Result addSharePermission(Long id){
        Form<PermissionForm> permissionForm = formFactory.form(PermissionForm.class).bindFromRequest();
        if (!permissionForm.hasErrors()) {
            User user = User.find.byId(permissionForm.get().getUserID());
            String perm = permissionForm.get().getPermission();
            SharePermission permission = new SharePermission(user, perm);
            PivotTable table = PivotTable.find.byId(id);
            if (table != null) {
                table.getSharedList().add(permission);
                table.update();

                String subject = getCurrentUser().getFullName() + " has shared with you a new pivot table!";
                String message = getCurrentUser().getFullName() + " has shared with you the pivot table " +
                        table.getName() + "! Login to your portal to view it!";
                if(user != null) {
                    Mail.Envelop envelop = new Mail.Envelop(subject, message, user.getEmail());
                    Mail.sendMail(envelop, mailerClient, actorSystem);
                }
            }
        }

        return redirect(controllers.routes.PivotTableController.sharePivotTable(id));
    }

    public Result deleteSharePermission(Long id, Long permissionID){
        SharePermission permission = SharePermission.find.byId(permissionID);

        PivotTable table = PivotTable.find.byId(id);
        if (table != null && permission != null) {
            table.setSharedList(table.getSharedList().stream().filter(p ->
                    !p.getId().equals(permissionID)).collect(Collectors.toList()));
            table.update();
        }

        return redirect(controllers.routes.PivotTableController.sharePivotTable(id));
    }

    /**
     * Checks to make sure contents is of certain size
     * @param id of the table
     * @return Return HTTP status
     */
    public Result contents(Long id, String page){
        PivotTable table = PivotTable.find.byId(id);
        if(table != null) {
            PivotTableHandler handler = new PivotTableHandler(table.mapList(), table);
            String contents = handler.tableHtml(page);
            if (contents.length() <= 1e6){
                return ok(contents);
            } else {
                return ok("<h4>Table too large. Cannot be displayed.</h4>");
            }
        }

        return ok("<h4>An error has occurred.</h4>");
    }

    /**
     * Get the options for the different fields
     * @param fieldID
     * @return HTTP status
     */
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

    public Result excelExport(Long tableID){
        PivotTable table = PivotTable.find.byId(tableID);
        if(table != null) {
            ExcelHandler handler = new ExcelHandler(table.mapList(), table);
            response().setHeader("Content-disposition", "attachment; filename=pivot-table.xlsx");
            return ok(handler.tableExcel()).as("application/x-download");
        }
        return ok();
    }

    public Result pdfExport(Long tableID){
        PivotTable table = PivotTable.find.byId(tableID);
        if(table != null) {
            PivotTableHandler handler = new PivotTableHandler(table.mapList(), table);

            StringBuilder sb = new StringBuilder();
            handler.pages().forEach(page -> {
                sb.append("<h1>").append(page).append("</h1>");
                sb.append(handler.tableHtml(page));
            });

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, baos);
                document.open();
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes());

                XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
                document.close();
                byte[] pdf = baos.toByteArray();

                return ok(pdf).as("application/pdf");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return getTable(tableID);
    }
}
