package com.vrv.vap.amonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.amonitor.command.MonitorRunnerV2;
import com.vrv.vap.amonitor.entity.*;
import com.vrv.vap.amonitor.fegin.Asset2Client;
import com.vrv.vap.amonitor.fegin.AssetClient;
import com.vrv.vap.amonitor.mapper.*;
import com.vrv.vap.amonitor.model.AssetType;
import com.vrv.vap.amonitor.model.ConfLookup;
import com.vrv.vap.amonitor.service.AssetMonitorInfoV2Service;
import com.vrv.vap.amonitor.vo.ConfLookupQuery;
import com.vrv.vap.amonitor.vo.Monitor2AssetIndicatorViewQuery;
import com.vrv.vap.amonitor.vo.Monitor2AssetTypeQuery;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssetMonitorInfoV2ServiceImpl implements AssetMonitorInfoV2Service {

    @Lazy
    @Autowired
    private MonitorRunnerV2 monitorRunner;

    @Autowired
    @Lazy
    private AssetClient assetClient;

    @Autowired
    @Lazy
    private Asset2Client asset2Client;

    @Autowired
    private Monitor2AssetInfoMapper mapper;

    @Autowired
    private Monitor2AssetOidAlgMapper oidMapper;

    @Autowired
    private Monitor2IndicatorMapper indicatorMapper;

    @Autowired
    private Monitor2IndicatorViewMapper indicatorViewMapper;

    @Autowired
    private Monitor2AssetIndicatorViewMapper assetIndicatorViewMapper;

    @Autowired
    private ConfLookupMapper confLookupMapper;

    @Autowired
    private Monitor2AssetTypeMapper monitor2AssetTypeMapper;

    @Value("${monitor.xc:false}")
    private boolean isXc;

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public int addItem(Monitor2AssetInfo monitor2AssetInfo) {
        return mapper.insert(monitor2AssetInfo);
    }

    @Override
    public int updateItem(Monitor2AssetInfo record) {
        record.setUpdateTime(LocalDateTime.now());
        Monitor2AssetInfo old = querySingle(record);
        int res = mapper.updateById(record);
        if (res == 0) {
            return res;
        }
        if (old.getStartupState() == 0 && record.getStartupState() == 1) {
            monitorRunner.addOrUpdateMonitor(record);
        }
        if (record.getStartupState() == 0) {
            monitorRunner.deleteMonitor(record);
        } else {
            //snmp连接配置发生变化
            if (record.getMonitorSetting() != null && !old.getMonitorSetting().equals(record.getMonitorSetting())) {
                monitorRunner.addOrUpdateMonitor(record);
            }
        }
        return res;
    }

    @Override
    public int deleteItem(Monitor2AssetInfo monitor2AssetInfo) {
        return mapper.deleteById(monitor2AssetInfo.getId());
    }

    @Override
    public Monitor2AssetInfo querySingle(Monitor2AssetInfo monitor2AssetInfo) {
        return mapper.selectById(monitor2AssetInfo.getId());
    }

    @Override
    public VList<Monitor2AssetInfo> queryByPage(Query record) {
        Page<Monitor2AssetInfo> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<Monitor2AssetInfo> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(mapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData<List<Monitor2AssetInfo>> queryAll(Query query) {
        QueryWrapper<Monitor2AssetInfo> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vd(mapper.selectList(queryWrapper));
    }

    @Override
    public void updateConnectStatus(Monitor2AssetInfo assetInfo) {
        QueryWrapper<Monitor2AssetInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dev_id", assetInfo.getDevId());
        mapper.update(assetInfo, queryWrapper);
    }

    @Override
    public List<Monitor2Indicator> queryIndicatorAll(Query query) {

        if (query.getOrder() == null) {
            query.setOrder("indicator_type,indicator_name");
            query.setBy("asc");
        }
        QueryWrapper<Monitor2Indicator> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        List<Monitor2Indicator> monitorIndicators = indicatorMapper.selectList(queryWrapper);
        return monitorIndicators;
    }

    @Override
    public List<Monitor2IndicatorView> queryIndicatorViewAll(Query query) {
        QueryWrapper<Monitor2IndicatorView> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        List<Monitor2IndicatorView> monitor2IndicatorViews = indicatorViewMapper.selectList(queryWrapper);
        return monitor2IndicatorViews;
    }

    @Override
    public List<Monitor2AssetIndicatorView> queryAssetIndicatorViewAll(Monitor2AssetIndicatorViewQuery query) {
        QueryWrapper<Monitor2AssetIndicatorView> queryWrapper = new QueryWrapper<>();
        //解决因资产类型改变导致的bug
        if(!StringUtils.isEmpty(query.getSnoUnicode())){
            query.setAssetType(null);
        }
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        List<Monitor2AssetIndicatorView> monitor2IndicatorViews = assetIndicatorViewMapper.selectList(queryWrapper);
        return monitor2IndicatorViews;
    }

    @Override
    public int saveAssetIndicatorViewAll(Monitor2AssetIndicatorView record) {
        if (record.getId() == null) {
            return assetIndicatorViewMapper.insert(record);
        } else {
            return assetIndicatorViewMapper.updateById(record);
        }
    }


    public VData<List<AssetType>> getMonitorAssetType() {
        VData<List<AssetType>> result = new VData<List<AssetType>>();


        AssetType rootType = new AssetType();
        rootType.setTreeCode("asset");
        rootType.setType("0");
        getAssetTypeTreeList(rootType);

        result.setData(rootType.getChildren());
        return result;
    }


    private void getAssetTypeTreeList(AssetType assetType) {

        List<Monitor2AssetType> assetTypeModels = getAssetTypeChildrenList(assetType.getTreeCode());

        if (assetTypeModels != null && !assetTypeModels.isEmpty()) {
            List<AssetType> children = new ArrayList<>();

            for (Monitor2AssetType item : assetTypeModels) {
                AssetType child = monitor2AssetTypeConvert(item);
                child.setType(Integer.toString(Integer.parseInt(assetType.getType()) + 1));
                getAssetTypeTreeList(child);//自己调用自己
                children.add(child);
            }
            assetType.setChildren(children);
        }
    }


    private AssetType monitor2AssetTypeConvert(Monitor2AssetType item) {


        AssetType asetType = new AssetType();
        asetType.setTreeCode(item.getTreeCode());
        asetType.setGuid(item.getGuid());
        asetType.setKey(item.getTreeCode());
        asetType.setTitle(item.getTitle());
        asetType.setIconCls(item.getIconCls());
        asetType.setUniqueCode(item.getUniqueCode());
        asetType.setParentId(item.getParentTreeCode());

        return asetType;
    }


    private List<Monitor2AssetType> getAssetTypeChildrenList(String parentCode) {
        if (StringUtils.isEmpty(parentCode)) {
            parentCode = "asset";
        }

        Monitor2AssetTypeQuery param = new Monitor2AssetTypeQuery();
        param.setParentTreeCode(parentCode);
        QueryWrapper<Monitor2AssetType> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, param);
        List<Monitor2AssetType> lookupModels = monitor2AssetTypeMapper.selectList(queryWrapper);

        return lookupModels;
    }


    @Override
    public VData<List<AssetType>> getMonitorAssetTypeTree() {
        VData<List<AssetType>> vData = getMonitorAssetType();
        return vData;
    }

    @Override
    public VData<List<AssetType>> getAssetType() {
        VData<List<AssetType>> vData = remoteSearchAssetType();


        ConfLookupQuery param = new ConfLookupQuery();
        param.setCode("ASSET_TYPE_SHOW");
        QueryWrapper<ConfLookup> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, param);
        List<ConfLookup> lookupModels = confLookupMapper.selectList(queryWrapper);
        //String assetTypeShow = lookupModels.isEmpty() ? "000000004ff3ad18014ff40347d10090,000000004ff3ad18014ff40dd3250098,000000004ff3ad18014ff4102483009b" : lookupModels.get(0).getValue();
        //
        String assetTypeShow = lookupModels.isEmpty() ? "" : lookupModels.get(0).getValue();
        if (StringUtils.isEmpty(assetTypeShow)) {
            assetTypeShow = "000000004ff3ad18014ff40347d10090,000000004ff3ad18014ff40dd3250098,000000004ff3ad18014ff411adb1009d,322f660945a14ea2a2b5244329134c1e,6d282dbaf86e4f5491ece02ac98cef72,7f63cce4220e4d46a0bda87a8a6c69f1,a7caf459f41449349081180260c44f5c,a7caf459f41449349081180260c4lf5c";
        }
        List<String> assetTypeShowList = Arrays.asList(assetTypeShow.split(","));
        List<AssetType> assetTypes = vData.getData().stream().filter(f -> f.getChildren() != null && !f.getChildren().isEmpty() && assetTypeShowList.contains(f.getGuid()))
                .collect(Collectors.toList());

        vData.setData(assetTypes);
        return vData;
    }

    private VData<List<AssetType>> remoteSearchAssetType() {
        if (isXc) {
            return asset2Client.getAssetType();
        }
        return assetClient.getAssetType();
    }

    @Override
    public List<Map<String, Object>> selectAssetConnectStatus() {
        List<Map<String, Object>> maps = mapper.selectAssetConnectStatus();
        return maps;
    }

    @Override
    public VData<AssetStatistic> statisticAssetCount() {
        AssetStatistic info = new AssetStatistic();
        info.setConnectCount(mapper.selectAssetConnectCount());
        info.setUnConnectCount(mapper.selectAssetUnConnectCount());
        info.setUnMonitorCount(mapper.selectAssetUnMonitorCount());
        return VoBuilder.vd(info);
    }

    @Override
    public List<Map<String, Object>> selectAssetOnlineTop() {
        List<Map<String, Object>> maps = mapper.selectAssetOnlineTop();
        return maps;
    }
}
