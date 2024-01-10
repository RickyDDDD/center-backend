package com.ricky.personcenter.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableListener implements ReadListener<XingqiuTableUserInfo> {


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param context
     */
    @Override
    public void invoke(XingqiuTableUserInfo data, AnalysisContext context) {

    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context 上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

}