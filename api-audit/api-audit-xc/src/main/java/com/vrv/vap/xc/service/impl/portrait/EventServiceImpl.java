package com.vrv.vap.xc.service.impl.portrait;

import cn.hutool.core.collection.IterUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.toolkit.constant.AlarmDealStateEnum;
import com.vrv.vap.toolkit.constant.AssetTypeGroupEnum;
import com.vrv.vap.toolkit.model.UserInfoModel;
import com.vrv.vap.toolkit.tools.SessionTools;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.mapper.core.AssetMapper;
import com.vrv.vap.xc.mapper.core.AssetTypeMapper;
import com.vrv.vap.xc.model.AssetTypeModel;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.pojo.AssetExtend;
import com.vrv.vap.xc.pojo.AssetType;
import com.vrv.vap.xc.pojo.BaseSecurityDomain;
import com.vrv.vap.xc.pojo.SelfConcernAsset;
import com.vrv.vap.xc.service.IAssetTypeService;
import com.vrv.vap.xc.service.IBaseSecurityDomainService;
import com.vrv.vap.xc.service.ISelfConcernAssetService;
import com.vrv.vap.xc.service.portrait.EventService;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.xc.vo.AssetVO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    @Resource
    private AssetMapper assetMapper;
    @Resource
    private AssetTypeMapper assetTypeMapper;
    @Resource
    private IAssetTypeService iAssetTypeService;
    @Resource
    private ISelfConcernAssetService iSelfConcernAssetService;
    @Resource
    private IBaseSecurityDomainService iBaseSecurityDomainService;

    /**
     * 设置分页
     *
     * @param model 请求参数
     * @return
     */
    private Page<AssetVO> setPage(AssetTypeModel model) {
        String order = model.getOrder();
        List<OrderItem> orderItems = new ArrayList<>();
        if (StringUtils.isNotEmpty(order)) {
            OrderItem item = new OrderItem();
            item.setColumn(order);
            item.setAsc(!"desc".equals(model.getBy()));
            orderItems.add(item);
        }
        int offset = model.getMyStart();
        int pageSize = model.getMyCount();
        int pageNum = offset / pageSize + 1;
        Page<AssetVO> page = new Page<>(pageNum, pageSize);
        page.setOrders(orderItems);
        return page;
    }

    @Override
    public Page<AssetVO> portraitList(AssetTypeModel model) {
        if (model.isFocusAssets()) {
            model.setFocusAssetsList(getFocusAssetsList());
        }
        //设置分页
        Page<AssetVO> page = setPage(model);
        //获取资产类型列表
        List<String> guidList = searchAssetType(model.getAssetTypeCode());
        model.setGuidList(guidList);
        //关联查询资产与窃泄密风险
        Page<AssetVO> assetList = assetMapper.findAssetStealLeakValue(page, model);
        //获取所有安全域
        List<BaseSecurityDomain> allDomains = queryAllDomain();
        //组装资产关联数据
        populateAssetVOListWithDetails(assetList.getRecords(), allDomains);
        //查询资产事件数
        populateEventNumbersForAssets(assetList.getRecords(), model);
        //设置关注资产
        setAssetsConcernStatus(assetList.getRecords());
        return assetList;
    }

    /**
     * 获取关注资产
     *
     * @return 关注资产列表
     */
    public List<String> getFocusAssetsList() {
        Optional<UserInfoModel> user = SessionTools.getUserInfo();
        final List<String> result = new ArrayList<>();
        user.ifPresent(model -> {
            Integer userId = model.getId();
            List<SelfConcernAsset> selfConcernAsset = iSelfConcernAssetService.findSelfConcernAsset(userId);
            List<String> ipList = selfConcernAsset.stream().map(SelfConcernAsset::getIp).collect(Collectors.toList());
            result.addAll(ipList);
        });
        return result;
    }

    /**
     * 设置关注资产
     *
     * @param list 资产列表
     */
    private void setAssetsConcernStatus(List<AssetVO> list) {
        Optional<UserInfoModel> user = SessionTools.getUserInfo();
        user.ifPresent(userInfoModel -> {
            Integer userId = userInfoModel.getId();
            Set<String> ipsOfConcern = iSelfConcernAssetService.findSelfConcernAsset(userId)
                    .stream()
                    .map(SelfConcernAsset::getIp)
                    .collect(Collectors.toSet());
            list.forEach(assetVO -> {
                if (ipsOfConcern.contains(assetVO.getIp())) {
                    assetVO.setFocusAssets(true);
                }
            });
        });
    }

    /**
     * 获取资产类型列表
     *
     * @param assetTypeCode 资产类型编码
     * @return
     */
    private List<String> searchAssetType(String assetTypeCode) {
        List<String> guids = new ArrayList<>();
        if (StringUtils.isEmpty(assetTypeCode)) return guids;
        String assetType = AssetTypeGroupEnum.forString(assetTypeCode).getValue();
        if (StringUtils.isBlank(assetType)) return guids;
        List<AssetType> assetTypeList = iAssetTypeService.findAssetTypeByCode(assetType);
        if (IterUtil.isNotEmpty(assetTypeList)) {
            guids.addAll(assetTypeList.stream().map(AssetType::getGuid).collect(Collectors.toList()));
        }
        return guids;
    }

    private List<BaseSecurityDomain> queryAllDomain() {
        return iBaseSecurityDomainService.findAll().stream().filter(domain -> StringUtils.isNotEmpty(domain.getParentCode())).collect(Collectors.toList());
    }

    /**
     * 根据guid查询资产类型和资产扩展信息，并组装资产关联数据
     *
     * @param assetVOList 资产列表
     * @param allDomains  安全域列表
     */
    private void populateAssetVOListWithDetails(List<AssetVO> assetVOList, List<BaseSecurityDomain> allDomains) {
        List<String> guidList = assetVOList.stream().map(AssetVO::getGuid).collect(Collectors.toList());
        // 根据guid列表查询资产类型信息
        List<AssetType> typeList = assetTypeMapper.findAssetTypeByGuidIn(guidList);
        // 根据guid列表查询资产扩展信息
        List<AssetExtend> extendList = assetTypeMapper.findAssetExtendByAssetGuidIn(guidList);
        Map<String, String> extendInfoMap = extendList.stream()
                .filter(extend -> extend != null && !StringUtils.isEmpty(extend.getExtendInfos()))
                .collect(Collectors.toMap(AssetExtend::getAssetGuid, AssetExtend::getExtendInfos));
        for (AssetVO asset : assetVOList) {
            AssetVO assetVO = fillAssetTypeAndSecurityName(asset, allDomains, typeList);
            String extendInfo = extendInfoMap.get(asset.getGuid());
            if (extendInfo != null) {
                assetVO.setAssetExtendInfo(extendInfo);
            }
        }
    }

    /**
     * 填充资产类型和安全域名称
     *
     * @param assetVO 资产关联实体
     * @param domains 安全域列表
     * @param types   资产类型列表
     * @return assetVO
     */
    private AssetVO fillAssetTypeAndSecurityName(AssetVO assetVO, List<BaseSecurityDomain> domains, List<AssetType> types) {
        Map<String, String> typeNameMap = types.stream().collect(Collectors.toMap(AssetType::getGuid, AssetType::getName));
        String typeName = typeNameMap.get(assetVO.getGuid());
        if (typeName != null) {
            assetVO.setTypeName(typeName);
        }
        setSecurityName(assetVO, domains, assetVO.getSecurityGuid());
        return assetVO;
    }

    private void setSecurityName(AssetVO assetVo, List<BaseSecurityDomain> domains, String guid) {
        Optional<BaseSecurityDomain> domain = domains.stream()
                .filter(d -> d.getCode().equals(guid))
                .findFirst();
        domain.ifPresent(d -> assetVo.setSecurityName(d.getDomainName()));
    }

    /**
     * 查询资产事件数
     *
     * @param list  资产列表
     * @param model 请求参数
     */
    private void populateEventNumbersForAssets(List<AssetVO> list, AssetTypeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.ALARM_EVENT_MANAGEMENT);
        List<String> ipList = list.stream().map(AssetVO::getIp).collect(Collectors.toList());
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(QueryBuilders.termsQuery("principalIp", ipList));
        queryBuilder.mustNot(QueryBuilders.termQuery("alarmDealState", AlarmDealStateEnum.PROCESSED.getValue()));
        queryModel.setQueryBuilder(queryBuilder);
        List<Map<String, Object>> result = QueryTools.simpleAgg(queryModel, wrapper, "principalIp", 10, "principalIp", "count");
        Map<String, Long> ipToCountMap = result.stream().collect(Collectors.groupingBy(e -> (String) e.get("principalIp"), Collectors.counting()));
        list.forEach(assetVO -> {
            Long count = ipToCountMap.get(assetVO.getIp());
            if (count != null) {
                assetVO.setEventNumber(Math.toIntExact(count));
            }
        });
    }

}
