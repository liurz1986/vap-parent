package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.analysis.model.ObjectResource;
import com.vrv.vap.alarmdeal.business.analysis.vo.ObjectResourceVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

public interface ObjectResourceService extends BaseService<ObjectResource,String> {

    /**
     * 新增资源对象
     */
    Result<ObjectResource> addResource(ObjectResourceVO objectResourceVO);

    /**
     * 编辑资源对象
     */
    Result<ObjectResource> editResource(ObjectResourceVO objectResourceVO);

    /**
     * 删除资源对象
     */
    Result<Boolean> deleteResource(String guid);

    /**
     * 资源对象分页查询
     */
    PageRes<ObjectResource> getObjectResourcePager(ObjectResourceVO objectResourceVO, Pageable pageable);
}
