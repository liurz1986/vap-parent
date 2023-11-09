package com.vrv.vap.monitor.fegin;

import com.vrv.vap.monitor.model.AssetType;
import com.vrv.vap.toolkit.vo.VData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *
 * @author cz
 * @date 2023年07月11日
 */
@FeignClient("api-alarmdeal")
public interface Asset2Client {

    @RequestMapping(method = RequestMethod.GET, value = "/assettype/getAssetTypeComboboxTree")
    VData<List<AssetType>> getAssetType();

}
