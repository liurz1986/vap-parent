package com.vrv.vap.xc.service.impl.portrait;

import cn.hutool.core.util.StrUtil;
import com.vrv.vap.toolkit.constant.DeviceTypeEnum;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.portrait.OperationService;
import com.vrv.vap.xc.tools.DictTools;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class OperationServiceImpl implements OperationService {

    @Resource
    private FileTransferServiceImpl fileTransferService;


    public String[] getIpList(String ips){
        String[] ipList = new String[0];
        if (StringUtils.isNotEmpty(ips)){
            ipList = ips.split(",");
        }
        return ipList;
    }

    /**
     * 根据设备类型构建ip查询条件
     *
     * @param model
     * @param query
     */
    public void buildIpConditionByDeviceType(ObjectPortraitModel model, BoolQueryBuilder query) {
        DeviceTypeEnum deviceType = DeviceTypeEnum.forString(model.getDevTypeGroup());
        boolean noIp = StrUtil.isEmpty(model.getIp());
        switch (deviceType) {
            case OPERATION_TERMINAL:
                query.must(QueryBuilders.termQuery("client_ip", model.getDevIp()));
                if (!noIp) {
                    query.must(QueryBuilders.termQuery("resource_ip", model.getIp()));
                }
                break;
            case SERVER:
            case SECURITY_CONFIDENTIALITY_PRODUCTS:
            case NETWORK_DEVICE:
                query.must(QueryBuilders.termQuery("resource_ip", model.getDevIp()));
                if (!noIp) {
                    query.must(QueryBuilders.termQuery("client_ip", model.getIp()));
                }
                break;
            default:
                query.must(QueryBuilders.termsQuery("resource_ip", getIpList(model.getIps())));
                if (!noIp) {
                    query.must(QueryBuilders.termQuery("client_ip", model.getIp()));
                }
                break;
        }
    }

    private Pair<EsQueryModel, QueryTools.QueryWrapper> buildQuery(ObjectPortraitModel model, BuildQuery buildQuery, boolean work) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, buildQuery.getIndex(), "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        buildIpConditionByDeviceType(model, query);
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(model.getConnType()).to(m -> query.must(QueryBuilders.termQuery("conn_type", DictTools.translate("68ccdf6f-89ef-4528-a973-3ca4fb4509c6", model.getConnType()))));
        map.from(model.getConnPort()).to(m -> query.must(QueryBuilders.termQuery("conn_port", model.getConnPort())));
        map.from(model.getInstruct()).to(m -> query.must(QueryBuilders.termQuery("opt_detail", model.getInstruct())));
        map.from(model.getClientIp()).to(m -> query.must(QueryBuilders.termQuery("client_ip", model.getClientIp())));
        map.from(model.getResourceAccount()).to(m -> query.must(QueryBuilders.termQuery("resource_account", model.getResourceAccount())));
        map.from(model.getResourceIp()).to(m -> query.must(QueryBuilders.termQuery("resource_ip", model.getResourceIp())));
        map.from(model.getResourceTypeGroup()).to(m -> query.must(QueryBuilders.termQuery("resource_type_group", model.getResourceTypeGroup())));
        map.from(model.getStdDevTypeGroup()).to(m -> query.must(QueryBuilders.termQuery("std_dev_type_group", model.getStdDevTypeGroup())));
        if (!work) QueryTools.buildNonWorkTimeQuery(queryModel, query, model.getInterval());
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }

    private VData<List<Map<String, Object>>> getListVData(ObjectPortraitModel model, BuildQuery buildQuery, boolean work) {
        buildQuery.setSize(100);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, work);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.simpleAggregation(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> number(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        buildQuery.setKeyField("ip");
        if (Objects.equals(DeviceTypeEnum.OPERATION_TERMINAL.getKey(), model.getDevTypeGroup())) {
            buildQuery.setAggField("resource_ip");
        } else {
            buildQuery.setAggField("client_ip");
        }
        return getListVData(model, buildQuery, model.isWork());
    }

    @Override
    public VData<List<Map<String, Object>>> protocol(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        buildQuery.setAggField("conn_type");
        buildQuery.setKeyField("type");
        return getListVData(model, buildQuery, model.isWork());
    }

    @Override
    public VData<List<Map<String, Object>>> port(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        buildQuery.setAggField("conn_port");
        buildQuery.setKeyField("port");
        return getListVData(model, buildQuery, model.isWork());
    }

    @Override
    public VData<List<Map<String, Object>>> trend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, model.isWork());
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.dateAgg(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> nonWorkTime(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, model.isWork());
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.dateAgg(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> instruct(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        buildQuery.setAggField("opt_detail");
        buildQuery.setKeyField("instruct");
        return getListVData(model, buildQuery, model.isWork());
    }

    @Override
    public VList<Map<String, String>> detail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, model.isWork());
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model, false);
    }

    @Override
    public VData<Export.Progress> export(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.OPERATION_AUDIT);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, true);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model, true);
        List<Map<String, String>> resultSortList = mapVList.getList();
        ExcelEnum excelEnumType = ExcelEnum.OPERATION_DETAIL;
        if (Objects.equals("6", model.getDevTypeGroup())) {
            excelEnumType = ExcelEnum.OPERATION_DETAIL2;
        }
        return fileTransferService.export(resultSortList, excelEnumType, model.getExportName());
    }
}
