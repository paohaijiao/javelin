package com.paohaijiao.javelin.test;

import com.paohaijiao.javelin.context.JEventSupportedApplicationContext;
import com.paohaijiao.javelin.event.JApplicationEvent;
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


    // Additional test classes
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
