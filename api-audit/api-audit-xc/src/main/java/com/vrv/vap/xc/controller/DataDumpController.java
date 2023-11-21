package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.fegin.ApiDataClient;
import com.vrv.vap.xc.model.DeleteModel;
import com.vrv.vap.xc.model.Source;
import com.vrv.vap.xc.pojo.DataCleanLog;
import com.vrv.vap.xc.pojo.DataDumpLog;
import com.vrv.vap.xc.pojo.DataDumpStrategy;
import com.vrv.vap.xc.service.DataDumpService;
import com.vrv.vap.xc.vo.DataCleanLogQuery;
import com.vrv.vap.xc.vo.DataDumpLogQuery;
import com.vrv.vap.xc.vo.DataDumpStrategyQuery;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 数据备份策略管理
 * Created by lizj on 2021/1/5
 */
@RestController
@RequestMapping("data/dump")
public class DataDumpController {
    private Log log = LogFactory.getLog(DataDumpController.class);

    @Autowired
    private DataDumpService dataDumpService;

    @Autowired
    private ApiDataClient apiDataClient;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    /**
     * 以免影响到搜索与探索的es的索引，新增一个接口，在加入监管事件的es索引。
     * @return listVData
     */
    @PostMapping("/source")
    @ApiOperation("查询数据备份策略列表")
    public  VData<List<Source>> queryAllSource() {
        VData<List<Source>> listVData = apiDataClient.queryAllSource();
        // 新增监管事件备份，alarmeventmanagement 索引
        Source source = new Source();
        source.setId(0);
        source.setName("alarmeventmanagement");
        source.setTitle("监管事件");
        source.setType(1);
        source.setTimeField("event_time");
        source.setTimeFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        source.setDataType(1);
        listVData.getData().add(source);
        return listVData;
    }

    @PostMapping("/strategy")
    @ApiOperation("查询数据备份策略列表")
    public VList<DataDumpStrategy> selectStrategyList(@RequestBody DataDumpStrategyQuery record) {
        VList<DataDumpStrategy> list = null;
        try {
            list = dataDumpService.selectStrategyListByPage(record);
        } catch (NumberFormatException e) {
            log.error("", e);
            list = new VList<>(0, Collections.emptyList());
        }
        return list;
    }

    @PostMapping("/exist/data/list")
    @ApiOperation("查询已存在策略的数据列表")
    public VData<List<String>> selectExistDataList(@RequestBody DataDumpStrategy record) {
        List<String> list = new ArrayList<>();
        try {
            list = dataDumpService.selectExistDataList(record);
        } catch (NumberFormatException e) {
            log.error("", e);
        }
        return VoBuilder.vd(list);
    }

    @DeleteMapping("/strategy")
    @ApiOperation("删除数据备份策略")
    public Result deleteStrategy(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            dataDumpService.deleteStrategyById(record);
        } catch (NumberFormatException e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @PutMapping("/strategy")
    @ApiOperation("新增数据备份策略")
    public Result addStrategy(@RequestBody DataDumpStrategy record) {
        try {
            dataDumpService.addStrategy(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.result(RetMsgEnum.FAIL);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PatchMapping("/strategy")
    @ApiOperation("修改数据备份策略")
    public Result updateStrategy(@RequestBody DataDumpStrategy record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            dataDumpService.updateStrategyByKey(record);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @PostMapping("/log")
    @ApiOperation("查询数据备份记录")
    public VList<DataDumpLog> selectDumpList(@RequestBody DataDumpLogQuery record) {
        VList<DataDumpLog> list = null;
        try {
            list = dataDumpService.selectDumpListByPage(record);
        } catch (NumberFormatException e) {
            log.error("", e);
            list = new VList<>(0, Collections.emptyList());
        }
        return list;
    }

    @PutMapping("/log")
    @ApiOperation("新增数据备份记录")
    public Result addDumpLog(@RequestBody DataDumpLog record) {
        try {
            dataDumpService.addDataDumpLog(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.result(RetMsgEnum.FAIL);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PostMapping("/clean/log")
    @ApiOperation("查询数据清理记录")
    public VList<DataCleanLog> selectCleanList(@RequestBody DataCleanLogQuery record) {
        VList<DataCleanLog> list = null;
        try {
            list = dataDumpService.selectCleanListByPage(record);
        } catch (NumberFormatException e) {
            log.error("", e);
            list = new VList<>(0, Collections.emptyList());
        }
        return list;
    }

    @PutMapping("/clean/log")
    @ApiOperation("新增数据清理记录")
    public Result addCleanLog(@RequestBody DataCleanLog record) {
        try {
            dataDumpService.addDataCleanLog(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.result(RetMsgEnum.FAIL);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PatchMapping("/log")
    @ApiOperation("修改数据备份记录")
    public Result updateDumpLog(@RequestBody DataDumpLog record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            dataDumpService.updateDumpLogByKey(record);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @GetMapping("/download/{id}")
    @ApiOperation("根据备份记录编号下载备份文件")
    public void downloadById(@PathVariable("id") String id, HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");
        // 获取文件名
        DataDumpLogQuery param = new DataDumpLogQuery();
        param.setId(Integer.parseInt(id));
        List<DataDumpLog> logList = dataDumpService.selectDumpListByPage(param).getList();
        if (logList != null && logList.size() > 0) {
            DataDumpLog dumpLog = logList.get(0);
            String filePath = dumpLog.getDumpFilePath();
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            // 兼容火狐浏览器导出文件名乱码问题
            String downloadName = fileName;
            String agent = req.getHeader("USER-AGENT");
            try {
                if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
                    downloadName = "=?UTF-8?B?" + (new String(Base64Utils.encodeToString(downloadName.getBytes("UTF-8")))) + "?=";
                } else {
                    downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            resp.setHeader("Content-Disposition", "attachment;filename=" + downloadName);
            OutputStream out = null;
            InputStream in = null;
            try {
                out = resp.getOutputStream();
                in = FileUtils.openInputStream(new File(filePath));
                IOUtils.copy(in, out);
            } catch (IOException e) {
                log.error("", e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }

    @PostMapping("/upload")
    @ResponseBody
    @ApiOperation("根据备份记录编号上传备份文件")
    public Result uploadDump(HttpServletRequest request,
                         @RequestParam(value = "id", required = true) String id) {
        // 获取文件存放路径
        DataDumpLogQuery param = new DataDumpLogQuery();
        param.setId(Integer.parseInt(id));
        List<DataDumpLog> logList = dataDumpService.selectDumpListByPage(param).getList();
        if (logList != null && logList.size() > 0) {
            DataDumpLog dumpLog = logList.get(0);
            InputStream in = null;
            try {
                AbstractMultipartHttpServletRequest multipartHttpServletRequest = (AbstractMultipartHttpServletRequest)request;
                Map<String,MultipartFile> fileParamMap = multipartHttpServletRequest.getFileMap();
                MultipartFile multiFile = fileParamMap.get("file");
                File file = new File(dumpLog.getDumpFilePath());
                in = multiFile.getInputStream();
                FileUtils.copyInputStreamToFile(in, file);
                // 计算并对比文件md5
                if (!dumpLog.getDumpFileMd5().equals(CommonTools.calcMD5(file))) {
                    // 文件不匹配，删除
                    file.delete();
                    return VoBuilder.result(RetMsgEnum.ERROR_NO_MATCH);
                } else {
                    // 文件上传成功，修改状态
                    if (file.exists()) {
                        dumpLog.setDumpFileState(1);
                        updateDumpLog(dumpLog);
                    }
                    return VoBuilder.success();
                }
            } catch (IOException e) {
                log.error("", e);
            }finally {
                try{
                    if(in != null){
                        in.close();
                    }
                }catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            }
        }
        return VoBuilder.fail();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation("根据备份记录编号删除备份文件（批量删除id逗号分隔）")
    public Result deleteDump(@PathVariable("ids") String ids) {
        String[] idList = ids.split(",");
        if (idList.length > 0) {
            for (String id : idList) {
                // 获取文件名
                DataDumpLogQuery param = new DataDumpLogQuery();
                param.setId(Integer.parseInt(id));
                List<DataDumpLog> logList = dataDumpService.selectDumpListByPage(param).getList();
                if (logList != null && logList.size() > 0) {
                    DataDumpLog dumpLog = logList.get(0);
                    String filePath = dumpLog.getDumpFilePath();
                    try {
                        File file = new File(filePath);
                        file.delete();
                        // 文件删除成功，修改状态
                        if (!file.exists()) {
                            dumpLog.setDumpFileState(0);
                            updateDumpLog(dumpLog);
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }
        return VoBuilder.success();
    }
}
