package com.vrv.vap.data.vo;

import java.util.ArrayList;
import java.util.List;

public class WhereGroup extends WhereCondition {

    private boolean not = false;

    public WhereGroup() {
        super("AND");
        this.items = new ArrayList<>();
    }


    public WhereGroup(String operation) {
        super(operation);
        if ("NOT".equals(operation)) {
            this.setOperation("AND");
            this.not = true;
        }
        this.items = new ArrayList<>();
    }


    private List<WhereCondition> items;

    public List<WhereCondition> getItems() {
        return items;
    }

    public void add(WhereItem item) {
        // 说明 in 和 like 查询效率低，放在where子句最后
        if ("in".equals(item.getOperation())) {
            this.items.add(item);
        } else if ("like".equals(item.getOperation())) {
            String val = item.getValue();
            if (val == null) {
                return;
            }
            if (!val.startsWith("%")) {
                val = "%" + val;
            }
            if (!val.endsWith("%")) {
                val = val + "%";
            }
            item.setValue(val);
            this.items.add(item);
        } else {
            this.items.add(0, item);
        }
    }

    public void add(WhereGroup item) {
        this.items.add(item);
    }


    public void add(RequestParamItem item) {
        this.add(new WhereItem(item.getOperation(), item.getField(), item.getValue()));
    }

    public int size() {
        return this.items.size();
    }

    @Override
    public String toString() {
        int size = this.items.size();
        if (size == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        if (this.not) {
            sb.append(" NOT (");
        }
        for (int i = 0; i < size; i++) {
            WhereCondition condition = this.items.get(i);
            sb.append(condition.toString());
            if (i < (size - 1)) {

                sb.append(" " + this.getOperation() + " ");
            }
        }
        if (this.not) {
            sb.append(')');
        }
        sb.append(')');
        return sb.toString();

    }
}
