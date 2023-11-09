package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.mapper.core.AssetMapper;
import com.vrv.vap.xc.model.AssetModel;
import com.vrv.vap.xc.model.EmpowerOutputDeviceModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.IBaseAuthConfigService;
import com.vrv.vap.xc.service.report.EmpowerIndicatorsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmpowerIndicatorsServiceImpl implements EmpowerIndicatorsService {

    @Resource
    private IBaseAuthConfigService iBaseAuthConfigService;

    @Resource
    private AssetMapper assetMapper;

    /**
     * 统计部门授权输出设备打印/刻录数
     */
    private List<Map<String, Object>> statisticsPrintBrun(ReportParam model) {
        List<Map<String, Object>> ipByType = iBaseAuthConfigService.getIpByType(model);
        List<String> ipList = ipByType.stream()
                .map(m -> StringUtils.defaultIfBlank((String)m.get("ip"), null))
                .collect(Collectors.toList());
        List<AssetModel> orgNameByIpList = assetMapper.getOrgNameByIpList(ipList);
        Map<String, String> ipToName = orgNameByIpList.stream()
                .collect(Collectors.toMap(AssetModel::getIp, AssetModel::getName));
        ipByType.parallelStream().forEach(data -> {
            // 在放入Map时防止空指针
            String ip = StringUtils.defaultIfBlank((String)data.get("ip"), null);
            data.computeIfAbsent("name", name -> ipToName.get(ip));
        });
        return ipByType;
    }

    @Override
    public VList<EmpowerOutputDeviceModel> statisticsEmpowerService(ReportParam model, boolean total) {
        List<Map<String, Object>> list = statisticsPrintBrun(model);
        Map<Object, Map<Object, List<Map<String, Object>>>> groupResult = list.stream()
                .filter(e -> e.get("typeId").equals(16) || e.get("typeId").equals(29))
                .collect(Collectors.groupingBy(e -> e.get("name"), Collectors.groupingBy(e -> e.get("typeId"))));
        List<EmpowerOutputDeviceModel> result = groupResult.entrySet().stream().map(outerEntry -> {
            String name = (String) outerEntry.getKey();
            Map<Object, List<Map<String, Object>>> typeMap = outerEntry.getValue();
            int printNumber = typeMap.getOrDefault(29, new ArrayList<>()).size();
            int burnNumber = typeMap.getOrDefault(16, new ArrayList<>()).size();
            EmpowerOutputDeviceModel deviceModel = new EmpowerOutputDeviceModel();
            deviceModel.setName(name);
            deviceModel.setPrintNumber(printNumber);
            deviceModel.setBurnNumber(burnNumber);
            deviceModel.setTotal(printNumber, burnNumber);
            return deviceModel;
        }).collect(Collectors.toList());
        if (total){
            // 添加总计
            int totalPrintNumber = result.stream().mapToInt(EmpowerOutputDeviceModel::getPrintNumber).sum();
            int totalBurnNumber = result.stream().mapToInt(EmpowerOutputDeviceModel::getBurnNumber).sum();
            EmpowerOutputDeviceModel totalModel = new EmpowerOutputDeviceModel();
            totalModel.setName("总计");
            totalModel.setPrintNumber(totalPrintNumber);
            totalModel.setBurnNumber(totalBurnNumber);
            totalModel.setTotal(totalPrintNumber, totalBurnNumber);
            result.add(totalModel);
        }
        return VoBuilder.vl(result.size(), result);
    }
}
