package models.pivottable;


public enum FieldType {
    String("String"), Boolean("Boolean"), Integer("Integer"), Number("Number"),
    Date("Date"), Time("Time"), DateTime("DateTime");

    private final String displayName;

    FieldType(final String displayName){
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}