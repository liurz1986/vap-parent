package com.vrv.vap.alarmdeal.business.asset.util;

import com.vrv.vap.jpa.common.SessionUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AssetDomainCodeUtil {


	/**
	 * 查询与用户权限相关的机构code集合
	 *
	 * @return null表示无权限控制 其他情况表示有权限控制
	 */
	public static List<String> getUserAuthorityDomainCodes() {
		List<String> domainCodes = new ArrayList<>();
		if (SessionUtil.getCurrentUser() != null && SessionUtil.getauthorityType()) {
			List<String> userDomainCodes = SessionUtil.getUserDomainCodes();
			if (userDomainCodes == null || userDomainCodes.isEmpty()) {
				domainCodes.add("@#￥@Q#￥￥ZDAS！@#￥@#￥");// 使无法查询得到数据
				return domainCodes;
			} else {
				for (String key : userDomainCodes) {
					domainCodes.add(key);
				}
			}
		} else if (SessionUtil.getCurrentUser() != null && !SessionUtil.getauthorityType()) {
			domainCodes = null;
		} else {// 未登陆用户
			domainCodes.add("@#￥@Q#￥￥ZDAS！@#￥@#￥");// 使无法查询得到数据
		}
		return domainCodes;
	}
}
