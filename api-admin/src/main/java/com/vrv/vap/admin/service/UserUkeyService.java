package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.model.UserUkey;
import com.vrv.vap.base.BaseService;

/**
 * @author lilang
 * @date 2020/11/12
 * @description
 */
public interface UserUkeyService extends BaseService<UserUkey> {

    /**
     * 检测ukey是否已绑定
     * @param ukeySerial ukey序列号
     * @return
     */
    User checkUkeyAvailable(String ukeySerial);

    /**
     * 验签ukey签名
     * @param account
     * @param sign
     * @return
     */
    Boolean verifyUkeySign(String account,String sign);

    /**
     * 解绑ukey
     * @param userId
     * @return
     */
    Boolean unbindUkey(Integer userId);

    /**
     * 绑定ukey
     * @param userId
     * @param ukeySerial
     * @param ukeyPublicKey
     * @return
     */
    Boolean bindUkey(Integer userId, String ukeySerial,String ukeyPublicKey,String ukeyCertificate);

    /**
     *  查找用户绑定ukey的序列号
     * @param account
     * @return
     */
    String queryUkeySerial(String account);
}
