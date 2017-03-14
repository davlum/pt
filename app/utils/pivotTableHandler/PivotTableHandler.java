package utils.pivotTableHandler;

import models.pivottable.*;
import org.joda.time.format.DateTimeFormat;

import java.util.*;
import java.util.stream.Collectors;

public class PivotTableHandler {

    private List<Map<String, String>> pivotTableData;
    private List<Map<String, String>> pageData;
    private PivotTable pivotTable;

    public PivotTableHandler(List<Map<String, String>> pivotTableData, PivotTable pivotTable){
        this.pivotTableData = pivotTableData;
        this.pivotTable = pivotTable;

        this.pivotTable.getFiltersList().forEach(filter -> {
            List<String> validValues = filter.getFilterValidValues().stream()
                    .map(FilterValidValue::getSpecificValue).collect(Collectors.toList());
            this.pivotTableData = this.pivotTableData.stream().filter(l -> validValues.contains(l.get(filter.getField().getFieldName())))
                    .collect(Collectors.toList());
        });
    }

    public List<String> pages(){
        if(pivotTable.getPivotPageList().size() > 0){
           return pivotTableData.stream().map(l -> l.get(pivotTable.getPivotPageList().get(0).getField().getFieldName()))
                    .distinct().collect(Collectors.toList());
        }
        return Collections.singletonList("all");
    }

    private Map<Integer, Long> cellspanPerLevel(String dimension){
        Map<Integer, Long> nbColumnsPerLevel = new HashMap<>();
        long lastTotal = 1;
        for(int i = pivotTable.fieldsByDimension(dimension).size() - 1; i >= 0; i--){
            String currentField = pivotTable.fieldsByDimension(dimension).get(i).getFieldName();
            long currentCount = pageData.stream().map(line -> line.get(currentField)).distinct().count();
            nbColumnsPerLevel.put(i, lastTotal);
            lastTotal = currentCount * lastTotal;
        }

        return nbColumnsPerLevel;
    }

    public static Comparator<String> comparator(FieldType fieldType){
        switch (fieldType){
            case Long:
                return Comparator.comparingLong(Long::parseLong);
            case Double:
                return Comparator.comparingDouble(Double::parseDouble);
            case DateTime:
                org.joda.time.format.DateTimeFormatter df1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                return Comparator.comparing(df1::parseDateTime);
            case Date:
                org.joda.time.format.DateTimeFormatter df2 = DateTimeFormat.forPattern("yyyy-MM-dd");
                return Comparator.comparing(df2::parseDateTime);
            case Time:
                org.joda.time.format.DateTimeFormatter df3 = DateTimeFormat.forPattern("HH:mm:ss");
                return Comparator.comparing(df3::parseDateTime);
            default:
                return Comparator.naturalOrder();
        }

    }

    private int i = 0;
    private int rand = 0;
    public String tableHtml(){
        StringBuilder pageHtml = new StringBuilder();
        rand = 0;
        pageHtml.append("<div class=\"tab-content\">");
        if(pivotTable.getPivotPageList().size() > 0){
            pages().forEach(page -> {
                pageHtml.append("<div id=\"").append(page).append("\" class=\"tab-pane fade")
                        .append(rand == 0 ? " in active" : "").append("\">");
                pageData = pivotTableData.stream().filter(l ->
                        l.get(pivotTable.getPivotPageList().get(0).getField().getFieldName())
                                .equals(page)).collect(Collectors.toList());
                pageHtml.append(processPage());
                pageHtml.append("</div>");
                rand = 1;
            });
        } else {
            pageHtml.append("<div id=\"all\" class=\"tab-pane fade in active\">");
            pageData = pivotTableData;
            pageHtml.append(processPage());
            pageHtml.append("</div>");
        }
        pageHtml.append("</div>");

        return pageHtml.toString();
    }

    private String processPage(){
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
                                Math.max(pivotTable.getValuesList().size(), 1)).append("\">").append(value).append("</th>"));
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
            tableHtml.append("<tr><th></th>");
            tableHtml.append(valueTitle(1L));
            tableHtml.append("</tr>");
        }


        tableHtml.append("</thead><tbody>");
        Map<Integer, Long> rowspanPerLevel = cellspanPerLevel("row");
        tableHtml.append(loopRows(pageData, 0, rowspanPerLevel));
        int i = 0;
        tableHtml.append("<tr>");
        while(i < pivotTable.getPivotRowList().size() - 1){
            tableHtml.append("<th></th>");
            i++;
        }
        tableHtml.append("<th>Total</th>");
        tableHtml.append(loopCols(pageData, 0));
        tableHtml.append(printValues(pageData));
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
            for (String value : pageData.stream().map(line -> line.get(currentField)).distinct()
                    .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotRowList().get(level).getField().getFieldType())))
                    .collect(Collectors.toList())) {
                workData = new ArrayList<>(restrictedData);
                workData = workData.stream().filter(l ->
                        value != null ? (l.get(currentField) != null && l.get(currentField).equals(value)) : l.get(currentField) == null)
                        .collect(Collectors.toList());
                if (level == 0) returnVal.append("<tr>");
                returnVal.append("<th rowspan=\"").append(rowspanPerLevel.get(level)).append("\">").append(value).append("</th>");
                if (level == pivotTable.getPivotRowList().size() - 1) {
                    returnVal.append(loopCols(workData, 0));
                    returnVal.append(printValues(workData)).append("</tr>");
                } else {
                    returnVal.append(loopRows(workData, level + 1, rowspanPerLevel));
                }
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

    private String valueAsRequested(List<Map<String, String>> restrictedData, PivotValue pivotValue){
        switch (pivotValue.getPivotValueType().getValueType()){
            case "count":
                return Long.toString(restrictedData.size());
            case "sum":
                return Long.toString(restrictedData.stream().mapToLong(l ->
                        Long.parseLong(l.get(pivotValue.getField().getFieldName()))).sum());
        }
        return null;
    }

    public List<Map<String, String>> getPivotTableData() {
        return pivotTableData;
    }

    public void setPivotTableData(List<Map<String, String>> pivotTableData) {
        this.pivotTableData = pivotTableData;
    }

    public PivotTable getPivotTable() {
        return pivotTable;
    }

    public void setPivotTable(PivotTable pivotTable) {
        this.pivotTable = pivotTable;
    }
}
