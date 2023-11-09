package com.vrv.vap.data.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.util.TimeTools;
import com.vrv.vap.data.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConvertSQL {


    // 语法规范 , 目前仅做 GroupBy  OrderBy  的实现
    // https://www.sqlite.org/lang_select.html
    // 目前仅实现 WHERE 子句部分转换（其它部分没有业务上的需求）
    // 1. timefiled 放最前
    // 2. like 条件放最后
    // 3. 数字条件放前， 字段串条件放后
    // 4. AND 条件放前, OR 条件放后 (已在add方法里面优化)

    private static Set<String> TEXT_SET = new HashSet<String>(Arrays.asList(new String[]{"varchar", "char"}));

    public WhereGroup buildWhere(CommonRequest query, List<SourceField> sourceFields, String timeField) {
        WhereGroup root = new WhereGroup("AND");
        if (StringUtils.isNotBlank(timeField)) {
            WhereGroup time = new WhereGroup("AND");
            time.add(new WhereItem(">=", timeField, TimeTools.format(query.getStartTime())));
            time.add(new WhereItem("<=", timeField, TimeTools.format(query.getEndTime())));
            root.add(time);
        }
        RequestParam filters = query.getParam();
        WhereGroup filter = new WhereGroup("AND");
        RequestParamItem[] must = filters.getMust();
        RequestParamItem[] should = filters.getShould();
        RequestParamItem[] mustNot = filters.getMust_not();
        if (must != null && must.length > 0) {
            for (RequestParamItem item : must) {
                filter.add(item);
            }
        }
        if (should != null && should.length > 0) {
            WhereGroup or = new WhereGroup("OR");
            for (RequestParamItem item : should) {
                or.add(item);
            }
            filter.add(or);
        }
        if (mustNot != null && mustNot.length > 0) {
            WhereGroup not = new WhereGroup("NOT");
            for (RequestParamItem item : mustNot) {
                not.add(item);
            }
            filter.add(not);
        }
        if (filter.size() > 0) {
            root.add(filter);
        }
        WhereGroup q = new WhereGroup("OR");
        String keyword = filters.getQ();
        if (StringUtils.isNoneBlank(keyword)) {
            for (SourceField field : sourceFields) {
                if (TEXT_SET.contains(field.getOrigin()) && field.getShow() && StringUtils.isNoneBlank(field.getName())) {
                    q.add(new WhereItem("LIKE", field.getField(), "%" + keyword.trim() + "%"));
                }
            }
            if (q.getItems().size() > 0) {
                root.add(q);
            }
        }
        return root;
    }

    /**
     * 将JSON体解析为 SqlWhere
     */
    public WhereCondition parseWhere(String str) throws JsonProcessingException {
        if (StringUtils.isBlank(str)) {
            return SYSTEM.EMPTY_WHERE;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode root = mapper.readTree(str);
        WhereGroup parent = new WhereGroup();      // 借壳递归
        parseNode(root, parent);
        if (parent.getItems().size() == 0) {
            return SYSTEM.EMPTY_WHERE;
        }
        return parent.getItems().get(0);
    }

    private void parseNode(JsonNode node, WhereGroup parent) {
        if (node.has("field")) {
            parent.add(new WhereItem(node.get("operation").textValue(), node.get("field").textValue(), node.get("value") == null ? null : node.get("value").textValue()));
        } else {
            WhereGroup children = new WhereGroup(node.get("operation").textValue());
            ArrayNode arr = node.withArray("items");
            for (JsonNode item : arr) {
                parseNode(item, children);
            }
            if (children.getItems().size() > 0) {
                parent.add(children);
            }
        }
    }


    public String buildAgg(String table, String timeField, Date start, Date end, String where) {
        SqlGroup param = new SqlGroup();
        param.setTable(table);
        param.setOrder_(timeField);
        param.setBy_(timeInterval(start, end));
        param.setWhere(where);
        param.setFields(new SqlGroupField[]{new SqlGroupField(timeField, "COUNT", "total", false)});
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 自动计算时间区间
     */
    private String timeInterval(Date start, Date end) {
        long seconds = Math.abs((end.getTime() - start.getTime()) / 1000);
        // 1小时内 ，以分钟区域
        if (seconds < 3600) {
            return "datetrend-m";
        }
        // 两天内（48小时） 以小时区分
        if (seconds < 172800) {
            return "datetrend-h";
        }
        // 60 天内，以 天区分
        if (seconds < 5184000) {
            return "datetrend-d";
        }
        // 超过60天，以月区域
        return "datetrend-M";
    }


}
