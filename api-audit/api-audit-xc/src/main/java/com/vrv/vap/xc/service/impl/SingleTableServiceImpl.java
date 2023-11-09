package com.vrv.vap.xc.service.impl;

import com.vrv.vap.xc.constants.QueryConstants;
import com.vrv.vap.xc.init.SingleTableBuilder;
import com.vrv.vap.xc.mapper.core.custom.SingleTableMapper;
import com.vrv.vap.xc.model.QueryModel;
import com.vrv.vap.xc.model.SingleTableModel;
import com.vrv.vap.xc.service.SingleTableService;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class SingleTableServiceImpl implements SingleTableService {

    private static final Log log = LogFactory.getLog(SingleTableServiceImpl.class);

    @Autowired
    private SingleTableMapper singleTableDao;

    @Override
    public Optional<VList<Map<String, Object>>> queryAll(SingleTableModel singleTableModel) {
        Optional<String> tmpColumnOpt = Arrays.stream(singleTableModel.getColumnString().split(",")).map(a -> a + " as " + CommonTools.underLineToCamel(a)).reduce((a, b) -> a + "," + b);
        String tmpColumn = tmpColumnOpt.isPresent() ? tmpColumnOpt.get() : "";
        String sql = new StringBuffer().append("select ").append(tmpColumn).append(" ").append("from ")
                .append(singleTableModel.getTable()).append(" limit 1000").toString();
        log.debug(sql);
        List<Map<String, Object>> list = singleTableDao.queryAll(sql);
        return Optional.of(VoBuilder.vl(list.size(), list));
    }

    @Override
    public Optional<VList<Map<String, Object>>> query(SingleTableModel singleTableModel, QueryModel queryModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("start", queryModel.getStart());
        map.put("count", queryModel.getCount());
        map.put("table", queryModel.getTable());
        if (null != queryModel.getWhere() && !queryModel.getWhere().isEmpty()) {
            Optional<Map<String, Object>> tmp = buildWhere(singleTableModel, queryModel.getWhere());
            if (tmp.isPresent()) {
                map.putAll(tmp.get());
            } else {
                return Optional.empty();
            }
        }

        Optional<String> tmpColumnOpt = Arrays.stream(singleTableModel.getColumnString().split(",")).map(a -> a + " as " + CommonTools.underLineToCamel(a)).reduce((a, b) -> a + "," + b);
        String tmpColumn = tmpColumnOpt.isPresent() ? tmpColumnOpt.get() : "";
        map.put("column", tmpColumn);

        if (StringUtils.isNotEmpty(queryModel.getOrder()) && StringUtils.isNotEmpty(queryModel.getBy())) {
            map.put(queryModel.getBy().toLowerCase(), queryModel.getOrder());
        }

        List<Map<String, Object>> list = singleTableDao.query(map);
        int total = singleTableDao.queryCount(map);

        return Optional.of(VoBuilder.vl(total, list));
    }

    @Override
    public Optional<VList<Map<String, Object>>> query(String table, String column, Map<String, Object> requestMap) {
        Optional<SingleTableModel> tmpOpt = SingleTableBuilder.getCloneSingleTableModel(table);
        if (!tmpOpt.isPresent()) {
            return Optional.empty();
        }
        SingleTableModel singleTableModel = tmpOpt.get();

        Optional<String> tmpColumnOpt = Arrays.stream(singleTableModel.getColumnString().split(",")).map(a -> a + " as " + CommonTools.underLineToCamel(a)).reduce((a, b) -> a + "," + b);
        String tmpColumn = tmpColumnOpt.isPresent() ? tmpColumnOpt.get() : "";
        singleTableModel.setColumnString(tmpColumn);

        QueryModel queryModel = SingleTableBuilder.buildQueryModel(table, requestMap);

        Map<String, Object> map = new HashMap<>();
        map.put("start", queryModel.getStart());
        map.put("count", queryModel.getCount());
        map.put("table", queryModel.getTable());
        if (null != queryModel.getWhere() && !queryModel.getWhere().isEmpty()) {
            Optional<Map<String, Object>> tmp = buildWhere(singleTableModel, queryModel.getWhere());
            if (tmp.isPresent()) {
                map.putAll(tmp.get());
            } else {
                return Optional.empty();
            }
        }
        map.put("column", singleTableModel.getColumnString());

        if (StringUtils.isNotEmpty(queryModel.getOrder()) && StringUtils.isNotEmpty(queryModel.getBy())) {
            map.put(queryModel.getBy().toLowerCase(), queryModel.getOrder());
        }

        List<Map<String, Object>> list = singleTableDao.query(map);
        int total = singleTableDao.queryCount(map);

        return Optional.of(VoBuilder.vl(total, list));
    }

    @Override
    public RetMsgEnum delete(SingleTableModel singleTableModel, String[] pk) {
        Map<String, Object> map = new HashMap<>();
        map.put("table", singleTableModel.getTable());
        map.put("pk", singleTableModel.getPrimaryKey());
        map.put("vs", pk);
        singleTableDao.delete(map);
        return RetMsgEnum.SUCCESS;
    }

    private Optional<Map<String, Object>> buildWhere(SingleTableModel singleTableModel, Map<String, Object> param) {
        Map<String, Object> retMap = new HashMap<>();

        Map<String, Object> likeMap = new HashMap<>(2);
        Map<String, Object> exactMap = new HashMap<>(2);
        Map<String, Object> smallRangeMap = new HashMap<>(1);
        Map<String, Object> bigRangeMap = new HashMap<>(1);

        param.entrySet().stream().forEach(kv -> {
            Object value = kv.getValue();
            if (null != value && StringUtils.isNotEmpty(value.toString())) {
                if (kv.getKey().startsWith(QueryConstants.EXACT)) {
                    String key = kv.getKey().substring(QueryConstants.EXACT.length());
                    exactMap.put(singleTableModel.getCamelToDbMap().getOrDefault(key, key), value);
                } else if (kv.getKey().startsWith(QueryConstants.LIKE)) {
                    String key = kv.getKey().substring(QueryConstants.LIKE.length());
                    likeMap.put(singleTableModel.getCamelToDbMap().getOrDefault(key, key), value);
                } else if (kv.getKey().startsWith(QueryConstants.RANGE_LTE)) {
                    String key = kv.getKey().substring(QueryConstants.RANGE_LTE.length());
                    bigRangeMap.put(singleTableModel.getCamelToDbMap().getOrDefault(key, key), value);
                } else if (kv.getKey().startsWith(QueryConstants.RANGE_GTE)) {
                    String key = kv.getKey().substring(QueryConstants.RANGE_GTE.length());
                    smallRangeMap.put(singleTableModel.getCamelToDbMap().getOrDefault(key, key), value);
                } else {
                    String key = kv.getKey();
                    likeMap.put(singleTableModel.getCamelToDbMap().getOrDefault(key, key), value);
                }
            }
        });
        if (!likeMap.isEmpty()) {
            if (checkColumns(singleTableModel, likeMap)) {
                retMap.put("likes", likeMap);
            } else {
                return Optional.empty();
            }
        }
        if (!exactMap.isEmpty()) {
            if (checkColumns(singleTableModel, likeMap)) {
                retMap.put("exacts", exactMap);
            } else {
                return Optional.empty();
            }
        }
        if (!bigRangeMap.isEmpty()) {
            if (checkColumns(singleTableModel, bigRangeMap)) {
                retMap.put("brange", bigRangeMap);
            } else {
                return Optional.empty();
            }
        }
        if (!smallRangeMap.isEmpty()) {
            if (checkColumns(singleTableModel, smallRangeMap)) {
                retMap.put("srange", smallRangeMap);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(retMap);
    }

    @Override
    public Optional<String> add(SingleTableModel singleTableModel, Map<String, Object> kv) {
        if (!checkColumns(singleTableModel, kv)) {
            return Optional.empty();
        }

        String[] columns = new String[kv.size()];
        Object[] values = new Object[kv.size()];
        int i = 0;
        for (String key : kv.keySet()) {
            columns[i] = key;
            values[i] = kv.get(key);
            i++;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("table", singleTableModel.getTable());
        param.put("columns", columns);
        param.put("values", values);
        singleTableDao.add(param);
        return Optional.of(param.get("_id").toString());
    }

    @Override
    public RetMsgEnum update(SingleTableModel singleTableModel, Map<String, Object> kv) {
        Object pkv = kv.get("pkv");
        kv.remove("pkv");
        Map<String, Object> param = new HashMap<>();
        param.put("table", singleTableModel.getTable());
        param.put("pk", singleTableModel.getPrimaryKey());
        param.put("pkv", pkv);
        param.put("kvs", kv);
        if (checkColumns(singleTableModel, kv)) {
            singleTableDao.update(param);
            return RetMsgEnum.SUCCESS;
        }
        return RetMsgEnum.SERVER_ERROR;
    }

    /**
     * 校验字段是否合法
     *
     * @param singleTableModel
     * @param kv
     * @return
     */
    private boolean checkColumns(SingleTableModel singleTableModel, Map<String, Object> kv) {
        for (String key : kv.keySet()) {
            if (singleTableModel.getColumnList().contains(key) || singleTableModel.getColumnCamelList().contains(key)) {
                continue;
            }
            return false;
        }
        return true;
    }
}
