package com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: 梁国露
 * @since: 2022/12/15 11:00
 * @description:
 */
@Slf4j
public abstract class ReformAbsConsumer implements WorkHandler<ReformModel> {

    @Override
    public void onEvent(ReformModel messageModel) throws Exception {

    }
}
