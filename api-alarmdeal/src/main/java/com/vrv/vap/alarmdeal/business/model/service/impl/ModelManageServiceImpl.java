package com.vrv.vap.alarmdeal.business.model.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.model.constant.ModelManageConstant;
import com.vrv.vap.alarmdeal.business.model.constant.ModelManageTypeEnum;
import com.vrv.vap.alarmdeal.business.model.dao.ModelManageDao;
import com.vrv.vap.alarmdeal.business.model.job.ModelRunJob;
import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import com.vrv.vap.alarmdeal.business.model.model.ModelParamConfig;
import com.vrv.vap.alarmdeal.business.model.repository.ModelManageRepository;
import com.vrv.vap.alarmdeal.business.model.service.ModelManageService;
import com.vrv.vap.alarmdeal.business.model.service.ModelParamConfigService;
import com.vrv.vap.alarmdeal.business.model.util.*;
import com.vrv.vap.alarmdeal.business.model.vo.*;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.quartz.QuartzFactory;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


@Service
@Transactional
public class ModelManageServiceImpl  extends BaseServiceImpl<ModelManage,String> implements ModelManageService {
    private static Logger logger = LoggerFactory.getLogger(ModelManageServiceImpl.class);
    @Autowired
    private ModelManageRepository modelManageRepository;
    @Autowired
    private ModelParamConfigService modelParamConfigService;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private ModelManageDao modelManageDao;
    @Autowired
    private ModelRedisUtil modelRedisUtil;
    @Autowired
    private QuartzFactory quartzFactory;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private FileConfiguration fileConfiguration;
    @Autowired
    private TbConfService tbConfService;
    @Override
    public ModelManageRepository getRepository() {
        return modelManageRepository;
    }

    /**
     * 解析资产导入文件
     * @param file
     * @param guid :为null表示新增导入  有值 表示编辑导入
     * @return
     */
    @Override
    public Result<ImportFileResultVO> parseImportFile(MultipartFile file, String guid,String type) throws IOException, ZipException {
         if(ModelManageConstant.OperationStatus.EDIT.equalsIgnoreCase(type)){
             ModelManage modelManage = this.modelManageRepository.getOne(guid);
             if(null == modelManage){
                 return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置guid没有查到对应信息！");
             }
         }
        return paraseZip(file,guid);
    }


    private Result<ImportFileResultVO> paraseZip(MultipartFile multipartFile, String guid) throws IOException, ZipException {
        // 解压zip包到指定目录
        String uuid = UUIDUtils.get32UUID();
        String path = fileConfiguration.getTempModelPath()+"/"+uuid;
        String zipFileName = multipartFile.getOriginalFilename(); //文件名称(是一个zip压缩包)
        String fileType =zipFileName.substring(zipFileName.lastIndexOf(".")+1,zipFileName.length());
        if(!"zip".equalsIgnoreCase(fileType)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不是zip包！");
        }
        try {
            // 将.zip包上传到指定位置
            FileUtil.uploadFile( multipartFile.getBytes(), path, zipFileName); //上传文件
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"上传文件失败！");
        }
        // 现有zip文件
        String unZipFilePath = path+"/"+zipFileName;
        File file = new File(unZipFilePath);
        //zip文件密码
        TbConf tbconf = tbConfService.getOne("alarmdeal_model_zipfile_password");
        if(null == tbconf){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"tb_conf表中配置文件解密密码(alarmdeal_model_zipfile_password)");
        }
        String zipFilePassword = tbconf.getValue();
        if(StringUtils.isEmpty(zipFilePassword)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件解密密码为空！");
        }
        // 解压zip包到当前路径下
        ZipFileUtil.unzipPassword(file,path,zipFilePassword);
        String fileName = zipFileName.substring(0,zipFileName.lastIndexOf("."));
        unZipFilePath = path+"/"+fileName;
        logger.info("资源上传后解压后的临时路径："+unZipFilePath);
        return parseFileValidate(unZipFilePath,guid);
    }

    /**
     * 解析zip包解压后下面文件：.json和.jar文件，并验证有效性
     * @param unZipFilePath
     * @return
     * @throws IOException
     */
    private Result<ImportFileResultVO> parseFileValidate(String unZipFilePath,String guid) throws IOException {
        File unZipFile = new File(unZipFilePath);
        String[] filelist = unZipFile.list();
        if(null == filelist || filelist.length == 0){ // 如果直接改zip包名称的话，解压后还是以前的名称，这样就会找不到，会报文件内容为空 2022-06-07
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件内容为空！");
        }
        // 文件格式校验
        Result<Map<String,String>> filesFormatValidate = filesFormatValidate(unZipFilePath,filelist);
        if(filesFormatValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),filesFormatValidate.getMsg());
        }
        Map<String,String> data = filesFormatValidate.getData();
        // 校验文件命名格式：模型名称_模型版本号(模型名称与模型版本号不能存在下滑线)
        String sourceFileName = data.get("fileName");
        String[] fileNames = sourceFileName.split("\\_");
        if(!(fileNames.length == 2)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：文件命名不规范，存在多个下划线");
        }
        // 验证json数据格式是否正确
        boolean jsonStatus = validateJsonFile(data.get("jsonData"));
        if(!jsonStatus){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：json文件格式解析异常");
        }
        ImportFileJsonVO modelversion = JSONObject.parseObject(data.get("jsonData"), ImportFileJsonVO.class);
        // 编辑时：验证导入的模型与历史是不是同一类型的：模型名称相同
        boolean nameStatus = validateModelNameEditStatus(guid,fileNames[0]);
        if(!nameStatus){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：不允许导入不同模型名称");
        }
        // 文件名中版本号与.json文件中版本号是否一致
        if(null == modelversion ||StringUtils.isEmpty(modelversion.getVersion())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：版本信息(.json)中版本号为空！");
        }
        if(!modelversion.getVersion().equalsIgnoreCase(fileNames[1])){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：文件名中版本号与.json文件中版本号不一致！");
        }
        // 版本说明不允许超过200
        if(StringUtils.isNotEmpty(modelversion.getVersionDesc())){
            if(modelversion.getVersionDesc().length() > 200){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "版本说明，不允许超过200汉字！");
            }
        }
        ImportFileResultVO resultVo = this.mapper.map(modelversion, ImportFileResultVO.class);
        resultVo.setModelFileName(sourceFileName);
        resultVo.setModelFileGuid(data.get("jarPath"));
        return ResultUtil.success(resultVo);
    }

    /***
     * 文件格式校验
     * @param unZipFilePath
     * @param filelist
     * @return
     * @throws IOException
     */
    private Result<Map<String, String>> filesFormatValidate(String unZipFilePath, String[] filelist) throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<>(); // zip包所有文件名称
        boolean isJson = false;
        boolean isJar = false;
        String jsonData = "";
        String jarPath = null;
        for (int i = 0; i < filelist.length; i++) {
            File readfile = new File(unZipFilePath + "/" + filelist[i]);
            if (readfile.isDirectory()) {
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求(存在文件夹)！");
            }
            String fileName = readfile.getName();
            int lastIndex  = fileName.lastIndexOf(".");  // 通过后缀名判断文件类型
            String name = fileName.substring(0,lastIndex);
            String fileType =fileName.substring(lastIndex+1,fileName.length());
            if(!fileNameList.contains(name)){
                fileNameList.add(name);
            }
            // 校验是不是存在多个文件名
            if(fileNameList.size() > 1){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：zip包中存在文件名不相同文件："+JSON.toJSONString(fileNameList));
            }
            // 校验是不是只有json和jar文件
            if(!ModelManageConstant.fileTypes.contains(fileType)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：存在文件格式"+fileType);
            }
            // josn格式进行解析
            if(ModelManageConstant.FILETYPEJSON.equalsIgnoreCase(fileType)){
                if(isJson){
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：存在两个.json文件");
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(readfile),"utf-8"));
                String tempString = null;
                while((tempString = br.readLine()) != null){
                    jsonData += tempString;
                }
                br.close();
                isJson= true;
            }else{
                if(isJar){
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件不符合要求：存在两个.jar文件");
                }
                jarPath = unZipFilePath + "/" + filelist[i];
                isJar = false;
            }
        }
        result.put("jsonData",jsonData);
        result.put("jarPath",jarPath);
        result.put("fileName",fileNameList.get(0));
        return ResultUtil.success(result);
    }

    /**
     * 判断导入的资源模型与历史模型是不是同一个模型
     * @param guid
     * @param newFileName
     * @return
     */
    private boolean validateModelNameEditStatus(String guid, String newFileName) {
        if(StringUtils.isEmpty(guid)){
            return true;
        }
        ModelManage modelManage = this.modelManageRepository.getOne(guid);
        if(null == modelManage){
            return true;
        }
        String modelFile = modelManage.getModelFileName();
        if(StringUtils.isEmpty(modelFile)){ // 为空表示没有导入模型
            return true;
        }
        String[] modelFiles = modelFile.split("\\_");
        String oldModelName = modelFiles[0];
        if(!oldModelName.equalsIgnoreCase(newFileName)){
            return false;
        }
        return true;
    }

    private boolean validateJsonFile(String jsonData) {
        // 验证json数据格式是否正确
        try{
            if(StringUtils.isNotEmpty(jsonData)){
                ImportFileJsonVO modelversion = JSONObject.parseObject(jsonData, ImportFileJsonVO.class);
            }
        }catch(Exception e){
            logger.error("解析json数据异常",e);
            return false;
        }
        return true;
    }

    @Override
    public Result<String> saveModleManage(ModelManageVO modelManageVO) throws IOException {
        // 数据校验处理
        Result<String> validateResult = validateModelManageSave(modelManageVO);
        if(validateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateResult;
        }
        String modelMangeGuid = UUIDUtils.get32UUID(); //模型配置id
        String modelGuid = UUIDUtils.get32UUID();
        ModelManage modelManage = mapper.map(modelManageVO, ModelManage.class);
        modelManage.setGuid(modelMangeGuid);
        modelManage.setModelId(modelGuid);
        // 将导入模型从临时文件复制到指定的模型存放位置，这是资源导入的唯一标识
        String mdelFileGuid = modelManageVO.getModelFileGuid();
        addModelFilePath(mdelFileGuid,modelManage);

        modelManage.setCreateTime(new Date());
        modelManage.setCreateUser(getSessionUserName());
        modelManage.setStatus(Integer.parseInt(ModelManageTypeEnum.DRAFT.getCode()));
        modelManage.setUsed(0);
        modelManage.setIsDelete(0);
        this.save(modelManage);
        // 保存参数设置
        List<ModelParamConfig> params = modelManageVO.getParamList();
        if(null != params && params.size() >0 ){
            for(ModelParamConfig param : params){
                String guid = UUIDUtils.get32UUID();
                param.setGuid(guid);
                param.setModelManageId(modelMangeGuid);
            }
            this.modelParamConfigService.save(params);
        }
        return ResultUtil.success("新增成功");
    }

    /**
     * 新增数据校验：
     *
     * @param modelManageVO
     * @return
     */
    private Result<String> validateModelManageSave(ModelManageVO modelManageVO) {
        // 模型名称不能W为空
        if(StringUtils.isEmpty(modelManageVO.getModelName())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型名称不能为空！");
        }
        // 版本说明，不允许超过200汉字：模型说明，不允许超过500汉字
        if(StringUtils.isNotEmpty(modelManageVO.getVersionDesc())){
            if(modelManageVO.getVersionDesc().length() > 200){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "版本说明，不允许超过200汉字！");
            }
        }
        if(StringUtils.isNotEmpty(modelManageVO.getModelDesc())){
            if(modelManageVO.getModelDesc().length() > 500){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型说明，不允许超过500汉字！");
            }
        }
        // 模型名称不能重复
        if(modelManageDao.existModelName(modelManageVO.getModelName())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型名称已经存在！");
        }
        return ResultUtil.success("OK");
    }


    /**
     * 编辑：
     * 1.版本一样、没有资源导入执行更新处理，modelFileGuid是资源导入的唯一标识
     * 2.版本不一样新增处理
     * @param modelManageVO
     */
    @Override
    public Result<String> editModleManage(ModelManageVO modelManageVO) throws IOException {
        ModelManage modelManage = this.modelManageRepository.getOne(modelManageVO.getGuid());
        if(null == modelManage){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置不存在！");
        }
        // 数据校验处理
        Result<String> validateResult = validateModelManageEdit(modelManageVO);
        if(validateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateResult;
        }
        // 版本号发生变化，新增一条配置记录；版本号没有发生变化，做修改操作
        String oldVersion = modelManage.getVersion();
        String newVersion = modelManageVO.getVersion();
        // 没有资源导入(modelFileGuid为空)或版本一样，执行修改操作; 否则执行新增操作
        if( StringUtils.isEmpty(modelManageVO.getModelFileGuid())|| oldVersion.equalsIgnoreCase(newVersion)){ // 修改
            // 组装更新数据
            modelManage = getUpdateModelManage(modelManage,modelManageVO);
            // 更新模型配置数据
            this.save(modelManage);
            // 更新配置信息参数
            updateModelConfigParams(modelManageVO.getGuid(),modelManageVO);
        }else{ //新增一条记录：模型id保持不变
            // 判断版本号是不是已经在当前模型中存在
            boolean versionResult = validateCurrentVersion(modelManage.getModelId(),newVersion);
            if(!versionResult){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"改版本号已经存在,请切换到该版本号，进行编辑处理！"+newVersion);
            }
            // 新增一条模型配置信息
            newModelManage(modelManage,modelManageVO);
        }
        return ResultUtil.success("编辑成功！");
    }

    /**
     * 编辑数据校验：
     *
     * @param modelManageVO
     * @return
     */
    private Result<String>  validateModelManageEdit(ModelManageVO modelManageVO) {
        // 版本说明，不允许超过200汉字：模型说明，不允许超过500汉字
        if(StringUtils.isNotEmpty(modelManageVO.getVersionDesc())){
            if(modelManageVO.getVersionDesc().length() > 200){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "版本说明，不允许超过200汉字！");
            }
        }
        if(StringUtils.isNotEmpty(modelManageVO.getModelDesc())){
            if(modelManageVO.getModelDesc().length() > 500){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型说明，不允许超过500汉字！");
            }
        }
        return ResultUtil.success("OK");
    }

    /**
     * 验证当前版本号是否在模型中存在
     * @param modelId
     * @param newVersion
     * @return
     */
    private boolean validateCurrentVersion(String modelId, String newVersion) {
        // 查询模型现有所有版本号
        List<ModelVersionVO> lists = queryModelVersions(modelId).getList();
        // 判断导入版本号是不是存在
        if(null != lists && lists.size() > 1){
            for(ModelVersionVO version : lists){
                if(newVersion.equalsIgnoreCase(version.getVersion())){
                    return false;
                }
            }
        }
        return true;
    }



    /**
     * 组装更新数据
     * @param modelManage
     * @param modelManageVO
     * @return
     * @throws IOException
     */
    private ModelManage getUpdateModelManage(ModelManage modelManage, ModelManageVO modelManageVO) throws IOException {
        // 更新时间及更新人
        modelManage.setUpdateTime(new Date());
        modelManage.setUpdateUser(getSessionUserName());
        modelManage.setCreateUser(modelManage.getCreateUser());
        modelManage.setCreateTime(modelManage.getCreateTime());
        // 待发布状态
        modelManage.setStatus(Integer.parseInt(ModelManageTypeEnum.DRAFT.getCode()));
        // 资源导入信息:如果导入模型文件的唯一标识为空，表示在编辑时没有导入模型;如果导入资源了，更新相关信息
        if(StringUtils.isNotEmpty(modelManageVO.getModelFileGuid())){
            modelManage.setModelRunUrl(modelManageVO.getModelRunUrl());
            modelManage.setModelTestUrl(modelManageVO.getModelTestUrl());
            modelManage.setVersion(modelManageVO.getVersion());
            modelManage.setVersionDesc(modelManageVO.getVersionDesc());
            modelManage.setModelVersionCreateTime(modelManageVO.getModelVersionCreateTime());
            modelManage.setModelFileName(modelManageVO.getModelFileName());
            // 复制导入资源包到运行模型文件下
            String modelTempPath = modelManageVO.getModelFileGuid();
            addModelFilePath(modelTempPath,modelManage);
        }
        // 基本信息页面
        modelManage.setModelDesc(modelManageVO.getModelDesc());
        modelManage.setLabel(modelManageVO.getLabel());
        return modelManage;

    }

    /**
     * 复制导入资源包到运行模型文件下
     * @param mdelFileGuid 资源导入的唯一标识，也是临时路径
     * @param modelManage
     * @throws IOException
     */
    private void addModelFilePath(String mdelFileGuid, ModelManage modelManage) throws IOException {
        if(StringUtils.isEmpty(mdelFileGuid)){
            logger.info("模型临时存放路径为空"); //表示没有导入资源
        }else{
            String modleRunPath = fileConfiguration.getRunModelPath();
            modleRunPath= modleRunPath+"/"+modelManage.getGuid();
            String modelRunPath = tempModelCopyRunModel(mdelFileGuid,modleRunPath);
            modelManage.setModelFilePath(modelRunPath);
        }
    }

    /**
     * 更新配置信息参数
     *
     * @param guid
     * @param modelManageVO
     */
    private void updateModelConfigParams(String guid, ModelManageVO modelManageVO) {
        // 如果没有导入模型不做配置参数更新
        if(StringUtils.isEmpty(modelManageVO.getModelFileGuid())){
            return;
        }
        List<ModelParamConfig> oldParams =modelParamConfigService.queryModelParamList(guid).getList();
        List<ModelParamConfig> newParams =modelManageVO.getParamList();
        // 如果更新后参数为空，删除以前的参数即可
        if(null == newParams || newParams.size() ==0){
            this.modelParamConfigService.deleteInBatch(oldParams);
        }
        for(ModelParamConfig param : newParams){
            param.setGuid(UUIDUtils.get32UUID());
            param.setModelManageId(guid);
        }
        this.modelParamConfigService.deleteInBatch(oldParams); // 删除以前的记录
        this.modelParamConfigService.save(newParams); // 更新新的参数记录
    }

    /**
     * 编辑时：版本不一样时新增一条模型配置信息，同时历史配置信息更新为不可用
     * @param modelManage
     * @param modelManageVO
     * @throws IOException
     */
    private void newModelManage(ModelManage modelManage, ModelManageVO modelManageVO ) throws IOException {
        ModelManage modelManageNew = mapper.map(modelManageVO, ModelManage.class);
        modelManageNew.setCreateUser(getSessionUserName());
        modelManageNew.setCreateTime(new Date());
        String modelMangeGuid = UUIDUtils.get32UUID();
        modelManageNew.setGuid(modelMangeGuid);
        modelManageNew.setModelId(modelManage.getModelId());
        modelManageNew.setUsed(0);
        modelManageNew.setStatus(Integer.parseInt(ModelManageTypeEnum.DRAFT.getCode()));
        modelManageNew.setModelFilePath(modelManageVO.getModelFileGuid());
        modelManageNew.setIsDelete(0);
        modelManageNew.setUpdateUser(null);
        modelManageNew.setUpdateTime(null);
        // 复制导入资源包到运行模型文件下
        String modelTempPath = modelManageVO.getModelFileGuid();
        addModelFilePath(modelTempPath,modelManage);
        // 历史数据更新为不可用
        modelManage.setUsed(-1);
        this.modelManageRepository.save(modelManage);
        // 新增一条记录
        this.modelManageRepository.save(modelManageNew);
        // 同时也新增参数设置
        List<ModelParamConfig> params = modelManageVO.getParamList();
        if(null != params && params.size() >0 ){
            for(ModelParamConfig param : params){
                param.setGuid(UUIDUtils.get32UUID());
                param.setModelManageId(modelMangeGuid);
            }
            this.modelParamConfigService.save(params);
        }
    }

    @Override
    public void deleteByGuids(List<String> guids) {
        modelManageDao.deleteByGuids(guids);
    }


    /**
     * 模型可用性测试
     * 没有具体实现  ---2022-4-2
     * @param guid
     */
    @Override
    public Result<Map<String,Object>>  modelTest(String guid) {
        String uuid = UUIDUtils.get32UUID();
        ModelManage modelManage = modelManageRepository.getOne(guid);
        if(null == modelManage){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置不存在！");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1. Shell命令启动模型
                //2. 检测模型是否启动
                //3. 调用测试接口
                //4. Shell命令停止模型
            }
        }).start();
        Map<String, Object> result = new HashMap<>();
        result.put("guid", uuid);
        return ResultUtil.success(result);
    }




    /**
     * 模型可用性测试状态
     * @param guid
     * @return
     */
    @Override
    public Result<Map<String, Object>> modelTestStatus(String guid) {
        Map<String, Object> result = new HashMap<>();
        result.put("status",  modelRedisUtil.get(guid));
        return ResultUtil.success(result);
    }

    /**
     * 模型发布
     * @param modelManageVO
     */
    @Override
    public Result<String> publish(ModelPublishVO modelManageVO) {
        logger.info("模型启动开始,modelManageVO："+ JSON.toJSONString(modelManageVO));
        ModelManage model =this.modelManageRepository.getOne(modelManageVO.getGuid());
        if(null == model){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置信息不存在！");
        }
        // 验证数据消费周期表达式
        if(StringUtils.isNotEmpty(modelManageVO.getDataCustomerPeriod())){
            if(!CronExpression.isValidExpression(modelManageVO.getDataCustomerPeriod())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"请确认数据消费周期表达式格式！");
            }
        }
        // 1.根据入参集更新参数的当前值
        String paramList = modelManageVO.getModelInputParams();
        updateModelParamConfig(paramList,modelManageVO.getGuid());
        model.setStatus(ModelManageConstant.ModelStatus.DEPLOYE);
        model.setUpdateTime(new Date());
        model.setUpdateUser(getSessionUserName());
        model.setDataCustomerModel(modelManageVO.getDataCustomerModel());
        model.setDataCustomerPeriod(modelManageVO.getDataCustomerPeriod());
        model.setModelInputParams(modelManageVO.getModelInputParams());
        model.setModelStartParam(modelManageVO.getModelStartParam());
        model.setModelLogLevel(modelManageVO.getModelLogLevel());
        model.setModelLogPath(modelManageVO.getModelLogPath());
        //2. 更新发布数据
        this.save(model);
        return ResultUtil.success("模型发布成功");
    }

    /**
     * 根据入参集更新参数的当前值处理
     * @param paramList
     * @param guid
     * @return
     */
    private void updateModelParamConfig(String paramList, String guid) {
        if(StringUtils.isEmpty(paramList)){
            return ;
        }
        Map<String,Object> maps = JSONObject.parseObject(paramList,Map.class);
        if(null == maps || maps.size() ==0){
            return ;
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("modelManageId",guid));
        List<ModelParamConfig> paramConfigs = modelParamConfigService.findAll(conditions);

        if (null==paramConfigs || paramConfigs.size()==0){
            return ;
        }
        // 执行更新处理
        for ( ModelParamConfig config : paramConfigs){
            String key = config.getName();
            if(maps.containsKey(key)){
                config.setParamValue(String.valueOf(maps.get(key)));
            }
        }
        modelParamConfigService.save(paramConfigs);
    }

    /**
     * 模型启动逻辑：
     * 1. 通过shell命令启动模型所在web服务
     * 2. 根据模型数据消费方式执行模型运行
     *     消费方式为一次性，执行调用模型运行(分析)接口
     *    消费方式周期性采用定时任务执行，调用模型运行(分析)接口
     *    模型已经停止的，周期作业不执行
     * @param guid
     */
    @Override
    public Result<String> start(String guid) {
        logger.info("模型启动开始,guid："+ guid);
        ModelManage model =this.modelManageRepository.getOne(guid);
        if(null == model){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置信息不存在！");
        }
        model.setUpdateTime(new Date());
        model.setUpdateUser(getSessionUserName());
        model.setStatus(ModelManageConstant.ModelStatus.START);
        modelManageRepository.save(model); //默认启动成功，异常执行失败后会改回状态，备注启动失败
        // shell命令执行启动操作  nohup java -Xmx200M -Xms200M -Xss200M  -jar xxx.jar --info  > /usr/local/log.log  --info &
        logger.info("执行模型启动,guid："+ guid);
        String modelFilePath =model.getModelFilePath();
        if(StringUtils.isEmpty(modelFilePath)){
            logger.info("模型路径为空，不执行启动操作！");
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"请确认是否有模型存在！");
        }else{
            // startShellExc(model);  // 异步执行启动操作, 目前没有模型暂时屏蔽 2022-4-13
        }
        return ResultUtil.success("模型启动操作成功");
    }

    /**
     * 模型启动异步执行
     * 1.验证模型服务是否已经启动，启动抛出异常
     * 2.执行启动
     * 3.验证进程是否存在
     * 4.启动成功后，据模型数据消费方式执行模型运行
     * @param model
     */
    private void startShellExc(ModelManage model) {
        new Thread(new Runnable() {
            String msg="";
            @Override
            public void run() {
                try{
                    boolean result = true;
                    logger.info("异步执行模型启动，guid="+model.getGuid());
                    msg="验证模型服务是否已经启动";
                    // 验证模型服务是否已经启动，启动抛出异常
                    result = checkServerStatus(model.getModelFilePath()); // 模型的全路径 ：/usr/local/../../xx.jar
                    if(result){
                        updateModelManage("模型已经处于启动中",ModelManageConstant.ModelStatus.DEPLOYE,model);
                        logger.info("模型=="+model.getModelFilePath()+"==已经处于启动中，不需要重复执行启动！");
                        return;
                    }
                    // 异步执行启动
                    msg="启动模型";
                    String startShell = getStartShell(model);
                    result = ShellExcUtil.excShellResult(startShell);
                    if(!result){
                        updateModelManage("启动模型异常",ModelManageConstant.ModelStatus.DEPLOYE,model);
                        logger.info("模型=="+model.getModelFilePath()+"==启动模型异常");
                        return;
                    }
                    Thread.sleep(3000);
                    // 验证进程是否存在
                    msg="验证启动模型进程是否存在";
                    result = checkServerStatus(model.getModelFilePath());
                    if(!result){
                        updateModelManage("模型启动没有成功",ModelManageConstant.ModelStatus.DEPLOYE,model);
                        logger.info("模型=="+model.getModelFilePath()+"==启动成功后，验证进程是否存在异常");
                        return;
                    }
                    //据模型数据消费方式执行模型运行
                    logger.info("据模型数据消费方式执行模型运行");
                    runModelByDataCustomerModel(model);
                }catch(Exception e){
                    model.setRemark("模型启动失败");
                    model.setStatus(ModelManageConstant.ModelStatus.DEPLOYE);
                    modelManageRepository.save(model);  //启动失败不更新状态，记录原因
                    logger.error("执行模型启动异常=="+msg,e);
                }
            }
        }).start();

    }

    private void updateModelManage(String msg, Integer status, ModelManage model) {
        model.setRemark(msg);
        if(status > -1){
            model.setStatus(status);
        }
        modelManageRepository.save(model);
    }

    /**
     * 根据模型数据消费方式执行模型运行
     * @param model
     */
    private void runModelByDataCustomerModel(ModelManage model) {
        String dataCustomerModel = model.getDataCustomerModel();
        String guid = model.getGuid();
        logger.info("模型数据消费方式："+dataCustomerModel);
        // 一次性
        if(ModelManageConstant.DataCustomerModelType.ONE.equalsIgnoreCase(dataCustomerModel)){
            modelRun(guid);
        }
        // 周期性，添加定时任务
        if(ModelManageConstant.DataCustomerModelType.PERIOD.equalsIgnoreCase(dataCustomerModel)){
            if(!quartzFactory.existsJobs(ModelManageConstant.modelJobName+model.getGuid(),ModelRunJob.class)){
                logger.info("定时任务名称:" + ModelManageConstant.modelJobName + model.getGuid());
                quartzFactory.addJob(ModelManageConstant.modelJobName + model.getGuid(), ModelRunJob.class, model.getDataCustomerPeriod(), guid);
            }else{
                logger.info("定时任务名称:"+ModelManageConstant.modelJobName+model.getGuid()+"--已经存在");
            }

        }
    }

    private String getStartShell(ModelManage model) {
        String modelStartParam = model.getModelStartParam();
        String modelFilePath =model.getModelFilePath();
        String modelLogLevel = model.getModelLogLevel();
        String modelLogPath=model.getModelLogPath();
        // shell命令执行启动操作  nohup java -Xmx200M -Xms200M -Xss200M  -jar xxx.jar --info  > /usr/local/log.log  --info &
        StringBuffer checkStatusShell = new StringBuffer();
        checkStatusShell.append("nohup java ");
        if(StringUtils.isNotEmpty(modelStartParam)){
            checkStatusShell.append(modelStartParam+" ");
        }
        checkStatusShell.append("-jar ");
        checkStatusShell.append(modelFilePath);
        if(StringUtils.isNotEmpty(modelLogLevel)){
            checkStatusShell.append(" --"+modelLogLevel);
        }
        if(StringUtils.isNotEmpty(modelLogPath)){
            checkStatusShell.append("  >"+modelLogPath);
        }
        checkStatusShell.append("  &");
        return checkStatusShell.toString();
    }

    // 验证进程是否存在
    private boolean checkServerStatus(String name){
        List<String> list = getServerPid(name);
        if(null != list && list.size() >0){
            logger.info("进程信息："+ JSON.toJSONString(list));
            return true;
        }
        return false;
    }

    /**
     * 获取服务进程
     * @param serverName
     * @return
     */
    private List<String> getServerPid(String serverName){
        StringBuffer checkStatusShell = new StringBuffer();
        checkStatusShell.append("ps  -ef|grep -v 'grep'|grep  '");
        checkStatusShell.append(serverName);
        checkStatusShell.append("'|awk '{print $2}'");
        return ShellExcUtil.excShellList(checkStatusShell.toString());
    }
    /**
     * 模型停止
     * @param guid
     */
    @Override
    public Result<String> stop(String guid) {
        logger.info("模型停止,guid："+ guid);
        ModelManage model =this.modelManageRepository.getOne(guid);
        if(null == model){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置信息不存在！");
        }
        // 目前没有模型暂时屏蔽 2022-4-13
        /*Result<String> result= excStopShell(model);
        if(!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())){
            return result;
        }*/
        model.setStatus(ModelManageConstant.ModelStatus.STOP);
        model.setUpdateTime(new Date());
        model.setUpdateUser(getSessionUserName());
        this.save(model);
        return ResultUtil.success("模型停止成功");
    }

    /**
     * 执行模型停止操作
     * @param model
     * @return
     */
    private Result<String> excStopShell(ModelManage model) {
        String serverPath =model.getModelFilePath();
        if(StringUtils.isEmpty(serverPath)){
            logger.info("模型路径为空，不执行启动操作！");
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"请确认是否有模型存在！");
        }else{
            // shell命令执行停止操作
            String pid="";
            List<String> list = getServerPid(serverPath);
            if(null != list && list.size() ==1){
                pid = list.get(0);
            }
            // 并且杀掉进程
            if(StringUtils.isNotEmpty(pid)){
                logger.info("shell命令执行停止操作,guid："+ model.getGuid());
                String stopCmd = "kill -15 "+pid;
                // 执行杀进程
                ShellExcUtil.excShellResult(stopCmd);
                // 验证是否关闭
                boolean result = checkServerStatus(model.getModelFileName());
                if(result){
                    logger.info("模型停止失败，服务进程还在！");
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型停止失败！");
                }
            }else{
                logger.info("服务进程不存在，不执行停止操作，guid"+model.getGuid());
            }
        }
        return ResultUtil.success("模型服务停止成功");
    }

    /**
     * 模型下架
     * @param guid
     */
    @Override
    public Result<String> downShelf(String guid) {
        ModelManage model =this.modelManageRepository.getOne(guid);
        if(null == model){
           return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置信息不存在！");
        }
        // 判断是不是符合下架的条件：启动状态不能直接下架，
        if(ModelManageConstant.ModelStatus.START == model.getStatus()){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"启动状态不能直接下架！");
        }
        // 数据消费模式是周期性，停止任务
        if(ModelManageConstant.DataCustomerModelType.PERIOD.equalsIgnoreCase(model.getDataCustomerModel())){
            quartzFactory.removeJob(ModelManageConstant.modelJobName+model.getGuid(), ModelRunJob.class);
        }
        model.setStatus(ModelManageConstant.ModelStatus.OFFSHELF);
        model.setUpdateTime(new Date());
        model.setUpdateUser(getSessionUserName());
        this.save(model);
        return ResultUtil.success("模型下架成功");
    }



    @Override
    public Result<List<ModelVersionVO>> queryModelVersions(String modelId) {
        return ResultUtil.successList(modelManageDao.queryModelVersions(modelId));
    }

    /**
     * 获取模型配置详情根据guid
     * @param guid
     * @return
     */
    @Override
    public Result<ModelManageVO> getModelManageByGuid(String guid) {
        logger.info("获取模型配置详情根据guid："+ guid);
        ModelManage modelManage = this.getRepository().getOne(guid);
        if(null == modelManage){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置信息不存在！");
        }
        ModelManageVO modelManageVO = mapper.map(modelManage, ModelManageVO.class);
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("modelManageId",guid));
        List<ModelParamConfig> paramConfigs = modelParamConfigService.findAll(conditions);
        if(null != paramConfigs && paramConfigs.size() >0){
            modelManageVO.setParamList(paramConfigs);
        }
        return ResultUtil.success(modelManageVO);
    }

    /**
     * 版本切换
     * 就是切换到对应版本的模型配置信息，将现有的模型配置信息变为不可用，切换的模型配置改为可用
     * @param modelVersionChangeVO
     */
    @Override
    public void changeVersion(ModelVersionChangeVO modelVersionChangeVO) {
        logger.info("版本切换开始："+ JSON.toJSONString(modelVersionChangeVO));
        String oldGuid = modelVersionChangeVO.getOldGuid();
        String newGuid = modelVersionChangeVO.getNewGuid();
        ModelManage oleModel = modelManageRepository.getOne(oldGuid);
        oleModel.setUsed(-1); // 设置不可用
        ModelManage newModel = modelManageRepository.getOne(newGuid);
        newModel.setUsed(0); // 设置可用
        newModel.setStatus(1); //设置为待测试状态
        this.save(oleModel);
        this.save(newModel);
    }


    /**
     * 模型管理分页查询
     *
     * @param search
     * @param pageable
     * @return
     */
    @Override
    public PageRes<ModelManageVO> getModelManagePage(ModelManageSearchVO search , Pageable pageable){
        PageRes<ModelManageVO> pageRes = new PageRes<>();
        pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        // 构造查询条件
        List<QueryCondition> conditions = searchAssetCondition(search);
        // 执行查询
        Page<ModelManage> pager = findAll(conditions, pageable);
        List<ModelManage> content = pager.getContent();
        if(null != content && content.size() > 0){
            List<ModelManageVO> modelManageVOs = mapper.mapList(content,ModelManageVO.class);
            pageRes.setList(modelManageVOs);
            pageRes.setTotal(pager.getTotalElements());
        }else{
            pageRes.setList(new ArrayList<>());
            pageRes.setTotal(0l);
        }
        return pageRes;
    }

    /**
     * 模型具体分析功能执行
     * @return
     */
    @Override
    public Result<String> modelRun(String modelManageId) {
        logger.info("执行具体模型运行分析开始,modelManageId"+modelManageId);
        ModelManage modelManage = this.modelManageRepository.getOne(modelManageId);
        if(null == modelManage){
            logger.info("模型配置信息不存在，guid:"+modelManageId);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型配置信息不存在，guid:"+modelManageId);
        }
        if(StringUtils.isEmpty(modelManage.getModelFilePath())){ //说明没有导入模型，不执行分析接口调用
            return ResultUtil.success("资源导入文件全路径为空，不执行分析功能！");
        }
        String url =modelManage.getModelRunUrl();
        if(StringUtils.isEmpty(url)){
            logger.info("没有配置模型运行接口，guid:"+modelManageId);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有配置模型运行接口，guid:"+modelManageId);
        }
        List<QueryCondition> conditions = new ArrayList<QueryCondition>();
        conditions.add(QueryCondition.eq("modelManageId",modelManageId));
        List<ModelParamConfig> paramConfigs = modelParamConfigService.findAll(conditions);
        List<ModelParamHttpVO> paramHttps = this.mapper.mapList(paramConfigs,ModelParamHttpVO.class);
        HttpResultVO result = restTemplateUtil.post(url,JSON.toJSONString(paramHttps),HttpResultVO.class);
        if(!ModelManageConstant.SUCSESS.equalsIgnoreCase(result.getStatus())){
            logger.error("模型执行分析运行失败:"+result.getMsg());
            updateModelManage("模型执行分析运行失败:"+result.getMsg(),-1,modelManage);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"模型执行分析运行时失败:"+result.getMsg());
        }
        logger.info("模型执行分析运行成功,modelManageId"+modelManageId);
        updateModelManage("模型执行分析运行成功",-1,modelManage);
       return ResultUtil.success("运行成功！");
    }


    /**
     * 构造查询条件
     * @param search
     * @return
     */
    private List<QueryCondition> searchAssetCondition(ModelManageSearchVO search){
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("used", 0)); // 可用配置信息
        conditions.add(QueryCondition.eq("isDelete", 0)); // 非删除的配置信息
        // 模型名称
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(search.getModelName())) {
             conditions.add(QueryCondition.like("modelName", search.getModelName().trim()));
        }
        // 标签
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(search.getLabel())) {
            conditions.add(QueryCondition.eq("label", search.getLabel()));
        }
        // 版本号
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(search.getVersion())) {

            conditions.add(QueryCondition.eq("version", search.getVersion()));
        }
        // 创建时间
        if (null !=search.getCreateTimeStart()&&null!=search.getCreateTimeEnd()) {

            conditions.add(QueryCondition.between("createTime",search.getCreateTimeStart(),search.getCreateTimeEnd()));
        }
        // 创建人
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(search.getCreateUser())) {

            conditions.add(QueryCondition.eq("createUser", search.getCreateUser()));
        }
        // 模型转态构造查询条件
        modelStatusSearchCondition(conditions,search);
        return conditions;
    }

    /**
     * 模型转态构造查询条件
     * @param conditions
     * @param search
     */
    private void modelStatusSearchCondition(List<QueryCondition> conditions, ModelManageSearchVO search) {
        String type = search.getType();   // 1:模型配置管理页面，2：已发布模型管理页面，all：所有
        // 状态 :all(全部)、1(待测试)、2(已测试)、启动中(4)、停用中(5)、6(已下架)
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(search.getModelStatus())) {
            switch(type){
                case ModelManageConstant.QueryType.ONE:
                    if(ModelManageConstant.QueryType.ALL.equalsIgnoreCase(search.getModelStatus())){
                        conditions.add(QueryCondition.in("status",Arrays.asList(new Integer[]{1,2,6})));
                    }else{
                        conditions.add(QueryCondition.eq("status",search.getModelStatus()));
                    }
                    break;
                case ModelManageConstant.QueryType.TWO:
                    if(ModelManageConstant.QueryType.ALL.equalsIgnoreCase(search.getModelStatus())){
                        conditions.add(QueryCondition.in("status",Arrays.asList(new Integer[]{3,4,5})));
                    }else if("5".equalsIgnoreCase(search.getModelStatus())){ //如果查询时停用中，展示发布中、停用中两种状态数据 20220419
                        conditions.add(QueryCondition.in("status",Arrays.asList(new Integer[]{3,5})));
                    }else{
                        conditions.add(QueryCondition.eq("status",search.getModelStatus()));
                    }
                    break;
            }
        }else{ // 没有选择状态：默认全部  type为：模型配置管理页面：展示1(待测试)、2(已测试)、6(已下架) ；已发布模型管理页面：展示启动中(4)、停用中(5)  ；all表示所有状态
            switch(type){
                case ModelManageConstant.QueryType.ONE:
                    conditions.add(QueryCondition.in("status",Arrays.asList(new Integer[]{1,2,6})));
                    break;
                case ModelManageConstant.QueryType.TWO:
                    conditions.add(QueryCondition.in("status",Arrays.asList(new Integer[]{3,4,5})));
                    break;
            }
        }
    }


    private String getSessionUserName(){

        return SessionUtil.getCurrentUser().getName();
    }

    /**
     * 临时模型复制到运行时模型文件夹下
     * @param modelTempPath
     * @param runModelPath
     * @return
     * @throws IOException
     */
    private String tempModelCopyRunModel(String modelTempPath,String runModelPath) throws IOException {
        if(StringUtils.isEmpty(modelTempPath)){
            logger.info("模型临时存放路径为空");
        }
        File old= new File(modelTempPath);
        String fileName= old.getName();
        File newFile= new File(runModelPath);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        String path = Paths.get(runModelPath, fileName).toString();
        logger.info("复制后路径："+path);
        newFile= new File(path);
        FileUtils.copyFile(old,newFile);
        // 将url中\替换成/,解决模型启动根据路径启动不了
        path= path.replace("\\","/");
        return path;

    }


}

