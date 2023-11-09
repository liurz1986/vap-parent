package com.vrv.vap.xc.service.portrait;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.xc.model.AssetTypeModel;
import com.vrv.vap.xc.vo.AssetVO;

public interface EventService {
    /**
     * 对象画像列表
     *
     * @param model 请求参数
     * @return
     */
    Page<AssetVO> portraitList(AssetTypeModel model);
}
