package com.vrv.vap.data.vo;


import org.apache.commons.lang3.StringUtils;

public class WhereItem extends WhereCondition {

    private static final String MEANING_LESS = " 1 = 1 ";

    public WhereItem(String operation) {
        super(operation);
    }

    public WhereItem(String operation, String field, String value) {
        super(operation);
        this.field = field;
        this.value = value;
    }

    private String field;

    private String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String operation = this.getOperation().toLowerCase();
        switch (operation) {
            case "between":
                String[] ptns = this.value.split(",");
                if (ptns.length == 2) {
                    return String.format(" %s BETWEEN '%s' AND '%s' ", this.field, ptns[0], ptns[1]);
                }
                return MEANING_LESS;
            case ">":
            case ">=":
            case "<":
            case "<=":
            case "=":
            case "like":
                return String.format(" %s %s '%s' ", this.field, this.getOperation(), this.value);
            case "exist":
                return String.format(" NOT ISNULL(%s) ", this.field);
            case "in":
                if (StringUtils.isBlank(this.value)) {
                    return MEANING_LESS;
                }
                return String.format(" %s %s (%s) ", this.field, this.getOperation(), this.value);
            default:
                return MEANING_LESS;
        }

    }
}
