package utils.pivotTableHandler;

import models.pivottable.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to manipulate the pivot table.
 */
public class PivotTableHandler extends RenderStrategy {

    public PivotTableHandler(List<Map<String, String>> pivotTableData, PivotTable pivotTable){
        super(pivotTableData, pivotTable);
    }

    /* *
    * Html That will be used to display the generated pivot table
    * */
    public String tableHtml(String pageName){
        StringBuilder pageHtml = new StringBuilder();
        rand = 0;
        pageHtml.append("<div class=\"tab-content\">");
        if(pivotTable.getPivotPageList().size() > 0){
            pages().stream().filter(page -> page.equals(pageName)).forEach(page -> {
                pageHtml.append("<div id=\"").append(page).append("\" class=\"tab-pane fade")
                        .append(rand == 0 ? " in active" : "").append("\">");
                pageData = pivotTableData;
                /*pageData = pivotTableData.stream().filter(l ->
                        l.get(pivotTable.getPivotPageList().get(0).getField().getFieldName())
                                .equals(page)).collect(Collectors.toList());*/
                pageHtml.append(processPage(page));
                pageHtml.append("</div>");
                rand = 1;
            });
        } else {
            pageHtml.append("<div id=\"all\" class=\"tab-pane fade in active\">");
            pageData = pivotTableData;
            pageHtml.append(processPage(null));
            pageHtml.append("</div>");
        }
        pageHtml.append("</div>");

        return pageHtml.toString();
    }

    /**
     * Process the page and returns a string of HTML as a result
     * @return string of HTML
     */

    private String processPage(String page){
        //System.out.println("Start: " + new Date());
        StringBuilder tableHtml = new StringBuilder();
        tableHtml.append("<table id=\"\" class=\"table\"><thead>");
        Map<Integer, Long> colspanPerLevel = cellspanPerLevel("column");
        if (pivotTable.getPivotColumnList().size() > 0) {
            i = 0;
            long repeat = 1;
            while (i < pivotTable.getPivotColumnList().size()) {
                tableHtml.append("<tr>");
                String currentField = pivotTable.getPivotColumnList().get(i).getField().getFieldName();
                if(pivotTable.getPivotRowList().size() > 0) {
                    pivotTable.getPivotRowList().forEach(row -> tableHtml.append("<th></th>"));
                } else tableHtml.append("<th></th>");
                StringBuilder repeatPattern = new StringBuilder();
                pageData.stream().map(line -> line.get(currentField)).distinct()
                        .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotColumnList().get(i).getField().getFieldType())))
                        .forEach(value -> repeatPattern.append("<th colspan=\"").append(colspanPerLevel.get(i) *
                                Math.max(pivotTable.getValuesList().size(), 1)).append("\">")
                                .append( (value != null && !value.equals("") )? value : "(BLANK)").append("</th>"));
                for (int r = 0; r < repeat; r++) {
                    tableHtml.append(repeatPattern);
                }
                if (pivotTable.getValuesList().size() > 0) {
                    for (int r = 0; r < pivotTable.getValuesList().size(); r++) {
                        tableHtml.append("<th></th>");
                    }
                } else {
                    tableHtml.append("<th></th>");
                }
                tableHtml.append("</tr>");
                repeat = repeat * pageData.stream().map(line -> line.get(currentField)).distinct().count();
                i++;
            }
            tableHtml.append(valueTitle(repeat));

        } else {
            tableHtml.append(valueTitle(1L));
        }


        tableHtml.append("</thead><tbody>");
        Map<Integer, Long> rowspanPerLevel = cellspanPerLevel("row");
        List<Map<String, String>> restrictedData;
        if (page != null) {
            restrictedData = pivotTableData.stream().filter(l ->
                    l.get(pivotTable.getPivotPageList().get(0).getField().getFieldName())
                            .equals(page)).collect(Collectors.toList());
        } else {
            restrictedData = pivotTableData;
        }
        tableHtml.append(loopRows(restrictedData, 0, rowspanPerLevel));
        int i = 0;
        tableHtml.append("<tr>");
        while(i < pivotTable.getPivotRowList().size() - 1){
            tableHtml.append("<th></th>");
            i++;
        }
        tableHtml.append("<th>Total</th>");
        tableHtml.append(loopCols(restrictedData, 0));
        tableHtml.append(printValues(restrictedData));
        tableHtml.append("</tr>");
        tableHtml.append("</tbody></table>");

        //System.out.println("End: " + tableHtml.length() + " " + new Date());

        return tableHtml.toString();
    }

    private String valueTitle(Long repeat){
        StringBuilder returnVal = new StringBuilder();
        StringBuilder repeatPattern = new StringBuilder();
        returnVal.append("<tr>");
        if(pivotTable.getPivotRowList().size() > 0) {
            for (PivotRow ignored : pivotTable.getPivotRowList()) {
                returnVal.append("<th></th>");
            }
        } else returnVal.append("<th></th>");
        if (pivotTable.getValuesList().size() > 0){
            pivotTable.getValuesList().forEach(val -> repeatPattern.append("<th>").append(val.displayTitle()).append("</th>"));
        } else {
            repeatPattern.append("<th>Count</th>");
        }
        for (int r = 0; r < repeat; r++) {
            returnVal.append(repeatPattern);
        }
        if (pivotTable.getValuesList().size() > 0){
            for (PivotValue val : pivotTable.getValuesList()) returnVal.append("<th>").append(val.displayTitle()).append("\nTotal</th>");
        } else {
            returnVal.append("<th>Total</th>");
        }

        returnVal.append("</tr>");

        return returnVal.toString();
    }

    private String loopRows(List<Map<String, String>> restrictedData, int level, Map<Integer, Long> rowspanPerLevel) {
        StringBuilder returnVal = new StringBuilder();
        List<Map<String, String>> workData;
        if(pivotTable.getPivotRowList().size() > 0) {
            String currentField = pivotTable.getPivotRowList().get(level).getField().getFieldName();

            boolean flag = false;
            for (String value : pageData.stream().map(line -> line.get(currentField)).distinct()
                    .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotRowList().get(level).getField().getFieldType())))
                    .collect(Collectors.toList())) {
                workData = new ArrayList<>(restrictedData);
                workData = workData.stream().filter(l ->
                        value != null ? (l.get(currentField) != null && l.get(currentField).equals(value)) : l.get(currentField) == null)
                        .collect(Collectors.toList());
                if (level == 0 || flag) returnVal.append("<tr>");
                returnVal.append("<th rowspan=\"").append(rowspanPerLevel.get(level)).append("\">")
                        .append( (value != null && !value.equals("") )? value : "(BLANK)").append("</th>");
                if (level == pivotTable.getPivotRowList().size() - 1) {
                    returnVal.append(loopCols(workData, 0));
                    returnVal.append(printValues(workData)).append("</tr>");
                } else {
                    returnVal.append(loopRows(workData, level + 1, rowspanPerLevel));
                }

                flag = true;
            }
        } else {
            returnVal.append("<tr><td></td>").append(loopCols(restrictedData, 0)).append(printValues(restrictedData)).append("</tr>");
        }

        return returnVal.toString();
    }

    private String loopCols(List<Map<String, String>> restrictedData, int level) {
        StringBuilder returnVal = new StringBuilder();
        if(pivotTable.getPivotColumnList().size() > 0) {
            String currentField = pivotTable.getPivotColumnList().get(level).getField().getFieldName();
            for (String value : pageData.stream().map(line -> line.get(currentField)).distinct()
                    .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotColumnList().get(level).getField().getFieldType())))
                    .collect(Collectors.toList())) {
                List<Map<String, String>> workData = new ArrayList<>(restrictedData);
                workData = workData.stream().filter(l ->
                        value != null ? l.get(currentField).equals(value) : l.get(currentField) == null).collect(Collectors.toList());
                if (level == pivotTable.getPivotColumnList().size() - 1) {
                    returnVal.append(printValues(workData));
                } else {
                    returnVal.append(loopCols(workData, level + 1));
                }
            }
        } else {
            returnVal.append(printValues(restrictedData));
        }

        return returnVal.toString();
    }

    private String printValues(List<Map<String, String>> restrictedData){
        StringBuilder returnVal = new StringBuilder();
        for (PivotValue pivotValue : pivotTable.getValuesList()) {
            returnVal.append("<td>").append(valueAsRequested(restrictedData, pivotValue)).append("</td>");
        }

        if (pivotTable.getValuesList().size() == 0) returnVal.append("<td>").append(Long.toString(restrictedData.size())).append("</td>");

        return returnVal.toString();
    }
}
