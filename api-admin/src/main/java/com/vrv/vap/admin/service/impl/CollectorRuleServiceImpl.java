package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.CommonUtil;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.admin.service.CollectorRuleService;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.interceptor.DataPrepareConfig;
import com.vrv.vap.interceptor.DataPrepareInterceptor;
import com.vrv.vap.interceptor.dataprepare.DataPrepareEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
@Service
@Transactional
public class CollectorRuleServiceImpl extends BaseServiceImpl<CollectorRule> implements CollectorRuleService {

    private static final Logger log = LoggerFactory.getLogger(CollectorRuleServiceImpl.class);

    // 字符串分隔
    private static final int CHARATERTYPE_STR = 1;

    // 第一步
    private static final String STEP_ONE = "1";

    @Override
    public Map<String,Object> getFlumeRule(CollectorRule collectorRule,String step) {
        Map<String,String> map = new HashMap<>();
        map.put("fields",collectorRule.getFields());
        map.put("rename",collectorRule.getRenames());
        String handler = collectorRule.getHandler();
        map.put("handler",handler);
        String relateIndex = collectorRule.getRelateIndex();
        if (StringUtils.isEmpty(relateIndex)) {
            map.put("headers","_TYPE:test");
        } else {
            map.put("headers","_TYPE:" + relateIndex);
        }
        String split = collectorRule.getSplit();
        if (StringUtils.isNotEmpty(split)) {
            map.put("split",split);
        }
        String regex = collectorRule.getRegex();
        if (StringUtils.isNotEmpty(regex)) {
            map.put("regex",regex);
        }
        map.put("source",collectorRule.getSource());
        Map<String,Object> result = this.prepareData(map,step);
        return result;
    }

    private Map<String,Object> prepareData(Map<String, String> context,String step) {
        Charset charset = Charset.forName("UTF-8");
        DataPrepareConfig config = new DataPrepareConfig(context);
        String[] fields = config.getFields();
        String source = context.get("source");
        byte[] body = source.getBytes(charset);
        DataPrepareEvent event = null;
        if (config.getHandler().equals(DataPrepareConfig.DataPrepareHandler.TEST)) {
            // 尝试用json解析
            event = this.tryJson(config,step, body);
            if (null == event) {
                // 尝试用分隔符解析
                event = this.trySplit(config, fields, body);
            }
            // 返回失败结果
            if (null == event) {
                return null;
            }
        } else {
            if (STEP_ONE.equals(step) && config.getHandler().equals(DataPrepareConfig.DataPrepareHandler.JSON)) {
                // json数据首次解析时不使用指定字段
                config.setFields(null);
            }
            DataPrepareInterceptor interceptor = new DataPrepareInterceptor(config);
            interceptor.initialize();
            event = new DataPrepareEvent();
            event.setBody(body);
            event = (DataPrepareEvent) interceptor.intercept(event);
            if (null == event) {
                return null;
            }
        }
        Map<String,Object> result = new HashMap<>();
        result.put("fields", event.getFieldsAsString());
        result.put("headers", event.getHeaders());
        result.put("error", event.isError());
        result.put("body", new String(event.getBody(), charset));
        result.put("handler", config.getHandler().name());
        switch (config.getHandler()) {
            case SPLIT:
                result.put("split", config.getSplit());
                break;
            case REGEX:
                result.put("regex", config.getRegex());
                break;
            case JSON:
            default:
                // 不做处理
        }
        return result;
    }

    private DataPrepareEvent tryJson(DataPrepareConfig config,String step, byte[] body) {
        config.setHandler(DataPrepareConfig.DataPrepareHandler.JSON);
        if (STEP_ONE.equals(step)) {
            config.setFields(null);
        }
        DataPrepareInterceptor interceptor = new DataPrepareInterceptor(config);
        interceptor.initialize();
        DataPrepareEvent event = new DataPrepareEvent();
        event.setBody(body);
        try {
            event = interceptor.tryit(event);
        } catch (Exception e) {
            // 不做处理
            event = null;
        }
        return event;
    }

    private DataPrepareEvent trySplit(DataPrepareConfig config, String[] fields, byte[] body) {
        config.setHandler(DataPrepareConfig.DataPrepareHandler.SPLIT);
        config.setFields(fields);
        String[] splits = new String[] { ";", ",", "\\|", "#", "~", "!", "@", "\\$", "%", "\\^", "&", "\\*", "\\s" };
        DataPrepareEvent event = null;
        for (String s : splits) {
            config.setSplit(s);
            DataPrepareInterceptor interceptor = new DataPrepareInterceptor(config);
            interceptor.initialize();
            event = new DataPrepareEvent();
            event.setBody(body);
            try {
                event = interceptor.tryit(event);
            } catch (Exception e) {
                // 不做处理
                event = null;
            }
            if (null != event) {
                break;
            }
        }
        return event;
    }

    public String buildConfigData(Map<String,Object> result,String renames) {
        if (result == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");
        sb.append("a1.sources.r1.interceptors = i1 i2" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.type = com.vrv.vap.interceptor.DataPrepareInterceptor$Builder" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.handler = " + result.get("handler") + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.fields = " + result.get("fields") + "\r\n");
        Map headers = (Map) result.get("headers");
        sb.append("a1.sources.r1.interceptors.i1.headers = _TYPE:" + headers.get("_TYPE") + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.ignoreError = true" + "\r\n");
        if (StringUtils.isNotEmpty(renames)) {
            sb.append("a1.sources.r1.interceptors.i1.rename = " + renames + "\r\n");
        }
        if (StringUtils.isNotEmpty((String) result.get("regex"))) {
            String regex = (String) result.get("regex");
            sb.append("a1.sources.r1.interceptors.i1.regex = " + Base64Util.encode(regex) + "\r\n");
            sb.append("a1.sources.r1.interceptors.i1.regexbase64 = true"  + "\r\n");
        }
        return sb.toString();
    }

    @Override
    public String generateRules(List<CollectorRule> ruleList,Integer accessId,Boolean updateNew) {
        if (CollectionUtils.isEmpty(ruleList)) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");
        sb.append("rules:");
        sb.append("\r\n");
        ruleList.stream().forEach(item -> {
            sb.append("  - type: " + item.getRelateIndex());
            sb.append("\r\n");
            sb.append("    type-match:");
            sb.append("\r\n");
            if (item.getPriority() != null) {
                sb.append("      priority: " + item.getPriority());
                sb.append("\r\n");
            }
            if (item.getCharaterType() == CHARATERTYPE_STR) {
                sb.append("      includes:");
                sb.append("\r\n");
                String charater = item.getCharater();
                if (StringUtils.isNotEmpty(charater)) {
                    String[] charaterArr = charater.split("&&");
                    for (String cha : charaterArr) {
                        if (StringUtils.isNotEmpty(cha)) {
                            sb.append("        - '" + cha + "'");
                            sb.append("\r\n");
                        }
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(item.getCharater())) {
                    sb.append("      regex: '" + item.getCharater() + "'");
                    sb.append("\r\n");
                }
            }
            sb.append("    handler: " + item.getHandler());
            sb.append("\r\n");
            if (StringUtils.isNotEmpty(item.getRegex())) {
                sb.append("    regex: " + item.getRegex());
            }
            sb.append("\r\n");
            if (StringUtils.isNotEmpty(item.getSplit())) {
                sb.append("    split: " + item.getSplit());
                sb.append("\r\n");
            }
            String fields = item.getFields();
            if (StringUtils.isNotEmpty(fields)) {
                Map renameMap = new HashMap();
                String renames = item.getRenames();
                if (StringUtils.isNotEmpty(renames)) {
                    String[] renamesArr = renames.split(",");
                    for (String rename : renamesArr) {
                        if (StringUtils.isNotEmpty(rename)) {
                            String[] renameArr = rename.split(":");
                            renameMap.put(renameArr[0],renameArr[1]);
                        }
                    }
                }

                String[] fieldsArr = fields.split(",");
                sb.append("    fields:");
                sb.append("\r\n");
                for (String field : fieldsArr) {
                    if ("JSON".equals(item.getHandler())) {
                        sb.append("      - " + field);
                    } else {
                        if (renameMap.containsKey(field)) {
                            sb.append("      - " + renameMap.get(field));
                        } else {
                            sb.append("      - " + field);
                        }
                    }
                    sb.append("\r\n");
                }
                if (fields.endsWith(",")) {
                    sb.append("      - ");
                    sb.append("\r\n");
                }
            }
            if ("JSON".equals(item.getHandler())) {
                String renames = item.getRenames();
                if (StringUtils.isNotEmpty(renames)) {
                    String[] renamesArr = renames.split(",");
                    sb.append("    renames:");
                    sb.append("\r\n");
                    for (String rename : renamesArr) {
                        if (StringUtils.isNotEmpty(rename)) {
                            sb.append("      - " + rename);
                            sb.append("\r\n");
                        }
                    }
                }
            }
        });
        // 写入yml文件
        String rulesContent = sb.toString();
        String workingDir = CommonUtil.getBaseInfo("VAP_WORK_DIR");
        String filePath = workingDir + File.separator + "flume" + File.separator + "file" + File.separator + "00" + File.separator + "data-prepare-rules" + accessId + ".yml";
        if (updateNew) {
            FileUtils.writeFile(rulesContent,filePath);
        }
        return filePath;
    }

    @Override
    public void syncJsContent(Integer collectionId, String jsContent) {
        List<CollectorRule> ruleList = this.findByProperty(CollectorRule.class,"collectionId",collectionId);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(ruleList)) {
            for (CollectorRule rule : ruleList) {
                rule.setJsContent(jsContent);
                this.updateSelective(rule);
            }
        }
    }
}
