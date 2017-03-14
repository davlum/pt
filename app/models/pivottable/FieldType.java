package models.pivottable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum FieldType {
    String("String"), Boolean("Boolean"), Long("Long"),
    Date("Date"), Time("Time"), DateTime("DateTime"), Double("Double");

    private final String displayName;

    FieldType(final String displayName){
        this.displayName = displayName;
    }

    public static boolean onlyCount(FieldType fieldType){
        switch (fieldType) {
            case Long:
            case Double:
                return false;
            default:
                return true;
        }
    }

    public static FieldType decide(List<String> list){
        Map<FieldType, List<FieldType>> types = list.stream().map(s -> {
            try {
                java.lang.Long.parseLong(s);
                return Long;
            } catch (Exception e1){
                try {
                    java.lang.Double.parseDouble(s);
                    return Double;
                } catch (Exception e2){
                    if (Arrays.asList("true", "false").contains(s.toLowerCase())){
                        return Boolean;
                    } else {
                        try {
                            DateTimeFormatter df1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                            df1.parseDateTime(s);
                            return DateTime;
                        } catch (Exception e4){
                            try {
                                DateTimeFormatter df2 = DateTimeFormat.forPattern("yyyy-MM-dd");
                                df2.parseDateTime(s);
                                return Date;
                            } catch (Exception e5){
                                try {
                                    DateTimeFormatter df3 = DateTimeFormat.forPattern("HH:mm:ss");
                                    df3.parseDateTime(s);
                                    return Time;
                                } catch (Exception e6){
                                    return String;
                                }
                            }
                        }
                    }
                }
            }
        }).collect(Collectors.groupingBy(s -> s));

        if (types.get(String) != null && types.get(String).size() > 0) return String;
        if (types.get(Time) != null && types.get(Time).size() > 0) return Time;
        if (types.get(Date) != null && types.get(Date).size() > 0) return Date;
        if (types.get(DateTime) != null && types.get(DateTime).size() > 0) return DateTime;
        if (types.get(Boolean) != null && types.get(Boolean).size() > 0) return Boolean;
        if (types.get(Double) != null && types.get(Double).size() > 0) return Double;
        if (types.get(Long) != null && types.get(Long).size() > 0) return Long;

        return null;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
