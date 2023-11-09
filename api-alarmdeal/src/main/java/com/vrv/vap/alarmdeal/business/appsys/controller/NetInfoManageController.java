package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.model.NetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.NetInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.ProtectionLevelService;
import com.vrv.vap.alarmdeal.business.appsys.vo.NetInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
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
@Api(description = "网络信息管理")
@RestController
@RequestMapping("/netInfoManage")
public class NetInfoManageController extends AbstractAppSysController<NetInfoManage,Integer>  {

    private static Logger logger= LoggerFactory.getLogger(NetInfoManageController.class);

    @Autowired
    private NetInfoManageService netInfoManageService;

    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ProtectionLevelService protectionLevelService;
    @Autowired
    private ClassifiedLevelService classifiedLevelService;
    @Autowired
    private AssetBaseDataService assetBaseDataService;

    @Override
    public AbstractBaseService<NetInfoManage,Integer> getService(){
        return netInfoManageService;
    }


    @Override
    protected List<String> exportExcelHeaders() {
        return NetInfoManageVo.HEADERS;
    }

    @Override
    protected String[] getKeys() {
        return NetInfoManageVo.KEYS;
    }

    @Override
    protected String getSheetName(){
        return NetInfoManageVo.NET_INFO_MANAGE;
    }

    @Override
    protected List<BaseDictAll> getProtectLevelAll() {
        return protectionLevelService.getNtworkAll();
    }

    @Override
    protected List<BaseDictAll> getSecretLevelAll() {
        return classifiedLevelService.getNetWorkAll();
    }

    @Override
    protected String[] getProtectLevelAllCodeValue() {
        return protectionLevelService.getNtworkAllValues();
    }

    @Override
    protected String[] getSecretLevelAllCodeValue() {
        return classifiedLevelService.getNetWorkSecretLevelAllValues();
    }

    @Override
    protected List<String> getBaseSecurityDomain() {
        List<String> domainNames = new ArrayList<>();
        List<BaseSecurityDomain> allDomains = assetBaseDataService.queryAllDomain();
        allDomains.forEach(a->{
            domainNames.add(a.getDomainName());
        });
        return domainNames;
    }


    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    /**
     * 网络信息管理 分页查询
     * @param netInfoManageVo
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="分页查询",notes="")
    @SysRequestLog(description="网络信息-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<NetInfoManage> getNetInfoManagePage(@RequestBody NetInfoManageVo netInfoManageVo){
        return netInfoManageService.getNetInfoManagePage(netInfoManageVo);
    }

    /**
     * 新增互联网信息
     * @param netInfoManageVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增网络信息",notes="")
    @SysRequestLog(description="网络信息-新增网络信息", actionType = ActionType.ADD,manually=false)
    public Result<NetInfoManage> addNetInfoManage(@RequestBody NetInfoManageVo netInfoManageVo){
        NetInfoManage netInfoManage=mapperUtil.map(netInfoManageVo,NetInfoManage.class);
        netInfoManage.setCreateTime(new Date());
        netInfoManageService.save(netInfoManage);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("net");
        return ResultUtil.success(netInfoManage);
    }

    /**
     * 编辑互联网信息
     * @param netInfoManageVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑网络信息",notes="")
    @SysRequestLog(description="网络信息-编辑网络信息", actionType = ActionType.UPDATE,manually=false)
    public Result<NetInfoManage> editRoleManage(@RequestBody NetInfoManageVo netInfoManageVo){
        NetInfoManage netInfoManage=mapperUtil.map(netInfoManageVo,NetInfoManage.class);
        netInfoManageService.save(netInfoManage);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("net");
        return ResultUtil.success(netInfoManage);
    }

    /**
     * 数据导入校验
     * @param file
     * @return
     */
    @PostMapping(value="/checkImportData")
    @ApiOperation(value="网络信息-数据导入校验",notes="")
    @SysRequestLog(description="网络信息管理数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file){
        Map<String, List<Map<String, Object>>> map = netInfoManageService.checkImportData(file);
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
    @ApiOperation(value="网络信息管理导入数据入库",notes="")
    @SysRequestLog(description="网络信息-导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody List<Map<String,Object>> list){
        List<NetInfoManage> datas = new ArrayList<>();
        for(Map<String,Object> map : list){
            NetInfoManage netInfoManage=mapperUtil.map(map,NetInfoManage.class);
            netInfoManage.setCreateTime(new Date());
            datas.add(netInfoManage);
        }
        netInfoManageService.save(datas);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("net");
        return ResultUtil.success(true);
    }

    /**
     * 删除互联网信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value="删除网络信息",notes="")
    @SysRequestLog(description="网络信息-删除网络信息", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("id") String id){
        netInfoManageService.delete(Integer.valueOf(id));
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("net");
        return ResultUtil.success(true);
    }

    /**
     *
     * @param netInfoManageVo
     * @return
     */
    @PostMapping("/exportDataExcel")
    @ApiOperation(value="网络信息管理数据导出",notes="")
    @SysRequestLog(description="网络信息-数据导出", actionType = ActionType.EXPORT,manually=false)
    public Result<String> exportDataExcel(@RequestBody NetInfoManageVo netInfoManageVo){
        netInfoManageVo.setCount_(100000);
        PageRes<NetInfoManage> netInfoManagePageRes=netInfoManageService.getNetInfoManagePage(netInfoManageVo);
        List<NetInfoManage> netInfoManageList=netInfoManagePageRes.getList();
        List<Map<String,Object>> mapList=new Gson().fromJson(gson.toJson(netInfoManageList),new TypeToken<List<Map<String,Object>>>(){}.getType());
        String fileName = constructExcelData(mapList,NetInfoManageVo.KEYS,NetInfoManageVo.NET_INFO_MANAGE);
        return ResultUtil.success(fileName);
    }




}
