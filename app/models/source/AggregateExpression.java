package models.source;

/**
 * Created by hal on 2017-02-21.
 */
class AggregateExpression {
    private AggregateFunction func;
    private String field;
    private String label;

    AggregateExpression(AggregateFunction func, String field, String label)
    {
        this.func = func;
        this.field = field;
        this.label = label;
    }

    AggregateExpression(AggregateFunction func, String field)
    {
        this.func = func;
        this.field = field;
        this.label = this.field + ":" + this.func.toString();
    }

    @Override
    public String toString()
    {
        switch (func) {
            case COUNT:
                return "COUNT(" + field + ") AS " + label;
            case COUNT_DISTINCT:
                return "COUNT(DISTINCT " + field + ") AS " + label;
            case SUM:
                return "SUM(" + field + ") AS " + label;
            case MIN:
                return "MIN(" + field + ") AS " + label;
            case MAX:
                return "MAX(" + field + ") AS " + label;
            case MEAN:
                return "AVG(" + field + ") AS " + label;
            default:
                throw new IllegalArgumentException("Function does not exist: " + func.toString());
        }
    }
}
