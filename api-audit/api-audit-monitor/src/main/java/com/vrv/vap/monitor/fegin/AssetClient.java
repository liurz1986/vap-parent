package com.vrv.vap.monitor.fegin;

import com.vrv.vap.monitor.model.AssetType;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 *
 * @author cz
 * @date 2023年01月04日
 */
@FeignClient("api-asset")
public interface AssetClient {

    @RequestMapping(method = RequestMethod.GET, value = "/assettype/getAssetTypeComboboxTree")
    VData<List<AssetType>> getAssetType();

}
