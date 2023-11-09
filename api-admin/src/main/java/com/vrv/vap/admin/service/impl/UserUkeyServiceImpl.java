package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.Base64Utils;
import com.vrv.vap.admin.common.util.BcEccUtils;
import com.vrv.vap.admin.common.util.HexUtils;
import com.vrv.vap.admin.common.util.Sm2Utils;
import com.vrv.vap.admin.mapper.UserUkeyMapper;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.model.UserUkey;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.service.UserUkeyService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author lilang
 * @date 2020/11/12
 * @description
 */
@Service
@Transactional
public class UserUkeyServiceImpl extends BaseServiceImpl<UserUkey> implements UserUkeyService {

    private static final Logger log = LoggerFactory.getLogger(UserUkeyServiceImpl.class);

    @Resource
    UserUkeyMapper userUkeyMapper;

    @Autowired
    UserService userService;

    static Charset charset = Charset.forName("UTF-8");

    private static final byte[] SM2_SIGN_ID = "1234567812345678".getBytes(charset);

    @Override
    public User checkUkeyAvailable(String ukeySerial) {
        Example example = new Example(UserUkey.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("serial",ukeySerial);
        List<UserUkey> ukeyList = userUkeyMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(ukeyList)) {
            UserUkey userUkey = ukeyList.get(0);
            Integer userId = userUkey.getUserId();
            User user = userService.findById(userId);
            return user;
        }
        return null;
    }

    @Override
    public Boolean verifyUkeySign(String account, String sign) {
        List<User> userList = userService.findByProperty(User.class, "account", account);
        if (CollectionUtils.isEmpty(userList)) {
            log.info("未找到账号" + LogForgingUtil.validLog(account) + "对应的用户！");
            return false;
        }
        User user = userList.get(0);
        Example example = new Example(UserUkey.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", user.getId());
        List<UserUkey> ukeyList = userUkeyMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(ukeyList)) {
            log.info("未找到" + user.getId() + "对应的绑定关系");
            return false;
        }
        UserUkey userUkey = ukeyList.get(0);
        String ukeyPublicKey = userUkey.getPublicKey();
        if (StringUtils.isEmpty(ukeyPublicKey)) {
            log.info("公钥信息为空！");
            return false;
        }
        try {
            byte[] bytes = Base64Utils.decode(ukeyPublicKey);
            byte[] x = Arrays.copyOfRange(bytes, 1, 33);
            byte[] y = Arrays.copyOfRange(bytes, 33, 65);
            ECPublicKeyParameters pubKey = BcEccUtils.createECPublicKeyParameters(HexUtils.toHexString(x),
                    HexUtils.toHexString(y), Sm2Utils.CURVE, Sm2Utils.DOMAIN_PARAMS);
            byte[] source = account.getBytes(charset);
            byte[] signedData = Base64Utils.decode(sign);
            return Sm2Utils.verify(pubKey, SM2_SIGN_ID, source, signedData);
        } catch (Exception e) {
            log.info("verify ukey sign exception, account={}, sign={}", account, sign, e);
        }
        return false;
    }

    @Override
    public Boolean unbindUkey(Integer userId) {
        Example example = new Example(UserUkey.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        int result = userUkeyMapper.deleteByExample(example);
        return result > 0;
    }

    @Override
    public Boolean bindUkey(Integer userId, String ukeySerial, String ukeyPublicKey, String ukeyCertificate) {
        UserUkey userUkey = new UserUkey();
        userUkey.setUserId(userId);
        userUkey.setSerial(ukeySerial);
        userUkey.setPublicKey(ukeyPublicKey);
        userUkey.setUkeyCertificate(ukeyCertificate);
        int result = userUkeyMapper.insert(userUkey);
        return result > 0;
    }

    @Override
    public String queryUkeySerial(String account) {
        List<User> userList = userService.findByProperty(User.class, "account", account);
        if (CollectionUtils.isEmpty(userList)) {
            return "";
        }
        User user = userList.get(0);
        Example example = new Example(UserUkey.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", user.getId());
        List<UserUkey> ukeyList = userUkeyMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(ukeyList)) {
            UserUkey userUkey = ukeyList.get(0);
            return userUkey.getSerial();
        }
        return "";
    }
}
