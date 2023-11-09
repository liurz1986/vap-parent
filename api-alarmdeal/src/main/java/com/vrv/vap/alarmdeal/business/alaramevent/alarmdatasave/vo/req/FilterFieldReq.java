package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req;

import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/11/28 15:43
 * @description:
 */
@Data
public class FilterFieldReq {
    // 规则ID
    private List<String> filterIds;
}
