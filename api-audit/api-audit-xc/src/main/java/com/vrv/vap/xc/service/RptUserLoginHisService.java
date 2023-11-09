package com.vrv.vap.xc.service;

import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.pojo.RptUserLoginHis;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
public interface RptUserLoginHisService extends IService<RptUserLoginHis> {
    void saveBatch4List(List<Map<String, Object>> list);

    List<RptUserLogin> countAll();
}
