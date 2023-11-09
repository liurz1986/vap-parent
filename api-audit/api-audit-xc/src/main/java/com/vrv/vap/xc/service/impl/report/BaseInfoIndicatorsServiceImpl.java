package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.mapper.core.AssetMapper;
import com.vrv.vap.xc.model.AssetCountModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.BaseInfoIndicatorsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BaseInfoIndicatorsServiceImpl implements BaseInfoIndicatorsService {
    @Resource
    private AssetMapper assetMapper;

    @Override
    public VList<AssetCountModel> statisticsAssetTypeNum(ReportParam model) {
        return Optional.ofNullable(assetMapper.countByAssetType(model)).map(list -> list.stream()
                .sorted(Comparator.comparing(AssetCountModel::getCount).reversed())
                .collect(Collectors.collectingAndThen(Collectors.toList(), assetCountModels -> VoBuilder.vl(assetCountModels.size(), assetCountModels)))
        ).orElseGet(() -> VoBuilder.vl(0, new ArrayList<>()));
    }

    @Override
    public VList<AssetCountModel> statisticsAssetTypeTotal(ReportParam model) {
        VList<AssetCountModel> result = statisticsAssetTypeNum(model);
        List<AssetCountModel> newAddedList = result != null ? result.getList() : new ArrayList<>();
        List<AssetCountModel> assetCountModels = assetMapper.countByAssetType(null);
        assetCountModels.forEach(assetCountModel -> {
            assetCountModel.setTotal(assetCountModel.getCount());
            Optional<AssetCountModel> matchingEntry = newAddedList.stream()
                    .filter(entry -> entry.getCode().equals(assetCountModel.getCode()))
                    .findFirst();
            if (matchingEntry.isPresent()) {
                assetCountModel.setCount(matchingEntry.get().getCount());
            } else {
                assetCountModel.setCount(0);
            }
        });
        return VoBuilder.vl(assetCountModels.size(), assetCountModels);
    }

}
