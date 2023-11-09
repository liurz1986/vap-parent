package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.DiscoverIndex;
import com.vrv.vap.admin.model.DiscoverIndexField;
import com.vrv.vap.base.BaseService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 索引接口类
 * 
 * @author lilang
 * @date 2018年1月31日
 */
public interface IndexService extends BaseService<DiscoverIndex> {

    /**
     * 根据索引查询索引字段
     * @return
     */
    List<DiscoverIndexField> queryFieldByIndexId(String title);

    /**
     * 离线导入索引
     * @return
     */
    Integer importIndex(MultipartFile file);

}
