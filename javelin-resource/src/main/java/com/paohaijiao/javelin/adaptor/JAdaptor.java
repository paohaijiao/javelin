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
package com.paohaijiao.javelin.adaptor;

import com.paohaijiao.javelin.resouce.JReader;

public class JAdaptor {
    private JReader dataReader;

    public JAdaptor(JReader dataReader) {
        this.dataReader = dataReader;
    }

    public void setDataReader(JReader dataReader) {
        this.dataReader = dataReader;
    }

    public String getRuleContent() {
        return dataReader.getRule();
    }
}
