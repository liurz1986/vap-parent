package com.vrv.vap.alarmdeal.business.model.service;

import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import com.vrv.vap.alarmdeal.business.model.model.ModelParamConfig;
import com.vrv.vap.alarmdeal.business.model.vo.*;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ModelManageService extends BaseService<ModelManage, String> {
    public Result<ImportFileResultVO> parseImportFile(MultipartFile file, String guid,String type) throws IOException, ZipException;

    public Result<String> saveModleManage(ModelManageVO modelManageVO) throws IOException;

    public Result<String> editModleManage(ModelManageVO modelManageVO) throws IOException;

    public void deleteByGuids(List<String> guids);

    public Result<Map<String,Object>>  modelTest(String guid);

    public Result<Map<String, Object>> modelTestStatus(String guid);

    public Result<String> publish(ModelPublishVO modelManageVO);

    public Result<String> start(String guid);

    public Result<String> stop(String guid);

    public Result<String> downShelf(String guid);

    public Result<List<ModelVersionVO>> queryModelVersions(String modelId);

    public Result<ModelManageVO> getModelManageByGuid(String guid);

    public void changeVersion(ModelVersionChangeVO modelVersionChangeVO);

    public PageRes<ModelManageVO> getModelManagePage(ModelManageSearchVO search , Pageable pageable);

    public Result<String> modelRun(String modelManageId);


}
