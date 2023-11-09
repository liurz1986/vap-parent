package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.constant.FileDirEnum;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.mapper.core.AssetMapper;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.pojo.BaseSecurityDomain;
import com.vrv.vap.xc.pojo.BaseSecurityDomainIpSegment;
import com.vrv.vap.xc.service.IBaseSecurityDomainIpSegmentService;
import com.vrv.vap.xc.service.IBaseSecurityDomainService;
import com.vrv.vap.xc.service.report.NetworkIndicatorsService;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class NetworkIndicatorsServiceImpl implements NetworkIndicatorsService {

    @Resource
    private PrintIndicatorsServiceImpl printIndicatorsService;
    @Resource
    private IBaseSecurityDomainService iBaseSecurityDomainService;
    @Resource
    private IBaseSecurityDomainIpSegmentService iBaseSecurityDomainIpSegmentService;

    @Resource
    private AssetMapper assetMapper;

    private static final int size = 1000;

    @Override
    public VList<NetWorkCountModel> statisticsAppAccess(ReportParam model) {
        List<Map<String, String>> allData = queryElasticSearch(model, LogTypeConstants.NET_FLOW_HTTP, false);
        List<Map<String, String>> deptName = getDeptName(allData);
        List<NetWorkCountModel> result = convert(deptName);
        return VoBuilder.vl(result.size(), result);
    }

    private List<Map<String, String>> queryElasticSearch(ReportParam model, String indexName ,boolean query) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, printIndicatorsService.parseModel(model),
                indexName, "event_time");
        queryModel.setCount(size);
        if (query) {
            List<BaseSecurityDomainIpSegment> ipRangeByCodes = findIpRangeByCodes();
            BoolQueryBuilder build = build(ipRangeByCodes);
            queryModel.setQueryBuilder(build);
        }
        List<Map<String, String>> allData = new ArrayList<>();
        SearchResponse response = null;
        int scrollCount = -1;
        do {
            response = wrapper.scrollQuery(queryModel, response == null ? null : response.getScrollId());
            long total = response.getHits().getTotalHits().value;
            List<Map<String, String>> list = wrapper.wrapResponse(response.getHits(), "event_time");
            allData.addAll(list);
            if (scrollCount == -1){
                scrollCount = (int) (total % size == 0 ? total / size : (total / size) + 1);
            }
            scrollCount--;
        } while (scrollCount > 0);
        return allData;
    }

    @Override
    public VList<NetWorkCaseModel> statisticsAppFile(ReportParam model) {
        List<BaseSecurityDomainIpSegment> segments = findIpRangeByCodes();
        List<Map<String, String>> fileRecords = queryElasticSearch(model, LogTypeConstants.NET_FLOW_FILE, true);
        List<Map<String, String>> accessRecords = queryElasticSearch(model, LogTypeConstants.NET_FLOW_HTTP, true);
        List<NetWorkCaseModel> fileStats = transform(fileRecords, segments);
        List<NetWorkCaseModel> accessStats = fillAccessNumber(accessRecords, segments);
        fileStats.forEach(fileStat ->
                accessStats.stream()
                        .filter(accessStat -> fileStat.getName().equals(accessStat.getName()))
                        .findFirst()
                        .ifPresent(accessStat -> fileStat.setAccessNum(accessStat.getAccessNum()))
        );
        return VoBuilder.vl(fileStats.size(), fileStats);
    }

    public List<NetWorkCaseModel> fillAccessNumber(List<Map<String, String>> accessResult, List<BaseSecurityDomainIpSegment> segments) {
        Map<Long, Map<String, List<Map<String, String>>>> groupedByIp = accessResult.stream()
                .collect(Collectors.groupingBy(r ->Long.parseLong(r.get("sip_num")), Collectors.groupingBy(r -> r.get("dst_std_sys_id"))));
        return segments.stream().map(segment -> {
            NetWorkCaseModel model = new NetWorkCaseModel();
            model.setName(segment.getName());
            groupedByIp.entrySet().stream()
                    .filter(e -> inRange(e.getKey(), segment))
                    .forEach(e -> model.setAccessNum(model.getAccessNum() + e.getValue().size()));
            return model;
        }).collect(Collectors.toList());
    }


    /**
     * 组装文件上传下载结果
     * @param records 查询结果
     * @param segments 单位ip范围
     * @return 各单位上传、下载数量
     */
    private List<NetWorkCaseModel> transform(List<Map<String, String>> records, List<BaseSecurityDomainIpSegment> segments) {
        Map<Long, Map<FileDirEnum, List<Map<String, String>>>> groupedByIp = records.stream()
                .collect(Collectors.groupingBy(r -> Long.parseLong(r.get("sip_num")),
                        Collectors.groupingBy(r -> FileDirEnum.forString(r.get("file_dir")))));
        return segments.stream().map(segment -> {
            NetWorkCaseModel model = new NetWorkCaseModel();
            model.setName(segment.getName());
            groupedByIp.entrySet().stream()
                    .filter(e -> inRange(e.getKey(), segment))
                    .forEach(e -> incrementCount(model, e.getValue()));
            return model;
        }).collect(Collectors.toList());
    }

    /**
     * 匹配ip是否在范围内
     * @param ip ip数字
     * @param segment 单位ip范围
     * @return 是否匹配
     */
    private boolean inRange(long ip, BaseSecurityDomainIpSegment segment) {
        return LongStream.rangeClosed(segment.getStartIpNum(), segment.getEndIpNum()).anyMatch(x -> x == ip);
    }

    /**
     * 将范围内的上传下载数量累加
     * @param model 单位上传下载总数
     * @param data 查询结果
     */
    private void incrementCount(NetWorkCaseModel model, Map<FileDirEnum, List<Map<String, String>>> data) {
        if (data.containsKey(FileDirEnum.UPLOAD)) {
            model.setUploadNum(model.getUploadNum() + data.get(FileDirEnum.UPLOAD).size());
        }
        if (data.containsKey(FileDirEnum.DOWNLOAD)) {
            model.setDownloadNum(model.getDownloadNum() + data.get(FileDirEnum.DOWNLOAD).size());
        }
    }

    private BoolQueryBuilder build(List<BaseSecurityDomainIpSegment> ipNumList) {
        BoolQueryBuilder builder = new BoolQueryBuilder();
        for (BaseSecurityDomainIpSegment ipSegment : ipNumList) {
            builder.should(QueryBuilders.rangeQuery("sip_num").gt(ipSegment.getStartIpNum()).lt(ipSegment.getEndIpNum()));
        }
        return builder;
    }

    private List<BaseSecurityDomainIpSegment> findIpRangeByCodes() {
        List<BaseSecurityDomain> listByParentCode = iBaseSecurityDomainService.findListByParentCode();
        List<String> codeList = listByParentCode.stream().map(BaseSecurityDomain::getCode).collect(Collectors.toList());
        return iBaseSecurityDomainIpSegmentService.findIpByCodes(codeList);
    }

    public List<Map<String, String>> getDeptName(List<Map<String, String>> allData) {
        List<String> ipList = allData.stream().map(m -> m.get("sip")).collect(Collectors.toList());
        List<AssetModel> orgNameByIpList = assetMapper.getOrgNameByIpList(ipList);
        Map<String, String> ipToName = orgNameByIpList.stream()
                .collect(Collectors.toMap(AssetModel::getIp, AssetModel::getName));
        allData.parallelStream().forEach(data -> data.computeIfAbsent("name", name -> ipToName.get(data.get("sip"))));
        return allData;
    }

    public List<NetWorkCountModel> convert(List<Map<String, String>> allData) {
        // 按部门名称分组流量数，再按应用编号分组，获取分组结果
        Map<String, Map<String, List<Map<String, String>>>> groupResult = allData.stream()
                .collect(Collectors.groupingBy(e -> e.get("name"),
                        Collectors.groupingBy(e -> e.get("dst_std_sys_id"))));
        return groupResult.entrySet().stream().map(outerEntry -> {
            List<NetWorkModel> elements = outerEntry.getValue().entrySet().stream()
                    .map(innerEntry -> new NetWorkModel(innerEntry.getKey(), innerEntry.getValue().size())).collect(Collectors.toList());
            return new NetWorkCountModel(outerEntry.getKey(), elements);
        }).collect(Collectors.toList());
    }

}
