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
package com.github.paohaijiao.test;

import com.github.paohaijiao.context.JEventSupportedApplicationContext;
import com.github.paohaijiao.event.JApplicationEvent;
import org.junit.Test;

public class JunitTest {

    @Test
    public void testInheritedEventTypes() {
        JEventSupportedApplicationContext context = new JEventSupportedApplicationContext(
                "com.paohaijiao.javelin.test"
        );
        System.out.println("Registered beans: " );
        ParentEventService service = context.getBean("parentEventService", ParentEventService.class);
        context.publishEvent(new AnotherTestEvent(context, "Child Message"));
    }

    public static class AnotherTestEvent extends JApplicationEvent {
        public AnotherTestEvent(Object source, String message) {
            super(source);
        }
    }


    public static class ParentTestEvent extends JApplicationEvent {
        private final String message;

        public ParentTestEvent(Object source, String message) {
            super(source);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ChildTestEvent extends ParentTestEvent {
        public ChildTestEvent(Object source, String message) {
            super(source, message);
        }
    }
}
