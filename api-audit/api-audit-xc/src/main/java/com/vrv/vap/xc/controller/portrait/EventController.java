package com.vrv.vap.xc.controller.portrait;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.AssetTypeModel;
import com.vrv.vap.xc.service.portrait.EventService;
import com.vrv.vap.xc.vo.AssetVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/portrait")
public class EventController {
    @Resource
    private EventService eventService;

    /**
     * 画像列表接口
     *
     * @param model 请求参数
     * @return 画像列表
     */
    @PostMapping("/list")
    @ApiOperation("画像列表接口")
    public VList<AssetVO> portraitList(@RequestBody AssetTypeModel model) {
        Page<AssetVO> assetStealLeakValue = eventService.portraitList(model);
        return VoBuilder.vl(assetStealLeakValue);
    }
}
