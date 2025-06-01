package com.paohaijiao.javelin.antlr;

import com.paohaijiao.javelin.exception.AntlrExecutionException;

public interface AntlrExecutor <I, O>{
    /**
     * 执行语法分析
     * @param input 输入内容
     * @return 分析结果
     * @throws AntlrExecutionException 执行异常
     */
    O execute(I input) throws AntlrExecutionException;

    /**
     * 添加错误监听器
     * @param listener 错误监听器
     */
    void addErrorListener(AntlrErrorListener listener);

    /**
     * 移除错误监听器
     * @param listener 错误监听器
     */
    void removeErrorListener(AntlrErrorListener listener);
}
