package com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common;

import com.lmax.disruptor.EventFactory;

/**
 * @author: 梁国露
 * @since: 2022/12/15 10:59
 * @description:
 */
public class ReformAbsFactory implements EventFactory<ReformModel> {

    @Override
    public ReformModel newInstance() {
        return new ReformModel();
    }
}
