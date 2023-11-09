package com.vrv.vap.data.component.config;

import com.vrv.vap.data.mapper.SourceMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DictConfig {

    @Resource
    private SourceMapper mapper;

    private Map<String, Map<String, String>> DICT = null;

    private Map<String, Map<String, String>> PARSER = null;

    private List<Dict> ROOT = null;

    private class Dict {
        private String type;
        private String code;
        private String value;
        private List<Dict> children;

        public Dict(String type, String code, String value) {
            this.type = type;
            this.code = code;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setChildren(List<Dict> children) {
            this.children = children;
        }
//
//        public String getCode() {
//            return code;
//        }
//
//        public String getValue() {
//            return value;
//        }
//
//        public List<Dict> getChildren() {
//            return children;
//        }
    }

    private String stringField(Object val) {
        if (val == null) {
            return null;
        }
        return val.toString();
    }

    private void buildItem(List<Map<String, Object>> records, Dict parent, Dict main) {
        List<Map<String, Object>> childs = records.stream().filter(item -> parent.getType().equals(item.get("parent_type"))).collect(Collectors.toList());
        int len = childs.size();
        if (len > 0) {
            List<Dict> children = new ArrayList<>();
            for (Map<String, Object> child : childs) {
                String type = stringField(child.get("type"));
                String code = stringField(child.get("code"));
                String value = stringField(child.get("code_value"));
                Dict item = new Dict(type, code, value);
                records.remove(child);
                this.buildItem(records, item, main);
                children.add(item);
                Map<String, String> dictItem = DICT.get(main.getType());
                Map<String, String> parserItem = PARSER.get(main.getType());
                dictItem.put(code, value);
                parserItem.put(value, code);
                parent.setChildren(children);
            }
        }
    }


    private void initDict() {
        if (DICT == null) {
            List<Map<String, Object>> records = mapper.execQuery("SELECT code,code_value,type,parent_type FROM base_dict_all");
//            List<Map<String, Object>> records = mapper.execQuery("SELECT code,code_value as value,type,parent_type FROM base_dict_all");
            List<Map<String, Object>> roots = records.stream().filter(item -> item.get("parent_type") == null || "0".equals(item.get("parent_type")) || "".equals(item.get("parent_type"))).collect(Collectors.toList());
            DICT = new HashMap<>();
            PARSER = new HashMap<>();
            ROOT = new ArrayList<>();
            for (Map<String, Object> sub : roots) {
                String type = stringField(sub.get("type"));
                String code = stringField(sub.get("code"));
                String value = stringField(sub.get("code_value"));
                records.remove(sub);
                Dict root = new Dict(type, code, value);
                ROOT.add(root);
                DICT.put(type, new HashMap<>());
                PARSER.put(type, new HashMap<>());
                this.buildItem(records, root, root);
            }
        }
    }


    /**
     * 通过 code 取出 text
     * */
    public String getString(String type, String code) {
        return this.getString(type, code, "");
    }

    public String getString(String type, String code, String def) {
        this.initDict();
        if (!DICT.containsKey(type)) {
            return def;
        }
        Map<String, String> typeMap = DICT.get(type);
        if (typeMap.containsKey(code)) {
            return typeMap.get(code);
        }
        return def;
    }
    /**
     * 通过 text 反向取出 code
     * */
    public String getCode(String type, String value) {
        return this.getString(type, value, "");
    }

    public String getCode(String type, String value, String def) {
        this.initDict();
        if (!PARSER.containsKey(type)) {
            return def;
        }
        Map<String, String> typeMap = PARSER.get(type);
        if (typeMap.containsKey(value)) {
            return typeMap.get(value);
        }
        return def;
    }

}
