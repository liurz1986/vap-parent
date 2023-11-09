package com.vrv.vap.admin.service.feign;


import com.vrv.vap.admin.util.Result;
import com.vrv.vap.admin.vo.AssetTypeVO;
import com.vrv.vap.admin.vo.AssetVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient("api-asset")
public interface AssertFeign {


    /**
     * 添加资产
     */
    @RequestMapping(value = "/asset/saveAddAsset",method = RequestMethod.POST,consumes= MediaType.APPLICATION_JSON_VALUE)
    public Result<String> saveAddAsset(@RequestBody AssetVo assetVO);


    /**
     *查询二级资产
     */
    @RequestMapping(value = "/assettype/{uniqueCode}",method = RequestMethod.GET,consumes= MediaType.APPLICATION_JSON_VALUE)
    public AssetTypeVO getAssetTypeByType(@PathVariable(value="uniqueCode") String uniqueCode );

    /**
     * 添加二级资产
     */
    @RequestMapping(value = "/assettype/saveAddAssetType",method = RequestMethod.POST,consumes= MediaType.APPLICATION_JSON_VALUE)
    public Result<Boolean> saveAddAssetType(@RequestBody AssetTypeVO assetTypeVO);









}
