package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.FileTransferService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 文件传输
 */
@RestController
@RequestMapping("/file/transfer")
public class FileTransferController {
    @Resource
    private FileTransferService fileTransferService;

    /**
     * 文件传输-文件传输关系图
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/diagram")
    @ApiOperation("文件传输-文件传输关系图")
    public VData<List<Map<String, Object>>> fileDiagram(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileDiagram(model);
    }

    /**
     * 文件传输-文件上传下载次数趋势
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/upDownTrend")
    @ApiOperation("文件传输-文件上传下载次数趋势")
    public VData<List<Map<String, Object>>> fileUpDownTrend(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileUpDownTrend(model);
    }

    /**
     * 文件传输-上传/下载文件密级分布
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/classification")
    @ApiOperation("文件传输-上传/下载文件密级分布")
    public VData<List<Map<String, Object>>> fileLevelBranch(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileInfo(model,"classification_level_code","fileLevel");
    }

    /**
     * 文件传输-上传/下载文件类型分布
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/type")
    @ApiOperation("文件传输-上传/下载文件类型分布")
    public VData<List<Map<String, Object>>> fileTypeBranch(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileInfo(model,"file_type","fileType");
    }

    /**
     * 文件传输-上传/下载文件大小分布
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/size")
    @ApiOperation("文件传输-上传/下载文件大小分布")
    public VData<List<Map<String, Object>>> fileSizeBranch(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileInfo(model,"file_size","fileSize");
    }

    /**
     * 文件传输-详情
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/detail")
    @ApiOperation("文件传输-详情")
    public VList<Map<String, String>> fileTransferDetail(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileTransferDetail(model);
    }

    /**
     * 文件传输-详情导出
     *
     * @param model 请求参数
     * @return 文件传输结果
     */
    @PostMapping("/export")
    @ApiOperation("文件传输-详情导出")
    public VData<Export.Progress> fileTransferDetailExport(@RequestBody ObjectPortraitModel model) {
        return fileTransferService.fileTransferDetailExport(model);
    }
}
