package com.vrv.vap.xc.service.impl.portrait;

import cn.hutool.core.util.StrUtil;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.portrait.BoundaryFlowService;
import com.vrv.vap.xc.tools.QueryTools;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class BoundaryFlowServiceImpl implements BoundaryFlowService {

    @Resource
    private FileTransferServiceImpl fileTransferService;

    private Map<String,List<Pair<Long,Long>>> splitIpRanges(List<CommunicationModel> ipRangeList){
        Map<String,List<Pair<Long,Long>>> pairMap = new HashMap<>();
        for (CommunicationModel entry : ipRangeList) {
            String rangeIps = entry.getRangeIps();
            if (StrUtil.isEmpty(rangeIps)) continue;
            List<Pair<Long,Long>> pairList = new ArrayList<>();
            if (rangeIps.contains(",")) {
                String[] split = rangeIps.split(",");
                for (String s : split) {
                    String[] ipRange = s.split("-");
                    long beginIp = QueryTools.transferIpNumber(ipRange[0]);
                    long endIp = QueryTools.transferIpNumber(ipRange[1]);
                    pairList.add(Pair.of(beginIp, endIp));
                }
            } else {
                String[] ipRange = rangeIps.split("-");
                long beginIp = QueryTools.transferIpNumber(ipRange[0]);
                long endIp = QueryTools.transferIpNumber(ipRange[1]);
                pairList.add(Pair.of(beginIp, endIp));
            }
            pairMap.put(rangeIps, pairList);
        }
        return pairMap;
    }

    @Override
    public VData<List<Map<String, Object>>> communicationTotalPkt(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP, LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setMultipleIndex(true);
        buildQuery.setAggField("sip_num");
        buildQuery.setKeyField("ipNum");
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        // 获取前一个月的日期
        calendar.add(Calendar.MONTH, -1);
        Date beginTime = calendar.getTime();
        model.setMyStartTime(beginTime);
        model.setMyEndTime(endTime);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO ex = QueryTools.buildQueryCondition(buildQuery);
        ex.setSumAggFields(new String[]{"client_total_pkt", "server_total_pkt"});
        List<CommunicationModel> ipRangeList = model.getIpRangeList();
        Map<String, List<Pair<Long, Long>>> pairMap = splitIpRanges(ipRangeList);
        List<Map<String, Object>> resultList = QueryTools.aggregationSum(pair.getFirst(), pair.getSecond(), ex, pairMap, ipRangeList);
        return VoBuilder.vd(resultList);
    }

    @Override
    public VData<List<Map<String, Object>>> sendReceiveFlowTrend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP, LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setMultipleIndex(true);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        buildQuery.setAggField("1".equals(model.getTransfer()) ? "client_total_byte" : "server_total_byte");
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        // 流量传输方向 1发送 2接收
        entry.setSumAddField("1".equals(model.getTransfer()) ? "client_total_byte" : "server_total_byte");
        List<Map<String, Object>> list = QueryTools.sumAggDate(pair.getFirst(), pair.getSecond(), entry, true);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> communicationTotalTrend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP, LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setMultipleIndex(true);
        buildQuery.setDate(true);
        buildQuery.setInterval(model.getInterval());
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        entry.setSumAggFields(new String[]{"client_total_pkt", "server_total_pkt"});
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAggDate(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> detail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> export(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> resultSortList = mapVList.getList();
        return fileTransferService.export(resultSortList, ExcelEnum.NET_WORK_BOUNDARY_DETAIL, model.getExportName());
    }
}
