package com.vrv.vap.monitor.es.compatible;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vrv.vap.monitor.es.client.EsClient;
import com.vrv.vap.monitor.tools.QueryTools;
import org.elasticsearch.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * es版本适配
 */
public class VersionFit5To7 {

    private static final Logger log = LoggerFactory.getLogger(VersionFit5To7.class);

    private static final String VERSION_5 = "5";
    private static final String VERSION_6 = "6";
    private static final String VERSION_7 = "7";


    public static void adaptiveVersion() {
        Version version = null;
        if (isV5()) {
            version = Version.V_5_5_3;
        } else if (isV6()) {
            version = Version.V_6_7_1;
        } else if (isV7()) {
            version = Version.fromId(7070199);
        } else {
            version = Version.V_5_5_3;
        }

        try {
            Field versionField = Version.class.getDeclaredField("CURRENT");
            versionField.setAccessible(true);
            Field modifiers = versionField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            //去除final使之可被修改
            modifiers.setInt(versionField, versionField.getModifiers() & ~Modifier.FINAL);
            versionField.set(null, version != null ? version : Version.V_5_5_3);
            modifiers.setInt(versionField, versionField.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            log.error("TermsAggregationBuilder", e);
        }
    }

    public static boolean isV7() {
        return EsClient.getVersion().startsWith(VERSION_7);
    }

    public static boolean isV6() {
        return EsClient.getVersion().startsWith(VERSION_6);
    }

    public static boolean isV5() {
        return EsClient.getVersion().startsWith(VERSION_5);
    }

    public static String removeUnSupportKey(String jsonParam) {
        if (!EsClient.getVersion().equals(VERSION_5)) {
            return jsonParam;
        }

        String res;
        try {
            Map<String, Object> map = QueryTools.objectMapper.readValue(jsonParam, Map.class);
            /*if (EsClient.getVersion().equals(VERSION_7)) {
                //es7以上版本有hits total限制, 设置track_total_hits=true则返回真实total
                map.put("track_total_hits", "true");
            }*/
            removeUnSupportKey(map);

            res = QueryTools.objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            res = jsonParam;
        }
        return res;
    }

    private static void removeUnSupportKey(Map<String, Object> map) {
        //String seqNoPrimaryTerm = "seq_no_primary_term";
        String[] unSupportKeys = {"seq_no_primary_term", "auto_generate_synonyms_phrase_query"};

        for (String unSupportKey : unSupportKeys) {
            if (map.containsKey(unSupportKey)) {
                map.remove(unSupportKey);
            }
        }

        map.forEach((k, v) -> {
            if (v instanceof Map) {
                Map<String, Object> map2 = (Map<String, Object>) v;
                for (String unSupportKey : unSupportKeys) {
                    if (map2.containsKey(unSupportKey)) {
                        map2.remove(unSupportKey);
                    }
                }
                removeUnSupportKey(map2);
            }
        });
    }

}
