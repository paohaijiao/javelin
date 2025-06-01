package org.paohaijiao.jstark.test;

import org.junit.Test;
import org.paohaijiao.jstark.anno.JComponent;
import org.paohaijiao.jstark.anno.JEventListener;
import org.paohaijiao.jstark.context.JEventSupportedApplicationContext;
import org.paohaijiao.jstark.event.JApplicationEvent;

public class JunitTest {

    @Test
    public void testInheritedEventTypes() {

        JEventSupportedApplicationContext context = new JEventSupportedApplicationContext(
                "org.paohaijiao.jstark.test"
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
