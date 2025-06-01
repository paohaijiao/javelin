package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.anno.JComponent;
import org.paohaijiao.jstark.anno.JEventListener;
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
