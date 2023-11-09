package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.service.*;
import com.vrv.vap.alarmdeal.business.appsys.util.EnumTransferUtil;
import com.vrv.vap.alarmdeal.business.appsys.util.MapTypeAdapter;
import com.vrv.vap.alarmdeal.business.appsys.vo.*;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.vo.ExcelValidationData;
import com.vrv.vap.alarmdeal.business.asset.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.common.FileHeaderUtil;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author lps 2021/8/12
 */

public abstract class AbstractAppSysController<T,ID extends Serializable> {

    private static Logger logger= LoggerFactory.getLogger(AbstractAppSysController.class);

    public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    public static final Pattern  DOMAIN_PATTERN=Pattern.compile("^((http|ftp|https):\\/\\/)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(\\/[a-zA-Z0-9\\&#%_\\.\\/-~-]*)*$");

    protected static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new MapTypeAdapter()).create();

    private static String APP_ID="appId";


    private  Map<String,String> deptMap=new HashMap<>();
    private  Map<String,String> personMap=new HashMap<>();


    @Autowired
    private FileConfiguration fileConfiguration;



    @Autowired
    private AppTemplateInitDataService appTemplateInitDataService;


    protected abstract AbstractBaseService<T, ID> getService();


    /**
     * excel表头
     * @return
     */
    protected abstract List<String> exportExcelHeaders();

    /**
     * excel每列对应的值
     * @return
     */
    protected abstract String[] getKeys();

    /**
     * excel中文sheet
     * @return
     */
    protected abstract String getSheetName();

    /**
     * 获取防护等级
     * @return
     */
    protected abstract List<BaseDictAll> getProtectLevelAll();

    /**
     * 涉密等级
     * @return
     */
    protected abstract List<BaseDictAll> getSecretLevelAll();
    /**
     * 获取所用防护等级value值
     * @return
     */
    protected abstract  String[] getProtectLevelAllCodeValue();



    /**
     * 获取所用涉密等级value值
     * @return
     */
    protected abstract String[] getSecretLevelAllCodeValue();

    /**
     * 获取所有安全域
     * @return
     */
    protected abstract List<String> getBaseSecurityDomain();





    @GetMapping("/exportTemplate/{type}")
    @ApiOperation(value="应用系统相关导入模板",notes="")
    @SysRequestLog(description="应用系统相关导入模板", actionType = ActionType.IMPORT,manually=false)
    public Result<String> exportTemplate(@PathVariable("type") String type){
        //创建空的excel文件
        String sheetName=getSheetName();
        String fileName=sheetName+"导入模板.xls";
        //String filePath = "D:\\"+fileName;
        //模板关联应用时,type为应用id；否则为空
        if(StringUtils.isBlank(type) ||"null".equals(type)){
            type=sheetName;
        }
        String filePath= fileConfiguration.getFilePath()+ File.separator+fileName;
        OutputStream out = null;
        HSSFWorkbook workbook = null;
        List<List<String>> list = null;
        try {
            out = new FileOutputStream(filePath);
            // 生成Excel
            workbook = new HSSFWorkbook();
            ExportExcelUtils eeu = new ExportExcelUtils();
            List<String> headColumns= exportExcelHeaders();
            Map<String,List<List<String>>> templates = appTemplateInitDataService.getInitDataByType(sheetName);  // 获取导入模板预置数据
            if(sheetName.equals(AppSysManagerVo.APP_SYS_MANAGER)){
                exportExcelByEntities(workbook,eeu, AppSysManagerVo.APP_SYS_MANAGER,AppSysManagerVo.HEADERS,templates.get("main"));
                exportExcelByEntities(workbook,eeu, AppRoleManageVo.APP_ROLE_MANAGE,AppRoleManageVo.HEADERS,templates.get("role"));
                exportExcelByEntities(workbook,eeu, AppAccountManageVo.APP_ACCOUNT_MANAGE,AppAccountManageVo.HEADERS,templates.get("account"));
                exportExcelByEntities(workbook,eeu, AppResourceManageVo.APP_RESOURCE_MANAGE,AppResourceManageVo.HEADERS,templates.get("resouces"));
            }else{
                exportExcelByEntities(workbook,eeu,type,headColumns,templates.get("main"));
            }
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(workbook != null){
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ResultUtil.success(fileName);
    }


    @GetMapping("getList/{appId}")
    public Result<List<T>> getList(@PathVariable("appId") Integer appId){
        List<QueryCondition> conditions=new ArrayList<>();
        conditions.add(QueryCondition.eq("appId",appId));
        List<T> list=this.getService().findAll(conditions);
        return ResultUtil.success(list);
    }

    @GetMapping("countByAppId/{appId}")
    public Result<Long> countByAppId(@PathVariable("appId") Integer appId){
        List<QueryCondition> conditions=new ArrayList<>();
        conditions.add(QueryCondition.eq("appId",appId));
        Long size=this.getService().count(conditions);
        return ResultUtil.success(size);
    }

     public void mapEnumTransfer(Map<String, Object> map,List<BaseDictAll> secretLevels, List<BaseDictAll> protectLevels) {
        for(Map.Entry<String,Object> entry : map.entrySet()){
            String key= FileHeaderUtil.checkFileHeader(entry.getKey());
            Object object=FileHeaderUtil.checkFileHeader(String.valueOf(entry.getValue()));
            if(object!=null){
                String val= EnumTransferUtil.nameTransfer(key,object,secretLevels,protectLevels) ;
                map.put(key,val);
            }
        }
    }

    /**
     * 下载文件
     * @param response
     */
    @GetMapping(value="/exportFile/{fileName}")
    @ApiOperation(value="应用系统相关下载导出文件",notes="")
    @SysRequestLog(description="应用系统相关下载导出文件", actionType = ActionType.EXPORT,manually=false)
    public void exportFile(@PathVariable  String fileName, HttpServletResponse response){
        // 文件路径
        String realPath = fileConfiguration.getFilePath();
        FileUtil.downLoadFile(fileName+".xls", realPath, response);
    }

    /**
     * 校验字段
     * @param key(key-需要校验的字段，value-对应的值)
     * @return
     */
    @GetMapping("/checkParam/{key}/{value}")
    @ApiOperation(value="校验字段",notes="")
    public Result<Boolean> checkAppRoleName(@PathVariable("key") String key, @PathVariable("value") String value ){
       Boolean bool=this.getService().checkParam(key,value);
        return ResultUtil.success(bool);
    }

    /**
     * 校验字段；限定单个应该
     * @param key(key-需要校验的字段，value-对应的值)
     * @return
     */
    @GetMapping("/checkParam/{key}/{value}/{appId}")
    @ApiOperation(value="校验字段",notes="")
    public Result<Boolean> checkAppRoleName(@PathVariable("key") String key, @PathVariable("value") String value , @PathVariable("appId") Integer appId){
        Boolean bool=this.getService().checkParam(key,value,appId);
        return ResultUtil.success(bool);
    }

    /**
     *
     * @param workbook
     * @param eeu
     * @param sheetName
     * @param columns-excel内容
     */
    public void exportExcelByEntities(HSSFWorkbook workbook, ExportExcelUtils eeu, String sheetName, List<String> headColumns, List<List<String>> columns) {
        String[] strArray = new String[headColumns.size()];
        headColumns.toArray(strArray);
        List<ExcelValidationData> validationDatas = setColChooseList(strArray,sheetName);
        HSSFRichTextString helpDocumentsRich=new HSSFRichTextString();
        //设置第一行大写
        helpDocumentsRich.applyFont(eeu.getDefaultBoldFont(workbook));
        try {
            eeu.exportExcel(workbook, sheetName, headColumns.toArray(new String[headColumns.size()]) , columns, validationDatas,helpDocumentsRich);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * excel 需要下拉选择的列数据
     * @param headers
     * @param exportType
     * @return
     */
    public   List<ExcelValidationData> setColChooseList(String[] headers, String exportType) {
        List<ExcelValidationData> validationDatas=new ArrayList<>();
        int index=0;
        String[]  secretLevelAllCodeValues = getSecretLevelAllCodeValue();
        String[]  protectLevelValues = getProtectLevelAllCodeValue();
        List<String> domainNames  = getBaseSecurityDomain();
        for(String colName : headers){
              ExcelValidationData excelValidationData = getService().getExcelValidationData(exportType, index, colName,getSheetName(),secretLevelAllCodeValues,protectLevelValues,domainNames);
            if (excelValidationData != null) {
                validationDatas.add(excelValidationData);
            }else{
                logger.info("32");
            }
            index++;
        }
        return validationDatas;
    }



    public String constructExcelData(List<Map<String, Object>> mapList,String[] keys,String sheetName) {
        //excels数据筛选
        List<List<String>> result=mapListEnumTransfer(mapList, keys);
        //创建空的excel文件
        String fileName=sheetName+".xls";
        //String filePath = "D:\\"+fileName;
        String filePath= fileConfiguration.getFilePath()+ File.separator+fileName;
        OutputStream out = null;
        HSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(filePath);
            // 生成Excel
            workbook = new HSSFWorkbook();
            ExportExcelUtils eeu = new ExportExcelUtils();
            List<String> headColumns= exportExcelHeaders();
            exportExcelByEntities(workbook,eeu,sheetName,headColumns,result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(workbook!=null){
                workbook.write(out);
            }
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }finally {
            if(workbook != null){
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileName;
    }

    /**
     *  excels数据筛选
     * @param mapList
     * @param keys
     * @return
     */
    public List<List<String>> mapListEnumTransfer(List<Map<String, Object>> mapList, String[] keys) {
        List<List<String>> result=new ArrayList<>();
        List<BaseDictAll> protectLevels = getProtectLevelAll();
        List<BaseDictAll> sercretLevels =getSecretLevelAll();
        for(Map<String,Object> map :mapList){
            List<String> columnList=new ArrayList<>();
             for(String key : keys){
                Object object=map.get(key);
                if(object!=null){
                    String value= EnumTransferUtil.numTransfer(key, object,sercretLevels,protectLevels);
                    columnList.add(value);
                }else{
                    columnList.add("");
                }
            }
            result.add(columnList);
        }
        return result;
    }

    private Class<T> getTClass() {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }







}
