package utils.pivotTableHandler;

import models.pivottable.PivotRow;
import models.pivottable.PivotTable;
import models.pivottable.PivotValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelHandler extends RenderStrategy {


    private List<Map<String, String>> pivotTableData;
    private List<Map<String, String>> pageData;
    private PivotTable pivotTable;

    public ExcelHandler(List<Map<String, String>> pivotTableData, PivotTable pivotTable){
        super(pivotTableData, pivotTable);
        this.pivotTableData = pivotTableData;
        this.pivotTable = pivotTable;
    }

    private XSSFWorkbook wb;
    private Sheet sheet;
    private int rNum = 0;
    private int cNum = 0;
    private Row eRow;
    private Cell cell;

    public byte[] tableExcel(){
        try {
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream(8192);
            wb = new XSSFWorkbook();
            rand = 0;

            if(pivotTable.getPivotPageList().size() > 0){
                pages().forEach(page -> {
                    sheet = wb.createSheet(page);
                    rNum = 0;
                    cNum = 0;
                    pageData = pivotTableData;
                    processPage(page);
                    rand = 1;
                });
            } else {
                pageData = pivotTableData;
                sheet = wb.createSheet("All");
                rNum = 0;
                cNum = 0;
                processPage(null);
            }

            wb.write(fileOut);
            fileOut.close();

            return fileOut.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processPage(String page){
        Map<Integer, Long> colspanPerLevel = cellspanPerLevel("column");
        if (pivotTable.getPivotColumnList().size() > 0) {
            i = 0;
            long repeat = 1;
            while (i < pivotTable.getPivotColumnList().size()) {
                eRow = sheet.createRow(rNum++);
                cNum = 0;
                String currentField = pivotTable.getPivotColumnList().get(i).getField().getFieldName();
                if(pivotTable.getPivotRowList().size() > 0) {
                    pivotTable.getPivotRowList().forEach(row -> cell = eRow.createCell(cNum++));
                } else eRow.createCell(cNum++);
                for (int r = 0; r < repeat; r++) {
                    pageData.stream().map(line -> line.get(currentField)).distinct()
                            .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotColumnList().get(i).getField().getFieldType())))
                            .forEach(value -> {
                                cell = eRow.createCell(cNum++);
                                cell.setCellValue( (value != null && !value.equals("") )? value : "(BLANK)");
                                if(colspanPerLevel.get(i) * Math.max(pivotTable.getValuesList().size(), 1) > 1) {
                                    for (int n = 0; n < colspanPerLevel.get(i) * Math.max(pivotTable.getValuesList().size(), 1) - 1; n++) {
                                        cell = eRow.createCell(cNum++);
                                    }
                                }
                            });
                }
                repeat = repeat * pageData.stream().map(line -> line.get(currentField)).distinct().count();
                i++;
            }
            valueTitle(repeat);

        } else {
            valueTitle(1L);
        }

        List<Map<String, String>> restrictedData;
        if (page != null) {
            restrictedData = pivotTableData.stream().filter(l ->
                    l.get(pivotTable.getPivotPageList().get(0).getField().getFieldName())
                            .equals(page)).collect(Collectors.toList());
        } else {
            restrictedData = pivotTableData;
        }

        loopRows(restrictedData, 0);
        int i = 0;
        eRow = sheet.createRow(rNum++);
        cNum = 0;
        while(i < pivotTable.getPivotRowList().size() - 1){
            cell = eRow.createCell(cNum++);
            i++;
        }
        cell = eRow.createCell(cNum++);
        cell.setCellValue("Total");
        loopCols(restrictedData, 0);
        printValues(restrictedData);

    }

    private void valueTitle(Long repeat){
        eRow = sheet.createRow(rNum++);
        cNum = 0;
        if(pivotTable.getPivotRowList().size() > 0) {
            for (PivotRow ignored : pivotTable.getPivotRowList()) {
                cell = eRow.createCell(cNum++);
            }
        } else cell = eRow.createCell(cNum++);

        for (int r = 0; r < repeat; r++) {
            if (pivotTable.getValuesList().size() > 0){
                pivotTable.getValuesList().forEach(val -> {
                    cell = eRow.createCell(cNum++);
                    cell.setCellValue(val.displayTitle());
                });
            } else {
                cell = eRow.createCell(cNum++);
                cell.setCellValue("Count");
            }
        }
        if (pivotTable.getValuesList().size() > 0){
            for (PivotValue val : pivotTable.getValuesList()){
                cell = eRow.createCell(cNum++);
                cell.setCellValue(val.displayTitle() + " - Total");
            }
        } else {
            cell = eRow.createCell(cNum++);
            cell.setCellValue("Total");
        }

    }

    private void loopRows(List<Map<String, String>> restrictedData, int level) {
        List<Map<String, String>> workData;
        if(pivotTable.getPivotRowList().size() > 0) {
            String currentField = pivotTable.getPivotRowList().get(level).getField().getFieldName();
            boolean newLine = false;
            for (String value : pageData.stream().map(line -> line.get(currentField)).distinct()
                    .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotRowList().get(level).getField().getFieldType())))
                    .collect(Collectors.toList())) {
                workData = new ArrayList<>(restrictedData);
                workData = workData.stream().filter(l ->
                        value != null ? (l.get(currentField) != null && l.get(currentField).equals(value)) : l.get(currentField) == null)
                        .collect(Collectors.toList());
                if(level == 0 || newLine) {
                    eRow = sheet.createRow(rNum++);
                    cNum = 0;
                }
                if (level > 0 && newLine) {
                    for(int n = 0; n < level; n++) cell = eRow.createCell(cNum++);
                }
                cell = eRow.createCell(cNum++);
                cell.setCellValue( (value != null && !value.equals("") )? value : "(BLANK)");
                /*if (rowspanPerLevel.get(level) > 1) {
                    for (int n = 0; n < rowspanPerLevel.get(level); n++) {
                        cell = eRow.createCell(cNum++);
                    }
                }*/
                if (level == pivotTable.getPivotRowList().size() - 1) {
                    loopCols(workData, 0);
                    printValues(workData);
                } else {
                    loopRows(workData, level + 1);
                }
                newLine = true;
            }
        } else {
            eRow = sheet.createRow(rNum++);
            cNum = 0;
            cell = eRow.createCell(cNum++);
            loopCols(restrictedData, 0);
            printValues(restrictedData);
        }

    }

    private void loopCols(List<Map<String, String>> restrictedData, int level) {
        if(pivotTable.getPivotColumnList().size() > 0) {
            String currentField = pivotTable.getPivotColumnList().get(level).getField().getFieldName();
            for (String value : pageData.stream().map(line -> line.get(currentField)).distinct()
                    .sorted(Comparator.nullsLast(comparator(pivotTable.getPivotColumnList().get(level).getField().getFieldType())))
                    .collect(Collectors.toList())) {
                List<Map<String, String>> workData = new ArrayList<>(restrictedData);
                workData = workData.stream().filter(l ->
                        value != null ? l.get(currentField).equals(value) : l.get(currentField) == null).collect(Collectors.toList());
                if (level == pivotTable.getPivotColumnList().size() - 1) {
                    printValues(workData);
                } else {
                    loopCols(workData, level + 1);
                }
            }
        } else {
            printValues(restrictedData);
        }

    }

    private void printValues(List<Map<String, String>> restrictedData){
        for (PivotValue pivotValue : pivotTable.getValuesList()) {
            cell = eRow.createCell(cNum++);
            cell.setCellValue(valueAsRequested(restrictedData, pivotValue));
        }

        if (pivotTable.getValuesList().size() == 0){
            cell = eRow.createCell(cNum++);
            cell.setCellValue(Long.toString(restrictedData.size()));
        }

    }

    Map<Integer, Long> cellspanPerLevel(String dimension){
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

}
