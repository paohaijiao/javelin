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
package com.paohaijiao.javelin.test;
import com.paohaijiao.javelin.anno.JComponent;
import com.paohaijiao.javelin.anno.JEventListener;
@JComponent
public class MultiListenerService {
    private int testEventCount = 0;
    private int anotherTestEventCount = 0;



    @JEventListener
    public void handleAnotherTestEvent(JunitTest.AnotherTestEvent event) {
        System.out.println("AnotherTestEvent: " + event);
        anotherTestEventCount++;
    }

    public int getTestEventCount() {
        return testEventCount;
    }

    public int getAnotherTestEventCount() {
        return anotherTestEventCount;
    }
}
