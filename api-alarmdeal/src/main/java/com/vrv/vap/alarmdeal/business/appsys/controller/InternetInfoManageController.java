package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.model.InternetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.InternetInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.ProtectionLevelService;
import com.vrv.vap.alarmdeal.business.appsys.vo.InternetInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.baseauth.util.PValidUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

@Api(description = "互联网信息管理")
@RequestMapping("/internetInfoManage")
@RestController
public class InternetInfoManageController extends AbstractAppSysController<InternetInfoManage,Integer>  {

    private static Logger logger= LoggerFactory.getLogger(InternetInfoManageController.class);


    @Autowired
    private InternetInfoManageService internetInfoManageService;

    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private MessageService messageService;

    @Autowired
    private ProtectionLevelService protectionLevelService;

    @Autowired
    private ClassifiedLevelService classifiedLevelService;

    @Override
    public AbstractBaseService<InternetInfoManage,Integer> getService(){
        return internetInfoManageService;
    }


    @Override
    protected List<String> exportExcelHeaders() {
        return InternetInfoManageVo.HEADERS;
    }

    @Override
    protected String[] getKeys() {
        return InternetInfoManageVo.KEYS;
    }

    @Override
    protected String getSheetName(){
        return InternetInfoManageVo.INTERNET_INFO_MANAGE;
    }


    @Override
    protected List<BaseDictAll> getProtectLevelAll() {
        return protectionLevelService.getInternetAll();
    }

    @Override
    protected List<BaseDictAll> getSecretLevelAll() {
        return classifiedLevelService.getInternetAll();
    }

    @Override
    protected String[] getProtectLevelAllCodeValue() {
        return protectionLevelService.getInternetAllValues();
    }

    @Override
    protected String[] getSecretLevelAllCodeValue() {
        return classifiedLevelService.getInternetSecretLevelAllValues();
    }

    @Override
    protected List<String> getBaseSecurityDomain() {
        return new ArrayList<>();
    }


    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    /**
     * 互联网信息管理 分页查询
     * @param internetInfoManageVo
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="分页查询",notes="")
    @SysRequestLog(description="互联网信息-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<InternetInfoManage> getInternetInfoManagePage(@RequestBody InternetInfoManageVo internetInfoManageVo){
        return internetInfoManageService.getInternetInfoManagePage(internetInfoManageVo);
    }

    /**
     * 新增互联网信息
     * @param internetInfoManageVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增互联网信息",notes="")
    @SysRequestLog(description="互联网信息-新增互联网信息", actionType = ActionType.ADD,manually=false)
    public Result<InternetInfoManage> addInternetInfoManage(@RequestBody InternetInfoManageVo internetInfoManageVo){
        InternetInfoManage internetInfoManage=mapperUtil.map(internetInfoManageVo,InternetInfoManage.class);
        internetInfoManage.setCreateTime(new Date());
        if (StringUtils.isNotBlank(internetInfoManageVo.getIp())){
            if (!PValidUtil.isIPValid(internetInfoManageVo.getIp())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "互联边界ip格式异常");
            }
            if (PValidUtil.hasDuplicate(internetInfoManageVo.getIp())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "互联边界ip存在重复ip");
            }
        }
        internetInfoManageService.save(internetInfoManage);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("internet");
        return ResultUtil.success(internetInfoManage);
    }

    /**
     * 编辑互联网信息
     * @param internetInfoManageVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑互联网信息",notes="")
    @SysRequestLog(description="互联网信息-编辑互联网信息", actionType = ActionType.UPDATE,manually=false)
    public Result<InternetInfoManage> editRoleManage(@RequestBody InternetInfoManageVo internetInfoManageVo){
        InternetInfoManage internetInfoManage=internetInfoManageService.getOne(internetInfoManageVo.getId());
        internetInfoManage=mapperUtil.map(internetInfoManageVo,internetInfoManage.getClass());
        if (StringUtils.isNotBlank(internetInfoManageVo.getIp())){
            if (!PValidUtil.isIPValid(internetInfoManageVo.getIp())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "互联边界ip格式异常");
            }
            if (PValidUtil.hasDuplicate(internetInfoManageVo.getIp())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "互联边界ip存在重复ip");
            }
        }
        internetInfoManageService.save(internetInfoManage);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("internet");
        return ResultUtil.success(internetInfoManage);
    }


    @PostMapping(value="/checkImportData")
    @ApiOperation(value="互联网信息管理数据导入校验",notes="")
    @SysRequestLog(description="互联网信息-数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file){
        Map<String, List<Map<String, Object>>> map = internetInfoManageService.checkImportData(file);
        if(map==null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "导入文件解析异常");
        }
        Result<Map<String, List<Map<String, Object>>>> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        result.setData(map);
        return result;
    }
    @PostMapping(value="/saveList")
    @ApiOperation(value="互联网信息管理导入数据入库",notes="")
    @SysRequestLog(description="互联网信息-导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody List<Map<String,Object>> list){
        List<InternetInfoManage> datas = new ArrayList<>();
        for(Map<String,Object> map : list){
            InternetInfoManage internetInfoManage=mapperUtil.map(map,InternetInfoManage.class);
            internetInfoManage.setCreateTime(new Date());
            datas.add(internetInfoManage);
        }
        internetInfoManageService.save(datas);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("internet");
        return ResultUtil.success(true);
    }
    /**
     * 删除互联网信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value="删除互联网信息",notes="")
    @SysRequestLog(description="互联网信息-删除互联网信息", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("id") String id){
        internetInfoManageService.delete(Integer.valueOf(id));
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("internet");
        return ResultUtil.success(true);
    }

    /**
     *
     * @param internetInfoManageVo
     * @return
     */
    @PostMapping("/exportDataExcel")
    @ApiOperation(value="互联网信息管理数据导出",notes="")
    @SysRequestLog(description="互联网信息-数据导出", actionType = ActionType.EXPORT,manually=false)
    public Result<String> exportDataExcel(@RequestBody InternetInfoManageVo internetInfoManageVo){
        internetInfoManageVo.setCount_(100000);
        PageRes<InternetInfoManage> appSysManagerVoPageRes=internetInfoManageService.getInternetInfoManagePage(internetInfoManageVo);
        List<InternetInfoManage> internetInfoManageList=appSysManagerVoPageRes.getList();
        List<Map<String,Object>> mapList=new Gson().fromJson(gson.toJson(internetInfoManageList),new TypeToken<List<Map<String,Object>>>(){}.getType());
        String fileName = constructExcelData(mapList,InternetInfoManageVo.KEYS,InternetInfoManageVo.INTERNET_INFO_MANAGE);
        return ResultUtil.success(fileName);
    }


    @GetMapping("countInternetInfo")
    @ApiOperation(value="互联网信息-数量统计",notes="")
    @SysRequestLog(description="互联网信息-数量统计", actionType = ActionType.SELECT,manually=false)
    public Result<Long> countInternetInfo(){
        Long appCount=internetInfoManageService.count();
        return ResultUtil.success(appCount);
    }

    @GetMapping("getInternetInfo")
    @ApiOperation(value="获取互联网信息",notes="")
    @SysRequestLog(description="获取互联网信息", actionType = ActionType.SELECT,manually=false)
    public Result<List<InternetInfoManage>> getInternetInfo(){
        List<InternetInfoManage> all = internetInfoManageService.findAll();
        return ResultUtil.successList(all);
    }


}
