/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.antlr;

import com.github.paohaijiao.exception.JAntlrExecutionException;

public interface JAntlrExecutor<I, O>{
    /**
     * 执行语法分析
     * @param input 输入内容
     * @return 分析结果
     * @throws JAntlrExecutionException 执行异常
     */
    O execute(I input) throws JAntlrExecutionException;

    /**
     * 添加错误监听器
     * @param listener 错误监听器
     */
    void addErrorListener(JAntlrErrorListener listener);

    /**
     * 移除错误监听器
     * @param listener 错误监听器
     */
    void removeErrorListener(JAntlrErrorListener listener);
}
