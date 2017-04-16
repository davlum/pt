package utils.pivotTableHandler;

import models.pivottable.FieldType;
import models.pivottable.FilterValidValue;
import models.pivottable.PivotTable;
import models.pivottable.PivotValue;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

class RenderStrategy {

    List<Map<String, String>> pivotTableData;
    List<Map<String, String>> pageData;
    PivotTable pivotTable;

    int i = 0;
    int rand = 0;

    RenderStrategy(List<Map<String, String>> pivotTableData, PivotTable pivotTable){
        this.pivotTableData = pivotTableData;
        this.pivotTable = pivotTable;

        this.pivotTable.getFiltersList().forEach(filter -> {
            List<String> validValues = filter.getFilterValidValues().stream()
                    .map(FilterValidValue::getSpecificValue).collect(Collectors.toList());
            this.pivotTableData = this.pivotTableData.stream().filter(l -> validValues.contains(l.get(filter.getField().getFieldName())))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Returns a list of pages of the pivot table
     * @return
     */
    public List<String> pages(){
        if(pivotTable.getPivotPageList().size() > 0){
            List<String> list = pivotTableData.stream().map(l -> l.get(pivotTable.getPivotPageList().get(0).getField().getFieldName()))
                    .distinct().collect(Collectors.toList());
            if (list.size() > 0) return list;
        }
        return Collections.singletonList("all");
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

    private static int size = 0;
    private static double min = 0;
    private static double max = 0;
    String valueAsRequested(List<Map<String, String>> restrictedData, PivotValue pivotValue){
        final DecimalFormat decimalFormatter = new DecimalFormat("###0.00#");
        switch (pivotValue.getPivotValueType().getValueType()){
            case "count":
                return Long.toString(restrictedData.size());
            case "sum":
                return Long.toString(restrictedData.stream().filter(Objects::nonNull).mapToLong(l ->
                        Long.parseLong(l.get(pivotValue.getField().getFieldName()))).sum());
            case "mean":
                SummaryStatistics statistics = new SummaryStatistics();
                restrictedData.stream().filter(Objects::nonNull)
                        .map(s -> Double.parseDouble(s.get(pivotValue.getField().getFieldName())))
                        .forEach(statistics::addValue);
                double average = statistics.getMean();
                return decimalFormatter.format(average);
            case "std_dev":
                StandardDeviation stdDev = new StandardDeviation(true);
                List<Double> array = new ArrayList<>();
                restrictedData.stream().filter(Objects::nonNull)
                        .map(s -> Double.parseDouble(s.get(pivotValue.getField().getFieldName())))
                        .forEach(array::add);
                double[] a = new double[array.size()];
                size = 0;
                array.forEach(d -> {
                    a[size] = d;
                    size++;
                });
                double standardDeviation = stdDev.evaluate(a);
                return decimalFormatter.format(standardDeviation);
            case "min":
                min = 999999999999999d;
                restrictedData.stream().filter(Objects::nonNull)
                        .map(s -> Double.parseDouble(s.get(pivotValue.getField().getFieldName())))
                        .forEach(d -> {
                            if (d < min) min = d;
                        });
                if (min == 999999999999999d) return "N.A.";
                return decimalFormatter.format(min);
            case "max":
                max = -999999999999999d;
                restrictedData.stream().filter(Objects::nonNull)
                        .map(s -> Double.parseDouble(s.get(pivotValue.getField().getFieldName())))
                        .forEach(d -> {
                            if (d > max) max = d;
                        });
                if (max == -999999999999999d) return "N.A.";
                return decimalFormatter.format(max);
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
