package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.model.UserToken;
import com.vrv.vap.admin.service.UserTokenService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lilang
 * @date 2021/6/21
 * @description
 */
@Service
@Transactional
public class UserTokenServiceImpl extends BaseServiceImpl<UserToken> implements UserTokenService {

}
