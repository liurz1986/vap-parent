package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.RptUserLogin;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
public interface RptUserLoginService extends IService<RptUserLogin> {

    void replaceInto(List<RptUserLogin> list);
}
