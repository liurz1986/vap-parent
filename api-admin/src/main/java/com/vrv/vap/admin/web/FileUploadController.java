package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.FileTypeEnum;
import com.vrv.vap.admin.common.util.FastDFSUtils;
import com.vrv.vap.admin.common.util.Md5Util;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.FileMd5;
import com.vrv.vap.admin.model.FileUpLoadInfo;
import com.vrv.vap.admin.service.FileMd5Service;
import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.FileUploadInfoQuery;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/file")
public class FileUploadController extends ApiController {

    @Autowired
    private FileMd5Service fileMd5Service;

    @Autowired
    private FileUploadInfoService fileUploadInfoService;

    @Autowired
    private FastDFSUtils fastDFSUtils;

    @Value("${fileupload.type}")
    private String fileuploadType;

    private static  final  String FASTDFS = "fastdfs";

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("uploadType", "{\"0\":\"本地地址\",\"1\":\"Fastdfs地址\"}");
    }

    @ApiOperation("上传文件")
    @PostMapping(path = "/upload")
    @SysRequestLog(description = "上传文件", actionType = ActionType.UPLOAD)
    public Result uploadFile(@ApiParam(value = "上传的文件", required = true) @RequestParam MultipartFile file,
                             @ApiParam(value = "命名空间", required = false) @RequestParam("namespace") String namespace,@ApiParam(value = "上传文件信息", required = false) @RequestParam(value="msg",required = false)String msg) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return new Result("-1","文件类型错误，支持类型:"+StringUtils.join(FileFilterUtil.fileTypes.toArray(), ","));
        }
        /*if (!(fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".zip")
                || fileName.endsWith(".data") || fileName.endsWith(".vapbak") || fileName.endsWith(".xml")
                || fileName.endsWith(".rar") || fileName.endsWith(".txt"))) {
            return this.result(false);
        }*/

        if (StringUtils.isEmpty(namespace)) {
            logger.info("namespace: {}", LogForgingUtil.validLog(namespace));
            namespace = "default";
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);

        FileUpLoadInfo fileUpLoadInfo = FileUpLoadInfo.builder()
                .fileName(fileName)
                .fileType(fileType)
                .namespace(namespace)
                .msg(msg)
                .userId(user != null ? user.getId() : 0)
                .userName(user != null ? user.getName() : "")
                .createTime(new Date())
                .build();
        //进行文件MD5值的判断,若相同，不进行上传
        String md5 = Md5Util.getMD5(file);
        if (StringUtils.isNotEmpty(md5)) {
            List<FileMd5> list = fileMd5Service.findByProperty(FileMd5.class, "md5", md5);
            if (CollectionUtils.isNotEmpty(list)) {
                fillFileUpInfo(list, fileUpLoadInfo);
                int result = fileUploadInfoService.save(fileUpLoadInfo);
                if (result == 1) {
                    SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
                    syslogSender.sendSysLog(ActionType.UPLOAD, "上传文件:【文件名:" + fileName + "】", null, "1");
                }
                return this.vData(fileUpLoadInfo);
            }
        }
       fileUpLoadInfo.setFileId(Uuid.uuid());
       String filePath =  fileUploadInfoService.uploadFile(fileUpLoadInfo ,file);
        if (filePath == null){
            return new Result("-1","文件上传失败");
        }
        if(FASTDFS.equals(fileuploadType)){
            fileUpLoadInfo.setUploadType(1);
        }
        else {
            fileUpLoadInfo.setUploadType(0);
        }
        fileUpLoadInfo.setFilePath(filePath);
        int result = fileUploadInfoService.save(fileUpLoadInfo);
        if (result == 1) {
            SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
            syslogSender.sendSysLog(ActionType.UPLOAD, "上传文件:【文件名:" + fileName + "】", null, "1");
        }
        // 保存文件的MD5信息
        FileMd5  fileMd5 = new FileMd5();
        fileMd5.setFileId( fileUpLoadInfo.getFileId());
        fileMd5.setFileType(fileUpLoadInfo.getFileType());
        fileMd5.setMd5(md5);
        fileMd5Service.save(fileMd5);
        return this.vData(fileUpLoadInfo);
    }


    private FileUpLoadInfo fillFileUpInfo(List<FileMd5> list ,FileUpLoadInfo fileUpLoadInfo) {
       FileMd5 fileMd5 = list.get(0);
       List<FileUpLoadInfo>  result = fileUploadInfoService.findByProperty(FileUpLoadInfo.class,"fileId",fileMd5.getFileId());
        if (CollectionUtils.isNotEmpty(result)) {
            fileUpLoadInfo.setFileId(result.get(0).getFileId());
            fileUpLoadInfo.setFilePath(result.get(0).getFilePath());
            fileUpLoadInfo.setUploadType(result.get(0).getUploadType());
        }
        return fileUpLoadInfo;
    }

    @ApiOperation("展示文件")
    @GetMapping(path = "/show/{fileId}")
    public void fileShow(@PathVariable("fileId") String fileId, HttpServletResponse response){
        List<FileUpLoadInfo> fileUpLoadInfoList = fileUploadInfoService.findByProperty(FileUpLoadInfo.class,"fileId",fileId);
        String filePath = "" ;
        InputStream st = null;
        try {
            if (CollectionUtils.isNotEmpty(fileUpLoadInfoList)) {
                filePath = fileUpLoadInfoList.get(0).getFilePath();
                st = new FileInputStream(filePath);

            }
            String fileExtName = filePath.substring(filePath.lastIndexOf(".") + 1);
            if (fileExtName.equalsIgnoreCase(FileTypeEnum.MP4.getName())) {
                response.setContentType("video/mp4");
            } else if (fileExtName.equalsIgnoreCase(FileTypeEnum.OGG.getName())) {
                response.setContentType("audio/ogg");
            } else if (fileExtName.equalsIgnoreCase(FileTypeEnum.MP3.getName())) {
                response.setContentType("audio/mpeg");
            } else if (fileExtName.equalsIgnoreCase(FileTypeEnum.JPG.getName()) || fileExtName.equalsIgnoreCase(FileTypeEnum.PNG.getName()) || fileExtName.equalsIgnoreCase(FileTypeEnum.GIF.getName()) || fileExtName.equalsIgnoreCase(FileTypeEnum.JPEG.getName())) {
                response.setContentType("image/jpeg");
            } else {
                response.setContentType("text/html");
                response.getWriter().write("暂时不支持该格式的文件");
                return;
            }
            if (st != null) {
                StreamUtils.copy(st, response.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(st != null){
                    st.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @ApiOperation("上传文件的信息展示")
    @PostMapping(path = "/fileInfo")
    public VList<FileUpLoadInfo> queryFileUpInfo(@RequestBody FileUploadInfoQuery fileUploadInfoQuery){
        if(fileUploadInfoQuery.getOrder_() == null){
            fileUploadInfoQuery.setOrder_("createTime");
        }
        if(fileUploadInfoQuery.getBy_() == null){
            fileUploadInfoQuery.setBy_("desc");
        }
        boolean hasTimeFilter = false;
        Example.Criteria criteria = null;
        if(fileUploadInfoQuery.getMyEndTime()!= null && fileUploadInfoQuery.getMyEndTime() != null){
            Example example = new Example(FileUpLoadInfo.class);
            criteria = example.createCriteria().andBetween("createTime",fileUploadInfoQuery.getMyStartTime(),fileUploadInfoQuery.getMyEndTime());
            hasTimeFilter = true;
        }
        fileUploadInfoQuery.setMyStartTime(null);
        fileUploadInfoQuery.setMyEndTime(null);
        Example example1 =this.pageQuery(fileUploadInfoQuery,FileUpLoadInfo.class);
        if(hasTimeFilter){
            example1.and(criteria);
        }
        return  this.vList(fileUploadInfoService.findByExample(example1));
    }

    @ApiOperation("删除上传文件")
    @DeleteMapping(path = "/delete")
    @SysRequestLog(description = "删除上传文件", actionType = ActionType.DELETE)
    public Result fileUpInfoDelete(@RequestBody DeleteQuery deleteQuery){
        List<FileUpLoadInfo> fileUpLoadInfoList = fileUploadInfoService.findByids(deleteQuery.getIds());
        for(FileUpLoadInfo fileUpLoadInfo : fileUpLoadInfoList){
            String fileId = fileUpLoadInfo.getFileId();
            Integer id = fileUpLoadInfo.getId();
            String filePath = fileUpLoadInfo.getFilePath();
            Integer upLoadType = fileUpLoadInfo.getUploadType();
            List<FileUpLoadInfo> list = fileUploadInfoService.findByProperty(FileUpLoadInfo.class, "fileId", fileId);
            if(list.size() == 1){
                // 删除上传记录和文件
                int result = fileUploadInfoService.deleteById(id);
                if (result > 0) {
                    SyslogSenderUtils.sendDeleteSyslog(fileUpLoadInfo,"删除上传文件");
                }
                if(upLoadType==0){
                    File file = new File(filePath);
                    file.delete();
                }
               if(upLoadType==1){
                    fastDFSUtils.deleteFile(filePath);
               }
                fileMd5Service.deleteByFileId(fileId);
            }
            else{
                // 删除该条上传记录
                int result = fileUploadInfoService.deleteById(id);
                if (result > 0) {
                    SyslogSenderUtils.sendDeleteSyslog(fileUpLoadInfo,"删除上传文件");
                }
            }
        }
      return this.result(true);
    }


    @ApiOperation("下载文件")
    @GetMapping(path = "/download/{fileId}")
    @SysRequestLog(description = "下载文件", actionType = ActionType.DOWNLOAD)
    public Result fileDownload(@PathVariable("fileId") @ApiParam(value = "文件ID") String fileId, HttpServletResponse response){
        SyslogSenderUtils.sendDownLosdSyslog();
        List<FileUpLoadInfo> fileUpLoadInfoList = fileUploadInfoService.findByProperty(FileUpLoadInfo.class,"fileId",fileId);
        String filePath ;
        String fileName = "";
        InputStream st = null;
        try {
            if (CollectionUtils.isNotEmpty(fileUpLoadInfoList)) {
                FileUpLoadInfo fileUpLoadInfo = fileUpLoadInfoList.get(0);
                fileName =fileUpLoadInfo.getFileName();
                if (fileUpLoadInfo.getUploadType() == 1) {
                    filePath = fileUpLoadInfo.getFilePath();
                    st = fastDFSUtils.downloadSteamFileByPath(filePath);
                }
                if (fileUpLoadInfo.getUploadType() == 0) {
                    filePath = fileUpLoadInfo.getFilePath();
                    st = new FileInputStream(filePath);
                }
            }
            if(st == null){
                return new Result("-1","下载文件流缺失");
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8") );
            IOUtils.copy(st, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(st != null){
                    st.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
