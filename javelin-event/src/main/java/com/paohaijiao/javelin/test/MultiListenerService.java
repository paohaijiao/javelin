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
