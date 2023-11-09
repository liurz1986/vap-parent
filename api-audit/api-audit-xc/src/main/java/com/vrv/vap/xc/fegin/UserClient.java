package com.vrv.vap.xc.fegin;

import com.vrv.vap.xc.config.FeignFlumeConfig;
import com.vrv.vap.xc.model.BaseSecurityDomain;
import com.vrv.vap.xc.model.UserModel;
import com.vrv.vap.xc.model.UserQuery;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import java.util.List;
import java.util.Map;

/**
 * 获取当前登录用户信息
 *
 * @author xw
 * @date 2018年5月3日
 */
@FeignClient(value = "api-admin", configuration = FeignFlumeConfig.class)
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/user/info")
    public String getUserInfo(@RequestHeader("Cookie") String sessionCookie);

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    VList<UserModel> getUser(@RequestBody UserQuery record);

    @RequestMapping(method = RequestMethod.GET, value = "/user")
    VData<List<UserModel>> getUser();

    @RequestMapping(method = RequestMethod.GET, value = "/secruity/domain/sub/{code}")
    VData<List<BaseSecurityDomain>> getSecruityDomainByCode(@PathVariable("code") String code);

    @RequestMapping(method = RequestMethod.POST, value = "/shortMessage/byPhoneAndContent")
    Result sendMsgbyPhoneAndContent(@RequestBody Map<String, Object> device);

    @RequestMapping(method = RequestMethod.GET, value = "/user/token/default")
    VData<String> getUserToken(@RequestBody Map<String, Object> param);

}
