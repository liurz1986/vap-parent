package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req;

import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/12/1 10:23
 * @description:
 */
@Data
public class ChangeRiskReq {

    private List<String> oneList;

    private List<String> twoList;
}
