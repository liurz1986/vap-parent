package com.vrv.vap.xc.controller;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.DeleteModel;
import com.vrv.vap.xc.model.LineExportModel;
import com.vrv.vap.xc.pojo.BaseLine;
import com.vrv.vap.xc.pojo.BaseLineResult;
import com.vrv.vap.xc.pojo.BaseLineSpecial;
import com.vrv.vap.xc.service.BaseLineService;
import com.vrv.vap.xc.service.BaseLineSpecialService;
import com.vrv.vap.xc.service.CommonService;
import com.vrv.vap.xc.vo.BaseLineQuery;
import com.vrv.vap.xc.vo.BaseResultQuery;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("base_line")
public class BaseLineController {
    private Log log = LogFactory.getLog(BaseLineController.class);

    @Autowired
    private BaseLineService baseLineService;
    @Autowired
    private BaseLineSpecialService baseLineSpecialService;
    @Autowired
    private CommonService commonService;

    @ResponseBody
    @PutMapping
    @ApiOperation("新增基线")
    public VData<BaseLine> add(@RequestBody BaseLine line) {
        return baseLineService.add(line);
    }

    @ResponseBody
    @PatchMapping
    @ApiOperation("修改基线")
    public VData<BaseLine> update(@RequestBody BaseLine line) {
        return baseLineService.update(line);
    }

    @ResponseBody
    @PostMapping
    @ApiOperation("查询基线")
    public VList<BaseLine> findAllByQuery(@RequestBody BaseLineQuery query) {
        return baseLineService.findAll(query);
    }

    @ResponseBody
    @GetMapping
    @ApiOperation("查询基线")
    public VList<BaseLine> findAll() {
        return baseLineService.findAll(new BaseLineQuery());
    }

    @ResponseBody
    @DeleteMapping
    @ApiOperation("删除基线")
    public Result delete(@RequestBody DeleteModel model) {
        return baseLineService.delete(model.getIds());
    }

    @GetMapping("/export/{ids}")
    @ApiOperation("基线配置导出")
    public void export(@PathVariable("ids") String ids, HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(ids)){
            log.error("报表导出reportIds为空");
            return;
        }
        String reg = "^[,0-9]+$";
        if(!ids.matches(reg)){
            log.info("ids：" + ids + " 参数错误！");
            return;
        }
        LineExportModel lineExportModel = baseLineService.exportConfigs(ids);
        File file = File.createTempFile(UUID.randomUUID().toString().replaceAll("-",""),".json");
        try (FileOutputStream fos = new FileOutputStream(file);
             Writer write = new OutputStreamWriter(fos, "UTF-8")) {
            write.write(JSONObject.toJSONString(lineExportModel));
            write.flush();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        //String name = StringUtils.isNotEmpty(report.getName()) ? URLEncoder.encode(report.getName())+".json" : file.getName();
        file.setReadOnly();
        String name = file.getName();
        try (FileInputStream fileIn = new FileInputStream(file);
             ServletOutputStream out = response.getOutputStream();) {
            //String fileName = new String(fileNameString.getBytes("ISO8859-1"), "UTF-8");
            response.setContentType("text/html");
            // URLEncoder.encode(fileNameString, "UTF-8") 下载文件名为中文的，文件名需要经过url编码
            response.setHeader("Content-Disposition", "attachment;filename=" + name/*URLEncoder.encode(report.getName(), "UTF-8")*/);
            byte[] outputByte = new byte[1024];
            int readTmp = 0;
            while ((readTmp = fileIn.read(outputByte)) != -1) {
                out.write(outputByte, 0, readTmp); //并不是每次都能读到1024个字节，所有用readTmp作为每次读取数据的长度，否则会出现文件损坏的错误
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @PostMapping("/cycleReport/import")
    @ApiOperation("导入基线配置")
    public Result importConfigs(@RequestParam("file") MultipartFile file){
        try{
            if(file == null){
                return VoBuilder.result(RetMsgEnum.EMPTY_PARAM);
            }
            if(!file.getOriginalFilename().endsWith(".json")){
                return new Result("50015","文件格式错误");
            }
            File temfile = File.createTempFile("report-",".json");
            file.transferTo(temfile);
            String config = FileUtils.readFileToString(temfile, Charset.forName("UTF-8"));
            LineExportModel modle = JSONObject.parseObject(config,LineExportModel.class);
            this.baseLineService.importConfigs(modle);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new Result("50015",e.getMessage());
        }
        return VoBuilder.result(RetMsgEnum.SUCCESS);
    }

    @ResponseBody
    @GetMapping("/findAllEnable")
    public List<BaseLine> findAllEnable(){
        return this.baseLineService.findAllEnable();
    }

    @ResponseBody
    @PostMapping("/result")
    @ApiOperation("分页查询基线结果")
    public VList<BaseLineResult> findResult(@RequestBody BaseResultQuery query) {
        return baseLineService.findResult(query);
    }


    @ResponseBody
    @GetMapping("/special")
    @ApiOperation("获取所有特殊模型")
    public VData<List<BaseLineSpecial>> findAllSpecial() {
        return VoBuilder.vd(this.baseLineSpecialService.findAll());
    }


    @GetMapping("/dolineAll")
    public Result dolineAll(){
        /*String printAuditSummaryPrefix = "print-audit-db";
        commonService.create365Alias(printAuditSummaryPrefix+"-2022", printAuditSummaryPrefix + "-", "event_time", "yyyy-MM-dd", "2022", true);
        */

    //        String indexs = "print-audit,netflow-http,netflow-app-file,netflow-db,netflow-dns,netflow-email,netflow-file,netflow-tcp,netflow-udp";
        /*String indexs = "print-audit";
        String[] ins = indexs.split(",");
        for(String ind : ins){
            commonService.create365Alias(ind+"-2022", ind+"-", "event_time", TimeTools.TIME_FMT_1, "2022", true);
        }*/
    //ExecutorService exec = Executors.newFixedThreadPool(20);
        List<BaseLine> all = this.baseLineService.findAll();
            for(BaseLine ln : all){
            Map m = new HashMap<>();
            m.put("id",ln.getId());
            //TestLine line = new TestLine();
            //line.run("cs",m);
            //line.run("测试",m);
            //exec.execute(line);
        }
        return VoBuilder.success();
    }


    @GetMapping("/doline")
    public Result run(Integer id){
            /*String printAuditSummaryPrefix = "print-audit-db";
            commonService.create365Alias(printAuditSummaryPrefix+"-2022", printAuditSummaryPrefix + "-", "event_time", "yyyy-MM-dd", "2022", true);
            */

    //        String indexs = "print-audit,netflow-http,netflow-app-file,netflow-db,netflow-dns,netflow-email,netflow-file,netflow-tcp,netflow-udp";
            /*String indexs = "print-audit";
            String[] ins = indexs.split(",");
            for(String ind : ins){
                commonService.create365Alias(ind+"-2022", ind+"-", "event_time", TimeTools.TIME_FMT_1, "2022", true);
            }*//*
            ExecutorService exec = Executors.newFixedThreadPool(20);
            List<BaseLine> all = this.baseLineService.findAll();
            for(BaseLine ln : all){
                Map m = new HashMap<>();
                m.put("id",ln.getId());
                TestLine  line = new TestLine(m);
                //line.run("测试",m);
                exec.execute(line);
            }*/

            Map m = new HashMap<>();
            m.put("id",id);
            //LineTaskRun line = new LineTaskRun();
            //line.run("cs",m);
            return VoBuilder.success();
            }

    @GetMapping("/runData")
    public Result runData(Integer id){
            //LineTaskRun line = new LineTaskRun();
            //line.rundata();
            return VoBuilder.success();
            }


    @GetMapping("/createAlias")
    public Result createAlias(String index){
            commonService.create365Alias(index+"-2022", index+"-", "event_time", TimeTools.TIME_FMT_1, "2022", true);
            return VoBuilder.success();
            }



    /*
    @GetMapping("/updateLine")
    public Result updateLine(){
        LineUtil l = new LineUtil();
        l.updateLine();
        return VoBuilder.success();
    }*/

}
