package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.CmdUtil;
import com.vrv.vap.admin.mapper.CascadePlatformMapper;
import com.vrv.vap.admin.model.CascadePlatform;
import com.vrv.vap.admin.service.CascadePlatformService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/03/26.
 */
@Service
@Transactional
public class CascadePlatformServiceImpl extends BaseServiceImpl<CascadePlatform> implements CascadePlatformService {
    @Resource
    private CascadePlatformMapper cascadePlatformMapper;

    private static final Logger logger = LoggerFactory.getLogger(CascadePlatformServiceImpl.class);

    @Override
    public String getLocalHost() {
        String ip = CmdUtil.runShellCmd("ifconfig bm_net|grep 'inet '|awk '{print $2}'");
        if (StringUtils.isEmpty(ip)) {
            ip = CmdUtil.runShellCmd("ifconfig bm_net|grep inet|awk '{print $2}'|awk -F : '{ print $2 }'");
        }
        logger.info("获取到的ip为：" + ip);
        return ip;
    }
}
